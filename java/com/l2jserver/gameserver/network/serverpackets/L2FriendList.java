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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.friend.FriendInfo;

public class L2FriendList extends L2GameServerPacket
{
	private List<FriendInfo> _info;
	
	public L2FriendList(L2PcInstance player)
	{
		if (!player.hasFriends())
		{
			_info = Collections.emptyList();
			return;
		}
		
		_info = new ArrayList<>(player.getFriends().size());
		for (int objId : player.getFriends())
		{
			final String name = CharNameTable.getInstance().getNameById(objId);
			final L2PcInstance friend = L2World.getInstance().getPlayer(objId);
			final boolean online = (friend != null) && friend.isOnline();
			_info.add(new FriendInfo(objId, name, online));
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x75);
		writeD(_info.size());
		for (FriendInfo info : _info)
		{
			writeD(info.getCharId()); // character id
			writeS(info.getCharName());
			writeD(info.getCharOnline() ? 0x01 : 0x00); // online
			writeD(info.getCharOnline() ? info.getCharId() : 0x00); // object id if online
		}
	}
}
