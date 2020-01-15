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
package com.l2jserver.gameserver.dao;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Player DAO interface.
 * @author Zoey76
 */
public interface PlayerDAO
{
	void storeCharBase(L2PcInstance player);
	
	boolean insert(L2PcInstance player);
	
	void updateOnlineStatus(L2PcInstance player);
	
	L2PcInstance load(int objectId);
	
	void loadCharacters(L2PcInstance player);
	
	void checkAndSave(long date, String nick, int mult);
	
	void clean();
	
	boolean checkAccount(L2PcInstance player, String repairplayer);
	
	boolean checkJail(L2PcInstance player);
	
	void repairBadCharacter(String playerName);
	
	String getCharList(L2PcInstance player);
}
