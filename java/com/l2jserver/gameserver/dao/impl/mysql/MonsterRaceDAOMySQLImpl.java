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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.MonsterRaceDAO;
import com.l2jserver.gameserver.instancemanager.games.MonsterRace;
import com.l2jserver.gameserver.model.HistoryInfo;

/**
 * Monster Race DAO MySQL implementation.
 * @author Мо3олЬ
 */
public class MonsterRaceDAOMySQLImpl implements MonsterRaceDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(MonsterRaceDAOMySQLImpl.class);
	
	private static final String SAVE_MONSTER_RACE_BETS = "REPLACE INTO mdt_bets (lane_id, bet) VALUES (?,?)";
	private static final String SELECT_MONSTER_RACE_BETS = "SELECT * FROM mdt_bets";
	private static final String CLEAR_MONSTER_RACE_BETS = "UPDATE mdt_bets SET bet = 0";
	private static final String ADD_HISTORY_MONSTER_RACE = "INSERT INTO mdt_history (race_id, first, second, odd_rate) VALUES (?,?,?,?)";
	private static final String LOAD_HISTORY_MONSTER_RACE = "SELECT * FROM mdt_history";
	
	@Override
	public void loadBets(MonsterRace race)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MONSTER_RACE_BETS))
		{
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					race.setBetOnLane(rs.getInt("lane_id"), rs.getLong("bet"), false);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("Can't load bets!", e);
		}
	}
	
	@Override
	public void saveBet(int lane, long sum)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SAVE_MONSTER_RACE_BETS))
		{
			ps.setInt(1, lane);
			ps.setLong(2, sum);
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("Can't save bet!", e);
		}
	}
	
	@Override
	public void clearBets()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_MONSTER_RACE_BETS))
		{
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("Can't clear bets!", e);
		}
	}
	
	@Override
	public void loadHistory(MonsterRace race)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_HISTORY_MONSTER_RACE))
		{
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					race._history.add(new HistoryInfo(rs.getInt("race_id"), rs.getInt("first"), rs.getInt("second"), rs.getDouble("odd_rate")));
					race._raceNumber++;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("Can't load history!", e);
		}
	}
	
	@Override
	public void saveHistory(HistoryInfo history)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_HISTORY_MONSTER_RACE))
		{
			ps.setInt(1, history.getRaceId());
			ps.setInt(2, history.getFirst());
			ps.setInt(3, history.getSecond());
			ps.setDouble(4, history.getOddRate());
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("Can't save history!", e);
		}
	}
}
