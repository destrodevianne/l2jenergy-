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
package com.l2jserver.gameserver.dao;

import com.l2jserver.gameserver.model.L2Clan;

/**
 * @author Мо3олЬ
 */
public interface ClanDAO
{
	void storeClan(L2Clan clan);
	
	void updateClan(L2Clan clan);
	
	void restoreClan(L2Clan clan);
	
	void changeAllyCrest(L2Clan clan, int crestId, boolean onlyThisClan);
	
	void changeLevel(L2Clan clan, int level);
	
	void changeClanCrest(L2Clan clan, int crestId);
	
	void changeLargeCrest(L2Clan clan, int crestId);
	
	void removeMember(int playerId, long clanJoinExpiryTime, long clanCreateExpiryTime);
	
	void updateSubPledge(L2Clan clan, int pledgeType);
	
	void setNotice(L2Clan clan, String notice, boolean enabled);
	
	void updateClanScore(L2Clan clan);
	
	void updateBloodOathCount(L2Clan clan);
	
	void updateBloodAllianceCount(L2Clan clan);
	
	void updateClanPrivsOld(L2Clan clan);
	
	void updateClanPrivsNew(L2Clan clan);
}
