/*
 * Copyright (C) 2004-2018 L2J Server
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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

import com.l2jserver.gameserver.instancemanager.games.MiniGameScoreManager;
import com.l2jserver.gameserver.model.L2MiniGameScore;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class ExBrMiniGameLoadScores extends L2GameServerPacket
{
	private int _place;
	private int _score;
	private int _lastScore;
	
	private final List<L2MiniGameScore> _entries;
	
	public ExBrMiniGameLoadScores(final L2PcInstance player)
	{
		int i = 1;
		
		final NavigableSet<L2MiniGameScore> score = MiniGameScoreManager.getInstance().getScores();
		_entries = new ArrayList<>(score.size() >= 100 ? 100 : score.size());
		
		final L2MiniGameScore last = score.isEmpty() ? null : score.last();
		if (last != null)
		{
			_lastScore = last.getScore();
		}
		
		for (final L2MiniGameScore entry : score)
		{
			if (i > 100)
			{
				break;
			}
			
			if (entry.getObjectId() == player.getObjectId())
			{
				_place = i;
				_score = entry.getScore();
			}
			_entries.add(entry);
			i++;
		}
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xDD);
		
		writeD(_place); // place of last big score of player
		writeD(_score); // last big score of player
		writeD(_entries.size()); //
		writeD(_lastScore); // last score of list
		for (int i = 0; i < _entries.size(); i++)
		{
			final L2MiniGameScore pair = _entries.get(i);
			writeD(i + 1);
			writeS(pair.getName());
			writeD(pair.getScore());
		}
	}
}