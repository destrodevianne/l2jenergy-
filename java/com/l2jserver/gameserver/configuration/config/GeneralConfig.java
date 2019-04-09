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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Мо3олЬ
 */
@Configuration("general.json")
public class GeneralConfig
{
	@Setting(ignore = true)
	private static final Logger LOG = LoggerFactory.getLogger(GeneralConfig.class);
	
	@Setting(name = "EverybodyHasAdminRights")
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	
	@Setting(name = "ServerListBrackets")
	public static boolean SERVER_LIST_BRACKET;
	
	@Setting(name = "ServerListType", method = "getServerTypeId")
	public static int SERVER_LIST_TYPE;
	
	@Setting(name = "ServerListAge")
	public static int SERVER_LIST_AGE;
	
	@Setting(name = "ServerGMOnly")
	public static boolean SERVER_GMONLY;
	
	@Setting(name = "GMHeroAura")
	public static boolean GM_HERO_AURA;
	
	@Setting(name = "GMStartupInvulnerable")
	public static boolean GM_STARTUP_INVULNERABLE;
	
	@Setting(name = "GMStartupInvisible")
	public static boolean GM_STARTUP_INVISIBLE;
	
	@Setting(name = "GMStartupSilence")
	public static boolean GM_STARTUP_SILENCE;
	
	@Setting(name = "GMStartupAutoList")
	public static boolean GM_STARTUP_AUTO_LIST;
	
	@Setting(name = "GMStartupDietMode")
	public static boolean GM_STARTUP_DIET_MODE;
	
	@Setting(name = "GMItemRestriction")
	public static boolean GM_ITEM_RESTRICTION;
	
	@Setting(name = "GMSkillRestriction")
	public static boolean GM_SKILL_RESTRICTION;
	
	@Setting(name = "GMTradeRestrictedItems")
	public static boolean GM_TRADE_RESTRICTED_ITEMS;
	
	@Setting(name = "GMRestartFighting")
	public static boolean GM_RESTART_FIGHTING;
	
	@Setting(name = "GMShowAnnouncerName")
	public static boolean GM_ANNOUNCER_NAME;
	
	@Setting(name = "GMShowCritAnnouncerName")
	public static boolean GM_CRITANNOUNCER_NAME;
	
	@Setting(name = "GMGiveSpecialSkills")
	public static boolean GM_GIVE_SPECIAL_SKILLS;
	
	@Setting(name = "GMGiveSpecialAuraSkills")
	public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
	
	@Setting(name = "GameGuardEnforce")
	public static boolean GAMEGUARD_ENFORCE;
	
	@Setting(name = "GameGuardProhibitAction")
	public static boolean GAMEGUARD_PROHIBITACTION;
	
	@Setting(name = "LogChat")
	public static boolean LOG_CHAT;
	
	@Setting(name = "LogAutoAnnouncements")
	public static boolean LOG_AUTO_ANNOUNCEMENTS;
	
	@Setting(name = "LogItems")
	public static boolean LOG_ITEMS;
	
	@Setting(name = "LogItemsSmallLog")
	public static boolean LOG_ITEMS_SMALL_LOG;
	
	@Setting(name = "LogItemEnchants")
	public static boolean LOG_ITEM_ENCHANTS;
	
	@Setting(name = "LogSkillEnchants")
	public static boolean LOG_SKILL_ENCHANTS;
	
	@Setting(name = "GMAudit")
	public static boolean GMAUDIT;
	
	@Setting(name = "SkillCheckEnable")
	public static boolean SKILL_CHECK_ENABLE;
	
	@Setting(name = "SkillCheckRemove")
	public static boolean SKILL_CHECK_REMOVE;
	
	@Setting(name = "SkillCheckGM")
	public static boolean SKILL_CHECK_GM;
	
	@Setting(name = "Debug")
	public static boolean DEBUG;
	
	@Setting(name = "InstanceDebug")
	public static boolean DEBUG_INSTANCES;
	
	@Setting(name = "HtmlActionCacheDebug")
	public static boolean HTML_ACTION_CACHE_DEBUG;
	
	@Setting(name = "PacketHandlerDebug")
	public static boolean PACKET_HANDLER_DEBUG;
	
	@Setting(name = "Developer")
	public static boolean DEVELOPER;
	
	@Setting(name = "AltDevNoHandlers")
	public static boolean ALT_DEV_NO_HANDLERS;
	
	@Setting(name = "AltDevNoQuests")
	public static boolean ALT_DEV_NO_QUESTS;
	
	@Setting(name = "AltDevNoSpawns")
	public static boolean ALT_DEV_NO_SPAWNS;
	
	@Setting(name = "AltDevShowQuestsLoadInLogs")
	public static boolean ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS;
	
	@Setting(name = "AltDevShowScriptsLoadInLogs")
	public static boolean ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS;
	
	@Setting(name = "ScheduledThreadCorePoolSizeForAI")
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_AI;
	
	@Setting(name = "ScheduledThreadCorePoolSizeForEffects")
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_EFFECTS;
	
	@Setting(name = "ScheduledThreadCorePoolSizeForEvents")
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_EVENTS;
	
	@Setting(name = "ScheduledThreadCorePoolSizeForGeneral")
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_GENERAL;
	
	@Setting(name = "ThreadCorePoolSizeForEvents")
	public static int THREAD_CORE_POOL_SIZE_EVENT;
	
	@Setting(name = "ThreadCorePoolSizeForGeneral")
	public static int THREAD_CORE_POOL_SIZE_GENERAL;
	
	@Setting(name = "ThreadCorePoolSizeForGeneralPackets")
	public static int THREAD_CORE_POOL_SIZE_GENERAL_PACKETS;
	
	@Setting(name = "ThreadCorePoolSizeForIOPackets")
	public static int THREAD_CORE_POOL_SIZE_IO_PACKETS;
	
	@Setting(name = "ClientPacketQueueSize", method = "clientpacketqueuesize")
	public static int CLIENT_PACKET_QUEUE_SIZE;
	
	@Setting(name = "ClientPacketQueueMaxBurstSize", method = "clientpacketqueuemaxburstsize")
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE;
	
	@Setting(name = "ClientPacketQueueMaxPacketsPerSecond")
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND;
	
	@Setting(name = "ClientPacketQueueMeasureInterval")
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL;
	
	@Setting(name = "ClientPacketQueueMaxAveragePacketsPerSecond")
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND;
	
	@Setting(name = "ClientPacketQueueMaxFloodsPerMin")
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN;
	
	@Setting(name = "ClientPacketQueueMaxOverflowsPerMin")
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN;
	
	@Setting(name = "ClientPacketQueueMaxUnderflowsPerMin")
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN;
	
	@Setting(name = "ClientPacketQueueMaxUnknownPerMin")
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN;
	
	@Setting(name = "DeadLockDetector")
	public static boolean DEADLOCK_DETECTOR;
	
	@Setting(name = "DeadLockCheckInterval")
	public static int DEADLOCK_CHECK_INTERVAL;
	
	@Setting(name = "RestartOnDeadlock")
	public static boolean RESTART_ON_DEADLOCK;
	
	@Setting(name = "AllowDiscardItem")
	public static boolean ALLOW_DISCARDITEM;
	
	@Setting(name = "AutoDestroyDroppedItemAfter")
	public static int AUTODESTROY_ITEM_AFTER;
	
	@Setting(name = "AutoDestroyHerbTime", increase = 1000)
	public static int HERB_AUTO_DESTROY_TIME;
	
	@Setting(name = "ListOfProtectedItems", method = "listOfprotected")
	public static List<Integer> LIST_PROTECTED_ITEMS;
	
	@Setting(name = "DatabaseCleanUp")
	public static boolean DATABASE_CLEAN_UP;
	
	@Setting(name = "ConnectionCloseTime")
	public static long CONNECTION_CLOSE_TIME;
	
	@Setting(name = "CharacterDataStoreInterval")
	public static int CHAR_STORE_INTERVAL;
	
	@Setting(name = "LazyItemsUpdate")
	public static boolean LAZY_ITEMS_UPDATE;
	
	@Setting(name = "UpdateItemsOnCharStore")
	public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
	
	@Setting(name = "DestroyPlayerDroppedItem")
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	
	@Setting(name = "DestroyEquipableItem")
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	
	@Setting(name = "SaveDroppedItem")
	public static boolean SAVE_DROPPED_ITEM;
	
	@Setting(name = "EmptyDroppedItemTableAfterLoad")
	public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	
	@Setting(name = "SaveDroppedItemInterval", increase = 60000)
	public static int SAVE_DROPPED_ITEM_INTERVAL;
	
	@Setting(name = "ClearDroppedItemTable")
	public static boolean CLEAR_DROPPED_ITEM_TABLE;
	
	@Setting(name = "AutoDeleteInvalidQuestData")
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	
	@Setting(name = "PreciseDropCalculation")
	public static boolean PRECISE_DROP_CALCULATION;
	
	@Setting(name = "MultipleItemDrop")
	public static boolean MULTIPLE_ITEM_DROP;
	
	@Setting(name = "ForceInventoryUpdate")
	public static boolean FORCE_INVENTORY_UPDATE;
	
	@Setting(name = "LazyCache")
	public static boolean LAZY_CACHE;
	
	@Setting(name = "CacheCharNames")
	public static boolean CACHE_CHAR_NAMES;
	
	@Setting(name = "MinNPCAnimation")
	public static int MIN_NPC_ANIMATION;
	
	@Setting(name = "MaxNPCAnimation")
	public static int MAX_NPC_ANIMATION;
	
	@Setting(name = "MinMonsterAnimation")
	public static int MIN_MONSTER_ANIMATION;
	
	@Setting(name = "MaxMonsterAnimation")
	public static int MAX_MONSTER_ANIMATION;
	
	@Setting(name = "EnableFallingDamage")
	public static boolean ENABLE_FALLING_DAMAGE;
	
	@Setting(name = "JapanMinigame")
	public static boolean EX_JAPAN_MINIGAME;
	
	@Setting(name = "EnableItemMall")
	public static boolean ENABLE_ITEM_MALL;
	
	@Setting(name = "GridsAlwaysOn")
	public static boolean GRIDS_ALWAYS_ON;
	
	@Setting(name = "GridNeighborTurnOnTime")
	public static int GRID_NEIGHBOR_TURNON_TIME;
	
	@Setting(name = "GridNeighborTurnOffTime")
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	
	@Setting(name = "MoveBasedKnownlist")
	public static boolean MOVE_BASED_KNOWNLIST;
	
	@Setting(name = "KnownListUpdateInterval")
	public static long KNOWNLIST_UPDATE_INTERVAL;
	
	@Setting(name = "PeaceZoneMode")
	public static int PEACE_ZONE_MODE;
	
	@Setting(name = "GlobalChat")
	public static String DEFAULT_GLOBAL_CHAT;
	
	@Setting(name = "TradeChat")
	public static String DEFAULT_TRADE_CHAT;
	
	@Setting(name = "AllowWarehouse")
	public static boolean ALLOW_WAREHOUSE;
	
	@Setting(name = "WarehouseCache")
	public static boolean WAREHOUSE_CACHE;
	
	@Setting(name = "WarehouseCacheTime")
	public static int WAREHOUSE_CACHE_TIME;
	
	@Setting(name = "AllowRefund")
	public static boolean ALLOW_REFUND;
	
	@Setting(name = "AllowMail")
	public static boolean ALLOW_MAIL;
	
	@Setting(name = "AllowAttachments")
	public static boolean ALLOW_ATTACHMENTS;
	
	@Setting(name = "AllowWear")
	public static boolean ALLOW_WEAR;
	
	@Setting(name = "WearDelay")
	public static int WEAR_DELAY;
	
	@Setting(name = "WearPrice")
	public static int WEAR_PRICE;
	
	@Setting(name = "DefaultFinishTime")
	public static int INSTANCE_FINISH_TIME;
	
	@Setting(name = "RestorePlayerInstance")
	public static boolean RESTORE_PLAYER_INSTANCE;
	
	@Setting(name = "AllowSummonInInstance")
	public static boolean ALLOW_SUMMON_IN_INSTANCE;
	
	@Setting(name = "EjectDeadPlayerTime")
	public static int EJECT_DEAD_PLAYER_TIME;
	
	@Setting(name = "AllowRace")
	public static boolean ALLOW_RACE;
	
	@Setting(name = "AllowWater")
	public static boolean ALLOW_WATER;
	
	@Setting(name = "AllowRentPet")
	public static boolean ALLOW_RENTPET;
	
	@Setting(name = "AllowFishing")
	public static boolean ALLOWFISHING;
	
	@Setting(name = "AllowBoat")
	public static boolean ALLOW_BOAT;
	
	@Setting(name = "BoatBroadcastRadius")
	public static int BOAT_BROADCAST_RADIUS;
	
	@Setting(name = "AllowCursedWeapons")
	public static boolean ALLOW_CURSED_WEAPONS;
	
	@Setting(name = "AllowPetWalkers")
	public static boolean ALLOW_PET_WALKERS;
	
	@Setting(name = "ShowServerNews")
	public static boolean SERVER_NEWS;
	
	@Setting(name = "UseChatFilter")
	public static boolean USE_SAY_FILTER;
	
	@Setting(name = "ChatFilterChars")
	public static String CHAT_FILTER_CHARS;
	
	@Setting(name = "BanChatChannels", method = "banChat", splitter = ";")
	public static int[] BAN_CHAT_CHANNELS;
	
	@Setting(name = "AltItemAuctionEnabled")
	public static boolean ALT_ITEM_AUCTION_ENABLED;
	
	@Setting(name = "AltItemAuctionExpiredAfter")
	public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
	
	@Setting(name = "AltItemAuctionTimeExtendsOnBid", increase = 1000)
	public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
	
	@Setting(name = "TimeOfAttack", minValue = 0, maxValue = 50)
	public static int FS_TIME_ATTACK;
	
	@Setting(name = "TimeOfCoolDown", minValue = 0, maxValue = 5)
	public static int FS_TIME_COOLDOWN;
	
	@Setting(name = "TimeOfEntry", minValue = 0, maxValue = 3)
	public static int FS_TIME_ENTRY;
	
	@Setting(name = "TimeOfWarmUp", minValue = 0, maxValue = 3)
	public static int FS_TIME_WARMUP;
	
	@Setting(name = "NumberOfNecessaryPartyMembers", minValue = 0, maxValue = 3)
	public static int FS_PARTY_MEMBER_COUNT;
	
	@Setting(name = "RiftMinPartySize")
	public static int RIFT_MIN_PARTY_SIZE;
	
	@Setting(name = "RiftSpawnDelay")
	public static int RIFT_SPAWN_DELAY;
	
	@Setting(name = "MaxRiftJumps")
	public static int RIFT_MAX_JUMPS;
	
	@Setting(name = "AutoJumpsDelayMin")
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	
	@Setting(name = "AutoJumpsDelayMax")
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	
	@Setting(name = "BossRoomTimeMultiply")
	public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	@Setting(name = "RecruitCost")
	public static int RIFT_ENTER_COST_RECRUIT;
	
	@Setting(name = "SoldierCost")
	public static int RIFT_ENTER_COST_SOLDIER;
	
	@Setting(name = "OfficerCost")
	public static int RIFT_ENTER_COST_OFFICER;
	
	@Setting(name = "CaptainCost")
	public static int RIFT_ENTER_COST_CAPTAIN;
	
	@Setting(name = "CommanderCost")
	public static int RIFT_ENTER_COST_COMMANDER;
	
	@Setting(name = "HeroCost")
	public static int RIFT_ENTER_COST_HERO;
	
	@Setting(name = "DefaultPunish", method = "legalAction")
	public static IllegalActionPunishmentType DEFAULT_PUNISH;
	
	@Setting(name = "DefaultPunishParam")
	public static int DEFAULT_PUNISH_PARAM;
	
	@Setting(name = "OnlyGMItemsFree")
	public static boolean ONLY_GM_ITEMS_FREE;
	
	@Setting(name = "JailIsPvp")
	public static boolean JAIL_IS_PVP;
	
	@Setting(name = "JailDisableChat")
	public static boolean JAIL_DISABLE_CHAT;
	
	@Setting(name = "JailDisableTransaction")
	public static boolean JAIL_DISABLE_TRANSACTION;
	
	@Setting(name = "CustomSpawnlistTable")
	public static boolean CUSTOM_SPAWNLIST_TABLE;
	
	@Setting(name = "SaveGmSpawnOnCustom")
	public static boolean SAVE_GMSPAWN_ON_CUSTOM;
	
	@Setting(name = "CustomNpcData")
	public static boolean CUSTOM_NPC_DATA;
	
	@Setting(name = "CustomTeleportTable")
	public static boolean CUSTOM_TELEPORT_TABLE;
	
	@Setting(name = "CustomNpcBufferTables")
	public static boolean CUSTOM_NPCBUFFER_TABLES;
	
	@Setting(name = "CustomSkillsLoad")
	public static boolean CUSTOM_SKILLS_LOAD;
	
	@Setting(name = "CustomItemsLoad")
	public static boolean CUSTOM_ITEMS_LOAD;
	
	@Setting(name = "CustomMultisellLoad")
	public static boolean CUSTOM_MULTISELL_LOAD;
	
	@Setting(name = "CustomBuyListLoad")
	public static boolean CUSTOM_BUYLIST_LOAD;
	
	@Setting(name = "AltBirthdayGift")
	public static int ALT_BIRTHDAY_GIFT;
	
	@Setting(name = "EnableBlockCheckerEvent")
	public static boolean ENABLE_BLOCK_CHECKER_EVENT;
	
	@Setting(name = "BlockCheckerMinTeamMembers", minValue = 1, maxValue = 6)
	public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	
	@Setting(name = "HBCEFairPlay")
	public static boolean HBCE_FAIR_PLAY;
	
	@Setting(name = "HellboundWithoutQuest")
	public static boolean HELLBOUND_WITHOUT_QUEST;
	
	@Setting(name = "NormalEnchantCostMultipiler")
	public static int NORMAL_ENCHANT_COST_MULTIPLIER;
	
	@Setting(name = "SafeEnchantCostMultipiler")
	public static int SAFE_ENCHANT_COST_MULTIPLIER;
	
	@Setting(name = "EnableBotReportButton")
	public static boolean BOTREPORT_ENABLE;
	
	@Setting(name = "BotReportPointsResetHour", splitter = ":")
	public static String[] BOTREPORT_RESETPOINT_HOUR;
	
	@Setting(name = "BotReportDelay", increase = 60000)
	public static long BOTREPORT_REPORT_DELAY;
	
	@Setting(name = "AllowReportsFromSameClanMembers")
	public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;
	
	public void legalAction(String value)
	{
		DEFAULT_PUNISH = IllegalActionPunishmentType.findByName(value);
	}
	
	public void clientpacketqueuesize(String value)
	{
		CLIENT_PACKET_QUEUE_SIZE = Integer.parseInt(value);
		if (CLIENT_PACKET_QUEUE_SIZE == 0)
		{
			CLIENT_PACKET_QUEUE_SIZE = MMOConfig.MMO_MAX_READ_PER_PASS + 1;
		}
	}
	
	public void clientpacketqueuemaxburstsize(String value)
	{
		CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = Integer.parseInt(value);
		if (CLIENT_PACKET_QUEUE_MAX_BURST_SIZE == 0)
		{
			CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMOConfig.MMO_MAX_READ_PER_PASS;
		}
	}
	
	public void banChat(String[] value)
	{
		BAN_CHAT_CHANNELS = new int[value.length];
		try
		{
			int i = 0;
			for (String chatId : value)
			{
				BAN_CHAT_CHANNELS[i++] = Integer.parseInt(chatId);
			}
		}
		catch (NumberFormatException nfe)
		{
			LOG.warn("Unable to load banned channels!", nfe);
		}
	}
	
	public void listOfprotected(final String value)
	{
		try
		{
			LIST_PROTECTED_ITEMS = Util.parseTemplateConfig(value, Integer.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static int getServerTypeId(String[] value)
	{
		int tType = 0;
		for (String cType : value)
		{
			switch (cType.trim().toLowerCase())
			{
				case "normal":
					tType |= 0x01;
					break;
				case "relax":
					tType |= 0x02;
					break;
				case "test":
					tType |= 0x04;
					break;
				case "nolabel":
					tType |= 0x08;
					break;
				case "restricted":
					tType |= 0x10;
					break;
				case "event":
					tType |= 0x20;
					break;
				case "free":
					tType |= 0x40;
					break;
				default:
					break;
			}
		}
		return tType;
	}
}
