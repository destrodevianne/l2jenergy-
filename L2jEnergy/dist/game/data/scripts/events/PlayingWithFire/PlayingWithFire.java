/*
 * Copyright (C) 2004-2018 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package events.PlayingWithFire;

import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.event.LongTimeEvent;

/**
 * Playing With Fire event.
 * @author U3Games, Sacrifice
 */
public final class PlayingWithFire extends LongTimeEvent
{
	private static final int EVENT_MANAGER = 32099; // Tony the Cat
	
	private PlayingWithFire()
	{
		super(PlayingWithFire.class.getSimpleName(), "events");
		addFirstTalkId(EVENT_MANAGER);
		addStartNpc(EVENT_MANAGER);
		addTalkId(EVENT_MANAGER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		MultisellData.getInstance().separateAndSend(10005, player, npc, false);
		return null;
	}
	
	public static void main(String[] args)
	{
		new PlayingWithFire();
	}
}