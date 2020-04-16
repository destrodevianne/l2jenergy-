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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("npc.json")
public class NpcConfig
{
	@Setting(ignore = true)
	private static final Logger LOG = LoggerFactory.getLogger(NpcConfig.class);
	
	@Setting(name = "AnnounceMammonSpawn")
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	
	@Setting(name = "AltMobAgroInPeaceZone")
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	
	@Setting(name = "AltAttackableNpcs")
	public static boolean ALT_ATTACKABLE_NPCS;
	
	@Setting(name = "AltGameViewNpc")
	public static boolean ALT_GAME_VIEWNPC;
	
	@Setting(name = "MaxDriftRange")
	public static int MAX_DRIFT_RANGE;
	
	@Setting(name = "UseDeepBlueDropRules")
	public static boolean DEEPBLUE_DROP_RULES;
	
	@Setting(name = "UseDeepBlueDropRulesRaid")
	public static boolean DEEPBLUE_DROP_RULES_RAID;
	
	@Setting(name = "ShowNpcLevel")
	public static boolean SHOW_NPC_LVL;
	
	@Setting(name = "ShowCrestWithoutQuest")
	public static boolean SHOW_CREST_WITHOUT_QUEST;
	
	@Setting(name = "EnableRandomEnchantEffect")
	public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
	
	@Setting(name = "MinNPCLevelForDmgPenalty")
	public static int MIN_NPC_LVL_DMG_PENALTY;
	
	@Setting(name = "DmgPenaltyForLvLDifferences", method = "parseDmgPenalty")
	public static Map<Integer, Float> NPC_DMG_PENALTY;
	
	@Setting(name = "CritDmgPenaltyForLvLDifferences", method = "parseCritDmg")
	public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
	
	@Setting(name = "SkillDmgPenaltyForLvLDifferences", method = "parseSkillDmg")
	public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
	
	@Setting(name = "SkillChancePenaltyForLvLDifferences", method = "parseSkillChance")
	public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
	
	@Setting(name = "MinNPCLevelForMagicPenalty")
	public static int MIN_NPC_LVL_MAGIC_PENALTY;
	
	@Setting(name = "DecayTimeTask")
	public static int DECAY_TIME_TASK;
	
	@Setting(name = "DefaultCorpseTime")
	public static int DEFAULT_CORPSE_TIME;
	
	@Setting(name = "SpoiledCorpseExtendTime")
	public static int SPOILED_CORPSE_EXTEND_TIME;
	
	@Setting(name = "CorpseConsumeSkillAllowedTimeBeforeDecay")
	public static int CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY;
	
	@Setting(name = "GuardAttackAggroMob")
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	
	@Setting(name = "AllowWyvernUpgrader")
	public static boolean ALLOW_WYVERN_UPGRADER;
	
	@Setting(name = "ListPetRentNpc", method = "listPet")
	public static List<Integer> LIST_PET_RENT_NPC;
	
	@Setting(name = "RaidHpRegenMultiplier")
	public static double RAID_HP_REGEN_MULTIPLIER;
	
	@Setting(name = "RaidMpRegenMultiplier")
	public static double RAID_MP_REGEN_MULTIPLIER;
	
	@Setting(name = "RaidPDefenceMultiplier")
	public static double RAID_PDEFENCE_MULTIPLIER;
	
	@Setting(name = "RaidMDefenceMultiplier")
	public static double RAID_MDEFENCE_MULTIPLIER;
	
	@Setting(name = "RaidPAttackMultiplier")
	public static double RAID_PATTACK_MULTIPLIER;
	
	@Setting(name = "RaidMAttackMultiplier")
	public static double RAID_MATTACK_MULTIPLIER;
	
	@Setting(name = "RaidMinionRespawnTime")
	public static double RAID_MINION_RESPAWN_TIMER;
	
	@Setting(name = "CustomMinionsRespawnTime", method = "minionsRespawnTime")
	public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
	
	@Setting(name = "RaidMinRespawnMultiplier")
	public static float RAID_MIN_RESPAWN_MULTIPLIER;
	
	@Setting(name = "RaidMaxRespawnMultiplier")
	public static float RAID_MAX_RESPAWN_MULTIPLIER;
	
	@Setting(name = "DisableRaidCurse")
	public static boolean RAID_DISABLE_CURSE;
	
	@Setting(name = "RaidChaosTime")
	public static int RAID_CHAOS_TIME;
	
	@Setting(name = "GrandChaosTime")
	public static int GRAND_CHAOS_TIME;
	
	@Setting(name = "MinionChaosTime")
	public static int MINION_CHAOS_TIME;
	
	@Setting(name = "MaximumSlotsForPet")
	public static int INVENTORY_MAXIMUM_PET;
	
	@Setting(name = "PetHpRegenMultiplier")
	public static double PET_HP_REGEN_MULTIPLIER;
	
	@Setting(name = "PetMpRegenMultiplier")
	public static double PET_MP_REGEN_MULTIPLIER;
	
	@Setting(name = "DropAdenaMinLevelDifference")
	public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
	
	@Setting(name = "DropAdenaMaxLevelDifference")
	public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
	
	@Setting(name = "DropAdenaMinLevelGapChance")
	public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
	
	@Setting(name = "DropItemMinLevelDifference")
	public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
	
	@Setting(name = "DropItemMaxLevelDifference")
	public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
	
	@Setting(name = "DropItemMinLevelGapChance")
	public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
	
	public void listPet(final String value)
	{
		String[] listPetRentNpc = value.split(",");
		LIST_PET_RENT_NPC = new ArrayList<>(listPetRentNpc.length);
		for (String id : listPetRentNpc)
		{
			LIST_PET_RENT_NPC.add(Integer.valueOf(id));
		}
	}
	
	public void minionsRespawnTime(final String value)
	{
		final String[] propertySplit = value.split(";");
		MINIONS_RESPAWN_TIME = new HashMap<>(propertySplit.length);
		for (String prop : propertySplit)
		{
			String[] propSplit = prop.split(",");
			if (propSplit.length != 2)
			{
				LOG.warn("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime {}", prop);
			}
			
			try
			{
				MINIONS_RESPAWN_TIME.put(Integer.valueOf(propSplit[0]), Integer.valueOf(propSplit[1]));
			}
			catch (NumberFormatException nfe)
			{
				if (!prop.isEmpty())
				{
					LOG.warn("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime {}", prop);
				}
			}
		}
	}
	
	public void parseDmgPenalty(String line)
	{
		String[] propertySplit = line.split(",");
		NPC_DMG_PENALTY = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			NPC_DMG_PENALTY.put(i++, Float.parseFloat(value));
		}
	}
	
	public void parseCritDmg(String line)
	{
		String[] propertySplit = line.split(",");
		NPC_CRIT_DMG_PENALTY = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			NPC_CRIT_DMG_PENALTY.put(i++, Float.parseFloat(value));
		}
	}
	
	public void parseSkillChance(String line)
	{
		String[] propertySplit = line.split(",");
		NPC_SKILL_CHANCE_PENALTY = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			NPC_SKILL_CHANCE_PENALTY.put(i++, Float.parseFloat(value));
		}
	}
	
	public void parseSkillDmg(String line)
	{
		String[] propertySplit = line.split(",");
		NPC_SKILL_DMG_PENALTY = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			NPC_SKILL_DMG_PENALTY.put(i++, Float.parseFloat(value));
		}
	}
}
