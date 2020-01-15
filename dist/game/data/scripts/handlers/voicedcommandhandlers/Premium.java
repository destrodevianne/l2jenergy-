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
package handlers.voicedcommandhandlers;

import java.time.Instant;

import com.l2jserver.gameserver.configuration.config.RatesConfig;
import com.l2jserver.gameserver.configuration.config.custom.PremiumConfig;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.PremiumManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.TimeUtils;

public class Premium implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"premium"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("premium") && PremiumConfig.PREMIUM_SYSTEM_ENABLED)
		{
			final Instant instant = Instant.ofEpochMilli(PremiumManager.getInstance().getPremiumExpiration(activeChar.getAccountName()));
			final NpcHtmlMessage msg = new NpcHtmlMessage(5);
			final StringBuilder html = new StringBuilder();
			if (activeChar.isPremium())
			{
				html.append("<html><body><title>Premium Account Details</title><center>");
				html.append("<table>");
				html.append("<tr><td><center>Account Status: <font color=\"LEVEL\">Premium<br></font></td></tr>");
				html.append("<tr><td>Rate XP: <font color=\"LEVEL\">x" + (RatesConfig.RATE_XP * PremiumConfig.PREMIUM_RATE_XP) + " <br1></font></td></tr>");
				html.append("<tr><td>Rate SP: <font color=\"LEVEL\">x" + (RatesConfig.RATE_SP * PremiumConfig.PREMIUM_RATE_SP) + "  <br1></font></td></tr>");
				html.append("<tr><td>Drop Chance: <font color=\"LEVEL\">x" + (RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER * PremiumConfig.PREMIUM_RATE_DROP_CHANCE) + " <br1></font></td></tr>");
				html.append("<tr><td>Drop Amount: <font color=\"LEVEL\">x" + (RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER * PremiumConfig.PREMIUM_RATE_DROP_AMOUNT) + " <br1></font></td></tr>");
				html.append("<tr><td>Drop Manor: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_DROP_MANOR * PremiumConfig.PREMIUM_RATE_DROP_MANOR) + "<br1></font></td></tr>");
				html.append("<tr><td>Spoil Chance: <font color=\"LEVEL\">x" + (RatesConfig.RATE_CORPSE_DROP_CHANCE_MULTIPLIER * PremiumConfig.PREMIUM_RATE_SPOIL_CHANCE) + " <br1></font></td></tr>");
				html.append("<tr><td>Spoil Amount: <font color=\"LEVEL\">x" + (RatesConfig.RATE_CORPSE_DROP_AMOUNT_MULTIPLIER * PremiumConfig.PREMIUM_RATE_SPOIL_AMMOUNT) + " <br1></font></td></tr>");
				html.append("<tr><td>End date premium account: <font color=\"00A5FF\">" + TimeUtils.dateTimeFormat(instant) + "</font></td></tr>");
				html.append("<tr><td><center>Premium Info & Rules<br></center></td></tr>");
				html.append("<tr><td><font color=\"70FFCA\">1. Premium accounts CAN NOT BE TRANSFERED.<br1></font></td></tr>");
				html.append("<tr><td><font color=\"70FFCA\">2. Premium does not effect party members.<br1></font></td></tr>");
				html.append("<tr><td><font color=\"70FFCA\">3. Premium account effects ALL characters in same account.<br><br><br></font></td></tr>");
			}
			else
			{
				html.append("<html><body><title>Account Details</title><center>");
				html.append("<table>");
				html.append("<tr><td><center>Account Status: <font color=\"LEVEL\">Normal<br></font></td></tr>");
				html.append("<tr><td>Rate XP: <font color=\"LEVEL\"> x" + RatesConfig.RATE_XP + "<br1></font></td></tr>");
				html.append("<tr><td>Rate SP: <font color=\"LEVEL\"> x" + RatesConfig.RATE_SP + "<br1></font></td></tr>");
				html.append("<tr><td>Drop Chance: <font color=\"LEVEL\"> x" + RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER + "<br1></font></td></tr><br>");
				html.append("<tr><td>Drop Amount: <font color=\"LEVEL\"> x" + RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER + "<br1></font></td></tr><br>");
				html.append("<tr><td>Spoil Chance: <font color=\"LEVEL\"> x" + RatesConfig.RATE_CORPSE_DROP_CHANCE_MULTIPLIER + "<br1></font></td></tr><br>");
				html.append("<tr><td>Spoil Amount: <font color=\"LEVEL\"> x" + RatesConfig.RATE_CORPSE_DROP_AMOUNT_MULTIPLIER + "<br><br></font></td></tr><br>");
				html.append("<tr><td>Drop Manor: <font color=\"LEVEL\"> x" + RatesConfig.RATE_DROP_MANOR + "<br1></font></td></tr>");
				html.append("<tr><td><center>Premium Info & Rules<br></td></tr>");
				html.append("<tr><td>Rate XP: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_XP * PremiumConfig.PREMIUM_RATE_XP) + "<br1></font></td></tr>");
				html.append("<tr><td>Rate SP: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_SP * PremiumConfig.PREMIUM_RATE_SP) + "<br1></font></td></tr>");
				html.append("<tr><td>Drop Chance: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_DEATH_DROP_CHANCE_MULTIPLIER * PremiumConfig.PREMIUM_RATE_DROP_CHANCE) + "<br1></font></td></tr>");
				html.append("<tr><td>Drop Amount: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_DEATH_DROP_AMOUNT_MULTIPLIER * PremiumConfig.PREMIUM_RATE_DROP_AMOUNT) + "<br1></font></td></tr>");
				html.append("<tr><td>Spoil Chance: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_CORPSE_DROP_CHANCE_MULTIPLIER * PremiumConfig.PREMIUM_RATE_SPOIL_CHANCE) + "<br1></font></td></tr>");
				html.append("<tr><td>Spoil Amount: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_CORPSE_DROP_AMOUNT_MULTIPLIER * PremiumConfig.PREMIUM_RATE_SPOIL_AMMOUNT) + "<br1></font></td></tr>");
				html.append("<tr><td>Drop Manor: <font color=\"LEVEL\"> x" + (RatesConfig.RATE_DROP_MANOR * PremiumConfig.PREMIUM_RATE_DROP_MANOR) + "<br1></font></td></tr>");
				html.append("<tr><td> <font color=\"70FFCA\">1. Premium benefits CAN NOT BE TRANSFERED.<br1></font></td></tr>");
				html.append("<tr><td> <font color=\"70FFCA\">2. Premium does not effect party members.<br1></font></td></tr>");
				html.append("<tr><td> <font color=\"70FFCA\">3. Premium benefits effect ALL characters in same account.</font></td></tr>");
			}
			html.append("</table>");
			html.append("</center></body></html>");
			msg.setHtml(html.toString());
			activeChar.sendPacket(msg);
		}
		else
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}