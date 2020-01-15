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
package quests.Q00708_PathToBecomingALordGludio;

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
import com.l2jserver.gameserver.util.Util;

/**
 * Path to Becoming a Lord - Gludio (708)
 * @author Pandragon
 */
public class Q00708_PathToBecomingALordGludio extends Quest
{
	// NPCs
	private static final int SAYRES = 35100;
	private static final int PINTER = 30298;
	private static final int BATHIS = 30332;
	
	// Items
	private static final int HEADLESS_ARMOR = 13848;
	private static final int VARNISH = 1865;
	private static final int ANIMAL_SKIN = 1867;
	private static final int IRON_ORE = 1869;
	private static final int COKES = 1879;
	
	// Monsters
	private static final int HEADLESS_KNIGHT = 27393;
	
	private static final int[] MOBS =
	{
		20045, // Skeleton Scout
		20051, // Skeleton Bowman
		20099, // Skeleton
		HEADLESS_KNIGHT
	};
	
	private static final int GLUDIO_CASTLE = 1;
	
	public Q00708_PathToBecomingALordGludio()
	{
		super(708, Q00708_PathToBecomingALordGludio.class.getSimpleName(), "Path To Becoming A Lord Gludio");
		addStartNpc(SAYRES);
		addKillId(MOBS);
		addTalkId(SAYRES, PINTER, BATHIS);
		
		_questItemIds = new int[]
		{
			HEADLESS_ARMOR
		};
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "35100-02.htm":
			case "30298-04.html":
			{
				htmltext = event;
				break;
			}
			case "35100-04.html":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "35100-08.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "35100-12.html":
			{
				final QuestState qs0 = getQuestState(player.getClan().getLeader().getPlayerInstance(), false);
				if (qs0.isCond(2))
				{
					qs0.set("clanmember", player.getId());
					qs0.setCond(3);
					// htmltext = event; // TODO: %name%, your mission is to help
					htmltext = event.replace("%name%", player.getName());
				}
				break;
			}
			case "30298-05.html":
			{
				final QuestState qs0 = getQuestState(player.getClan().getLeader().getPlayerInstance(), false);
				if (qs0.isCond(3))
				{
					qs0.setCond(4);
					htmltext = event;
				}
				break;
			}
			case "30298-09.html":
			{
				final QuestState qs0 = getQuestState(player.getClan().getLeader().getPlayerInstance(), false);
				if (qs0.isCond(4) && (getQuestItemsCount(player, VARNISH) >= 100) && (getQuestItemsCount(player, ANIMAL_SKIN) >= 100) && (getQuestItemsCount(player, IRON_ORE) >= 100) && (getQuestItemsCount(player, COKES) >= 50))
				{
					qs0.setCond(5);
					htmltext = event;
				}
				break;
			}
			case "30332-02.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6);
					htmltext = event;
				}
				break;
			}
			case "30332-05.html":
			{
				if (qs.isCond(7) && (getQuestItemsCount(player, HEADLESS_ARMOR) >= 1))
				{
					takeItems(player, HEADLESS_ARMOR, -1);
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.LISTEN_YOU_VILLAGERS_OUR_LIEGE_WHO_WILL_SOON_BECOME_A_LORD_HAS_DEFEATED_THE_HEADLESS_KNIGHT_YOU_CAN_NOW_REST_EASY));
					qs.setCond(9);
					htmltext = event;
				}
				break;
			}
			case "35100-23.html":
			{
				if (qs.isCond(9))
				{
					if (CastleManager.getInstance().getCastleById(GLUDIO_CASTLE).getSiege().isInProgress())
					{
						return "35100-22а.html";
					}
					
					for (Fort fort : FortManager.getInstance().getForts())
					{
						if (!fort.isBorderFortress() && fort.getSiege().isInProgress())
						{
							return "35100-22а.html";
						}
						else if (!fort.isBorderFortress() && (fort.getContractedCastleId() != GLUDIO_CASTLE))
						{
							return "35100-22b.html";
						}
					}
					
					// DeclareLord(gludio_dominion, player);
					CastleManager.getInstance().getCastleById(GLUDIO_CASTLE).setShowNpcCrest(true);
					NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_LORD_OF_THE_TOWN_OF_GLUDIO_LONG_MAY_HE_REIGN);
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
		
		if ((talker.getClan() == null) || (talker.getClan().getLeaderId() != CastleManager.getInstance().getCastleById(GLUDIO_CASTLE).getOwnerId()))
		{
			return htmltext;
		}
		
		switch (npc.getId())
		{
			case SAYRES:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						final L2PcInstance leader = talker.getClan().getLeader().getPlayerInstance();
						
						if (qs.getPlayer() != leader)
						{
							final QuestState qs0 = getQuestState(leader, false);
							if (qs0.isCond(2))
							{
								if (Util.checkIfInRange(1500, talker, leader, true) && leader.isOnline())
								{
									htmltext = "35100-11.html";
								}
								else
								{
									htmltext = "35100-10.html";
								}
							}
							else if (qs0.isCond(3))
							{
								htmltext = "35100-13а.html";
							}
							else
							{
								htmltext = "35100-09.html";
							}
						}
						else
						{
							htmltext = "35100-01.htm";
						}
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "35100-06.html";
								break;
							}
							case 2:
							{
								htmltext = "35100-14.html";
								break;
							}
							case 3:
							{
								htmltext = "35100-15.html";
								break;
							}
							case 4:
							{
								htmltext = "35100-16.html";
								break;
							}
							case 5:
							{
								htmltext = "35100-18.html";
								break;
							}
							case 6:
							{
								htmltext = "35100-19.html";
								break;
							}
							case 7:
							{
								htmltext = "35100-20.html";
								break;
							}
							case 8:
							{
								htmltext = "35100-21.html";
								break;
							}
							case 9:
							{
								htmltext = "35100-22.html";
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
			case PINTER:
			{
				final L2PcInstance leader = talker.getClan().getLeader().getPlayerInstance();
				
				if (qs.getPlayer() != leader)
				{
					final QuestState qs0 = getQuestState(leader, false);
					if (leader.isOnline())
					{
						if (talker.getId() == qs0.getInt("clanmember"))
						{
							switch (qs0.getCond())
							{
								case 3:
								{
									htmltext = "30298-03.html";
									break;
								}
								case 4:
								{
									if ((getQuestItemsCount(talker, VARNISH) >= 100) && (getQuestItemsCount(talker, ANIMAL_SKIN) >= 100) && (getQuestItemsCount(talker, IRON_ORE) >= 100) && (getQuestItemsCount(talker, COKES) >= 50))
									{
										htmltext = "30298-08.html";
									}
									else
									{
										htmltext = "30298-07.html";
									}
									break;
								}
								case 5:
								{
									htmltext = "30298-12.html";
									break;
								}
							}
							
						}
						else
						{
							htmltext = "30298-03a.html";
						}
					}
					else
					{
						htmltext = "30298-01.html";
					}
				}
				break;
			}
			case BATHIS:
			{
				switch (qs.getCond())
				{
					case 5:
					{
						htmltext = "30332-01.html";
						break;
					}
					case 6:
					{
						htmltext = "30332-03.html";
						break;
					}
					case 7:
					{
						if (getQuestItemsCount(talker, HEADLESS_ARMOR) >= 1)
						{
							htmltext = "30332-04.html";
						}
						break;
					}
					case 9:
					{
						htmltext = "30332-06.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = killer.getQuestState(getName());
		
		if ((qs != null) && qs.isCond(6))
		{
			if (npc.getId() == HEADLESS_KNIGHT)
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.DOES_MY_MISSION_TO_BLOCK_THE_SUPPLIES_END_HERE));
				giveItems(killer, HEADLESS_ARMOR, 1);
				qs.setCond(7);
			}
			else if (getRandom(100) < 10)
			{
				addSpawn(HEADLESS_KNIGHT, npc, false, 100000);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
