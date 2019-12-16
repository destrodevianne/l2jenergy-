/*
 * Copyright (C) 2004-2019 L2jEnergy Server
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

import java.util.Map;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Мо3олЬ
 */
@Configuration("rates.json")
public class RatesConfig
{
	@Setting(name = "RateXp")
	public static float RATE_XP;
	
	@Setting(name = "RateSp")
	public static float RATE_SP;
	
	@Setting(name = "RatePartyXp")
	public static float RATE_PARTY_XP;
	
	@Setting(name = "RatePartySp")
	public static float RATE_PARTY_SP;
	
	@Setting(name = "RateExtractable")
	public static float RATE_EXTRACTABLE;
	
	@Setting(name = "RateDropManor")
	public static int RATE_DROP_MANOR;
	
	@Setting(name = "RateQuestDrop")
	public static float RATE_QUEST_DROP;
	
	@Setting(name = "RateQuestReward")
	public static float RATE_QUEST_REWARD;
	
	@Setting(name = "RateQuestRewardXP")
	public static float RATE_QUEST_REWARD_XP;
	
	@Setting(name = "RateQuestRewardSP")
	public static float RATE_QUEST_REWARD_SP;
	
	@Setting(name = "RateQuestRewardAdena")
	public static float RATE_QUEST_REWARD_ADENA;
	
	@Setting(name = "UseQuestRewardMultipliers")
	public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
	
	@Setting(name = "RateQuestRewardPotion")
	public static float RATE_QUEST_REWARD_POTION;
	
	@Setting(name = "RateQuestRewardScroll")
	public static float RATE_QUEST_REWARD_SCROLL;
	
	@Setting(name = "RateQuestRewardRecipe")
	public static float RATE_QUEST_REWARD_RECIPE;
	
	@Setting(name = "RateQuestRewardMaterial")
	public static float RATE_QUEST_REWARD_MATERIAL;
	
	@Setting(name = "RateHellboundTrustIncrease")
	public static float RATE_HB_TRUST_INCREASE;
	
	@Setting(name = "RateHellboundTrustDecrease")
	public static float RATE_HB_TRUST_DECREASE;
	
	@Setting(name = "DeathDropAmountMultiplier")
	public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
	
	@Setting(name = "CorpseDropAmountMultiplier")
	public static float RATE_CORPSE_DROP_AMOUNT_MULTIPLIER;
	
	@Setting(name = "HerbDropAmountMultiplier")
	public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
	
	@Setting(name = "RaidDropAmountMultiplier")
	public static float RATE_RAID_DROP_AMOUNT_MULTIPLIER;
	
	@Setting(name = "DeathDropChanceMultiplier")
	public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
	
	@Setting(name = "CorpseDropChanceMultiplier")
	public static float RATE_CORPSE_DROP_CHANCE_MULTIPLIER;
	
	@Setting(name = "HerbDropChanceMultiplier")
	public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
	
	@Setting(name = "RaidDropChanceMultiplier")
	public static float RATE_RAID_DROP_CHANCE_MULTIPLIER;
	
	@Setting(name = "DropAmountMultiplierByItemId", method = "ratesDropAmountMultiplier")
	public static Map<Integer, Float> RATE_DROP_AMOUNT_MULTIPLIER;
	
	@Setting(name = "DropChanceMultiplierByItemId", method = "ratesDropChanceMultiplier")
	public static Map<Integer, Float> RATE_DROP_CHANCE_MULTIPLIER;
	
	@Setting(name = "RateKarmaLost", method = "ratesKarmaLost") // TODO: test
	public static float RATE_KARMA_LOST;
	
	@Setting(name = "RateKarmaExpLost")
	public static float RATE_KARMA_EXP_LOST;
	
	@Setting(name = "RateSiegeGuardsPrice")
	public static float RATE_SIEGE_GUARDS_PRICE;
	
	@Setting(name = "RateDropCommonHerbs")
	public static float RATE_DROP_COMMON_HERBS;
	
	@Setting(name = "RateDropHpHerbs")
	public static float RATE_DROP_HP_HERBS;
	
	@Setting(name = "RateDropMpHerbs")
	public static float RATE_DROP_MP_HERBS;
	
	@Setting(name = "RateDropSpecialHerbs")
	public static float RATE_DROP_SPECIAL_HERBS;
	
	@Setting(name = "PlayerDropLimit")
	public static int PLAYER_DROP_LIMIT;
	
	@Setting(name = "PlayerRateDrop")
	public static int PLAYER_RATE_DROP;
	
	@Setting(name = "PlayerRateDropItem")
	public static int PLAYER_RATE_DROP_ITEM;
	
	@Setting(name = "PlayerRateDropEquip")
	public static int PLAYER_RATE_DROP_EQUIP;
	
	@Setting(name = "PlayerRateDropEquipWeapon")
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	@Setting(name = "RateVitalityLevel1")
	public static float RATE_VITALITY_LEVEL_1;
	
	@Setting(name = "RateVitalityLevel2")
	public static float RATE_VITALITY_LEVEL_2;
	
	@Setting(name = "RateVitalityLevel3")
	public static float RATE_VITALITY_LEVEL_3;
	
	@Setting(name = "RateVitalityLevel4")
	public static float RATE_VITALITY_LEVEL_4;
	
	@Setting(name = "RateDropVitalityHerbs")
	public static float RATE_DROP_VITALITY_HERBS;
	
	@Setting(name = "RateRecoveryPeaceZone")
	public static float RATE_RECOVERY_VITALITY_PEACE_ZONE;
	
	@Setting(name = "RateVitalityLost")
	public static float RATE_VITALITY_LOST;
	
	@Setting(name = "RateVitalityGain")
	public static float RATE_VITALITY_GAIN;
	
	@Setting(name = "RateRecoveryOnReconnect")
	public static float RATE_RECOVERY_ON_RECONNECT;
	
	@Setting(name = "PetXpRate")
	public static float PET_XP_RATE;
	
	@Setting(name = "PetFoodRate")
	public static int PET_FOOD_RATE;
	
	@Setting(name = "SinEaterXpRate")
	public static float SINEATER_XP_RATE;
	
	@Setting(name = "KarmaDropLimit")
	public static int KARMA_DROP_LIMIT;
	
	@Setting(name = "KarmaRateDrop")
	public static int KARMA_RATE_DROP;
	
	@Setting(name = "KarmaRateDropItem")
	public static int KARMA_RATE_DROP_ITEM;
	
	@Setting(name = "KarmaRateDropEquip")
	public static int KARMA_RATE_DROP_EQUIP;
	
	@Setting(name = "KarmaRateDropEquipWeapon")
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	public void ratesKarmaLost(final float value)
	{
		float val = value;
		if (val == -1)
		{
			val = RATE_XP;
		}
		RATE_KARMA_LOST = val;
	}
	
	public void ratesDropChanceMultiplier(String value)
	{
		RATE_DROP_CHANCE_MULTIPLIER = Util.parseConfigMap(value, Integer.class, Float.class);
	}
	
	public void ratesDropAmountMultiplier(String value)
	{
		RATE_DROP_AMOUNT_MULTIPLIER = Util.parseConfigMap(value, Integer.class, Float.class);
	}
}
