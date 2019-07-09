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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.enums.ItemMallFlag;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;
import com.l2jserver.gameserver.model.primeshop.L2ProductItemComponent;
import com.l2jserver.gameserver.network.serverpackets.ExBR_BuyProduct;
import com.l2jserver.gameserver.network.serverpackets.ExBR_GamePoint;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.util.IXmlReader;
import com.l2jserver.gameserver.util.TimeUtils;
import com.l2jserver.gameserver.util.Util;

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
						final boolean onSale = (onSaleNode != null) && Boolean.parseBoolean(onSaleNode.getNodeValue());
						if (!onSale)
						{
							continue;
						}
						
						final int productId = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
						
						Node categoryNode = d.getAttributes().getNamedItem("category");
						final int category = categoryNode != null ? Integer.parseInt(categoryNode.getNodeValue()) : 5;
						
						Node priceNode = d.getAttributes().getNamedItem("price");
						final int price = priceNode != null ? Integer.parseInt(priceNode.getNodeValue()) : 0;
						
						Node dayWeekNode = d.getAttributes().getNamedItem("day_of_week");
						final int dayWeek = dayWeekNode != null ? Integer.parseInt(dayWeekNode.getNodeValue()) : Byte.MAX_VALUE;
						
						Node isEventNode = d.getAttributes().getNamedItem("is_event");
						final boolean isEvent = isEventNode != null ? Boolean.parseBoolean(isEventNode.getNodeValue()) : L2ProductItem.DEFAULT_IS_EVENT;
						
						Node startTimeNode = d.getAttributes().getNamedItem("start_sale_date");
						final ZonedDateTime startTimeSale = startTimeNode != null ? getMillisecondsFromString(startTimeNode.getNodeValue()) : L2ProductItem.DEFAULT_START_SALE_DATE;
						
						Node endTimeNode = d.getAttributes().getNamedItem("end_sale_date");
						final ZonedDateTime endTimeSale = endTimeNode != null ? getMillisecondsFromString(endTimeNode.getNodeValue()) : L2ProductItem.DEFAULT_END_SALE_DATE;
						if (endTimeSale.isBefore(ZonedDateTime.now()))
						{
							continue;
						}
						
						Node isMaxStockNode = d.getAttributes().getNamedItem("max_stock");
						int isMaxStock = isMaxStockNode != null ? Integer.parseInt(isMaxStockNode.getNodeValue()) : L2ProductItem.DEFAULT_MAX_STOCK;
						
						final ItemMallFlag event = isEvent ? ItemMallFlag.EVENT : ItemMallFlag.NONE;
						
						ArrayList<L2ProductItemComponent> components = new ArrayList<>();
						final L2ProductItem product = new L2ProductItem(productId, category, price, dayWeek, startTimeSale, endTimeSale, isMaxStock, event);
						
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
	
	public void buyItem(L2PcInstance activeChar, int productId, int count)
	{
		if (activeChar.isOutOfControl())
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_USER_STATE));
			return;
		}
		
		if ((count <= 0) || (count >= 99))
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_USER_STATE));
			Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to buy invalid itemcount [" + count + "] from Prime", IllegalActionPunishmentType.JAIL);
			return;
		}
		
		if (_itemsList.containsKey(Integer.valueOf(productId)))
		{
			L2ProductItem product = ProductItemData.getInstance().getItem(productId);
			
			if (product == null)
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
				Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to buy invalid brId from Prime", IllegalActionPunishmentType.JAIL);
				return;
			}
			else if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & product.getDayWeek()) == 0)
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_DAY_OF_WEEK));
				return;
			}
			else if (!ProductItemData.getInstance().calcStartEndTime(product.getProductId()))
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_SALE_PERIOD_ENDED));
				return;
			}
			else if (product.isLimited() && (product.getLimit() || ((product.getMaxStock() - product.getCurrentStock()) < count))) // TODO: Доделать
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_SOLD_OUT));
				return;
			}
			
			final long totalPoints = product.getPrice() * count;
			
			if (totalPoints < 0)
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
				return;
			}
			
			if (totalPoints > activeChar.getPrimePoints())
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_ENOUGH_POINTS));
				return;
			}
			
			int totalWeight = 0;
			for (final L2ProductItemComponent com : product.getComponents())
			{
				totalWeight += com.getWeight();
			}
			totalWeight *= count; // увеличиваем вес согласно количеству
			int totalCount = 0;
			
			for (final L2ProductItemComponent com : product.getComponents())
			{
				final L2Item item = ItemTable.getInstance().getTemplate(com.getId());
				if (item == null)
				{
					activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT_ITEM));
					return; // what
				}
				
				totalCount += item.isStackable() ? 1 : com.getCount() * count;
			}
			
			if (!activeChar.getInventory().validateCapacity(totalCount) || !activeChar.getInventory().validateWeight(totalWeight))
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_INVENTORY_FULL));
				return;
			}
			
			for (L2ProductItemComponent comp : product.getComponents())
			{
				activeChar.addItem("Buy Product" + productId, comp.getId(), comp.getCount() * count, activeChar, true);
			}
			
			activeChar.setPrimePoints(activeChar.getPrimePoints() - totalPoints);
			
			if (_recentList.get(activeChar.getObjectId()) == null)
			{
				List<L2ProductItem> charList = new ArrayList<>();
				charList.add(product);
				_recentList.put(activeChar.getObjectId(), charList);
			}
			else
			{
				_recentList.get(activeChar.getObjectId()).add(product);
			}
			
			final StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
			activeChar.sendPacket(su);
			
			activeChar.sendPacket(new ExBR_GamePoint(activeChar));
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_OK));
			activeChar.broadcastUserInfo();
			DAOFactory.getInstance().getItemMallDAO().requestBuyItem(activeChar, productId, (byte) count);
		}
		else
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
			Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to buy invalid brId from Prime", IllegalActionPunishmentType.JAIL);
		}
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
