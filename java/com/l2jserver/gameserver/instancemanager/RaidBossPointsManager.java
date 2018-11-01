/*
 * Copyright (C) 2004-2018 L2J Server
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
package com.l2jserver.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Kerberos, JIV
 * @version 8/24/10
 */
public class RaidBossPointsManager
{
	private static final Logger LOG = LoggerFactory.getLogger(RaidBossPointsManager.class);
	
	private static final String SELECT = "SELECT charId, boss_id, points FROM character_raid_points";
	private static final String REPLACE = "REPLACE INTO character_raid_points (charId, boss_id, points) VALUES (?, ?, ?)";
	private static final String DELETE = "DELETE from character_raid_points WHERE charId > 0";
	
	private final Map<Integer, Map<Integer, Integer>> _list = new ConcurrentHashMap<>();
	
	public RaidBossPointsManager()
	{
		init();
	}
	
	private final void init()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT);
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				int charId = rset.getInt("charId");
				int bossId = rset.getInt("boss_id");
				int points = rset.getInt("points");
				Map<Integer, Integer> values = _list.get(charId);
				if (values == null)
				{
					values = new HashMap<>();
				}
				values.put(bossId, points);
				_list.put(charId, values);
			}
			LOG.info("{}: Loaded {} Character Raid Points.", getClass().getSimpleName(), _list.size());
		}
		catch (SQLException e)
		{
			LOG.warn("{}: Couldn't load raid points {}", getClass().getSimpleName(), e);
		}
	}
	
	public final void updatePointsInDB(L2PcInstance player, int raidId, int points)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(REPLACE))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, raidId);
			ps.setInt(3, points);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.warn("{}: Couldn't update char raid points for player: {} {}", getClass().getSimpleName(), player, e);
		}
	}
	
	public final void addPoints(L2PcInstance player, int bossId, int points)
	{
		final Map<Integer, Integer> tmpPoint = _list.computeIfAbsent(player.getObjectId(), k -> new HashMap<>());
		updatePointsInDB(player, bossId, tmpPoint.merge(bossId, points, Integer::sum));
	}
	
	public final int getPointsByOwnerId(int ownerId)
	{
		final Map<Integer, Integer> tmpPoint = _list.get(ownerId);
		int totalPoints = 0;
		
		if ((tmpPoint == null) || tmpPoint.isEmpty())
		{
			return 0;
		}
		
		for (int points : tmpPoint.values())
		{
			totalPoints += points;
		}
		return totalPoints;
	}
	
	public final Map<Integer, Integer> getList(L2PcInstance player)
	{
		return _list.get(player.getObjectId());
	}
	
	public final void cleanUp()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE))
		{
			ps.executeUpdate();
			_list.clear();
		}
		catch (Exception e)
		{
			LOG.warn("{}: Couldn't clean raid points. {}", getClass().getSimpleName(), e);
		}
	}
	
	public final int calculateRanking(int playerObjId)
	{
		final Map<Integer, Integer> rank = getRankList();
		if (rank.containsKey(playerObjId))
		{
			return rank.get(playerObjId);
		}
		return 0;
	}
	
	public Map<Integer, Integer> getRankList()
	{
		final Map<Integer, Integer> tmpPoints = new HashMap<>();
		for (int ownerId : _list.keySet())
		{
			int totalPoints = getPointsByOwnerId(ownerId);
			if (totalPoints != 0)
			{
				tmpPoints.put(ownerId, totalPoints);
			}
		}
		
		final List<Entry<Integer, Integer>> list = new ArrayList<>(tmpPoints.entrySet());
		list.sort(Comparator.comparing(Entry<Integer, Integer>::getValue).reversed());
		int ranking = 1;
		final Map<Integer, Integer> tmpRanking = new HashMap<>();
		for (Entry<Integer, Integer> entry : list)
		{
			tmpRanking.put(entry.getKey(), ranking++);
		}
		return tmpRanking;
	}
	
	public static final RaidBossPointsManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RaidBossPointsManager _instance = new RaidBossPointsManager();
	}
}