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
package com.l2jserver.gameserver.model.actor.tasks.character;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;

public class CastTask implements Runnable
{
	private final L2Character _activeChar;
	private final L2Object _target;
	private final Skill _skill;
	
	public CastTask(L2Character actor, Skill skill, L2Object target)
	{
		_activeChar = actor;
		_target = target;
		_skill = skill;
	}
	
	@Override
	public void run()
	{
		if (_activeChar.isAttackingNow())
		{
			_activeChar.abortAttack();
		}
		_activeChar.getAI().changeIntentionToCast(_skill, _target);
	}
}
