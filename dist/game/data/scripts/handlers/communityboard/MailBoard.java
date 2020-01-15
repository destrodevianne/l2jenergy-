/*
 * Copyright (C) 2004-2020 L2J DataPack
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
package handlers.communityboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.annotations.RegisterEvent;
import com.l2jserver.gameserver.model.events.annotations.RegisterType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExMailArrived;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.bbs.MailData;

/**
 * Mail board.
 * @author Zoey76
 */
public class MailBoard implements IWriteBoardHandler
{
	// SQL Queries
	private static final String SELECT_MAIL = "SELECT * FROM `bbs_mail` WHERE `box_type` = 0 and `read` = 0 and `to_object_id` = ?";
	private static final String DELETE_MAIL = "DELETE FROM `bbs_mail` WHERE `to_object_id` = ? and `post_date` < ?";
	private static final String SELECT_MAIL2 = "SELECT * FROM `bbs_mail` WHERE `box_type` = ? and `to_object_id` = ? ORDER BY post_date DESC";
	
	private static final int MESSAGE_PER_PAGE = 10;
	
	private static final String[] COMMANDS =
	{
		"_maillist_",
		"_mailsearch_",
		"_mailread_",
		"_maildelete_"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		activeChar.setSessionVar("add_fav", null);
		if ("maillist".equals(cmd))
		{
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int byTitle = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken() : "";
			
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Mail Command", command);
			String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_mail_list.html");
			
			int inbox = 0;
			int send = 0;
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps1 = con.prepareStatement("SELECT count(*) as cnt FROM `bbs_mail` WHERE `box_type` = 0 and `to_object_id` = ?");
				PreparedStatement ps2 = con.prepareStatement("SELECT count(*) as cnt FROM `bbs_mail` WHERE `box_type` = 1 and `from_object_id` = ?"))
			{
				ps1.setInt(1, activeChar.getObjectId());
				try (ResultSet rs = ps1.executeQuery())
				{
					if (rs.next())
					{
						inbox = rs.getInt("cnt");
					}
				}
				
				ps2.setInt(1, activeChar.getObjectId());
				try (ResultSet rs = ps2.executeQuery())
				{
					if (rs.next())
					{
						send = rs.getInt("cnt");
					}
				}
			}
			catch (Exception e)
			{
			}
			
			List<MailData> mailList = null;
			
			switch (type)
			{
				case 0:
					html = html.replace("%inbox_link%", "[&$917;]");
					html = html.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
					html = html.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
					html = html.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
					html = html.replace("%TREE%", "&$917;");
					html = html.replace("%writer_header%", "&$911;");
					mailList = getMailList(activeChar, type, search, byTitle == 1);
					break;
				case 1:
					html = html.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
					html = html.replace("%sentbox_link%", "[&$918;]");
					html = html.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
					html = html.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
					html = html.replace("%TREE%", "&$918;");
					html = html.replace("%writer_header%", "&$909;");
					mailList = getMailList(activeChar, type, search, byTitle == 1);
					break;
				case 2:
					html = html.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
					html = html.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
					html = html.replace("%archive_link%", "[&$919;]");
					html = html.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
					html = html.replace("%TREE%", "&$919;");
					html = html.replace("%writer_header%", "&$911;");
					break;
				case 3:
					html = html.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
					html = html.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
					html = html.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
					html = html.replace("%temp_archive_link%", "[&$920;]");
					html = html.replace("%TREE%", "&$920;");
					html = html.replace("%writer_header%", "&$909;");
					break;
			}
			
			if (mailList != null)
			{
				int start = (page - 1) * MESSAGE_PER_PAGE;
				int end = Math.min(page * MESSAGE_PER_PAGE, mailList.size());
				
				if (page == 1)
				{
					html = html.replace("%ACTION_GO_LEFT%", "");
					html = html.replace("%GO_LIST%", "");
					html = html.replace("%NPAGE%", "1");
				}
				else
				{
					html = html.replace("%ACTION_GO_LEFT%", "bypass _maillist_" + type + "_" + (page - 1) + "_" + byTitle + "_" + search);
					html = html.replace("%NPAGE%", String.valueOf(page));
					StringBuilder goList = new StringBuilder("");
					for (int i = page > 10 ? page - 10 : 1; i < page; i++)
					{
						goList.append("<td><a action=\"bypass _maillist_").append(type).append("_").append(i).append("_").append(byTitle).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");
					}
					
					html = html.replace("%GO_LIST%", goList.toString());
				}
				
				int pages = Math.max(mailList.size() / MESSAGE_PER_PAGE, 1);
				if (mailList.size() > (pages * MESSAGE_PER_PAGE))
				{
					pages++;
				}
				
				if (pages > page)
				{
					html = html.replace("%ACTION_GO_RIGHT%", "bypass _maillist_" + type + "_" + (page + 1) + "_" + byTitle + "_" + search);
					int ep = Math.min(page + 10, pages);
					StringBuilder goList = new StringBuilder("");
					for (int i = page + 1; i <= ep; i++)
					{
						goList.append("<td><a action=\"bypass _maillist_").append(type).append("_").append(i).append("_").append(byTitle).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");
					}
					
					html = html.replace("%GO_LIST2%", goList.toString());
				}
				else
				{
					html = html.replace("%ACTION_GO_RIGHT%", "");
					html = html.replace("%GO_LIST2%", "");
				}
				
				StringBuilder ml = new StringBuilder("");
				final String tpl = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_mailtpl.html");
				for (int i = start; i < end; i++)
				{
					MailData md = mailList.get(i);
					String mailtpl = tpl;
					mailtpl = mailtpl.replace("%action%", "bypass _mailread_" + md.getMessageId() + "_" + type + "_" + page + "_" + byTitle + "_" + search);
					mailtpl = mailtpl.replace("%writer%", md.getAuthor());
					mailtpl = mailtpl.replace("%title%", md.getTitle());
					mailtpl = mailtpl.replace("%post_date%", md.getPostDate());
					ml.append(mailtpl);
				}
				
				html = html.replace("%MAIL_LIST%", ml.toString());
			}
			else
			{
				html = html.replace("%ACTION_GO_LEFT%", "");
				html = html.replace("%GO_LIST%", "");
				html = html.replace("%NPAGE%", "1");
				html = html.replace("%GO_LIST2%", "");
				html = html.replace("%ACTION_GO_RIGHT%", "");
				html = html.replace("%MAIL_LIST%", "");
			}
			
			html = html.replace("%mailbox_type%", String.valueOf(type));
			html = html.replace("%incomming_mail_no%", String.valueOf(inbox));
			html = html.replace("%sent_mail_no%", String.valueOf(send));
			html = html.replace("%archived_mail_no%", "0");
			html = html.replace("%temp_mail_no%", "0");
			
			CommunityBoardHandler.separateAndSend(html, activeChar);
		}
		else if ("mailread".equals(cmd))
		{
			int messageId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int byTitle = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken() : "";
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps1 = con.prepareStatement("SELECT * FROM `bbs_mail` WHERE `message_id` = ? and `box_type` = ? and `to_object_id` = ?");
				PreparedStatement ps2 = con.prepareStatement("UPDATE `bbs_mail` SET `read` = `read` + 1 WHERE message_id = ?"))
			{
				ps1.setInt(1, messageId);
				ps1.setInt(2, type);
				ps1.setInt(3, activeChar.getObjectId());
				try (ResultSet rs = ps1.executeQuery())
				{
					if (rs.next())
					{
						String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_mail_read.html");
						
						switch (type)
						{
							case 0:
								html = html.replace("%TREE%", "<a action=\"bypass _maillist_0_1_0_\">&$917;</a>");
								break;
							case 1:
								html = html.replace("%TREE%", "<a action=\"bypass _maillist_1_1_0__\">&$918;</a>");
								break;
							case 2:
								html = html.replace("%TREE%", "<a action=\"bypass _maillist_2_1_0__\">&$919;</a>");
								break;
							case 3:
								html = html.replace("%TREE%", "<a action=\"bypass _maillist_3_1_0__\">&$920;</a>");
								break;
						}
						
						html = html.replace("%writer%", rs.getString("from_name"));
						html = html.replace("%post_date%", String.format("%1$te-%1$tm-%1$tY", new Date(rs.getInt("post_date") * 1000L)));
						html = html.replace("%del_date%", String.format("%1$te-%1$tm-%1$tY", new Date((rs.getInt("post_date") + (90 * 24 * 60 * 60)) * 1000L)));
						html = html.replace("%char_name%", rs.getString("to_name"));
						html = html.replace("%title%", rs.getString("title"));
						html = html.replace("%CONTENT%", rs.getString("message").replace("\n", "<br1>"));
						html = html.replace("%GOTO_LIST_LINK%", "bypass _maillist_" + type + "_" + page + "_" + byTitle + "_" + search);
						html = html.replace("%message_id%", String.valueOf(messageId));
						html = html.replace("%mailbox_type%", String.valueOf(type));
						activeChar.setSessionVar("add_fav", command + "&" + rs.getString("title"));
						
						ps2.setInt(1, messageId);
						ps2.execute();
						
						CommunityBoardHandler.separateAndSend(html, activeChar);
						return false;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			parseCommunityBoardCommand("_maillist_" + type + "_" + page + "_" + byTitle + "_" + search, activeChar);
		}
		else if ("maildelete".equals(cmd))
		{
			int type = Integer.parseInt(st.nextToken());
			int messageId = Integer.parseInt(st.nextToken());
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM `bbs_mail` WHERE `box_type` = ? and `message_id` = ? and `to_object_id` = ?"))
			{
				ps.setInt(1, type);
				ps.setInt(2, messageId);
				ps.setInt(3, activeChar.getObjectId());
				ps.execute();
			}
			catch (Exception e)
			{
			}
			parseCommunityBoardCommand("_maillist_" + type + "_1_0_", activeChar);
		}
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(arg1, "_");
		String cmd = st.nextToken();
		if ("mailsearch".equals(cmd))
		{
			parseCommunityBoardCommand("_maillist_" + st.nextToken() + "_1_" + ("Title".equals(arg3) ? "1_" : "0_") + (arg5 != null ? arg5 : ""), activeChar);
		}
		return false;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public static void OnPlayerEnter(L2PcInstance activeChar)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MAIL))
		{
			ps.setInt(1, activeChar.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NEW_MAIL));
					activeChar.sendPacket(ExMailArrived.STATIC_PACKET);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static List<MailData> getMailList(L2PcInstance activeChar, int type, String search, boolean byTitle)
	{
		List<MailData> list = new ArrayList<>();
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps1 = con.prepareStatement(DELETE_MAIL);
			PreparedStatement ps2 = con.prepareStatement(SELECT_MAIL2))
		{
			ps1.setInt(1, activeChar.getObjectId());
			ps1.setInt(2, (int) (System.currentTimeMillis() / 1000) - (90 * 24 * 60 * 60));
			ps1.execute();
			
			String column_name = type == 0 ? "from_name" : "to_name";
			ps2.setInt(1, type);
			ps2.setInt(2, activeChar.getObjectId());
			try (ResultSet rs = ps2.executeQuery())
			{
				while (rs.next())
				{
					if (search.isEmpty())
					{
						list.add(new MailData(rs.getString(column_name), rs.getString("title"), rs.getInt("post_date"), rs.getInt("message_id")));
					}
					else if (byTitle && !search.isEmpty() && rs.getString("title").toLowerCase().contains(search.toLowerCase()))
					{
						list.add(new MailData(rs.getString(column_name), rs.getString("title"), rs.getInt("post_date"), rs.getInt("message_id")));
					}
					else if (!byTitle && !search.isEmpty() && rs.getString(column_name).toLowerCase().contains(search.toLowerCase()))
					{
						list.add(new MailData(rs.getString(column_name), rs.getString("title"), rs.getInt("post_date"), rs.getInt("message_id")));
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}
