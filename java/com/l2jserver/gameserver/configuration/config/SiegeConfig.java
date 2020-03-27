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
@Configuration("siege.json")
public class SiegeConfig
{
	@Setting(name = "AttackerMaxClans")
	public static int ATTACKER_MAX_CLANS;
	
	@Setting(name = "AttackerRespawn")
	public static int ATTACKER_RESPAWN_DELAY;
	
	@Setting(name = "DefenderMaxClans")
	public static int DEFENDER_MAX_CLANS;
	
	@Setting(name = "MaxFlags")
	public static int FLAG_MAX_COUNT;
	
	@Setting(name = "SiegeClanMinLevel")
	public static int SIEGE_CLAN_MIN_LEVEL;
	
	@Setting(name = "SiegeLength")
	public static int SIEGE_LENGTH;
	
	@Setting(name = "BloodAllianceReward")
	public static int BLOOD_ALLIANCE_REWARD;
}
