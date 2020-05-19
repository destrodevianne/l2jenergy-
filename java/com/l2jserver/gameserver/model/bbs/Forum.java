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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.enums.ForumType;
import com.l2jserver.gameserver.enums.ForumVisibility;

/**
 * Forum.
 * @author Zoey76
 */
public final class Forum
{
	private final int _id;
	private String _name;
	private ForumType _type;
	private int _post;
	private ForumVisibility _visibility;
	private final Forum _parent;
	private int _ownerId;
	private final List<Forum> _children = new ArrayList<>();
	private final Map<Integer, Topic> _topics = new ConcurrentHashMap<>();
	
	public Forum(int id, Forum parent)
	{
		_id = id;
		_parent = parent;
	}
	
	public Forum(int id, String name, Forum parent, ForumType type, ForumVisibility visibility, int ownerId)
	{
		_name = name;
		_id = id;
		_type = type;
		_post = 0;
		_visibility = visibility;
		_parent = parent;
		_ownerId = ownerId;
	}
	
	public void addChildren(Forum children)
	{
		_children.add(children);
	}
	
	public int getTopicSize()
	{
		return _topics.size();
	}
	
	public Topic getTopic(int j)
	{
		return _topics.get(j);
	}
	
	public void addTopic(Topic t)
	{
		_topics.put(t.getId(), t);
	}
	
	public Forum getChildByName(String name)
	{
		return _children.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}
	
	public void rmTopicByID(int id)
	{
		_topics.remove(id);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		_name = name;
	}
	
	public ForumType getType()
	{
		return _type;
	}
	
	public void setType(ForumType type)
	{
		_type = type;
	}
	
	public int getPost()
	{
		return _post;
	}
	
	public void setPost(int post)
	{
		_post = post;
	}
	
	public ForumVisibility getVisibility()
	{
		return _visibility;
	}
	
	public void setVisibility(ForumVisibility visibility)
	{
		_visibility = visibility;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public void setOwnerId(int ownerId)
	{
		_ownerId = ownerId;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public Forum getParent()
	{
		return _parent;
	}
	
	public List<Forum> getChildren()
	{
		return _children;
	}
	
	public Map<Integer, Topic> getTopics()
	{
		return _topics;
	}
}
