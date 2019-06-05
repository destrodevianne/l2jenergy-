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
package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.instancemanager.games.MiniGameScoreManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class RequestBrMinigameInsertScore extends L2GameClientPacket
{
	private static final String _C__D0_8F_BRMINIGAMEINSERTSCORE = "[C] D0:8F RequestBrMinigameInsertScore";
	
	private int _score;
	
	@Override
	protected void readImpl()
	{
		_score = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if ((player == null) || !Config.EX_JAPAN_MINIGAME)
		{
			return;
		}
		MiniGameScoreManager.getInstance().addScore(player, _score);
	}
	
	@Override
	public String getType()
	{
		return _C__D0_8F_BRMINIGAMEINSERTSCORE;
	}
}