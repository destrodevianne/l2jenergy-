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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.PlayerMinigameScoreDAO;
import com.l2jserver.gameserver.instancemanager.games.MiniGameScoreManager;

public class PlayerMinigameScoreDAOMySQLImpl implements PlayerMinigameScoreDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(PlayerMinigameScoreDAOMySQLImpl.class);
	
	private static final String SELECT = "SELECT characters.char_name AS name, character_minigame_score.score AS score, character_minigame_score.charId AS charId FROM characters, character_minigame_score WHERE characters.charId=character_minigame_score.charId";
	private static final String REPLACE = "REPLACE INTO character_minigame_score(charId, score) VALUES (?, ?)";
	
	@Override
	public void select()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT))
		{
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					final String name = rs.getString("name");
					final int score = rs.getInt("score");
					final int objectId = rs.getInt("charId");
					MiniGameScoreManager.addScore(objectId, score, name);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Could not restore score data!", e);
		}
	}
	
	@Override
	public void replace(final int objectId, final int score)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(REPLACE))
		{
			ps.setInt(1, objectId);
			ps.setInt(2, score);
			ps.execute();
		}
		catch (final Exception e)
		{
			LOG.error("Could not restore score data!", e);
		}
	}
}
