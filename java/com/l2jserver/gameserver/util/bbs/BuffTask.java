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
package com.l2jserver.gameserver.util.bbs;

import java.util.List;

import com.l2jserver.commons.collections.CollectionUtils;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.community.CBufferConfig;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.skills.Skill;

public class BuffTask implements Runnable
{
	private static final double enchantedMultiplier = 1;
	private final List<Buff> _buffList;
	private int _index;
	private final L2Playable _playable;
	
	public BuffTask(final List<Buff> buffList, final L2Playable playable)
	{
		_buffList = buffList;
		_playable = playable;
	}
	
	public void buff(int id, int level, L2Playable playable)
	{
		if ((playable == null) || (id < 20))
		{
			return;
		}
		
		if (id == 1363)
		{
			decreaseHealth(playable);
		}
		
		final Skill skill = SkillData.getInstance().getSkill(id, level > 0 ? level : SkillData.getInstance().getMaxLevel(id));
		int time = CBufferConfig.CB_BUFFER_TIME * 60000;
		if (skill.getLevel() < level)
		{
			time *= enchantedMultiplier;
		}
		skill.applyEffects(playable, playable, true, false, false, time);
	}
	
	public void decreaseHealth(L2Playable target)
	{
		double hp = target.getCurrentHp() - (target.getMaxHp() * 0.20);
		target.setCurrentHp(hp > 0 ? hp : 1);
	}
	
	@Override
	public void run()
	{
		final Buff poll = CollectionUtils.safeGet(_buffList, _index);
		if (poll == null)
		{
			return;
		}
		buff(poll.getId(), poll.getLevel(), _playable);
		
		_index += 1;
		
		ThreadPoolManager.getInstance().scheduleGeneral(this, 50);
	}
}
