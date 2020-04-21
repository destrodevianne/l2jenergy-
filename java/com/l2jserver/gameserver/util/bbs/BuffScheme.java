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
package com.l2jserver.gameserver.util.bbs;

import java.util.ArrayList;
import java.util.List;

public class BuffScheme
{
	private final int _id;
	private final String _name;
	private final int _priceId;
	private final long _priceCount;
	private final List<Buff> _buffIds = new ArrayList<>();
	
	public BuffScheme(final int id, final String name, final int priceId, final long priceCount)
	{
		_id = id;
		_name = name;
		_priceId = priceId;
		_priceCount = priceCount;
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
	
	public long getPriceCount()
	{
		return _priceCount;
	}
	
	public void addBuff(final Buff buff)
	{
		_buffIds.add(buff);
	}
	
	public List<Buff> getBuffIds()
	{
		return _buffIds;
	}
}
