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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.PlayerPostFriendDAO;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Мо3олЬ
 */
public class PlayerPostFriendDAOMySQLImpl implements PlayerPostFriendDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(PlayerPostFriendDAOMySQLImpl.class);
	
	private final List<String> _postFriends = new CopyOnWriteArrayList<>();
	
	private static final String INSERT = "INSERT INTO character_post_friends (charId, post_friend) VALUES (?, ?)";
	private static final String DELETE = "DELETE FROM character_post_friends WHERE charId = ? and post_friend = ?";
	private static final String SELECT = "SELECT post_friend FROM character_post_friends WHERE charId = ?";
	
	public static final int MAX_POST_FRIEND_SIZE = 100;
	
	@Override
	public void select(L2PcInstance activeChar)
	{
		_postFriends.clear();
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			ps.setInt(1, activeChar.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				int contactId;
				String contactName;
				while (rs.next())
				{
					contactId = rs.getInt(1);
					contactName = CharNameTable.getInstance().getNameById(contactId);
					if ((contactName == null) || contactName.equals(activeChar.getName()) || (contactId == activeChar.getObjectId()))
					{
						continue;
					}
					
					_postFriends.add(contactName);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Error found in {}'s post friends list!", activeChar.getName(), e);
		}
	}
	
	@Override
	public boolean insert(L2PcInstance activeChar, String name)
	{
		int contactId = CharNameTable.getInstance().getIdByName(name);
		if (_postFriends.contains(name))
		{
			activeChar.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
			return false;
		}
		else if (activeChar.getName().equals(name))
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOUR_OWN_NAME);
			return false;
		}
		else if (_postFriends.size() >= MAX_POST_FRIEND_SIZE)
		{
			activeChar.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED);
			return false;
		}
		else if (contactId < 1)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_NAME_S1_NOT_EXIST_TRY_ANOTHER_NAME);
			sm.addString(name);
			activeChar.sendPacket(sm);
			return false;
		}
		else
		{
			for (String contactName : _postFriends)
			{
				if (contactName.equalsIgnoreCase(name))
				{
					activeChar.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
					return false;
				}
			}
		}
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT))
		{
			ps.setInt(1, activeChar.getObjectId());
			ps.setInt(2, contactId);
			ps.execute();
			
			_postFriends.add(name);
		}
		catch (Exception e)
		{
			LOG.warn("Error found in {}'s post friends list!", activeChar.getName(), e);
		}
		return true;
	}
	
	@Override
	public void remove(L2PcInstance activeChar, String name)
	{
		int contactId = CharNameTable.getInstance().getIdByName(name);
		
		if (!_postFriends.contains(name))
		{
			activeChar.sendPacket(SystemMessageId.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
			return;
		}
		else if (contactId < 1)
		{
			// TODO: Message?
			return;
		}
		
		_postFriends.remove(name);
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE))
		{
			ps.setInt(1, activeChar.getObjectId());
			ps.setInt(2, contactId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warn("Error found in {}'s post friends list!", activeChar.getName(), e);
		}
	}
}
