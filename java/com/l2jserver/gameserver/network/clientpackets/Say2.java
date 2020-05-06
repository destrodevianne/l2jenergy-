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
package com.l2jserver.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.configuration.config.protection.BaseProtectionConfig;
import com.l2jserver.gameserver.configuration.parser.chatFilter.ChatFilterConfigParser;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.ChatType;
import com.l2jserver.gameserver.enums.skills.L2EffectType;
import com.l2jserver.gameserver.handler.ChatHandler;
import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerChat;
import com.l2jserver.gameserver.model.events.returns.ChatFilterReturn;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.util.LoggingUtils;
import com.l2jserver.gameserver.util.Util;

public final class Say2 extends L2GameClientPacket
{
	private static final String _C__49_SAY2 = "[C] 49 Say2";
	private static Logger LOG_CHAT = LoggerFactory.getLogger("chat");
	
	private static final String[] WALKER_COMMAND_LIST =
	{
		"USESKILL",
		"USEITEM",
		"BUYITEM",
		"SELLITEM",
		"SAVEITEM",
		"LOADITEM",
		"MSG",
		"DELAY",
		"LABEL",
		"JMP",
		"CALL",
		"RETURN",
		"MOVETO",
		"NPCSEL",
		"NPCDLG",
		"DLGSEL",
		"CHARSTATUS",
		"POSOUTRANGE",
		"POSINRANGE",
		"GOHOME",
		"SAY",
		"EXIT",
		"PAUSE",
		"STRINDLG",
		"STRNOTINDLG",
		"CHANGEWAITTYPE",
		"FORCEATTACK",
		"ISMEMBER",
		"REQUESTJOINPARTY",
		"REQUESTOUTPARTY",
		"QUITPARTY",
		"MEMBERSTATUS",
		"CHARBUFFS",
		"ITEMCOUNT",
		"FOLLOWTELEPORT"
	};
	
	private String _text;
	private int _type;
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_text = readS();
		_type = readD();
		_target = _type == ChatType.WHISPER.getClientId() ? readS() : null;
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		ChatType chatType = ChatType.findByClientId(_type);
		if (chatType == null)
		{
			LOG.warn("Say2: Invalid type: {} Player : {} text: {}", _type, activeChar.getName(), _text);
			return;
		}
		
		if (_text.isEmpty())
		{
			LOG.warn("{}: sending empty text. Possible packet hack!", activeChar.getName());
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}
		
		// Even though the client can handle more characters than it's current limit allows, an overflow (critical error) happens if you pass a huge (1000+) message.
		// July 11, 2011 - Verified on High Five 4 official client as 105.
		// Allow higher limit if player shift some item (text is longer then).
		if (!activeChar.isGM() && (((_text.indexOf(8) >= 0) && (_text.length() > 500)) || ((_text.indexOf(8) < 0) && (_text.length() > 105))))
		{
			activeChar.sendPacket(SystemMessageId.DONT_SPAM);
			return;
		}
		
		if (BaseProtectionConfig.L2WALKER_PROTECTION && (chatType == ChatType.WHISPER) && checkBot(_text))
		{
			Util.handleIllegalPlayerAction(activeChar, "Client Emulator Detect: Player " + activeChar.getName() + " using l2walker.", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		if (activeChar.isCursedWeaponEquipped() && ((chatType == ChatType.TRADE) || (chatType == ChatType.SHOUT)))
		{
			activeChar.sendPacket(SystemMessageId.SHOUT_AND_TRADE_CHAT_CANNOT_BE_USED_WHILE_POSSESSING_CURSED_WEAPON);
			return;
		}
		
		if (activeChar.isChatBanned() && (_text.charAt(0) != '.') && (chatType != ChatType.CLAN) && (chatType != ChatType.ALLIANCE) && (chatType != ChatType.PARTY))
		{
			if (activeChar.getEffectList().getFirstEffect(L2EffectType.CHAT_BLOCK) != null)
			{
				activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_SO_CHATTING_NOT_ALLOWED);
			}
			else if (GeneralConfig.BAN_CHAT_CHANNELS.contains(chatType))
			{
				activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
			}
			return;
		}
		
		if (activeChar.isJailed() && GeneralConfig.JAIL_DISABLE_CHAT)
		{
			if ((chatType == ChatType.WHISPER) || (chatType == ChatType.SHOUT) || (chatType == ChatType.TRADE) || (chatType == ChatType.HERO_VOICE))
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "no_chat_in_jail"));
				return;
			}
		}
		
		if ((chatType == ChatType.PETITION_PLAYER) && activeChar.isGM())
		{
			chatType = ChatType.PETITION_GM;
		}
		
		if (GeneralConfig.LOG_CHAT)
		{
			LoggingUtils.logChat(LOG_CHAT, chatType == ChatType.WHISPER ? activeChar.getName() : null, _target, _text);
		}
		
		if (_text.indexOf(8) >= 0)
		{
			if (!parseAndPublishItem(activeChar))
			{
				return;
			}
		}
		
		final ChatFilterReturn filter = EventDispatcher.getInstance().notifyEvent(new OnPlayerChat(activeChar, L2World.getInstance().getPlayer(_target), _text, chatType), ChatFilterReturn.class);
		if (filter != null)
		{
			_text = filter.getFilteredText();
		}
		
		// Say Filter implementation
		if (GeneralConfig.USE_SAY_FILTER)
		{
			checkText();
		}
		
		final IChatHandler handler = ChatHandler.getInstance().getHandler(chatType);
		if (handler != null)
		{
			handler.handleChat(chatType, activeChar, _target, _text);
		}
		else
		{
			LOG.info("No handler registered for ChatType: {} Player: {}", _type, getClient());
		}
	}
	
	private boolean checkBot(String text)
	{
		for (String botCommand : WALKER_COMMAND_LIST)
		{
			if (text.startsWith(botCommand))
			{
				return true;
			}
		}
		return false;
	}
	
	private void checkText()
	{
		String filteredText = _text;
		for (String pattern : ChatFilterConfigParser.getInstance().FILTER_LIST)
		{
			filteredText = filteredText.replaceAll("(?i)" + pattern, GeneralConfig.CHAT_FILTER_CHARS);
		}
		_text = filteredText;
	}
	
	private boolean parseAndPublishItem(L2PcInstance owner)
	{
		int pos1 = -1;
		while ((pos1 = _text.indexOf(8, pos1)) > -1)
		{
			int pos = _text.indexOf("ID=", pos1);
			if (pos == -1)
			{
				return false;
			}
			StringBuilder result = new StringBuilder(9);
			pos += 3;
			while (Character.isDigit(_text.charAt(pos)))
			{
				result.append(_text.charAt(pos++));
			}
			int id = Integer.parseInt(result.toString());
			L2Object item = L2World.getInstance().findObject(id);
			if (item instanceof L2ItemInstance)
			{
				if (owner.getInventory().getItemByObjectId(id) == null)
				{
					LOG.info("{} trying publish item which doesnt own! ID: {}", getClient(), id);
					return false;
				}
				((L2ItemInstance) item).publish();
			}
			else
			{
				LOG.info("{} trying publish object which is not item! Object: {}", getClient(), item);
				return false;
			}
			pos1 = _text.indexOf(8, pos) + 1;
			if (pos1 == 0) // missing ending tag
			{
				LOG.info("{} sent invalid publish item msg! ID: {}", getClient(), id);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getType()
	{
		return _C__49_SAY2;
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}
