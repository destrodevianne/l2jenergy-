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
public interface IAmountMultiplierStrategy
{
	public static final IAmountMultiplierStrategy DROP = DEFAULT_STRATEGY(RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER);
	public static final IAmountMultiplierStrategy SPOIL = DEFAULT_STRATEGY(RatesConfig.RATE_CORPSE_DROP_AMOUNT_MULTIPLIER); // TODO:fix add * Config.PREMIUM_RATE_SPOIL_AMMOUNT
	public static final IAmountMultiplierStrategy STATIC = (item, victim) -> 1;
	
	public static IAmountMultiplierStrategy DEFAULT_STRATEGY(final double defaultMultiplier)
	{
		return (item, victim) ->
		{
			double multiplier = 1;
			if (victim.isChampion())
			{
				multiplier *= item.getItemId() != Inventory.ADENA_ID ? ChampionData.getInstance().getRewardMultipler((L2Attackable) victim) : ChampionData.getInstance().getAdenaMultipler((L2Attackable) victim);
			}
			Float dropAmountMultiplier = RatesConfig.RATE_DROP_AMOUNT_MULTIPLIER.get(item.getItemId());
			if (dropAmountMultiplier != null)
			{
				multiplier *= dropAmountMultiplier;
			}
			else if (ItemTable.getInstance().getTemplate(item.getItemId()).hasExImmediateEffect())
			{
				multiplier *= RatesConfig.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
			}
			else if (victim.isRaid())
			{
				multiplier *= RatesConfig.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
			}
			else
			{
				multiplier *= defaultMultiplier;
			}
			return multiplier;
		};
	}
	
	public double getAmountMultiplier(GeneralDropItem item, L2Character victim);
}
