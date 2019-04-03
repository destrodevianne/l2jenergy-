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
@Configuration("pcCafe.json")
public class PcCafeConfig
{
	@Setting(name = "AltPcBangPointsEnabled")
	public static boolean ALT_PCBANG_POINTS_ENABLED;
	
	@Setting(name = "AltPcBangPointsMax", minValue = 0, maxValue = 200000)
	public static int MAX_PC_BANG_POINTS;
	
	@Setting(name = "AltPcBangPointsDelay")
	public static int ALT_PCBANG_POINTS_DELAY;
	
	@Setting(name = "AltPcBangPointsMinLvl", maxValue = 86)
	public static int ALT_PCBANG_POINTS_MIN_LVL;
	
	@Setting(name = "AltPcBangPointsBonus")
	public static int ALT_PCBANG_POINTS_BONUS;
	
	@Setting(name = "AltPcBangPointsDoubleChance")
	public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
	
	@Setting(name = "AltPcBangDailyBonusEnabled")
	public static boolean ENABLE_DAILY_BONUS_KEY;
	
	@Setting(name = "AltPcBangDailyBonusPoints")
	public static int ALT_PCBANG_DIALY_BONUS_POINTS;
}
