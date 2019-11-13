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
package handlers.voicedcommandhandlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Repair Character Voiced Command
 * @author Sacrifice, Мо3олЬ
 */
public final class Repair implements IVoicedCommandHandler
{
	private static final Map<Integer, Long> REUSES = new ConcurrentHashMap<>();
	private static final int REUSE = 30 * 60 * 1000; // 30 Min
	
	private static final String[] VOICED_COMMANDS =
	{
		"repair"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		String repairChar = null;
		
		try
		{
			if (params != null)
			{
				if (params.length() > 1)
				{
					final String[] cmdParams = params.split(" ");
					repairChar = cmdParams[0];
				}
			}
		}
		catch (Exception e)
		{
			repairChar = null;
		}
		
		if (command.equals("repair") && (repairChar != null))
		{
			final Long timeStamp = REUSES.get(activeChar.getObjectId());
			if ((timeStamp != null) && ((System.currentTimeMillis() - REUSE) < timeStamp.longValue()))
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "you_cannot_repair_often"));
				return false;
			}
			REUSES.put(activeChar.getObjectId(), System.currentTimeMillis());
			
			if (DAOFactory.getInstance().getPlayerDAO().checkAccount(activeChar, repairChar))
			{
				if (checkChar(activeChar, repairChar))
				{
					final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-self.htm");
					final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					npcHtmlMessage.setHtml(htmContent);
					activeChar.sendPacket(npcHtmlMessage);
					return false;
				}
				else if (DAOFactory.getInstance().getPlayerDAO().checkJail(activeChar))
				{
					final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-jail.htm");
					final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					npcHtmlMessage.setHtml(htmContent);
					activeChar.sendPacket(npcHtmlMessage);
					return false;
				}
				else
				{
					DAOFactory.getInstance().getPlayerDAO().repairBadCharacter(repairChar);
					final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-done.htm");
					final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					npcHtmlMessage.setHtml(htmContent);
					activeChar.sendPacket(npcHtmlMessage);
					return true;
				}
			}
			final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-error.htm");
			final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
			npcHtmlMessage.setHtml(htmContent);
			activeChar.sendPacket(npcHtmlMessage);
			return false;
		}
		String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair.htm");
		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
		npcHtmlMessage.setHtml(htmContent);
		npcHtmlMessage.replace("%acc_chars%", DAOFactory.getInstance().getPlayerDAO().getCharList(activeChar));
		activeChar.sendPacket(npcHtmlMessage);
		return true;
	}
	
	private boolean checkChar(L2PcInstance activeChar, String repairChar)
	{
		boolean result = false;
		if (activeChar.getName().compareTo(repairChar) == 0)
		{
			result = true;
		}
		return result;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
