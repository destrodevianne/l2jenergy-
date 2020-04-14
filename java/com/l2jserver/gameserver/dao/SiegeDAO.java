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

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.Siege;

/**
 * Siege DAO interface.
 * @author Мо3олЬ
 */
public interface SiegeDAO
{
	boolean checkIsRegistered(L2Clan clan, int castleid);
	
	void loadTrapUpgrade(int castleId);
	
	void clearSiegeClan(Siege clan);
	
	void clearSiegeWaitingClan(Castle clan);
	
	void updateShowNpcCrest(Castle castle);
}