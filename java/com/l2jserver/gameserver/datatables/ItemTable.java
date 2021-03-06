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
package com.l2jserver.gameserver.datatables;

import static com.l2jserver.gameserver.model.itemcontainer.Inventory.ADENA_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemHPBonusData;
import com.l2jserver.gameserver.engines.DocumentEngine;
import com.l2jserver.gameserver.enums.ItemLocation;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.instance.L2EventMonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.item.OnItemCreate;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.util.GMAudit;
import com.l2jserver.gameserver.util.LoggingUtils;

/**
 * This class serves as a container for all item templates in the game.
 */
public class ItemTable
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemTable.class);
	private static final Logger LOG_ITEMS = LoggerFactory.getLogger("item");
	
	public static final Map<String, Integer> SLOTS = new HashMap<>();
	
	private L2Item[] _allTemplates;
	private final Map<Integer, L2EtcItem> _etcItems = new HashMap<>();
	private final Map<Integer, L2Armor> _armors = new HashMap<>();
	private final Map<Integer, L2Weapon> _weapons = new HashMap<>();
	
	static
	{
		SLOTS.put("shirt", L2Item.SLOT_UNDERWEAR);
		SLOTS.put("lbracelet", L2Item.SLOT_L_BRACELET);
		SLOTS.put("rbracelet", L2Item.SLOT_R_BRACELET);
		SLOTS.put("talisman", L2Item.SLOT_DECO);
		SLOTS.put("chest", L2Item.SLOT_CHEST);
		SLOTS.put("fullarmor", L2Item.SLOT_FULL_ARMOR);
		SLOTS.put("head", L2Item.SLOT_HEAD);
		SLOTS.put("hair", L2Item.SLOT_HAIR);
		SLOTS.put("hairall", L2Item.SLOT_HAIRALL);
		SLOTS.put("underwear", L2Item.SLOT_UNDERWEAR);
		SLOTS.put("back", L2Item.SLOT_BACK);
		SLOTS.put("neck", L2Item.SLOT_NECK);
		SLOTS.put("legs", L2Item.SLOT_LEGS);
		SLOTS.put("feet", L2Item.SLOT_FEET);
		SLOTS.put("gloves", L2Item.SLOT_GLOVES);
		SLOTS.put("chest,legs", L2Item.SLOT_CHEST | L2Item.SLOT_LEGS);
		SLOTS.put("belt", L2Item.SLOT_BELT);
		SLOTS.put("rhand", L2Item.SLOT_R_HAND);
		SLOTS.put("lhand", L2Item.SLOT_L_HAND);
		SLOTS.put("lrhand", L2Item.SLOT_LR_HAND);
		SLOTS.put("rear;lear", L2Item.SLOT_R_EAR | L2Item.SLOT_L_EAR);
		SLOTS.put("rfinger;lfinger", L2Item.SLOT_R_FINGER | L2Item.SLOT_L_FINGER);
		SLOTS.put("wolf", L2Item.SLOT_WOLF);
		SLOTS.put("greatwolf", L2Item.SLOT_GREATWOLF);
		SLOTS.put("hatchling", L2Item.SLOT_HATCHLING);
		SLOTS.put("strider", L2Item.SLOT_STRIDER);
		SLOTS.put("babypet", L2Item.SLOT_BABYPET);
		SLOTS.put("none", L2Item.SLOT_NONE);
		
		// retail compatibility
		SLOTS.put("onepiece", L2Item.SLOT_FULL_ARMOR);
		SLOTS.put("hair2", L2Item.SLOT_HAIR2);
		SLOTS.put("dhair", L2Item.SLOT_HAIRALL);
		SLOTS.put("alldress", L2Item.SLOT_ALLDRESS);
		SLOTS.put("deco1", L2Item.SLOT_DECO);
		SLOTS.put("waist", L2Item.SLOT_BELT);
	}
	
	protected ItemTable()
	{
		load();
	}
	
	private void load()
	{
		int highest = 0;
		_armors.clear();
		_etcItems.clear();
		_weapons.clear();
		for (L2Item item : DocumentEngine.getInstance().loadItems())
		{
			if (highest < item.getId())
			{
				highest = item.getId();
			}
			if (item instanceof L2EtcItem)
			{
				_etcItems.put(item.getId(), (L2EtcItem) item);
			}
			else if (item instanceof L2Armor)
			{
				_armors.put(item.getId(), (L2Armor) item);
			}
			else
			{
				_weapons.put(item.getId(), (L2Weapon) item);
			}
		}
		buildFastLookupTable(highest);
		LOG.info("{}: Loaded: {} Etc Items", getClass().getSimpleName(), _etcItems.size());
		LOG.info("{}: Loaded: {} Armor Items", getClass().getSimpleName(), _armors.size());
		LOG.info("{}: Loaded: {} Weapon Items", getClass().getSimpleName(), _weapons.size());
		LOG.info("{}: Loaded: {} Items in total.", getClass().getSimpleName(), (_etcItems.size() + _armors.size() + _weapons.size()));
	}
	
	/**
	 * Builds a variable in which all items are putting in in function of their ID.
	 * @param size
	 */
	private void buildFastLookupTable(int size)
	{
		// Create a FastLookUp Table called _allTemplates of size : value of the highest item ID
		LOG.info("{}: Highest item id used: {}", getClass().getSimpleName(), size);
		_allTemplates = new L2Item[size + 1];
		
		// Insert armor item in Fast Look Up Table
		for (L2Armor item : _armors.values())
		{
			_allTemplates[item.getId()] = item;
		}
		
		// Insert weapon item in Fast Look Up Table
		for (L2Weapon item : _weapons.values())
		{
			_allTemplates[item.getId()] = item;
		}
		
		// Insert etcItem item in Fast Look Up Table
		for (L2EtcItem item : _etcItems.values())
		{
			_allTemplates[item.getId()] = item;
		}
	}
	
	/**
	 * Returns the item corresponding to the item ID
	 * @param id : int designating the item
	 * @return L2Item
	 */
	public L2Item getTemplate(int id)
	{
		if ((id >= _allTemplates.length) || (id < 0))
		{
			return null;
		}
		
		return _allTemplates[id];
	}
	
	/**
	 * Create the L2ItemInstance corresponding to the Item Identifier and quantitiy add logs the activity. <B><U> Actions</U> :</B>
	 * <li>Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity</li>
	 * <li>Add the L2ItemInstance object to _allObjects of L2world</li>
	 * <li>Logs Item creation according to log settings</li>
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item Identifier of the item to be created
	 * @param count : int Quantity of items to be created for stackable items
	 * @param actor : L2PcInstance Player requesting the item creation
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new item
	 */
	public L2ItemInstance createItem(String process, int itemId, long count, L2PcInstance actor, Object reference)
	{
		// Create and Init the L2ItemInstance corresponding to the Item Identifier
		L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		
		if (process.equalsIgnoreCase("loot"))
		{
			ScheduledFuture<?> itemLootShedule;
			if ((reference instanceof L2Attackable) && ((L2Attackable) reference).isRaid()) // loot privilege for raids
			{
				L2Attackable raid = (L2Attackable) reference;
				// if in CommandChannel and was killing a World/RaidBoss
				if ((raid.getFirstCommandChannelAttacked() != null) && !CharacterConfig.AUTO_LOOT_RAIDS)
				{
					item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
					itemLootShedule = ThreadPoolManager.getInstance().scheduleGeneral(new ResetOwner(item), CharacterConfig.LOOT_RAIDS_PRIVILEGE_INTERVAL);
					item.setItemLootShedule(itemLootShedule);
				}
			}
			else if (!CharacterConfig.AUTO_LOOT || ((reference instanceof L2EventMonsterInstance) && ((L2EventMonsterInstance) reference).eventDropOnGround()))
			{
				item.setOwnerId(actor.getObjectId());
				itemLootShedule = ThreadPoolManager.getInstance().scheduleGeneral(new ResetOwner(item), 15000);
				item.setItemLootShedule(itemLootShedule);
			}
		}
		
		// Add the L2ItemInstance object to _allObjects of L2world
		L2World.getInstance().storeObject(item);
		
		// Set Item parameters
		if (item.isStackable() && (count > 1))
		{
			item.setCount(count);
		}
		
		if (GeneralConfig.LOG_ITEMS && !process.equals("Reset"))
		{
			if (!GeneralConfig.LOG_ITEMS_SMALL_LOG || (GeneralConfig.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == ADENA_ID))))
			{
				LoggingUtils.logItem(LOG_ITEMS, "CREATE: ", process, item, actor.getName(), reference);
			}
		}
		
		if (actor != null)
		{
			if (actor.isGM())
			{
				String referenceName = "no-reference";
				if (reference instanceof L2Object)
				{
					referenceName = (((L2Object) reference).getName() != null ? ((L2Object) reference).getName() : "no-name");
				}
				else if (reference instanceof String)
				{
					referenceName = (String) reference;
				}
				String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
				if (GeneralConfig.GMAUDIT)
				{
					GMAudit.auditGMAction(actor.getName() + " [" + actor.getObjectId() + "]", process + "(id: " + itemId + " count: " + count + " name: " + item.getItemName() + " objId: " + item.getObjectId() + ")", targetName, "L2Object referencing this action is: " + referenceName);
				}
			}
		}
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getItem());
		return item;
	}
	
	public L2ItemInstance createItem(String process, int itemId, int count, L2PcInstance actor)
	{
		return createItem(process, itemId, count, actor, null);
	}
	
	/**
	 * Destroys the L2ItemInstance.<br>
	 * <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Sets L2ItemInstance parameters to be unusable</li>
	 * <li>Removes the L2ItemInstance object to _allObjects of L2world</li>
	 * <li>Logs Item deletion according to log settings</li>
	 * </ul>
	 * @param process a string identifier of process triggering this action.
	 * @param item the item instance to be destroyed.
	 * @param actor the player requesting the item destroy.
	 * @param reference the object referencing current action like NPC selling item or previous item in transformation.
	 */
	public void destroyItem(String process, L2ItemInstance item, L2PcInstance actor, Object reference)
	{
		synchronized (item)
		{
			item.setCount(0);
			item.setOwnerId(0);
			item.setItemLocation(ItemLocation.VOID);
			item.setLastChange(L2ItemInstance.REMOVED);
			
			L2World.getInstance().removeObject(item);
			IdFactory.getInstance().releaseId(item.getObjectId());
			
			if (GeneralConfig.LOG_ITEMS)
			{
				if (!GeneralConfig.LOG_ITEMS_SMALL_LOG || (GeneralConfig.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == ADENA_ID))))
				{
					LoggingUtils.logItem(LOG_ITEMS, "DELETE: ", process, item, actor.getName(), reference);
				}
			}
			
			if (actor != null)
			{
				if (actor.isGM())
				{
					String referenceName = "no-reference";
					if (reference instanceof L2Object)
					{
						referenceName = (((L2Object) reference).getName() != null ? ((L2Object) reference).getName() : "no-name");
					}
					else if (reference instanceof String)
					{
						referenceName = (String) reference;
					}
					String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
					if (GeneralConfig.GMAUDIT)
					{
						GMAudit.auditGMAction(actor.getName() + " [" + actor.getObjectId() + "]", process + "(id: " + item.getId() + " count: " + item.getCount() + " itemObjId: " + item.getObjectId() + ")", targetName, "L2Object referencing this action is: " + referenceName);
					}
				}
			}
			
			// if it's a pet control item, delete the pet as well
			if (item.getItem().isPetItem())
			{
				try (Connection con = ConnectionFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
				{
					// Delete the pet in db
					statement.setInt(1, item.getObjectId());
					statement.execute();
				}
				catch (Exception e)
				{
					LOG.warn("Could not delete pet objectid!", e);
				}
			}
		}
	}
	
	public void reload()
	{
		load();
		EnchantItemHPBonusData.getInstance().load();
	}
	
	protected static class ResetOwner implements Runnable
	{
		L2ItemInstance _item;
		
		public ResetOwner(L2ItemInstance item)
		{
			_item = item;
		}
		
		@Override
		public void run()
		{
			_item.setOwnerId(0);
			_item.setItemLootShedule(null);
		}
	}
	
	public Set<Integer> getAllArmorsId()
	{
		return _armors.keySet();
	}
	
	public Set<Integer> getAllWeaponsId()
	{
		return _weapons.keySet();
	}
	
	public int getArraySize()
	{
		return _allTemplates.length;
	}
	
	public static ItemTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemTable INSTANCE = new ItemTable();
	}
}
