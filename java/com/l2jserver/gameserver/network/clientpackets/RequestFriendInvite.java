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

import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.friend.BlockList;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.FriendAddRequest;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendInvite extends L2GameClientPacket
{
	private static final String _C__77_REQUESTFRIENDINVITE = "[C] 77 RequestFriendInvite";
	
	private String _targetName;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final L2PcInstance target = L2World.getInstance().getPlayer(_targetName);
		
		// Target is not found in the game.
		if ((target == null) || !target.isOnline() || target.isInvisible())
		{
			player.sendPacket(SystemMessageId.THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME);
			return;
		}
		// You cannot add yourself to your own friend list.
		if (target == player)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST);
			return;
		}
		// Target is in olympiad.
		if (player.isInOlympiadMode() || target.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
			return;
		}
		// Target blocked active player.
		if (BlockList.isBlocked(target, player))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "player_target_block"));
			return;
		}
		
		// Target is blocked.
		if (BlockList.isBlocked(player, target))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BLOCKED_S1).addString(_targetName));
			return;
		}
		// Target already in friend list.
		if (player.isFriend(target.getObjectId()))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_IN_FRIENDS_LIST).addString(_targetName));
			return;
		}
		// Target is busy.
		if (target.isProcessingRequest())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addString(_targetName));
			return;
		}
		// Friend request sent.
		player.onTransactionRequest(target);
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_REQUESTED_S1_TO_BE_FRIEND).addString(_targetName));
		target.sendPacket(new FriendAddRequest(player.getName()));
	}
	
	@Override
	public String getType()
	{
		return _C__77_REQUESTFRIENDINVITE;
	}
}