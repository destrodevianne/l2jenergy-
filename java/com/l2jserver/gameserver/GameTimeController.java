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
package com.l2jserver.gameserver;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jserver.gameserver.model.actor.L2Character;

/**
 * Game Time controller class.
 * @author Forsaiken, Sacrifice
 */
public final class GameTimeController extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger(GameTimeController.class);
	
	public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	private static final int IG_DAYS_PER_DAY = 6;
	private static final int MILLIS_PER_IG_DAY = (3600000 * 24) / IG_DAYS_PER_DAY;
	private static final int SECONDS_PER_IG_DAY = MILLIS_PER_IG_DAY / 1000;
	protected static final int MINUTES_PER_IG_DAY = SECONDS_PER_IG_DAY / 60;
	private static final int TICKS_PER_IG_DAY = SECONDS_PER_IG_DAY * TICKS_PER_SECOND;
	protected static final int TICKS_SUN_STATE_CHANGE = TICKS_PER_IG_DAY / 4;
	
	private final Set<L2Character> _movingObjects = ConcurrentHashMap.newKeySet();
	private final long _referenceTime;
	
	protected GameTimeController()
	{
		super("GameTimeController");
		super.setDaemon(true);
		super.setPriority(MAX_PRIORITY);
		
		_referenceTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static void init()
	{
		getInstance().start();
	}
	
	public final int getGameHour()
	{
		return getGameTime() / 60;
	}
	
	public final int getGameMinute()
	{
		return getGameTime() % 60;
	}
	
	/**
	 * The true GameTime tick. Directly taken from current time. This represents the tick of the time.
	 * @return
	 */
	public final int getGameTicks()
	{
		return (int) ((System.currentTimeMillis() - _referenceTime) / MILLIS_IN_TICK);
	}
	
	public final int getGameTime()
	{
		return (getGameTicks() % TICKS_PER_IG_DAY) / MILLIS_IN_TICK;
	}
	
	public final boolean isNight()
	{
		return getGameHour() < 6;
	}
	
	/**
	 * Move all L2Characters contained in movingObjects of GameTimeController.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <ul>
	 * <li>Update the position of each L2Character</li>
	 * <li>If movement is finished, the L2Character is removed from movingObjects</li>
	 * <li>Create a task to update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED</li>
	 * </ul>
	 */
	private final void moveObjects()
	{
		_movingObjects.removeIf(L2Character::updatePosition);
	}
	
	/**
	 * Add a L2Character to movingObjects of GameTimeController.
	 * @param character The L2Character to add to movingObjects of GameTimeController
	 */
	public final void registerMovingObject(final L2Character character)
	{
		if (character == null)
		{
			return;
		}
		_movingObjects.add(character);
	}
	
	protected final void stopTimer()
	{
		super.interrupt();
		LOG.info("Stopping {}", getClass().getSimpleName());
	}
	
	@Override
	public final void run()
	{
		long nextTickTime, sleepTime;
		boolean isNight = isNight();
		
		if (isNight)
		{
			ThreadPoolManager.getInstance().executeAi(() -> DayNightSpawnManager.getInstance().notifyChangeMode());
		}
		
		while (true)
		{
			nextTickTime = ((System.currentTimeMillis() / MILLIS_IN_TICK) * MILLIS_IN_TICK) + 100;
			
			try
			{
				moveObjects();
			}
			catch (final Throwable e)
			{
				LOG.warn("{}: Unable to move objects!", getClass().getSimpleName(), e);
			}
			
			sleepTime = nextTickTime - System.currentTimeMillis();
			if (sleepTime > 0)
			{
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (final InterruptedException e)
				{
					LOG.info("{}: it has been interrupted! due to {}", getClass().getSimpleName(), e.getMessage());
				}
			}
			
			if (isNight() != isNight)
			{
				isNight = !isNight;
				ThreadPoolManager.getInstance().executeAi(() -> DayNightSpawnManager.getInstance().notifyChangeMode());
			}
		}
	}
	
	public static GameTimeController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GameTimeController INSTANCE = new GameTimeController();
	}
}