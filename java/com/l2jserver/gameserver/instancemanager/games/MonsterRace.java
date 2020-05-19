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
package com.l2jserver.gameserver.instancemanager.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.enums.RaceState;
import com.l2jserver.gameserver.enums.audio.Music;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.HistoryInfo;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.zone.type.L2DerbyTrackZone;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.DeleteObject;
import com.l2jserver.gameserver.network.serverpackets.MonRaceInfo;
import com.l2jserver.gameserver.network.serverpackets.PlaySound;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

public class MonsterRace
{
	protected static final Logger LOG = LoggerFactory.getLogger(MonsterRace.class);
	
	protected static final PlaySound SOUND_1 = Music.S_RACE.getPacket();
	protected static final PlaySound SOUND_2 = Sound.ITEMSOUND2_RACE_START.getPacket();
	
	protected static final int[][] CODES =
	{
		{
			-1,
			0
		},
		{
			0,
			15322
		},
		{
			13765,
			-1
		}
	};
	
	protected final List<Integer> _npcTemplates = new ArrayList<>(); // List holding npc templates, shuffled on a new race.
	public final List<HistoryInfo> _history = new ArrayList<>(); // List holding old race records.
	protected final Map<Integer, Long> _betsPerLane = new ConcurrentHashMap<>(); // Map holding all bets for each lane ; values setted to 0 after every race.
	protected final List<Double> _odds = new ArrayList<>(); // List holding sorted odds per lane ; cleared at new odds calculation.
	
	public int _raceNumber = 1;
	protected int _finalCountdown = 0;
	protected RaceState _state = RaceState.RACE_END;
	
	protected MonRaceInfo _packet;
	
	private final L2Npc[] _monsters = new L2Npc[8];
	private int[][] _speeds = new int[8][20];
	private final int[] _first = new int[2];
	private final int[] _second = new int[2];
	
	protected MonsterRace()
	{
		if (!GeneralConfig.ALLOW_RACE)
		{
			return;
		}
		
		// Feed _history with previous race results.
		DAOFactory.getInstance().getMonsterRaceDAO().loadHistory(this);
		
		// Feed _betsPerLane with stored informations on bets.
		DAOFactory.getInstance().getMonsterRaceDAO().loadBets(this);
		
		// Feed _npcTemplates, we will only have to shuffle it when needed.
		for (int i = 31003; i < 31027; i++)
		{
			_npcTemplates.add(i);
		}
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Announcement(), 0, 1000);
		LOG.info("MonsterRace: loaded {} records, currently on race #{}", _history.size(), _raceNumber);
	}
	
	private class Announcement implements Runnable
	{
		public Announcement()
		{
		}
		
		@Override
		public void run()
		{
			if (_finalCountdown > 1200)
			{
				_finalCountdown = 0;
			}
			
			switch (_finalCountdown)
			{
				case 0:
				{
					newRace();
					newSpeeds();
					
					_state = RaceState.ACCEPTING_BETS;
					_packet = new MonRaceInfo(CODES[0][0], CODES[0][1], getMonsters(), getSpeeds());
					
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1);
					msg.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, _packet, msg);
					break;
				}
				case 30: // 30 sec
				case 60: // 1 min
				case 90: // 1 min 30 sec
				case 120: // 2 min
				case 150: // 2 min 30
				case 180: // 3 min
				case 210: // 3 min 30
				case 240: // 4 min
				case 270: // 4 min 30 sec
				case 330: // 5 min 30 sec
				case 360: // 6 min
				case 390: // 6 min 30 sec
				case 420: // 7 min
				case 450: // 7 min 30
				case 480: // 8 min
				case 510: // 8 min 30
				case 540: // 9 min
				case 570: // 9 min 30 sec
				case 630: // 10 min 30 sec
				case 660: // 11 min
				case 690: // 11 min 30 sec
				case 720: // 12 min
				case 750: // 12 min 30
				case 780: // 13 min
				case 810: // 13 min 30
				case 870: // 14 min 30 sec
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1);
					msg.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg);
					break;
				}
				case 300: // 5 min
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1);
					msg.addInt(_raceNumber);
					final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S);
					msg2.addInt(10);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg, msg2);
					break;
				}
				case 600: // 10 min
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1);
					msg.addInt(_raceNumber);
					final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S);
					msg2.addInt(5);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg, msg2);
					break;
				}
				case 840: // 14 min
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1);
					msg.addInt(_raceNumber);
					final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S);
					msg2.addInt(1);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg, msg2);
					break;
				}
				case 900: // 15 min
				{
					_state = RaceState.WAITING;
					
					calculateOdds();
					
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1);
					msg.addInt(_raceNumber);
					final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1_ODDS_ARE_POSTED);
					msg2.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg, msg2);
					break;
				}
				case 960: // 16 min
				case 1020: // 17 min
				{
					final int minutes = (_finalCountdown == 960) ? 2 : 1;
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTE_S);
					msg.addInt(minutes);
					msg.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg);
					break;
				}
				case 1050: // 17 min 30 sec
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS);
					msg.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg);
					break;
				}
				case 1070: // 17 min 50 sec
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.MONSTER_RACE_S1_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS);
					msg.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg);
					break;
				}
				case 1075: // 17 min 55 sec
				case 1076: // 17 min 56 sec
				case 1077: // 17 min 57 sec
				case 1078: // 17 min 58 sec
				case 1079: // 17 min 59 sec
				{
					final int seconds = 1080 - _finalCountdown;
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S);
					msg.addInt(seconds);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg);
					break;
				}
				case 1080: // 18 min
				{
					_state = RaceState.STARTING_RACE;
					_packet = new MonRaceInfo(CODES[1][0], CODES[1][1], getMonsters(), getSpeeds());
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.THEY_RE_OFF), SOUND_1, SOUND_2, _packet);
					break;
				}
				case 1085: // 18 min 5 sec
				{
					_packet = new MonRaceInfo(CODES[2][0], CODES[2][1], getMonsters(), getSpeeds());
					
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, _packet);
					break;
				}
				case 1115: // 18 min 35 sec
				{
					_state = RaceState.RACE_END;
					
					// Populate history info with data, stores it in database.
					final HistoryInfo info = _history.get(_history.size() - 1);
					info.setFirst(getFirstPlace());
					info.setSecond(getSecondPlace());
					info.setOddRate(_odds.get(getFirstPlace() - 1));
					
					DAOFactory.getInstance().getMonsterRaceDAO().saveHistory(info);
					clearBets();
					
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2);
					msg.addInt(getFirstPlace());
					msg.addInt(getSecondPlace());
					final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.MONSTER_RACE_S1_IS_FINISHED);
					msg2.addInt(_raceNumber);
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, msg, msg2);
					_raceNumber++;
					break;
				}
				case 1140: // 19 min
				{
					Broadcast.toAllPlayersInZoneType(L2DerbyTrackZone.class, new DeleteObject(getMonsters()[0]), new DeleteObject(getMonsters()[1]), new DeleteObject(getMonsters()[2]), new DeleteObject(getMonsters()[3]), new DeleteObject(getMonsters()[4]), new DeleteObject(getMonsters()[5]), new DeleteObject(getMonsters()[6]), new DeleteObject(getMonsters()[7]));
					break;
				}
			}
			_finalCountdown += 1;
		}
	}
	
	public void newRace()
	{
		// Edit _history.
		_history.add(new HistoryInfo(_raceNumber, 0, 0, 0));
		
		// Randomize _npcTemplates.
		Collections.shuffle(_npcTemplates);
		
		// Setup 8 new creatures ; pickup the first 8 from _npcTemplates.
		for (int i = 0; i < 8; i++)
		{
			try
			{
				final L2NpcTemplate template = NpcData.getInstance().getTemplate(_npcTemplates.get(i));
				_monsters[i] = (L2Npc) Class.forName("com.l2jserver.gameserver.model.actor.instance." + template.getType() + "Instance").getConstructors()[0].newInstance(template);
			}
			catch (Exception e)
			{
				LOG.warn("Unable to create monster!", e);
			}
		}
	}
	
	public void newSpeeds()
	{
		_speeds = new int[8][20];
		int total = 0;
		_first[1] = 0;
		_second[1] = 0;
		
		for (int i = 0; i < 8; i++)
		{
			total = 0;
			for (int j = 0; j < 20; j++)
			{
				if (j == 19)
				{
					_speeds[i][j] = 100;
				}
				else
				{
					_speeds[i][j] = Rnd.get(60) + 65;
				}
				total += _speeds[i][j];
			}
			
			if (total >= _first[1])
			{
				_second[0] = _first[0];
				_second[1] = _first[1];
				_first[0] = 8 - i;
				_first[1] = total;
			}
			else if (total >= _second[1])
			{
				_second[0] = 8 - i;
				_second[1] = total;
			}
		}
	}
	
	protected void clearBets()
	{
		for (int key : _betsPerLane.keySet())
		{
			_betsPerLane.put(key, 0L);
		}
		DAOFactory.getInstance().getMonsterRaceDAO().clearBets();
	}
	
	public void setBetOnLane(int lane, long amount, boolean saveOnDb)
	{
		final long sum = (_betsPerLane.containsKey(lane)) ? _betsPerLane.get(lane) + amount : amount;
		
		_betsPerLane.put(lane, sum);
		
		if (saveOnDb)
		{
			DAOFactory.getInstance().getMonsterRaceDAO().saveBet(lane, sum);
		}
	}
	
	protected void calculateOdds()
	{
		// Clear previous List holding old odds.
		_odds.clear();
		
		// Sort bets lanes per lane.
		final Map<Integer, Long> sortedLanes = new TreeMap<>(_betsPerLane);
		
		// Pass a first loop in order to calculate total sum of all lanes.
		long sumOfAllLanes = 0;
		for (long amount : sortedLanes.values())
		{
			sumOfAllLanes += amount;
		}
		
		// As we get the sum, we can now calculate the odd rate of each lane.
		for (long amount : sortedLanes.values())
		{
			_odds.add((amount == 0) ? 0D : Math.max(1.25, (sumOfAllLanes * 0.7) / amount));
		}
	}
	
	public L2Npc[] getMonsters()
	{
		return _monsters;
	}
	
	public int[][] getSpeeds()
	{
		return _speeds;
	}
	
	public int getFirstPlace()
	{
		return _first[0];
	}
	
	public int getSecondPlace()
	{
		return _second[0];
	}
	
	public MonRaceInfo getRacePacket()
	{
		return _packet;
	}
	
	public RaceState getCurrentRaceState()
	{
		return _state;
	}
	
	public int getRaceNumber()
	{
		return _raceNumber;
	}
	
	public List<HistoryInfo> getHistory()
	{
		return _history;
	}
	
	public List<Double> getOdds()
	{
		return _odds;
	}
	
	public static MonsterRace getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MonsterRace INSTANCE = new MonsterRace();
	}
}
