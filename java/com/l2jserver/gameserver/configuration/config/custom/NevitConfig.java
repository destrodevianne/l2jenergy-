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
package com.l2jserver.gameserver.configuration.config.custom;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("custom/nevitSystem.json")
public class NevitConfig
{
	@Setting(name = "NevitBonusMaxPoints")
	public static int NEVIT_BONUS_MAX_POINTS;
	
	@Setting(name = "NevitBonusMaxTime")
	public static int NEVIT_BONUS_MAX_TIME;
	
	@Setting(name = "NevitBonusRefreshRate")
	public static int NEVIT_BONUS_REFRESH_RATE;
	
	@Setting(name = "NevitBonusRefreshPoints")
	public static int NEVIT_BONUS_POINTS_ON_REFRESH;
	
	@Setting(name = "NevitBonusEffectTime")
	public static int NEVIT_BONUS_EFFECT_TIME;
	
	@Setting(name = "NevitBonusExtraPoints")
	public static boolean NEVIT_BONUS_EXTRA_POINTS;
	
	@Setting(name = "NevitBonusExtraPointsAllTime")
	public static boolean NEVIT_BONUS_EXTRA_POINTS_ALL_TIME;
}
