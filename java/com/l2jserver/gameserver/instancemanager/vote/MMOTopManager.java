/*
 * Copyright (C) 2004-2019 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager.vote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.Config;
import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.itemcontainer.Mail;

/**
 * @author Mobius, Мо3олЬ
 */
public class MMOTopManager
{
	protected static final Logger LOG = LoggerFactory.getLogger(MMOTopManager.class);
	
	private static final String SELECT_MULTIPLER_MMOTOP_DATA = "SELECT multipler FROM character_votes WHERE id=? AND has_reward=0";
	private static final String UPDATE_MMOTOP_DATA = "UPDATE character_votes SET has_reward=1 WHERE id=?";
	
	BufferedReader reader;
	
	protected MMOTopManager()
	{
		if (Config.MMO_TOP_MANAGER_ENABLED)
		{
			load();
			LOG.info("MMOTopManager: Started.");
		}
		else
		{
			LOG.info("MMOTopManager: Engine is disabled.");
		}
	}
	
	public void load()
	{
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new ConnectAndUpdate(), Config.TOP_MANAGER_INTERVAL, Config.TOP_MANAGER_INTERVAL);
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Clean(), Config.TOP_MANAGER_INTERVAL, Config.TOP_MANAGER_INTERVAL);
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new GiveReward(), Config.TOP_MANAGER_INTERVAL, Config.TOP_MANAGER_INTERVAL);
	}
	
	public void getPage(String address)
	{
		try
		{
			URL url = new URL(address);
			reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF8"));
		}
		catch (Exception e)
		{
			LOG.warn("", e);
		}
	}
	
	public void parse()
	{
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\t. :");
				while (st.hasMoreTokens())
				{
					try
					{
						st.nextToken();
						int day = Integer.parseInt(st.nextToken());
						int month = Integer.parseInt(st.nextToken()) - 1;
						int year = Integer.parseInt(st.nextToken());
						int hour = Integer.parseInt(st.nextToken());
						int minute = Integer.parseInt(st.nextToken());
						int second = Integer.parseInt(st.nextToken());
						st.nextToken();
						st.nextToken();
						st.nextToken();
						st.nextToken();
						String charName = st.nextToken();
						int voteType = Integer.parseInt(st.nextToken());
						Calendar calendar = Calendar.getInstance();
						calendar.set(1, year);
						calendar.set(2, month);
						calendar.set(5, day);
						calendar.set(11, hour);
						calendar.set(12, minute);
						calendar.set(13, second);
						calendar.set(14, 0);
						long voteTime = calendar.getTimeInMillis() / 1000;
						if ((voteTime + (Config.TOP_SAVE_DAYS * 86400)) > (System.currentTimeMillis() / 1000))
						{
							DAOFactory.getInstance().getPlayerDAO().checkAndSave(voteTime, charName, voteType);
						}
					}
					catch (Exception e)
					{
						LOG.warn("", e);
					}
				}
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
				PreparedStatement ps = con.prepareStatement(SELECT_MULTIPLER_MMOTOP_DATA);
				ps.setInt(1, charId);
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						mult += rs.getInt("multipler");
					}
					
					PreparedStatement pst = con.prepareStatement(UPDATE_MMOTOP_DATA);
					pst.setInt(1, charId);
					pst.executeUpdate();
					
					if (mult > 0)
					{
						L2PcInstance pl = (L2PcInstance) L2World.getInstance().findObject(charId);
						
						String text1 = "Reward MMOTop raiting!"; // TODO: перенести в xml
						String text2 = "Thank you for your vote in MMOTop raiting. Best regards " + Config.TOP_SERVER_ADDRESS; // TODO: перенести в xml
						Message msg = new Message(pl.getObjectId(), text1, text2, Message.SendBySystem.NEWS);
						Mail attachments = msg.createAttachments();
						attachments.addItem("MMOTop", Config.MMO_TOP_REWARD_ID, Config.MMO_TOP_REWARD_COUNT * mult, null, null);
						MailManager.getInstance().sendMessage(msg);
						LOG.info("MMOTOP: " + winner.getName() + "[charId:" + winner.getObjectId() + "]  item: [id:" + Config.MMO_TOP_REWARD_ID + "count:" + (Config.MMO_TOP_REWARD_COUNT * mult) + ']');
					}
				}
			}
		}
		catch (Exception e)
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
			getPage(Config.MMO_TOP_WEB_ADDRESS);
			parse();
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
	
	public static MMOTopManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final MMOTopManager _instance = new MMOTopManager();
	}
}
