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

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.events.GameEventState;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.events.OnEventRegistrationStart;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExBlockUpSetState;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.EventUtil;

public abstract class GameEvent
{
	protected static final Logger LOG = LoggerFactory.getLogger(GameEvent.class);
	
	private GameEventState _state = GameEventState.INACTIVE;
	
	private String _command;
	private String _eventName = "";
	private int _eventInstance = 0;
	protected static long _startedTime;
	
	public abstract GameEventConfig getConfigs();
	
	public abstract void init();
	
	protected abstract String calculateRewards();
	
	protected abstract void teleportPlayersToArena();
	
	protected abstract void broadcastPacketToTeams(L2GameServerPacket... packets);
	
	protected abstract void teleportPlayersBack();
	
	protected abstract void clear();
	
	public abstract boolean isParticipant(int eventPlayerObjectId);
	
	public abstract int getCountPlayers();
	
	public abstract boolean addParticipant(L2PcInstance playerInstance);
	
	public abstract boolean removeParticipant(L2PcInstance playerInstance);
	
	public abstract void sysMsgToAllParticipants(String message);
	
	public abstract void onKill(L2Character killerCharacter, L2PcInstance killedPlayerInstance);
	
	public abstract void onLogin(L2PcInstance playerInstance);
	
	public abstract boolean onAction(L2PcInstance playerInstance, int targetedPlayerObjectId);
	
	public abstract boolean checkForEventsSkill(L2PcInstance source, L2PcInstance target, Skill skill);
	
	public abstract boolean eventTarget(L2PcInstance owner, L2Character _target);
	
	public GameEventState getState()
	{
		synchronized (_state)
		{
			return _state;
		}
	}
	
	protected void setState(GameEventState state)
	{
		synchronized (_state)
		{
			_state = state;
		}
	}
	
	public String getEventName()
	{
		return _eventName;
	}
	
	public void setEventName(String eventName)
	{
		_eventName = eventName;
	}
	
	public boolean isInactive()
	{
		return getState() == GameEventState.INACTIVE;
	}
	
	public boolean isParticipating() // isRegistrating
	{
		return getState() == GameEventState.PARTICIPATING;
	}
	
	public boolean isStarting()
	{
		return getState() == GameEventState.STARTING;
	}
	
	public boolean isStarted() // isRunning
	{
		return getState() == GameEventState.STARTED;
	}
	
	public boolean isRewarding()
	{
		return getState() == GameEventState.REWARDING;
	}
	
	public boolean startParticipation()
	{
		setState(GameEventState.PARTICIPATING);
		EventDispatcher.getInstance().notifyEventAsync(new OnEventRegistrationStart());
		// question();
		return true;
	}
	
	public boolean startFight()
	{
		// Set state to STARTING
		setState(GameEventState.STARTING);
		_startedTime = System.currentTimeMillis() + (getConfigs().EVENT_RUNNING_TIME * 60 * 1000);
		int timeLeft = (int) ((_startedTime - System.currentTimeMillis()) / 1000);
		
		if (getCountPlayers() >= getConfigs().EVENT_MIN_PLAYERS_IN_TEAMS)
		{
			if (getConfigs().EVENT_IN_INSTANCE)
			{
				createInstance();
			}
			closeDoors();
			setState(GameEventState.STARTED);
			teleportPlayersToArena();
			broadcastPacketToTeams(new ExBlockUpSetState(timeLeft, 0, 0));
			return true;
		}
		clear();
		setState(GameEventState.INACTIVE);
		return false;
	}
	
	public void stopFight()
	{
		// Set state INACTIVATING
		setState(GameEventState.INACTIVATING);
		// Opens all doors specified in configs
		openDoors();
		teleportPlayersBack();
		clear();
		setState(GameEventState.INACTIVE);
	}
	
	public boolean register(L2PcInstance playerInstance)
	{
		if (playerInstance == null)
		{
			return false;
		}
		
		if (!EventUtil.checkPlayer(playerInstance, true))
		{
			return false;
		}
		
		if (getConfigs().EVENT_PARTICIPATION_FEE)
		{
			if (playerInstance.destroyItemByItemId("Event Participation Fee", getConfigs().EVENT_TAKE_ITEM_ID, getConfigs().EVENT_TAKE_COUNT, null, true))
			{
				if (addParticipant(playerInstance))
				{
					return true;
				}
			}
		}
		
		if (addParticipant(playerInstance))
		{
			return true;
		}
		return false;
	}
	
	// TODO: fix
	public void question()
	{
		for (L2PcInstance player : L2World.getInstance().getPlayers())
		{
			if (player.getLevel() >= getConfigs().EVENT_MIN_LVL)
			{
				// player.scriptRequest(MessagesData.getInstance().getMessage(player, "event_you_want_participate_event").replace("%s%", getEventName() + ""), "com.l2jserver.gameserver.model.gameeventengine.GameEvent.register");
			}
		}
	}
	
	public boolean unRegister(L2PcInstance playerInstance)
	{
		if (playerInstance == null)
		{
			return false;
		}
		
		if ((getState() == GameEventState.STARTED) || !isParticipant(playerInstance.getObjectId()))
		{
			playerInstance.sendMessage("You can't cancel your registration to the event at this moment!");
			return false;
		}
		
		if (removeParticipant(playerInstance))
		{
			playerInstance.sendMessage("Your registration at the " + _eventName + " Event was cancelled!");
			return true;
		}
		return false;
	}
	
	protected void closeDoors()
	{
		Collection<L2DoorInstance> doorsToClose = InstanceManager.getInstance().getInstance(_eventInstance).getDoors();
		
		for (L2DoorInstance door : doorsToClose)
		{
			if (door != null)
			{
				door.closeMe();
			}
		}
	}
	
	private void openDoors()
	{
		Collection<L2DoorInstance> doorsToOpen = InstanceManager.getInstance().getInstance(_eventInstance).getDoors();
		
		for (L2DoorInstance door : doorsToOpen)
		{
			if (door != null)
			{
				door.openMe();
			}
		}
	}
	
	protected void createInstance()
	{
		try
		{
			_eventInstance = InstanceManager.getInstance().createDynamicInstance(getConfigs().EVENT_INSTANCE_FILE);
			InstanceManager.getInstance().getInstance(_eventInstance).setAllowSummon(false);
			InstanceManager.getInstance().getInstance(_eventInstance).setPvPInstance(true);
			InstanceManager.getInstance().getInstance(_eventInstance).setEmptyDestroyTime((getConfigs().EVENT_START_LEAVE_TELEPORT_DELAY * 1000) + 60000L);
		}
		catch (Exception e)
		{
			_eventInstance = 0;
			LOG.warn("{} Event: Could not create the Event Instance Location!", _eventName);
		}
	}
	
	protected void giveKillBonus(L2PcInstance playerInstance)
	{
		SystemMessage systemMessage = null;
		PcInventory inv = playerInstance.getInventory();
		if (ItemTable.getInstance().getTemplate(getConfigs().EVENT_KILL_BONUS_ID).isStackable())
		{
			inv.addItem(_eventName, getConfigs().EVENT_KILL_BONUS_ID, getConfigs().EVENT_KILL_BONUS_COUNT, playerInstance, playerInstance);
			
			if (getConfigs().EVENT_KILL_BONUS_COUNT > 1)
			{
				systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
				systemMessage.addItemName(getConfigs().EVENT_KILL_BONUS_ID);
				systemMessage.addLong(getConfigs().EVENT_KILL_BONUS_COUNT);
			}
			else
			{
				systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
				systemMessage.addItemName(getConfigs().EVENT_KILL_BONUS_ID);
			}
			
			playerInstance.sendPacket(systemMessage);
		}
		else
		{
			for (int i = 0; i < getConfigs().EVENT_KILL_BONUS_COUNT; ++i)
			{
				inv.addItem(_eventName, getConfigs().EVENT_KILL_BONUS_ID, 1, playerInstance, playerInstance);
				systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
				systemMessage.addItemName(getConfigs().EVENT_KILL_BONUS_ID);
				playerInstance.sendPacket(systemMessage);
			}
		}
	}
	
	protected void deliverRewards(L2PcInstance playerInstance, List<int[]> rewards)
	{
		SystemMessage systemMessage = null;
		
		for (int[] reward : rewards)
		{
			PcInventory inv = playerInstance.getInventory();
			
			// Check for stackable item, non stackabe items need to be added one by one
			if (ItemTable.getInstance().getTemplate(reward[0]).isStackable())
			{
				inv.addItem(_eventName, reward[0], reward[1], playerInstance, playerInstance);
				
				if (reward[1] > 1)
				{
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
					systemMessage.addItemName(reward[0]);
					systemMessage.addLong(reward[1]);
				}
				else
				{
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
					systemMessage.addItemName(reward[0]);
				}
				
				playerInstance.sendPacket(systemMessage);
			}
			else
			{
				for (int i = 0; i < reward[1]; ++i)
				{
					inv.addItem(_eventName, reward[0], 1, playerInstance, playerInstance);
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
					systemMessage.addItemName(reward[0]);
					playerInstance.sendPacket(systemMessage);
				}
			}
		}
		
		StatusUpdate statusUpdate = new StatusUpdate(playerInstance);
		final NpcHtmlMessage html = new NpcHtmlMessage();
		
		statusUpdate.addAttribute(StatusUpdate.CUR_LOAD, playerInstance.getCurrentLoad());
		html.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), "data/html/events/Reward.html"));
		playerInstance.sendPacket(statusUpdate);
		playerInstance.sendPacket(html);
	}
	
	public void onTeleported(L2PcInstance playerInstance)
	{
		if (!isStarted() || (playerInstance == null) || !isParticipant(playerInstance.getObjectId()))
		{
			return;
		}
		
		if (playerInstance.isMageClass())
		{
			if ((getConfigs().EVENT_MAGE_BUFFS != null) && (!getConfigs().EVENT_MAGE_BUFFS.isEmpty()))
			{
				
				for (Entry<Integer, Integer> e : getConfigs().EVENT_MAGE_BUFFS.entrySet())
				{
					Skill skill = SkillData.getInstance().getSkill(e.getKey(), e.getValue());
					if (skill != null)
					{
						skill.applyEffects(playerInstance, playerInstance);
					}
				}
			}
		}
		else
		{
			if ((getConfigs().EVENT_FIGHTER_BUFFS != null) && !getConfigs().EVENT_FIGHTER_BUFFS.isEmpty())
			{
				for (Entry<Integer, Integer> e : getConfigs().EVENT_FIGHTER_BUFFS.entrySet())
				{
					Skill skill = SkillData.getInstance().getSkill(e.getKey(), e.getValue());
					if (skill != null)
					{
						skill.applyEffects(playerInstance, playerInstance);
					}
				}
			}
		}
	}
	
	public void onLogout(L2PcInstance playerInstance)
	{
		if ((playerInstance != null) && (isStarted() || isParticipating()))
		{
			if (removeParticipant(playerInstance))
			{
				playerInstance.setXYZInvisible((83447 + Rnd.get(101)) - 50, (148638 + Rnd.get(101)) - 50, -3400); // Giran
			}
		}
	}
	
	public boolean onScrollUse(int eventPlayerObjectId)
	{
		if (!isStarted())
		{
			return true;
		}
		
		if (isParticipant(eventPlayerObjectId) && !getConfigs().EVENT_SCROLL_ALLOWED)
		{
			return false;
		}
		return true;
	}
	
	public boolean onPotionUse(int eventPlayerObjectId)
	{
		if (!isStarted())
		{
			return true;
		}
		
		if (isParticipant(eventPlayerObjectId) && !getConfigs().EVENT_POTIONS_ALLOWED)
		{
			return false;
		}
		return true;
	}
	
	public boolean onEscapeUse(int eventPlayerObjectId)
	{
		if (!isStarted())
		{
			return true;
		}
		
		if (isParticipant(eventPlayerObjectId))
		{
			return false;
		}
		return true;
	}
	
	public boolean onItemSummon(int eventPlayerObjectId)
	{
		if (!isStarted())
		{
			return true;
		}
		
		if (isParticipant(eventPlayerObjectId) && !getConfigs().EVENT_SUMMON_BY_ITEM_ALLOWED)
		{
			return false;
		}
		return true;
	}
	
	public boolean onUseItem(L2PcInstance actor, L2ItemInstance item)
	{
		if (!isStarted() || !isParticipant(actor.getObjectId()))
		{
			return true;
		}
		
		if ((getConfigs().EVENT_RESTRICT_ITEMS != null) && (getConfigs().EVENT_RESTRICT_ITEMS.length != 0))
		{
			for (int itemId : getConfigs().EVENT_RESTRICT_ITEMS)
			{
				if (item.getId() == itemId)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean onRequestUnEquipItem(L2PcInstance player)
	{
		return false;
	}
	
	// TODO: add
	public boolean calculateDeathPenaltyBuff(L2PcInstance player)
	{
		if (isStarted() && isParticipant(player.getObjectId()))
		{
			return true;
		}
		return false;
	}
	
	public String getCommand()
	{
		return _command;
	}
	
	public int getEventInstance()
	{
		return _eventInstance;
	}
	
	public boolean talkWithNpc(L2PcInstance player, L2Npc npc)
	{
		return false;
	}
}
