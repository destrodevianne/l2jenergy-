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

import java.util.Collection;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Util;

public class AdminGamePoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_gamepoints"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_gamepoints"))
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
					AdminHtml.showAdminHtml(activeChar, "game_points.htm");
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_invalid_value"));
					return false;
				}
				
				switch (action)
				{
					case "set":
					{
						target.setPrimePoints(value);
						target.sendMessage(MessagesData.getInstance().getMessage(target, "target_set_your_prime_point").replace("%s%", value + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_set_your_prime_point").replace("%s%", value + "").replace("%c%", target.getName() + ""));
						break;
					}
					case "increase":
					{
						if (target.getPrimePoints() == Integer.MAX_VALUE)
						{
							AdminHtml.showAdminHtml(activeChar, "game_points.htm");
							activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "target_already_have_max_count_prime_point").replace("%c%", target.getName() + ""));
							return false;
						}
						
						long primeCount = Math.min((target.getPrimePoints() + value), Integer.MAX_VALUE);
						if (primeCount < 0)
						{
							primeCount = Integer.MAX_VALUE;
						}
						target.setPrimePoints(primeCount);
						target.sendMessage(MessagesData.getInstance().getMessage(target, "target_increase_your_prime_point").replace("%s%", value + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_increased_your_prime_point").replace("%c%", target.getName() + "").replace("%s%", value + ""));
						break;
					}
					case "decrease":
					{
						if (target.getPrimePoints() == 0)
						{
							AdminHtml.showAdminHtml(activeChar, "game_points.htm");
							activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "target_already_have_min_count_prime_point").replace("%c%", target.getName() + ""));
							return false;
						}
						
						final long primeCount = Math.max(target.getPrimePoints() - value, 0);
						target.setPrimePoints(primeCount);
						target.sendMessage(MessagesData.getInstance().getMessage(target, "target_decreased_your_prime_point").replace("%s%", value + ""));
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_decreased_your_prime_point").replace("%c%", target.getName() + "").replace("%s%", value + ""));
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
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_increased_your_prime_point_all_online").replace("%i%", count + "").replace("%s%", value + ""));
						}
						else if (range > 0)
						{
							final int count = increaseForAll(activeChar.getKnownList().getKnownPlayers().values(), value);
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_increased_your_prime_point_all_players").replace("%i%", count + "").replace("%r%", range + "").replace("%s%", value + ""));
						}
						break;
					}
				}
				AdminHtml.showAdminHtml(activeChar, "game_points.htm");
			}
			else
			{
				AdminHtml.showAdminHtml(activeChar, "game_points.htm");
			}
		}
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/game_points.htm"));
		final L2PcInstance target = getTarget(activeChar);
		final long points = target.getPrimePoints();
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
				if (temp.getPrimePoints() == Integer.MAX_VALUE)
				{
					continue;
				}
				
				long primeCount = Math.min((temp.getPrimePoints() + value), Integer.MAX_VALUE);
				if (primeCount < 0)
				{
					primeCount = Integer.MAX_VALUE;
				}
				temp.setPrimePoints(primeCount);
				temp.sendMessage(MessagesData.getInstance().getMessage(temp, "admin_increase_your_prime_point").replace("%s%", value + ""));
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
