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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.FriendDAO;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Friend DAO MySQL implementation.
 * @author Zoey76, Мо3олЬ
 */
public class FriendDAOMySQLImpl implements FriendDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(FriendDAOMySQLImpl.class);
	
	private static final String INSERT = "INSERT INTO character_friends (charId, friendId, relation) VALUES (?, ?, 1)";
	private static final String SELECT = "SELECT friendId FROM character_friends WHERE charId=? AND relation=0 AND friendId<>charId";
	private static final String SELECT_LIST = "SELECT friendId FROM character_friends WHERE charId=? AND relation=1";
	private static final String DELETE = "DELETE FROM character_friends WHERE charId=? AND friendId=? AND relation=1";
	
	@Override
	public void load(L2PcInstance player)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					player.addFriend(rs.getInt("friendId"));
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Error found in {} FriendList: ", player, e);
		}
	}
	
	@Override
	public List<Integer> loadList(int objectId)
	{
		List<Integer> list = new ArrayList<>();
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_LIST))
		{
			ps.setInt(1, objectId);
			try (ResultSet rs = ps.executeQuery())
			{
				int friendId;
				while (rs.next())
				{
					friendId = rs.getInt("friendId");
					if (friendId == objectId)
					{
						continue;
					}
					list.add(friendId);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Error found in {} FriendList while loading BlockList!", objectId, e);
		}
		return list;
	}
	
	@Override
	public void removeFromDB(L2PcInstance player, int targetId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, targetId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warn("Could not remove blocked player!", e);
		}
	}
	
	@Override
	public void persistInDB(L2PcInstance player, int targetId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT))
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, targetId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warn("Could not add blocked player!", e);
		}
	}
}
