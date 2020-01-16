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
package com.l2jserver.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.UPnPService;
import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.loginserver.configuration.config.EmailConfig;
import com.l2jserver.loginserver.configuration.config.LoginConfig;
import com.l2jserver.loginserver.configuration.config.MMOConfig;
import com.l2jserver.loginserver.configuration.config.TelnetConfig;
import com.l2jserver.loginserver.configuration.loader.ConfigLoader;
import com.l2jserver.loginserver.mail.MailSystem;
import com.l2jserver.loginserver.network.L2LoginClient;
import com.l2jserver.loginserver.network.L2LoginPacketHandler;
import com.l2jserver.loginserver.status.Status;
import com.l2jserver.mmocore.SelectorConfig;
import com.l2jserver.mmocore.SelectorThread;

/**
 * @author KenM
 */
public final class L2LoginServer
{
	private final static Logger LOG = LoggerFactory.getLogger(L2LoginServer.class);
	
	public static final int PROTOCOL_REV = 0x0106;
	private static final String BANNED_IPS = "./configuration/banned_ip.cfg";
	private static L2LoginServer _instance;
	private GameServerListener _gameServerListener;
	private SelectorThread<L2LoginClient> _selectorThread;
	private Status _statusServer;
	private Thread _restartLoginServer;
	
	public static void main(String[] args)
	{
		new L2LoginServer();
	}
	
	public static L2LoginServer getInstance()
	{
		return _instance;
	}
	
	private L2LoginServer()
	{
		_instance = this;
		
		new File("./logs/").mkdir();
		StringUtil.printSection("Config");
		// Initialize config
		ConfigLoader.loading();
		// Prepare Database
		ConnectionFactory.builder() //
			.withDriver(LoginConfig.DATABASE_DRIVER) //
			.withUrl(LoginConfig.DATABASE_URL) //
			.withUser(LoginConfig.DATABASE_LOGIN) //
			.withPassword(LoginConfig.DATABASE_PASSWORD) //
			.withConnectionPool(LoginConfig.DATABASE_CONNECTION_POOL) //
			.withMaxIdleTime(LoginConfig.DATABASE_MAX_IDLE_TIME) //
			.withMaxPoolSize(LoginConfig.DATABASE_MAX_CONNECTIONS) //
			.build();
		
		StringUtil.printSection("LoginController");
		LoginController.getInstance();
		
		StringUtil.printSection("GameServerTable");
		GameServerTable.getInstance();
		
		StringUtil.printSection("Ban List");
		loadBanFile();
		
		if (EmailConfig.EMAIL_SYS_ENABLED)
		{
			StringUtil.printSection("Mail System");
			MailSystem.getInstance();
		}
		
		StringUtil.printSection("IP, Ports & Socket infos");
		InetAddress bindAddress = null;
		if (!LoginConfig.LOGIN_BIND_ADDRESS.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(LoginConfig.LOGIN_BIND_ADDRESS);
			}
			catch (UnknownHostException e)
			{
				LOG.error("WARNING: The LoginServer bind address is invalid, using all avaliable IPs. Reason!", e);
			}
		}
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = MMOConfig.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = MMOConfig.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = MMOConfig.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = MMOConfig.MMO_HELPER_BUFFER_COUNT;
		
		final L2LoginPacketHandler lph = new L2LoginPacketHandler();
		final SelectorHelper sh = new SelectorHelper();
		try
		{
			_selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (IOException e)
		{
			LOG.error("FATAL: Failed to open Selector. Reason!", e);
			System.exit(1);
		}
		
		try
		{
			_gameServerListener = new GameServerListener();
			_gameServerListener.start();
			LOG.info("Listening for GameServers on {}: {}", LoginConfig.GAME_SERVER_LOGIN_HOST, LoginConfig.GAME_SERVER_LOGIN_PORT);
		}
		catch (IOException e)
		{
			LOG.error("FATAL: Failed to start the Game Server Listener. Reason!", e);
			System.exit(1);
		}
		
		if (TelnetConfig.IS_TELNET_ENABLED)
		{
			try
			{
				_statusServer = new Status();
				_statusServer.start();
			}
			catch (IOException ex)
			{
				LOG.warn("Failed to start the Telnet Server!", ex);
			}
		}
		else
		{
			LOG.info("Telnet server is currently disabled.");
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, LoginConfig.PORT_LOGIN);
			_selectorThread.start();
			LOG.info("{}: is now listening on: {}: {}", getClass().getSimpleName(), LoginConfig.LOGIN_BIND_ADDRESS, LoginConfig.PORT_LOGIN);
		}
		catch (IOException e)
		{
			LOG.error("FATAL: Failed to open server socket. Reason!", e);
			System.exit(1);
		}
		
		if (LoginConfig.ENABLE_UPNP)
		{
			StringUtil.printSection("UPnP");
			UPnPService.getInstance().load(LoginConfig.PORT_LOGIN, "L2J Login Server");
		}
		StringUtil.printSection("Waiting for gameserver answer");
	}
	
	public Status getStatusServer()
	{
		return _statusServer;
	}
	
	public GameServerListener getGameServerListener()
	{
		return _gameServerListener;
	}
	
	private void loadBanFile()
	{
		final File bannedFile = new File(BANNED_IPS);
		if (bannedFile.exists() && bannedFile.isFile())
		{
			try (FileInputStream fis = new FileInputStream(bannedFile);
				InputStreamReader is = new InputStreamReader(fis);
				LineNumberReader lnr = new LineNumberReader(is))
			{
				//@formatter:off
				lnr.lines()
					.map(String::trim)
					.filter(l -> !l.isEmpty() && (l.charAt(0) != '#'))
					.forEach(line -> {
						String[] parts = line.split("#", 2); // address[ duration][ # comments]
						line = parts[0];
						parts = line.split("\\s+"); // durations might be aligned via multiple spaces
						String address = parts[0];
						long duration = 0;
						
						if (parts.length > 1)
						{
							try
							{
								duration = Long.parseLong(parts[1]);
							}
							catch (NumberFormatException nfe)
							{
								LOG.warn("Skipped: Incorrect ban duration ({}) on ({}). Line: ",parts[1], bannedFile.getName(), lnr.getLineNumber());
								return;
							}
						}
						
						try
						{
							LoginController.getInstance().addBanForAddress(address, duration);
						}
						catch (UnknownHostException e)
						{
							LOG.warn("Skipped: Invalid address ({}) on ({}). Line: {}",address, bannedFile.getName(), lnr.getLineNumber());
						}
					});
				//@formatter:on
			}
			catch (IOException e)
			{
				LOG.warn("Error while reading the bans file ({}).", bannedFile.getName(), e);
			}
			LOG.info("Loaded {} IP Bans.", LoginController.getInstance().getBannedIps().size());
		}
		else
		{
			LOG.warn("IP Bans file ({}) is missing or is a directory, skipped.", bannedFile.getName());
		}
		
		if (LoginConfig.LOGIN_SERVER_SCHEDULE_RESTART)
		{
			LOG.info("Scheduled LS restart after {} hours", LoginConfig.LOGIN_SERVER_SCHEDULE_RESTART_TIME);
			_restartLoginServer = new LoginServerRestart();
			_restartLoginServer.setDaemon(true);
			_restartLoginServer.start();
		}
	}
	
	class LoginServerRestart extends Thread
	{
		public LoginServerRestart()
		{
			setName("LoginServerRestart");
		}
		
		@Override
		public void run()
		{
			while (!isInterrupted())
			{
				try
				{
					Thread.sleep(LoginConfig.LOGIN_SERVER_SCHEDULE_RESTART_TIME * 3600000);
				}
				catch (InterruptedException e)
				{
					return;
				}
				shutdown(true);
			}
		}
	}
	
	public void shutdown(boolean restart)
	{
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}
