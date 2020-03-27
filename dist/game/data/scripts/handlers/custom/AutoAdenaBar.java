/*
 * Copyright © 2004-2019 L2jEnergy Server
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
package handlers.custom;

import com.l2jserver.gameserver.configuration.config.custom.CustomConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.events.EventType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.impl.character.player.inventory.OnPlayerItemAdd;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author Zealar, Мо3олЬ
 */
public class AutoAdenaBar
{
	public static int GOLD_BAR_ID = 3470;
	
	public AutoAdenaBar()
	{
		if (CustomConfig.BANKING_SYSTEM_AUTO_ENABLED)
		{
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_ITEM_ADD, (OnPlayerItemAdd event) -> OnPlayerItemAdd(event), this));
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	private Object OnPlayerItemAdd(OnPlayerItemAdd event)
	{
		L2PcInstance player = event.getActiveChar();
		
		if (player.getInventory().getInventoryItemCount(57, 0) > CustomConfig.BANKING_SYSTEM_AUTO_MIN_ADENA)
		{
			if (!player.reduceAdena("Goldbar", CustomConfig.BANKING_SYSTEM_ADENA, player, false))
			{
				return false;
			}
			player.getInventory().addItem("Goldbar", GOLD_BAR_ID, CustomConfig.BANKING_SYSTEM_GOLDBARS, player, null);
			player.getInventory().updateDatabase();
			player.sendMessage(MessagesData.getInstance().getMessage(player, "dp_handler_you_now_have_auto_goldbar_and_adena").replace("%i%", CustomConfig.BANKING_SYSTEM_GOLDBARS + "").replace("%s%", CustomConfig.BANKING_SYSTEM_ADENA + ""));
		}
		return true;
	}
}
