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

import com.l2jserver.gameserver.enums.PcCafeType;

/**
 * @author KenM
 * @author UnAfraid
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private final long _points;
	private final long _mAddPoint;
	private final int _mPeriodType;
	private final int _remainTime;
	private final PcCafeType _pointType;
	private final int _time;
	
	public ExPCCafePointInfo(final long points, final long mAddPoint, final int mPeriodType, final PcCafeType pointType, final int remainTime)
	{
		_points = points;
		_mAddPoint = mAddPoint;
		_mPeriodType = mPeriodType;
		_pointType = pointType;
		_remainTime = remainTime;
		_time = 0;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x32);
		writeD((int) _points); // num points
		writeD((int) _mAddPoint); // points inc display
		writeC(_mPeriodType); // period(0=don't show window,1=acquisition,2=use points)
		writeD(_remainTime); // period hours left
		writeC(_pointType.ordinal()); // points inc display color(0=yellow, 1=cyan-blue, 2=red, all other black)
		writeD(_time * 3); // value is in seconds * 3
	}
}
