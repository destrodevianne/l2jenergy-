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
package com.l2jserver.gameserver.model.stats.functions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Stats;

/**
 * Returns the initial value minus the function value, if the condition are met.
 * @author Zoey76
 */
public class FuncSub extends AbstractFunction
{
	public FuncSub(Stats stat, int order, Object owner, double value, Condition applayCond)
	{
		super(stat, order, owner, value, applayCond);
	}
	
	@Override
	public double calc(L2Character effector, L2Character effected, Skill skill, double initVal)
	{
		if ((getApplayCond() == null) || getApplayCond().test(effector, effected, skill))
		{
			return initVal - getValue();
		}
		return initVal;
	}
}
