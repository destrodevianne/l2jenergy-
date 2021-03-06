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

import com.l2jserver.gameserver.configuration.config.NpcConfig;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.drops.IDropItem;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Battlecruiser
 */
public interface IKillerChanceModifierStrategy extends INonGroupedKillerChanceModifierStrategy
{
	public static final IKillerChanceModifierStrategy DEFAULT_STRATEGY = (item, victim, killer) ->
	{
		int levelDifference = victim.getLevel() - killer.getLevel();
		if ((victim.isRaid()) && NpcConfig.DEEPBLUE_DROP_RULES_RAID)
		{
			// FIXME: Config?
			return Math.max(0, Math.min(1, (levelDifference * 0.15) + 1));
		}
		else if (NpcConfig.DEEPBLUE_DROP_RULES)
		{
			
			return Util.map(levelDifference, -NpcConfig.DROP_ITEM_MAX_LEVEL_DIFFERENCE, -NpcConfig.DROP_ITEM_MIN_LEVEL_DIFFERENCE, NpcConfig.DROP_ITEM_MIN_LEVEL_GAP_CHANCE, 100.0) / 100;
		}
		return 1;
	};
	public static final INonGroupedKillerChanceModifierStrategy DEFAULT_NONGROUP_STRATEGY = (item, victim, killer) ->
	{
		if (((!(victim.isRaid())) && NpcConfig.DEEPBLUE_DROP_RULES) || ((victim.isRaid()) && NpcConfig.DEEPBLUE_DROP_RULES_RAID))
		{
			int levelDifference = victim.getLevel() - killer.getLevel();
			if (item.getItemId() == Inventory.ADENA_ID)
			{
				
				return Util.map(levelDifference, -NpcConfig.DROP_ADENA_MAX_LEVEL_DIFFERENCE, -NpcConfig.DROP_ADENA_MIN_LEVEL_DIFFERENCE, NpcConfig.DROP_ADENA_MIN_LEVEL_GAP_CHANCE, 100.0) / 100;
			}
			return Util.map(levelDifference, -NpcConfig.DROP_ITEM_MAX_LEVEL_DIFFERENCE, -NpcConfig.DROP_ITEM_MIN_LEVEL_DIFFERENCE, NpcConfig.DROP_ITEM_MIN_LEVEL_GAP_CHANCE, 100.0) / 100;
		}
		return 1;
	};
	
	IKillerChanceModifierStrategy NO_RULES = (item, victim, killer) -> 1;
	
	public double getKillerChanceModifier(IDropItem item, L2Character victim, L2Character killer);
	
	@Override
	public default double getKillerChanceModifier(GeneralDropItem item, L2Character victim, L2Character killer)
	{
		return getKillerChanceModifier((IDropItem) item, victim, killer);
	}
}
