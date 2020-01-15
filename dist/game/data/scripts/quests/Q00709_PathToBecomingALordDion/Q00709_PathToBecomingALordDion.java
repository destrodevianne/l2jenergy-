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
package quests.Q00709_PathToBecomingALordDion;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.util.Util;

/**
 * Path to Becoming a Lord - Dion (709) TODO: fix quest
 */
public class Q00709_PathToBecomingALordDion extends Quest
{
	// NPCs
	private static final int CROSBY = 35142;
	private static final int ROUKE = 31418;
	private static final int SOPHIA = 30735;
	// Items
	private static final int MANDRADORA_ROOT = 13849;
	private static final int BLOODY_AXE_BLACK_EPAULETTE = 13850;
	// Monsters
	private static final int BLOODY_AXE_SUBORDINATE = 27392;
	private static final int[] OL_MAHUMS =
	{
		20208, // Ol Mahum Raider
		20209, // Ol Mahum Sniper
		20210, // Ol Mahum Lieutenant
		20211, // Ol Mahum Captain
		BLOODY_AXE_SUBORDINATE
	};
	private static final int[] MANRAGORAS =
	{
		20154, // Mandragora
		20155, // Mandragora Sapling
		20156 // Mandragora Blossom
	};
	
	// Misc
	private static final int CASTLE_ID = 2; // Dion
	
	public Q00709_PathToBecomingALordDion()
	{
		super(709, Q00709_PathToBecomingALordDion.class.getSimpleName(), "Path to Becoming a Lord - Dion");
		addStartNpc(CROSBY);
		addTalkId(CROSBY);
		addTalkId(SOPHIA);
		addTalkId(ROUKE);
		addKillId(OL_MAHUMS);
		addKillId(MANRAGORAS);
		registerQuestItems(MANDRADORA_ROOT, BLOODY_AXE_BLACK_EPAULETTE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState qs = player.getQuestState(getName());
		final Castle castle = CastleManager.getInstance().getCastleById(CASTLE_ID);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if (event.equals("35142-04.html"))
		{
			qs.startQuest();
		}
		else if (event.equals("35142-12.html"))
		{
			if (isLordAvailable(2, qs))
			{
				castleOwner.getQuestState(getName()).set("confidant", String.valueOf(qs.getPlayer().getObjectId()));
				castleOwner.getQuestState(getName()).setCond(3);
				qs.setState(State.STARTED);
			}
			else
			{
				htmltext = "35142-09b.html";
			}
		}
		else if (event.equals("35142-11.html"))
		{
			if (isLordAvailable(3, qs))
			{
				castleOwner.getQuestState(getName()).setCond(4);
			}
			else
			{
				htmltext = "35142-09b.html";
			}
		}
		else if (event.equals("30735-02.html"))
		{
			qs.set("cond", "6");
		}
		else if (event.equals("30735-05.html"))
		{
			takeItems(player, BLOODY_AXE_BLACK_EPAULETTE, 1);
			qs.set("cond", "8");
		}
		else if (event.equals("31418-09.html"))
		{
			if (isLordAvailable(8, qs))
			{
				takeItems(player, MANDRADORA_ROOT, -1);
				castleOwner.getQuestState(getName()).setCond(9);
			}
		}
		else if (event.equals("35142-23.html"))
		{
			if (CastleManager.getInstance().getCastleById(CASTLE_ID).getSiege().isInProgress())
			{
				return "35142-22à.html";
			}
			
			for (Fort fort : FortManager.getInstance().getForts())
			{
				if (!fort.isBorderFortress() && fort.getSiege().isInProgress())
				{
					return "35142-22à.html";
				}
				else if (!fort.isBorderFortress() && (fort.getContractedCastleId() != CASTLE_ID))
				{
					return "35142-22b.html";
				}
			}
			
			// DeclareLord(gludio_dominion, player);
			CastleManager.getInstance().getCastleById(CASTLE_ID).setShowNpcCrest(true);
			NpcSay packet = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.S1_HAS_BECOME_LORD_OF_THE_TOWN_OF_DION_LONG_MAY_HE_REIGN);
			packet.addStringParameter(player.getName());
			npc.broadcastPacket(packet);
			qs.exitQuest(false, true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		final Castle castle = CastleManager.getInstance().getCastleById(CASTLE_ID);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		
		switch (npc.getId())
		{
			case CROSBY:
			{
				if (qs.isCond(0))
				{
					if (castleOwner == qs.getPlayer())
					{
						if (!hasFort())
						{
							htmltext = "35142-01.html";
						}
						else
						{
							htmltext = "35142-03.html";
							qs.exitQuest(true);
						}
					}
					else if (isLordAvailable(2, qs))
					{
						if (castleOwner.calculateDistance(npc, false, false) <= 200)
						{
							htmltext = "35142-11.html";
						}
						else
						{
							htmltext = "35142-09b.html";
						}
					}
					else
					{
						htmltext = "35142-09a.html";
						qs.exitQuest(true);
					}
				}
				else if (qs.isCond(1))
				{
					qs.set("cond", "2");
					htmltext = "35142-08.html";
				}
				else if (qs.isCond(2) || qs.isCond(3))
				{
					htmltext = "35142-14.html";
				}
				else if (qs.isCond(4))
				{
					qs.set("cond", "5");
					htmltext = "35142-16.html";
				}
				else if (qs.isCond(5))
				{
					htmltext = "35142-16.html";
				}
				else if ((qs.getCond() > 5) && (qs.getCond() < 9))
				{
					htmltext = "35142-15.html";
				}
				else if (qs.isCond(9))
				{
					htmltext = "35142-22.html";
				}
				break;
			}
			case ROUKE:
			{
				if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(3, qs))
				{
					if (castleOwner.getQuestState(getName()).getInt("confidant") == qs.getPlayer().getObjectId())
					{
						htmltext = "31418-03.html";
					}
				}
				else if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(8, qs))
				{
					if (getQuestItemsCount(player, MANDRADORA_ROOT) >= 100)
					{
						htmltext = "31418-08.html";
					}
					else
					{
						htmltext = "31418-07.html";
					}
				}
				else if ((qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(9, qs))
				{
					htmltext = "31418-12.html";
				}
				break;
			}
			case SOPHIA:
			{
				if (qs.isCond(5))
				{
					htmltext = "30735-01.html";
				}
				else if (qs.isCond(6))
				{
					htmltext = "30735-03.html";
				}
				else if (qs.isCond(7))
				{
					htmltext = "30735-04.html";
				}
				else if (qs.isCond(8))
				{
					htmltext = "30735-07.htm";
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
		
		if ((qs != null) && qs.isCond(6) && Util.contains(OL_MAHUMS, npc.getId()))
		{
			if ((npc.getId() != BLOODY_AXE_SUBORDINATE) && (Rnd.get(9) == 0))
			{
				addSpawn(BLOODY_AXE_SUBORDINATE, npc, true, 300000);
			}
			else if (npc.getId() == BLOODY_AXE_SUBORDINATE)
			{
				giveItems(killer, BLOODY_AXE_BLACK_EPAULETTE, 1);
				qs.setCond(7);
			}
		}
		if ((qs != null) && (qs.getState() == State.STARTED) && qs.isCond(0) && isLordAvailable(8, qs) && Util.contains(MANRAGORAS, npc.getId()))
		{
			if (getQuestItemsCount(killer, MANDRADORA_ROOT) < 100)
			{
				giveItems(killer, MANDRADORA_ROOT, 1);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private boolean isLordAvailable(int cond, QuestState qs)
	{
		final Castle castle = CastleManager.getInstance().getCastleById(CASTLE_ID);
		final L2Clan owner = castle.getOwner();
		final L2PcInstance castleOwner = castle.getOwner().getLeader().getPlayerInstance();
		if (owner != null)
		{
			if ((castleOwner != null) && (castleOwner != qs.getPlayer()) && (owner == qs.getPlayer().getClan()) && (castleOwner.getQuestState(getName()) != null) && castleOwner.getQuestState(getName()).isCond(cond))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean hasFort()
	{
		for (Fort fortress : FortManager.getInstance().getForts())
		{
			if (fortress.getContractedCastleId() == CASTLE_ID)
			{
				return true;
			}
		}
		return false;
	}
}