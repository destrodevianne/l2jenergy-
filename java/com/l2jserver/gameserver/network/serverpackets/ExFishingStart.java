/*
 * Copyright (C) 2004-2018 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;

/**
 * @author -Wooden-
 */
public class ExFishingStart extends L2GameServerPacket
{
	private final L2Character _activeChar;
	private final Location _loc;
	private final int _fishType;
	private final boolean _isNightLure;
	
	public ExFishingStart(L2Character character, int fishType, Location loc, boolean isNightLure)
	{
		_activeChar = character;
		_fishType = fishType;
		_loc = loc;
		_isNightLure = isNightLure;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1E);
		writeD(_activeChar.getObjectId());
		writeD(_fishType); // fish type
		writeD(_loc.getX()); // x position
		writeD(_loc.getY()); // y position
		writeD(_loc.getZ()); // z position
		writeC(_isNightLure ? 0x01 : 0x00); // night lure
		writeC(Config.ALT_FISH_CHAMPIONSHIP_ENABLED ? 0x01 : 0x00); // show fish rank result button
	}
}