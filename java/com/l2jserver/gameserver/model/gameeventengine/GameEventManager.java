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
package com.l2jserver.gameserver.model.gameeventengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.gameeventengine.TvT.TvTEvent;
import com.l2jserver.gameserver.model.gameeventengine.TvT.TvTManager;
import com.l2jserver.gameserver.model.skills.Skill;

public class GameEventManager
{
	protected static final Logger LOG = LoggerFactory.getLogger(GameEventManager.class);
	
	public static void getEventsInstances()
	{
		TvTManager.getInstance();
	}
	
	public static void onKill(L2Character killerCharacter, L2PcInstance killedPlayerInstance)
	{
		TvTEvent.onKill(killerCharacter, killedPlayerInstance);
	}
	
	public static boolean onAction(L2PcInstance activeChar, int objectId)
	{
		if (!TvTEvent.onAction(activeChar, objectId))
		{
			return false;
		}
		return true;
	}
	
	public static void onTeleported(L2PcInstance player)
	{
		TvTEvent.onTeleported(player);
	}
	
	public static void onLogin(L2PcInstance player)
	{
		TvTEvent.onLogin(player);
	}
	
	public static void onLogout(L2PcInstance player)
	{
		// TvT Event removal
		try
		{
			TvTEvent.onLogout(player);
		}
		catch (Exception e)
		{
			LOG.error("deleteMe()", e);
		}
	}
	
	public static boolean calculateDeathPenaltyBuff(L2PcInstance player)
	{
		if (TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(player.getObjectId()))
		{
			return true;
		}
		return false;
	}
	
	// TODO fix
	public static boolean onEscapeUse(int playerObjectId)
	{
		if (!TvTEvent.onEscapeUse(playerObjectId))
		{
			// player.sendPacket(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
			return false;
		}
		return true;
	}
	
	public static boolean eventTarget(L2PcInstance _owner, L2Character _target)
	{
		L2Object ownerTarget = _owner.getTarget();
		if (ownerTarget == null)
		{
			return true;
		}
		
		// TvT event targeting
		if (TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(_owner.getObjectId()))
		{
			GameEventTeam enemyTeam = TvTEvent.getParticipantEnemyTeam(_owner.getObjectId());
			
			if (ownerTarget.getActingPlayer() != null)
			{
				L2PcInstance target = ownerTarget.getActingPlayer();
				if (enemyTeam.containsPlayer(target.getObjectId()) && !target.isDead())
				{
					_target = (L2Character) ownerTarget;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean isStarted()
	{
		if (TvTEvent.isStarted())
		{
			return true;
		}
		return false;
	}
	
	public static boolean isInactive()
	{
		if (TvTEvent.isInactive())
		{
			return true;
		}
		return false;
	}
	
	public static boolean isPlayerParticipant(int playerObjectId)
	{
		if (TvTEvent.isPlayerParticipant(playerObjectId))
		{
			return true;
		}
		return false;
	}
	
	public static boolean removeParticipant(int playerObjectId)
	{
		if (TvTEvent.removeParticipant(playerObjectId))
		{
			return true;
		}
		return false;
	}
	
	public static boolean checkForEventSkill(L2PcInstance player, L2PcInstance targetPlayer, Skill skill)
	{
		if (!TvTEvent.checkForTvTSkill(player, targetPlayer, skill))
		{
			return false;
		}
		return true;
	}
	
	public static boolean onItemSummon(int playerObjectId)
	{
		if (!TvTEvent.onItemSummon(playerObjectId))
		{
			return false;
		}
		return true;
	}
	
	public static boolean onScrollUse(int playerObjectId)
	{
		if (!TvTEvent.onScrollUse(playerObjectId))
		{
			return false;
		}
		return true;
	}
	
	public static boolean onRequestUnEquipItem(L2PcInstance player)
	{
		return false;
	}
	
	public static boolean onUseItem(L2PcInstance player)
	{
		// Dont allow weapon/shielf equipment if a ctf flag is equipped
		return false;
	}
}
