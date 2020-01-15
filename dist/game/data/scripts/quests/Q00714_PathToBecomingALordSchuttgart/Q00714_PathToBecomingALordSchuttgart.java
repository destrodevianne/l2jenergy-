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
package quests.Q00714_PathToBecomingALordSchuttgart;

import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

import quests.Q00114_ResurrectionOfAnOldManager.Q00114_ResurrectionOfAnOldManager;
import quests.Q00120_PavelsLastResearch.Q00120_PavelsLastResearch;
import quests.Q00121_PavelTheGiant.Q00121_PavelTheGiant;

/**
 * Path to Becoming a Lord - Schuttgart (714)
 * @author Pandragon
 */
public class Q00714_PathToBecomingALordSchuttgart extends Quest
{
	// NPCs
	private static final int AUGUST = 35555;
	private static final int NEWYEAR = 31961;
	private static final int YASENI = 31958;
	// Item
	private static final int GOLEM_SHARD = 17162;
	// Monsters
	private static final int[] GOLEMS =
	{
		22809, // Guard Golem
		22810, // Micro Scout Golem
		22811, // Great Chaos Golem
		22812, // Boom Golem
	};
	// Misc
	private static final int CASTLE_ID = 9; // Schuttgart
	
	public Q00714_PathToBecomingALordSchuttgart()
	{
		super(714, Q00714_PathToBecomingALordSchuttgart.class.getSimpleName(), "Path to Becoming a Lord - Schuttgart");
		addStartNpc(AUGUST);
		addTalkId(AUGUST, NEWYEAR, YASENI);
		addKillId(GOLEMS);
		registerQuestItems(GOLEM_SHARD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "35555-02.htm":
			case "35555-06.html":
			case "31961-03.html":
			{
				htmltext = event;
				break;
			}
			case "35555-04.html":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "35555-08.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "31961-04.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "31958-02.html":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
					htmltext = event;
				}
				break;
			}
			case "35555-13.html":
			{
				if (qs.isCond(7))
				{
					if (CastleManager.getInstance().getCastleById(CASTLE_ID).getSiege().isInProgress())
					{
						return "35555-12a.html";
					}
					
					for (Fort fort : FortManager.getInstance().getForts())
					{
						if (!fort.isBorderFortress() && fort.getSiege().isInProgress())
						{
							return "35555-12a.html";
						}
						else if (!fort.isBorderFortress() && (fort.getContractedCastleId() != CASTLE_ID))
						{
							return "35555-12b.html";
						}
					}
					
					// DeclareLord(schuttgart_dominion, player);
					CastleManager.getInstance().getCastleById(CASTLE_ID).setShowNpcCrest(true);
					NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_SCHUTTGART_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_SCHUTTGART);
					packet.addStringParameter(player.getName());
					npc.broadcastPacket(packet);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		if ((talker.getClan() == null) || (talker.getClan().getLeaderId() != CastleManager.getInstance().getCastleById(CASTLE_ID).getOwnerId()))
		{
			return htmltext;
		}
		
		switch (npc.getId())
		{
			case AUGUST:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = (talker.getId() == talker.getClan().getLeaderId()) ? "35555-01.htm" : "35555-03.html";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "35555-06.html";
								break;
							}
							case 2:
							case 3:
							{
								htmltext = "35555-09.html";
								break;
							}
							case 4:
							{
								htmltext = "35555-10.html";
								break;
							}
							case 5:
							case 6:
							{
								htmltext = "35555-11.html";
								break;
							}
							case 7:
							{
								htmltext = "35555-12.html";
								break;
							}
						}
					}
					case State.COMPLETED:
					{
						htmltext = getAlreadyCompletedMsg(talker);
						break;
					}
				}
				break;
			}
			case NEWYEAR:
			{
				switch (qs.getCond())
				{
					case 2:
					{
						htmltext = "31961-02.html";
						break;
					}
					case 3:
					{
						QuestState qs1 = getQuestState(talker, false);
						qs1 = talker.getQuestState(Q00114_ResurrectionOfAnOldManager.class.getSimpleName());
						QuestState qs2 = getQuestState(talker, false);
						qs2 = talker.getQuestState(Q00120_PavelsLastResearch.class.getSimpleName());
						QuestState qs3 = getQuestState(talker, false);
						qs3 = talker.getQuestState(Q00121_PavelTheGiant.class.getSimpleName());
						
						if ((qs3 != null) && qs3.isCompleted())
						{
							if ((qs1 != null) && qs1.isCompleted())
							{
								if ((qs2 != null) && qs2.isCompleted())
								{
									qs.setCond(4);
									htmltext = "31961-01.html";
								}
								else
								{
									htmltext = "31961-06.html";
								}
							}
							else
							{
								htmltext = "31961-05.html";
							}
						}
						else
						{
							htmltext = "31961-07.html";
						}
						break;
					}
					case 4:
					{
						htmltext = "31961-01.html";
						break;
					}
				}
				break;
			}
			case YASENI:
			{
				switch (qs.getCond())
				{
					case 4:
					{
						htmltext = "31958-01.html";
						break;
					}
					case 5:
					{
						htmltext = "31958-03.html";
						break;
					}
					case 6:
					{
						if (getQuestItemsCount(talker, GOLEM_SHARD) >= 300)
						{
							takeItems(talker, GOLEM_SHARD, -1);
							qs.setCond(7);
							htmltext = "31958-04.html";
						}
						break;
					}
					case 7:
					{
						htmltext = "31958-05.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 5, 3, npc);
		if (qs != null)
		{
			if (giveItemRandomly(killer, npc, GOLEM_SHARD, 1, 300, 1, true))
			{
				qs.setCond(6);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}