/*
 * Copyright (C) 2004-2020 L2jEnergy Server
 * 
 * This file is part of L2jEnergy Server.
 * 
 * L2jEnergy Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2jEnergy Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ItemsAutoDestroy;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

/**
 * This class manage all items on ground.
 * @author Enforcer
 */
public final class ItemsOnGroundManager implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemsOnGroundManager.class);
	
	private final List<L2ItemInstance> _items = new CopyOnWriteArrayList<>();
	
	protected ItemsOnGroundManager()
	{
		if (GeneralConfig.SAVE_DROPPED_ITEM_INTERVAL > 0)
		{
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this, GeneralConfig.SAVE_DROPPED_ITEM_INTERVAL, GeneralConfig.SAVE_DROPPED_ITEM_INTERVAL);
		}
		load();
	}
	
	private void load()
	{
		// If SaveDroppedItem is false, may want to delete all items previously stored to avoid add old items on reactivate
		if (!GeneralConfig.SAVE_DROPPED_ITEM && GeneralConfig.CLEAR_DROPPED_ITEM_TABLE)
		{
			emptyTable();
		}
		
		if (!GeneralConfig.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		// if DestroyPlayerDroppedItem was previously false, items currently protected will be added to ItemsAutoDestroy
		if (GeneralConfig.DESTROY_DROPPED_PLAYER_ITEM)
		{
			String str = null;
			if (!GeneralConfig.DESTROY_EQUIPABLE_PLAYER_ITEM)
			{
				// Recycle misc. items only
				str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1 AND equipable = 0";
			}
			else if (GeneralConfig.DESTROY_EQUIPABLE_PLAYER_ITEM)
			{
				// Recycle all items including equip-able
				str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1";
			}
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(str))
			{
				ps.setLong(1, System.currentTimeMillis());
				ps.execute();
			}
			catch (Exception e)
			{
				LOG.error("{}: Error while updating table ItemsOnGround!", getClass().getSimpleName(), e);
			}
		}
		
		// Add items to world
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable FROM itemsonground"))
		{
			int count = 0;
			try (ResultSet rs = ps.executeQuery())
			{
				L2ItemInstance item;
				while (rs.next())
				{
					item = new L2ItemInstance(rs.getInt(1), rs.getInt(2));
					L2World.getInstance().storeObject(item);
					// this check and..
					if (item.isStackable() && (rs.getInt(3) > 1))
					{
						item.setCount(rs.getInt(3));
					}
					// this, are really necessary?
					if (rs.getInt(4) > 0)
					{
						item.setEnchantLevel(rs.getInt(4));
					}
					item.setXYZ(rs.getInt(5), rs.getInt(6), rs.getInt(7));
					item.setWorldRegion(L2World.getInstance().getRegion(item.getLocation()));
					item.getWorldRegion().addVisibleObject(item);
					final long dropTime = rs.getLong(8);
					item.setDropTime(dropTime);
					item.setProtected(dropTime == -1);
					item.setIsVisible(true);
					L2World.getInstance().addVisibleObject(item, item.getWorldRegion());
					_items.add(item);
					count++;
					// add to ItemsAutoDestroy only items not protected
					if (!GeneralConfig.LIST_PROTECTED_ITEMS.contains(item.getId()))
					{
						if (dropTime > -1)
						{
							if (((GeneralConfig.AUTODESTROY_ITEM_AFTER > 0) && !item.getItem().hasExImmediateEffect()) || ((GeneralConfig.HERB_AUTO_DESTROY_TIME > 0) && item.getItem().hasExImmediateEffect()))
							{
								ItemsAutoDestroy.getInstance().addItem(item);
							}
						}
					}
				}
			}
			LOG.info("{}: Loaded {} items.", getClass().getSimpleName(), count);
		}
		catch (Exception e)
		{
			LOG.error("{}: Error while loading ItemsOnGround!", getClass().getSimpleName(), e);
		}
		
		if (GeneralConfig.EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD)
		{
			emptyTable();
		}
	}
	
	public void save(L2ItemInstance item)
	{
		if (!GeneralConfig.SAVE_DROPPED_ITEM)
		{
			return;
		}
		_items.add(item);
	}
	
	public void removeObject(L2ItemInstance item)
	{
		if (GeneralConfig.SAVE_DROPPED_ITEM)
		{
			_items.remove(item);
		}
	}
	
	public void saveInDb()
	{
		run();
	}
	
	public void cleanUp()
	{
		_items.clear();
	}
	
	public void emptyTable()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement s = con.createStatement())
		{
			s.executeUpdate("DELETE FROM itemsonground");
		}
		catch (Exception e1)
		{
			LOG.error("{}: Error while cleaning table ItemsOnGround!", getClass().getSimpleName(), e1);
		}
	}
	
	@Override
	public synchronized void run()
	{
		if (!GeneralConfig.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		emptyTable();
		
		if (_items.isEmpty())
		{
			return;
		}
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO itemsonground(object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable) VALUES(?,?,?,?,?,?,?,?,?)"))
		{
			for (L2ItemInstance item : _items)
			{
				if (item == null)
				{
					continue;
				}
				
				if (CursedWeaponsManager.getInstance().isCursed(item.getId()))
				{
					continue; // Cursed Items not saved to ground, prevent double save
				}
				
				try
				{
					ps.setInt(1, item.getObjectId());
					ps.setInt(2, item.getId());
					ps.setLong(3, item.getCount());
					ps.setInt(4, item.getEnchantLevel());
					ps.setInt(5, item.getX());
					ps.setInt(6, item.getY());
					ps.setInt(7, item.getZ());
					ps.setLong(8, (item.isProtected() ? -1 : item.getDropTime())); // item is protected or AutoDestroyed
					ps.setLong(9, (item.isEquipable() ? 1 : 0)); // set equip-able
					ps.execute();
					ps.clearParameters();
				}
				catch (Exception e)
				{
					LOG.error("{}: Error while inserting into table ItemsOnGround!", getClass().getSimpleName(), e);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("{}: SQL error while storing items on ground!", getClass().getSimpleName(), e);
		}
	}
	
	/**
	 * Gets the single instance of {@code ItemsOnGroundManager}.
	 * @return single instance of {@code ItemsOnGroundManager}
	 */
	public static final ItemsOnGroundManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsOnGroundManager INSTANCE = new ItemsOnGroundManager();
	}
}
