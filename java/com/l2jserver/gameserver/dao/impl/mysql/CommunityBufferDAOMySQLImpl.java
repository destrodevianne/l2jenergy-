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
import com.l2jserver.gameserver.dao.CommunityBufferDAO;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.bbs.Scheme;

/**
 * MySQL DAO Factory implementation.
 * @author Мо3олЬ
 */
public class CommunityBufferDAOMySQLImpl implements CommunityBufferDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(CommunityBufferDAOMySQLImpl.class);
	
	public static final String SELECT_SQL_QUERY = "SELECT name, buffs FROM bbs_buffer WHERE charId=?";
	public static final String INSERT_SQL_QUERY = "REPLACE INTO bbs_buffer (name, charId, buffs) VALUES (?,?,?)";
	public static final String DELETE_SQL_QUERY = "DELETE FROM bbs_buffer WHERE charId=? AND name=?";
	
	@Override
	public void select(final L2PcInstance player) // TODO: need fix
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_SQL_QUERY))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				final String buffs = rs.getString("buffs");
				final String name = rs.getString("name");
				if (!buffs.isEmpty())
				{
					final Scheme scheme = new Scheme(name);
					for (final String str : buffs.split(";"))
					{
						final String[] arrayOfString2 = str.split(",");
						final int id = Integer.parseInt(arrayOfString2[0]);
						final int lvl = Integer.parseInt(arrayOfString2[1]);
						scheme.addBuff(id, lvl);
					}
					
					if (scheme.getBuffs().size() > 1)
					{
						player.addBuffScheme(scheme);
					}
				}
				else
				{
					player.sendMessage("Schema " + name + " has been removed as it doesn't have buff!");
					delete(player, name);
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Couldn't restoring BuffScheme!", e);
		}
	}
	
	@Override
	public void insert(final L2PcInstance player, final String buffs, final Scheme scheme)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_SQL_QUERY))
		{
			ps.setString(1, scheme.getName());
			ps.setInt(2, player.getObjectId());
			ps.setString(3, buffs);
			ps.execute();
			
			player.addBuffScheme(scheme);
		}
		catch (Exception e)
		{
			LOG.error("Error add BuffScheme!", e);
		}
	}
	
	@Override
	public void delete(final L2PcInstance player, final String name)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_SQL_QUERY))
		{
			ps.setInt(1, player.getObjectId());
			ps.setString(2, name);
			ps.execute();
			
			player.deleteBuffScheme(name);
		}
		catch (Exception e)
		{
			LOG.error("Error delete BuffScheme!", e);
		}
	}
}
