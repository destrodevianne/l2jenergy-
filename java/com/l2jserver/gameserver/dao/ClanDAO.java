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

import java.util.Map;

import com.l2jserver.gameserver.model.L2Clan;

/**
 * Clan DAO interface.
 * @author Мо3олЬ, Zoey76
 */
public interface ClanDAO
{
	void storeClan(L2Clan clan); // Переделать
	
	void updateClan(L2Clan clan); // Переделать
	
	void restoreClan(L2Clan clan); // Переделать
	
	void updateSubPledge(L2Clan clan, int pledgeType); // Переделать
	
	void changeLevel(int level, int clan);
	
	void changeClanCrest(int crestId, int clan);
	
	void changeLargeCrest(int crestId, int clan);
	
	void removeMember(int playerId, long clanJoinExpiryTime, long clanCreateExpiryTime);
	
	void setNotice(int clan, String notice, boolean enabled);
	
	void updateClanScore(int reputationScore, int clan);
	
	void updateBloodOathCount(int bloodOath, int clan);
	
	void updateBloodAllianceCount(int bloodAlliance, int clan);
	
	void updateClanPrivsOld(int leader);
	
	void updateClanPrivsNew(int leader);
	
	Map<Integer, Integer> getPrivileges(int clanId);
	
	void storePrivileges(int clanId, int rank, int privileges);
}
