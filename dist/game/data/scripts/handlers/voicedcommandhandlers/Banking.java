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

import com.l2jserver.gameserver.configuration.config.custom.CustomConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class trades Gold Bars for Adena and vice versa.
 * @author Ahmed
 */
public class Banking implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"bank",
		"withdraw",
		"deposit"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (command.equals("bank"))
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_usage_deposit_withdraw").replace("%s%", CustomConfig.BANKING_SYSTEM_ADENA + "").replace("%i%", CustomConfig.BANKING_SYSTEM_GOLDBARS + ""));
		}
		else if (command.equals("deposit"))
		{
			if (activeChar.getInventory().getInventoryItemCount(57, 0) >= CustomConfig.BANKING_SYSTEM_ADENA)
			{
				if (!activeChar.reduceAdena("Goldbar", CustomConfig.BANKING_SYSTEM_ADENA, activeChar, false))
				{
					return false;
				}
				activeChar.getInventory().addItem("Goldbar", 3470, CustomConfig.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_now_have_goldbar_and_adena").replace("%i%", CustomConfig.BANKING_SYSTEM_GOLDBARS + "").replace("%s%", CustomConfig.BANKING_SYSTEM_ADENA + ""));
			}
			else
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_not_have_enough_goldbars_convert").replace("%s%", CustomConfig.BANKING_SYSTEM_ADENA + ""));
			}
		}
		else if (command.equals("withdraw"))
		{
			if (activeChar.getInventory().getInventoryItemCount(3470, 0) >= CustomConfig.BANKING_SYSTEM_GOLDBARS)
			{
				if (!activeChar.destroyItemByItemId("Adena", 3470, CustomConfig.BANKING_SYSTEM_GOLDBARS, activeChar, false))
				{
					return false;
				}
				activeChar.getInventory().addAdena("Adena", CustomConfig.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_now_have_adena_and_goldbar").replace("%s%", CustomConfig.BANKING_SYSTEM_ADENA + "").replace("%i%", CustomConfig.BANKING_SYSTEM_GOLDBARS + ""));
			}
			else
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "dp_handler_you_not_have_goldbars_into").replace("%s%", CustomConfig.BANKING_SYSTEM_ADENA + ""));
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}