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
package quests.Q00715_PathToBecomingALordGoddard;

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

/**
 * Path to Becoming a Lord - Goddard (715)
 * @author Pandragon
 */
public final class Q00715_PathToBecomingALordGoddard extends Quest
{
	// NPC
	private static final int ALFRED = 35363;
	// Monsters
	private static final int WATER_SPIRIT = 25316;
	private static final int FLAME_SPIRIT = 25306;
	// Misc
	private static final int CASTLE_ID = 7; // Goddard
	
	public Q00715_PathToBecomingALordGoddard()
	{
		super(715, Q00715_PathToBecomingALordGoddard.class.getSimpleName(), "Path to Becoming a Lord - Goddard");
		addStartNpc(ALFRED);
		addTalkId(ALFRED);
		addKillId(WATER_SPIRIT, FLAME_SPIRIT);
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
			case "35363-02.html":
			{
				htmltext = event;
				break;
			}
			case "35363-04a.html":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "35363-05.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "35363-06.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "35363-12.html":
			{
				if (qs.isCond(7))
				{
					if (CastleManager.getInstance().getCastleById(CASTLE_ID).getSiege().isInProgress())
					{
						return "35363-11a.html";
					}
					
					for (Fort fort : FortManager.getInstance().getForts())
					{
						if (!fort.isBorderFortress() && fort.getSiege().isInProgress())
						{
							return "35363-11a.html";
						}
						else if (!fort.isBorderFortress() && (fort.getContractedCastleId() != CASTLE_ID))
						{
							return "35363-11b.html";
						}
					}
					
					// DeclareLord(godad_dominion, player);
					CastleManager.getInstance().getCastleById(CASTLE_ID).setShowNpcCrest(true);
					NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GODDARD_MAY_THERE_BE_GLORY_IN_THE_TERRITORY_OF_GODDARD);
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = (talker.getId() == talker.getClan().getLeaderId()) ? "35363-01.html" : "35363-03.html";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "35363-04a.html";
						break;
					}
					case 2:
					{
						htmltext = "35363-07.html";
						break;
					}
					case 3:
					{
						htmltext = "35363-08.html";
						break;
					}
					case 4:
					{
						htmltext = "35363-09.html";
						qs.setCond(6);
						break;
					}
					case 5:
					{
						htmltext = "35363-10.html";
						qs.setCond(7);
						break;
					}
					case 6:
					{
						htmltext = "35363-09.html";
						break;
					}
					case 7:
					{
						htmltext = "35363-10.html";
						break;
					}
					case 8:
					case 9:
					{
						htmltext = "35363-11.html";
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
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (killer.getClan() == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		L2PcInstance leader = killer.getClan().getLeader().getPlayerInstance();
		if (!leader.isOnline())
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		QuestState qs = leader.getQuestState(getName());
		if (qs == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		switch (qs.getCond())
		{
			case 2:
			{
				if (npc.getId() == FLAME_SPIRIT)
				{
					qs.setCond(4);
				}
				break;
			}
			case 3:
			{
				if (npc.getId() == WATER_SPIRIT)
				{
					qs.setCond(5);
				}
				break;
			}
			case 6:
			{
				if (npc.getId() == WATER_SPIRIT)
				{
					qs.setCond(9);
				}
				break;
			}
			case 7:
			{
				if (npc.getId() == FLAME_SPIRIT)
				{
					qs.setCond(8);
				}
				break;
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
}