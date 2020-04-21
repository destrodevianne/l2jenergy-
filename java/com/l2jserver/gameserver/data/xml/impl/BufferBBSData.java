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

import com.l2jserver.gameserver.util.IXmlReader;
import com.l2jserver.gameserver.util.bbs.Buff;
import com.l2jserver.gameserver.util.bbs.BuffScheme;

public class BufferBBSData implements IXmlReader
{
	private static Map<Integer, BuffScheme> _buffSchemes = new HashMap<>();
	
	protected BufferBBSData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_buffSchemes.clear();
		parseDatapackFile("data/xml/buffer.xml");
		LOG.info("{}: Loaded {} Buff Scheme.", getClass().getSimpleName(), _buffSchemes.size());
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
		{
			if ("list".equalsIgnoreCase(list.getNodeName()))
			{
				for (Node scheme = list.getFirstChild(); scheme != null; scheme = scheme.getNextSibling())
				{
					if ("scheme".equalsIgnoreCase(scheme.getNodeName()))
					{
						int schemeId = Integer.parseInt(scheme.getAttributes().getNamedItem("id").getNodeValue());
						String name = scheme.getAttributes().getNamedItem("name").getNodeValue();
						int priceId = Integer.parseInt(scheme.getAttributes().getNamedItem("priceId").getNodeValue());
						long count = Long.parseLong(scheme.getAttributes().getNamedItem("count").getNodeValue());
						BuffScheme buffScheme = new BuffScheme(schemeId, name, priceId, count);
						
						for (Node cost = scheme.getFirstChild(); cost != null; cost = cost.getNextSibling())
						{
							if ("buff".equalsIgnoreCase(cost.getNodeName()))
							{
								int skillId = Integer.parseInt(cost.getAttributes().getNamedItem("id").getNodeValue());
								int level = Integer.parseInt(cost.getAttributes().getNamedItem("level").getNodeValue());
								Buff buff = new Buff(skillId, level);
								buffScheme.addBuff(buff);
							}
						}
						addBuffScheme(buffScheme);
					}
				}
			}
		}
	}
	
	public void addBuffScheme(BuffScheme buffScheme)
	{
		_buffSchemes.put(buffScheme.getId(), buffScheme);
	}
	
	public BuffScheme getBuffScheme(int buffSchemeId)
	{
		return _buffSchemes.get(buffSchemeId);
	}
	
	public Map<Integer, BuffScheme> getBuffScheme()
	{
		return _buffSchemes;
	}
	
	public static BufferBBSData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BufferBBSData INSTANCE = new BufferBBSData();
	}
}
