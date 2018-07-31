/*
 * Copyright (C) 2004-2018 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;

/**
 * @author Sacrifice
 */
public final class ScheduledAutoCleanCharacters
{
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledAutoCleanCharacters.class);
	
	private static final short MAX_STORED_DAYS = 45; // By default is 45 days
	
	public static void main(String[] args)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM characters WHERE lastAccess <= ? AND accessLevel = 0 AND online = 0"))
		{
			ps.setLong(1, System.currentTimeMillis() - (MAX_STORED_DAYS * 24L * 60L * 60L * 1000L));
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.warn("AutoCleanCharacters error delete:", e);
		}
		String sdf = new SimpleDateFormat("EEE MMM dd, yyyy' at 'HH:mm:ss").format(System.currentTimeMillis() - (MAX_STORED_DAYS * 24L * 60L * 60L * 1000L));
		LOG.info("Inactive players Deleted from: {}", sdf);
	}
}