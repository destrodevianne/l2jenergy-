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

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class ExJumpToLocation extends L2GameServerPacket
{
	private final int _objectId;
	private final Location _current;
	private final Location _destination;
	
	public ExJumpToLocation(final L2PcInstance player, final Location current, final Location destination)
	{
		_objectId = player.getObjectId();
		_current = current;
		_destination = destination;
	}
	
	public ExJumpToLocation(L2PcInstance player)
	{
		_objectId = player.getObjectId();
		_current = player.getLocation();
		_destination = _current;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x88);
		
		writeD(_objectId);
		writeD(_destination.getX());
		writeD(_destination.getY());
		writeD(_destination.getZ());
		
		writeD(_current.getX());
		writeD(_current.getY());
		writeD(_current.getZ());
	}
}
