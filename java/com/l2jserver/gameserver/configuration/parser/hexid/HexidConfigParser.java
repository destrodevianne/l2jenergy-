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
package com.l2jserver.gameserver.configuration.parser.hexid;

import java.io.File;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.PropertiesParser;

/**
 * @author Мо3олЬ
 */
public class HexidConfigParser
{
	private static final Logger LOG = LoggerFactory.getLogger(HexidConfigParser.class);
	
	public static final String HEXID_FILE = "./configuration/hexid.txt";
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	public void load()
	{
		final File hexIdFile = new File(HEXID_FILE);
		if (hexIdFile.exists())
		{
			final PropertiesParser hexId = new PropertiesParser(hexIdFile);
			if (hexId.containskey("ServerID") && hexId.containskey("HexID"))
			{
				SERVER_ID = hexId.getInt("ServerID", 1);
				try
				{
					HEX_ID = new BigInteger(hexId.getString("HexID", null), 16).toByteArray();
				}
				catch (Exception e)
				{
					LOG.warn("Could not load HexID file ({}). Hopefully login will give us one.", HEXID_FILE);
				}
			}
			else
			{
				LOG.warn("Could not load HexID file ({}). Hopefully login will give us one.", HEXID_FILE);
			}
		}
		else
		{
			LOG.warn("Could not load HexID file ({}). Hopefully login will give us one.", HEXID_FILE);
		}
	}
	
	public void reload()
	{
		load();
	}
	
	public static HexidConfigParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HexidConfigParser INSTANCE = new HexidConfigParser();
	}
}
