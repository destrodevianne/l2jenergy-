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
package com.l2jserver.gameserver.model.itemcontainer;

import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.enums.ItemLocation;
import com.l2jserver.gameserver.enums.actors.Stats;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public class PcFreight extends ItemContainer
{
	private final L2PcInstance _owner;
	private final int _ownerId;
	
	public PcFreight(int object_id)
	{
		_owner = null;
		_ownerId = object_id;
		restore();
	}
	
	public PcFreight(L2PcInstance owner)
	{
		_owner = owner;
		_ownerId = owner.getObjectId();
	}
	
	@Override
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	@Override
	public L2PcInstance getOwner()
	{
		return _owner;
	}
	
	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.FREIGHT;
	}
	
	@Override
	public String getName()
	{
		return "Freight";
	}
	
	@Override
	public boolean validateCapacity(long slots)
	{
		int curSlots = _owner == null ? CharacterConfig.ALT_FREIGHT_SLOTS : CharacterConfig.ALT_FREIGHT_SLOTS + (int) _owner.getStat().calcStat(Stats.FREIGHT_LIM, 0, null, null);
		return ((getSize() + slots) <= curSlots);
	}
	
	@Override
	public void refreshWeight()
	{
	}
}