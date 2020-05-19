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
package com.l2jserver.gameserver.bbs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.enums.ForumType;
import com.l2jserver.gameserver.enums.ForumVisibility;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.bbs.Forum;

public class ForumsBBSManager extends BaseBBSManager
{
	private static final Logger LOG = LoggerFactory.getLogger(ForumsBBSManager.class);
	
	private final List<Forum> _table = new CopyOnWriteArrayList<>();
	private int _lastId = 1;
	
	protected ForumsBBSManager()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT forum_id FROM forums WHERE forum_type = 0"))
		{
			while (rs.next())
			{
				addForum(new Forum(rs.getInt("forum_id"), null));
			}
		}
		catch (Exception e)
		{
			LOG.warn("Data error on Forum (root)!", e);
		}
	}
	
	public void initRoot()
	{
		_table.forEach(f -> DAOFactory.getInstance().getForumDAO().findById(f));
		LOG.info("Loaded {} forums. Last forum id used: {}", _table.size(), _lastId);
	}
	
	public void addForum(Forum forum)
	{
		if (forum == null)
		{
			return;
		}
		
		_table.add(forum);
		
		if (forum.getId() > _lastId)
		{
			_lastId = forum.getId();
		}
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
	}
	
	public Forum getForumByName(String name)
	{
		return _table.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}
	
	public Forum createNewForum(String name, Forum parent, ForumType type, ForumVisibility visibility, int ownerId)
	{
		final int id = getANewID();
		final Forum forum = new Forum(id, name, parent, type, visibility, ownerId);
		
		parent.addChildren(forum);
		addForum(forum);
		
		DAOFactory.getInstance().getForumDAO().save(forum);
		return forum;
	}
	
	public int getANewID()
	{
		return ++_lastId;
	}
	
	public Forum getForumByID(int id)
	{
		return _table.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		
	}
	
	public static ForumsBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ForumsBBSManager INSTANCE = new ForumsBBSManager();
	}
}