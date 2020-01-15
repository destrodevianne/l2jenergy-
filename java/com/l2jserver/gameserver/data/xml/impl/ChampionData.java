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
package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.configuration.config.custom.ChampionConfig;
import com.l2jserver.gameserver.model.actor.L2Attackable;

/**
 * @author St3eT, Мо3олЬ
 */
public class ChampionData
{
	public boolean isEnabled()
	{
		return ChampionConfig.CHAMPION_ENABLE;
	}
	
	public boolean isPassive(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_PASSIVE_HARD : ChampionConfig.CHAMPION_PASSIVE_EASY;
	}
	
	public String getTitle(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_TITLE_HARD : ChampionConfig.CHAMPION_TITLE_EASY;
	}
	
	public int getChance(boolean isHard)
	{
		return isHard ? ChampionConfig.CHAMPION_FREQUENCY_HARD : ChampionConfig.CHAMPION_FREQUENCY_EASY;
	}
	
	public int getMinLv(boolean isHard)
	{
		return isHard ? ChampionConfig.CHAMPION_MIN_LVL_HARD : ChampionConfig.CHAMPION_MIN_LVL_EASY;
	}
	
	public int getMaxLv(boolean isHard)
	{
		return isHard ? ChampionConfig.CHAMPION_MAX_LVL_HARD : ChampionConfig.CHAMPION_MAX_LVL_EASY;
	}
	
	public int getHpMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_HP_HARD : ChampionConfig.CHAMPION_HP_EASY;
	}
	
	public int getRewardMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_REWARDS_HARD : ChampionConfig.CHAMPION_REWARDS_EASY;
	}
	
	public float getAdenaMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_ADENAS_REWARD_HARD : ChampionConfig.CHAMPION_ADENAS_REWARD_EASY;
	}
	
	public float getHpRegMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_HP_REGEN_HARD : ChampionConfig.CHAMPION_HP_REGEN_EASY;
	}
	
	public float getAttackMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_ATK_HARD : ChampionConfig.CHAMPION_ATK_EASY;
	}
	
	public float getAttacSpdkMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_SPD_ATK_HARD : ChampionConfig.CHAMPION_SPD_ATK_EASY;
	}
	
	public int getLowerLvChance(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE_HARD : ChampionConfig.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE_EASY;
	}
	
	public int getHigherLvChance(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE_HARD : ChampionConfig.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE_EASY;
	}
	
	public int getRewardId(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_REWARD_ID_HARD : ChampionConfig.CHAMPION_REWARD_ID_EASY;
	}
	
	public int getRewardCount(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_REWARD_QTY_HARD : ChampionConfig.CHAMPION_REWARD_QTY_EASY;
	}
	
	public boolean isEnabledVitality(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_ENABLE_VITALITY_HARD : ChampionConfig.CHAMPION_ENABLE_VITALITY_EASY;
	}
	
	public boolean inInstanceEnabled()
	{
		return ChampionConfig.CHAMPION_ENABLE_IN_INSTANCES;
	}
	
	public int getEnabledAura(L2Attackable champion)
	{
		return champion.isHardChampion() ? ChampionConfig.CHAMPION_AURA_HARD : ChampionConfig.CHAMPION_AURA_EASY;
	}
	
	public static ChampionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChampionData INSTANCE = new ChampionData();
	}
}
