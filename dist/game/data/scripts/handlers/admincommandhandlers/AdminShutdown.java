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
package handlers.admincommandhandlers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.Shutdown;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.DifferentMethods;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.MemoryWatchDog;

public class AdminShutdown implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_server_shutdown",
		"admin_server_restart",
		"admin_server_abort"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_server_shutdown"))
		{
			try
			{
				final String val = command.substring(22);
				if (Util.isDigit(val))
				{
					serverShutdown(activeChar, Integer.valueOf(val), false);
				}
				else
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_server_shutdown"));
					AdminHtml.showAdminHtml(activeChar, "shutdown.htm");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				AdminHtml.showAdminHtml(activeChar, "shutdown.htm");
			}
		}
		else if (command.startsWith("admin_server_restart"))
		{
			try
			{
				final String val = command.substring(21);
				if (Util.isDigit(val))
				{
					serverShutdown(activeChar, Integer.parseInt(val), true);
				}
				else
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_server_restart"));
					AdminHtml.showAdminHtml(activeChar, "shutdown.htm");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				AdminHtml.showAdminHtml(activeChar, "shutdown.htm");
			}
		}
		else if (command.startsWith("admin_server_abort"))
		{
			serverAbort(activeChar);
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/shutdown.htm");
		html.replace("%onlineAll%", String.valueOf(DifferentMethods.getPlayersCount("ALL")));
		html.replace("%offlineTrade%", String.valueOf(DifferentMethods.getPlayersCount("OFF_TRADE")));
		html.replace("%onlineGM%", String.valueOf(DifferentMethods.getPlayersCount("GM")));
		html.replace("%onlineReal%", String.valueOf(DifferentMethods.getPlayersCount("ALL_REAL")));
		html.replace("%used%", String.valueOf(MemoryWatchDog.getMemUsedMb()));
		html.replace("%free%", String.valueOf(MemoryWatchDog.getMemFreeMb()));
		html.replace("%max%", String.valueOf(MemoryWatchDog.getMemMaxMb()));
		html.replace("%os%", System.getProperty("os.name"));
		html.replace("%gameTime%", GameTimeController.getInstance().getGameHour() + ":" + GameTimeController.getInstance().getGameMinute());
		html.replace("%dayNight%", GameTimeController.getInstance().isNight() ? MessagesData.getInstance().getMessage(activeChar, "admin_game_night") : MessagesData.getInstance().getMessage(activeChar, "admin_game_day"));
		html.replace("%timeserv%", String.valueOf(DifferentMethods.getServerUpTime()));
		html.replace("%maxonline%", String.valueOf(DifferentMethods.getPlayersCount("ALL") + "/" + Config.MAXIMUM_ONLINE_USERS));
		html.replace("%geo%", Config.PATHFINDING > 0 ? MessagesData.getInstance().getMessage(activeChar, "admin_geo_loading") : MessagesData.getInstance().getMessage(activeChar, "admin_geo_disabled"));
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void serverShutdown(L2PcInstance activeChar, int seconds, boolean restart)
	{
		Shutdown.getInstance().startShutdown(activeChar, seconds, restart);
	}
	
	private void serverAbort(L2PcInstance activeChar)
	{
		Shutdown.getInstance().abort(activeChar);
	}
}
