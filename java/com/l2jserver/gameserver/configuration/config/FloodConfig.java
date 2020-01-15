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
package com.l2jserver.gameserver.configuration.config;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("floodProtector.json")
public class FloodConfig
{
	@Setting(name = "UseItemTime")
	public static int USE_ITEM_TIME;
	
	@Setting(name = "RollDiceTime")
	public static int ROLL_DICE_TIME;
	
	@Setting(name = "FireworkTime")
	public static int FIREWORK_TIME;
	
	@Setting(name = "ItemPetSummonTime")
	public static int ITEM_PET_SUMMON_TIME;
	
	@Setting(name = "HeroVoiceTime")
	public static int HERO_VOICE_TIME;
	
	@Setting(name = "GlobalChatTime")
	public static int GLOBAL_CHAT_TIME;
	
	@Setting(name = "TradeChatTime")
	public static int TRADE_CHAT_TIME;
	
	@Setting(name = "SubclassTime")
	public static int SUBCLASS_TIME;
	
	@Setting(name = "DropItemTime")
	public static int DROP_ITEM_TIME;
	
	@Setting(name = "ServerBypassTime")
	public static int SERVER_BYPASS_TIME;
	
	@Setting(name = "MultiSellTime")
	public static int MULTISELL_TIME;
	
	@Setting(name = "TransactionTime")
	public static int TRANSACTION_TIME;
	
	@Setting(name = "ManufactureTime")
	public static int MANUFACTURE_TIME;
	
	@Setting(name = "ManorTime")
	public static int MANOR_TIME;
	
	@Setting(name = "SendMailTime")
	public static int SENDMAIL_TIME;
	
	@Setting(name = "CharacterSelectTime")
	public static int CHARACTER_SELECT_TIME;
	
	@Setting(name = "ItemAuctionTime")
	public static int ITEM_AUCTION_TIME;
}
