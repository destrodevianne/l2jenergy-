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
@Configuration("custom/custom.json")
public class CustomConfig
{
	@Setting(name = "EnableWarehouseSortingClan")
	public static boolean ENABLE_WAREHOUSESORTING_CLAN;
	
	@Setting(name = "EnableWarehouseSortingPrivate")
	public static boolean ENABLE_WAREHOUSESORTING_PRIVATE;
	
	@Setting(name = "BankingEnabled")
	public static boolean BANKING_SYSTEM_ENABLED;
	
	@Setting(name = "AutoBankingEnabled")
	public static boolean BANKING_SYSTEM_AUTO_ENABLED;
	
	@Setting(name = "BankingGoldbarCount")
	public static int BANKING_SYSTEM_GOLDBARS;
	
	@Setting(name = "AutoBankingAdenaMinCount")
	public static int BANKING_SYSTEM_ADENA;
	
	@Setting(name = "BankingAdenaCount")
	public static int BANKING_SYSTEM_AUTO_MIN_ADENA;
	
	@Setting(name = "HellboundStatus")
	public static boolean HELLBOUND_STATUS;
	
	@Setting(name = "AllowChangePassword")
	public static boolean ALLOW_CHANGE_PASSWORD;
	
	@Setting(name = "AllowRepairVoiceCommand")
	public static boolean ALLOW_REPAIR_VOICE_COMMAND;
	
	@Setting(name = "AllowHelpVoiceCommand")
	public static boolean ALLOW_HELP_VOICE_COMMAND;
	
	@Setting(name = "DebugVoiceCommand")
	public static boolean DEBUG_VOICE_COMMAND;
	
	@Setting(name = "EnableManaPotionSupport")
	public static boolean ENABLE_MANA_POTIONS_SUPPORT;
	
	@Setting(name = "DisplayServerTime")
	public static boolean DISPLAY_SERVER_TIME;
	
	@Setting(name = "ChatAdmin")
	public static boolean CHAT_ADMIN;
	
	@Setting(name = "ScreenWelcomeMessageEnable")
	public static boolean WELCOME_MESSAGE_ENABLED;
	
	@Setting(name = "ScreenWelcomeMessageTime", increase = 1000)
	public static int WELCOME_MESSAGE_TIME;
	
	@Setting(name = "AnnouncePkPvP")
	public static boolean ANNOUNCE_PK_PVP;
	
	@Setting(name = "AnnouncePkPvPNormalMessage")
	public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
}
