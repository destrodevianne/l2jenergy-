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
package com.l2jserver.gameserver.model.drops.strategy;

import com.l2jserver.gameserver.configuration.config.RatesConfig;
import com.l2jserver.gameserver.data.xml.impl.ChampionData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;

/**
 * @author Battlecruiser
 */
public interface IChanceMultiplierStrategy
{
	public static final IChanceMultiplierStrategy DROP = DEFAULT_STRATEGY(RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER);
	public static final IChanceMultiplierStrategy SPOIL = DEFAULT_STRATEGY(RatesConfig.RATE_CORPSE_DROP_CHANCE_MULTIPLIER);// TODO:fix add * Config.PREMIUM_RATE_SPOIL_CHANCE
	public static final IChanceMultiplierStrategy STATIC = (item, victim) -> 1;
	
	public static final IChanceMultiplierStrategy QUEST = (item, victim) ->
	{
		double championmult;
		if ((item.getItemId() == Inventory.ADENA_ID) || (item.getItemId() == Inventory.ANCIENT_ADENA_ID))
		{
			championmult = ChampionData.getInstance().getAdenaMultipler((L2Attackable) victim);
		}
		else
		{
			championmult = ChampionData.getInstance().getRewardMultipler((L2Attackable) victim);;
		}
		
		return (ChampionData.getInstance().isEnabled() && (victim != null) && victim.isChampion()) ? (RatesConfig.RATE_QUEST_DROP * championmult) : RatesConfig.RATE_QUEST_DROP;
	};
	
	public static IChanceMultiplierStrategy DEFAULT_STRATEGY(final double defaultMultiplier)
	{
		return (item, victim) ->
		{
			float multiplier = 1;
			if (victim.isChampion())
			{
				multiplier *= item.getItemId() != Inventory.ADENA_ID ? ChampionData.getInstance().getRewardMultipler((L2Attackable) victim) : ChampionData.getInstance().getAdenaMultipler((L2Attackable) victim);
			}
			
			Float dropChanceMultiplier = RatesConfig.RATE_DROP_CHANCE_MULTIPLIER.get(item.getItemId());
			if (dropChanceMultiplier != null)
			{
				multiplier *= dropChanceMultiplier;
			}
			else if (ItemTable.getInstance().getTemplate(item.getItemId()).hasExImmediateEffect())
			{
				multiplier *= RatesConfig.RATE_HERB_DROP_CHANCE_MULTIPLIER;
			}
			else if (victim.isRaid())
			{
				multiplier *= RatesConfig.RATE_RAID_DROP_CHANCE_MULTIPLIER;
			}
			else
			{
				multiplier *= defaultMultiplier;
			}
			return multiplier;
		};
	}
	
	public double getChanceMultiplier(GeneralDropItem item, L2Character victim);
}
