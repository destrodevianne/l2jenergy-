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
package handlers.communityboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;

/**
 * Memo board.
 * @author Zoey76
 */
public class MemoBoard implements IWriteBoardHandler
{
	private static final int MEMO_PER_PAGE = 12;
	
	private static final String[] COMMANDS =
	{
		"_bbsmemo",
		"_mmread_",
		"_mmlist_",
		"_mmcrea",
		"_mmwrite",
		"_mmmodi_",
		"_mmdele"
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
		
		CommunityBoardHandler.getInstance().addBypass(activeChar, "Memo Command", command);
		
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_memo_list.html");
		if ("bbsmemo".equals(cmd) || "_mmlist_1".equals(command))
		{
			int count = getMemoCount(activeChar);
			html = html.replace("%memo_list%", getMemoList(activeChar, 1, count));
			html = html.replace("%prev_page%", "");
			html = html.replace("%page%", "1");
			String pages = "<td>1</td>\n\n";
			if (count > MEMO_PER_PAGE)
			{
				int pgs = count / MEMO_PER_PAGE;
				if ((count % MEMO_PER_PAGE) != 0)
				{
					pgs++;
				}
				
				html = html.replace("%next_page%", "bypass _mmlist_2");
				for (int i = 2; i <= pgs; i++)
				{
					pages += "<td><a action=\"bypass _mmlist_" + i + "\"> " + i + " </a></td>\n\n";
				}
			}
			else
			{
				html = html.replace("%next_page%", "");
			}
			
			html = html.replace("%pages%", pages);
		}
		else if ("mmlist".equals(cmd))
		{
			int currPage = Integer.parseInt(st.nextToken());
			int count = getMemoCount(activeChar);
			html = html.replace("%memo_list%", getMemoList(activeChar, currPage, count));
			html = html.replace("%prev_page%", "bypass _mmlist_" + (currPage - 1));
			html = html.replace("%page%", String.valueOf(currPage));
			
			String pages = "";
			int pgs = count / MEMO_PER_PAGE;
			if ((count % MEMO_PER_PAGE) != 0)
			{
				pgs++;
			}
			
			if (count > (currPage * MEMO_PER_PAGE))
			{
				html = html.replace("%next_page%", "bypass _mmlist_" + (currPage + 1));
			}
			else
			{
				html = html.replace("%next_page%", "");
			}
			
			for (int i = 1; i <= pgs; i++)
			{
				if (i == currPage)
				{
					pages += "<td>" + i + "</td>\n\n";
				}
				else
				{
					pages += "<td height=15><a action=\"bypass _mmlist_" + i + "\"> " + i + " </a></td>\n\n";
				}
			}
			
			html = html.replace("%pages%", pages);
		}
		else if ("mmcrea".equals(cmd))
		{
			if (getMemoCount(activeChar) >= 100)
			{
				// activeChar.sendPacket(new SystemMessage(SystemMessage.MEMO_BOX_IS_FULL_100_MEMO_MAXIMUM));
				activeChar.sendMessage("");
				parseCommunityBoardCommand("_mmlist_1", activeChar);
				return false;
			}
			
			String page = st.nextToken();
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_memo_edit.html");
			html = html.replace("%page%", page);
			html = html.replace("%memo_id%", "0");
			html = html.replace("%TREE%", "&nbsp;>&nbsp;Создание записки");
			activeChar.sendPacket(new ShowBoard(html, "1001"));
			List<String> args = new ArrayList<>();
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("");
			args.add("0");
			args.add("");
			args.add("0");
			args.add("");
			args.add("");
			args.add("");
			// args.add(String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(0)));
			args.add("1970-01-01 00:00:00 ");
			args.add("1970-01-01 00:00:00 ");
			args.add("0");
			args.add("0");
			args.add("");
			activeChar.sendPacket(new ShowBoard(args));
			return false;
		}
		else if ("mmread".equals(cmd))
		{
			int memoId = Integer.parseInt(st.nextToken());
			String page = st.nextToken();
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM `bbs_memo` WHERE `account_name` = ? and memo_id = ?"))
			{
				ps.setString(1, activeChar.getAccountName());
				ps.setInt(2, memoId);
				try (ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						String post = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_memo_read.html");
						post = post.replace("%title%", rs.getString("title"));
						post = post.replace("%char_name%", rs.getString("char_name"));
						post = post.replace("%post_date%", String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(rs.getInt("post_date") * 1000L)));
						post = post.replace("%memo%", rs.getString("memo").replace("\n", "<br1>"));
						post = post.replace("%page%", page);
						post = post.replace("%memo_id%", String.valueOf(memoId));
						CommunityBoardHandler.separateAndSend(post, activeChar);
						return false;
					}
				}
			}
			catch (Exception e)
			{
			}
			parseCommunityBoardCommand("_bbsmemo", activeChar);
			return false;
		}
		else if ("mmdele".equals(cmd))
		{
			int memoId = Integer.parseInt(st.nextToken());
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM `bbs_memo` WHERE `account_name` = ? and memo_id = ?"))
			{
				ps.setString(1, activeChar.getAccountName());
				ps.setInt(2, memoId);
				ps.execute();
			}
			catch (Exception e)
			{
			}
			parseCommunityBoardCommand("_mmlist_1", activeChar);
			return false;
		}
		else if ("mmmodi".equals(cmd))
		{
			int memoId = Integer.parseInt(st.nextToken());
			String page = st.nextToken();
			
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM `bbs_memo` WHERE `account_name` = ? and memo_id = ?"))
			{
				ps.setString(1, activeChar.getAccountName());
				ps.setInt(2, memoId);
				try (ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_memo_edit.html");
						html = html.replace("%page%", page);
						html = html.replace("%memo_id%", String.valueOf(memoId));
						html = html.replace("%TREE%", "&nbsp;>&nbsp;<a action=\"bypass _mmread_" + memoId + "_" + page + "\">Записка: " + rs.getString("title") + "</a>&nbsp;>&nbsp;Редактирование");
						activeChar.sendPacket(new ShowBoard(html, "1001"));
						List<String> args = new ArrayList<>();
						args.add("0");
						args.add("0");
						args.add(String.valueOf(memoId));
						args.add("0");
						args.add("0");
						args.add("0"); // account data ?
						args.add(activeChar.getName());
						args.add("0"); // account data ?
						args.add(activeChar.getAccountName());
						args.add("0"); // account data ?
						args.add(rs.getString("title"));
						args.add(rs.getString("title"));
						args.add(rs.getString("memo"));
						args.add(String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(rs.getInt("post_date") * 1000L)));
						args.add(String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date(rs.getInt("post_date") * 1000L)));
						args.add("0");
						args.add("0");
						args.add("");
						activeChar.sendPacket(new ShowBoard(args));
						return false;
					}
				}
			}
			catch (Exception e)
			{
			}
			parseCommunityBoardCommand("_mmlist_" + page, activeChar);
			return false;
		}
		
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String command, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if ("mmwrite".equals(cmd))
		{
			if (getMemoCount(activeChar) >= 100)
			{
				// activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.MEMO_BOX_IS_FULL_100_MEMO_MAXIMUM));
				activeChar.sendMessage("");
				parseCommunityBoardCommand("_mmlist_1", activeChar);
				return false;
			}
			
			if ((arg3 != null) && !arg3.isEmpty() && (arg4 != null) && !arg4.isEmpty())
			{
				String title = arg3.replace("<", "");
				title = title.replace(">", "");
				title = title.replace("&", "");
				title = title.replace("$", "");
				if (title.length() > 128)
				{
					title = title.substring(0, 128);
				}
				String memo = arg4.replace("<", "");
				memo = memo.replace(">", "");
				memo = memo.replace("&", "");
				memo = memo.replace("$", "");
				if (memo.length() > 1000)
				{
					memo = memo.substring(0, 1000);
				}
				
				int memoId = 0;
				if ((arg2 != null) && !arg2.isEmpty())
				{
					memoId = Integer.parseInt(arg2);
				}
				
				if ((title.length() > 0) && (memo.length() > 0))
				{
					
					try (Connection con = ConnectionFactory.getInstance().getConnection();
						PreparedStatement ps1 = con.prepareStatement("UPDATE bbs_memo SET title = ?, memo = ? WHERE memo_id = ? AND account_name = ?");
						PreparedStatement ps2 = con.prepareStatement("INSERT INTO bbs_memo(account_name, char_name, ip, title, memo, post_date) VALUES(?, ?, ?, ?, ?, ?)"))
					{
						if (memoId > 0)
						{
							ps1.setString(1, title);
							ps1.setString(2, memo);
							ps1.setInt(3, memoId);
							ps1.setString(4, activeChar.getAccountName());
							ps1.execute();
						}
						else
						{
							ps2.setString(1, activeChar.getAccountName());
							ps2.setString(2, activeChar.getName());
							ps2.setString(3, activeChar.getIPAddress());
							ps2.setString(4, title);
							ps2.setString(5, memo);
							ps2.setInt(6, (int) (System.currentTimeMillis() / 1000));
							ps2.execute();
						}
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		parseCommunityBoardCommand("_bbsmemo", activeChar);
		return false;
	}
	
	private static String getMemoList(L2PcInstance activeChar, int page, int count)
	{
		StringBuilder memoList = new StringBuilder("");
		int start = (page - 1) * MEMO_PER_PAGE;
		int end = page * MEMO_PER_PAGE;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT memo_id,title,post_date FROM `bbs_memo` WHERE `account_name` = ? ORDER BY post_date DESC LIMIT " + start + "," + end))
		{
			if (count > 0)
			{
				ps.setString(1, activeChar.getAccountName());
				try (ResultSet rs = ps.executeQuery())
				{
					String tpl = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_memo_post.html");
					while (rs.next())
					{
						String post = tpl;
						post = post.replace("%memo_id%", String.valueOf(rs.getInt("memo_id")));
						post = post.replace("%memo_title%", rs.getString("title"));
						post = post.replace("%page%", String.valueOf(page));
						post = post.replace("%memo_date%", String.format("%1$te-%1$tm-%1$tY", new Date(rs.getInt("post_date") * 1000L)));
						memoList.append(post);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return memoList.toString();
	}
	
	private static int getMemoCount(L2PcInstance activeChar)
	{
		int count = 0;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT count(*) as cnt FROM bbs_memo WHERE `account_name` = ?"))
		{
			ps.setString(1, activeChar.getAccountName());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					count = rs.getInt("cnt");
				}
			}
		}
		catch (Exception e)
		{
		}
		return count;
	}
}
