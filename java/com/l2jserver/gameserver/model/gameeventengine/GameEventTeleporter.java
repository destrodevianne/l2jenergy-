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

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.DuelState;
import com.l2jserver.gameserver.enums.events.Team;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class GameEventTeleporter implements Runnable
{
	private L2PcInstance _playerInstance = null;
	private final GameEvent _event;
	private int[] _coordinates = new int[3];
	private final boolean _adminRemove = false;
	
	public GameEventTeleporter(L2PcInstance playerInstance, int[] coordinates, boolean fastSchedule, boolean adminRemove)
	{
		_playerInstance = playerInstance;
		_event = _playerInstance.getEvent();
		_coordinates = coordinates;
		long delay = (_event.isStarted() ? _event.getConfigs().EVENT_RESPAWN_TELEPORT_DELAY : _event.getConfigs().EVENT_START_LEAVE_TELEPORT_DELAY) * 1000;
		ThreadPoolManager.getInstance().scheduleGeneral(this, fastSchedule ? 0 : delay);
	}
	
	@Override
	public void run()
	{
		if ((_playerInstance == null) || (_event == null))
		{
			return;
		}
		
		L2Summon summon = _playerInstance.getSummon();
		
		if (summon != null)
		{
			summon.unSummon(_playerInstance);
		}
		
		if ((_event.getConfigs().EVENT_EFFECTS_REMOVAL == 0) || ((_event.getConfigs().EVENT_EFFECTS_REMOVAL == 1) && ((_playerInstance.getTeam() == Team.NONE) || (_playerInstance.isInDuel() && (_playerInstance.getDuelState() != DuelState.INTERRUPTED)))))
		{
			_playerInstance.stopAllEffectsExceptThoseThatLastThroughDeath();
		}
		
		if (_playerInstance.isInParty())
		{
			L2Party party = _playerInstance.getParty();
			party.removePartyMember(_playerInstance, null);
		}
		
		if (_playerInstance.isInDuel())
		{
			_playerInstance.setDuelState(DuelState.INTERRUPTED);
		}
		
		if (_playerInstance.getInstanceId() != 0)
		{
			_playerInstance.setInstanceId(0);
		}
		
		if (_event.getEventInstance() != 0)
		{
			if (_event.isStarted() && !_adminRemove)
			{
				_playerInstance.setInstanceId(_event.getEventInstance());
			}
			else
			{
				_playerInstance.setInstanceId(0);
			}
		}
		else
		{
			_playerInstance.setInstanceId(0);
		}
		
		_playerInstance.doRevive();
		
		_playerInstance.teleToLocation((_coordinates[0] + Rnd.get(101)) - 50, (_coordinates[1] + Rnd.get(101)) - 50, _coordinates[2], false);
		
		if (_event.isStarted() && !_adminRemove)
		{
			if (_event instanceof TeamGameEvent)
			{
				TeamGameEvent teamGameEvent = (TeamGameEvent) _event;
				
				GameEventTeam team = teamGameEvent.getParticipantTeam(_playerInstance.getObjectId());
				
				if (team != null)
				{
					if (teamGameEvent.getConfigs().EVENT_TEAM_ID_NAME_COORDINATES.size() <= 2)
					{
						_playerInstance.setTeam(team.getId() == 1 ? Team.BLUE : Team.RED);
					}
				}
			}
			else if (_event instanceof IndividualGameEvent)
			{
				_playerInstance.setTeam(Team.BLUE);
			}
			_playerInstance.setCanRevive(false);
		}
		else
		{
			if (_event instanceof TeamGameEvent)
			{
				TeamGameEvent teamGameEvent = (TeamGameEvent) _event;
				
				if (teamGameEvent.getConfigs().EVENT_TEAM_ID_NAME_COORDINATES.size() <= 2)
				{
					_playerInstance.setTeam(Team.NONE);
				}
			}
			else if (_event instanceof IndividualGameEvent)
			{
				_playerInstance.setTeam(Team.NONE);
			}
			_playerInstance.setCanRevive(true);
			
			final L2PcInstance player = L2World.getInstance().getPlayer(_playerInstance.getObjectId());
			if (player != null)
			{
				player.removeEventListener(GameEventListener.class);
			}
			
			_playerInstance.setEvent(null); // TODO:
		}
		_playerInstance.setCurrentCp(_playerInstance.getMaxCp());
		_playerInstance.setCurrentHp(_playerInstance.getMaxHp());
		_playerInstance.setCurrentMp(_playerInstance.getMaxMp());
		
		_playerInstance.broadcastStatusUpdate();
		_playerInstance.broadcastUserInfo();
	}
}