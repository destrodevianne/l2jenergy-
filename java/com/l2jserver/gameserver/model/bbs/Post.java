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

/**
 * Post.
 * @author Zoey76
 */
public class Post
{
	private int _id;
	private String _ownerName;
	private int _ownerId;
	private long _date;
	private int _topicId;
	private int _forumId;
	private String _txt;
	
	public Post(int id, String ownerName, int ownerId, long date, int topicId, int forumId, String txt)
	{
		_id = id;
		_ownerName = ownerName;
		_ownerId = ownerId;
		_date = date;
		_topicId = topicId;
		_forumId = forumId;
		_txt = txt;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public void setId(int id)
	{
		_id = id;
	}
	
	public String getOwnerName()
	{
		return _ownerName;
	}
	
	public void setOwnerName(String ownerName)
	{
		_ownerName = ownerName;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public void setOwnerId(int ownerId)
	{
		_ownerId = ownerId;
	}
	
	public long getDate()
	{
		return _date;
	}
	
	public void setDate(long date)
	{
		_date = date;
	}
	
	public int getTopicId()
	{
		return _topicId;
	}
	
	public void setTopicId(int topicId)
	{
		_topicId = topicId;
	}
	
	public int getForumId()
	{
		return _forumId;
	}
	
	public void setForumId(int forumId)
	{
		_forumId = forumId;
	}
	
	public String getTxt()
	{
		return _txt;
	}
	
	public void setTxt(String txt)
	{
		_txt = txt;
	}
}
