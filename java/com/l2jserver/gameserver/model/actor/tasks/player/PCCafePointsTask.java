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
package com.l2jserver.gameserver.model.actor.tasks.player;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.util.Rnd;

/**
 * @author Мо3олЬ
 */
public class PCCafePointsTask implements Runnable
{
	private final L2PcInstance _player;
	
	public PCCafePointsTask(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		if (_player == null)
		{
			return;
		}
		
		if (_player.isInOfflineMode() || (_player.getLevel() < Config.ALT_PCBANG_POINTS_MIN_LVL))
		{
			return;
		}
		
		if (_player.isPremium())
		{
			_player.increasePcCafePoints(Config.ALT_PCBANG_POINTS_BONUS, (Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0) && (Rnd.get(100) < Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
		}
	}
}
