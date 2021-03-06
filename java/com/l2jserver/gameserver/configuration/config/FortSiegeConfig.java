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
@Configuration("fortSiege.json")
public class FortSiegeConfig
{
	@Setting(name = "JustToTerritory")
	public static boolean JUST_TO_TERRITORY;
	
	@Setting(name = "AttackerMaxClans")
	public static int ATTACKER_MAX_CLANS;
	
	@Setting(name = "MaxFlags")
	public static int FLAG_MAX_COUNT;
	
	@Setting(name = "SiegeClanMinLevel")
	public static int SIEGE_CLAN_MIN_LEVEL;
	
	@Setting(name = "SiegeLength")
	public static int SIEGE_LENGTH;
	
	@Setting(name = "CountDownLength")
	public static int COUNT_DOWN_LENGTH;
	
	@Setting(name = "SuspiciousMerchantRespawnDelay")
	public static int SUSPICIOUS_MERCHANT_RESPAWN_DELAY;
}
