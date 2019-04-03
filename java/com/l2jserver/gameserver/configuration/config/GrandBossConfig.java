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
package com.l2jserver.gameserver.configuration.config;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("grandBoss.json")
public class GrandBossConfig
{
	@Setting(name = "AntharasWaitTime")
	public static int ANTHARAS_WAIT_TIME;
	@Setting(name = "IntervalOfAntharasSpawn")
	public static int ANTHARAS_SPAWN_INTERVAL;
	@Setting(name = "RandomOfAntharasSpawn")
	public static int ANTHARAS_SPAWN_RANDOM;
	@Setting(name = "ValakasWaitTime")
	public static int VALAKAS_WAIT_TIME;
	@Setting(name = "IntervalOfValakasSpawn")
	public static int VALAKAS_SPAWN_INTERVAL;
	@Setting(name = "RandomOfValakasSpawn")
	public static int VALAKAS_SPAWN_RANDOM;
	@Setting(name = "IntervalOfBaiumSpawn")
	public static int BAIUM_SPAWN_INTERVAL;
	@Setting(name = "RandomOfBaiumSpawn")
	public static int BAIUM_SPAWN_RANDOM;
	@Setting(name = "IntervalOfCoreSpawn")
	public static int CORE_SPAWN_INTERVAL;
	@Setting(name = "RandomOfCoreSpawn")
	public static int CORE_SPAWN_RANDOM;
	@Setting(name = "IntervalOfOrfenSpawn")
	public static int ORFEN_SPAWN_INTERVAL;
	@Setting(name = "RandomOfOrfenSpawn")
	public static int ORFEN_SPAWN_RANDOM;
	@Setting(name = "IntervalOfQueenAntSpawn")
	public static int QUEEN_ANT_SPAWN_INTERVAL;
	@Setting(name = "RandomOfQueenAntSpawn")
	public static int QUEEN_ANT_SPAWN_RANDOM;
	@Setting(name = "BelethMinPlayers")
	public static int BELETH_MIN_PLAYERS;
	@Setting(name = "IntervalOfBelethSpawn")
	public static int BELETH_SPAWN_INTERVAL;
	@Setting(name = "RandomOfBelethSpawn")
	public static int BELETH_SPAWN_RANDOM;
}
