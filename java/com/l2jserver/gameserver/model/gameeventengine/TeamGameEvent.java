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

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.enums.events.GameEventState;
import com.l2jserver.gameserver.enums.events.MessageType;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.events.OnEventFinish;
import com.l2jserver.gameserver.model.events.impl.events.OnEventStart;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.ExBlockUpSetList;
import com.l2jserver.gameserver.network.serverpackets.ExBlockUpSetState;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.util.EventUtil;

public abstract class TeamGameEvent extends GameEvent
{
	protected GameEventTeam[] _teams;
	protected short _rounds = 1;
	
	@Override
	public abstract TeamGameEventConfig getConfigs();
	
	@Override
	public boolean isParticipant(int eventPlayerObjectId)
	{
		boolean isParticipant = false;
		for (GameEventTeam team : _teams)
		{
			if (team.containsPlayer(eventPlayerObjectId))
			{
				isParticipant = true;
			}
		}
		return isParticipant;
	}
	
	@Override
	public int getCountPlayers()
	{
		int playersCount = 0;
		
		for (GameEventTeam team : _teams)
		{
			playersCount += team.getParticipatedPlayerCount();
		}
		
		return playersCount;
	}
	
	public GameEventTeam[] getTeams()
	{
		return _teams;
	}
	
	private GameEventTeam getTeamWithMinPlayers()
	{
		GameEventTeam teamWithMinPlayers = null;
		int playersCount = getConfigs().EVENT_MAX_PLAYERS_IN_TEAMS;
		
		for (GameEventTeam team : _teams)
		{
			if (playersCount > team.getParticipatedPlayerCount())
			{
				playersCount = team.getParticipatedPlayerCount();
				teamWithMinPlayers = team;
			}
		}
		return teamWithMinPlayers;
	}
	
	public GameEventTeam getParticipantTeam(int playerObjectId)
	{
		GameEventTeam participantTeam = null;
		
		for (GameEventTeam team : _teams)
		{
			if (team.containsPlayer(playerObjectId))
			{
				participantTeam = team;
			}
		}
		return participantTeam;
	}
	
	public byte getParticipantTeamId(int playerObjectId)
	{
		return (byte) (_teams[0].containsPlayer(playerObjectId) ? 0 : _teams[1].containsPlayer(playerObjectId) ? 1 : -1);
	}
	
	@Override
	public boolean addParticipant(L2PcInstance eventPlayer)
	{
		if (getTeamWithMinPlayers().addPlayer(eventPlayer))
		{
			eventPlayer.setEvent(this); // TODO:
			eventPlayer.addEventListener(new GameEventListener(eventPlayer));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeParticipant(L2PcInstance playerInstance)
	{
		GameEventTeam playerTeam = getParticipantTeam(playerInstance.getObjectId());
		
		if (playerTeam != null)
		{
			playerTeam.removePlayer(playerInstance.getObjectId());
			playerInstance.setEvent(null); // TODO:
			final L2PcInstance player = L2World.getInstance().getPlayer(playerInstance.getObjectId());
			if (player != null)
			{
				player.removeEventListener(GameEventListener.class);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void teleportPlayersToArena()
	{
		for (GameEventTeam team : _teams)
		{
			team.getParticipatedPlayers().values().stream().filter(playerInstance -> playerInstance != null).forEach(playerInstance ->
			{
				// Disabled skills by config.
				if (!getConfigs().EVENT_RESTRICT_SKILLS.isEmpty())
				{
					for (SkillHolder skill : getConfigs().EVENT_RESTRICT_SKILLS)
					{
						if (playerInstance.getSkills().values().contains(skill.getSkill()))
						{
							playerInstance.disableSkill(skill.getSkill(), ((getConfigs().EVENT_RUNNING_TIME * 60) + getConfigs().EVENT_RESPAWN_TELEPORT_DELAY) * 1000);
						}
					}
				}
				// Teleporter implements Runnable and starts itself
				new GameEventTeleporter(playerInstance, team.getCoordinates(), false, false);
				EventUtil.showEventMessage(playerInstance, "status_started", MessageType.START);
				playerInstance.setKills(0);
				broadcastPacketToTeams(new ExBlockUpSetList(playerInstance, getParticipantTeamId(playerInstance.getObjectId()) == 1, false));
			});
			
		}
		// Notify to scripts.
		EventDispatcher.getInstance().notifyEventAsync(new OnEventStart());
	}
	
	@Override
	public void teleportPlayersBack()
	{
		for (GameEventTeam team : _teams)
		{
			// Check for nullpointer
			team.getParticipatedPlayers().values().stream().filter(playerInstance -> playerInstance != null).forEach(playerInstance ->
			{
				EventUtil.showEventMessage(playerInstance, "status_finished", MessageType.FINISH);
				playerInstance.setKills(0);
				playerInstance.sendPacket(new ExBlockUpSetState(_teams[0].getPoints() < _teams[1].getPoints()));
				broadcastPacketToTeams(new ExBlockUpSetList(playerInstance, getParticipantTeamId(playerInstance.getObjectId()) == 1, true));
				new GameEventTeleporter(playerInstance, getConfigs().EVENT_PARTICIPATION_COORDINATES, false, false);
			});
		}
	}
	
	@Override
	public void broadcastPacketToTeams(L2GameServerPacket... packets)
	{
		for (GameEventTeam team : _teams)
		{
			team.getParticipatedPlayers().values().stream().filter(playerInstance -> playerInstance != null).forEach(playerInstance ->
			{
				for (L2GameServerPacket packet : packets)
				{
					playerInstance.sendPacket(packet);
				}
			});
		}
	}
	
	@Override
	public String calculateRewards()
	{
		List<GameEventTeam> winners = new ArrayList<>(getConfigs().EVENT_TEAM_ID_NAME_COORDINATES.size());
		GameEventTeam winnerTeam = null;
		short maxPoints = 0;
		String msg = "";
		
		for (GameEventTeam team : _teams)
		{
			if ((maxPoints == 0) && (team.getPoints() > 0))
			{
				maxPoints = team.getPoints();
				winnerTeam = team;
			}
			else if ((team.getPoints() == maxPoints) && (team.getPoints() > 0))
			{
				maxPoints = team.getPoints();
				winners.add(team);
				winners.add(winnerTeam);
				winnerTeam = null;
			}
			else if ((team.getPoints() > maxPoints))
			{
				maxPoints = team.getPoints();
				winnerTeam = team;
				winners.clear();
			}
		}
		
		setState(GameEventState.REWARDING);
		
		if (winnerTeam != null)
		{
			rewardTeam(winnerTeam);
			msg = ": Event finish. Team " + winnerTeam.getName() + " won with " + winnerTeam.getPoints() + " kills.";
		}
		else if (winners.size() >= 2)
		{
			if (getConfigs().EVENT_REWARD_TEAM_TIE)
			{
				for (GameEventTeam team : winners)
				{
					rewardTeam(team);
				}
			}
			msg = ": Event has ended with both teams tying!";
		}
		else if (winners.isEmpty())
		{
			msg = ": Event has ended with both teams tying without any points!";
		}
		
		winners.clear();
		
		// Notify to scripts.
		EventDispatcher.getInstance().notifyEventAsync(new OnEventFinish());
		return msg;
	}
	
	public void rewardTeam(GameEventTeam team)
	{
		for (L2PcInstance playerInstance : team.getParticipatedPlayers().values())
		{
			if (playerInstance == null)
			{
				continue;
			}
			
			if (getConfigs().EVENT_REQUIRE_MIN_FRAGS_TO_REWARD && (playerInstance.getKills() < getConfigs().EVENT_MIN_FRAGS_TO_REWARD))
			{
				playerInstance.sendMessage("You did not killed enough players at the event to receive the reward!");
				continue;
			}
			
			if (getConfigs().EVENT_REWARDS != null)
			{
				deliverRewards(playerInstance, getConfigs().EVENT_REWARDS);
			}
		}
	}
	
	@Override
	public void sysMsgToAllParticipants(String message)
	{
		_teams[0].getParticipatedPlayers().values().stream().filter(playerInstance -> playerInstance != null).forEach(playerInstance -> playerInstance.sendMessage(message));
		
		_teams[1].getParticipatedPlayers().values().stream().filter(playerInstance -> playerInstance != null).forEach(playerInstance -> playerInstance.sendMessage(message));
	}
	
	@Override
	public boolean onAction(L2PcInstance playerInstance, int targetedPlayerObjectId)
	{
		if ((playerInstance == null) || !isStarted())
		{
			return true;
		}
		
		if (playerInstance.isGM())
		{
			return true;
		}
		
		GameEventTeam playerTeam = getParticipantTeam(playerInstance.getObjectId());
		GameEventTeam targetedPlayerTeam = getParticipantTeam(targetedPlayerObjectId);
		
		if (((playerTeam != null) && (targetedPlayerTeam == null)) || ((playerTeam == null) && (targetedPlayerTeam != null)))
		{
			return false;
		}
		
		if ((playerTeam != null) && (targetedPlayerTeam != null) && (playerTeam == targetedPlayerTeam) && (playerInstance.getObjectId() != targetedPlayerObjectId) && !getConfigs().EVENT_TARGET_TEAM_MEMBERS_ALLOWED)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public boolean eventTarget(L2PcInstance owner, L2Character _target)
	{
		L2Object ownerTarget = owner.getTarget();
		if (ownerTarget == null)
		{
			return true;
		}
		
		if (isStarted() && isParticipant(owner.getObjectId()))
		{
			GameEventTeam enemyTeam = getParticipantTeam(owner.getObjectId());
			
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
	
	@Override
	public final boolean checkForEventsSkill(L2PcInstance source, L2PcInstance target, Skill skill)
	{
		if (!isStarted())
		{
			return true;
		}
		// TvT is started
		final int sourcePlayerId = source.getObjectId();
		final int targetPlayerId = target.getObjectId();
		final boolean isSourceParticipant = isParticipant(sourcePlayerId);
		final boolean isTargetParticipant = isParticipant(targetPlayerId);
		
		// both players not participating
		if (!isSourceParticipant && !isTargetParticipant)
		{
			return true;
		}
		// one player not participating
		if (!(isSourceParticipant && isTargetParticipant))
		{
			return false;
		}
		// players in the different teams ?
		if (getParticipantTeam(sourcePlayerId) != getParticipantTeam(targetPlayerId))
		{
			if (!skill.isBad())
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onLogin(L2PcInstance playerInstance)
	{
		if ((playerInstance == null) || (!isStarting() && !isStarted()))
		{
			return;
		}
		
		byte teamId = getParticipantTeamId(playerInstance.getObjectId());
		
		if (teamId == -1)
		{
			return;
		}
		
		_teams[teamId].addPlayer(playerInstance);
		new GameEventTeleporter(playerInstance, _teams[teamId].getCoordinates(), true, false);
	}
	
	@Override
	protected void clear()
	{
		for (GameEventTeam team : _teams)
		{
			team.cleanMe();
		}
	}
}
