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

import com.l2jserver.gameserver.enums.ZoneId;
import com.l2jserver.gameserver.enums.skills.targets.L2TargetType;
import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Enemy Summon target handler implementation.
 * @author UnAfraid
 */
public class EnemySummon implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if ((target != null) && target.isSummon())
		{
			final L2Summon targetSummon = (L2Summon) target;
			if ((activeChar.isPlayer() && (activeChar.getSummon() != targetSummon) && //
				!targetSummon.isDead() && ((targetSummon.getOwner().getPvpFlag() != 0) || (targetSummon.getOwner().getKarma() > 0))) || //
				(targetSummon.getOwner().isInsideZone(ZoneId.PVP) && activeChar.getActingPlayer().isInsideZone(ZoneId.PVP)) || //
				(targetSummon.getOwner().isInDuel() && activeChar.getActingPlayer().isInDuel() && (targetSummon.getOwner().getDuelId() == activeChar.getActingPlayer().getDuelId())))
			{
				return new L2Character[]
				{
					targetSummon
				};
			}
		}
		return EMPTY_TARGET_LIST;
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.ENEMY_SUMMON;
	}
}
