/*
 * Copyright (C) 2004-2020 L2J DataPack
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
package quests.Q00341_HuntingForWildBeasts;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Hunting for Wild Beasts (341)
 * @author xban1x
 */
public class Q00341_HuntingForWildBeasts extends Quest
{
	// NPCs
	private static final int PANO = 30078;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(20203, 99);
		MONSTERS.put(20310, 87);
		MONSTERS.put(20021, 83);
		MONSTERS.put(20335, 87);
	}
	// Items
	private static final int BEAR_SKIN = 4259;
	// Misc
	private static final int MIN_LVL = 20;
	private static final int ADENA_COUNT = 3710;
	private static final int REQUIRED_COUNT = 20;
	
	public Q00341_HuntingForWildBeasts()
	{
		super(341, Q00341_HuntingForWildBeasts.class.getSimpleName(), "Hunting for Wild Beasts");
		addStartNpc(PANO);
		addTalkId(PANO);
		addKillId(MONSTERS.keySet());
		registerQuestItems(BEAR_SKIN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs != null)
		{
			switch (event)
			{
				case "30078-03.htm":
				{
					htmltext = event;
					break;
				}
				case "30078-04.htm":
				{
					qs.startQuest();
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = talker.getLevel() >= MIN_LVL ? "30078-01.html" : "30078-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(2) && (qs.getQuestItemsCount(BEAR_SKIN) >= REQUIRED_COUNT))
				{
					qs.giveAdena(ADENA_COUNT, true);
					qs.exitQuest(true, true);
					htmltext = "30078-05.html";
				}
				else
				{
					htmltext = "30078-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			long skins = qs.getQuestItemsCount(BEAR_SKIN);
			if (skins < REQUIRED_COUNT)
			{
				if (getRandom(100) < MONSTERS.get(npc.getId()))
				{
					qs.giveItems(BEAR_SKIN, 1);
					if ((++skins) < REQUIRED_COUNT)
					{
						qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						qs.setCond(2, true);
					}
				}
			}
		}
		return super.onKill(npc, killer, isPet);
	}
}
