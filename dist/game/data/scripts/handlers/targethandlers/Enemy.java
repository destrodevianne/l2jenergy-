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
package handlers.targethandlers;

import static com.l2jserver.gameserver.enums.skills.targets.L2TargetType.ENEMY;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

import com.l2jserver.gameserver.enums.skills.targets.L2TargetType;
import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Enemy target type handler.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class Enemy implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		switch (skill.getAffectScope())
		{
			case SINGLE:
			{
				if (target == null)
				{
					return EMPTY_TARGET_LIST;
				}
				
				final L2PcInstance player = activeChar.getActingPlayer();
				if (target.isDead() || (!target.isAttackable() && //
					(player != null) && //
					!player.checkIfPvP(target) && //
					!player.getCurrentSkill().isCtrlPressed()))
				{
					activeChar.sendPacket(INCORRECT_TARGET);
					return EMPTY_TARGET_LIST;
				}
				
				return new L2Character[]
				{
					target
				};
			}
		}
		return EMPTY_TARGET_LIST;
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return ENEMY;
	}
}
