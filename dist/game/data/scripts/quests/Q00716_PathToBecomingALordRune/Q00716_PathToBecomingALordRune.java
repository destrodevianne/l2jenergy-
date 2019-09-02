/*
 * Copyright (C) 2004-2019 L2J Server
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
package quests.Q00716_PathToBecomingALordRune;

import com.l2jserver.gameserver.enums.audio.Sound;
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

import quests.Q00021_HiddenTruth.Q00021_HiddenTruth;

/**
 * Path to Becoming a Lord - Rune (716)
 * @author Pandragon
 */
public final class Q00716_PathToBecomingALordRune extends Quest
{
	// NPCs
	private static final int FREDERICK = 35509;
	private static final int AGRIPEL = 31348;
	private static final int INNOCENTIN = 31328;
	// Monsters
	private static final int[] PAGANS =
	{
		22136, // Doorman Zombie
		22137, // Penance Guard
		22138, // Chapel Guard
		22139, // Old Aristocrat's Soldier
		22140, // Zombie Worker
		22141, // Forgotten Victim
		22142, // Triol's Layperson
		22143, // Triol's Believer
		22144, // Resurrected Temple Knight
		22145, // Ritual Sacrifice
		22146, // Triol's Priest
		22147, // Ritual Offering
		22148, // Triol's Believer
		22149, // Ritual Offering
		22150, // Triol's Believer
		22151, // Triol's Priest
		22152, // Temple Guard
		22153, // Temple Guard Captain
		22154, // Ritual Sacrifice
		22155, // Triol's High Priest
		22156, // Triol's Priest
		22157, // Triol's Priest
		22158, // Triol's Believer
		22159, // Triol's High Priest
		22160, // Triol's Priest
		22161, // Ritual Sacrifice
		22163, // Triol's High Priest
		22164, // Triol's Believer
		22165, // Triol's Priest
		22166, // Triol's Believer
		22167, // Triol's High Priest
		22168, // Triol's Priest
		22169, // Ritual Sacrifice
		22170, // Triol's Believer
		22171, // Triol's High Priest
		22172, // Ritual Sacrifice
		22173, // Triol's Priest
		22174, // Triol's Priest
		22175, // Andreas' Captain of the Royal Guard
		22176, // Andreas' Royal Guards
		22194, // Penance Guard
	};
	// Misc
	private static final int CASTLE_ID = 8; // Rune
	
	public Q00716_PathToBecomingALordRune()
	{
		super(716, Q00716_PathToBecomingALordRune.class.getSimpleName(), "Path to Becoming a Lord - Rune");
		addStartNpc(FREDERICK);
		addTalkId(FREDERICK, AGRIPEL, INNOCENTIN);
		addKillId(PAGANS);
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
			case "35509-02.html":
			case "31348-02.html":
			case "31328-04.html":
			case "31348-06.html":
			case "31348-07.html":
			case "31348-08.html":
			{
				htmltext = event;
				break;
			}
			case "35509-04.html":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "31348-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "35509-16.html":
			{
				final QuestState qs0 = getQuestState(player.getClan().getLeader().getPlayerInstance(), false);
				if (qs0.isCond(4))
				{
					qs0.set("clanmember", player.getId());
					qs0.setCond(5);
					htmltext = event; // TODO: %name%, I will give you one mission.
				}
				break;
			}
			case "31328-05.html":
			{
				final QuestState qs0 = getQuestState(player.getClan().getLeader().getPlayerInstance(), false);
				if (qs0.isCond(5))
				{
					qs0.setMemoState(0);
					qs0.setCond(6);
					htmltext = event;
				}
				break;
			}
			case "31348-10.html":
			{
				if (qs.isCond(7))
				{
					qs.setCond(8);
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
			case FREDERICK:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						final L2PcInstance leader = talker.getClan().getLeader().getPlayerInstance();
						
						if (qs.getPlayer() != leader)
						{
							final QuestState qs0 = getQuestState(leader, false);
							if (qs0.isCond(4))
							{
								if (Util.checkIfInRange(1500, talker, leader, true) && leader.isOnline())
								{
									htmltext = "35509-15.html";
								}
								else
								{
									htmltext = "35509-14.html";
								}
							}
							else if (qs0.isCond(5))
							{
								htmltext = "35509-17.html";
							}
							else
							{
								htmltext = "35509-13.html";
							}
						}
						else
						{
							htmltext = "35509-01.htm";
						}
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								QuestState qs1 = getQuestState(talker, false);
								qs1 = talker.getQuestState(Q00021_HiddenTruth.class.getSimpleName());
								QuestState qs2 = getQuestState(talker, false);
								qs2 = talker.getQuestState("25_HidingBehindTheTruth");
								
								if ((qs1 != null) && qs1.isCompleted() && (qs2 != null) && qs2.isCompleted())
								{
									qs.setCond(2);
									htmltext = "35509-05.html";
								}
								else
								{
									htmltext = "35509-06.html";
								}
								break;
							}
							case 2:
							{
								htmltext = "35509-07.html";
								break;
							}
							case 3:
							{
								qs.setCond(4);
								htmltext = "35509-09.html";
								break;
							}
							case 4:
							{
								htmltext = "35509-10.html";
								break;
							}
							case 5:
							{
								htmltext = "35509-18.html";
								break;
							}
							case 6:
							{
								qs.setCond(7);
								htmltext = "35509-19.html";
								break;
							}
							case 7:
							{
								htmltext = "35509-20.html";
								break;
							}
							case 8:
							{
								if (CastleManager.getInstance().getCastleById(CASTLE_ID).getSiege().isInProgress())
								{
									return "35509-21a.html";
								}
								
								for (Fort fort : FortManager.getInstance().getForts())
								{
									if (!fort.isBorderFortress() && fort.getSiege().isInProgress())
									{
										return "35509-21a.html";
									}
									else if (!fort.isBorderFortress() && (fort.getContractedCastleId() != CASTLE_ID))
									{
										return "35509-21b.html";
									}
								}
								
								// DeclareLord(rune_dominion, player);
								CastleManager.getInstance().getCastleById(CASTLE_ID).setShowNpcCrest(true);
								NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_RUNE_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_RUNE);
								packet.addStringParameter(talker.getName());
								npc.broadcastPacket(packet);
								qs.exitQuest(false, true);
								htmltext = "35509-21.html";
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
			case AGRIPEL:
			{
				switch (qs.getCond())
				{
					case 2:
					{
						htmltext = "31348-01.html";
						break;
					}
					case 3:
					case 4:
					case 5:
					case 6:
					{
						htmltext = "31348-04.html";
						break;
					}
					case 7:
					{
						if (qs.getMemoState() >= 100)
						{
							htmltext = "31348-09.html";
						}
						else
						{
							htmltext = "31348-05.html";
						}
						break;
					}
					case 8:
					{
						htmltext = "31348-11.html";
						break;
					}
				}
				break;
			}
			case INNOCENTIN:
			{
				final L2PcInstance leader = talker.getClan().getLeader().getPlayerInstance();
				
				if (qs.getPlayer() != leader)
				{
					final QuestState qs0 = getQuestState(leader, false);
					if (qs.getMemoState() >= 100)
					{
						htmltext = "31328-06.html";
					}
					else
					{
						if (leader.isOnline())
						{
							if (talker.getId() == qs0.getInt("clanmember"))
							{
								htmltext = "31328-03.html";
							}
							else
							{
								htmltext = "31328-03а.html";
							}
						}
						else
						{
							htmltext = "31328-01.html";
						}
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
		if (killer.getClan() == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		final L2PcInstance leader = killer.getClan().getLeader().getPlayerInstance();
		final QuestState qs = getQuestState(leader, false);
		if ((qs != null) && qs.isCond(7) && leader.isOnline() && (killer != leader) && (killer.getId() == qs.getInt("clanmember")))
		{
			if (qs.getMemoState() < 100)
			{
				qs.setMemoState(qs.getMemoState() + 1);
			}
			else
			{
				playSound(leader, Sound.ITEMSOUND_QUEST_MIDDLE);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}