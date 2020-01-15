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

import java.util.Collection;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Util;

public final class AdminPcCafePoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_pccafepoints",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_pccafepoints"))
		{
			if (st.hasMoreTokens())
			{
				final String action = st.nextToken();
				
				final L2PcInstance target = getTarget(activeChar);
				if ((target == null) || !st.hasMoreTokens())
				{
					return false;
				}
				
				int value = 0;
				try
				{
					value = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					AdminHtml.showAdminHtml(activeChar, "pccafepoints.htm");
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_invalid_value"));
					return false;
				}
				
				switch (action)
				{
					case "increase":
					{
						if (target.getPcCafePoints() >= 200_000)
						{
							AdminHtml.showAdminHtml(activeChar, "pccafepoints.htm");
							activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "target_already_have_max_count_pc_cafe_point").replace("%c%", target.getName() + ""));
							return false;
						}
						
						activeChar.increasePcCafePoints(value);
						target.sendMessage(MessagesData.getInstance().getMessage(target, "target_increase_your_pc_cafe_point").replace("%s%", value + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_increased_your_pc_cafe_point").replace("%c%", target.getName() + "").replace("%s%", value + ""));
						break;
					}
					case "decrease":
					{
						if (target.getPcCafePoints() == 0)
						{
							AdminHtml.showAdminHtml(activeChar, "pccafepoints.htm");
							activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "target_already_have_min_count_pc_cafe_point").replace("%c%", target.getName() + ""));
							return false;
						}
						
						activeChar.decreasePcCafePoints(value);
						target.sendMessage(MessagesData.getInstance().getMessage(target, "target_decreased_your_pc_cafe_point").replace("%s%", value + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_decreased_your_pc_cafe_point").replace("%c%", target.getName() + "").replace("%s%", value + ""));
						break;
					}
					case "rewardOnline":
					{
						int range = 0;
						try
						{
							range = Integer.parseInt(st.nextToken());
						}
						catch (Exception e)
						{
							
						}
						
						if (range <= 0)
						{
							final int count = increaseForAll(L2World.getInstance().getPlayers(), value);
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_increased_your_pc_cafe_point_all_online").replace("%i%", count + "").replace("%s%", value + ""));
						}
						else if (range > 0)
						{
							final int count = increaseForAll(activeChar.getKnownList().getKnownPlayers().values(), value);
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_increased_your_pc_cafe_point_all_players").replace("%i%", count + "").replace("%r%", range + "").replace("%s%", value + ""));
						}
						break;
					}
				}
				AdminHtml.showAdminHtml(activeChar, "pccafepoints.htm");
			}
			else
			{
				AdminHtml.showAdminHtml(activeChar, "pccafepoints.htm");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/pccafepoints.htm"));
		final L2PcInstance target = getTarget(activeChar);
		final long points = target.getPcCafePoints();
		html.replace("%points%", Util.formatAdena(points));
		html.replace("%targetName%", target.getName());
		activeChar.sendPacket(html);
		return true;
	}
	
	private L2PcInstance getTarget(L2PcInstance activeChar)
	{
		return ((activeChar.getTarget() != null) && (activeChar.getTarget().getActingPlayer() != null)) ? activeChar.getTarget().getActingPlayer() : activeChar;
	}
	
	private int increaseForAll(Collection<L2PcInstance> playerList, int value)
	{
		int counter = 0;
		for (L2PcInstance temp : playerList)
		{
			if ((temp != null) && (temp.isOnlineInt() == 1))
			{
				if (temp.getPcCafePoints() >= 200_000)
				{
					continue;
				}
				
				temp.increasePcCafePoints(value);
				temp.sendMessage(MessagesData.getInstance().getMessage(temp, "admin_increase_your_pc_cafe_point").replace("%s%", value + ""));
				counter++;
			}
		}
		return counter;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}