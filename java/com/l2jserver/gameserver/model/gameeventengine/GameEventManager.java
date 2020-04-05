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

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Broadcast;

public class GameEventManager
{
	protected static final Logger LOG = LoggerFactory.getLogger(GameEventManager.class);
	
	private final Map<String, GameEvent> _engines = new HashMap<>();
	private GameEventStartTask _task;
	private String _currentEventName = "-";
	private String _nextEventInfo = "-";
	private GameEvent _event = null;
	
	protected GameEventManager()
	{
		
	}
	
	public void registerEventEngine(GameEvent gameEvent)
	{
		_engines.put(gameEvent.getEventName(), gameEvent);
		LOG.info("GameEventEngine - Event registered: {}", gameEvent.getEventName());
		scheduleNextEvent();
	}
	
	public GameEvent getEvent()
	{
		return _event;
	}
	
	public GameEvent getEventEngine(String engineName)
	{
		return _engines.get(engineName);
	}
	
	public Collection<GameEvent> getAllEvents()
	{
		return _engines.values();
	}
	
	public GameEvent participantOf(L2PcInstance player)
	{
		for (GameEvent evt : getAllEvents())
		{
			if (evt.addParticipant(player))
			{
				return evt;
			}
		}
		return null;
	}
	
	public void startReg() // startRegistration
	{
		_currentEventName = getEvent().getEventName();
		_nextEventInfo = "-";
		getEvent().startParticipation();
		Broadcast.toAllOnlinePlayers(getEvent().getEventName() + " Event: Registration opened for " + getEvent().getConfigs().EVENT_PARTICIPATION_TIME + " minute(s).", true);
		_task.setStartTime(System.currentTimeMillis() + (60000L * getEvent().getConfigs().EVENT_PARTICIPATION_TIME));
		ThreadPoolManager.getInstance().executeGeneral(_task);
		LOG.info("GameEventEngine - Registration period started for event {}!", getEvent().getEventName());
	}
	
	public void startEvent()
	{
		if (!getEvent().startFight())
		{
			Broadcast.toAllOnlinePlayers(getEvent().getEventName() + " Event: Event cancelled due to lack of Participation.", true);
			scheduleNextEvent();
			_currentEventName = "-";
		}
		else
		{
			getEvent().sysMsgToAllParticipants(getEvent().getEventName() + " Event: Teleporting participants to an arena in " + getEvent().getConfigs().EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
			_task.setStartTime(System.currentTimeMillis() + (60000L * getEvent().getConfigs().EVENT_RUNNING_TIME));
			ThreadPoolManager.getInstance().executeGeneral(_task);
			LOG.info("GameEventEngine - Registration to event {} is over! Event is starting in {} seconds!", getEvent().getEventName(), getEvent().getConfigs().EVENT_START_LEAVE_TELEPORT_DELAY);
		}
	}
	
	public void endEvent()
	{
		Broadcast.toAllOnlinePlayers(getEvent().getEventName() + " Event: " + getEvent().calculateRewards(), true);
		getEvent().sysMsgToAllParticipants(getEvent().getEventName() + " Event: Teleporting back in " + getEvent().getConfigs().EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		getEvent().stopFight();
		scheduleNextEvent();
		_currentEventName = "-";
	}
	
	public Calendar nextEventStartTime(GameEvent gameEvent)
	{
		Calendar currentTime = Calendar.getInstance();
		Calendar nextStartTime = null;
		Calendar testStartTime = null;
		
		if (gameEvent.getConfigs().EVENT_TIME_INTERVAL != null)
		{
			for (String timeOfDay : gameEvent.getConfigs().EVENT_TIME_INTERVAL)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
		}
		return nextStartTime;
	}
	
	public void scheduleNextEvent()
	{
		if ((_engines != null) && !_engines.isEmpty())
		{
			Calendar tempNextStartTime = null;
			Calendar nextStartTime = null;
			
			for (GameEvent gameEvent : _engines.values())
			{
				tempNextStartTime = nextEventStartTime(gameEvent);
				
				if (tempNextStartTime == null)
				{
					continue;
				}
				
				if (nextStartTime == null)
				{
					nextStartTime = tempNextStartTime;
					_event = gameEvent;
				}
				else
				{
					if (tempNextStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
					{
						nextStartTime = tempNextStartTime;
						_event = gameEvent;
					}
				}
			}
			
			if (nextStartTime != null)
			{
				_nextEventInfo = getEvent().getEventName() + " in " + ((nextStartTime.getTimeInMillis() - System.currentTimeMillis()) / 60000L) + " minutes!";
				_task = new GameEventStartTask(nextStartTime.getTimeInMillis());
				ThreadPoolManager.getInstance().executeGeneral(_task);
				LOG.info("GameEventEngine - Next Event: {}", _nextEventInfo);
			}
		}
	}
	
	public void skipDelay()
	{
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPoolManager.getInstance().executeGeneral(_task);
		}
	}
	
	public String getCurrentEventName()
	{
		return _currentEventName;
	}
	
	public String getNextEventInfo()
	{
		return _nextEventInfo;
	}
	
	class GameEventStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		
		public GameEventStartTask(long startTime)
		{
			_startTime = startTime;
		}
		
		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}
		
		@Override
		public void run()
		{
			int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);
			
			if (delay > 0)
			{
				announce(delay);
			}
			
			int nextMsg = 0;
			if (delay > 3600)
			{
				nextMsg = delay - 3600;
			}
			else if (delay > 1800)
			{
				nextMsg = delay - 1800;
			}
			else if (delay > 900)
			{
				nextMsg = delay - 900;
			}
			else if (delay > 600)
			{
				nextMsg = delay - 600;
			}
			else if (delay > 300)
			{
				nextMsg = delay - 300;
			}
			else if (delay > 60)
			{
				nextMsg = delay - 60;
			}
			else if (delay > 5)
			{
				nextMsg = delay - 5;
			}
			else if (delay > 0)
			{
				nextMsg = delay;
			}
			else
			{
				if (getEvent().isInactive())
				{
					startReg();
				}
				else if (getEvent().isParticipating())
				{
					startEvent();
				}
				else
				{
					endEvent();
				}
			}
			
			if (delay > 0)
			{
				nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
			}
		}
		
		private void announce(long time)
		{
			if ((time >= 3600) && ((time % 3600) == 0))
			{
				if (getEvent().isParticipating())
				{
					Broadcast.toAllOnlinePlayers(getEvent().getEventName() + " Event: " + (time / 60 / 60) + " hour(s) until registration is closed!", true);
				}
				else if (getEvent().isStarted())
				{
					getEvent().sysMsgToAllParticipants(getEvent().getEventName() + " Event: " + (time / 60 / 60) + " hour(s) until event is finished!");
				}
			}
			else if (time >= 60)
			{
				if (getEvent().isParticipating())
				{
					Broadcast.toAllOnlinePlayers(getEvent().getEventName() + " Event: " + (time / 60) + " minute(s) until registration is closed!", true);
				}
				else if (getEvent().isStarted())
				{
					getEvent().sysMsgToAllParticipants(getEvent().getEventName() + " Event: " + (time / 60) + " minute(s) until the event is finished!");
				}
			}
			else
			{
				if (getEvent().isParticipating())
				{
					Broadcast.toAllOnlinePlayers(getEvent().getEventName() + " Event: " + time + " second(s) until registration is closed!", true);
				}
				else if (getEvent().isStarted())
				{
					getEvent().sysMsgToAllParticipants(getEvent().getEventName() + " Event: " + time + " second(s) until the event is finished!");
				}
			}
		}
	}
	
	// TODO: доработать когда-то))
	public void useAdminCommand(L2PcInstance player, String command)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage();
		
		StringBuilder replyMSG = new StringBuilder("<html><title>Event Engines</title><body>");
		
		replyMSG.append("<table width=240 bgcolor=\"444444\"><tr>");
		replyMSG.append("<td><button value=\"Main\" action=\"bypass -h admin_admin\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Game\" action=\"bypass -h admin_admin2\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Effects\" action=\"bypass -h admin_admin3\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Events\" action=\"bypass -h admin_html events.htm\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td></td></tr><tr>");
		replyMSG.append("<td><button value=\"GM\" action=\"bypass -h admin_admin7\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Server\" action=\"bypass -h admin_admin4\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Char\" action=\"bypass -h admin_admin6\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Config\" action=\"bypass -h admin_config_server\" width=65 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td></td><td></td><td></td></tr></table><br>");
		replyMSG.append("<table width=\"292\">");
		replyMSG.append("<tr><td>Event Engines</td><td>Instances</td></tr>");
		for (GameEvent gameEvent : _engines.values())
		{
			replyMSG.append("<tr><td><button value=\"" + gameEvent.getEventName() + "\" action=\"bypass -h " + gameEvent.getCommand() + "\" width=190 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		}
		replyMSG.append("</table><br>");
		replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		player.sendPacket(adminReply);
	}
	
	public static GameEventManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GameEventManager INSTANCE = new GameEventManager();
	}
}
