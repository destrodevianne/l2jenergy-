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
package handlers.communityboard;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.instancemanager.AntiFeedManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;

/**
 * Event board.
 * @author Мо3олЬ
 */
public class EventBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbsevents"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		String html = null;
		if (command.equals("_bbsevents"))
		{
			final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/index.html");
		}
		else if (command.startsWith("_bbsevents:page"))
		{
			final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
			final String[] path = command.split(":");
			if (path.length > 3)
			{
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/" + path[2] + "/" + path[3] + ".html");
			}
			else
			{
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/" + path[2] + ".html");
			}
		}
		else if (command.startsWith("_bbsevents:tvt"))
		{
			final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
			
			if (!Config.TVT_EVENT_ENABLED)
			{
				activeChar.sendMessage("The event enabled.");
			}
			
			if (command.equalsIgnoreCase("_bbsevents:tvt_reg"))
			{
				if (TvTEvent.isParticipating())
				{
					int playerLevel = activeChar.getLevel();
					final int team1Count = TvTEvent.getTeamsPlayerCounts()[0];
					final int team2Count = TvTEvent.getTeamsPlayerCounts()[1];
					
					if (activeChar.isCursedWeaponEquipped())
					{
						activeChar.sendMessage("Cursed weapon holders are not allowed to participate.");
					}
					else if (OlympiadManager.getInstance().isRegistered(activeChar))
					{
						activeChar.sendMessage("You can not participate while registered for Olympiad.");
					}
					else if (activeChar.getKarma() > 0)
					{
						activeChar.sendMessage("Chaotic players are not allowed to participate.");
					}
					else if ((playerLevel < Config.TVT_EVENT_MIN_LVL) || (playerLevel > Config.TVT_EVENT_MAX_LVL))
					{
						activeChar.sendMessage("Only players from level" + Config.TVT_EVENT_MIN_LVL + " to level " + Config.TVT_EVENT_MAX_LVL + " are allowed to participate.");
					}
					else if ((team1Count == Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS) && (team2Count == Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS))
					{
						activeChar.sendMessage("The event is full! Only " + Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS + " players are allowed per team.");
					}
					else if ((Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.TVT_ID, activeChar, Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP))
					{
						activeChar.sendMessage("Maximum of " + AntiFeedManager.getInstance().getLimit(activeChar, Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP) + " participant(s) per IP address is allowed.");
					}
					else if (TvTEvent.needParticipationFee() && !TvTEvent.hasParticipationFee(activeChar))
					{
						activeChar.sendMessage("You need " + TvTEvent.getParticipationFee() + " for participation.");
					}
					else if (TvTEvent.addParticipant(activeChar))
					{
						activeChar.sendMessage("You are registered for a TvT Event.");
					}
				}
				else
				{
					activeChar.sendMessage("The event has not started.");
				}
			}
			else if (command.equalsIgnoreCase("_bbsevents:tvt_unreg"))
			{
				if (TvTEvent.isParticipating())
				{
					if (TvTEvent.removeParticipant(activeChar.getObjectId()))
					{
						if (Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP > 0)
						{
							AntiFeedManager.getInstance().removePlayer(AntiFeedManager.TVT_ID, activeChar);
						}
						activeChar.sendMessage("You are unregistered for a TvT Event.");
					}
					else
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "event_no_unregister"));
					}
				}
				else
				{
					activeChar.sendMessage("The event has not started.");
				}
			}
			
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "events/event_tvt.html");
		}
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
}
