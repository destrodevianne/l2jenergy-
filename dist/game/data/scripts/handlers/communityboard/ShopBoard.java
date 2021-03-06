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
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.DifferentMethods;

public class ShopBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbsshop"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		activeChar.setSessionVar("add_fav", null);
		
		if (!CBasicConfig.ENABLE_COMMUNITY_BOARD)
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbshome", activeChar);
			return false;
		}
		
		if (!DifferentMethods.checkFirstConditions(activeChar))
		{
			return false;
		}
		
		final String html;
		if (command.startsWith("_bbsshop"))
		{
			final String[] link = command.split(":");
			if (link[1].equals("open"))
			{
				final String[] data = command.split(";");
				final int listId = Integer.parseInt(link[2].split(";")[0]);
				if (data.length > 1)
				{
					parseCommunityBoardCommand(data[1], activeChar);
				}
				MultisellData.getInstance().separateAndSend(listId, activeChar, null, false);
				return false;
			}
			final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "shop/" + link[1] + ".html");
		}
		else
		{
			html = "";
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
