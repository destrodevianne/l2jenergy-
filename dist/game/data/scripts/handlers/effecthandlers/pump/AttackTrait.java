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
package handlers.effecthandlers.pump;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.enums.actors.TraitType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.stat.CharStat;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Attack Trait effect implementation.
 * @author NosBit
 */
public final class AttackTrait extends AbstractEffect
{
	private final Map<TraitType, Float> _attackTraits = new HashMap<>();
	
	public AttackTrait(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty())
		{
			LOG.warn("{}: this effect must have parameters!", getClass().getSimpleName());
			return;
		}
		
		for (Entry<String, Object> param : params.getSet().entrySet())
		{
			_attackTraits.put(TraitType.valueOf(param.getKey()), (Float.parseFloat((String) param.getValue()) + 100) / 100);
		}
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		final CharStat charStat = info.getEffected().getStat();
		synchronized (charStat.getAttackTraits())
		{
			for (Entry<TraitType, Float> trait : _attackTraits.entrySet())
			{
				charStat.getAttackTraits()[trait.getKey().getId()] /= trait.getValue();
				charStat.getAttackTraitsCount()[trait.getKey().getId()]--;
			}
		}
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		final CharStat charStat = info.getEffected().getStat();
		synchronized (charStat.getAttackTraits())
		{
			for (Entry<TraitType, Float> trait : _attackTraits.entrySet())
			{
				charStat.getAttackTraits()[trait.getKey().getId()] *= trait.getValue();
				charStat.getAttackTraitsCount()[trait.getKey().getId()]++;
			}
		}
	}
}
