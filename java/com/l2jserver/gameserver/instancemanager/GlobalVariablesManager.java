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
package com.l2jserver.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.variables.AbstractVariables;

/**
 * Global Variables Manager.
 * @author xban1x
 */
public final class GlobalVariablesManager extends AbstractVariables
{
	private static final Logger LOG = LoggerFactory.getLogger(GlobalVariablesManager.class);
	
	// SQL Queries.
	private static final String SELECT_QUERY = "SELECT * FROM global_variables";
	private static final String DELETE_QUERY = "DELETE FROM global_variables";
	private static final String INSERT_QUERY = "INSERT INTO global_variables (var, value) VALUES (?, ?)";
	
	protected GlobalVariablesManager()
	{
		restoreMe();
	}
	
	@Override
	public boolean restoreMe()
	{
		// Restore previous variables.
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement st = con.createStatement();
			ResultSet rset = st.executeQuery(SELECT_QUERY))
		{
			while (rset.next())
			{
				set(rset.getString("var"), rset.getString("value"));
			}
		}
		catch (SQLException e)
		{
			LOG.warn("{}: Couldn't restore global variables", getClass().getSimpleName());
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		LOG.info("{}: Loaded {} variables.", getClass().getSimpleName(), getSet().size());
		return true;
	}
	
	@Override
	public boolean storeMe()
	{
		// No changes, nothing to store.
		if (!hasChanges())
		{
			return false;
		}
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement del = con.createStatement();
			PreparedStatement st = con.prepareStatement(INSERT_QUERY))
		{
			// Clear previous entries.
			del.execute(DELETE_QUERY);
			
			// Insert all variables.
			for (Entry<String, Object> entry : getSet().entrySet())
			{
				st.setString(1, entry.getKey());
				st.setString(2, String.valueOf(entry.getValue()));
				st.addBatch();
			}
			st.executeBatch();
		}
		catch (SQLException e)
		{
			LOG.warn("{}: Couldn't save global variables to database.", getClass().getSimpleName(), e);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		LOG.info("{}: Stored {} variables.", getClass().getSimpleName(), getSet().size());
		return true;
	}
	
	/**
	 * Gets the single instance of {@code GlobalVariablesManager}.
	 * @return single instance of {@code GlobalVariablesManager}
	 */
	public static final GlobalVariablesManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GlobalVariablesManager INSTANCE = new GlobalVariablesManager();
	}
}