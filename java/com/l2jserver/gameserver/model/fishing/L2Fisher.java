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
package com.l2jserver.gameserver.model.fishing;

public class L2Fisher
{
	private double _length;
	private final String _name;
	private int _reward;
	
	public L2Fisher(String name, double length, int rewardType)
	{
		_name = name;
		_length = length;
		_reward = rewardType;
	}
	
	public void setLength(double value)
	{
		_length = value;
	}
	
	public void setRewardType(int value)
	{
		_reward = value;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getRewardType()
	{
		return _reward;
	}
	
	public double getLength()
	{
		return _length;
	}
}
