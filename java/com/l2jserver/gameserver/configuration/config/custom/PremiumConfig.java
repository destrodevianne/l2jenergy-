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

import java.util.Map;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Мо3олЬ
 */
@Configuration("custom/premium.json")
public class PremiumConfig
{
	@Setting(name = "EnablePremiumSystem")
	public static boolean PREMIUM_SYSTEM_ENABLED;
	
	@Setting(name = "NewbiesPremiumPeriod", minValue = 0)
	public static int NEWBIES_PREMIUM_PERIOD;
	
	@Setting(name = "PremiumAllowVoiced")
	public static boolean PREMIUM_ALLOW_VOICED;
	
	@Setting(name = "NotifyPremiumExpiration")
	public static boolean NOTIFY_PREMIUM_EXPIRATION;
	
	@Setting(name = "PremiumRateXp")
	public static float PREMIUM_RATE_XP;
	
	@Setting(name = "PremiumRateSp")
	public static float PREMIUM_RATE_SP;
	
	@Setting(name = "PremiumRateDropChance")
	public static float PREMIUM_RATE_DROP_CHANCE;
	
	@Setting(name = "PremiumRateDropAmount")
	public static float PREMIUM_RATE_DROP_AMOUNT;
	
	@Setting(name = "PremiumRateDropManor")
	public static int PREMIUM_RATE_DROP_MANOR;
	
	@Setting(name = "PremiumRateSpoilChance")
	public static float PREMIUM_RATE_SPOIL_CHANCE;
	
	@Setting(name = "PremiumRateSpoilAmmount")
	public static float PREMIUM_RATE_SPOIL_AMMOUNT;
	
	@Setting(name = "PremiumDropChanceMultiplierByItemId", method = "premiumDropChanceMultiplier")
	public static Map<Integer, Float> PREMIUM_RATE_DROP_CHANCE_MULTIPLIER;
	
	@Setting(name = "PremiumDropAmountMultiplierByItemId", method = "premiumDropAmountMultiplier")
	public static Map<Integer, Float> PREMIUM_RATE_DROP_AMOUNT_MULTIPLIER;
	
	public void premiumDropChanceMultiplier(String value)
	{
		PREMIUM_RATE_DROP_CHANCE_MULTIPLIER = Util.parseConfigMap(value, Integer.class, Float.class);
	}
	
	public void premiumDropAmountMultiplier(String value)
	{
		PREMIUM_RATE_DROP_AMOUNT_MULTIPLIER = Util.parseConfigMap(value, Integer.class, Float.class);
	}
}
