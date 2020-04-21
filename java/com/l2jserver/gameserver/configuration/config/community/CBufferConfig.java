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
package com.l2jserver.gameserver.configuration.config.community;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

@Configuration("community/communityBuffer.json")
public class CBufferConfig
{
	@Setting(name = "AllowCommunityBuffer")
	public static boolean ALLOW_CB_BUFFER;
	
	@Setting(name = "CommunityBufferPrice", splitter = ";")
	public static int[] CB_BUFFER_PRICE;
	
	@Setting(name = "AllowCommunityBufferPriceMod")
	public static boolean ALLOW_CB_BUFFER_PRICE_MOD;
	
	@Setting(name = "CommunityBufferSaveId")
	public static int CB_BUFFER_SAVED_ID;
	
	@Setting(name = "CommunityBufferSavePrice")
	public static long CB_BUFFER_SAVED_PRICE;
	
	@Setting(name = "CommunityBufferTime")
	public static int CB_BUFFER_TIME;
	
	@Setting(name = "CommunityBufferPlayerMinLevel")
	public static int CB_BUFFER_PLAYER_MIN_LEVEL;
	
	@Setting(name = "CommunityBufferPlayerMaxLevel")
	public static int CB_BUFFER_PLAYER_MAX_LEVEL;
	
	@Setting(name = "CommunityBufferPlayerFreeLevel")
	public static int CB_BUFFER_PLAYER_FREE_LEVEL;
	
	@Setting(name = "CommunityBufferAllowedBuffs", splitter = ",")
	public static int[] CB_BUFFER_ALLOWED_BUFFS;
	
	@Setting(name = "AllowCommunityBufferRecover")
	public static boolean ALLOW_CB_BUFFER_RECOVER;
	
	@Setting(name = "AllowCommunityBufferClear")
	public static boolean ALLOW_CB_BUFFER_CLEAR;
	
	@Setting(name = "AllowCommunityBufferPeaceRecover")
	public static boolean ALLOW_CB_BUFFER_PEACE_RECOVER;
	
	@Setting(name = "CommunityBufferRecoverPrice", splitter = ";")
	public static int[] CB_BUFFER_RECOVER_PRICE;
	
	@Setting(name = "AllowCommunityBufferPremiumMod")
	public static boolean ALLOW_CB_BUFFER_PREMIUM_MOD;
	
	@Setting(name = "CommunityBufferPaMod")
	public static double CB_BUFFER_PA_MOD;
	
	@Setting(name = "AllowCommunityBufferInSiege")
	public static boolean ALLOW_CB_BUFFER_IN_SIEGE;
	
	@Setting(name = "AllowCommunityBufferInPvP")
	public static boolean ALLOW_CB_BUFFER_IN_PVP;
	
	@Setting(name = "AllowCommunityBufferInBattle")
	public static boolean ALLOW_CB_BUFFER_IN_BATTLE;
	
	@Setting(name = "AllowCommunityBufferInEvents")
	public static boolean ALLOW_CB_BUFFER_IN_EVENTS;
	
	@Setting(name = "AllowCommunityBufferRecoverPvPFlag")
	public static boolean ALLOW_CB_BUFFER_RECOVER_PVP_FLAG;
	
	@Setting(name = "AllowCommunityBufferRecoverBattle")
	public static boolean ALLOW_CB_BUFFER_RECOVER_BATTLE;
}
