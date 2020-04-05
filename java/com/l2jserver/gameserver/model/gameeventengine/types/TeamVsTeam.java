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
package com.l2jserver.gameserver.model.gameeventengine.types;

import java.util.List;

import com.l2jserver.gameserver.configuration.config.events.TvTConfig;
import com.l2jserver.gameserver.enums.events.GameEventState;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.events.OnEventKill;
import com.l2jserver.gameserver.model.gameeventengine.GameEventManager;
import com.l2jserver.gameserver.model.gameeventengine.GameEventTeam;
import com.l2jserver.gameserver.model.gameeventengine.GameEventTeleporter;
import com.l2jserver.gameserver.model.gameeventengine.TeamGameEvent;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExBlockUpSetState;

public class TeamVsTeam extends TeamGameEvent
{
	@Override
	public TvTConfig getConfigs()
	{
		return TvTConfig.getInstance();
	}
	
	@Override
	public void init()
	{
		setEventName("TeamVsTeam");
		
		List<GameEventTeam> teamsConfig = getConfigs().EVENT_TEAM_ID_NAME_COORDINATES;
		_teams = new GameEventTeam[teamsConfig.size()];
		for (int i = 0; i < teamsConfig.size(); i++)
		{
			_teams[i] = new GameEventTeam(teamsConfig.get(i).getId(), teamsConfig.get(i).getName(), teamsConfig.get(i).getCoordinates());
		}
		
		setState(GameEventState.INACTIVE);
		GameEventManager.getInstance().registerEventEngine(getInstance());
	}
	
	@Override
	public void onKill(L2Character killerCharacter, L2PcInstance killedPlayerInstance)
	{
		if ((killedPlayerInstance == null) || !isStarted())
		{
			return;
		}
		
		byte killedTeamId = getParticipantTeamId(killedPlayerInstance.getObjectId());
		
		if (killedTeamId == -1)
		{
			return;
		}
		
		new GameEventTeleporter(killedPlayerInstance, _teams[killedTeamId].getCoordinates(), false, false);
		
		if (killerCharacter == null)
		{
			return;
		}
		
		L2PcInstance killerPlayerInstance = null;
		
		if (killerCharacter.isSummon())
		{
			killerPlayerInstance = ((L2Summon) killerCharacter).getOwner();
			
			if (killerPlayerInstance == null)
			{
				return;
			}
		}
		else if (killerCharacter.isPlayer())
		{
			killerPlayerInstance = (L2PcInstance) killerCharacter;
		}
		else
		{
			return;
		}
		
		byte killerTeamId = getParticipantTeamId(killerPlayerInstance.getObjectId());
		
		if ((killerTeamId != -1) && (killedTeamId != -1) && (killerTeamId != killedTeamId))
		{
			GameEventTeam killerTeam = _teams[killerTeamId];
			
			killerTeam.increasePoints();
			
			CreatureSay cs = new CreatureSay(killerPlayerInstance.getObjectId(), Say2.TELL, killerPlayerInstance.getName(), "I have killed " + killedPlayerInstance.getName() + "!");
			
			for (L2PcInstance playerInstance : _teams[killerTeamId].getParticipatedPlayers().values())
			{
				if (playerInstance != null)
				{
					playerInstance.sendPacket(cs);
				}
			}
			
			// Notify to scripts.
			EventDispatcher.getInstance().notifyEventAsync(new OnEventKill(killerPlayerInstance, killedPlayerInstance, killerTeam));
		}
		
		try
		{
			int timeLeft = (int) ((_startedTime - System.currentTimeMillis()) / 1000);
			int[] points = new int[2];
			points[0] = _teams[0].getPoints();
			points[1] = _teams[1].getPoints();
			
			broadcastPacketToTeams(new ExBlockUpSetState(timeLeft, points[0], points[1]), new ExBlockUpSetState(timeLeft, points[0], points[1], getParticipantTeamId(killerPlayerInstance.getObjectId()) == 1, killerPlayerInstance, killerPlayerInstance.getKills()));
		}
		catch (Exception e)
		{
			LOG.error("", e);
		}
		
		if (getConfigs().EVENT_ENABLED_KILL_BONUS)
		{
			giveKillBonus(killerPlayerInstance);
		}
	}
	
	public static TeamVsTeam getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TeamVsTeam INSTANCE = new TeamVsTeam();
	}
}
