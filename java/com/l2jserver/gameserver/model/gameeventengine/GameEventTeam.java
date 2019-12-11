/*
 * Copyright (C) 2004-2019 L2J Server
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
package com.l2jserver.gameserver.model.gameeventengine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class GameEventTeam
{
	private final String _name;
	private int[] _coordinates = new int[3];
	private short _points;
	
	private final Map<Integer, L2PcInstance> _participatedPlayers = new ConcurrentHashMap<>();
	
	public GameEventTeam(String name, int[] coordinates)
	{
		_name = name;
		_coordinates = coordinates;
		_points = 0;
	}
	
	public boolean addPlayer(L2PcInstance playerInstance)
	{
		if (playerInstance == null)
		{
			return false;
		}
		_participatedPlayers.put(playerInstance.getObjectId(), playerInstance);
		return true;
	}
	
	public void removePlayer(int playerObjectId)
	{
		_participatedPlayers.remove(playerObjectId);
	}
	
	public void increasePoints()
	{
		++_points;
	}
	
	public void cleanMe()
	{
		_participatedPlayers.clear();
		_points = 0;
	}
	
	public boolean containsPlayer(int playerObjectId)
	{
		return _participatedPlayers.containsKey(playerObjectId);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int[] getCoordinates()
	{
		return _coordinates;
	}
	
	public short getPoints()
	{
		return _points;
	}
	
	public Map<Integer, L2PcInstance> getParticipatedPlayers()
	{
		return _participatedPlayers;
	}
	
	public int getParticipatedPlayerCount()
	{
		return _participatedPlayers.size();
	}
}
