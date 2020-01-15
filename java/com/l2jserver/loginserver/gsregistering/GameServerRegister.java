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
package com.l2jserver.loginserver.gsregistering;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.l2jserver.commons.dao.ServerNameDAO;
import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.Util;
import com.l2jserver.loginserver.GameServerTable;
import com.l2jserver.loginserver.configuration.loader.ConfigLoader;

public class GameServerRegister
{
	private static String _choice;
	private static boolean _choiceOk;
	
	public static void main(String[] args) throws IOException
	{
		ConfigLoader.loading();
		
		LineNumberReader _in = new LineNumberReader(new InputStreamReader(System.in));
		GameServerTable gameServerTable = GameServerTable.getInstance();
		System.out.println("Welcome to L2jEnergy gameserver registering.");
		System.out.println("Enter the ID of the server you want to register.");
		System.out.println("-- Type 'help' to get a list of IDs.");
		System.out.println("-- Type 'clean' to unregister all registered gameservers from this LoginServer.");
		while (!_choiceOk)
		{
			System.out.println("Your choice:");
			_choice = _in.readLine();
			if (_choice.equalsIgnoreCase("help"))
			{
				for (Map.Entry<Integer, String> entry : ServerNameDAO.getServers().entrySet())
				{
					System.out.println("Server ID: " + entry.getKey() + "\t- " + entry.getValue() + " - In Use: " + (gameServerTable.hasRegisteredGameServerOnId(entry.getKey()) ? "YES" : "NO"));
				}
				System.out.println("You can also see 'servername.xml'.");
			}
			else if (_choice.equalsIgnoreCase("clean"))
			{
				System.out.println("This is going to UNREGISTER ALL servers from this LoginServer. Are you sure? (y/n) ");
				_choice = _in.readLine();
				if (_choice.equals("y"))
				{
					GameServerRegister.cleanRegisteredGameServersFromDB();
					gameServerTable.getRegisteredGameServers().clear();
				}
			}
			else
			{
				try
				{
					if (ServerNameDAO.getServers().isEmpty())
					{
						System.out.println("No server names available, be sure 'servername.xml' is in the LoginServer directory.");
						System.exit(1);
					}
					
					final int id = Integer.parseInt(_choice);
					if (ServerNameDAO.getServer(id) == null)
					{
						System.out.println("No name for id: " + id);
						continue;
					}
					
					if (gameServerTable.hasRegisteredGameServerOnId(id))
					{
						System.out.println("This ID isn't available.");
					}
					else
					{
						byte[] hexId = Util.generateHex(16);
						
						gameServerTable.registerServerOnDB(hexId, id, "");
						Util.saveHexid(id, new BigInteger(hexId).toString(16), "hexid.txt");
						System.out.println("Server registered. Its hexid is saved to 'hexid.txt'.");
						System.out.println("Put this file in the /config folder of your gameserver'.");
						return;
					}
				}
				catch (NumberFormatException nfe)
				{
					System.out.println("Type a number or 'help'.");
				}
			}
		}
	}
	
	public static void cleanRegisteredGameServersFromDB()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM gameservers");
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			System.out.println("SQL error while cleaning registered servers: " + e);
		}
	}
}