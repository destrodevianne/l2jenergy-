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
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.ClanPrivilege;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.ClanWarehouse;
import com.l2jserver.gameserver.model.itemcontainer.ItemContainer;
import com.l2jserver.gameserver.model.itemcontainer.PcWarehouse;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.ItemList;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.util.FloodProtectors;
import com.l2jserver.gameserver.util.FloodProtectors.Action;
import com.l2jserver.gameserver.util.Util;

/**
 * This class ... 32 SendWareHouseWithDrawList cd (dd) WootenGil rox :P
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/29 23:15:16 $
 */
public final class SendWareHouseWithDrawList extends L2GameClientPacket
{
	private static final String _C__32_SENDWAREHOUSEWITHDRAWLIST = "[C] 3C SendWareHouseWithDrawList";
	
	private static final int BATCH_LENGTH = 12; // length of the one item
	
	private ItemHolder _items[] = null;
	
	@Override
	protected void readImpl()
	{
		final int count = readD();
		if ((count <= 0) || (count > MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != _buf.remaining()))
		{
			return;
		}
		
		_items = new ItemHolder[count];
		for (int i = 0; i < count; i++)
		{
			int objId = readD();
			long cnt = readQ();
			if ((objId < 1) || (cnt < 0))
			{
				_items = null;
				return;
			}
			_items[i] = new ItemHolder(objId, cnt);
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
		{
			return;
		}
		
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!FloodProtectors.performAction(getClient(), Action.TRANSACTION))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "withdrawing_too_fast"));
			return;
		}
		
		final ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
		{
			return;
		}
		
		final L2Npc manager = player.getLastFolkNPC();
		if (((manager == null) || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM())
		{
			return;
		}
		
		if (!(warehouse instanceof PcWarehouse) && !player.getAccessLevel().allowTransaction())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "transactions_disabled"));
			return;
		}
		
		// Alt game - Karma punishment
		if (!CharacterConfig.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getKarma() > 0))
		{
			return;
		}
		
		if (CharacterConfig.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH)
		{
			if ((warehouse instanceof ClanWarehouse) && !player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE))
			{
				return;
			}
		}
		else
		{
			if ((warehouse instanceof ClanWarehouse) && !player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_RETRIEVE_ITEMS_FROM_CLAN_WAREHOUSE);
				return;
			}
		}
		
		int weight = 0;
		int slots = 0;
		
		for (ItemHolder i : _items)
		{
			// Calculate needed slots
			L2ItemInstance item = warehouse.getItemByObjectId(i.getId());
			if ((item == null) || (item.getCount() < i.getCount()))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to withdraw non-existent item from warehouse.", GeneralConfig.DEFAULT_PUNISH);
				return;
			}
			
			weight += i.getCount() * item.getItem().getWeight();
			if (!item.isStackable())
			{
				slots += i.getCount();
			}
			else if (player.getInventory().getItemByItemId(item.getId()) == null)
			{
				slots++;
			}
		}
		
		// Item Max Limit Check
		if (!player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.SLOTS_FULL);
			return;
		}
		
		// Weight limit Check
		if (!player.getInventory().validateWeight(weight))
		{
			player.sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
			return;
		}
		
		// Proceed to the transfer
		InventoryUpdate playerIU = GeneralConfig.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		for (ItemHolder i : _items)
		{
			L2ItemInstance oldItem = warehouse.getItemByObjectId(i.getId());
			if ((oldItem == null) || (oldItem.getCount() < i.getCount()))
			{
				LOG.warn("Error withdrawing a warehouse object for char {} (olditem == null)", player.getName());
				return;
			}
			final L2ItemInstance newItem = warehouse.transferItem(warehouse.getName(), i.getId(), i.getCount(), player.getInventory(), player, manager);
			if (newItem == null)
			{
				LOG.warn("Error withdrawing a warehouse object for char {} (newitem == null)", player.getName());
				return;
			}
			
			if (playerIU != null)
			{
				if (newItem.getCount() > i.getCount())
				{
					playerIU.addModifiedItem(newItem);
				}
				else
				{
					playerIU.addNewItem(newItem);
				}
			}
		}
		
		// Send updated item list to the player
		if (playerIU != null)
		{
			player.sendPacket(playerIU);
		}
		else
		{
			player.sendPacket(new ItemList(player, false));
		}
		
		// Update current load status on player
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}
	
	@Override
	public String getType()
	{
		return _C__32_SENDWAREHOUSEWITHDRAWLIST;
	}
}
