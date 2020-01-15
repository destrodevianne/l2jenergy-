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
package com.l2jserver.gameserver.configuration.config.events;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("events/olympiad.json")
public class OlympiadConfig
{
	@Setting(ignore = true)
	private static final Logger LOG = LoggerFactory.getLogger(OlympiadConfig.class);
	
	@Setting(name = "AltOlyStartTime")
	public static int ALT_OLY_START_TIME;
	
	@Setting(name = "AltOlyMin")
	public static int ALT_OLY_MIN;
	
	@Setting(name = "AltOlyMaxBuffs")
	public static int ALT_OLY_MAX_BUFFS;
	
	@Setting(name = "AltOlyCPeriod")
	public static long ALT_OLY_CPERIOD;
	
	@Setting(name = "AltOlyBattle")
	public static long ALT_OLY_BATTLE;
	
	@Setting(name = "AltOlyWPeriod")
	public static long ALT_OLY_WPERIOD;
	
	@Setting(name = "AltOlyVPeriod")
	public static long ALT_OLY_VPERIOD;
	
	@Setting(name = "AltOlyStartPoints")
	public static int ALT_OLY_START_POINTS;
	
	@Setting(name = "AltOlyWeeklyPoints")
	public static int ALT_OLY_WEEKLY_POINTS;
	
	@Setting(name = "AltOlyClassedParticipants")
	public static int ALT_OLY_CLASSED;
	
	@Setting(name = "AltOlyNonClassedParticipants")
	public static int ALT_OLY_NONCLASSED;
	
	@Setting(name = "AltOlyTeamsParticipants")
	public static int ALT_OLY_TEAMS;
	
	@Setting(name = "AltOlyRegistrationDisplayNumber")
	public static int ALT_OLY_REG_DISPLAY;
	
	@Setting(name = "AltOlyClassedReward", method = "parseItemsList")
	public static int[][] ALT_OLY_CLASSED_REWARD;
	
	@Setting(name = "AltOlyNonClassedReward", method = "parseItemsList")
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	
	@Setting(name = "AltOlyTeamReward", method = "parseItemsList")
	public static int[][] ALT_OLY_TEAM_REWARD;
	
	@Setting(name = "AltOlyCompRewItem")
	public static int ALT_OLY_COMP_RITEM;
	
	@Setting(name = "AltOlyMinMatchesForPoints")
	public static int ALT_OLY_MIN_MATCHES;
	
	@Setting(name = "AltOlyGPPerPoint")
	public static int ALT_OLY_GP_PER_POINT;
	
	@Setting(name = "AltOlyHeroPoints")
	public static int ALT_OLY_HERO_POINTS;
	
	@Setting(name = "AltOlyRank1Points")
	public static int ALT_OLY_RANK1_POINTS;
	
	@Setting(name = "AltOlyRank2Points")
	public static int ALT_OLY_RANK2_POINTS;
	
	@Setting(name = "AltOlyRank3Points")
	public static int ALT_OLY_RANK3_POINTS;
	
	@Setting(name = "AltOlyRank4Points")
	public static int ALT_OLY_RANK4_POINTS;
	
	@Setting(name = "AltOlyRank5Points")
	public static int ALT_OLY_RANK5_POINTS;
	
	@Setting(name = "AltOlyMaxPoints")
	public static int ALT_OLY_MAX_POINTS;
	
	@Setting(name = "AltOlyDividerClassed")
	public static int ALT_OLY_DIVIDER_CLASSED;
	
	@Setting(name = "AltOlyDividerNonClassed")
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	
	@Setting(name = "AltOlyMaxWeeklyMatches")
	public static int ALT_OLY_MAX_WEEKLY_MATCHES;
	
	@Setting(name = "AltOlyMaxWeeklyMatchesNonClassed")
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED;
	
	@Setting(name = "AltOlyMaxWeeklyMatchesClassed")
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED;
	
	@Setting(name = "AltOlyMaxWeeklyMatchesTeam")
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_TEAM;
	
	@Setting(name = "AltOlyLogFights")
	public static boolean ALT_OLY_LOG_FIGHTS;
	
	@Setting(name = "AltOlyShowMonthlyWinners")
	public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
	
	@Setting(name = "AltOlyAnnounceGames")
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	
	@Setting(name = "AltOlyRestrictedItems", splitter = ",", canNull = true)
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS = Arrays.asList(6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 9388, 9389, 9390, 17049, 17050, 17051, 17052, 17053, 17054, 17055, 17056, 17057, 17058, 17059, 17060, 17061, 20759, 20775, 20776, 20777, 20778, 14774);
	
	@Setting(name = "AltOlyEnchantLimit", minValue = -1)
	public static int ALT_OLY_ENCHANT_LIMIT;
	
	@Setting(name = "AltOlyWaitTime")
	public static int ALT_OLY_WAIT_TIME;
	
	@Setting(name = "AltOlyUseCustomPeriodSettings")
	public static boolean ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS;
	
	@Setting(name = "AltOlyPeriod")
	public static String ALT_OLY_PERIOD; // MONTH
	
	@Setting(name = "AltOlyPeriodMultiplier")
	public static int ALT_OLY_PERIOD_MULTIPLIER;
	
	@Setting(name = "AltOlyWinFameReward")
	public static boolean OLYMPIAD_WIN_REWARDS_FAME;
	
	@Setting(name = "AltOlyWinFameAmount")
	public static int OLYMPIAD_WIN_FAME_AMOUNT;
	
	public static int[][] parseItemsList(String line) // TODO: переписать
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
		{
			// nothing to do here
			return null;
		}
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		int[] tmp;
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				LOG.warn("parseItemsList[Config.load()]: invalid entry -> {}, should be itemId,itemNumber. Skipping to the next entry in the list.", valueSplit[0]);
				continue;
			}
			
			tmp = new int[2];
			try
			{
				tmp[0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				LOG.warn("parseItemsList[Config.load()]: invalid itemId -> {}, value must be an integer. Skipping to the next entry in the list.", valueSplit[0]);
				continue;
			}
			try
			{
				tmp[1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				LOG.warn("parseItemsList[Config.load()]: invalid item number -> {}, value must be an integer. Skipping to the next entry in the list.", valueSplit[1]);
				continue;
			}
			result[i++] = tmp;
		}
		return result;
	}
}
