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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.RecommendationBonusDAO;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Recommendation Bonus DAO MySQL implementation.
 * @author Zoey76, Мо3олЬ
 */
public class RecommendationBonusDAOMySQLImpl implements RecommendationBonusDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationBonusDAOMySQLImpl.class);
	
	private static final String SELECT = "SELECT rec_have,rec_left,time_left FROM character_reco_bonus WHERE charId=? LIMIT 1";
	
	private static final String INSERT = "INSERT INTO character_reco_bonus (charId,rec_have,rec_left,time_left) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE rec_have=?, rec_left=?, time_left=?";
	
	@Override
	public void load(L2PcInstance player)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					player.setRecomHave(rs.getInt("rec_have"));
					player.setRecomLeft(rs.getInt("rec_left"));
					player.setRecomBonusTime(rs.getInt("time_left"));
				}
				else
				{
					player.setRecomBonusTime(3600);
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not restore Recommendations for {}, {}", player, e);
		}
	}
	
	@Override
	public void insert(L2PcInstance player, int recTimeToSave)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, player.getRecomHave());
			ps.setInt(3, player.getRecomLeft());
			ps.setLong(4, recTimeToSave);
			// Update part
			ps.setInt(5, player.getRecomHave());
			ps.setInt(6, player.getRecomLeft());
			ps.setLong(7, recTimeToSave);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.error("Could not update Recommendations for player: {}", player, e);
		}
	}
}
