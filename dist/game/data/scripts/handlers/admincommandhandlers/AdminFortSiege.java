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

import java.util.List;
import java.util.StringTokenizer;

import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles all siege commands.
 * @author U3Games (rework)
 */
public class AdminFortSiege implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_fortsiege",
		"admin_add_fortattacker",
		"admin_list_fortsiege_clans",
		"admin_clear_fortsiege_list",
		"admin_spawn_fortdoors",
		"admin_endfortsiege",
		"admin_startfortsiege",
		"admin_setfort",
		"admin_removefort"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken(); // Get actual command
		
		// Get fort
		Fort fort = null;
		int fortId = 0;
		
		if (st.hasMoreTokens())
		{
			fortId = Integer.parseInt(st.nextToken());
			fort = FortManager.getInstance().getFortById(fortId);
		}
		
		// Check fort
		if (((fort == null) || (fortId == 0)))
		{
			// No fort specified
			showFortSelectPage(activeChar);
		}
		else
		{
			L2PcInstance player = null;
			if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
			{
				player = activeChar.getTarget().getActingPlayer();
			}
			
			switch (command)
			{
				case "admin_add_fortattacker":
				{
					if (player == null)
					{
						activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					}
					else
					{
						if (fort.getSiege().addAttacker(player, false) == 4)
						{
							final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.REGISTERED_TO_S1_FORTRESS_BATTLE);
							sm.addCastleId(fort.getResidenceId());
							player.sendPacket(sm);
						}
						else
						{
							player.sendMessage(MessagesData.getInstance().getMessage(activeChar, "admin_during_registering_error_occurred"));
						}
					}
					break;
				}
				case "admin_clear_fortsiege_list":
				{
					fort.getSiege().clearSiegeClan();
					break;
				}
				case "admin_endfortsiege":
				{
					fort.getSiege().endSiege();
					break;
				}
				case "admin_list_fortsiege_clans":
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_not_implemented_yet"));
					break;
				}
				case "admin_setfort":
				{
					if ((player == null) || (player.getClan() == null))
					{
						activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					}
					else
					{
						fort.endOfSiege(player.getClan());
					}
					break;
				}
				case "admin_removefort":
				{
					final L2Clan clan = fort.getOwnerClan();
					if (clan != null)
					{
						fort.removeOwner(true);
					}
					else
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_unable_to_remove_fort"));
					}
					break;
				}
				case "admin_spawn_fortdoors":
				{
					fort.resetDoors();
					break;
				}
				case "admin_startfortsiege":
				{
					fort.getSiege().startSiege();
					break;
				}
			}
			
			showFortSiegePage(activeChar, fort);
		}
		
		return true;
	}
	
	private void showFortSelectPage(L2PcInstance activeChar)
	{
		int i = 0;
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/forts.htm");
		
		final List<Fort> forts = FortManager.getInstance().getForts();
		final StringBuilder cList = new StringBuilder(forts.size() * 100);
		
		for (Fort fort : forts)
		{
			if (fort != null)
			{
				StringUtil.append(cList, "<td fixwidth=90><a action=\"bypass -h admin_fortsiege ", String.valueOf(fort.getResidenceId()), "\">", fort.getName(), " id: ", String.valueOf(fort.getResidenceId()), "</a></td>");
				i++;
			}
			
			if (i > 2)
			{
				cList.append("</tr><tr>");
				i = 0;
			}
		}
		
		adminReply.replace("%forts%", cList.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private void showFortSiegePage(L2PcInstance activeChar, Fort fort)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/fort.htm");
		adminReply.replace("%fortName%", fort.getName());
		adminReply.replace("%fortId%", String.valueOf(fort.getResidenceId()));
		activeChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}