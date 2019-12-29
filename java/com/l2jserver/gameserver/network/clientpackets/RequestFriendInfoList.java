/*
 * Copyright (C) 2004-2019 L2jEnergy Server
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

import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendInfoList extends L2GameClientPacket
{
	private static final String _C__6A_REQUESTFRIENDINFOLIST = "[C] 6A RequestFriendInfoList";
	
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(SystemMessageId.FRIEND_LIST_HEADER);
		if (activeChar.hasFriends())
		{
			for (int id : activeChar.getFriends())
			{
				final String friendName = CharNameTable.getInstance().getNameById(id);
				if (friendName == null)
				{
					continue;
				}
				
				final L2PcInstance friend = L2World.getInstance().getPlayer(friendName);
				final SystemMessage sm = SystemMessage.getSystemMessage((friend == null) || !friend.isOnline() ? SystemMessageId.S1_OFFLINE : SystemMessageId.S1_ONLINE);
				sm.addString(friendName);
				activeChar.sendPacket(sm);
			}
		}
		activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
	}
	
	@Override
	public String getType()
	{
		return _C__6A_REQUESTFRIENDINFOLIST;
	}
}
