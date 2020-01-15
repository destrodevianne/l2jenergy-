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
package com.l2jserver.gameserver.instancemanager.vote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.configuration.config.custom.TopsConfig;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.itemcontainer.Mail;

/**
 * @author Mobius, Мо3олЬ
 */
public class L2TopManager
{
	protected static final Logger LOG = LoggerFactory.getLogger(L2TopManager.class);
	
	private static final String SELECT_MULTIPLER_L2TOP_DATA = "SELECT multipler FROM character_votes WHERE id=? AND has_reward=0";
	private static final String UPDATE_L2TOP_DATA = "UPDATE character_votes SET has_reward=1 WHERE id=?";
	
	private final static String voteWeb = ServerConfig.DATAPACK_ROOT + "/data/l2top_vote-web.txt";
	private final static String voteSms = ServerConfig.DATAPACK_ROOT + "/data/l2top_vote-sms.txt";
	
	protected L2TopManager()
	{
		if (TopsConfig.L2_TOP_MANAGER_ENABLED)
		{
			load();
			LOG.info("L2TopManager: Started.");
		}
		else
		{
			LOG.info("L2TopManager: Engine is disabled.");
		}
	}
	
	public void load()
	{
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new ConnectAndUpdate(), TopsConfig.TOP_MANAGER_INTERVAL, TopsConfig.TOP_MANAGER_INTERVAL);
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Clean(), TopsConfig.TOP_MANAGER_INTERVAL, TopsConfig.TOP_MANAGER_INTERVAL);
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new GiveReward(), TopsConfig.TOP_MANAGER_INTERVAL, TopsConfig.TOP_MANAGER_INTERVAL);
	}
	
	public void update()
	{
		String out_sms = getPage(TopsConfig.L2_TOP_SMS_ADDRESS);
		String out_web = getPage(TopsConfig.L2_TOP_WEB_ADDRESS);
		File sms = new File(voteSms);
		File web = new File(voteWeb);
		FileWriter SaveWeb = null;
		FileWriter SaveSms = null;
		try
		{
			SaveSms = new FileWriter(sms);
			SaveSms.write(out_sms);
			SaveWeb = new FileWriter(web);
			SaveWeb.write(out_web);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String getPage(String address)
	{
		StringBuffer buf = new StringBuffer();
		Socket s;
		try
		{
			s = new Socket("l2top.ru", 80);
			s.setSoTimeout(30000);
			String request = "GET " + address + " HTTP/1.1\r\n" + "User-Agent: http:\\" + ServerConfig.GAMESERVER_HOSTNAME + " server\r\n" + "Host: http:\\" + ServerConfig.GAMESERVER_HOSTNAME + " \r\n" + "Accept: */*\r\n" + "Connection: close\r\n" + "\r\n";
			s.getOutputStream().write(request.getBytes());
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "Cp1251"));
			for (String line = in.readLine(); line != null; line = in.readLine())
			{
				buf.append(line);
				buf.append("\r\n");
			}
			s.close();
		}
		catch (Exception e)
		{
			buf.append("Connection error");
		}
		return buf.toString();
	}
	
	public void parse(boolean sms)
	{
		String nick = "";
		try (BufferedReader in = new BufferedReader(new FileReader(sms ? voteSms : voteWeb)))
		{
			String line = in.readLine();
			while (line != null)
			{
				Calendar cal = Calendar.getInstance();
				int year = cal.get(Calendar.YEAR);
				if (line.startsWith("" + year))
				{
					try
					{
						StringTokenizer st = new StringTokenizer(line, "- :\t");
						if (st.countTokens() == 7)
						{
							cal.set(Calendar.YEAR, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.MONTH, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.MINUTE, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.SECOND, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.MILLISECOND, 0);
							nick = st.nextToken();
						}
						else
						{
							cal.set(Calendar.YEAR, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.MONTH, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.MINUTE, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.SECOND, Integer.parseInt(st.nextToken()));
							cal.set(Calendar.MILLISECOND, 0);
							st.nextToken();
							nick = st.nextToken();
						}
						
						int mult = 1;
						
						if (sms)
						{
							mult = Integer.parseInt(new StringBuffer(st.nextToken()).delete(0, 1).toString());
						}
						if ((cal.getTimeInMillis() + (TopsConfig.TOP_SAVE_DAYS * 86400)) > (System.currentTimeMillis() / 1000))
						{
							DAOFactory.getInstance().getPlayerDAO().checkAndSave(cal.getTimeInMillis() / 1000, nick, mult);
						}
					}
					catch (NoSuchElementException nsee)
					{
						continue;
					}
				}
				line = in.readLine();
			}
		}
		catch (Exception e)
		{
			LOG.warn("", e);
		}
	}
	
	public void giveReward()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection())
		{
			for (L2PcInstance winner : L2World.getInstance().getPlayers())
			{
				int charId = winner.getObjectId();
				int mult = 0;
				PreparedStatement ps = con.prepareStatement(SELECT_MULTIPLER_L2TOP_DATA);
				ps.setInt(1, charId);
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						mult += rs.getInt("multipler");
					}
					
					PreparedStatement pst = con.prepareStatement(UPDATE_L2TOP_DATA);
					pst.setInt(1, charId);
					pst.executeUpdate();
					if (mult > 0)
					{
						L2PcInstance pl = (L2PcInstance) L2World.getInstance().findObject(charId);
						
						String text1 = "Reward L2Top raiting!"; // TODO: перенести в xml
						String text2 = "Thank you for your vote in L2Top raiting. Best regards " + TopsConfig.TOP_SERVER_ADDRESS; // TODO: перенести в xml
						Message msg = new Message(pl.getObjectId(), text1, text2, Message.SendBySystem.NEWS);
						Mail attachments = msg.createAttachments();
						attachments.addItem("L2Top", TopsConfig.L2_TOP_REWARD_ID, TopsConfig.L2_TOP_REWARD_COUNT * mult, null, null);
						MailManager.getInstance().sendMessage(msg);
						LOG.info("L2TOP: " + winner.getName() + "[charId:" + winner.getObjectId() + "]  item: [id:" + TopsConfig.L2_TOP_REWARD_ID + "count:" + (TopsConfig.L2_TOP_REWARD_COUNT * mult) + ']');
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
	}
	
	private class ConnectAndUpdate implements Runnable
	{
		public ConnectAndUpdate()
		{
			
		}
		
		@Override
		public void run()
		{
			update();
			parse(true);
			parse(false);
		}
	}
	
	private class Clean implements Runnable
	{
		public Clean()
		{
			
		}
		
		@Override
		public void run()
		{
			DAOFactory.getInstance().getPlayerDAO().clean();
		}
	}
	
	private class GiveReward implements Runnable
	{
		public GiveReward()
		{
			
		}
		
		@Override
		public void run()
		{
			giveReward();
		}
	}
	
	public static L2TopManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final L2TopManager _instance = new L2TopManager();
	}
}
