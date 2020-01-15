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

public final class SendTimeCheck extends L2GameClientPacket
{
	private static final String _C__A0_SENDTIMECHECK = "[C] A0 SendTimeCheck";
	
	@SuppressWarnings("unused")
	private int _requestId;
	@SuppressWarnings("unused")
	private int _responseId;
	
	@Override
	protected void readImpl()
	{
		_requestId = readD();
		_responseId = readD();
	}
	
	@Override
	protected void runImpl()
	{
	}
	
	@Override
	public String getType()
	{
		return _C__A0_SENDTIMECHECK;
	}
}
