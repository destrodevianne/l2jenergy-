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

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("events/fishingTournament.json")
public class FishingConfig
{
	@Setting(name = "AltFishChampionshipEnabled")
	public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
	
	@Setting(name = "AltFishChampionshipRewardItemId")
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
	
	@Setting(name = "AltFishChampionshipReward1")
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
	
	@Setting(name = "AltFishChampionshipReward2")
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
	
	@Setting(name = "AltFishChampionshipReward3")
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
	
	@Setting(name = "AltFishChampionshipReward4")
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
	
	@Setting(name = "AltFishChampionshipReward5")
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
}
