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
package handlers.voicedcommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.PunishmentManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.punishment.PunishmentAffect;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import com.l2jserver.gameserver.util.Util;

public class ChatAdmin implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"banchat",
		"unbanchat"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
		{
			return false;
		}
		
		if (command.equals(VOICED_COMMANDS[0])) // banchat
		{
			if (params == null)
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_usage_banchat_name"));
				return true;
			}
			StringTokenizer st = new StringTokenizer(params);
			if (st.hasMoreTokens())
			{
				String name = st.nextToken();
				long expirationTime = 0;
				if (st.hasMoreTokens())
				{
					String token = st.nextToken();
					if (Util.isDigit(token))
					{
						expirationTime = System.currentTimeMillis() + (Integer.parseInt(st.nextToken()) * 60 * 1000);
					}
				}
				
				int objId = CharNameTable.getInstance().getIdByName(name);
				if (objId > 0)
				{
					L2PcInstance player = L2World.getInstance().getPlayer(objId);
					if ((player == null) || !player.isOnline())
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_not_online"));
						return false;
					}
					if (player.isChatBanned())
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_already_punished"));
						return false;
					}
					if (player == activeChar)
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_can_ban_yourself"));
						return false;
					}
					if (player.isGM())
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_can_ban_gm"));
						return false;
					}
					if (AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_can_ban_moderator"));
						return false;
					}
					
					PunishmentManager.getInstance().startPunishment(new PunishmentTask(objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, expirationTime, "Chat banned by moderator", activeChar.getName()));
					
					player.sendMessage(MessagesData.getInstance().getMessage(player, "dp_handler_chat_banned_moderator").replace("%s%", activeChar.getName() + ""));
					
					if (expirationTime > 0)
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_chat_banned_for_minutes").replace("%s%", player.getName() + "").replace("%i%", expirationTime + ""));
					}
					else
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_chat_banned_forever").replace("%s%", player.getName() + ""));
					}
				}
				else
				{
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_not_found"));
					return false;
				}
			}
		}
		else if (command.equals(VOICED_COMMANDS[1])) // unbanchat
		{
			if (params == null)
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_usage_unbanchat_name"));
				return true;
			}
			StringTokenizer st = new StringTokenizer(params);
			if (st.hasMoreTokens())
			{
				String name = st.nextToken();
				
				int objId = CharNameTable.getInstance().getIdByName(name);
				if (objId > 0)
				{
					L2PcInstance player = L2World.getInstance().getPlayer(objId);
					if ((player == null) || !player.isOnline())
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_not_online"));
						return false;
					}
					if (!player.isChatBanned())
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_not_chat_banned"));
						return false;
					}
					
					PunishmentManager.getInstance().stopPunishment(objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
					
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_chat_unbanned").replace("%s%", player.getName() + ""));
					player.sendMessage(MessagesData.getInstance().getMessage(player, "dp_handler_chat_unbanned_moderator").replace("%s%", activeChar.getName() + ""));
				}
				else
				{
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_player_not_found"));
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
