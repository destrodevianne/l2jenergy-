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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.PremiumItemDAO;
import com.l2jserver.gameserver.model.L2PremiumItem;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExNotifyPremiumItem;

/**
 * Premium Item DAO MySQL implementation.
 * @author Zoey76, update code Мо3олЬ
 */
public class PremiumItemDAOMySQLImpl implements PremiumItemDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(PremiumItemDAOMySQLImpl.class);
	
	private static final String GET_PREMIUM_ITEMS = "SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?";
	private static final String DELETE_PREMIUM_ITEMS = "DELETE FROM character_premium_items WHERE charId=? AND itemNum=? ";
	private static final String UPDATE_PREMIUM_ITEMS = "UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=? ";
	private static final String ADD_PREMIUM_ITEMS = "INSERT INTO character_premium_items (`charId`, `itemNum`, `itemId`, `itemCount`, `itemSender`) VALUES (?, ?, ?, ?, ?)";
	
	@Override
	public void load(L2PcInstance player)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(GET_PREMIUM_ITEMS))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					int itemNum = rset.getInt("itemNum");
					int itemId = rset.getInt("itemId");
					long itemCount = rset.getLong("itemCount");
					String itemSender = rset.getString("itemSender");
					player.getPremiumItemList().put(itemNum, new L2PremiumItem(itemId, itemCount, itemSender));
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not restore premium items: {}", e);
		}
	}
	
	@Override
	public void update(L2PcInstance player, int itemNum, long newcount)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_PREMIUM_ITEMS))
		{
			ps.setLong(1, newcount);
			ps.setInt(2, player.getObjectId());
			ps.setInt(3, itemNum);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.error("Could not update premium items: {}", e);
		}
	}
	
	@Override
	public void delete(L2PcInstance player, int itemNum)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_PREMIUM_ITEMS))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, itemNum);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.error("Could not delete premium item: {}" + e);
		}
	}
	
	@Override
	public void add(L2PcInstance player, int itemId, long itemCount, String itemSender)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(ADD_PREMIUM_ITEMS))
		{
			int itemNum;
			for (itemNum = 1; itemNum <= player.getPremiumItemList().size(); ++itemNum)
			{
				if (!player.getPremiumItemList().containsKey(itemNum))
				{
					break;
				}
			}
			
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, itemNum);
			statement.setInt(3, itemId);
			statement.setLong(4, itemCount);
			statement.setString(5, itemSender);
			statement.execute();
			
			player.getPremiumItemList().put(itemNum, new L2PremiumItem(itemId, itemCount, itemSender));
			player.sendPacket(ExNotifyPremiumItem.STATIC_PACKET);
		}
		catch (Exception e)
		{
			LOG.error("Could not add premium item: {}", e);
		}
	}
}
