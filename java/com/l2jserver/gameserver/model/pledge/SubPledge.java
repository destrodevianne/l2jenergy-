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
package com.l2jserver.gameserver.model.pledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.model.skills.Skill;

/**
 * @author Мо3олЬ
 */
public class SubPledge
{
	private final int _id;
	private String _subPledgeName;
	private int _leaderId;
	private final Map<Integer, Skill> _subPledgeSkills = new HashMap<>();
	
	public SubPledge(int id, String name, int leaderId)
	{
		_id = id;
		_subPledgeName = name;
		_leaderId = leaderId;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _subPledgeName;
	}
	
	public void setName(String name)
	{
		_subPledgeName = name;
	}
	
	public int getLeaderId()
	{
		return _leaderId;
	}
	
	public void setLeaderId(int leaderId)
	{
		_leaderId = leaderId;
	}
	
	public Skill addNewSkill(Skill skill)
	{
		return _subPledgeSkills.put(skill.getId(), skill);
	}
	
	public Collection<Skill> getSkills()
	{
		return _subPledgeSkills.values();
	}
	
	public Skill getSkill(int id)
	{
		return _subPledgeSkills.get(id);
	}
}
