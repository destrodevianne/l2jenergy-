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
package com.l2jserver.gameserver.configuration.config;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("pvp.json")
public class PvPConfig
{
	@Setting(name = "CanGMDropEquipment")
	public static boolean KARMA_DROP_GM;
	
	@Setting(name = "AwardPKKillPVPPoint")
	public static boolean KARMA_AWARD_PK_KILL;
	
	@Setting(name = "MinimumPKRequiredToDrop", minValue = 0)
	public static int KARMA_PK_LIMIT;
	
	@Setting(name = "ListOfPetItems", splitter = ",")
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	
	@Setting(name = "ListOfNonDroppableItems", splitter = ",")
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
	
	@Setting(name = "PvPVsNormalTime")
	public static int PVP_NORMAL_TIME;
	
	@Setting(name = "PvPVsPvPTime")
	public static int PVP_PVP_TIME;
}
