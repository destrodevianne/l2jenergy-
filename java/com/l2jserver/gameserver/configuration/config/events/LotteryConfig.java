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
package com.l2jserver.gameserver.configuration.config.events;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("events/lottery.json")
public class LotteryConfig
{
	@Setting(name = "AllowLottery")
	public static boolean ALLOW_LOTTERY;
	
	@Setting(name = "AltLotteryPrize")
	public static long ALT_LOTTERY_PRIZE;
	
	@Setting(name = "AltLotteryTicketPrice")
	public static long ALT_LOTTERY_TICKET_PRICE;
	
	@Setting(name = "AltLottery5NumberRate")
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	
	@Setting(name = "AltLottery4NumberRate")
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	
	@Setting(name = "AltLottery3NumberRate")
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	
	@Setting(name = "AltLottery2and1NumberPrize")
	public static long ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
}
