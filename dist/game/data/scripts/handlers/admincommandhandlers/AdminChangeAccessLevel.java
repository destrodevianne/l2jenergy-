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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2AccessLevel;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Change access level command handler.
 */
public final class AdminChangeAccessLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_changelvl"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		String[] parts = command.split(" ");
		if (parts.length == 2)
		{
			try
			{
				int lvl = Integer.parseInt(parts[1]);
				if (activeChar.getTarget() instanceof L2PcInstance)
				{
					onlineChange(activeChar, (L2PcInstance) activeChar.getTarget(), lvl);
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
			}
			catch (Exception e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_changelvl"));
			}
		}
		else if (parts.length == 3)
		{
			String name = parts[1];
			int lvl = Integer.parseInt(parts[2]);
			L2PcInstance player = L2World.getInstance().getPlayer(name);
			if (player != null)
			{
				onlineChange(activeChar, player, lvl);
			}
			else
			{
				try (Connection con = ConnectionFactory.getInstance().getConnection();
					PreparedStatement ps = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?"))
				{
					ps.setInt(1, lvl);
					ps.setString(2, name);
					ps.execute();
					
					if (ps.getUpdateCount() == 0)
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_character_not_found_access_level"));
					}
					else
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_character_access_level_now_set").replace("%s%", lvl + ""));
					}
				}
				catch (SQLException se)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_changing_sqlexception_character_access_level"));
					if (GeneralConfig.DEBUG)
					{
						se.printStackTrace();
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * @param activeChar the active GM
	 * @param player the online target
	 * @param lvl the access level
	 */
	private static void onlineChange(L2PcInstance activeChar, L2PcInstance player, int lvl)
	{
		if (lvl >= 0)
		{
			if (AdminData.getInstance().hasAccessLevel(lvl))
			{
				final L2AccessLevel acccessLevel = AdminData.getInstance().getAccessLevel(lvl);
				player.setAccessLevel(lvl);
				player.sendMessage(MessagesData.getInstance().getMessage(player, "admin_your_access_level_has_been_changed_to").replace("%c%", acccessLevel.getName() + "").replace("%i%", acccessLevel.getName() + ""));
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "players_access_level_has_been_changed_to").replace("%s%", player.getName() + "").replace("%c%", acccessLevel.getName() + "").replace("%i%", acccessLevel.getName() + ""));
			}
			else
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_you_trying_set_unexisting_access_level").replace("%s%", lvl + ""));
			}
		}
		else
		{
			player.setAccessLevel(lvl);
			player.sendMessage(MessagesData.getInstance().getMessage(activeChar, "player_your_character_banned"));
			player.logout();
		}
	}
}
