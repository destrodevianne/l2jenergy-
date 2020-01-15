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
package com.l2jserver.gameserver.util;

import com.l2jserver.gameserver.configuration.config.FloodConfig;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.network.L2GameClient;

public final class FloodProtectors
{
	public static enum Action
	{
		USE_ITEM(FloodConfig.USE_ITEM_TIME),
		ROLL_DICE(FloodConfig.ROLL_DICE_TIME),
		FIREWORK(FloodConfig.FIREWORK_TIME),
		ITEM_PET_SUMMON(FloodConfig.ITEM_PET_SUMMON_TIME),
		HERO_VOICE(FloodConfig.HERO_VOICE_TIME),
		GLOBAL_CHAT(FloodConfig.GLOBAL_CHAT_TIME),
		TRADE_CHAT(FloodConfig.TRADE_CHAT_TIME),
		SUBCLASS(FloodConfig.SUBCLASS_TIME),
		DROP_ITEM(FloodConfig.DROP_ITEM_TIME),
		SERVER_BYPASS(FloodConfig.SERVER_BYPASS_TIME),
		MULTISELL(FloodConfig.MULTISELL_TIME),
		TRANSACTION(FloodConfig.TRANSACTION_TIME),
		MANUFACTURE(FloodConfig.MANUFACTURE_TIME),
		MANOR(FloodConfig.MANOR_TIME),
		SENDMAIL(FloodConfig.SENDMAIL_TIME),
		CHARACTER_SELECT(FloodConfig.CHARACTER_SELECT_TIME),
		ITEM_AUCTION(FloodConfig.ITEM_AUCTION_TIME);
		
		private final int _reuseDelay;
		
		private Action(int reuseDelay)
		{
			_reuseDelay = reuseDelay;
		}
		
		public int getReuseDelay()
		{
			return _reuseDelay;
		}
		
		public static final int VALUES_LENGTH = Action.values().length;
	}
	
	/**
	 * Try to perform an action according to client FPs value. A 0 reuse delay means the action is always possible.
	 * @param client : The client to check protectors on.
	 * @param action : The action to track.
	 * @return True if the action is possible, False otherwise.
	 */
	public static boolean performAction(final L2GameClient client, Action action)
	{
		if ((client.getActiveChar() != null) && client.getActiveChar().canOverrideCond(PcCondOverride.FLOOD_CONDITIONS))
		{
			return true;
		}
		
		final int reuseDelay = action.getReuseDelay();
		if (reuseDelay == 0)
		{
			return true;
		}
		
		long[] value = client.getFloodProtectors();
		
		synchronized (value)
		{
			if (value[action.ordinal()] > System.currentTimeMillis())
			{
				return false;
			}
			
			value[action.ordinal()] = System.currentTimeMillis() + reuseDelay;
			return true;
		}
	}
}
