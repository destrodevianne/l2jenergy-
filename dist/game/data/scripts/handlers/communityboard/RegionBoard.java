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
package handlers.communityboard;

import java.util.List;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.util.Util;

/**
 * Region board.
 * @author Zoey76
 */
public class RegionBoard implements IWriteBoardHandler
{
	// Region data
	// @formatter:off
	private static final int[] REGIONS = { 1049, 1052, 1053, 1057, 1060, 1059, 1248, 1247, 1056 };
	// @formatter:on
	private static final String[] COMMANDS =
	{
		"_bbsloc",
		"_bbsregsearch",
		"_bbsreglist_"
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
		if ("bbsloc".equals(cmd))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Region", command);
			
			final String list = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_regiontpl.html");
			final StringBuilder sb = new StringBuilder();
			final List<Castle> castles = CastleManager.getInstance().getCastles();
			for (int i = 0; i < REGIONS.length; i++)
			{
				final Castle castle = castles.get(i);
				final L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
				String link = list.replaceAll("%region_id%", String.valueOf(i));
				link = link.replace("%region_name%", String.valueOf(REGIONS[i]));
				link = link.replace("%region_owning_clan%", (clan != null ? clan.getName() : "NPC"));
				link = link.replace("%region_owning_clan_alliance%", ((clan != null) && (clan.getAllyName() != null) ? clan.getAllyName() : ""));
				link = link.replace("%region_tax_rate%", String.valueOf(castle.getTaxRate() * 100) + "%");
				sb.append(link);
			}
			
			String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/bbs_region_list.html");
			html = html.replace("%region_list%", sb.toString());
			CommunityBoardHandler.separateAndSend(html, activeChar);
		}
		else if ("bbsloc".equals(cmd))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Region>", command);
			
			final String id = command.replace("_bbsloc;", "");
			if (!Util.isDigit(id))
			{
				LOG.warn("{}: Player {} sent and invalid region bypass {}!", RegionBoard.class.getSimpleName(), activeChar, command);
				return false;
			}
			// TODO: Implement.
		}
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(arg1, "_");
		String cmd = st.nextToken();
		if ("bbsregsearch".equals(cmd))
		{
			int townId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			String byItem = "Item".equals(arg4) ? "1" : "0";
			if (arg3 == null)
			{
				arg3 = "";
			}
			
			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");
			
			if (arg3.length() > 30)
			{
				arg3 = arg3.substring(0, 30);
			}
			
			parseCommunityBoardCommand("_bbsreglist_" + townId + "_" + type + "_1_" + byItem + "_" + arg3, activeChar);
		}
		return false;
	}
}
