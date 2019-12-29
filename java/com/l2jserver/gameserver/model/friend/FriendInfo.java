/*
 * Copyright (C) 2004-2019 L2jEnergy Server
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
package com.l2jserver.gameserver.model.friend;

/**
 * @author Мо3олЬ
 */
public class FriendInfo
{
	private final int _objId;
	private final String _name;
	private final boolean _online;
	private int _classid;
	private int _level;
	
	public FriendInfo(int objId, String name, boolean online)
	{
		_objId = objId;
		_name = name;
		_online = online;
	}
	
	public FriendInfo(int objId, String name, boolean online, int classid, int level)
	{
		_objId = objId;
		_name = name;
		_online = online;
		_classid = classid;
		_level = level;
	}
	
	public int getCharId()
	{
		return _objId;
	}
	
	public String getCharName()
	{
		return _name;
	}
	
	public boolean getCharOnline()
	{
		return _online;
	}
	
	public int getCharClassId()
	{
		return _classid;
	}
	
	public int getCharLevel()
	{
		return _level;
	}
}
