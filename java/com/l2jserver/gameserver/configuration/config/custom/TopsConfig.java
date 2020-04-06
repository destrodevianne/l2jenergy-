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

import java.util.HashMap;
import java.util.Map;

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
	
	@Setting(name = "EnableVoteSystem")
	public static boolean ENABLE_VOTE_SYSTEM;
	
	@Setting(name = "AllowNetworkVoteReward")
	public static boolean ALLOW_NETWORK_VOTE_REWARD;
	
	@Setting(name = "NetworkServerLink", canNull = true)
	public static String NETWORK_SERVER_LINK;
	
	@Setting(name = "NetworkVotesDifference")
	public static int NETWORK_VOTES_DIFFERENCE;
	
	@Setting(name = "NetworkRewardCheckTime")
	public static int NETWORK_REWARD_CHECK_TIME;
	
	@Setting(name = "NetworkReward", method = "networkReward")
	public static Map<Integer, Integer> NETWORK_REWARD;
	
	@Setting(name = "NetworkDualboxesAllowed")
	public static int NETWORK_DUALBOXES_ALLOWED;
	
	@Setting(name = "AllowNetworkGameServerReport")
	public static boolean ALLOW_NETWORK_GAME_SERVER_REPORT;
	
	@Setting(name = "AllowTopzoneVoteReward")
	public static boolean ALLOW_TOPZONE_VOTE_REWARD;
	
	@Setting(name = "TopzoneServerLink", canNull = true)
	public static String TOPZONE_SERVER_LINK;
	
	@Setting(name = "TopzoneVotesDifference")
	public static int TOPZONE_VOTES_DIFFERENCE;
	
	@Setting(name = "TopzoneRewardCheckTime")
	public static int TOPZONE_REWARD_CHECK_TIME;
	
	@Setting(name = "TopzoneReward", method = "topzoneReward")
	public static Map<Integer, Integer> TOPZONE_REWARD;
	
	@Setting(name = "TopzoneDualboxesAllowed")
	public static int TOPZONE_DUALBOXES_ALLOWED;
	
	@Setting(name = "AllowTopzoneGameServerReport")
	public static boolean ALLOW_TOPZONE_GAME_SERVER_REPORT;
	
	@Setting(name = "AllowHopzoneVoteReward")
	public static boolean ALLOW_HOPZONE_VOTE_REWARD;
	
	@Setting(name = "HopzoneServerLink", canNull = true)
	public static String HOPZONE_SERVER_LINK;
	
	@Setting(name = "HopzoneVotesDifference")
	public static int HOPZONE_VOTES_DIFFERENCE;
	
	@Setting(name = "HopzoneRewardCheckTime")
	public static int HOPZONE_REWARD_CHECK_TIME;
	
	@Setting(name = "HopzoneReward", method = "hopzoneReward")
	public static Map<Integer, Integer> HOPZONE_REWARD;
	
	@Setting(name = "HopzoneDualboxesAllowed")
	public static int HOPZONE_DUALBOXES_ALLOWED;
	
	@Setting(name = "AllowHopzoneGameServerReport")
	public static boolean ALLOW_HOPZONE_GAME_SERVER_REPORT;
	
	public void networkReward(String value)
	{
		String[] array = value.split(";");
		NETWORK_REWARD = new HashMap<>(array.length);
		for (String i : array)
		{
			String[] small_reward = i.split(",");
			NETWORK_REWARD.put(Integer.parseInt(small_reward[0]), Integer.parseInt(small_reward[1]));
		}
	}
	
	public void topzoneReward(String value)
	{
		String[] array = value.split(";");
		TOPZONE_REWARD = new HashMap<>(array.length);
		for (String i : array)
		{
			String[] small_reward = i.split(",");
			TOPZONE_REWARD.put(Integer.parseInt(small_reward[0]), Integer.parseInt(small_reward[1]));
		}
	}
	
	public void hopzoneReward(String value)
	{
		String[] array = value.split(";");
		HOPZONE_REWARD = new HashMap<>(array.length);
		for (String i : array)
		{
			String[] small_reward = i.split(",");
			HOPZONE_REWARD.put(Integer.parseInt(small_reward[0]), Integer.parseInt(small_reward[1]));
		}
	}
}
