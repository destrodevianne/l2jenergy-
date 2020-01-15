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

import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.stat.PcStat;

/**
 * @author Psychokiller1888
 */
public class AdminVitality implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_set_vitality",
		"admin_set_vitality_level",
		"admin_full_vitality",
		"admin_empty_vitality",
		"admin_get_vitality"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!Config.ENABLE_VITALITY)
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_vitality_not_enabled_server"));
			return false;
		}
		
		int level = 0;
		int vitality = 0;
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String cmd = st.nextToken();
		
		if (activeChar.getTarget() instanceof L2PcInstance)
		{
			L2PcInstance target;
			target = (L2PcInstance) activeChar.getTarget();
			
			if (cmd.equals("admin_set_vitality"))
			{
				try
				{
					vitality = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_incorrect_vitality"));
				}
				target.setVitalityPoints(vitality, true);
				target.sendMessage(MessagesData.getInstance().getMessage(target, "admin_your_set_vitality_points").replace("%i%", vitality + ""));
			}
			else if (cmd.equals("admin_set_vitality_level"))
			{
				try
				{
					level = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_incorrect_vitality_level"));
				}
				
				if ((level >= 0) && (level <= 4))
				{
					if (level == 0)
					{
						vitality = PcStat.MIN_VITALITY_POINTS;
					}
					else
					{
						vitality = PcStat.VITALITY_LEVELS[level - 1];
					}
					target.setVitalityPoints(vitality, true);
					target.sendMessage(MessagesData.getInstance().getMessage(target, "admin_your_set_vitality_level").replace("%i%", level + ""));
				}
				else
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_incorrect_vitality_level"));
				}
			}
			else if (cmd.equals("admin_full_vitality"))
			{
				target.setVitalityPoints(PcStat.MAX_VITALITY_POINTS, true);
				target.sendMessage(MessagesData.getInstance().getMessage(target, "admin_completly_recharged_your_vitality"));
			}
			else if (cmd.equals("admin_empty_vitality"))
			{
				target.setVitalityPoints(PcStat.MIN_VITALITY_POINTS, true);
				target.sendMessage(MessagesData.getInstance().getMessage(target, "admin_completly_emptied_your_vitality"));
			}
			else if (cmd.equals("admin_get_vitality"))
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_vitality_level").replace("%i%", target.getVitalityLevel() + ""));
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_vitality_points").replace("%i%", target.getVitalityPoints() + ""));
			}
			return true;
		}
		activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_target_not_found_not_player"));
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	public static void main(String[] args)
	{
		new AdminVitality();
	}
}
