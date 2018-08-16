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

/**
 * This class handles following admin commands: - server_shutdown [sec] = shows menu or shuts down server in sec seconds
 * @version $Revision: 1.5.2.1.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminShutdown implements IAdminCommandHandler
{
	// private static Logger _log = Logger.getLogger(AdminShutdown.class.getName());
	
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
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_server_shutdown"));
					sendHtmlForm(activeChar);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				sendHtmlForm(activeChar);
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
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_server_restart"));
					sendHtmlForm(activeChar);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				sendHtmlForm(activeChar);
			}
		}
		else if (command.startsWith("admin_server_abort"))
		{
			serverAbort(activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void sendHtmlForm(L2PcInstance activeChar)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/shutdown.htm");
		adminReply.replace("%onlineAll%", String.valueOf(DifferentMethods.getPlayersCount("ALL")));
		adminReply.replace("%offlineTrade%", String.valueOf(DifferentMethods.getPlayersCount("OFF_TRADE")));
		adminReply.replace("%onlineGM%", String.valueOf(DifferentMethods.getPlayersCount("GM")));
		adminReply.replace("%onlineReal%", String.valueOf(DifferentMethods.getPlayersCount("ALL_REAL")));
		adminReply.replace("%used%", String.valueOf(MemoryWatchDog.getMemUsedMb()));
		adminReply.replace("%free%", String.valueOf(MemoryWatchDog.getMemFreeMb()));
		adminReply.replace("%max%", String.valueOf(MemoryWatchDog.getMemMaxMb()));
		adminReply.replace("%os%", System.getProperty("os.name"));
		adminReply.replace("%gameTime%", GameTimeController.getInstance().getGameHour() + ":" + GameTimeController.getInstance().getGameMinute());
		adminReply.replace("%dayNight%", GameTimeController.getInstance().isNight() ? MessagesData.getInstance().getMessage(activeChar, "admin_game_night") : MessagesData.getInstance().getMessage(activeChar, "admin_game_day"));
		adminReply.replace("%timeserv%", String.valueOf(DifferentMethods.getServerUpTime()));
		adminReply.replace("%maxonline%", String.valueOf(DifferentMethods.getPlayersCount("ALL") + "/" + Config.MAXIMUM_ONLINE_USERS));
		adminReply.replace("%geo%", Config.PATHFINDING > 0 ? MessagesData.getInstance().getMessage(activeChar, "admin_geo_loading") : MessagesData.getInstance().getMessage(activeChar, "admin_geo_disabled"));
		activeChar.sendPacket(adminReply);
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
