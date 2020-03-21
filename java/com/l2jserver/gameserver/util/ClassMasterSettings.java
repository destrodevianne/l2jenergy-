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
package com.l2jserver.gameserver.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.model.holders.ItemHolder;

/**
 * @author Мо3олЬ
 */
public class ClassMasterSettings
{
	private final Map<Integer, List<ItemHolder>> _claimItems = new HashMap<>(3);
	private final Map<Integer, List<ItemHolder>> _rewardItems = new HashMap<>(3);
	private final Map<Integer, Boolean> _allowedClassChange = new HashMap<>(3);
	
	public ClassMasterSettings(String configLine)
	{
		parseConfigLine(configLine.trim());
	}
	
	private void parseConfigLine(String configLine)
	{
		if (configLine.isEmpty())
		{
			return;
		}
		
		final StringTokenizer st = new StringTokenizer(configLine, ";");
		
		while (st.hasMoreTokens())
		{
			// get allowed class change
			final int job = Integer.parseInt(st.nextToken());
			
			_allowedClassChange.put(job, true);
			
			final List<ItemHolder> requiredItems = new ArrayList<>();
			// parse items needed for class change
			if (st.hasMoreTokens())
			{
				final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
				
				while (st2.hasMoreTokens())
				{
					final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
					final int itemId = Integer.parseInt(st3.nextToken());
					final int quantity = Integer.parseInt(st3.nextToken());
					requiredItems.add(new ItemHolder(itemId, quantity));
				}
			}
			
			_claimItems.put(job, requiredItems);
			
			final List<ItemHolder> rewardItems = new ArrayList<>();
			// parse gifts after class change
			if (st.hasMoreTokens())
			{
				final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
				
				while (st2.hasMoreTokens())
				{
					final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
					final int itemId = Integer.parseInt(st3.nextToken());
					final int quantity = Integer.parseInt(st3.nextToken());
					rewardItems.add(new ItemHolder(itemId, quantity));
				}
			}
			
			_rewardItems.put(job, rewardItems);
		}
	}
	
	public boolean isAllowed(int job)
	{
		if ((_allowedClassChange == null) || !_allowedClassChange.containsKey(job))
		{
			return false;
		}
		return _allowedClassChange.get(job);
	}
	
	public List<ItemHolder> getRewardItems(int job)
	{
		return _rewardItems.get(job);
	}
	
	public List<ItemHolder> getRequireItems(int job)
	{
		return _claimItems.get(job);
	}
}
