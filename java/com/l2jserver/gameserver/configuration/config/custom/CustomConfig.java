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

import java.util.ArrayList;
import java.util.List;

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
	
	@Setting(name = "MultiLangEnable")
	public static boolean ENABLE_MULTILANG;
	
	@Setting(name = "MultiLangDefault")
	public static String MULTILANG_DEFAULT;
	
	@Setting(name = "MultiLangAllowed", method = "listlang")
	public static List<String> MULTILANG_ALLOWED;
	
	@Setting(name = "MultiLangVoiceCommand")
	public static boolean MULTILANG_VOICED_ALLOW;
	
	@Setting(name = "MultiLangSystemMessageEnable")
	public static boolean MULTILANG_SM_ENABLE;
	
	@Setting(name = "MultiLangSystemMessageAllowed", method = "listlangSM", ignore = true)
	public static List<String> MULTILANG_SM_ALLOWED;
	
	@Setting(name = "MultiLangNpcStringEnable")
	public static boolean MULTILANG_NS_ENABLE;
	
	@Setting(name = "MultiLangNpcStringAllowed", method = "listlangNS", ignore = true)
	public static List<String> MULTILANG_NS_ALLOWED;
	
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
	
	public void listlang(final String value)
	{
		final String[] listlan = value.split(";");
		MULTILANG_ALLOWED = new ArrayList<>(listlan.length);
		
		for (final String type : listlan)
		{
			MULTILANG_ALLOWED.add(type);
		}
		MULTILANG_ALLOWED.contains(MULTILANG_DEFAULT);
	}
	
	public void listlangSM(final String value)
	{
		final String[] listlan = value.split(";");
		MULTILANG_SM_ALLOWED = new ArrayList<>(listlan.length);
		for (String type : listlan)
		{
			if (!type.isEmpty())
			{
				MULTILANG_SM_ALLOWED.add(type);
			}
		}
	}
	
	public void listlangNS(final String value)
	{
		final String[] listlan = value.split(";");
		MULTILANG_NS_ALLOWED = new ArrayList<>(listlan.length);
		for (String type : listlan)
		{
			if (!type.isEmpty())
			{
				MULTILANG_NS_ALLOWED.add(type);
			}
		}
	}
}
