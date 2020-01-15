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

/**
 * @author Мо3олЬ
 */
public class L2MiniGameScore
{
	private final int _objectId;
	private final String _name;
	private int _score;
	
	public L2MiniGameScore(final int objectId, final String name, final int score)
	{
		_objectId = objectId;
		_name = name;
		_score = score;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getScore()
	{
		return _score;
	}
	
	public void setScore(final int score)
	{
		_score = score;
	}
	
	@Override
	public boolean equals(final Object o)
	{
		return !((o == null) || (o.getClass() != L2MiniGameScore.class)) && (((L2MiniGameScore) o).getObjectId() == getObjectId());
	}
	
	@Override
	public int hashCode()
	{
		return _objectId;
	}
}
