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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.LoginServerThread;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.gameserverpackets.ServerStatus;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles the admin commands that acts on the login
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2007/07/31 10:05:56 $
 */
public class AdminLogin implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_server_gm_only",
		"admin_server_all",
		"admin_server_max_player",
		"admin_server_list_type",
		"admin_server_list_age",
		"admin_server_login"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_server_gm_only"))
		{
			gmOnly();
			activeChar.sendAdminMessage("Server is now GM only");
			showMainPage(activeChar);
		}
		else if (command.equals("admin_server_all"))
		{
			allowToAll();
			activeChar.sendAdminMessage("Server is not GM only anymore");
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_server_max_player"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String number = st.nextToken();
				try
				{
					LoginServerThread.getInstance().setMaxPlayer(Integer.parseInt(number));
					activeChar.sendAdminMessage("maxPlayer set to " + number);
					showMainPage(activeChar);
				}
				catch (NumberFormatException e)
				{
					activeChar.sendAdminMessage("Max players must be a number.");
				}
			}
			else
			{
				activeChar.sendAdminMessage("Format is server_max_player <max>");
			}
		}
		else if (command.startsWith("admin_server_list_type"))
		{
			StringTokenizer st = new StringTokenizer(command);
			int tokens = st.countTokens();
			if (tokens > 1)
			{
				st.nextToken();
				String[] modes = new String[tokens - 1];
				
				for (int i = 0; i < (tokens - 1); i++)
				{
					modes[i] = st.nextToken().trim();
				}
				int newType = 0;
				try
				{
					newType = Integer.parseInt(modes[0]);
				}
				catch (NumberFormatException e)
				{
					newType = GeneralConfig.serverTypeId(modes);
				}
				if (GeneralConfig.SERVER_LIST_TYPE != newType)
				{
					GeneralConfig.SERVER_LIST_TYPE = newType;
					LoginServerThread.getInstance().sendServerType();
					activeChar.sendAdminMessage("Server Type changed to " + getServerTypeName(newType));
					showMainPage(activeChar);
				}
				else
				{
					activeChar.sendAdminMessage("Server Type is already " + getServerTypeName(newType));
					showMainPage(activeChar);
				}
			}
			else
			{
				activeChar.sendAdminMessage("Format is server_list_type <normal/relax/test/nolabel/restricted/event/free>");
			}
		}
		else if (command.startsWith("admin_server_list_age"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String mode = st.nextToken();
				int age = 0;
				try
				{
					age = Integer.parseInt(mode);
					if (GeneralConfig.SERVER_LIST_AGE != age)
					{
						GeneralConfig.SERVER_LIST_TYPE = age;
						LoginServerThread.getInstance().sendServerStatus(ServerStatus.SERVER_AGE, age);
						activeChar.sendAdminMessage("Server Age changed to " + age);
						showMainPage(activeChar);
					}
					else
					{
						activeChar.sendAdminMessage("Server Age is already " + age);
						showMainPage(activeChar);
					}
				}
				catch (NumberFormatException e)
				{
					activeChar.sendAdminMessage("Age must be a number");
				}
			}
			else
			{
				activeChar.sendAdminMessage("Format is server_list_age <number>");
			}
		}
		else if (command.equals("admin_server_login"))
		{
			showMainPage(activeChar);
		}
		return true;
	}
	
	/**
	 * @param activeChar
	 */
	private void showMainPage(L2PcInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/login.htm");
		html.replace("%server_name%", LoginServerThread.getInstance().getServerName());
		html.replace("%status%", LoginServerThread.getInstance().getStatusString());
		html.replace("%clock%", getServerTypeName(GeneralConfig.SERVER_LIST_TYPE));
		html.replace("%brackets%", String.valueOf(GeneralConfig.SERVER_LIST_BRACKET));
		html.replace("%max_players%", String.valueOf(LoginServerThread.getInstance().getMaxPlayer()));
		activeChar.sendPacket(html);
	}
	
	private String getServerTypeName(int serverType)
	{
		String nameType = "";
		for (int i = 0; i < 7; i++)
		{
			int currentType = serverType & (int) Math.pow(2, i);
			
			if (currentType > 0)
			{
				if (!nameType.isEmpty())
				{
					nameType += "+";
				}
				
				switch (currentType)
				{
					case 0x01:
						nameType += "Normal";
						break;
					case 0x02:
						nameType += "Relax";
						break;
					case 0x04:
						nameType += "Test";
						break;
					case 0x08:
						nameType += "NoLabel";
						break;
					case 0x10:
						nameType += "Restricted";
						break;
					case 0x20:
						nameType += "Event";
						break;
					case 0x40:
						nameType += "Free";
						break;
				}
			}
		}
		return nameType;
	}
	
	/**
	 *
	 */
	private void allowToAll()
	{
		LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_AUTO);
		GeneralConfig.SERVER_GMONLY = false;
	}
	
	/**
	 *
	 */
	private void gmOnly()
	{
		LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_GM_ONLY);
		GeneralConfig.SERVER_GMONLY = true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
