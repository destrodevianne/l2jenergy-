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
package com.l2jserver.loginserver.network.gameserverpackets;

import com.l2jserver.commons.network.BaseRecievePacket;
import com.l2jserver.loginserver.LoginController;

/**
 * @author mrTJO
 */
public class PlayerTracert extends BaseRecievePacket
{
	public PlayerTracert(byte[] decrypt)
	{
		super(decrypt);
		String account = readS();
		String pcIp = readS();
		String hop1 = readS();
		String hop2 = readS();
		String hop3 = readS();
		String hop4 = readS();
		
		LoginController.getInstance().setAccountLastTracert(account, pcIp, hop1, hop2, hop3, hop4);
	}
}
