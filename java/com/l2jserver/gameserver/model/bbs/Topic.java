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
package com.l2jserver.gameserver.model.bbs;

import com.l2jserver.gameserver.enums.TopicType;

/**
 * Topic.
 * @author Zoey76
 */
public class Topic
{
	private final int _id;
	private final int _forumId;
	private final String _name;
	private final long _date;
	private final String _ownerName;
	private final int _ownerId;
	private final TopicType _type;
	private final int _reply;
	
	public Topic(int id, int forumId, String name, long date, String ownerName, int ownerId, TopicType type, int reply)
	{
		_id = id;
		_forumId = forumId;
		_name = name;
		_date = date;
		_ownerName = ownerName;
		_ownerId = ownerId;
		_type = type;
		_reply = reply;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getForumId()
	{
		return _forumId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public long getDate()
	{
		return _date;
	}
	
	public String getOwnerName()
	{
		return _ownerName;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public TopicType getType()
	{
		return _type;
	}
	
	public int getReply()
	{
		return _reply;
	}
}
