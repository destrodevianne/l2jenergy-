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
package com.l2jserver.gameserver.instancemanager.games;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.model.L2MiniGameScore;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class MiniGameScoreManager
{
	protected MiniGameScoreManager()
	{
		DAOFactory.getInstance().getPlayerMinigameScoreDAO().select();
	}
	
	public void addScore(final L2PcInstance player, final int score)
	{
		L2MiniGameScore miniGameScore = null;
		for (final L2MiniGameScore _miniGameScore : _scores)
		{
			if (_miniGameScore.getObjectId() == player.getObjectId())
			{
				miniGameScore = _miniGameScore;
			}
		}
		
		if (miniGameScore == null)
		{
			_scores.add(new L2MiniGameScore(player.getObjectId(), player.getName(), score));
		}
		else
		{
			if (miniGameScore.getScore() > score)
			{
				return;
			}
			miniGameScore.setScore(score);
		}
		DAOFactory.getInstance().getPlayerMinigameScoreDAO().replace(player.getObjectId(), score);
	}
	
	public static void addScore(final int objectId, final int score, final String name)
	{
		_scores.add(new L2MiniGameScore(objectId, name, score));
	}
	
	public NavigableSet<L2MiniGameScore> getScores()
	{
		return _scores;
	}
	
	private final static NavigableSet<L2MiniGameScore> _scores = new ConcurrentSkipListSet<>((o1, o2) -> o2.getScore() - o1.getScore());
	
	public static MiniGameScoreManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MiniGameScoreManager INSTANCE = new MiniGameScoreManager();
	}
}