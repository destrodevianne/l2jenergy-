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

import java.util.Map;
import java.util.TreeSet;

import com.l2jserver.gameserver.enums.events.MessageType;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.events.OnEventStart;
import com.l2jserver.gameserver.util.EventUtil;

public abstract class IndividualGameEvent extends GameEvent
{
	protected Map<Integer, L2PcInstance> _participantPlayers;
	
	@Override
	public abstract IndividualGameEventConfig getConfigs();
	
	@Override
	public boolean isParticipant(int eventPlayerObjectId)
	{
		return _participantPlayers.containsKey(eventPlayerObjectId);
	}
	
	@Override
	public int getCountPlayers()
	{
		return _participantPlayers.size();
	}
	
	@Override
	public boolean addParticipant(L2PcInstance playerInstance)
	{
		if (_participantPlayers.containsKey(playerInstance.getObjectId()))
		{
			return false;
		}
		_participantPlayers.put(playerInstance.getObjectId(), playerInstance);
		playerInstance.setEvent(this); // TODO
		playerInstance.addEventListener(new GameEventListener(playerInstance));
		return true;
	}
	
	@Override
	public boolean removeParticipant(L2PcInstance playerInstance)
	{
		if (!_participantPlayers.containsKey(playerInstance.getObjectId()))
		{
			return false;
		}
		
		_participantPlayers.remove(playerInstance.getObjectId());
		playerInstance.setEvent(null); // TODO
		
		final L2PcInstance player = L2World.getInstance().getPlayer(playerInstance.getObjectId());
		if (player != null)
		{
			player.removeEventListener(GameEventListener.class);
		}
		return true;
	}
	
	@Override
	public void teleportPlayersToArena()
	{
		for (L2PcInstance playerInstance : _participantPlayers.values())
		{
			if (playerInstance != null)
			{
				EventUtil.showEventMessage(playerInstance, "status_started", MessageType.START);
				new GameEventTeleporter(playerInstance, getConfigs().EVENT_COORDINATES, false, false);
			}
		}
		// Notify to scripts.
		EventDispatcher.getInstance().notifyEventAsync(new OnEventStart());
	}
	
	@Override
	public void teleportPlayersBack()
	{
		for (L2PcInstance playerInstance : _participantPlayers.values())
		{
			if (playerInstance != null)
			{
				EventUtil.showEventMessage(playerInstance, "status_finished", MessageType.FINISH);
				new GameEventTeleporter(playerInstance, getConfigs().EVENT_PARTICIPATION_COORDINATES, false, false);
			}
		}
	}
	
	@Override
	public void sysMsgToAllParticipants(String message)
	{
		for (L2PcInstance playerInstance : _participantPlayers.values())
		{
			if (playerInstance != null)
			{
				playerInstance.sendMessage(message);
			}
		}
	}
	
	public String[] getFirstPosition(int countPos)
	{
		TreeSet<L2PcInstance> players = new TreeSet<>();
		players.addAll(_participantPlayers.values());
		
		String text = "";
		
		for (int i = 0; i < countPos; i++)
		{
			if (players.isEmpty())
			{
				break;
			}
			
			L2PcInstance player = players.first();
			
			if (player.getKills() == 0)
			{
				break;
			}
			
			text += player.getName() + "," + String.valueOf(player.getKills()) + ";";
			players.remove(player);
			
			int playerPointPrev = player.getKills();
			
			if (!getConfigs().EVENT_REWARD_TEAM_TIE)
			{
				continue;
			}
			
			while (!players.isEmpty())
			{
				player = players.first();
				if (player.getKills() != playerPointPrev)
				{
					break;
				}
				text += player.getName() + "," + String.valueOf(player.getKills()) + ";";
				players.remove(player);
			}
		}
		
		if (text != "")
		{
			return text.split("\\;");
		}
		
		return null;
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
		
		if (!isParticipant(playerInstance.getObjectId()) && isParticipant(targetedPlayerObjectId))
		{
			return false;
		}
		
		if (isParticipant(playerInstance.getObjectId()) && !isParticipant(targetedPlayerObjectId))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onLogin(L2PcInstance playerInstance)
	{
		if ((playerInstance == null) || (!isStarted()))
		{
			return;
		}
		
		if (isParticipant(playerInstance.getObjectId()))
		{
			L2PcInstance eventPlayer = _participantPlayers.get(playerInstance.getObjectId());
			
			if (eventPlayer == null)
			{
				return;
			}
			
			new GameEventTeleporter(eventPlayer, getConfigs().EVENT_COORDINATES, true, true);
		}
	}
	
	@Override
	protected void clear()
	{
		_participantPlayers.clear();
	}
}
