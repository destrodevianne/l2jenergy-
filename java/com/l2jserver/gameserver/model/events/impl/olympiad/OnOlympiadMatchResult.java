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
package com.l2jserver.gameserver.model.events.impl.olympiad;

import com.l2jserver.gameserver.enums.OlympiadType;
import com.l2jserver.gameserver.enums.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.olympiad.Participant;

/**
 * @author UnAfraid
 */
public class OnOlympiadMatchResult implements IBaseEvent
{
	private final Participant _winner;
	private final Participant _loser;
	private final OlympiadType _type;
	
	public OnOlympiadMatchResult(Participant winner, Participant looser, OlympiadType type)
	{
		_winner = winner;
		_loser = looser;
		_type = type;
	}
	
	public Participant getWinner()
	{
		return _winner;
	}
	
	public Participant getLoser()
	{
		return _loser;
	}
	
	public OlympiadType getCompetitionType()
	{
		return _type;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_OLYMPIAD_MATCH_RESULT;
	}
}
