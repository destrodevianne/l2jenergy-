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

import java.util.List;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Мо3олЬ
 */
@Configuration("feature.json")
public class FeatureConfig
{
	@Setting(name = "ClanHallTeleportFunctionFeeRatio")
	public static long CH_TELE_FEE_RATIO;
	
	@Setting(name = "ClanHallTeleportFunctionFeeLvl1")
	public static int CH_TELE1_FEE;
	
	@Setting(name = "ClanHallTeleportFunctionFeeLvl2")
	public static int CH_TELE2_FEE;
	
	@Setting(name = "ClanHallSupportFunctionFeeRatio")
	public static long CH_SUPPORT_FEE_RATIO;
	
	@Setting(name = "ClanHallSupportFeeLvl1")
	public static int CH_SUPPORT1_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl2")
	public static int CH_SUPPORT2_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl3")
	public static int CH_SUPPORT3_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl4")
	public static int CH_SUPPORT4_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl5")
	public static int CH_SUPPORT5_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl6")
	public static int CH_SUPPORT6_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl7")
	public static int CH_SUPPORT7_FEE;
	
	@Setting(name = "ClanHallSupportFeeLvl8")
	public static int CH_SUPPORT8_FEE;
	
	@Setting(name = "ClanHallMpRegenerationFunctionFeeRatio")
	public static long CH_MPREG_FEE_RATIO;
	
	@Setting(name = "ClanHallMpRegenerationFeeLvl1")
	public static int CH_MPREG1_FEE;
	
	@Setting(name = "ClanHallMpRegenerationFeeLvl2")
	public static int CH_MPREG2_FEE;
	
	@Setting(name = "ClanHallMpRegenerationFeeLvl3")
	public static int CH_MPREG3_FEE;
	
	@Setting(name = "ClanHallMpRegenerationFeeLvl4")
	public static int CH_MPREG4_FEE;
	
	@Setting(name = "ClanHallMpRegenerationFeeLvl5")
	public static int CH_MPREG5_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFunctionFeeRatio")
	public static long CH_HPREG_FEE_RATIO;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl1")
	public static int CH_HPREG1_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl2")
	public static int CH_HPREG2_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl3")
	public static int CH_HPREG3_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl4")
	public static int CH_HPREG4_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl5")
	public static int CH_HPREG5_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl6")
	public static int CH_HPREG6_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl7")
	public static int CH_HPREG7_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl8")
	public static int CH_HPREG8_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl9")
	public static int CH_HPREG9_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl10")
	public static int CH_HPREG10_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl11")
	public static int CH_HPREG11_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl12")
	public static int CH_HPREG12_FEE;
	
	@Setting(name = "ClanHallHpRegenerationFeeLvl13")
	public static int CH_HPREG13_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFunctionFeeRatio")
	public static long CH_EXPREG_FEE_RATIO;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl1")
	public static int CH_EXPREG1_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl2")
	public static int CH_EXPREG2_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl3")
	public static int CH_EXPREG3_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl4")
	public static int CH_EXPREG4_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl5")
	public static int CH_EXPREG5_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl6")
	public static int CH_EXPREG6_FEE;
	
	@Setting(name = "ClanHallExpRegenerationFeeLvl7")
	public static int CH_EXPREG7_FEE;
	
	@Setting(name = "ClanHallItemCreationFunctionFeeRatio")
	public static long CH_ITEM_FEE_RATIO;
	
	@Setting(name = "ClanHallItemCreationFunctionFeeLvl1")
	public static int CH_ITEM1_FEE;
	
	@Setting(name = "ClanHallItemCreationFunctionFeeLvl2")
	public static int CH_ITEM2_FEE;
	
	@Setting(name = "ClanHallItemCreationFunctionFeeLvl3")
	public static int CH_ITEM3_FEE;
	
	@Setting(name = "ClanHallCurtainFunctionFeeRatio")
	public static long CH_CURTAIN_FEE_RATIO;
	
	@Setting(name = "ClanHallCurtainFunctionFeeLvl1")
	public static int CH_CURTAIN1_FEE;
	
	@Setting(name = "ClanHallCurtainFunctionFeeLvl2")
	public static int CH_CURTAIN2_FEE;
	
	@Setting(name = "ClanHallFrontPlatformFunctionFeeRatio")
	public static long CH_FRONT_FEE_RATIO;
	
	@Setting(name = "ClanHallFrontPlatformFunctionFeeLvl1")
	public static int CH_FRONT1_FEE;
	
	@Setting(name = "ClanHallFrontPlatformFunctionFeeLvl2")
	public static int CH_FRONT2_FEE;
	
	@Setting(name = "AltClanHallMpBuffFree")
	public static boolean CH_BUFF_FREE;
	
	@Setting(name = "CastleTeleportFunctionFeeRatio")
	public static long CS_TELE_FEE_RATIO;
	
	@Setting(name = "CastleTeleportFunctionFeeLvl1")
	public static int CS_TELE1_FEE;
	
	@Setting(name = "CastleTeleportFunctionFeeLvl2")
	public static int CS_TELE2_FEE;
	
	@Setting(name = "CastleMpRegenerationFunctionFeeRatio")
	public static long CS_MPREG_FEE_RATIO;
	
	@Setting(name = "CastleMpRegenerationFeeLvl1")
	public static int CS_MPREG1_FEE;
	
	@Setting(name = "CastleMpRegenerationFeeLvl2")
	public static int CS_MPREG2_FEE;
	
	@Setting(name = "CastleHpRegenerationFunctionFeeRatio")
	public static long CS_HPREG_FEE_RATIO;
	
	@Setting(name = "CastleHpRegenerationFeeLvl1")
	public static int CS_HPREG1_FEE;
	
	@Setting(name = "CastleHpRegenerationFeeLvl2")
	public static int CS_HPREG2_FEE;
	
	@Setting(name = "CastleExpRegenerationFunctionFeeRatio")
	public static long CS_EXPREG_FEE_RATIO;
	
	@Setting(name = "CastleExpRegenerationFeeLvl1")
	public static int CS_EXPREG1_FEE;
	
	@Setting(name = "CastleExpRegenerationFeeLvl2")
	public static int CS_EXPREG2_FEE;
	
	@Setting(name = "CastleSupportFunctionFeeRatio")
	public static long CS_SUPPORT_FEE_RATIO;
	
	@Setting(name = "CastleSupportFeeLvl1")
	public static int CS_SUPPORT1_FEE;
	
	@Setting(name = "CastleSupportFeeLvl2")
	public static int CS_SUPPORT2_FEE;
	
	@Setting(name = "SiegeHourList", splitter = ",", method = "parseSiegeHourList")
	public static List<Integer> SIEGE_HOUR_LIST;
	
	@Setting(name = "OuterDoorUpgradePriceLvl2")
	public static int OUTER_DOOR_UPGRADE_PRICE2;
	
	@Setting(name = "OuterDoorUpgradePriceLvl3")
	public static int OUTER_DOOR_UPGRADE_PRICE3;
	
	@Setting(name = "OuterDoorUpgradePriceLvl5")
	public static int OUTER_DOOR_UPGRADE_PRICE5;
	
	@Setting(name = "InnerDoorUpgradePriceLvl2")
	public static int INNER_DOOR_UPGRADE_PRICE2;
	
	@Setting(name = "InnerDoorUpgradePriceLvl3")
	public static int INNER_DOOR_UPGRADE_PRICE3;
	
	@Setting(name = "InnerDoorUpgradePriceLvl5")
	public static int INNER_DOOR_UPGRADE_PRICE5;
	
	@Setting(name = "WallUpgradePriceLvl2")
	public static int WALL_UPGRADE_PRICE2;
	
	@Setting(name = "WallUpgradePriceLvl3")
	public static int WALL_UPGRADE_PRICE3;
	
	@Setting(name = "WallUpgradePriceLvl5")
	public static int WALL_UPGRADE_PRICE5;
	
	@Setting(name = "TrapUpgradePriceLvl1")
	public static int TRAP_UPGRADE_PRICE1;
	
	@Setting(name = "TrapUpgradePriceLvl2")
	public static int TRAP_UPGRADE_PRICE2;
	
	@Setting(name = "TrapUpgradePriceLvl3")
	public static int TRAP_UPGRADE_PRICE3;
	
	@Setting(name = "TrapUpgradePriceLvl4")
	public static int TRAP_UPGRADE_PRICE4;
	
	@Setting(name = "FortressTeleportFunctionFeeRatio")
	public static long FS_TELE_FEE_RATIO;
	
	@Setting(name = "FortressTeleportFunctionFeeLvl1")
	public static int FS_TELE1_FEE;
	
	@Setting(name = "FortressTeleportFunctionFeeLvl2")
	public static int FS_TELE2_FEE;
	
	@Setting(name = "FortressSupportFunctionFeeRatio")
	public static long FS_SUPPORT_FEE_RATIO;
	
	@Setting(name = "FortressSupportFeeLvl1")
	public static int FS_SUPPORT1_FEE;
	
	@Setting(name = "FortressSupportFeeLvl2")
	public static int FS_SUPPORT2_FEE;
	
	@Setting(name = "FortressMpRegenerationFunctionFeeRatio")
	public static long FS_MPREG_FEE_RATIO;
	
	@Setting(name = "FortressMpRegenerationFeeLvl1")
	public static int FS_MPREG1_FEE;
	
	@Setting(name = "FortressMpRegenerationFeeLvl2")
	public static int FS_MPREG2_FEE;
	
	@Setting(name = "FortressHpRegenerationFunctionFeeRatio")
	public static long FS_HPREG_FEE_RATIO;
	
	@Setting(name = "FortressHpRegenerationFeeLvl1")
	public static int FS_HPREG1_FEE;
	
	@Setting(name = "FortressHpRegenerationFeeLvl2")
	public static int FS_HPREG2_FEE;
	@Setting(name = "FortressExpRegenerationFunctionFeeRatio")
	public static long FS_EXPREG_FEE_RATIO;
	
	@Setting(name = "FortressExpRegenerationFeeLvl1")
	public static int FS_EXPREG1_FEE;
	
	@Setting(name = "FortressExpRegenerationFeeLvl2")
	public static int FS_EXPREG2_FEE;
	
	@Setting(name = "FortressPeriodicUpdateFrequency")
	public static int FS_UPDATE_FRQ;
	
	@Setting(name = "FortressBloodOathCount")
	public static int FS_BLOOD_OATH_COUNT;
	
	@Setting(name = "FortressMaxSupplyLevel")
	public static int FS_MAX_SUPPLY_LEVEL;
	
	@Setting(name = "FortressFeeForCastle")
	public static int FS_FEE_FOR_CASTLE;
	
	@Setting(name = "FortressMaximumOwnTime")
	public static int FS_MAX_OWN_TIME;
	
	@Setting(name = "AltCastleForDawn")
	public static boolean ALT_GAME_CASTLE_DAWN;
	
	@Setting(name = "AltCastleForDusk")
	public static boolean ALT_GAME_CASTLE_DUSK;
	
	@Setting(name = "AltRequireClanCastle")
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	
	@Setting(name = "AltFestivalMinPlayer")
	public static int ALT_FESTIVAL_MIN_PLAYER;
	
	@Setting(name = "AltMaxPlayerContrib")
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	
	@Setting(name = "AltFestivalManagerStart")
	public static long ALT_FESTIVAL_MANAGER_START;
	
	@Setting(name = "AltFestivalLength")
	public static long ALT_FESTIVAL_LENGTH;
	
	@Setting(name = "AltFestivalCycleLength")
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	
	@Setting(name = "AltFestivalFirstSpawn")
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	
	@Setting(name = "AltFestivalFirstSwarm")
	public static long ALT_FESTIVAL_FIRST_SWARM;
	
	@Setting(name = "AltFestivalSecondSpawn")
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	
	@Setting(name = "AltFestivalSecondSwarm")
	public static long ALT_FESTIVAL_SECOND_SWARM;
	
	@Setting(name = "AltFestivalChestSpawn")
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	
	@Setting(name = "AltDawnGatesPdefMult")
	public static double ALT_SIEGE_DAWN_GATES_PDEF_MULT;
	
	@Setting(name = "AltDuskGatesPdefMult")
	public static double ALT_SIEGE_DUSK_GATES_PDEF_MULT;
	
	@Setting(name = "AltDawnGatesMdefMult")
	public static double ALT_SIEGE_DAWN_GATES_MDEF_MULT;
	
	@Setting(name = "AltDuskGatesMdefMult")
	public static double ALT_SIEGE_DUSK_GATES_MDEF_MULT;
	
	@Setting(name = "StrictSevenSigns")
	public static boolean ALT_STRICT_SEVENSIGNS;
	
	@Setting(name = "AltSevenSignsLazyUpdate")
	public static boolean ALT_SEVENSIGNS_LAZY_UPDATE;
	
	@Setting(name = "SevenSignsDawnTicketQuantity")
	public static int SSQ_DAWN_TICKET_QUANTITY;
	
	@Setting(name = "SevenSignsDawnTicketPrice")
	public static int SSQ_DAWN_TICKET_PRICE;
	
	@Setting(name = "SevenSignsDawnTicketBundle")
	public static int SSQ_DAWN_TICKET_BUNDLE;
	
	@Setting(name = "SevenSignsManorsAgreementId")
	public static int SSQ_MANORS_AGREEMENT_ID;
	
	@Setting(name = "SevenSignsJoinDawnFee")
	public static int SSQ_JOIN_DAWN_ADENA_FEE;
	
	@Setting(name = "TakeFortPoints")
	public static int TAKE_FORT_POINTS;
	
	@Setting(name = "LooseFortPoints")
	public static int LOOSE_FORT_POINTS;
	
	@Setting(name = "TakeCastlePoints")
	public static int TAKE_CASTLE_POINTS;
	
	@Setting(name = "LooseCastlePoints")
	public static int LOOSE_CASTLE_POINTS;
	
	@Setting(name = "CastleDefendedPoints")
	public static int CASTLE_DEFENDED_POINTS;
	
	@Setting(name = "FestivalOfDarknessWin")
	public static int FESTIVAL_WIN_POINTS;
	
	@Setting(name = "HeroPoints")
	public static int HERO_POINTS;
	
	@Setting(name = "CreateRoyalGuardCost")
	public static int ROYAL_GUARD_COST;
	
	@Setting(name = "CreateKnightUnitCost")
	public static int KNIGHT_UNIT_COST;
	
	@Setting(name = "ReinforceKnightUnitCost")
	public static int KNIGHT_REINFORCE_COST;
	
	@Setting(name = "KillBallistaPoints")
	public static int BALLISTA_POINTS;
	
	@Setting(name = "BloodAlliancePoints")
	public static int BLOODALLIANCE_POINTS;
	
	@Setting(name = "BloodOathPoints")
	public static int BLOODOATH_POINTS;
	
	@Setting(name = "KnightsEpaulettePoints")
	public static int KNIGHTSEPAULETTE_POINTS;
	
	@Setting(name = "ReputationScorePerKill")
	public static int REPUTATION_SCORE_PER_KILL;
	
	@Setting(name = "CompleteAcademyMinPoints")
	public static int JOIN_ACADEMY_MIN_REP_SCORE;
	
	@Setting(name = "CompleteAcademyMaxPoints")
	public static int JOIN_ACADEMY_MAX_REP_SCORE;
	
	@Setting(name = "1stRaidRankingPoints")
	public static int RAID_RANKING_1ST;
	
	@Setting(name = "2ndRaidRankingPoints")
	public static int RAID_RANKING_2ND;
	
	@Setting(name = "3rdRaidRankingPoints")
	public static int RAID_RANKING_3RD;
	
	@Setting(name = "4thRaidRankingPoints")
	public static int RAID_RANKING_4TH;
	
	@Setting(name = "5thRaidRankingPoints")
	public static int RAID_RANKING_5TH;
	
	@Setting(name = "6thRaidRankingPoints")
	public static int RAID_RANKING_6TH;
	
	@Setting(name = "7thRaidRankingPoints")
	public static int RAID_RANKING_7TH;
	
	@Setting(name = "8thRaidRankingPoints")
	public static int RAID_RANKING_8TH;
	
	@Setting(name = "9thRaidRankingPoints")
	public static int RAID_RANKING_9TH;
	
	@Setting(name = "10thRaidRankingPoints")
	public static int RAID_RANKING_10TH;
	
	@Setting(name = "UpTo50thRaidRankingPoints")
	public static int RAID_RANKING_UP_TO_50TH;
	
	@Setting(name = "UpTo100thRaidRankingPoints")
	public static int RAID_RANKING_UP_TO_100TH;
	
	@Setting(name = "ClanLevel6Cost")
	public static int CLAN_LEVEL_6_COST;
	
	@Setting(name = "ClanLevel7Cost")
	public static int CLAN_LEVEL_7_COST;
	
	@Setting(name = "ClanLevel8Cost")
	public static int CLAN_LEVEL_8_COST;
	
	@Setting(name = "ClanLevel9Cost")
	public static int CLAN_LEVEL_9_COST;
	
	@Setting(name = "ClanLevel10Cost")
	public static int CLAN_LEVEL_10_COST;
	
	@Setting(name = "ClanLevel11Cost")
	public static int CLAN_LEVEL_11_COST;
	
	@Setting(name = "ClanLevel6Requirement")
	public static int CLAN_LEVEL_6_REQUIREMENT;
	
	@Setting(name = "ClanLevel7Requirement")
	public static int CLAN_LEVEL_7_REQUIREMENT;
	
	@Setting(name = "ClanLevel8Requirement")
	public static int CLAN_LEVEL_8_REQUIREMENT;
	
	@Setting(name = "ClanLevel9Requirement")
	public static int CLAN_LEVEL_9_REQUIREMENT;
	
	@Setting(name = "ClanLevel10Requirement")
	public static int CLAN_LEVEL_10_REQUIREMENT;
	
	@Setting(name = "ClanLevel11Requirement")
	public static int CLAN_LEVEL_11_REQUIREMENT;
	
	@Setting(name = "AllowRideWyvernAlways")
	public static boolean ALLOW_WYVERN_ALWAYS;
	
	@Setting(name = "AllowRideWyvernDuringSiege")
	public static boolean ALLOW_WYVERN_DURING_SIEGE;
	
	public static void parseSiegeHourList(String value)
	{
		if (Util.isDigit(value))
		{
			SIEGE_HOUR_LIST.add(Integer.parseInt(value));
		}
	}
}