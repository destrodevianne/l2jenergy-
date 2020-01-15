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
package handlers.admincommandhandlers;

import java.util.Collection;
import java.util.StringTokenizer;

import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.model.CursedWeapon;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminCursedWeapons implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_cw_info",
		"admin_cw_remove",
		"admin_cw_goto",
		"admin_cw_reload",
		"admin_cw_add",
		"admin_cw_info_menu"
	};
	
	private int itemId;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		int id = 0;
		
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.startsWith("admin_cw_info"))
		{
			if (!command.contains("menu"))
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_cursed_weapons"));
				for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_player").replace("%i%", cw.getName() + "").replace("%s%", cw.getItemId() + ""));
					if (cw.isActivated())
					{
						L2PcInstance pl = cw.getPlayer();
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_player_holding").replace("%i%", (pl == null ? "" + MessagesData.getInstance().getMessage(activeChar, "admin_info_null") + "" : pl.getName()) + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_player_karma").replace("%i%", cw.getPlayerKarma() + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_time_remaining").replace("%i%", (cw.getTimeLeft() / 60000) + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_kills").replace("%i%", cw.getNbKills() + ""));
					}
					else if (cw.isDropped())
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_lying_ground"));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_time_remaining").replace("%i%", (cw.getTimeLeft() / 60000) + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_kills").replace("%i%", cw.getNbKills() + ""));
					}
					else
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_info_dont_exist_world"));
					}
					activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
				}
			}
			else
			{
				final Collection<CursedWeapon> cws = CursedWeaponsManager.getInstance().getCursedWeapons();
				final StringBuilder replyMSG = new StringBuilder(cws.size() * 300);
				final NpcHtmlMessage adminReply = new NpcHtmlMessage();
				adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/cwinfo.htm");
				for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
				{
					itemId = cw.getItemId();
					
					// TODO: переписать
					StringUtil.append(replyMSG, "<table width=270><tr><td>Name:</td><td>", cw.getName(), "</td></tr>");
					
					if (cw.isActivated())
					{
						L2PcInstance pl = cw.getPlayer();
						StringUtil.append(replyMSG, "<tr><td>Weilder:</td><td>", (pl == null ? "null" : pl.getName()), "</td></tr>" + "<tr><td>Karma:</td><td>", String.valueOf(cw.getPlayerKarma()), "</td></tr>"
							+ "<tr><td>Kills:</td><td>", String.valueOf(cw.getPlayerPkKills()), "/", String.valueOf(cw.getNbKills()), "</td></tr>" + "<tr><td>Time remaining:</td><td>", String.valueOf(cw.getTimeLeft() / 60000), " min.</td></tr>"
								+ "<tr><td><button value=\"Remove\" action=\"bypass -h admin_cw_remove ", String.valueOf(itemId), "\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
									+ "<td><button value=\"Go\" action=\"bypass -h admin_cw_goto ", String.valueOf(itemId), "\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					}
					else if (cw.isDropped())
					{
						StringUtil.append(replyMSG, "<tr><td>Position:</td><td>Lying on the ground</td></tr>" + "<tr><td>Time remaining:</td><td>", String.valueOf(cw.getTimeLeft() / 60000), " min.</td></tr>" + "<tr><td>Kills:</td><td>", String.valueOf(cw.getNbKills()), "</td></tr>"
							+ "<tr><td><button value=\"Remove\" action=\"bypass -h admin_cw_remove ", String.valueOf(itemId), "\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
								+ "<td><button value=\"Go\" action=\"bypass -h admin_cw_goto ", String.valueOf(itemId), "\" width=73 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
					}
					else
					{
						StringUtil.append(replyMSG, "<tr><td>Position:</td><td>Doesn't exist.</td></tr>"
							+ "<tr><td><button value=\"Give to Target\" action=\"bypass -h admin_cw_add ", String.valueOf(itemId), "\" width=130 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td></td></tr>");
					}
					
					replyMSG.append("</table><br>");
				}
				adminReply.replace("%cwinfo%", replyMSG.toString());
				activeChar.sendPacket(adminReply);
			}
		}
		else if (command.startsWith("admin_cw_reload"))
		{
			CursedWeaponsManager.getInstance().reload();
		}
		else
		{
			CursedWeapon cw = null;
			try
			{
				String parameter = st.nextToken();
				if (parameter.matches("[0-9]*"))
				{
					id = Integer.parseInt(parameter);
				}
				else
				{
					parameter = parameter.replace('_', ' ');
					for (CursedWeapon cwp : CursedWeaponsManager.getInstance().getCursedWeapons())
					{
						if (cwp.getName().toLowerCase().contains(parameter.toLowerCase()))
						{
							id = cwp.getItemId();
							break;
						}
					}
				}
				cw = CursedWeaponsManager.getInstance().getCursedWeapon(id);
			}
			catch (Exception e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_cw"));
			}
			
			if (cw == null)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_unknown_cursed_weapon_id"));
				return false;
			}
			
			if (command.startsWith("admin_cw_remove "))
			{
				cw.endOfLife();
			}
			else if (command.startsWith("admin_cw_goto "))
			{
				cw.goTo(activeChar);
			}
			else if (command.startsWith("admin_cw_add"))
			{
				if (cw.isActive())
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_this_cursed_weapon_already_active"));
				}
				else
				{
					L2Object target = activeChar.getTarget();
					if (target instanceof L2PcInstance)
					{
						((L2PcInstance) target).addItem("AdminCursedWeaponAdd", id, 1, target, true);
					}
					else
					{
						activeChar.addItem("AdminCursedWeaponAdd", id, 1, activeChar, true);
					}
					cw.setEndTime(System.currentTimeMillis() + (cw.getDuration() * 60000L));
					cw.reActivate();
				}
			}
			else
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_unknown_command"));
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}