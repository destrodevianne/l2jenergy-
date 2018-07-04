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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.primeshop.L2ProductItem;
import com.l2jserver.gameserver.model.primeshop.L2ProductItemComponent;
import com.l2jserver.gameserver.util.TimeUtils;
import com.l2jserver.util.data.xml.IXmlReader;

/**
 * @author Мо3олЬ
 */
public class ProductItemData implements IXmlReader
{
	private final Map<Integer, L2ProductItem> _itemsList = new HashMap<>();
	private final ConcurrentHashMap<Integer, List<L2ProductItem>> _recentList = new ConcurrentHashMap<>();
	
	protected ProductItemData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_itemsList.clear();
		parseDatapackDirectory("data/itemMall", false);
		LOG.info("{}: Loaded {} products", getClass().getSimpleName(), _itemsList.size());
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("product".equalsIgnoreCase(d.getNodeName()))
					{
						Node onSaleNode = d.getAttributes().getNamedItem("on_sale");
						Boolean onSale = (onSaleNode != null) && Boolean.parseBoolean(onSaleNode.getNodeValue());
						if (!onSale)
						{
							continue;
						}
						
						Node endTimeNode = d.getAttributes().getNamedItem("end_sale_date");
						ZonedDateTime endTimeSale = endTimeNode != null ? getMillisecondsFromString(endTimeNode.getNodeValue()) : L2ProductItem.DEFAULT_END_SALE_DATE;
						if (endTimeSale.isBefore(ZonedDateTime.now()))
						{
							continue;
						}
						
						int productId = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
						
						Node categoryNode = d.getAttributes().getNamedItem("category");
						int category = categoryNode != null ? Integer.parseInt(categoryNode.getNodeValue()) : 5;
						
						Node priceNode = d.getAttributes().getNamedItem("price");
						int price = priceNode != null ? Integer.parseInt(priceNode.getNodeValue()) : 0;
						
						Node isEventNode = d.getAttributes().getNamedItem("is_event");
						Boolean isEvent = (isEventNode != null) && Boolean.parseBoolean(isEventNode.getNodeValue());
						
						Node isBestNode = d.getAttributes().getNamedItem("is_best");
						Boolean isBest = (isBestNode != null) && Boolean.parseBoolean(isBestNode.getNodeValue());
						
						Node isNewNode = d.getAttributes().getNamedItem("is_new");
						Boolean isNew = (isNewNode != null) && Boolean.parseBoolean(isNewNode.getNodeValue());
						int tabId = getProductTabId(isEvent, isBest, isNew);
						
						Node isMaxStockNode = d.getAttributes().getNamedItem("max_stock");
						int isMaxStock = isMaxStockNode != null ? Integer.parseInt(isMaxStockNode.getNodeValue()) : L2ProductItem.DEFAULT_MAX_STOCK;
						
						Node startTimeNode = d.getAttributes().getNamedItem("start_sale_date");
						ZonedDateTime startTimeSale = startTimeNode != null ? getMillisecondsFromString(startTimeNode.getNodeValue()) : L2ProductItem.DEFAULT_START_SALE_DATE;
						
						Node dayWeekNode = d.getAttributes().getNamedItem("day_of_week");
						int dayWeek = dayWeekNode != null ? Integer.parseInt(dayWeekNode.getNodeValue()) : Byte.MAX_VALUE;
						
						ArrayList<L2ProductItemComponent> components = new ArrayList<>();
						final L2ProductItem product = new L2ProductItem(productId, category, price, dayWeek, startTimeSale, endTimeSale, isMaxStock, tabId);
						
						for (Node t1 = d.getFirstChild(); t1 != null; t1 = t1.getNextSibling())
						{
							if ("item".equalsIgnoreCase(t1.getNodeName()))
							{
								int id = Integer.parseInt(t1.getAttributes().getNamedItem("id").getNodeValue());
								int count = Integer.parseInt(t1.getAttributes().getNamedItem("count").getNodeValue());
								L2ProductItemComponent component = new L2ProductItemComponent(id, count);
								components.add(component);
							}
						}
						product.setComponents(components);
						_itemsList.put(productId, product);
					}
				}
			}
		}
	}
	
	/**
	 * @param isEvent boolean
	 * @param isBest boolean
	 * @param isNew boolean
	 * @return int
	 */
	private static int getProductTabId(boolean isEvent, boolean isBest, boolean isNew)
	{
		if (isEvent && isBest)
		{
			return 3;
		}
		if (isEvent)
		{
			return 1;
		}
		if (isBest)
		{
			return 2;
		}
		return 4;
	}
	
	private static ZonedDateTime getMillisecondsFromString(final String datetime)
	{
		try
		{
			return TimeUtils.DATE_TIME_FORMATTER.withZone(ZoneId.systemDefault()).parse(datetime, ZonedDateTime::from);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean calcStartEndTime(int productId)
	{
		if (_itemsList.get(productId) == null)
		{
			return false;
		}
		if ((_itemsList.get(productId).getStartSaleDate() * 1000) >= System.currentTimeMillis())
		{
			return false;
		}
		if ((_itemsList.get(productId).getEndSaleDate() * 1000) <= System.currentTimeMillis())
		{
			return false;
		}
		return true;
	}
	
	public Collection<L2ProductItem> getProducts()
	{
		return _itemsList.values();
	}
	
	public L2ProductItem getItem(int id)
	{
		return _itemsList.get(id);
	}
	
	public List<L2ProductItem> getRecentListByOID(int objId)
	{
		return _recentList.get(objId) == null ? new ArrayList<>() : _recentList.get(objId);
	}
	
	public static ProductItemData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ProductItemData _instance = new ProductItemData();
	}
}
