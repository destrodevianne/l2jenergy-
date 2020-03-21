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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.gameserver.util.ClassMasterSettings;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Мо3олЬ
 */
@Configuration("character.json")
public class CharacterConfig
{
	@Setting(ignore = true)
	private static final Logger LOG = LoggerFactory.getLogger(CharacterConfig.class);
	
	@Setting(name = "Delevel")
	public static boolean ALT_GAME_DELEVEL;
	
	@Setting(name = "DecreaseSkillOnDelevel")
	public static boolean DECREASE_SKILL_LEVEL;
	
	@Setting(name = "AltWeightLimit")
	public static double ALT_WEIGHT_LIMIT;
	
	@Setting(name = "RunSpeedBoost")
	public static int RUN_SPD_BOOST;
	
	@Setting(name = "DeathPenaltyChance")
	public static int DEATH_PENALTY_CHANCE;
	
	@Setting(name = "RespawnRestoreCP", decrease = 100)
	public static double RESPAWN_RESTORE_CP; // character.getDouble("RespawnRestoreCP", 0) / 100;
	
	@Setting(name = "RespawnRestoreHP", decrease = 100)
	public static double RESPAWN_RESTORE_HP; // character.getDouble("RespawnRestoreHP", 65) / 100;
	
	@Setting(name = "RespawnRestoreMP", decrease = 100)
	public static double RESPAWN_RESTORE_MP; // character.getDouble("RespawnRestoreMP", 0) / 100;
	
	@Setting(name = "HpRegenMultiplier", decrease = 100)
	public static double HP_REGEN_MULTIPLIER; // character.getDouble("HpRegenMultiplier", 100) / 100;
	
	@Setting(name = "MpRegenMultiplier", decrease = 100)
	public static double MP_REGEN_MULTIPLIER; // character.getDouble("MpRegenMultiplier", 100) / 100;
	
	@Setting(name = "CpRegenMultiplier", decrease = 100)
	public static double CP_REGEN_MULTIPLIER; // character.getDouble("CpRegenMultiplier", 100) / 100;
	
	@Setting(name = "EnableModifySkillDuration")
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	
	@Setting(name = "SkillDurationList", method = "skillListDuration", canNull = true)
	public static Map<Integer, Integer> SKILL_DURATION_LIST;
	
	@Setting(name = "EnableModifySkillReuse")
	public static boolean ENABLE_MODIFY_SKILL_REUSE;
	
	@Setting(name = "SkillReuseList", method = "skillListReuse", canNull = true)
	public static Map<Integer, Integer> SKILL_REUSE_LIST;
	
	@Setting(name = "AutoLearnSkills")
	public static boolean AUTO_LEARN_SKILLS;
	
	@Setting(name = "AutoLearnForgottenScrollSkills")
	public static boolean AUTO_LEARN_FS_SKILLS;
	
	@Setting(name = "AutoLootHerbs")
	public static boolean AUTO_LOOT_HERBS;
	
	@Setting(name = "MaxBuffAmount")
	public static byte BUFFS_MAX_AMOUNT;
	
	@Setting(name = "MaxTriggeredBuffAmount")
	public static byte TRIGGERED_BUFFS_MAX_AMOUNT;
	
	@Setting(name = "MaxDanceAmount")
	public static byte DANCES_MAX_AMOUNT;
	
	@Setting(name = "DanceCancelBuff")
	public static boolean DANCE_CANCEL_BUFF;
	
	@Setting(name = "DanceConsumeAdditionalMP")
	public static boolean DANCE_CONSUME_ADDITIONAL_MP;
	
	@Setting(name = "AltStoreDances")
	public static boolean ALT_STORE_DANCES;
	
	@Setting(name = "AutoLearnDivineInspiration")
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	
	@Setting(name = "AltGameCancelByHit")
	public static boolean ALT_GAME_CANCEL_BOW; // ALT_GAME_CANCEL_BOW = character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
	
	@Setting(name = "AltGameCancelByHit")
	public static boolean ALT_GAME_CANCEL_CAST; // ALT_GAME_CANCEL_CAST = character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
	
	@Setting(name = "MagicFailures")
	public static boolean ALT_GAME_MAGICFAILURES;
	
	@Setting(name = "PlayerFakeDeathUpProtection")
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	
	@Setting(name = "StoreSkillCooltime")
	public static boolean STORE_SKILL_COOLTIME;
	
	@Setting(name = "SubclassStoreSkillCooltime")
	public static boolean SUBCLASS_STORE_SKILL_COOLTIME;
	
	@Setting(name = "AltShieldBlocks")
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	
	@Setting(name = "AltPerfectShieldBlockRate")
	public static int ALT_PERFECT_SHLD_BLOCK;
	
	@Setting(name = "EffectTickRatio")
	public static long EFFECT_TICK_RATIO;
	
	@Setting(name = "AllowClassMasters")
	public static boolean ALLOW_CLASS_MASTERS;
	
	@Setting(name = "ConfigClassMaster", method = "configClassMaster")
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	
	@Setting(name = "AllowEntireTree")
	public static boolean ALLOW_ENTIRE_TREE;
	
	@Setting(name = "AlternateClassMaster")
	public static boolean ALTERNATE_CLASS_MASTER;
	
	@Setting(name = "LifeCrystalNeeded")
	public static boolean LIFE_CRYSTAL_NEEDED;
	
	@Setting(name = "EnchantSkillSpBookNeeded")
	public static boolean ES_SP_BOOK_NEEDED;
	
	@Setting(name = "DivineInspirationSpBookNeeded")
	public static boolean DIVINE_SP_BOOK_NEEDED;
	
	@Setting(name = "AltGameSkillLearn")
	public static boolean ALT_GAME_SKILL_LEARN;
	
	@Setting(name = "AltSubClassWithoutQuests")
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	
	@Setting(name = "AltSubclassEverywhere")
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	
	@Setting(name = "AltTransformationWithoutQuest")
	public static boolean ALLOW_TRANSFORM_WITHOUT_QUEST;
	
	@Setting(name = "FeeDeleteTransferSkills")
	public static int FEE_DELETE_TRANSFER_SKILLS;
	
	@Setting(name = "FeeDeleteSubClassSkills")
	public static int FEE_DELETE_SUBCLASS_SKILLS;
	
	@Setting(name = "SummonStoreSkillCooltime")
	public static boolean SUMMON_STORE_SKILL_COOLTIME;
	
	@Setting(name = "RestoreServitorOnReconnect")
	public static boolean RESTORE_SERVITOR_ON_RECONNECT;
	
	@Setting(name = "RestorePetOnReconnect")
	public static boolean RESTORE_PET_ON_RECONNECT;
	
	@Setting(name = "EnableVitality")
	public static boolean ENABLE_VITALITY;
	
	@Setting(name = "RecoverVitalityOnReconnect")
	public static boolean RECOVER_VITALITY_ON_RECONNECT;
	
	@Setting(name = "StartingVitalityPoints")
	public static int STARTING_VITALITY_POINTS;
	
	@Setting(name = "MaxExpBonus")
	public static double MAX_BONUS_EXP;
	
	@Setting(name = "MaxSpBonus")
	public static double MAX_BONUS_SP;
	
	@Setting(name = "MaxRunSpeed")
	public static int MAX_RUN_SPEED;
	
	@Setting(name = "MaxPCritRate")
	public static int MAX_PCRIT_RATE;
	
	@Setting(name = "MaxMCritRate")
	public static int MAX_MCRIT_RATE;
	
	@Setting(name = "MaxPAtkSpeed")
	public static int MAX_PATK_SPEED;
	
	@Setting(name = "MaxMAtkSpeed")
	public static int MAX_MATK_SPEED;
	
	@Setting(name = "MaxEvasion")
	public static int MAX_EVASION;
	
	@Setting(name = "MinAbnormalStateSuccessRate")
	public static int MIN_ABNORMAL_STATE_SUCCESS_RATE;
	
	@Setting(name = "MaxAbnormalStateSuccessRate")
	public static int MAX_ABNORMAL_STATE_SUCCESS_RATE;
	
	@Setting(name = "MaxPlayerLevel")
	public static int MAX_PLAYER_LEVEL;
	
	@Setting(name = "MaxPetLevel")
	public static int MAX_PET_LEVEL;
	
	@Setting(name = "MaxSubclass")
	public static byte MAX_SUBCLASS;
	
	@Setting(name = "BaseSubclassLevel")
	public static int BASE_SUBCLASS_LEVEL;
	
	@Setting(name = "MaxSubclassLevel")
	public static int MAX_SUBCLASS_LEVEL;
	
	@Setting(name = "MaxPvtStoreSellSlotsDwarf")
	public static int MAX_PVTSTORESELL_SLOTS_DWARF;
	
	@Setting(name = "MaxPvtStoreSellSlotsOther")
	public static int MAX_PVTSTORESELL_SLOTS_OTHER;
	
	@Setting(name = "MaxPvtStoreBuySlotsDwarf")
	public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
	
	@Setting(name = "MaxPvtStoreBuySlotsOther")
	public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
	
	@Setting(name = "MaximumSlotsForNoDwarf")
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	
	@Setting(name = "MaximumSlotsForDwarf")
	public static int INVENTORY_MAXIMUM_DWARF;
	
	@Setting(name = "MaximumSlotsForGMPlayer")
	public static int INVENTORY_MAXIMUM_GM;
	
	@Setting(name = "MaximumSlotsForQuestItems")
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	
	@Setting(name = "MaximumWarehouseSlotsForDwarf")
	public static int WAREHOUSE_SLOTS_DWARF;
	
	@Setting(name = "MaximumWarehouseSlotsForNoDwarf")
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	
	@Setting(name = "MaximumWarehouseSlotsForClan")
	public static int WAREHOUSE_SLOTS_CLAN;
	
	@Setting(name = "MaximumFreightSlots")
	public static int ALT_FREIGHT_SLOTS;
	
	@Setting(name = "FreightPrice")
	public static int ALT_FREIGHT_PRICE;
	
	@Setting(name = "NpcTalkBlockingTime", increase = 1000)
	public static int PLAYER_MOVEMENT_BLOCK_TIME;
	
	@Setting(name = "EnchantChanceElementStone")
	public static double ENCHANT_CHANCE_ELEMENT_STONE;
	
	@Setting(name = "EnchantChanceElementCrystal")
	public static double ENCHANT_CHANCE_ELEMENT_CRYSTAL;
	
	@Setting(name = "EnchantChanceElementJewel")
	public static double ENCHANT_CHANCE_ELEMENT_JEWEL;
	
	@Setting(name = "EnchantChanceElementEnergy")
	public static double ENCHANT_CHANCE_ELEMENT_ENERGY;
	
	@Setting(name = "EnchantBlackList", method = "enchantBlackList", canNull = true)
	public static int[] ENCHANT_BLACKLIST;
	
	@Setting(name = "AugmentationNGSkillChance")
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	
	@Setting(name = "AugmentationNGGlowChance")
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	
	@Setting(name = "AugmentationMidSkillChance")
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	
	@Setting(name = "AugmentationMidGlowChance")
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	
	@Setting(name = "AugmentationHighSkillChance")
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	
	@Setting(name = "AugmentationHighGlowChance")
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	
	@Setting(name = "AugmentationTopSkillChance")
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	
	@Setting(name = "AugmentationTopGlowChance")
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	
	@Setting(name = "AugmentationBaseStatChance")
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	@Setting(name = "AugmentationAccSkillChance")
	public static int AUGMENTATION_ACC_SKILL_CHANCE;
	
	@Setting(name = "RetailLikeAugmentation")
	public static boolean RETAIL_LIKE_AUGMENTATION;
	
	@Setting(name = "RetailLikeAugmentationNoGradeChance", method = "retailLikeNoGradeChance")
	public static int[] RETAIL_LIKE_AUGMENTATION_NG_CHANCE;
	
	@Setting(name = "RetailLikeAugmentationMidGradeChance", method = "retailLikeMidGradeChance")
	public static int[] RETAIL_LIKE_AUGMENTATION_MID_CHANCE;
	
	@Setting(name = "RetailLikeAugmentationHighGradeChance", method = "retailLikeHighGradeChance")
	public static int[] RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE;
	
	@Setting(name = "RetailLikeAugmentationTopGradeChance", method = "retailLikeTopGradeChance")
	public static int[] RETAIL_LIKE_AUGMENTATION_TOP_CHANCE;
	
	@Setting(name = "RetailLikeAugmentationAccessory")
	public static boolean RETAIL_LIKE_AUGMENTATION_ACCESSORY;
	
	@Setting(name = "AugmentationBlackList", method = "augmentationBlackList")
	public static int[] AUGMENTATION_BLACKLIST;
	
	@Setting(name = "AltAllowAugmentPvPItems")
	public static boolean ALT_ALLOW_AUGMENT_PVP_ITEMS;
	
	@Setting(name = "AltKarmaPlayerCanBeKilledInPeaceZone")
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	
	@Setting(name = "AltKarmaPlayerCanShop")
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	
	@Setting(name = "AltKarmaPlayerCanTeleport")
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	
	@Setting(name = "AltKarmaPlayerCanUseGK")
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
	
	@Setting(name = "AltKarmaPlayerCanTrade")
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	
	@Setting(name = "AltKarmaPlayerCanUseWareHouse")
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	
	@Setting(name = "MaxPersonalFamePoints")
	public static int MAX_PERSONAL_FAME_POINTS;
	
	@Setting(name = "FortressZoneFameTaskFrequency")
	public static int FORTRESS_ZONE_FAME_TASK_FREQUENCY;
	
	@Setting(name = "FortressZoneFameAquirePoints")
	public static int FORTRESS_ZONE_FAME_AQUIRE_POINTS;
	
	@Setting(name = "CastleZoneFameTaskFrequency")
	public static int CASTLE_ZONE_FAME_TASK_FREQUENCY;
	
	@Setting(name = "CastleZoneFameAquirePoints")
	public static int CASTLE_ZONE_FAME_AQUIRE_POINTS;
	
	@Setting(name = "FameForDeadPlayers")
	public static boolean FAME_FOR_DEAD_PLAYERS;
	
	@Setting(name = "CraftingEnabled")
	public static boolean IS_CRAFTING_ENABLED;
	
	@Setting(name = "CraftMasterwork")
	public static boolean CRAFT_MASTERWORK;
	
	@Setting(name = "DwarfRecipeLimit")
	public static int DWARF_RECIPE_LIMIT;
	
	@Setting(name = "CommonRecipeLimit")
	public static int COMMON_RECIPE_LIMIT;
	
	@Setting(name = "AltGameCreation")
	public static boolean ALT_GAME_CREATION;
	
	@Setting(name = "AltGameCreationSpeed")
	public static double ALT_GAME_CREATION_SPEED;
	
	@Setting(name = "AltGameCreationXpRate")
	public static double ALT_GAME_CREATION_XP_RATE;
	
	@Setting(name = "AltGameCreationSpRate")
	public static double ALT_GAME_CREATION_SP_RATE;
	
	@Setting(name = "AltGameCreationRareXpSpRate")
	public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
	
	@Setting(name = "AltBlacksmithUseRecipes")
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	
	@Setting(name = "StoreRecipeShopList")
	public static boolean STORE_RECIPE_SHOPLIST;
	
	@Setting(name = "AltClanLeaderDateChange", minValue = 1, maxValue = 7)
	public static int ALT_CLAN_LEADER_DATE_CHANGE;
	
	@Setting(name = "AltClanLeaderHourChange")
	public static String ALT_CLAN_LEADER_HOUR_CHANGE;
	
	@Setting(name = "AltClanLeaderInstantActivation")
	public static boolean ALT_CLAN_LEADER_INSTANT_ACTIVATION;
	
	@Setting(name = "DaysBeforeJoinAClan")
	public static int ALT_CLAN_JOIN_DAYS;
	
	@Setting(name = "DaysBeforeCreateAClan")
	public static int ALT_CLAN_CREATE_DAYS;
	
	@Setting(name = "DaysToPassToDissolveAClan")
	public static int ALT_CLAN_DISSOLVE_DAYS;
	
	@Setting(name = "DaysBeforeJoinAllyWhenLeaved")
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	
	@Setting(name = "DaysBeforeJoinAllyWhenDismissed")
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	
	@Setting(name = "DaysBeforeAcceptNewClanWhenDismissed")
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	
	@Setting(name = "DaysBeforeCreateNewAllyWhenDissolved")
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	
	@Setting(name = "AltMaxNumOfClansInAlly")
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	
	@Setting(name = "AltMembersCanWithdrawFromClanWH")
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	
	@Setting(name = "RemoveCastleCirclets")
	public static boolean REMOVE_CASTLE_CIRCLETS;
	
	@Setting(name = "AltClanMembersForWar")
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	
	@Setting(name = "AltPartyRange")
	public static int ALT_PARTY_RANGE;
	
	@Setting(name = "AltPartyRange2")
	public static int ALT_PARTY_RANGE2;
	
	@Setting(name = "AltLeavePartyLeader")
	public static boolean ALT_LEAVE_PARTY_LEADER;
	
	@Setting(name = "InitialEquipmentEvent")
	public static boolean INITIAL_EQUIPMENT_EVENT;
	
	@Setting(name = "StartingAdena")
	public static long STARTING_ADENA;
	
	@Setting(name = "StartingLevel")
	public static int STARTING_LEVEL;
	
	@Setting(name = "StartingSP")
	public static int STARTING_SP;
	
	@Setting(name = "MaxAdena")
	public static long MAX_ADENA;
	
	@Setting(name = "AutoLoot")
	public static boolean AUTO_LOOT;
	
	@Setting(name = "AutoLootRaids")
	public static boolean AUTO_LOOT_RAIDS;
	
	@Setting(name = "RaidLootRightsInterval", increase = 1000)
	public static int LOOT_RAIDS_PRIVILEGE_INTERVAL;
	
	@Setting(name = "RaidLootRightsCCSize")
	public static int LOOT_RAIDS_PRIVILEGE_CC_SIZE;
	
	@Setting(name = "UnstuckInterval")
	public static int UNSTUCK_INTERVAL;
	
	@Setting(name = "TeleportWatchdogTimeout")
	public static int TELEPORT_WATCHDOG_TIMEOUT;
	
	@Setting(name = "PlayerSpawnProtection")
	public static int PLAYER_SPAWN_PROTECTION;
	
	@Setting(name = "PlayerSpawnProtectionAllowedItems", method = "spawnProtection", canNull = true)
	public static List<Integer> SPAWN_PROTECTION_ALLOWED_ITEMS;
	
	@Setting(name = "PlayerTeleportProtection")
	public static int PLAYER_TELEPORT_PROTECTION;
	
	@Setting(name = "RandomRespawnInTownEnabled")
	public static boolean RANDOM_RESPAWN_IN_TOWN_ENABLED;
	
	@Setting(name = "OffsetOnTeleportEnabled")
	public static boolean OFFSET_ON_TELEPORT_ENABLED;
	
	@Setting(name = "MaxOffsetOnTeleport")
	public static int MAX_OFFSET_ON_TELEPORT;
	
	@Setting(name = "PetitioningAllowed")
	public static boolean PETITIONING_ALLOWED;
	
	@Setting(name = "MaxPetitionsPerPlayer")
	public static int MAX_PETITIONS_PER_PLAYER;
	
	@Setting(name = "MaxPetitionsPending")
	public static int MAX_PETITIONS_PENDING;
	
	@Setting(name = "AltFreeTeleporting")
	public static boolean ALT_GAME_FREE_TELEPORT;
	
	@Setting(name = "DeleteCharAfterDays")
	public static int DELETE_DAYS;
	
	@Setting(name = "AltGameExponentXp")
	public static float ALT_GAME_EXPONENT_XP;
	
	@Setting(name = "AltGameExponentSp")
	public static float ALT_GAME_EXPONENT_SP;
	
	@Setting(name = "PartyXpCutoffMethod")
	public static String PARTY_XP_CUTOFF_METHOD;
	
	@Setting(name = "PartyXpCutoffPercent")
	public static double PARTY_XP_CUTOFF_PERCENT;
	
	@Setting(name = "PartyXpCutoffLevel")
	public static int PARTY_XP_CUTOFF_LEVEL;
	
	@Setting(name = "PartyXpCutoffGaps", method = "partyXpCutoffGaps")
	public static int[][] PARTY_XP_CUTOFF_GAPS;
	
	@Setting(name = "PartyXpCutoffGapPercent", method = "partyXpCutoffGapPercent")
	public static int[] PARTY_XP_CUTOFF_GAP_PERCENTS;
	
	@Setting(name = "DisableTutorial")
	public static boolean DISABLE_TUTORIAL;
	
	@Setting(name = "ExpertisePenalty")
	public static boolean EXPERTISE_PENALTY;
	
	@Setting(name = "StoreCharUiSettings")
	public static boolean STORE_UI_SETTINGS;
	
	@Setting(name = "ForbiddenNames", method = "forbidNames")
	public static Set<String> FORBIDDEN_NAMES;
	
	@Setting(name = "SilenceModeExclude")
	public static boolean SILENCE_MODE_EXCLUDE;
	
	@Setting(name = "AltValidateTriggerSkills")
	public static boolean ALT_VALIDATE_TRIGGER_SKILLS;
	
	public void forbidNames(String value)
	{
		FORBIDDEN_NAMES = new HashSet<>(Arrays.asList(value.split(",")));
	}
	
	public void partyXpCutoffGaps(String value)
	{
		final String[] gaps = value.split(";");
		PARTY_XP_CUTOFF_GAPS = new int[gaps.length][2];
		for (int i = 0; i < gaps.length; i++)
		{
			PARTY_XP_CUTOFF_GAPS[i] = new int[]
			{
				Integer.parseInt(gaps[i].split(",")[0]),
				Integer.parseInt(gaps[i].split(",")[1])
			};
		}
	}
	
	public void partyXpCutoffGapPercent(String value)
	{
		final String[] percents = value.split(";");
		PARTY_XP_CUTOFF_GAP_PERCENTS = new int[percents.length];
		for (int i = 0; i < percents.length; i++)
		{
			PARTY_XP_CUTOFF_GAP_PERCENTS[i] = Integer.parseInt(percents[i]);
		}
	}
	
	public void spawnProtection(String value)
	{
		String[] items = value.split(",");
		SPAWN_PROTECTION_ALLOWED_ITEMS = new ArrayList<>(items.length);
		for (String item : items)
		{
			try
			{
				if (!item.isEmpty())
				{
					SPAWN_PROTECTION_ALLOWED_ITEMS.add(Integer.parseInt(item));
				}
			}
			catch (NumberFormatException nfe)
			{
				LOG.warn("Player Spawn Protection: Wrong Item ID passed: {}", item, nfe);
			}
		}
	}
	
	public void skillListDuration(String value)
	{
		SKILL_DURATION_LIST = Util.parseConfigMap(value, Integer.class, Integer.class);
	}
	
	public void skillListReuse(String value)
	{
		SKILL_REUSE_LIST = Util.parseConfigMap(value, Integer.class, Integer.class);
	}
	
	public void configClassMaster(String value)
	{
		if (ALLOW_CLASS_MASTERS || ALTERNATE_CLASS_MASTER)
		{
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(value);
		}
	}
	
	public void enchantBlackList(String value)
	{
		String[] notenchantable = value.split(",");
		ENCHANT_BLACKLIST = new int[notenchantable.length];
		for (int i = 0; i < notenchantable.length; i++)
		{
			ENCHANT_BLACKLIST[i] = Integer.parseInt(notenchantable[i]);
		}
		Arrays.sort(ENCHANT_BLACKLIST);
	}
	
	public void retailLikeNoGradeChance(String value)
	{
		String[] array = value.split(",");
		RETAIL_LIKE_AUGMENTATION_NG_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_NG_CHANCE[i] = Integer.parseInt(array[i]);
		}
	}
	
	public void retailLikeMidGradeChance(String value)
	{
		String[] array = value.split(",");
		RETAIL_LIKE_AUGMENTATION_MID_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_MID_CHANCE[i] = Integer.parseInt(array[i]);
		}
	}
	
	public void retailLikeHighGradeChance(String value)
	{
		String[] array = value.split(",");
		RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE[i] = Integer.parseInt(array[i]);
		}
	}
	
	public void retailLikeTopGradeChance(String value)
	{
		String[] array = value.split(",");
		RETAIL_LIKE_AUGMENTATION_TOP_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_TOP_CHANCE[i] = Integer.parseInt(array[i]);
		}
	}
	
	public void augmentationBlackList(String value)
	{
		String[] array = value.split(",");
		AUGMENTATION_BLACKLIST = new int[array.length];
		for (int i = 0; i < array.length; i++)
		{
			AUGMENTATION_BLACKLIST[i] = Integer.parseInt(array[i]);
		}
		Arrays.sort(AUGMENTATION_BLACKLIST);
	}
}