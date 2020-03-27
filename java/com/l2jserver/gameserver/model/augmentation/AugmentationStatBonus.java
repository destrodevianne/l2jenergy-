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
package com.l2jserver.gameserver.model.augmentation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.OptionData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.options.Options;

public class AugmentationStatBonus
{
	private static final Logger LOG = LoggerFactory.getLogger(AugmentationStatBonus.class);
	
	private final List<Options> _options = new ArrayList<>();
	private boolean _active;
	
	public AugmentationStatBonus(int augmentationId)
	{
		_active = false;
		int[] stats = new int[2];
		stats[0] = 0x0000FFFF & augmentationId;
		stats[1] = (augmentationId >> 16);
		
		for (int stat : stats)
		{
			Options op = OptionData.getInstance().getOptions(stat);
			if (op != null)
			{
				_options.add(op);
			}
			else
			{
				LOG.warn("{}: Couldn't find option: {}", getClass().getSimpleName(), stat);
			}
		}
	}
	
	public void applyBonus(L2PcInstance player)
	{
		// make sure the bonuses are not applied twice..
		if (_active)
		{
			return;
		}
		
		for (Options op : _options)
		{
			op.apply(player);
		}
		_active = true;
	}
	
	public void removeBonus(L2PcInstance player)
	{
		// make sure the bonuses are not removed twice
		if (!_active)
		{
			return;
		}
		
		for (Options op : _options)
		{
			op.remove(player);
		}
		_active = false;
	}
}
