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
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.gameeventengine.GameEventManager;

/**
 * Event board.
 * @author Мо3олЬ
 */
public class EventBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbsevents"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		String html = null;
		if (command.equals("_bbsevents"))
		{
			final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/index.html");
		}
		else if (command.startsWith("_bbsevents:page"))
		{
			final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
			final String[] path = command.split(":");
			if (path.length > 3)
			{
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/" + path[2] + "/" + path[3] + ".html");
			}
			else
			{
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/" + path[2] + ".html");
			}
		}
		else if (command.equalsIgnoreCase("_bbsevents:tvt_reg"))
		{
			if (GameEventManager.getInstance().getEvent().register(activeChar))
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_event_you_reg_for_event"));
			}
		}
		else if (command.equalsIgnoreCase("_bbsevents:tvt_unreg"))
		{
			if (GameEventManager.getInstance().getEvent().unRegister(activeChar))
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_event_you_unregistered_for_event"));
			}
		}
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
}
