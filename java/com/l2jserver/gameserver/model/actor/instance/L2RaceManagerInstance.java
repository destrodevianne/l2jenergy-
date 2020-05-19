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
package com.l2jserver.gameserver.model.actor.instance;

import java.util.List;
import java.util.Locale;

import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.enums.RaceState;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.games.MonsterRace;
import com.l2jserver.gameserver.model.HistoryInfo;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public class L2RaceManagerInstance extends L2Npc
{
	protected static final int TICKET_PRICES[] =
	{
		100,
		500,
		1000,
		5000,
		10000,
		20000,
		50000,
		100000
	};
	
	public L2RaceManagerInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2RaceManagerInstance);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("BuyTicket"))
		{
			if (!GeneralConfig.ALLOW_RACE || (MonsterRace.getInstance().getCurrentRaceState() != RaceState.ACCEPTING_BETS))
			{
				player.sendPacket(SystemMessageId.MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			int val = Integer.parseInt(command.substring(10));
			if (val == 0)
			{
				player.setRace(0, 0);
				player.setRace(1, 0);
			}
			
			if (((val == 10) && (player.getRace(0) == 0)) || ((val == 20) && (player.getRace(0) == 0) && (player.getRace(1) == 0)))
			{
				val = 0;
			}
			
			String search, replace;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (val < 10)
			{
				html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 2));
				for (int i = 0; i < 8; i++)
				{
					int n = i + 1;
					search = "Mob" + n;
					html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
				}
				search = "No1";
				if (val == 0)
				{
					html.replace(search, "");
				}
				else
				{
					html.replace(search, val);
					player.setRace(0, val);
				}
			}
			else if (val < 20)
			{
				if (player.getRace(0) == 0)
				{
					return;
				}
				
				html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 3));
				html.replace("0place", player.getRace(0));
				search = "Mob1";
				replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().getName();
				html.replace(search, replace);
				search = "0adena";
				
				if (val == 10)
				{
					html.replace(search, "");
				}
				else
				{
					html.replace(search, TICKET_PRICES[val - 11]);
					player.setRace(1, val - 10);
				}
			}
			else if (val == 20)
			{
				if ((player.getRace(0) == 0) || (player.getRace(1) == 0))
				{
					return;
				}
				
				html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 4));
				html.replace("0place", player.getRace(0));
				search = "Mob1";
				replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().getName();
				html.replace(search, replace);
				search = "0adena";
				int price = TICKET_PRICES[player.getRace(1) - 1];
				html.replace(search, price);
				search = "0tax";
				int tax = 0;
				html.replace(search, tax);
				search = "0total";
				int total = price + tax;
				html.replace(search, total);
			}
			else
			{
				if ((player.getRace(0) == 0) || (player.getRace(1) == 0))
				{
					return;
				}
				
				int ticket = player.getRace(0);
				int priceId = player.getRace(1);
				
				if (!player.reduceAdena("Race", TICKET_PRICES[priceId - 1], this, true))
				{
					return;
				}
				
				player.setRace(0, 0);
				player.setRace(1, 0);
				
				L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), 4443);
				item.setCount(1);
				item.setEnchantLevel(MonsterRace.getInstance().getRaceNumber());
				item.setCustomType1(ticket);
				item.setCustomType2(TICKET_PRICES[priceId - 1] / 100);
				
				player.addItem("Race", item, player, false);
				final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_S2);
				msg.addInt(MonsterRace.getInstance().getRaceNumber());
				msg.addItemName(4443);
				player.sendPacket(msg);
				
				// Refresh lane bet.
				MonsterRace.getInstance().setBetOnLane(ticket, TICKET_PRICES[priceId - 1], true);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			html.replace("1race", MonsterRace.getInstance().getRaceNumber());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("ShowOdds"))
		{
			if (!GeneralConfig.ALLOW_RACE || (MonsterRace.getInstance().getCurrentRaceState() == RaceState.ACCEPTING_BETS))
			{
				player.sendPacket(SystemMessageId.MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 5));
			for (int i = 0; i < 8; i++)
			{
				final int n = i + 1;
				
				html.replace("Mob" + n, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
				
				// Odd
				final double odd = MonsterRace.getInstance().getOdds().get(i);
				html.replace("Odd" + n, (odd > 0D) ? String.format(Locale.ENGLISH, "%.1f", odd) : "&$804;");
			}
			html.replace("1race", MonsterRace.getInstance().getRaceNumber());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowInfo"))
		{
			if (!GeneralConfig.ALLOW_RACE)
			{
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 6));
			
			for (int i = 0; i < 8; i++)
			{
				int n = i + 1;
				String search = "Mob" + n;
				html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowTickets"))
		{
			if (!GeneralConfig.ALLOW_RACE)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Generate data.
			final StringBuilder sb = new StringBuilder();
			
			// Retrieve player's tickets.
			for (L2ItemInstance ticket : player.getInventory().getAllItemsByItemId(4443))
			{
				// Don't list current race tickets.
				if (ticket.getEnchantLevel() == MonsterRace.getInstance().getRaceNumber())
				{
					continue;
				}
				
				StringUtil.append(sb, "<tr><td><a action=\"bypass -h npc_%objectId%_ShowTicket ", "" + ticket.getObjectId(), "\">", "" + ticket.getEnchantLevel(), " Race Number</a></td><td align=right><font color=\"LEVEL\">", ""
					+ ticket.getCustomType1(), "</font> Number</td><td align=right><font color=\"LEVEL\">", "" + (ticket.getCustomType2() * 100), "</font> Adena</td></tr>");
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 7));
			html.replace("%tickets%", sb.toString());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("ShowTicket"))
		{
			// Retrieve ticket objectId.
			final int val = Integer.parseInt(command.substring(11));
			if (!GeneralConfig.ALLOW_RACE || (val == 0))
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Retrieve ticket on player's inventory.
			final L2ItemInstance ticket = player.getInventory().getItemByObjectId(val);
			if (ticket == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final int raceId = ticket.getEnchantLevel();
			final int lane = ticket.getCustomType1();
			final int bet = ticket.getCustomType2() * 100;
			
			// Retrieve HistoryInfo for that race.
			final HistoryInfo info = MonsterRace.getInstance().getHistory().get(raceId - 1);
			if (info == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 8));
			html.replace("%raceId%", raceId);
			html.replace("%lane%", lane);
			html.replace("%bet%", bet);
			html.replace("%firstLane%", info.getFirst());
			html.replace("%odd%", (lane == info.getFirst()) ? String.format(Locale.ENGLISH, "%.2f", info.getOddRate()) : "0.01");
			html.replace("%objectId%", getObjectId());
			html.replace("%ticketObjectId%", val);
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("CalculateWin"))
		{
			// Retrieve ticket objectId.
			final int val = Integer.parseInt(command.substring(13));
			if (!GeneralConfig.ALLOW_RACE || (val == 0))
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Delete ticket on player's inventory.
			final L2ItemInstance ticket = player.getInventory().getItemByObjectId(val);
			if (ticket == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final int raceId = ticket.getEnchantLevel();
			final int lane = ticket.getCustomType1();
			final int bet = ticket.getCustomType2() * 100;
			
			// Retrieve HistoryInfo for that race.
			final HistoryInfo info = MonsterRace.getInstance().getHistory().get(raceId - 1);
			if (info == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Destroy the ticket.
			if (player.destroyItem("MonsterTrack", ticket, this, true))
			{
				player.addAdena("MonsterTrack", (int) (bet * ((lane == info.getFirst()) ? info.getOddRate() : 0.01)), this, true);
			}
			
			super.onBypassFeedback(player, "Chat 0");
			return;
		}
		else if (command.equals("ViewHistory"))
		{
			if (!GeneralConfig.ALLOW_RACE)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Generate data.
			final StringBuilder sb = new StringBuilder();
			
			// Use whole history, pickup from 'last element' and stop at 'latest element - 7'.
			final List<HistoryInfo> history = MonsterRace.getInstance().getHistory();
			for (int i = history.size() - 1; i >= Math.max(0, history.size() - 7); i--)
			{
				final HistoryInfo info = history.get(i);
				StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", "" + info.getRaceId(), "</font> th</td><td><font color=\"LEVEL\">", "" + info.getFirst(), "</font> Lane </td><td><font color=\"LEVEL\">", ""
					+ info.getSecond(), "</font> Lane</td><td align=right><font color=00ffff>", String.format(Locale.ENGLISH, "%.2f", info.getOddRate()), "</font> Times</td></tr>");
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getHtmlPrefix(), getHtmlPath(getId(), 9));
			html.replace("%infos%", sb.toString());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
