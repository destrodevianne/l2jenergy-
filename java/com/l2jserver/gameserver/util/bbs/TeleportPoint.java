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
package com.l2jserver.gameserver.util.bbs;

import com.l2jserver.gameserver.model.Location;

public class TeleportPoint
{
	private final int _id;
	private final String _name;
	private final int _priceId;
	private final int _priceCount;
	private final int _minLevel;
	private final int _maxLevel;
	private final boolean _pk;
	private final boolean _premium;
	private final int _premiumPriceId;
	private final int _premiumCount;
	private final Location _location;
	
	public TeleportPoint(int id, String name, int priceId, int priceCount, int minLevel, int maxLevel, boolean pk, boolean premium, int premiumPriceId, int premiumCount, final Location location)
	{
		_id = id;
		_name = name;
		_priceId = priceId;
		_priceCount = priceCount;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_pk = pk;
		_premium = premium;
		_premiumPriceId = premiumPriceId;
		_premiumCount = premiumCount;
		_location = location;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getPriceId()
	{
		return _priceId;
	}
	
	public int getPriceCount()
	{
		return _priceCount;
	}
	
	public int getMinLevel()
	{
		return _minLevel;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public boolean isPk()
	{
		return _pk;
	}
	
	public boolean isPremium()
	{
		return _premium;
	}
	
	public int getPremiumPriceId()
	{
		return _premiumPriceId;
	}
	
	public int getPremiumPriceCount()
	{
		return _premiumCount;
	}
	
	public Location getLocation()
	{
		return _location;
	}
}
