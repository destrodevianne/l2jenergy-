/*
 * Copyright (C) 2004-2019 L2J DataPack
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

import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.entity.TvTEventTeleporter;
import com.l2jserver.gameserver.model.entity.TvTManager;

/**
 * @author HorridoJoho
 */
public class AdminTvTEvent implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_tvt_add",
		"admin_tvt_remove",
		"admin_tvt_advance"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_tvt_add"))
		{
			L2Object target = activeChar.getTarget();
			
			if (!(target instanceof L2PcInstance))
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_should_select_player"));
				return true;
			}
			
			add(activeChar, (L2PcInstance) target);
		}
		else if (command.equals("admin_tvt_remove"))
		{
			L2Object target = activeChar.getTarget();
			
			if (!(target instanceof L2PcInstance))
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_should_select_player"));
				return true;
			}
			
			remove(activeChar, (L2PcInstance) target);
		}
		else if (command.equals("admin_tvt_advance"))
		{
			TvTManager.getInstance().skipDelay();
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void add(L2PcInstance activeChar, L2PcInstance playerInstance)
	{
		if (playerInstance.isOnEvent())
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_already_participated_event"));
			return;
		}
		
		if (!TvTEvent.addParticipant(playerInstance))
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_instance_could_not_added_null"));
			return;
		}
		
		if (TvTEvent.isStarted())
		{
			new TvTEventTeleporter(playerInstance, TvTEvent.getParticipantTeamCoordinates(playerInstance.getObjectId()), true, false);
		}
	}
	
	private void remove(L2PcInstance activeChar, L2PcInstance playerInstance)
	{
		if (!TvTEvent.removeParticipant(playerInstance.getObjectId()))
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_player_not_part_event"));
			return;
		}
		
		new TvTEventTeleporter(playerInstance, Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
	}
}
