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
package com.l2jserver.gameserver.configuration.config.custom;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("custom/offline.json")
public class OfflineConfig
{
	@Setting(name = "OfflineTradeEnable")
	public static boolean OFFLINE_TRADE_ENABLE;
	@Setting(name = "OfflineCraftEnable")
	public static boolean OFFLINE_CRAFT_ENABLE;
	@Setting(name = "OfflineModeInPeaceZone")
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	@Setting(name = "OfflineModeNoDamage")
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	@Setting(name = "RestoreOffliners")
	public static boolean RESTORE_OFFLINERS;
	@Setting(name = "OfflineMaxDays")
	public static int OFFLINE_MAX_DAYS;
	@Setting(name = "OfflineDisconnectFinished")
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	@Setting(name = "OfflineSetNameColor")
	public static boolean OFFLINE_SET_NAME_COLOR;
	@Setting(name = "OfflineNameColor", method = "NameColor")
	public static int OFFLINE_NAME_COLOR;
	@Setting(name = "OfflineSetSleep")
	public static boolean OFFLINE_SET_SLEEP;
	@Setting(name = "OfflineFame")
	public static boolean OFFLINE_FAME;
	
	public void NameColor(final String value)
	{
		OFFLINE_NAME_COLOR = Integer.decode("0x" + value);
	}
}
