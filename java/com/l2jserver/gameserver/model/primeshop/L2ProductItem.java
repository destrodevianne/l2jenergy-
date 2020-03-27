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
package com.l2jserver.gameserver.model.primeshop;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.enums.ItemMallFlag;

public class L2ProductItem
{
	public static final int DEFAULT_MAX_STOCK = -1;
	public static final int DEFAULT_CURRENT_STOCK = 0;
	public static final boolean DEFAULT_IS_EVENT = false;
	
	public static final ZonedDateTime DEFAULT_START_SALE_DATE = ZonedDateTime.of(LocalDateTime.of(1980, 01, 01, 00, 00), ZoneId.systemDefault());
	public static final ZonedDateTime DEFAULT_END_SALE_DATE = ZonedDateTime.of(LocalDateTime.of(2037, 06, 01, 23, 59), ZoneId.systemDefault());
	
	private final int _productId;
	private final int _category;
	private final int _price;
	private final int _maxStock;
	private final int _dayWeek;
	private final ZonedDateTime _startSaleDate;
	private final ZonedDateTime _endSaleDate;
	private ItemMallFlag _eventsFlag;
	
	private List<L2ProductItemComponent> _components = new ArrayList<>(1);
	
	/**
	 * @param productId
	 * @param category
	 * @param price
	 * @param startTimeSale
	 * @param endTimeNode
	 * @param max_stock
	 * @param event
	 */
	public L2ProductItem(int productId, int category, int price, int dayWeek, ZonedDateTime startSaleDate, ZonedDateTime endSaleDate, int isMaxStock, final ItemMallFlag eventsFlag)
	{
		_productId = productId;
		_category = category;
		_price = price;
		_dayWeek = dayWeek;
		_startSaleDate = startSaleDate;
		_endSaleDate = endSaleDate;
		_maxStock = isMaxStock;
		_eventsFlag = eventsFlag;
	}
	
	public void setComponents(ArrayList<L2ProductItemComponent> a)
	{
		_components = a;
	}
	
	public List<L2ProductItemComponent> getComponents()
	{
		return _components;
	}
	
	public int getProductId()
	{
		return _productId;
	}
	
	public int getCategory()
	{
		return _category;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public int getDayWeek()
	{
		return _dayWeek;
	}
	
	public ItemMallFlag getEventFlag()
	{
		return _eventsFlag;
	}
	
	public void enableEventFlag()
	{
		if (isFlag(ItemMallFlag.EVENT))
		{
			return;
		}
		_eventsFlag = ItemMallFlag.EVENT;
	}
	
	public void disableEventFlag()
	{
		if (!isFlag(ItemMallFlag.EVENT))
		{
			return;
		}
		_eventsFlag = ItemMallFlag.NONE;
	}
	
	public void enableBestFlag()
	{
		if (isFlag(ItemMallFlag.BEST))
		{
			return;
		}
		_eventsFlag = ItemMallFlag.BEST;
	}
	
	public void disableBestFlag()
	{
		if (!isFlag(ItemMallFlag.BEST))
		{
			return;
		}
		_eventsFlag = ItemMallFlag.NONE;
	}
	
	public boolean isFlag(final ItemMallFlag g)
	{
		return _eventsFlag == g;
	}
	
	public int getMaxStock()
	{
		return _maxStock;
	}
	
	public boolean getLimit()
	{
		return getCurrentStock() >= _maxStock;
	}
	
	public boolean isLimited()
	{
		return _maxStock > 0;
	}
	
	public long getStartSaleDate()
	{
		return _startSaleDate.toEpochSecond();
	}
	
	public long getEndSaleDate()
	{
		return _endSaleDate.toEpochSecond();
	}
	
	public int getCurrentStock()
	{
		return DEFAULT_CURRENT_STOCK;
	}
	
	public int getStartSaleHour()
	{
		return _startSaleDate.getHour();
	}
	
	public int getEndSaleHour()
	{
		return _endSaleDate.getHour();
	}
	
	public int getStartSaleMin()
	{
		return _startSaleDate.getMinute();
	}
	
	public int getEndSaleMin()
	{
		return _endSaleDate.getMinute();
	}
}
