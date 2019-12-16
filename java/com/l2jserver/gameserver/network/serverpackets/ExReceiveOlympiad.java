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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.model.olympiad.AbstractOlympiadGame;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameClassed;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameNonClassed;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameTask;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameTeams;
import com.l2jserver.gameserver.model.olympiad.OlympiadInfo;

public class ExReceiveOlympiad extends L2GameServerPacket
{
	private final List<OlympiadGameTask> _games = new ArrayList<>();
	
	private boolean _tie;
	private int _winTeam; // 1,2
	private int _loseTeam = 2;
	private List<OlympiadInfo> _winnerList;
	private List<OlympiadInfo> _loserList;
	
	private final boolean _isResult;
	
	public ExReceiveOlympiad()
	{
		_isResult = false;
		OlympiadGameTask task;
		for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); i++)
		{
			task = OlympiadGameManager.getInstance().getOlympiadTask(i);
			if (task != null)
			{
				if (!task.isGameStarted() || task.isBattleFinished())
				{
					continue; // initial or finished state not shown
				}
				_games.add(task);
			}
		}
	}
	
	// ExReceiveOlympiadResult
	public ExReceiveOlympiad(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList)
	{
		_isResult = true;
		_tie = tie;
		_winTeam = winTeam;
		_winnerList = winnerList;
		_loserList = loserList;
		
		if (_winTeam == 2)
		{
			_loseTeam = 1;
		}
		else if (_winTeam == 0)
		{
			_winTeam = 1;
		}
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xD4);
		
		if (_isResult)
		{
			writeD(0x01); // Type 0 = Match List, 1 = Match Result
			
			writeD(_tie ? 1 : 0); // 0 - win, 1 - tie
			writeS(_winnerList.get(0).getName());
			writeD(_winTeam);
			writeD(_winnerList.size());
			
			for (OlympiadInfo info : _winnerList)
			{
				writeS(info.getName());
				writeS(info.getClanName());
				writeD(info.getClanId());
				writeD(info.getClassId());
				writeD(info.getDamage());
				writeD(info.getCurrentPoints());
				writeD(info.getDiffPoints());
			}
			
			writeD(_loseTeam);
			writeD(_loserList.size());
			
			for (OlympiadInfo info : _loserList)
			{
				writeS(info.getName());
				writeS(info.getClanName());
				writeD(info.getClanId());
				writeD(info.getClassId());
				writeD(info.getDamage());
				writeD(info.getCurrentPoints());
				writeD(info.getDiffPoints());
			}
		}
		else
		{
			writeD(0x00); // Type 0 = Match List, 1 = Match Result
			
			writeD(_games.size());
			writeD(0x00);
			
			for (OlympiadGameTask curGame : _games)
			{
				AbstractOlympiadGame game = curGame.getGame();
				if (game != null)
				{
					writeD(game.getStadiumId()); // Stadium Id (Arena 1 = 0)
					
					if (game instanceof OlympiadGameNonClassed)
					{
						writeD(1);
					}
					else if (game instanceof OlympiadGameClassed)
					{
						writeD(2);
					}
					else if (game instanceof OlympiadGameTeams)
					{
						writeD(-1);
					}
					else
					{
						writeD(0);
					}
					writeD(curGame.isRunning() ? 0x02 : 0x01); // (1 = Standby, 2 = Playing)
					writeS(game.getPlayerNames()[0]); // Player 1 Name
					writeS(game.getPlayerNames()[1]); // Player 2 Name
				}
			}
		}
	}
}
