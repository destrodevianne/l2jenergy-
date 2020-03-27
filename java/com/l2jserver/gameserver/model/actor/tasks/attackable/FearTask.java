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
package com.l2jserver.gameserver.model.actor.tasks.attackable;

import com.l2jserver.gameserver.ai.L2AttackableAI;
import com.l2jserver.gameserver.model.actor.L2Character;

public class FearTask implements Runnable
{
	private final L2AttackableAI _ai;
	private final L2Character _effector;
	private boolean _start;
	
	public FearTask(L2AttackableAI ai, L2Character effector, boolean start)
	{
		_ai = ai;
		_effector = effector;
		_start = start;
	}
	
	@Override
	public void run()
	{
		final int fearTimeLeft = _ai.getFearTime() - L2AttackableAI.FEAR_TICKS;
		_ai.setFearTime(fearTimeLeft);
		_ai.onEvtAfraid(_effector, _start);
		_start = false;
	}
}
