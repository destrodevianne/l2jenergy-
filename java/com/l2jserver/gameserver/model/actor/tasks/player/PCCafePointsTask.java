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
package com.l2jserver.gameserver.model.actor.tasks.player;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.configuration.config.events.PcCafeConfig;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

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
		if ((_player == null) || (_player.getClient() == null))
		{
			return;
		}
		
		if (_player.isInOfflineMode() || (_player.getLevel() < PcCafeConfig.ALT_PCBANG_POINTS_MIN_LVL))
		{
			return;
		}
		
		if (_player.isPremium())
		{
			_player.increasePcCafePoints(PcCafeConfig.ALT_PCBANG_POINTS_BONUS, (PcCafeConfig.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0) && (Rnd.get(100) < PcCafeConfig.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
		}
	}
}
