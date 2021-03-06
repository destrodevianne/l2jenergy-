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
package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.enums.ZoneId;
import com.l2jserver.gameserver.enums.items.EtcItemType;
import com.l2jserver.gameserver.enums.items.ItemType2;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.ItemList;
import com.l2jserver.gameserver.util.FloodProtectors;
import com.l2jserver.gameserver.util.FloodProtectors.Action;
import com.l2jserver.gameserver.util.GMAudit;
import com.l2jserver.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/02 21:25:21 $
 */
public final class RequestDropItem extends L2GameClientPacket
{
	private static final String _C__17_REQUESTDROPITEM = "[C] 17 RequestDropItem";
	
	private int _objectId;
	private long _count;
	private int _x;
	private int _y;
	private int _z;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readQ();
		_x = readD();
		_y = readD();
		_z = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if ((activeChar == null) || activeChar.isDead())
		{
			return;
		}
		// Flood protect drop to avoid packet lag
		if (!FloodProtectors.performAction(getClient(), Action.DROP_ITEM))
		{
			return;
		}
		
		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		
		if ((item == null) || (_count == 0) || !activeChar.validateItemManipulation(_objectId, "drop") || (!GeneralConfig.ALLOW_DISCARDITEM && !activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS))
			|| (!item.isDropable() && !(activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) && GeneralConfig.GM_TRADE_RESTRICTED_ITEMS)) || ((item.getItemType() == EtcItemType.PET_COLLAR) && activeChar.havePetInvItems()) || activeChar.isInsideZone(ZoneId.NO_ITEM_DROP))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		if (item.isQuestItem() && !(activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) && GeneralConfig.GM_TRADE_RESTRICTED_ITEMS))
		{
			return;
		}
		
		if (_count > item.getCount())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if ((CharacterConfig.PLAYER_SPAWN_PROTECTION > 0) && activeChar.isInvul() && !activeChar.isGM())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if (_count < 0)
		{
			Util.handleIllegalPlayerAction(activeChar, "[RequestDropItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to drop item with oid " + _objectId + " but has count < 0!", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		if (!item.isStackable() && (_count > 1))
		{
			Util.handleIllegalPlayerAction(activeChar, "[RequestDropItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to drop non-stackable item with oid " + _objectId + " but has count > 1!", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		if (GeneralConfig.JAIL_DISABLE_TRANSACTION && activeChar.isJailed())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "no_drop_in_jail"));
			return;
		}
		
		if (!activeChar.getAccessLevel().allowTransaction())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "transactions_disabled"));
			activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
		if (activeChar.isProcessingTransaction() || (activeChar.getPrivateStoreType() != PrivateStoreType.NONE))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		if (activeChar.isFishing())
		{
			// You can't mount, dismount, break and drop items while fishing
			activeChar.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_2);
			return;
		}
		if (activeChar.isFlying())
		{
			return;
		}
		
		// Cannot discard item that the skill is consuming
		if (activeChar.isCastingNow())
		{
			if ((activeChar.getCurrentSkill() != null) && (activeChar.getCurrentSkill().getSkill().getItemConsumeId() == item.getId()))
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}
		
		// Cannot discard item that the skill is consuming
		if (activeChar.isCastingSimultaneouslyNow())
		{
			if ((activeChar.getLastSimultaneousSkillCast() != null) && (activeChar.getLastSimultaneousSkillCast().getItemConsumeId() == item.getId()))
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}
		
		if ((ItemType2.QUEST == item.getItem().getType2()) && !activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_EXCHANGE_ITEM);
			return;
		}
		
		if (!activeChar.isInsideRadius(_x, _y, 0, 150, false, false) || (Math.abs(_z - activeChar.getZ()) > 50))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DISCARD_DISTANCE_TOO_FAR);
			return;
		}
		
		if (!activeChar.getInventory().canManipulateWithItemId(item.getId()))
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "item_cannot_use"));
			return;
		}
		
		if (item.isEquipped())
		{
			L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(item.getLocationSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (L2ItemInstance itm : unequiped)
			{
				itm.unChargeAllShots();
				iu.addModifiedItem(itm);
			}
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();
			
			ItemList il = new ItemList(activeChar, true);
			activeChar.sendPacket(il);
		}
		
		L2ItemInstance dropedItem = activeChar.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false, false);
		// activeChar.broadcastUserInfo();
		
		if (activeChar.isGM())
		{
			String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
			GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Drop", target, "(id: " + dropedItem.getId() + " name: " + dropedItem.getItemName() + " objId: " + dropedItem.getObjectId() + " x: " + activeChar.getX() + " y: " + activeChar.getY() + " z: "
				+ activeChar.getZ() + ")");
		}
		
		if ((dropedItem != null) && (dropedItem.getId() == Inventory.ADENA_ID) && (dropedItem.getCount() >= 1000000))
		{
			String msg = "Character (" + activeChar.getName() + ") has dropped (" + dropedItem.getCount() + ")adena at (" + _x + "," + _y + "," + _z + ")";
			LOG.warn(msg);
			AdminData.getInstance().broadcastMessageToGMs(msg);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__17_REQUESTDROPITEM;
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}
