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
import com.l2jserver.gameserver.bbs.ForumsBBSManager;
import com.l2jserver.gameserver.dao.ForumDAO;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.enums.ForumType;
import com.l2jserver.gameserver.enums.ForumVisibility;
import com.l2jserver.gameserver.model.bbs.Forum;

/**
 * @author Мо3олЬ
 */
public class ForumDAOMySQLImpl implements ForumDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(ForumDAOMySQLImpl.class);
	
	private static final String SELECT_FORUM = "SELECT forum_name, forum_post, forum_type, forum_perm, forum_owner_id FROM forums WHERE forum_id=?";
	private static final String INSERT_FORUM = "INSERT INTO forums (forum_id,forum_name,forum_parent,forum_post,forum_type,forum_perm,forum_owner_id) VALUES (?,?,?,?,?,?,?)";
	
	@Override
	public void findById(Forum forum)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_FORUM))
		{
			ps.setInt(1, forum.getId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					forum.setName(rs.getString("forum_name"));
					forum.setPost(rs.getInt("forum_post"));
					forum.setType(ForumType.values()[rs.getInt("forum_type")]);
					forum.setVisibility(ForumVisibility.values()[rs.getInt("forum_perm")]);
					forum.setOwnerId(rs.getInt("forum_owner_id"));
				}
			}
		}
		catch (Exception ex)
		{
			LOG.warn("Could not get from database forum Id {}!", forum.getId(), ex);
		}
		
		DAOFactory.getInstance().getTopicDAO().load(forum);
		
		loadChildren(forum);
	}
	
	private void loadChildren(Forum forum)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT forum_id FROM forums WHERE forum_parent=?"))
		{
			ps.setInt(1, forum.getId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final Forum childForum = new Forum(rs.getInt("forum_id"), forum);
					forum.getChildren().add(childForum);
					ForumsBBSManager.getInstance().addForum(childForum);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Could not get from database child forums for forum Id {}!", forum.getId(), e);
		}
	}
	
	@Override
	public void save(Forum forum)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_FORUM))
		{
			ps.setInt(1, forum.getId());
			ps.setString(2, forum.getName());
			ps.setInt(3, forum.getParent().getId());
			ps.setInt(4, forum.getPost());
			ps.setInt(5, forum.getType().ordinal());
			ps.setInt(6, forum.getVisibility().ordinal());
			ps.setInt(7, forum.getOwnerId());
			ps.execute();
		}
		catch (Exception ex)
		{
			LOG.error("Could not save forum If {} in database!", forum.getId(), ex);
		}
	}
}
