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
package com.l2jserver.gameserver.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.l2jserver.gameserver.GameServer;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public class DifferentMethods
{
	public static int getPlayersCount(String type)
	{
		switch (type)
		{
			case "ALL":
			{
				return L2World.getInstance().getAllPlayersCount();
			}
			case "OFF_TRADE":
			{
				int offlineCount = 0;
				
				final Collection<L2PcInstance> objs = L2World.getInstance().getPlayers();
				for (L2PcInstance player : objs)
				{
					if ((player.getClient() == null) || player.getClient().isDetached())
					{
						offlineCount++;
					}
				}
				return offlineCount;
			}
			case "GM":
			{
				int onlineGMcount = 0;
				for (L2PcInstance gm : L2World.getInstance().getAllGMs())
				{
					if ((gm != null) && gm.isOnline() && (gm.getClient() != null) && !gm.getClient().isDetached())
					{
						onlineGMcount++;
					}
				}
				return onlineGMcount;
			}
			case "ALL_REAL":
			{
				Set<String> realPlayers = new HashSet<>();
				
				for (L2PcInstance onlinePlayer : L2World.getInstance().getPlayers())
				{
					if (((onlinePlayer != null) && (onlinePlayer.getClient() != null)) && !onlinePlayer.getClient().isDetached())
					{
						realPlayers.add(onlinePlayer.getIPAddress());
					}
				}
				return realPlayers.size();
			}
		}
		return 0;
	}
	
	public static String getServerUpTime()
	{
		long time = System.currentTimeMillis() - GameServer.dateTimeServerStarted.getTimeInMillis();
		
		final long days = TimeUnit.MILLISECONDS.toDays(time);
		time -= TimeUnit.DAYS.toMillis(days);
		final long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		final long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		
		return days + " Days, " + hours + " Hours, " + minutes + " Minutes";
	}
	
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	
	public static String time()
	{
		return TIME_FORMAT.format(new Date(System.currentTimeMillis()));
	}
	
	public static String time(long time)
	{
		return TIME_FORMAT.format(new Date(time * 1000));
	}
	
	public static String getItemName(int itemId)
	{
		return ItemTable.getInstance().getTemplate(itemId).getName();
	}
	
	// TODO: переделать
	public static boolean getPay(L2PcInstance activeChar, int itemid, long count)
	{
		L2ItemInstance item = activeChar.getInventory().getItemByItemId(itemid);
		if (item != null)
		{
			activeChar.destroyItem("BBS", itemid, count, activeChar, true);
			return true;
		}
		return false;
	}
}
