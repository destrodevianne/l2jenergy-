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
package com.l2jserver.loginserver.network.gameserverpackets;

import com.l2jserver.commons.network.BaseRecievePacket;
import com.l2jserver.loginserver.GameServerThread;

/**
 * @author -Wooden-
 */
public class PlayerInGame extends BaseRecievePacket
{
	public PlayerInGame(byte[] decrypt, GameServerThread server)
	{
		super(decrypt);
		int size = readH();
		for (int i = 0; i < size; i++)
		{
			String account = readS();
			server.addAccountOnGameServer(account);
			server.broadcastToTelnet("Account " + account + " logged in GameServer " + server.getServerId());
		}
	}
}
