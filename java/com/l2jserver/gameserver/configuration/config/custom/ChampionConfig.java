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
@Configuration("custom/champion.json")
public class ChampionConfig
{
	@Setting(name = "ChampionEnable")
	public static boolean CHAMPION_ENABLE;
	
	@Setting(name = "ChampionPassiveEasy")
	public static boolean CHAMPION_PASSIVE_EASY;
	
	@Setting(name = "ChampionPassiveHard")
	public static boolean CHAMPION_PASSIVE_HARD;
	
	@Setting(name = "ChampionFrequencyEasy")
	public static int CHAMPION_FREQUENCY_EASY;
	
	@Setting(name = "ChampionFrequencyHard")
	public static int CHAMPION_FREQUENCY_HARD;
	
	@Setting(name = "ChampionTitleEasy")
	public static String CHAMPION_TITLE_EASY;
	
	@Setting(name = "ChampionTitleHard")
	public static String CHAMPION_TITLE_HARD;
	
	@Setting(name = "ChampionMinLevelEasy", minValue = 1, maxValue = 90)
	public static int CHAMPION_MIN_LVL_EASY;
	
	@Setting(name = "ChampionMinLevelHard", minValue = 1, maxValue = 90)
	public static int CHAMPION_MIN_LVL_HARD;
	
	@Setting(name = "ChampionMaxLevelEasy", minValue = 1, maxValue = 90)
	public static int CHAMPION_MAX_LVL_EASY;
	
	@Setting(name = "ChampionMaxLevelHard", minValue = 1, maxValue = 90)
	public static int CHAMPION_MAX_LVL_HARD;
	
	@Setting(name = "ChampionHpEasy")
	public static int CHAMPION_HP_EASY;
	
	@Setting(name = "ChampionHpHard")
	public static int CHAMPION_HP_HARD;
	
	@Setting(name = "ChampionRewardsEasy")
	public static int CHAMPION_REWARDS_EASY;
	
	@Setting(name = "ChampionRewardsHard")
	public static int CHAMPION_REWARDS_HARD;
	
	@Setting(name = "ChampionAdenasRewardsEasy")
	public static float CHAMPION_ADENAS_REWARD_EASY;
	
	@Setting(name = "ChampionAdenasRewardsHard")
	public static float CHAMPION_ADENAS_REWARD_HARD;
	
	@Setting(name = "ChampionHpRegenEasy")
	public static float CHAMPION_HP_REGEN_EASY;
	
	@Setting(name = "ChampionHpRegenHard")
	public static float CHAMPION_HP_REGEN_HARD;
	
	@Setting(name = "ChampionAtkEasy")
	public static float CHAMPION_ATK_EASY;
	
	@Setting(name = "ChampionAtkHard")
	public static float CHAMPION_ATK_HARD;
	
	@Setting(name = "ChampionSpdAtkEasy")
	public static float CHAMPION_SPD_ATK_EASY;
	
	@Setting(name = "ChampionSpdAtkHard")
	public static float CHAMPION_SPD_ATK_HARD;
	
	@Setting(name = "ChampionRewardLowerLvlItemChanceEasy")
	public static int CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE_EASY;
	
	@Setting(name = "ChampionRewardHigherLvlItemChanceEasy")
	public static int CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE_EASY;
	
	@Setting(name = "ChampionRewardLowerLvlItemChanceHard")
	public static int CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE_HARD;
	
	@Setting(name = "ChampionRewardHigherLvlItemChanceHard")
	public static int CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE_HARD;
	
	@Setting(name = "ChampionRewardItemIDEasy")
	public static int CHAMPION_REWARD_ID_EASY;
	
	@Setting(name = "ChampionRewardItemIDHard")
	public static int CHAMPION_REWARD_ID_HARD;
	
	@Setting(name = "ChampionRewardItemQtyEasy")
	public static int CHAMPION_REWARD_QTY_EASY;
	
	@Setting(name = "ChampionRewardItemQtyHard")
	public static int CHAMPION_REWARD_QTY_HARD;
	
	@Setting(name = "ChampionEnableVitalityEasy")
	public static boolean CHAMPION_ENABLE_VITALITY_EASY;
	
	@Setting(name = "ChampionEnableVitalityHard")
	public static boolean CHAMPION_ENABLE_VITALITY_HARD;
	
	@Setting(name = "ChampionEnableInInstances")
	public static boolean CHAMPION_ENABLE_IN_INSTANCES;
	
	@Setting(name = "ChampionAuraHard")
	public static int CHAMPION_AURA_HARD;
	
	@Setting(name = "ChampionAuraEasy")
	public static int CHAMPION_AURA_EASY;
}
