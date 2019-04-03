/*
 * Copyright (C) 2004-2019 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.events.FishingConfig;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.instancemanager.GlobalVariablesManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.fishing.L2Fisher;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public class FishingChampionshipManager
{
	protected static final Logger LOG = LoggerFactory.getLogger(FishingChampionshipManager.class);
	
	private static final String INSERT = "INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)";
	private static final String DELETE = "DELETE FROM fishing_championship";
	private static final String SELECT = "SELECT `PlayerName`, `fishLength`, `rewarded` FROM fishing_championship";
	
	protected final List<String> _playersName = new ArrayList<>();
	protected final List<String> _fishLength = new ArrayList<>();
	protected final List<String> _winPlayersName = new ArrayList<>();
	protected final List<String> _winFishLength = new ArrayList<>();
	protected final List<L2Fisher> _tmpPlayers = new ArrayList<>();
	protected final List<L2Fisher> _winPlayers = new ArrayList<>();
	protected long _enddate = 0;
	protected double _minFishLength = 0;
	protected boolean _needRefresh = true;
	
	protected FishingChampionshipManager()
	{
		if (FishingConfig.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			restoreData();
			refreshWinResult();
			recalculateMinLength();
			
			if (_enddate <= System.currentTimeMillis())
			{
				_enddate = System.currentTimeMillis();
				new finishChamp().run();
			}
			else
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new finishChamp(), _enddate - System.currentTimeMillis());
			}
		}
		else
		{
			LOG.info("FishingChampionshipManager: Disabled.");
		}
		
	}
	
	protected void setEndOfChamp()
	{
		Calendar finishtime = Calendar.getInstance();
		finishtime.setTimeInMillis(_enddate);
		finishtime.set(Calendar.MINUTE, 0);
		finishtime.set(Calendar.SECOND, 0);
		finishtime.add(Calendar.DAY_OF_MONTH, 6);
		finishtime.set(Calendar.DAY_OF_WEEK, 3);
		finishtime.set(Calendar.HOUR_OF_DAY, 19);
		_enddate = finishtime.getTimeInMillis();
	}
	
	private void restoreData()
	{
		_enddate = GlobalVariablesManager.getInstance().getLong("fishChampionshipEnd", 0);
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					int rewarded = rs.getInt("rewarded");
					if (rewarded == 0)
					{
						_tmpPlayers.add(new L2Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), 0));
					}
					else if (rewarded > 0)
					{
						_winPlayers.add(new L2Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), rewarded));
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("FishingChampionshipManager: Can't restore fishing championship info!", e);
		}
	}
	
	public synchronized void newFish(L2PcInstance pl, int lureId)
	{
		if (!FishingConfig.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			return;
		}
		
		double p1 = Rnd.get(60, 80);
		if ((p1 < 90) && (lureId > 8484) && (lureId < 8486))
		{
			final long diff = Math.round(90 - p1);
			if (diff > 1)
			{
				p1 += Rnd.get(1, diff);
			}
		}
		
		final double len = (Rnd.get(100, 999) / 1000.) + p1;
		
		pl.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CAUGHT_FISH_S1_LENGTH).addString(String.valueOf(len)));
		
		if (_tmpPlayers.size() < 5)
		{
			for (L2Fisher fisher : _tmpPlayers)
			{
				if (fisher.getName().equalsIgnoreCase(pl.getName()))
				{
					if (fisher.getLength() < len)
					{
						fisher.setLength(len);
						pl.sendPacket(SystemMessageId.REGISTERED_IN_FISH_SIZE_RANKING);
						recalculateMinLength();
					}
					return;
				}
			}
			_tmpPlayers.add(new L2Fisher(pl.getName(), len, 0));
			pl.sendPacket(SystemMessageId.REGISTERED_IN_FISH_SIZE_RANKING);
			recalculateMinLength();
		}
		else if (_minFishLength < len)
		{
			for (L2Fisher fisher : _tmpPlayers)
			{
				if (fisher.getName().equalsIgnoreCase(pl.getName()))
				{
					if (fisher.getLength() < len)
					{
						fisher.setLength(len);
						pl.sendPacket(SystemMessageId.REGISTERED_IN_FISH_SIZE_RANKING);
						recalculateMinLength();
					}
					return;
				}
			}
			
			L2Fisher minFisher = null;
			double minLen = 99999.;
			for (L2Fisher fisher : _tmpPlayers)
			{
				if (fisher.getLength() < minLen)
				{
					minFisher = fisher;
					minLen = minFisher.getLength();
				}
			}
			_tmpPlayers.remove(minFisher);
			_tmpPlayers.add(new L2Fisher(pl.getName(), len, 0));
			pl.sendPacket(SystemMessageId.REGISTERED_IN_FISH_SIZE_RANKING);
			recalculateMinLength();
		}
	}
	
	private void recalculateMinLength()
	{
		double minLen = 99999.;
		for (L2Fisher fisher : _tmpPlayers)
		{
			if (fisher.getLength() < minLen)
			{
				minLen = fisher.getLength();
			}
		}
		_minFishLength = minLen;
	}
	
	public long getTimeRemaining()
	{
		return (_enddate - System.currentTimeMillis()) / 60000;
	}
	
	public String getWinnerName(int par)
	{
		if (_winPlayersName.size() >= par)
		{
			return _winPlayersName.get(par - 1);
		}
		
		return "-";
	}
	
	public String getCurrentName(int par)
	{
		if (_playersName.size() >= par)
		{
			return _playersName.get(par - 1);
		}
		
		return "-";
	}
	
	public String getFishLength(int par)
	{
		if (_winFishLength.size() >= par)
		{
			return _winFishLength.get(par - 1);
		}
		
		return "0";
	}
	
	public String getCurrentFishLength(int par)
	{
		if (_fishLength.size() >= par)
		{
			return _fishLength.get(par - 1);
		}
		return "0";
	}
	
	public boolean isWinner(String playerName)
	{
		for (String name : _winPlayersName)
		{
			if (name.equals(playerName))
			{
				return true;
			}
		}
		return false;
	}
	
	public void getReward(L2PcInstance pl)
	{
		for (L2Fisher fisher : _winPlayers)
		{
			if (fisher.getName().equalsIgnoreCase(pl.getName()))
			{
				if (fisher.getRewardType() != 2)
				{
					int rewardCnt = 0;
					for (int x = 0; x < _winPlayersName.size(); x++)
					{
						if (_winPlayersName.get(x).equalsIgnoreCase(pl.getName()))
						{
							switch (x)
							{
								case 0:
									rewardCnt = FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_1;
									break;
								
								case 1:
									rewardCnt = FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_2;
									break;
								
								case 2:
									rewardCnt = FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_3;
									break;
								
								case 3:
									rewardCnt = FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_4;
									break;
								
								case 4:
									rewardCnt = FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_5;
									break;
							}
						}
					}
					fisher.setRewardType(2);
					if (rewardCnt > 0)
					{
						pl.addItem("fishing_reward", FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM, rewardCnt, null, true);
						
						final NpcHtmlMessage html = new NpcHtmlMessage();
						html.setFile(pl.getHtmlPrefix(), "data/scripts/ai/npc/Fisherman/fish_event_reward001.htm");
						pl.sendPacket(html);
						
					}
				}
			}
		}
	}
	
	public void showMidResult(L2PcInstance pl)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		
		if (_needRefresh)
		{
			html.setFile(pl.getHtmlPrefix(), "data/scripts/ai/npc/Fisherman/fish_event003.htm");
			pl.sendPacket(html);
			
			refreshResult();
			ThreadPoolManager.getInstance().scheduleGeneral(new needRefresh(), 60000);
			return;
		}
		
		html.setFile(pl.getHtmlPrefix(), "data/scripts/ai/npc/Fisherman/fish_event002.htm");
		
		String str = null;
		for (int x = 1; x <= 5; x++)
		{
			str += "<tr><td width=50 align=center>" + x + "</td>";
			str += "<td width=150 align=center>" + getCurrentName(x) + "</td>";
			str += "<td width=70 align=center>" + getCurrentFishLength(x) + "</td></tr>";
		}
		html.replace("%TABLE%", str);
		html.replace("%prizeItem%", ItemTable.getInstance().getTemplate(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
		html.replace("%prizeFirst%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_1));
		html.replace("%prizeTwo%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_2));
		html.replace("%prizeThree%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_3));
		html.replace("%prizeFour%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_4));
		html.replace("%prizeFive%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_5));
		pl.sendPacket(html);
	}
	
	public void showChampScreen(L2PcInstance pl, L2Npc npc)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(pl.getHtmlPrefix(), "data/scripts/ai/npc/Fisherman/fish_event001.htm");
		
		String str = null;
		for (int x = 1; x <= 5; x++)
		{
			str += "<tr><td width=50 align=center>" + x + "</td>";
			str += "<td width=150 align=center>" + getWinnerName(x) + "</td>";
			str += "<td width=70 align=center>" + getFishLength(x) + "</td></tr>";
		}
		html.replace("%TABLE%", str);
		html.replace("%prizeItem%", ItemTable.getInstance().getTemplate(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
		html.replace("%prizeFirst%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_1));
		html.replace("%prizeTwo%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_2));
		html.replace("%prizeThree%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_3));
		html.replace("%prizeFour%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_4));
		html.replace("%prizeFive%", String.valueOf(FishingConfig.ALT_FISH_CHAMPIONSHIP_REWARD_5));
		html.replace("%refresh%", String.valueOf(getTimeRemaining()));
		html.replace("%objectId%", String.valueOf(npc.getObjectId()));
		pl.sendPacket(html);
	}
	
	public void shutdown()
	{
		GlobalVariablesManager.getInstance().set("fishChampionshipEnd", _enddate);
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE))
		{
			for (L2Fisher fisher : _winPlayers)
			{
				PreparedStatement ps1 = con.prepareStatement(INSERT);
				ps1.setString(1, fisher.getName());
				ps1.setDouble(2, fisher.getLength());
				ps1.setInt(3, fisher.getRewardType());
				ps.execute();
			}
			
			for (L2Fisher fisher : _tmpPlayers)
			{
				PreparedStatement ps2 = con.prepareStatement(INSERT);
				ps2.setString(1, fisher.getName());
				ps2.setDouble(2, fisher.getLength());
				ps2.setInt(3, 0);
				ps2.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.warn("FishingChampionshipManager: Can't update infos!", e);
		}
	}
	
	private synchronized void refreshResult()
	{
		_needRefresh = false;
		
		_playersName.clear();
		_fishLength.clear();
		
		L2Fisher fisher1;
		L2Fisher fisher2;
		
		for (int x = 0; x <= (_tmpPlayers.size() - 1); x++)
		{
			for (int y = 0; y <= (_tmpPlayers.size() - 2); y++)
			{
				fisher1 = _tmpPlayers.get(y);
				fisher2 = _tmpPlayers.get(y + 1);
				if (fisher1.getLength() < fisher2.getLength())
				{
					_tmpPlayers.set(y, fisher2);
					_tmpPlayers.set(y + 1, fisher1);
				}
			}
		}
		
		for (int x = 0; x <= (_tmpPlayers.size() - 1); x++)
		{
			_playersName.add(_tmpPlayers.get(x).getName());
			_fishLength.add(String.valueOf(_tmpPlayers.get(x).getLength()));
		}
	}
	
	protected void refreshWinResult()
	{
		_winPlayersName.clear();
		_winFishLength.clear();
		
		L2Fisher fisher1;
		L2Fisher fisher2;
		
		for (int x = 0; x <= (_winPlayers.size() - 1); x++)
		{
			for (int y = 0; y <= (_winPlayers.size() - 2); y++)
			{
				fisher1 = _winPlayers.get(y);
				fisher2 = _winPlayers.get(y + 1);
				if (fisher1.getLength() < fisher2.getLength())
				{
					_winPlayers.set(y, fisher2);
					_winPlayers.set(y + 1, fisher1);
				}
			}
		}
		
		for (int x = 0; x <= (_winPlayers.size() - 1); x++)
		{
			_winPlayersName.add(_winPlayers.get(x).getName());
			_winFishLength.add(String.valueOf(_winPlayers.get(x).getLength()));
		}
	}
	
	private class finishChamp implements Runnable
	{
		protected finishChamp()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			_winPlayers.clear();
			for (L2Fisher fisher : _tmpPlayers)
			{
				fisher.setRewardType(1);
				_winPlayers.add(fisher);
			}
			_tmpPlayers.clear();
			
			refreshWinResult();
			setEndOfChamp();
			shutdown();
			
			LOG.info("FishingChampionshipManager : New event period start.");
			ThreadPoolManager.getInstance().scheduleGeneral(new finishChamp(), _enddate - System.currentTimeMillis());
		}
	}
	
	private class needRefresh implements Runnable
	{
		protected needRefresh()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			_needRefresh = true;
		}
	}
	
	public static FishingChampionshipManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FishingChampionshipManager _instance = new FishingChampionshipManager();
	}
}
