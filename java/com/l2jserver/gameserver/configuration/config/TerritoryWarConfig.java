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
 * @author ��3���
 */
@Configuration("territoryWar.json")
public class TerritoryWarConfig
{
	@Setting(name = "DefenderMaxClans")
	public static int DEFENDERMAXCLANS; // Max number of clans
	@Setting(name = "DefenderMaxPlayers")
	public static int DEFENDERMAXPLAYERS; // Max number of individual player
	@Setting(name = "ClanMinLevel", minValue = 0)
	public static int CLANMINLEVEL;
	@Setting(name = "PlayerMinLevel")
	public static int PLAYERMINLEVEL;
	@Setting(name = "MinTerritoryBadgeForNobless")
	public static int MINTWBADGEFORNOBLESS;
	@Setting(name = "MinTerritoryBadgeForStriders")
	public static int MINTWBADGEFORSTRIDERS;
	@Setting(name = "MinTerritoryBadgeForBigStrider")
	public static int MINTWBADGEFORBIGSTRIDER;
	@Setting(name = "WarLength", increase = 60000)
	public static Long WARLENGTH;
	@Setting(name = "PlayerWithWardCanBeKilledInPeaceZone")
	public static boolean PLAYER_WITH_WARD_CAN_BE_KILLED_IN_PEACEZONE;
	@Setting(name = "SpawnWardsWhenTWIsNotInProgress")
	public static boolean SPAWN_WARDS_WHEN_TW_IS_NOT_IN_PROGRESS;
	@Setting(name = "ReturnWardsWhenTWStarts")
	public static boolean RETURN_WARDS_WHEN_TW_STARTS;
}