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
package com.l2jserver.gameserver.data.xml.impl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.TranslationMessage;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.IXmlReader;

/**
 * @author Мо3олЬ
 */
public class MessagesData implements IXmlReader
{
	private final Map<String, TranslationMessage> _translation = new HashMap<>();
	
	protected MessagesData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_translation.clear();
		parseDatapackDirectory("data/translation", false);
		LOG.info("{}: Loaded {} translation messages", getClass().getSimpleName(), _translation.size());
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node d = doc.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if ("list".equalsIgnoreCase(d.getNodeName()))
			{
				for (Node h = d.getFirstChild(); h != null; h = h.getNextSibling())
				{
					if ("message".equalsIgnoreCase(h.getNodeName()))
					{
						final String id = h.getAttributes().getNamedItem("id").getNodeValue();
						TranslationMessage message = new TranslationMessage(id);
						
						for (Node n = h.getFirstChild(); n != null; n = n.getNextSibling())
						{
							if ("set".equalsIgnoreCase(n.getNodeName()))
							{
								message.addNewMessage(n.getAttributes().getNamedItem("lang").getNodeValue(), n.getAttributes().getNamedItem("val").getNodeValue());
							}
						}
						_translation.put(id, message);
					}
				}
			}
		}
	}
	
	public String getMessage(L2PcInstance player, String id)
	{
		if ((player == null) || (player.getLang() == null) || !_translation.containsKey(id))
		{
			return id;
		}
		
		if (_translation.get(id).getMessageByLang(player.getLang()) != null)
		{
			return _translation.get(id).getMessageByLang(player.getLang());
		}
		return _translation.get(id).getMessageByLang("en");
	}
	
	public static MessagesData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final MessagesData _instance = new MessagesData();
	}
}