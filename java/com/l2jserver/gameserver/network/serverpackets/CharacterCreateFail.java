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

public class CharacterCreateFail extends L2GameServerPacket
{
	public static final CharacterCreateFail REASON_CREATION_FAILED = new CharacterCreateFail(0x00);
	public static final CharacterCreateFail REASON_TOO_MANY_CHARACTERS = new CharacterCreateFail(0x01);
	public static final CharacterCreateFail REASON_NAME_ALREADY_EXISTS = new CharacterCreateFail(0x02);
	public static final CharacterCreateFail REASON_16_ENG_CHARS = new CharacterCreateFail(0x03);
	public static final CharacterCreateFail REASON_INCORRECT_NAME = new CharacterCreateFail(0x04);
	public static final CharacterCreateFail REASON_CREATE_NOT_ALLOWED = new CharacterCreateFail(0x05);
	public static final CharacterCreateFail REASON_CHOOSE_ANOTHER_SVR = new CharacterCreateFail(0x06);
	
	private final int _error;
	
	public CharacterCreateFail(int errorCode)
	{
		_error = errorCode;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x10);
		writeD(_error);
	}
}
