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
package com.l2jserver.gameserver.configuration.config.custom;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("custom/voteBonus.json")
public class TopsConfig
{
	@Setting(name = "L2TopManagerEnabled")
	public static boolean L2_TOP_MANAGER_ENABLED;
	
	@Setting(name = "L2TopWebAddress", canNull = true)
	public static String L2_TOP_WEB_ADDRESS = "";
	
	@Setting(name = "L2TopSmsAddress", canNull = true)
	public static String L2_TOP_SMS_ADDRESS = "";
	
	@Setting(name = "L2TopRewardId")
	public static int L2_TOP_REWARD_ID;
	
	@Setting(name = "L2TopRewardCount")
	public static int L2_TOP_REWARD_COUNT;
	
	@Setting(name = "L2TopServerPrefix")
	public static String L2_TOP_SERVER_PREFIX;
	
	@Setting(name = "MMOTopEnable")
	public static boolean MMO_TOP_MANAGER_ENABLED;
	
	@Setting(name = "MMOTopUrl", canNull = true)
	public static String MMO_TOP_WEB_ADDRESS = "";
	
	@Setting(name = "MMOTopRewardId")
	public static int MMO_TOP_REWARD_ID;
	
	@Setting(name = "MMOTopRewardCount")
	public static int MMO_TOP_REWARD_COUNT;
	
	@Setting(name = "TopManagerInterval", increase = 300000)
	public static int TOP_MANAGER_INTERVAL;
	
	@Setting(name = "TopServerAddress")
	public static String TOP_SERVER_ADDRESS;
	
	@Setting(name = "TopSaveDays")
	public static int TOP_SAVE_DAYS;
}
