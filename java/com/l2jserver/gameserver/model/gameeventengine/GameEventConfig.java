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
package com.l2jserver.gameserver.model.gameeventengine;

import java.util.List;
import java.util.Map;

import com.l2jserver.gameserver.model.holders.SkillHolder;

public abstract class GameEventConfig
{
	public boolean EVENT_IN_INSTANCE = true;
	public String EVENT_INSTANCE_FILE = "";
	public String[] EVENT_TIME_INTERVAL;
	public int EVENT_RUNNING_TIME = 20;
	public int EVENT_MIN_PLAYERS_IN_TEAMS = 2;
	public int EVENT_MAX_PLAYERS_IN_TEAMS = 10;
	public byte EVENT_MIN_LVL = 1;
	public byte EVENT_MAX_LVL = 85;
	public int EVENT_PARTICIPATION_TIME = 10;
	public int EVENT_RESPAWN_TELEPORT_DELAY = 10;
	public int EVENT_START_LEAVE_TELEPORT_DELAY = 10;
	public boolean EVENT_PARTICIPATION_FEE = false;
	public int EVENT_TAKE_ITEM_ID = 0;
	public int EVENT_TAKE_COUNT = 0;
	public boolean EVENT_ENABLED_KILL_BONUS = false;
	public int EVENT_KILL_BONUS_ID = 0;
	public int EVENT_KILL_BONUS_COUNT = 0;
	public int EVENT_EFFECTS_REMOVAL = 0;
	public List<int[]> EVENT_REWARDS;
	public boolean EVENT_REWARD_TEAM_TIE = false;
	public Map<Integer, Integer> EVENT_MAGE_BUFFS;
	public Map<Integer, Integer> EVENT_FIGHTER_BUFFS;
	public int[] EVENT_RESTRICT_ITEMS;
	public List<SkillHolder> EVENT_RESTRICT_SKILLS;
	public boolean EVENT_POTIONS_ALLOWED = false;
	public boolean EVENT_SCROLL_ALLOWED = false;
	public boolean EVENT_SUMMON_BY_ITEM_ALLOWED = false;
	public boolean EVENT_REQUIRE_MIN_FRAGS_TO_REWARD = false;
	public byte EVENT_MIN_FRAGS_TO_REWARD = 0;
	
	// TODO: rework
	public int[] EVENT_PARTICIPATION_COORDINATES =
	{
		83425,
		148585,
		-3406
	};
}
