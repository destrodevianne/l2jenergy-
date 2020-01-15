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
package com.l2jserver.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.tasks.PremiumExpireTask;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenersContainer;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;

public class PremiumManager
{
	private static final Logger LOG = LoggerFactory.getLogger(PremiumManager.class);
	
	private static final String LOAD_SQL = "SELECT account_name,enddate FROM account_premium WHERE account_name = ?";
	private static final String UPDATE_SQL = "REPLACE INTO account_premium (enddate,account_name) VALUE (?,?)";
	private static final String DELETE_SQL = "DELETE FROM account_premium WHERE account_name = ?";
	
	private final Map<String, Long> _premiumData = new HashMap<>();
	private final Map<String, ScheduledFuture<?>> _expiretasks = new HashMap<>();
	private final ListenersContainer _listenerContainer = Containers.Players();
	
	private final Consumer<OnPlayerLogin> _playerLoginEvent = event ->
	{
		L2PcInstance player = event.getActiveChar();
		String accountName = player.getAccountName();
		loadPremiumData(accountName);
		long now = System.currentTimeMillis();
		long premiumExpiration = getPremiumExpiration(accountName);
		player.setPremium(premiumExpiration > now);
		if (player.isPremium())
		{
			startExpireTask(player, premiumExpiration - now);
		}
		else if (premiumExpiration > 0L)
		{
			removePremiumStatus(accountName, false);
		}
	};
	
	private final Consumer<OnPlayerLogout> _playerLogoutEvent = event ->
	{
		L2PcInstance player = event.getActiveChar();
		stopExpireTask(player);
	};
	
	protected PremiumManager()
	{
		getListenerContainer().addListener(new ConsumerEventListener(getListenerContainer(), EventType.ON_PLAYER_LOGIN, getPlayerLoginEvent(), this));
		getListenerContainer().addListener(new ConsumerEventListener(getListenerContainer(), EventType.ON_PLAYER_LOGOUT, getPlayerLogoutEvent(), this));
	}
	
	private void startExpireTask(L2PcInstance player, long delay)
	{
		ScheduledFuture<?> task = ThreadPoolManager.getInstance().scheduleGeneral(new PremiumExpireTask(player), delay);
		getExpiretasks().put(player.getAccountName(), task);
	}
	
	private void stopExpireTask(L2PcInstance player)
	{
		ScheduledFuture<?> task = getExpiretasks().remove(player.getAccountName());
		if (task != null)
		{
			task.cancel(false);
			task = null;
		}
	}
	
	private void loadPremiumData(String accountName)
	{
		try
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(LOAD_SQL);)
			{
				stmt.setString(1, accountName);
				try (ResultSet rset = stmt.executeQuery();)
				{
					while (rset.next())
					{
						getPremiumData().put(rset.getString(1), rset.getLong(2));
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
	}
	
	public long getPremiumExpiration(String accountName)
	{
		return getPremiumData().getOrDefault(accountName, 0L);
	}
	
	public void addPremiumTime(String accountName, int timeValue, TimeUnit timeUnit)
	{
		long now;
		long newPremiumExpiration;
		long addTime = timeUnit.toMillis(timeValue);
		now = System.currentTimeMillis();
		long oldPremiumExpiration = Math.max(now, getPremiumExpiration(accountName));
		newPremiumExpiration = oldPremiumExpiration + addTime;
		try
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(UPDATE_SQL);)
			{
				stmt.setLong(1, newPremiumExpiration);
				stmt.setString(2, accountName);
				stmt.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
		
		getPremiumData().put(accountName, newPremiumExpiration);
		L2PcInstance playerOnline = L2World.getInstance().getPlayers().stream().filter(p -> accountName.equals(p.getAccountName())).findFirst().orElse(null);
		if (playerOnline != null)
		{
			stopExpireTask(playerOnline);
			startExpireTask(playerOnline, newPremiumExpiration - now);
			if (!playerOnline.isPremium())
			{
				playerOnline.setPremium(true);
			}
		}
	}
	
	public void removePremiumStatus(String accountName, boolean checkOnline)
	{
		if (checkOnline)
		{
			L2PcInstance playerOnline = L2World.getInstance().getPlayers().stream().filter(p -> accountName.equals(p.getAccountName())).findFirst().orElse(null);
			if ((playerOnline != null) && playerOnline.isPremium())
			{
				playerOnline.setPremium(false);
				stopExpireTask(playerOnline);
			}
		}
		getPremiumData().remove(accountName);
		
		try
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(DELETE_SQL);)
			{
				stmt.setString(1, accountName);
				stmt.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
	}
	
	public Map<String, Long> getPremiumData()
	{
		return _premiumData;
	}
	
	public ListenersContainer getListenerContainer()
	{
		return _listenerContainer;
	}
	
	public Map<String, ScheduledFuture<?>> getExpiretasks()
	{
		return _expiretasks;
	}
	
	public Consumer<OnPlayerLogin> getPlayerLoginEvent()
	{
		return _playerLoginEvent;
	}
	
	public Consumer<OnPlayerLogout> getPlayerLogoutEvent()
	{
		return _playerLogoutEvent;
	}
	
	public static final PremiumManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PremiumManager _instance = new PremiumManager();
	}
}
