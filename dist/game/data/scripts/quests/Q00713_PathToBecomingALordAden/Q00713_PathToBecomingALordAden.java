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
package quests.Q00713_PathToBecomingALordAden;

import com.l2jserver.gameserver.enums.ChatType;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.L2ClanMember;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

import quests.Q00359_ForASleeplessDeadman.Q00359_ForASleeplessDeadman;

/**
 * Path to Becoming a Lord - Aden (713)
 * @author Pandragon
 */
public class Q00713_PathToBecomingALordAden extends Quest
{
	// NPCs
	private static final int LOGAN = 35274;
	private static final int ORVEN = 30857;
	// Monsters
	private static final int TAIK_SEEKER = 20666;
	private static final int TAIK_LEADER = 20669;
	// Misc
	private static final int CASTLE_ID = 5; // Aden
	private static final int REQUIRED_CLAN_MEMBERS = 5;
	
	public Q00713_PathToBecomingALordAden()
	{
		super(713, Q00713_PathToBecomingALordAden.class.getSimpleName(), "Path to Becoming a Lord - Aden");
		addStartNpc(LOGAN);
		addTalkId(LOGAN, ORVEN);
		addKillId(TAIK_SEEKER, TAIK_LEADER);
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
			case "30857-02.html":
			{
				htmltext = event;
				break;
			}
			case "35274-03.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30857-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setMemoState(0);
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "35274-06.html":
			{
				if (qs.isCond(7))
				{
					if (CastleManager.getInstance().getCastleById(CASTLE_ID).getSiege().isInProgress())
					{
						return "35274-05a.html";
					}
					
					for (Fort fort : FortManager.getInstance().getForts())
					{
						if (!fort.isBorderFortress() && fort.getSiege().isInProgress())
						{
							return "35274-05a.html";
						}
						else if (!fort.isBorderFortress() && (fort.getContractedCastleId() != CASTLE_ID))
						{
							return "35274-05b.html";
						}
					}
					
					// DeclareLord(aden_dominion, player);
					CastleManager.getInstance().getCastleById(CASTLE_ID).setShowNpcCrest(true);
					NpcSay packet = new NpcSay(npc.getObjectId(), ChatType.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_ADEN_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_ADEN);
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
			case LOGAN:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = (talker.getId() == talker.getClan().getLeaderId()) ? "35274-01.htm" : "35274-02.html";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							case 2:
							case 3:
							case 5:
							{
								htmltext = "35274-04.html";
								break;
							}
							case 7:
							{
								htmltext = "35274-05.html";
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
			case ORVEN:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "30857-01.html";
						break;
					}
					case 2:
					{
						htmltext = "30857-04.html";
						break;
					}
					case 5:
					{
						int clanMemberCount = 0;
						for (L2ClanMember clanMember : talker.getClan().getMembers())
						{
							final L2PcInstance member = clanMember.getPlayerInstance();
							if ((member != null) && member.isOnline() && (member.getId() != talker.getId()))
							{
								QuestState st = getQuestState(member, false);
								st = member.getQuestState(Q00359_ForASleeplessDeadman.class.getSimpleName());
								if (st.isCompleted())
								{
									clanMemberCount++;
								}
							}
						}
						if (clanMemberCount >= REQUIRED_CLAN_MEMBERS)
						{
							qs.setCond(7);
							htmltext = "30857-06.html";
						}
						else
						{
							htmltext = "30857-05.html";
						}
						break;
					}
					case 7:
					{
						htmltext = "30857-07.html";
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
		final QuestState qs = killer.getQuestState(getName());
		if ((qs != null) && qs.isCond(2))
		{
			if (qs.getMemoState() < 100)
			{
				qs.setMemoState(qs.getMemoState() + 1);
			}
			else
			{
				qs.setCond(5);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}