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
package com.l2jserver.loginserver.status;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Util;
import com.l2jserver.loginserver.configuration.config.TelnetConfig;

public class Status extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger(Status.class);
	
	private final ServerSocket statusServerSocket;
	
	private final int _uptime;
	private String _statusPw;
	
	private final List<LoginStatusThread> _loginStatus;
	
	public Status() throws IOException
	{
		super("Status");
		_statusPw = TelnetConfig.TELNET_PASSWORD;
		
		if (_statusPw == null)
		{
			_statusPw = Util.randomPassword(10);
			LOG.info("Server's Telnet function has no password defined!");
			LOG.info("A password has been automatically created!");
			LOG.info("Password has been set to: {}", _statusPw);
		}
		
		statusServerSocket = new ServerSocket(TelnetConfig.TELNET_PORT);
		_uptime = (int) System.currentTimeMillis();
		_loginStatus = new LinkedList<>();
		LOG.info("Telnet server started successfully, listening on port {}.", TelnetConfig.TELNET_PORT);
	}
	
	@Override
	public void run()
	{
		setPriority(Thread.MAX_PRIORITY);
		
		while (!isInterrupted())
		{
			try
			{
				Socket connection = statusServerSocket.accept();
				LoginStatusThread lst = new LoginStatusThread(connection, _uptime, _statusPw);
				if (lst.isAlive())
				{
					_loginStatus.add(lst);
				}
				
				if (isInterrupted())
				{
					try
					{
						statusServerSocket.close();
					}
					catch (IOException ex)
					{
						LOG.warn("There has been an error closing status server socket!", ex);
					}
					break;
				}
			}
			catch (IOException e)
			{
				if (isInterrupted())
				{
					try
					{
						statusServerSocket.close();
					}
					catch (IOException ex2)
					{
						LOG.warn("There has been an error closing status server socket!", ex2);
					}
					break;
				}
			}
		}
	}
	
	public void sendMessageToTelnets(String msg)
	{
		for (LoginStatusThread ls : _loginStatus)
		{
			if (!ls.isInterrupted())
			{
				ls.printToTelnet(msg);
			}
		}
	}
}
