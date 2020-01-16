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

import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.util.FloodProtectors;
import com.l2jserver.gameserver.util.FloodProtectors.Action;
import com.l2jserver.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGiveItemToPet extends L2GameClientPacket
{
	private static final String _C__95_REQUESTCIVEITEMTOPET = "[C] 95 RequestGiveItemToPet";
	
	private int _objectId;
	private long _amount;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_amount = readQ();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if ((_amount <= 0) || (player == null) || !player.hasPet())
		{
			return;
		}
		
		if (!FloodProtectors.performAction(getClient(), Action.TRANSACTION))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "give_item_pet_too_fast"));
			return;
		}
		
		if (player.getActiveEnchantItemId() != L2PcInstance.ID_NONE)
		{
			return;
		}
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getKarma() > 0))
		{
			return;
		}
		
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
			return;
		}
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}
		
		if (player.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "no_exchange_items"));
			return;
		}
		
		final L2ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		if ((item == null) || item.isAugmented())
		{
			return;
		}
		
		if (_amount > item.getCount())
		{
			Util.handleIllegalPlayerAction(player, getClass().getSimpleName() + ": Character " + player.getName() + " of account " + player.getAccountName() + " tried to get item with oid " + _objectId + " from pet but has invalid count " + _amount + " item count: "
				+ item.getCount(), Config.DEFAULT_PUNISH);
			return;
		}
		
		if (item.isHeroItem() || !item.isDropable() || !item.isDestroyable() || !item.isTradeable() || (item.getItem().getItemType() == EtcItemType.ARROW) || (item.getItem().getItemType() == EtcItemType.SHOT))
		{
			player.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return;
		}
		
		final L2PetInstance pet = (L2PetInstance) player.getSummon();
		if (pet.isDead())
		{
			player.sendPacket(SystemMessageId.CANNOT_GIVE_ITEMS_TO_DEAD_PET);
			return;
		}
		
		if (Util.calculateDistance(player, pet, true, false) > L2Npc.INTERACTION_DISTANCE)
		{
			player.sendPacket(SystemMessageId.TARGET_TOO_FAR);
			return;
		}
		
		if (!pet.getInventory().validateCapacity(item))
		{
			player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
			return;
		}
		
		if (!pet.getInventory().validateWeight(item, _amount))
		{
			player.sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
			return;
		}
		
		if (player.transferItem("Transfer", _objectId, _amount, pet.getInventory(), pet) == null)
		{
			LOG.warn("Invalid item transfer request: {}(pet) --> {}", pet.getName(), player.getName());
		}
	}
	
	@Override
	public String getType()
	{
		return _C__95_REQUESTCIVEITEMTOPET;
	}
}
