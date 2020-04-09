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

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.configuration.config.community.CBasicConfig;
import com.l2jserver.gameserver.configuration.config.community.CStatsConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Мо3олЬ TODO: add
 */
public class StatisticsBoard implements IParseBoardHandler
{
	public long update = System.currentTimeMillis() / 1000;
	
	private static final String[] COMMANDS =
	{
		"_bbsstatistic"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		if (!CStatsConfig.BBS_STATISTIC_ALLOW)
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbsstatistic", activeChar);
			return false;
		}
		
		String html = null;
		if (command.equals("_bbsstatistic"))
		{
			final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "statistic/index_stistic.html");
		}
		else if (command.equals("_bbsstatistic;pvp"))
		{
			return true;
		}
		else if (command.equals("_bbsstatistic;pk"))
		{
			return true;
		}
		else if (command.equals("_bbsstatistic;online"))
		{
			return true;
		}
		else if (command.equals("_bbsstatistic;rich"))
		{
			return true;
		}
		else if (command.equals("_bbsstatistic;rk"))
		{
			return true;
		}
		else if (command.equals("_bbsstatistic;cis"))
		{
			return true;
		}
		else if (command.equals("_bbsstatistic;cip"))
		{
			return true;
		}
		
		if ((update + (CStatsConfig.BBS_STATISTIC_UPDATE_TIME)) < (System.currentTimeMillis() / 1000))
		{
			
			update = System.currentTimeMillis() / 1000;
			LOG.info("Full statistics in the commynity board has been updated.");
		}
		
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}
	
	public void writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		
	}
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
}