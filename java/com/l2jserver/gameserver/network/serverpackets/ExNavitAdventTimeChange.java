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

import com.l2jserver.gameserver.configuration.config.custom.NevitConfig;

/**
 * @author mochitto, IrLex
 */
public class ExNavitAdventTimeChange extends L2GameServerPacket
{
	private final boolean _paused;
	private final int _time;
	
	public ExNavitAdventTimeChange(int time, boolean paused)
	{
		_time = Math.min(time, NevitConfig.NEVIT_BONUS_MAX_TIME);
		_paused = paused;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xE1);
		writeC(_paused ? 0x00 : 0x01); // state 0 - pause 1 - started
		writeD(_time); // left time in ms max is 16000 its 4m and state is automatically changed to quit
	}
}
