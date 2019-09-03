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
@Configuration("custom.json")
public class CustomConfig
{
	@Setting(name = "EnableWarehouseSortingClan")
	public static boolean ENABLE_WAREHOUSESORTING_CLAN;
	@Setting(name = "EnableWarehouseSortingPrivate")
	public static boolean ENABLE_WAREHOUSESORTING_PRIVATE;
	@Setting(name = "BankingEnabled")
	public static boolean BANKING_SYSTEM_ENABLED;
	@Setting(name = "BankingGoldbarCount")
	public static int BANKING_SYSTEM_GOLDBARS;
	@Setting(name = "BankingAdenaCount")
	public static int BANKING_SYSTEM_ADENA;
	@Setting(name = "HellboundStatus")
	public static boolean HELLBOUND_STATUS;
	@Setting(name = "AllowChangePassword")
	public static boolean ALLOW_CHANGE_PASSWORD;
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
