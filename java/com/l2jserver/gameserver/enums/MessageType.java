/*
 * Copyright (C) 2004-2019 L2J Server
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
package com.l2jserver.gameserver.enums;

public enum MessageType
{
	CUSTOM(-1),
	TEXT(0),
	FINISH(1),
	START(2),
	GAME_OVER(3),
	NUMBER_1(4),
	NUMBER_2(5),
	NUMBER_3(6),
	NUMBER_4(7),
	NUMBER_5(8);
	
	private final int _type;
	
	private MessageType(int type)
	{
		_type = type;
	}
	
	public int getType()
	{
		return _type;
	}
}
