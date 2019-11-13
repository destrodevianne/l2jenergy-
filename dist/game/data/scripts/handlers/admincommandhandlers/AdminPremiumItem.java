/*
 * Copyright (C) 2004-2019 L2J Server
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

import java.util.StringTokenizer;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Premium item commands.
 * @author Pandragon, update code Мо3олЬ
 */
public class AdminPremiumItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_premium_giveitem",
		"admin_give_premium_item"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_premium_giveitem"))
		{
			AdminHtml.showAdminHtml(activeChar, "premiumitemcreation.htm");
		}
		else if (command.startsWith("admin_give_premium_item"))
		{
			try
			{
				L2PcInstance target;
				if (activeChar.getTarget() instanceof L2PcInstance)
				{
					target = (L2PcInstance) activeChar.getTarget();
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return false;
				}
				
				String val = command.substring(24);
				StringTokenizer st = new StringTokenizer(val);
				String id = st.nextToken();
				int idval = Integer.parseInt(id);
				String num = st.nextToken();
				long numval = Long.parseLong(num);
				
				if (!givePremiumItem(activeChar, target, idval, numval))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_create_premium_item"));
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{ // Case of missing parameter
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_create_premium_item"));
			}
			AdminHtml.showAdminHtml(activeChar, "premiumitemcreation.htm");
		}
		return true;
	}
	
	private boolean givePremiumItem(L2PcInstance activeChar, L2PcInstance target, int itemId, long itemCount)
	{
		if ((itemId != 0) || (itemCount != 0))
		{
			DAOFactory.getInstance().getPremiumItemDAO().add(target, itemId, itemCount, activeChar.getName());
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_you_have_spawned_dimensional_storage").replace("%s%", (itemCount > 1 ? itemCount + " x " : "") + "").replace("%i%", itemId + "").replace("%c%", target.getName() + ""));
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
