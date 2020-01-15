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

import java.util.List;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Friend DAO interface.
 * @author Zoey76
 */
public interface FriendDAO
{
	void load(L2PcInstance player);
	
	List<Integer> loadList(int objectId);
	
	void removeFromDB(L2PcInstance player, int targetId);
	
	void persistInDB(L2PcInstance player, int targetId);
}
