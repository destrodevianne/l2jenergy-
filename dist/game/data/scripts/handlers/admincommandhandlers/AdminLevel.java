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

import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

public class AdminLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_level",
		"admin_set_level"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		L2Object targetChar = activeChar.getTarget();
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		String val = "";
		if (st.countTokens() >= 1)
		{
			val = st.nextToken();
		}
		
		if (actualCommand.equalsIgnoreCase("admin_add_level"))
		{
			try
			{
				if (targetChar.isPlayer())
				{
					L2PcInstance targetPlayer = (L2PcInstance) targetChar;
					targetPlayer.addLevel(Integer.parseInt(val));
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_wrong_number_format"));
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_level"))
		{
			try
			{
				if (!(targetChar instanceof L2PcInstance))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					return false;
				}
				L2PcInstance targetPlayer = (L2PcInstance) targetChar;
				int oldLevel = targetPlayer.getLevel();
				int newLevel = Integer.parseInt(val);
				
				if (newLevel < 1)
				{
					newLevel = 1;
				}
				targetPlayer.setLevel(newLevel);
				targetPlayer.setExp(ExperienceData.getInstance().getExpForLevel(Math.min(newLevel, targetPlayer.getMaxExpLevel())));
				targetPlayer.onLevelChange(newLevel > oldLevel);
				targetPlayer.broadcastInfo();
			}
			catch (NumberFormatException e)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_level_require_number_value"));
				return false;
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
