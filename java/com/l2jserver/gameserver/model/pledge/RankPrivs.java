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

import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.util.EnumIntBitmask;

/**
 * @author Мо3олЬ
 */
public class RankPrivs
{
	private final int _rankId;
	private final int _party;// TODO find out what this stuff means and implement it
	private final EnumIntBitmask<ClanPrivilege> _rankPrivs;
	
	public RankPrivs(int rank, int party, int privs)
	{
		_rankId = rank;
		_party = party;
		_rankPrivs = new EnumIntBitmask<>(ClanPrivilege.class, privs);
	}
	
	public RankPrivs(int rank, int party, EnumIntBitmask<ClanPrivilege> rankPrivs)
	{
		_rankId = rank;
		_party = party;
		_rankPrivs = rankPrivs;
	}
	
	public int getRank()
	{
		return _rankId;
	}
	
	public int getParty()
	{
		return _party;
	}
	
	public EnumIntBitmask<ClanPrivilege> getPrivs()
	{
		return _rankPrivs;
	}
	
	public void setPrivs(int privs)
	{
		_rankPrivs.setBitmask(privs);
	}
}
