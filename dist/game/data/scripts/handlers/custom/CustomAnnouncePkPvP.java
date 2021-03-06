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
package handlers.custom;

import com.l2jserver.gameserver.configuration.config.custom.CustomConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.events.EventType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * @author Zealar
 */
public class CustomAnnouncePkPvP
{
	
	public CustomAnnouncePkPvP()
	{
		if (CustomConfig.ANNOUNCE_PK_PVP)
		{
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, (OnPlayerPvPKill event) -> OnPlayerPvPKill(event), this));
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	private Object OnPlayerPvPKill(OnPlayerPvPKill event)
	{
		L2PcInstance pk = event.getActiveChar();
		if (pk.isGM())
		{
			return null;
		}
		L2PcInstance player = event.getTarget();
		
		String msg = MessagesData.getInstance().getMessage(player, "announce_pvp_msg").replace("%s%", pk.getName() + "").replace("%i%", player.getName() + "");
		if (player.getPvpFlag() == 0)
		{
			
			msg = MessagesData.getInstance().getMessage(player, "announce_pk_msg").replace("%s%", pk.getName() + "").replace("%i%", player.getName() + "");
		}
		
		if (CustomConfig.ANNOUNCE_PK_PVP_NORMAL_MESSAGE)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1);
			sm.addString(msg);
			Broadcast.toAllOnlinePlayers(sm);
		}
		else
		{
			Broadcast.toAllOnlinePlayers(msg, false);
		}
		return null;
	}
}
