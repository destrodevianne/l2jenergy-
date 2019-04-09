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
package com.l2jserver.gameserver.configuration.config;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("conquerableHallSiege.json")
public class ConquerableHallSiegeConfig
{
	@Setting(name = "MinClanLevel")
	public static int CHS_CLAN_MINLEVEL;
	
	@Setting(name = "MaxAttackers")
	public static int CHS_MAX_ATTACKERS;
	
	@Setting(name = "MaxFlagsPerClan")
	public static int CHS_MAX_FLAGS_PER_CLAN;
	
	@Setting(name = "EnableFame")
	public static boolean CHS_ENABLE_FAME;
	
	@Setting(name = "FameAmount")
	public static int CHS_FAME_AMOUNT;
	
	@Setting(name = "FameFrequency")
	public static int CHS_FAME_FREQUENCY;
}
