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
package com.l2jserver.gameserver.model;

import com.l2jserver.commons.util.Rnd;

public class FestivalSpawn
{
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final int _npcId;
	
	public FestivalSpawn(int x, int y, int z, int heading)
	{
		_x = x;
		_y = y;
		_z = z;
		
		// Generate a random heading if no positive one given.
		_heading = (heading < 0) ? Rnd.nextInt(65536) : heading;
		_npcId = -1;
	}
	
	public FestivalSpawn(int[] spawnData)
	{
		_x = spawnData[0];
		_y = spawnData[1];
		_z = spawnData[2];
		
		_heading = (spawnData[3] < 0) ? Rnd.nextInt(65536) : spawnData[3];
		
		if (spawnData.length > 4)
		{
			_npcId = spawnData[4];
		}
		else
		{
			_npcId = -1;
		}
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public int getZ()
	{
		return _z;
	}
	
	public int getHeading()
	{
		return _heading;
	}
	
	public int getNpcId()
	{
		return _npcId;
	}
}
