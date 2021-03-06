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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.configuration.config.RatesConfig;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminAdmin implements IAdminCommandHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AdminAdmin.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_admin",
		"admin_admin1",
		"admin_admin2",
		"admin_admin3",
		"admin_admin4",
		"admin_admin5",
		"admin_admin6",
		"admin_admin7",
		"admin_gmliston",
		"admin_gmlistoff",
		"admin_silence",
		"admin_diet",
		"admin_tradeoff",
		"admin_set",
		"admin_set_mod",
		"admin_saveolymp",
		"admin_sethero",
		"admin_givehero",
		"admin_endolympiad",
		"admin_setconfig",
		"admin_config_server",
		"admin_gmon"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_admin"))
		{
			showMainPage(activeChar, command);
		}
		else if (command.equals("admin_config_server"))
		{
			showConfigPage(activeChar);
		}
		else if (command.startsWith("admin_gmliston"))
		{
			AdminData.getInstance().showGm(activeChar);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_reg_gm_list"));
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_gmlistoff"))
		{
			AdminData.getInstance().hideGm(activeChar);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_unreg_gm_list"));
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_silence"))
		{
			if (activeChar.isSilenceMode()) // already in message refusal mode
			{
				activeChar.setSilenceMode(false);
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
			}
			else
			{
				activeChar.setSilenceMode(true);
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_saveolymp"))
		{
			Olympiad.getInstance().saveOlympiadStatus();
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_save_oly_sys"));
		}
		else if (command.startsWith("admin_endolympiad"))
		{
			try
			{
				Olympiad.getInstance().manualSelectHeroes();
			}
			catch (Exception e)
			{
				LOG.warn("An error occured while ending olympiad!", e);
			}
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_hero_formed"));
		}
		else if (command.startsWith("admin_sethero"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			final L2PcInstance target = activeChar.getTarget().isPlayer() ? activeChar.getTarget().getActingPlayer() : activeChar;
			target.setHero(!target.isHero());
			target.broadcastUserInfo();
		}
		else if (command.startsWith("admin_givehero"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			final L2PcInstance target = activeChar.getTarget().isPlayer() ? activeChar.getTarget().getActingPlayer() : activeChar;
			if (Hero.getInstance().isHero(target.getObjectId()))
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_hero_status_already_claimed"));
				return false;
			}
			
			if (!Hero.getInstance().isUnclaimedHero(target.getObjectId()))
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_hero_status_cannot_claimed"));
				return false;
			}
			Hero.getInstance().claimHero(target);
		}
		else if (command.startsWith("admin_diet"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				if (st.nextToken().equalsIgnoreCase("on"))
				{
					activeChar.setDietMode(true);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_diet_on"));
				}
				else if (st.nextToken().equalsIgnoreCase("off"))
				{
					activeChar.setDietMode(false);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_diet_off"));
				}
			}
			catch (Exception ex)
			{
				if (activeChar.getDietMode())
				{
					activeChar.setDietMode(false);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_diet_off"));
				}
				else
				{
					activeChar.setDietMode(true);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_diet_on"));
				}
			}
			finally
			{
				activeChar.refreshOverloaded();
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_tradeoff"))
		{
			try
			{
				String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on"))
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_trade_on"));
				}
				else if (mode.equalsIgnoreCase("off"))
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_trade_off"));
				}
			}
			catch (Exception ex)
			{
				if (activeChar.getTradeRefusal())
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_trade_off"));
				}
				else
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_trade_on"));
				}
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_setconfig"))
		{
			showConfigPage(activeChar);
		}
		else if (command.startsWith("admin_set"))
		{
			StringTokenizer st = new StringTokenizer(command);
			String[] cmd = st.nextToken().split("_");
			
			if (cmd.length == 3)
			{
				if (cmd[2].equalsIgnoreCase("mod"))
				{
					AdminHtml.showAdminHtml(activeChar, "mods_menu.htm");
				}
			}
		}
		else if (command.startsWith("admin_gmon"))
		{
			// nothing
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showMainPage(L2PcInstance activeChar, String command)
	{
		int mode = 0;
		String filename = null;
		try
		{
			mode = Integer.parseInt(command.substring(11));
		}
		catch (Exception e)
		{
		}
		switch (mode)
		{
			case 1:
				filename = "main";
				break;
			case 2:
				filename = "game";
				break;
			case 3:
				filename = "effects";
				break;
			case 4:
				filename = "server";
				break;
			case 5:
				filename = "mods";
				break;
			case 6:
				filename = "char";
				break;
			case 7:
				filename = "gm";
				break;
			default:
				filename = "main";
				break;
		}
		AdminHtml.showAdminHtml(activeChar, filename + "_menu.htm");
	}
	
	public void showConfigPage(L2PcInstance activeChar)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		StringBuilder replyMSG = new StringBuilder("<html><title>" + MessagesData.getInstance().getMessage(activeChar, "admin_html_title_config") + "</title><body>");
		replyMSG.append("<center><table width=270><tr><td width=60><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=150>"
			+ MessagesData.getInstance().getMessage(activeChar, "admin_html_title_config_panel") + "</td><td width=60><button value=\"Back\" action=\"bypass -h admin_admin4\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table></center><br>");
		replyMSG.append("<center><table width=260><tr><td width=140></td><td width=40></td><td width=40></td></tr>");
		replyMSG.append("<tr><td><font color=\"00AA00\">Drop:</font></td><td></td><td></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate EXP</font> = " + RatesConfig.RATE_XP + "</td><td><edit var=\"param1\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig RateXp $param1\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate SP</font> = " + RatesConfig.RATE_SP + "</td><td><edit var=\"param2\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig RateSp $param2\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Drop Spoil</font> = " + RatesConfig.RATE_CORPSE_DROP_CHANCE_MULTIPLIER + "</td><td><edit var=\"param4\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig RateDropSpoil $param4\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td width=140></td><td width=40></td><td width=40></td></tr>");
		replyMSG.append("<tr><td><font color=\"00AA00\">Enchant:</font></td><td></td><td></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Stone</font> = " + CharacterConfig.ENCHANT_CHANCE_ELEMENT_STONE + "</td><td><edit var=\"param8\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig EnchantChanceElementStone $param8\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Crystal</font> = " + CharacterConfig.ENCHANT_CHANCE_ELEMENT_CRYSTAL + "</td><td><edit var=\"param9\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig EnchantChanceElementCrystal $param9\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Jewel</font> = " + CharacterConfig.ENCHANT_CHANCE_ELEMENT_JEWEL + "</td><td><edit var=\"param10\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig EnchantChanceElementJewel $param10\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Energy</font> = " + CharacterConfig.ENCHANT_CHANCE_ELEMENT_ENERGY + "</td><td><edit var=\"param11\" width=40 height=15></td><td><button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_set")
			+ "\" action=\"bypass -h admin_setconfig EnchantChanceElementEnergy $param11\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("</table></body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}