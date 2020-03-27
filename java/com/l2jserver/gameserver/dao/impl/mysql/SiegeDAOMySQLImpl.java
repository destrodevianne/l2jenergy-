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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.SiegeDAO;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.TowerSpawn;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.Siege;

/**
 * Siege DAO MySQL implementation.
 * @author Мо3олЬ
 */
public class SiegeDAOMySQLImpl implements SiegeDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(SiegeDAOMySQLImpl.class);
	
	private final Map<Integer, List<TowerSpawn>> _flameTowers = new HashMap<>();
	
	private static final String SELECT_CASTLE_TRAPUPGRADE = "SELECT * FROM castle_trapupgrade WHERE castleId=?";
	private static final String SELECT_SIEGE_CLANS = "SELECT clan_id FROM siege_clans where clan_id=? and castle_id=?";
	private static final String DELETE_SIEGE_CLANS_CASTLE_ID = "DELETE FROM siege_clans WHERE castle_id=?";
	private static final String DELETE_SIEGE_CLANS_CLAN_ID = "DELETE FROM siege_clans WHERE clan_id=?";
	private static final String DELETE_SIEGE_CLANS_CLEAR_SIEGE_WAITING = "DELETE FROM siege_clans WHERE castle_id=? and type = 2";
	private static final String UPDATE_CASTLE_CREST = "UPDATE castle SET showNpcCrest = ? WHERE id = ?";
	
	@Override
	public void loadTrapUpgrade(int castleId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_CASTLE_TRAPUPGRADE))
		{
			ps.setInt(1, castleId);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					_flameTowers.get(castleId).get(rs.getInt("towerIndex")).setUpgradeLevel(rs.getInt("level"));
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Exception: loadTrapUpgrade!", e);
		}
	}
	
	@Override
	public boolean checkIsRegistered(L2Clan clan, int castleid)
	{
		if (clan == null)
		{
			return false;
		}
		
		if (clan.getCastleId() > 0)
		{
			return true;
		}
		
		boolean register = false;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_SIEGE_CLANS))
		{
			ps.setInt(1, clan.getId());
			ps.setInt(2, castleid);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					register = true;
					break;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("{}: Exception checkIsRegistered!", getClass().getSimpleName(), e);
		}
		return register;
	}
	
	/** Clear all registered siege clans from database for castle */
	@Override
	public void clearSiegeClan(Siege clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_SIEGE_CLANS_CASTLE_ID))
		{
			ps.setInt(1, clan.getCastle().getResidenceId());
			ps.execute();
			
			if (clan.getCastle().getOwnerId() > 0)
			{
				try (PreparedStatement delete = con.prepareStatement(DELETE_SIEGE_CLANS_CLAN_ID))
				{
					delete.setInt(1, clan.getCastle().getOwnerId());
					delete.execute();
				}
			}
			
			clan.getAttackerClans().clear();
			clan.getDefenderClans().clear();
			clan.getDefenderWaitingClans().clear();
		}
		catch (Exception e)
		{
			LOG.warn("Exception: clearSiegeClan()", e);
		}
	}
	
	/** Clear all siege clans waiting for approval from database for castle */
	@Override
	public void clearSiegeWaitingClan(Castle castle)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_SIEGE_CLANS_CLEAR_SIEGE_WAITING))
		{
			ps.setInt(1, castle.getResidenceId());
			ps.execute();
			castle.getSiege().getDefenderWaitingClans().clear();
		}
		catch (Exception e)
		{
			LOG.warn("Exception: clearSiegeWaitingClan()", e);
		}
	}
	
	@Override
	public void updateShowNpcCrest(Castle castle)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CASTLE_CREST))
		{
			ps.setString(1, String.valueOf(castle.getShowNpcCrest()));
			ps.setInt(2, castle.getResidenceId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.info("Error saving showNpcCrest for castle {}!", castle.getName(), e);
		}
	}
}
