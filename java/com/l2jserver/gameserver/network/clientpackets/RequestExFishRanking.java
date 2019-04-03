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

import com.l2jserver.gameserver.configuration.config.events.FishingConfig;
import com.l2jserver.gameserver.instancemanager.games.FishingChampionshipManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: (ch) just a trigger
 * @author -Wooden-
 */
public final class RequestExFishRanking extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (FishingConfig.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionshipManager.getInstance().showMidResult(activeChar);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:18 RequestExFishRanking";
	}
}