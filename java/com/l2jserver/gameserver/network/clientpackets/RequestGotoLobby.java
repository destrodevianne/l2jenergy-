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

import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.CharacterSelectionInfo;

/**
 * (ch)
 * @author KenM
 */
public class RequestGotoLobby extends L2GameClientPacket
{
	private static final String _C__D0_38_REQUESTGOTOLOBBY = "[C] D0:38 RequestGotoLobby";
	
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		L2GameClient client = getClient();
		client.sendPacket(new CharacterSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1));
	}
	
	@Override
	public String getType()
	{
		return _C__D0_38_REQUESTGOTOLOBBY;
	}
}
