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
package com.l2jserver.gameserver.data.xml.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.util.bbs.TeleportPoint;
import com.l2jserver.util.data.xml.IXmlReader;

/**
 * @author Мо3олЬ
 */
public class TeleportBBSData implements IXmlReader
{
	private static Map<Integer, TeleportPoint> _teleport = new HashMap<>();
	
	protected TeleportBBSData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_teleport.clear();
		parseDatapackFile("data/teleports.xml");
		LOG.info("{}: Loaded {} teleport points.", getClass().getSimpleName(), _teleport.size());
		
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
		{
			if ("list".equalsIgnoreCase(list.getNodeName()))
			{
				for (Node point = list.getFirstChild(); point != null; point = point.getNextSibling())
				{
					if ("point".equalsIgnoreCase(point.getNodeName()))
					{
						NamedNodeMap PointMap = point.getAttributes();
						int id = Integer.parseInt(PointMap.getNamedItem("id").getNodeValue());
						String name = PointMap.getNamedItem("name").getNodeValue();
						int minLevel = Integer.parseInt(PointMap.getNamedItem("minLevel").getNodeValue());
						int maxLevel = Integer.parseInt(PointMap.getNamedItem("maxLevel").getNodeValue());
						boolean pk = Boolean.parseBoolean(PointMap.getNamedItem("pk").getNodeValue());
						boolean isPremiumPoint = Boolean.parseBoolean(PointMap.getNamedItem("isPremiumPoint").getNodeValue());
						
						int premiumPriceId = 0;
						int premiumCount = 0;
						int priceId = 0;
						int count = 0;
						
						Node cost = point.getFirstChild();
						if (cost != null)
						{
							cost = cost.getNextSibling();
							if ("cost".equalsIgnoreCase(cost.getNodeName()))
							{
								NamedNodeMap CostMap = cost.getAttributes();
								priceId = Integer.parseInt(CostMap.getNamedItem("itemId").getNodeValue());
								count = Integer.parseInt(CostMap.getNamedItem("count").getNodeValue());
								premiumPriceId = Integer.parseInt(CostMap.getNamedItem("premiumItemId").getNodeValue());
								premiumCount = Integer.parseInt(CostMap.getNamedItem("premiumCount").getNodeValue());
							}
						}
						int x = 83352;
						int y = 145616;
						int z = 62106;
						
						Node coordinates = point.getLastChild();
						if (coordinates != null)
						{
							coordinates = coordinates.getPreviousSibling();
							if ("coordinates".equalsIgnoreCase(coordinates.getNodeName()))
							{
								NamedNodeMap CordMap = coordinates.getAttributes();
								x = Integer.parseInt(CordMap.getNamedItem("x").getNodeValue());
								y = Integer.parseInt(CordMap.getNamedItem("y").getNodeValue());
								z = Integer.parseInt(CordMap.getNamedItem("z").getNodeValue());
							}
						}
						TeleportPoint data = new TeleportPoint(id, name, priceId, count, minLevel, maxLevel, pk, isPremiumPoint, premiumPriceId, premiumCount, new Location(x, y, z));
						_teleport.put(Integer.valueOf(id), data);
					}
				}
			}
		}
	}
	
	public Optional<TeleportPoint> getTeleportId(final int id)
	{
		final Optional<TeleportPoint> teleportPoint = Optional.ofNullable(_teleport.get(id));
		if (teleportPoint.isPresent())
		{
			return teleportPoint;
		}
		return Optional.empty();
	}
	
	public static TeleportBBSData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TeleportBBSData _instance = new TeleportBBSData();
	}
}
