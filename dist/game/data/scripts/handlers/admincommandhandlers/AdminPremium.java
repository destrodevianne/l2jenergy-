/*
 * Copyright (C) 2004-2018 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.PremiumManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminPremium implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_premium_menu",
		"admin_premium_add1",
		"admin_premium_add2",
		"admin_premium_add3",
		"admin_premium_info",
		"admin_premium_remove"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_premium_menu"))
		{
			AdminHtml.showAdminHtml(activeChar, "premium_menu.htm");
		}
		else if (command.startsWith("admin_premium_add1"))
		{
			try
			{
				addPremiumStatus(activeChar, 1, command.substring(19));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_premium_add1"));
			}
		}
		else if (command.startsWith("admin_premium_add2"))
		{
			try
			{
				addPremiumStatus(activeChar, 2, command.substring(19));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_premium_add2"));
			}
		}
		else if (command.startsWith("admin_premium_add3"))
		{
			try
			{
				addPremiumStatus(activeChar, 3, command.substring(19));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_premium_add3"));
			}
		}
		else if (command.startsWith("admin_premium_info"))
		{
			try
			{
				viewPremiumInfo(activeChar, command.substring(19));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_premium_info"));
			}
		}
		else if (command.startsWith("admin_premium_remove"))
		{
			try
			{
				removePremium(activeChar, command.substring(21));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_premium_remove"));
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/premium_menu.htm"));
		activeChar.sendPacket(html);
		return true;
	}
	
	private void addPremiumStatus(L2PcInstance activeChar, int months, String accountName)
	{
		// TODO: Add check if account exists XD
		PremiumManager.getInstance().addPremiumTime(accountName, months * 30, TimeUnit.DAYS);
		activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_account_will_have_premium_status_ntil").replace("%s%", accountName + "").replace("%i%", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(PremiumManager.getInstance().getPremiumExpiration(accountName)) + ""));
	}
	
	private void viewPremiumInfo(L2PcInstance activeChar, String accountName)
	{
		if (PremiumManager.getInstance().getPremiumExpiration(accountName) > 0)
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_account_has_premium_status_until").replace("%s%", accountName + "").replace("%i%", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(PremiumManager.getInstance().getPremiumExpiration(accountName)) + ""));
		}
		else
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_account_has_no_premium_status").replace("%s%", accountName + ""));
		}
	}
	
	private void removePremium(L2PcInstance activeChar, String accountName)
	{
		if (PremiumManager.getInstance().getPremiumExpiration(accountName) > 0)
		{
			PremiumManager.getInstance().removePremiumStatus(accountName, true);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_account_has_no_longer_premium_status").replace("%s%", accountName + ""));
		}
		else
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_account_has_no_premium_status").replace("%s%", accountName + ""));
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
