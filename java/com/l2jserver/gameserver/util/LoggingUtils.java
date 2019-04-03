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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.model.items.type.ItemType;
import com.l2jserver.gameserver.network.L2GameClient;

public class LoggingUtils
{
	private static final Logger LOG_GAME = LoggerFactory.getLogger("game");
	
	private static final Set<ItemType> EXCLUDED_ITEM_TYPES = new HashSet<>();
	
	static
	{
		EXCLUDED_ITEM_TYPES.add(EtcItemType.ARROW);
		EXCLUDED_ITEM_TYPES.add(EtcItemType.SHOT);
	}
	
	public static void logItem(Logger itemLogger, String processPrefix, String process, L2ItemInstance item, String ownerName, Object reference)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		
		// Don't log arrows, shots and herbs.
		if (!EXCLUDED_ITEM_TYPES.contains(item.getItemType()))
		{
			return;
		}
		
		stringBuilder.append(processPrefix);
		stringBuilder.append(process);
		
		stringBuilder.append(", owner '");
		stringBuilder.append(ownerName);
		stringBuilder.append('\'');
		
		stringBuilder.append(", item object id '");
		stringBuilder.append(item.getObjectId());
		stringBuilder.append('\'');
		
		stringBuilder.append(", item name '");
		stringBuilder.append(item.getItem().getName());
		stringBuilder.append('\'');
		
		stringBuilder.append(", item count '");
		stringBuilder.append(item.getCount());
		stringBuilder.append('\'');
		
		if (item.getEnchantLevel() > 0)
		{
			stringBuilder.append(", item enchant level '");
			stringBuilder.append('+');
			stringBuilder.append(item.getEnchantLevel());
			stringBuilder.append('\'');
		}
		stringBuilder.append(", reference '");
		stringBuilder.append(reference.toString());
		stringBuilder.append('.');
		itemLogger.info(stringBuilder.toString());
	}
	
	public static void logChat(Logger chatLogger, String senderName, String receiverName, String message, String chatName)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append('[');
		stringBuilder.append(chatName);
		stringBuilder.append("] ");
		
		stringBuilder.append('\'');
		stringBuilder.append(senderName);
		stringBuilder.append('\'');
		
		if (receiverName != null)
		{
			stringBuilder.append(" to ");
			
			stringBuilder.append('\'');
			stringBuilder.append(receiverName);
			stringBuilder.append('\'');
		}
		
		stringBuilder.append(", message: \"");
		stringBuilder.append(message);
		stringBuilder.append('"');
		
		stringBuilder.append('.');
		
		chatLogger.info(stringBuilder.toString());
	}
	
	public static void add(String text, String cat, L2PcInstance player)
	{
		StringBuilder output = new StringBuilder();
		
		output.append(cat);
		if (player != null)
		{
			output.append(' ');
			output.append(player);
		}
		output.append(' ');
		output.append(text);
		LOG_GAME.info(output.toString());
	}
	
	public static void add(String text, String cat)
	{
		add(text, cat, null);
	}
	
	// TODO: Переписать
	public static void logAccounting(Logger accountingLogger, String message, Object[] params)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(message);
		
		if (params != null)
		{
			for (Object p : params)
			{
				if (p == null)
				{
					continue;
				}
				
				stringBuilder.append(", ");
				
				if (p instanceof L2GameClient)
				{
					final L2GameClient client = (L2GameClient) p;
					String address = null;
					try
					{
						if (!client.isDetached())
						{
							address = client.getConnection().getInetAddress().getHostAddress();
						}
					}
					catch (Exception e)
					{
					}
					
					switch (client.getState())
					{
						case ENTERING:
						case IN_GAME:
							if (client.getActiveChar() != null)
							{
								stringBuilder.append(client.getActiveChar().getName());
								stringBuilder.append(String.valueOf(client.getActiveChar().getObjectId()));
							}
							break;
						case AUTHED:
							if (client.getAccountName() != null)
							{
								stringBuilder.append(client.getAccountName());
							}
							break;
						case CONNECTED:
							if (address != null)
							{
								stringBuilder.append(address);
							}
							break;
						default:
							throw new IllegalStateException("Missing state on switch");
					}
				}
				else if (p instanceof L2PcInstance)
				{
					L2PcInstance player = (L2PcInstance) p;
					stringBuilder.append(player.getName());
					stringBuilder.append(String.valueOf(player.getObjectId()));
				}
				else
				{
					stringBuilder.append(p.toString());
				}
			}
		}
		stringBuilder.append('.');
		accountingLogger.info(stringBuilder.toString());
	}
	
	// TODO: Переписать
	public static void logOlympiad(Logger olympiadLogger, String message, Object[] params)
	{
		final StringBuilder output = StringUtil.startAppend(30 + message.length() + (params == null ? 0 : params.length * 10), message);
		if (params != null)
		{
			for (Object p : params)
			{
				if (p == null)
				{
					continue;
				}
				StringUtil.append(output, ",", p.toString());
			}
		}
		output.append('.');
		olympiadLogger.info(output.toString());
	}
}
