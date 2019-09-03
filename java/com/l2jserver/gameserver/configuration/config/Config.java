/*
 * Copyright (C) 2004-2018 L2J Server
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.PropertiesParser;
import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.util.FloodProtectorConfig;

/**
 * This class loads all the game server related configurations from files.<br>
 * The files are usually located in config folder in server root folder.<br>
 * Each configuration has a default value (that should reflect retail behavior).
 */
public final class Config
{
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	
	// --------------------------------------------------
	// Constants
	// --------------------------------------------------
	public static final String EOL = System.lineSeparator();
	
	// --------------------------------------------------
	// L2J Property File Definitions
	// --------------------------------------------------
	public static final String CHARACTER_CONFIG_FILE = "./config/Character.properties";
	public static final String FLOOD_PROTECTOR_FILE = "./config/FloodProtector.properties";
	public static final String FORTSIEGE_CONFIGURATION_FILE = "./config/FortSiege.properties";
	public static final String GENERAL_CONFIG_FILE = "./config/General.properties";
	public static final String L2JMOD_CONFIG_FILE = "./config/L2JMods.properties";
	public static final String NPC_CONFIG_FILE = "./config/NPC.properties";
	public static final String RATES_CONFIG_FILE = "./config/Rates.properties";
	public static final String SIEGE_CONFIGURATION_FILE = "./config/Siege.properties";
	public static final String TOP_CONFIG_FILE = "config/services/Tops.properties";
	
	// --------------------------------------------------
	// L2J Variable Definitions
	// --------------------------------------------------
	public static boolean ALT_GAME_DELEVEL;
	public static boolean DECREASE_SKILL_LEVEL;
	public static double ALT_WEIGHT_LIMIT;
	public static int RUN_SPD_BOOST;
	public static int DEATH_PENALTY_CHANCE;
	public static double RESPAWN_RESTORE_CP;
	public static double RESPAWN_RESTORE_HP;
	public static double RESPAWN_RESTORE_MP;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static Map<Integer, Integer> SKILL_DURATION_LIST;
	public static boolean ENABLE_MODIFY_SKILL_REUSE;
	public static Map<Integer, Integer> SKILL_REUSE_LIST;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean AUTO_LEARN_FS_SKILLS;
	public static boolean AUTO_LOOT_HERBS;
	public static byte BUFFS_MAX_AMOUNT;
	public static byte TRIGGERED_BUFFS_MAX_AMOUNT;
	public static byte DANCES_MAX_AMOUNT;
	public static boolean DANCE_CANCEL_BUFF;
	public static boolean DANCE_CONSUME_ADDITIONAL_MP;
	public static boolean ALT_STORE_DANCES;
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	public static boolean ALT_GAME_CANCEL_BOW;
	public static boolean ALT_GAME_CANCEL_CAST;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static boolean STORE_SKILL_COOLTIME;
	public static boolean SUBCLASS_STORE_SKILL_COOLTIME;
	public static boolean SUMMON_STORE_SKILL_COOLTIME;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static long EFFECT_TICK_RATIO;
	public static boolean ALLOW_CLASS_MASTERS;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static boolean ALTERNATE_CLASS_MASTER;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean ALT_GAME_SKILL_LEARN;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	public static boolean ALLOW_TRANSFORM_WITHOUT_QUEST;
	public static int FEE_DELETE_TRANSFER_SKILLS;
	public static int FEE_DELETE_SUBCLASS_SKILLS;
	public static boolean RESTORE_SERVITOR_ON_RECONNECT;
	public static boolean RESTORE_PET_ON_RECONNECT;
	public static double MAX_BONUS_EXP;
	public static double MAX_BONUS_SP;
	public static int MAX_RUN_SPEED;
	public static int MAX_PCRIT_RATE;
	public static int MAX_MCRIT_RATE;
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	public static int MAX_EVASION;
	public static int MIN_ABNORMAL_STATE_SUCCESS_RATE;
	public static int MAX_ABNORMAL_STATE_SUCCESS_RATE;
	public static int MAX_PLAYER_LEVEL;
	public static int MAX_PET_LEVEL;
	public static byte MAX_SUBCLASS;
	public static int BASE_SUBCLASS_LEVEL;
	public static int MAX_SUBCLASS_LEVEL;
	public static int MAX_PVTSTORESELL_SLOTS_DWARF;
	public static int MAX_PVTSTORESELL_SLOTS_OTHER;
	public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
	public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_GM;
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int ALT_FREIGHT_SLOTS;
	public static int ALT_FREIGHT_PRICE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	public static int MAX_PERSONAL_FAME_POINTS;
	public static int FORTRESS_ZONE_FAME_TASK_FREQUENCY;
	public static int FORTRESS_ZONE_FAME_AQUIRE_POINTS;
	public static int CASTLE_ZONE_FAME_TASK_FREQUENCY;
	public static int CASTLE_ZONE_FAME_AQUIRE_POINTS;
	public static boolean FAME_FOR_DEAD_PLAYERS;
	public static boolean IS_CRAFTING_ENABLED;
	public static boolean CRAFT_MASTERWORK;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_GAME_CREATION;
	public static double ALT_GAME_CREATION_SPEED;
	public static double ALT_GAME_CREATION_XP_RATE;
	public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
	public static double ALT_GAME_CREATION_SP_RATE;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	public static int ALT_CLAN_LEADER_DATE_CHANGE;
	public static String ALT_CLAN_LEADER_HOUR_CHANGE;
	public static boolean ALT_CLAN_LEADER_INSTANT_ACTIVATION;
	public static int ALT_CLAN_JOIN_DAYS;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	public static boolean REMOVE_CASTLE_CIRCLETS;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	public static boolean ALT_LEAVE_PARTY_LEADER;
	public static boolean INITIAL_EQUIPMENT_EVENT;
	public static long STARTING_ADENA;
	public static int STARTING_LEVEL;
	public static int STARTING_SP;
	public static long MAX_ADENA;
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_RAIDS;
	public static int LOOT_RAIDS_PRIVILEGE_INTERVAL;
	public static int LOOT_RAIDS_PRIVILEGE_CC_SIZE;
	public static int UNSTUCK_INTERVAL;
	public static int TELEPORT_WATCHDOG_TIMEOUT;
	public static int PLAYER_SPAWN_PROTECTION;
	public static List<Integer> SPAWN_PROTECTION_ALLOWED_ITEMS;
	public static int PLAYER_TELEPORT_PROTECTION;
	public static boolean RANDOM_RESPAWN_IN_TOWN_ENABLED;
	public static boolean OFFSET_ON_TELEPORT_ENABLED;
	public static int MAX_OFFSET_ON_TELEPORT;
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	public static boolean ALT_GAME_FREE_TELEPORT;
	public static int DELETE_DAYS;
	public static float ALT_GAME_EXPONENT_XP;
	public static float ALT_GAME_EXPONENT_SP;
	public static String PARTY_XP_CUTOFF_METHOD;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static int[][] PARTY_XP_CUTOFF_GAPS;
	public static int[] PARTY_XP_CUTOFF_GAP_PERCENTS;
	public static boolean DISABLE_TUTORIAL;
	public static boolean EXPERTISE_PENALTY;
	public static boolean STORE_RECIPE_SHOPLIST;
	public static boolean STORE_UI_SETTINGS;
	public static Set<String> FORBIDDEN_NAMES;
	public static boolean SILENCE_MODE_EXCLUDE;
	public static boolean ALT_VALIDATE_TRIGGER_SKILLS;
	
	// --------------------------------------------------
	// General Settings
	// --------------------------------------------------
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	public static boolean SERVER_LIST_BRACKET;
	public static int SERVER_LIST_TYPE;
	public static int SERVER_LIST_AGE;
	public static boolean SERVER_GMONLY;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	public static boolean GM_STARTUP_DIET_MODE;
	public static boolean GM_ITEM_RESTRICTION;
	public static boolean GM_SKILL_RESTRICTION;
	public static boolean GM_TRADE_RESTRICTED_ITEMS;
	public static boolean GM_RESTART_FIGHTING;
	public static boolean GM_ANNOUNCER_NAME;
	public static boolean GM_CRITANNOUNCER_NAME;
	public static boolean GM_GIVE_SPECIAL_SKILLS;
	public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
	public static boolean GAMEGUARD_ENFORCE;
	public static boolean GAMEGUARD_PROHIBITACTION;
	public static boolean LOG_CHAT;
	public static boolean LOG_AUTO_ANNOUNCEMENTS;
	public static boolean LOG_ITEMS;
	public static boolean LOG_ITEMS_SMALL_LOG;
	public static boolean LOG_ITEM_ENCHANTS;
	public static boolean LOG_SKILL_ENCHANTS;
	public static boolean GMAUDIT;
	public static boolean SKILL_CHECK_ENABLE;
	public static boolean SKILL_CHECK_REMOVE;
	public static boolean SKILL_CHECK_GM;
	public static boolean DEBUG;
	public static boolean DEBUG_INSTANCES;
	public static boolean HTML_ACTION_CACHE_DEBUG;
	public static boolean PACKET_HANDLER_DEBUG;
	public static boolean DEVELOPER;
	public static boolean ALT_DEV_NO_HANDLERS;
	public static boolean ALT_DEV_NO_QUESTS;
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS;
	public static boolean ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS;
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_AI;
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_EFFECTS;
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_EVENTS;
	public static int SCHEDULED_THREAD_CORE_POOL_SIZE_GENERAL;
	public static int THREAD_CORE_POOL_SIZE_EVENT;
	public static int THREAD_CORE_POOL_SIZE_GENERAL;
	public static int THREAD_CORE_POOL_SIZE_GENERAL_PACKETS;
	public static int THREAD_CORE_POOL_SIZE_IO_PACKETS;
	public static int CLIENT_PACKET_QUEUE_SIZE;
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE;
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND;
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL;
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND;
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN;
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	public static boolean ALLOW_DISCARDITEM;
	public static int AUTODESTROY_ITEM_AFTER;
	public static int HERB_AUTO_DESTROY_TIME;
	public static List<Integer> LIST_PROTECTED_ITEMS;
	public static boolean DATABASE_CLEAN_UP;
	public static long CONNECTION_CLOSE_TIME;
	public static int CHAR_STORE_INTERVAL;
	public static boolean LAZY_ITEMS_UPDATE;
	public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	public static boolean SAVE_DROPPED_ITEM;
	public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	public static int SAVE_DROPPED_ITEM_INTERVAL;
	public static boolean CLEAR_DROPPED_ITEM_TABLE;
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	public static boolean PRECISE_DROP_CALCULATION;
	public static boolean MULTIPLE_ITEM_DROP;
	public static boolean FORCE_INVENTORY_UPDATE;
	public static boolean LAZY_CACHE;
	public static boolean CACHE_CHAR_NAMES;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	public static boolean ENABLE_FALLING_DAMAGE;
	public static boolean GRIDS_ALWAYS_ON;
	public static int GRID_NEIGHBOR_TURNON_TIME;
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	public static boolean MOVE_BASED_KNOWNLIST;
	public static long KNOWNLIST_UPDATE_INTERVAL;
	public static int PEACE_ZONE_MODE;
	public static String DEFAULT_GLOBAL_CHAT;
	public static String DEFAULT_TRADE_CHAT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean WAREHOUSE_CACHE;
	public static int WAREHOUSE_CACHE_TIME;
	public static boolean ALLOW_REFUND;
	public static boolean ALLOW_MAIL;
	public static boolean ALLOW_ATTACHMENTS;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static int INSTANCE_FINISH_TIME;
	public static boolean RESTORE_PLAYER_INSTANCE;
	public static boolean ALLOW_SUMMON_IN_INSTANCE;
	public static int EJECT_DEAD_PLAYER_TIME;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_RACE;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_RENTPET;
	public static boolean ALLOWFISHING;
	public static boolean ALLOW_BOAT;
	public static int BOAT_BROADCAST_RADIUS;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ALLOW_PET_WALKERS;
	public static boolean SERVER_NEWS;
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static int[] BAN_CHAT_CHANNELS;
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static int ALT_OLY_MAX_BUFFS;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int ALT_OLY_TEAMS;
	public static int ALT_OLY_REG_DISPLAY;
	public static int[][] ALT_OLY_CLASSED_REWARD;
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	public static int[][] ALT_OLY_TEAM_REWARD;
	public static int ALT_OLY_COMP_RITEM;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_TEAM;
	public static boolean ALT_OLY_LOG_FIGHTS;
	public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS;
	public static int ALT_OLY_ENCHANT_LIMIT;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_MIN;
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	public static long ALT_LOTTERY_PRIZE;
	public static long ALT_LOTTERY_TICKET_PRICE;
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	public static long ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	public static boolean ALT_ITEM_AUCTION_ENABLED;
	public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
	public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static IllegalActionPunishmentType DEFAULT_PUNISH;
	public static int DEFAULT_PUNISH_PARAM;
	public static boolean ONLY_GM_ITEMS_FREE;
	public static boolean JAIL_IS_PVP;
	public static boolean JAIL_DISABLE_CHAT;
	public static boolean JAIL_DISABLE_TRANSACTION;
	public static boolean CUSTOM_SPAWNLIST_TABLE;
	public static boolean SAVE_GMSPAWN_ON_CUSTOM;
	public static boolean CUSTOM_NPC_DATA;
	public static boolean CUSTOM_TELEPORT_TABLE;
	public static boolean CUSTOM_NPCBUFFER_TABLES;
	public static boolean CUSTOM_SKILLS_LOAD;
	public static boolean CUSTOM_ITEMS_LOAD;
	public static boolean CUSTOM_MULTISELL_LOAD;
	public static boolean CUSTOM_BUYLIST_LOAD;
	public static int ALT_BIRTHDAY_GIFT;
	public static String ALT_BIRTHDAY_MAIL_SUBJECT;
	public static String ALT_BIRTHDAY_MAIL_TEXT;
	public static boolean ENABLE_BLOCK_CHECKER_EVENT;
	public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	public static boolean HBCE_FAIR_PLAY;
	public static boolean HELLBOUND_WITHOUT_QUEST;
	public static int PLAYER_MOVEMENT_BLOCK_TIME;
	public static int NORMAL_ENCHANT_COST_MULTIPLIER;
	public static int SAFE_ENCHANT_COST_MULTIPLIER;
	public static boolean BOTREPORT_ENABLE;
	public static boolean ENABLE_ITEM_MALL;
	public static boolean EX_JAPAN_MINIGAME;
	public static String[] BOTREPORT_RESETPOINT_HOUR;
	public static long BOTREPORT_REPORT_DELAY;
	public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;
	
	// --------------------------------------------------
	// FloodProtector Settings
	// --------------------------------------------------
	public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
	public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANOR;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SENDMAIL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_AUCTION;
	// --------------------------------------------------
	// L2JMods Settings
	// --------------------------------------------------
	public static boolean TVT_EVENT_ENABLED;
	public static boolean TVT_EVENT_IN_INSTANCE;
	public static String TVT_EVENT_INSTANCE_FILE;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
	public static int TVT_EVENT_MAX_PARTICIPANTS_PER_IP;
	public static boolean TVT_ALLOW_VOICED_COMMAND;
	
	public static boolean L2JMOD_ANTIFEED_ENABLE;
	public static boolean L2JMOD_ANTIFEED_DUALBOX;
	public static boolean L2JMOD_ANTIFEED_DISCONNECTED_AS_DUALBOX;
	public static int L2JMOD_ANTIFEED_INTERVAL;
	public static boolean L2JMOD_MULTILANG_ENABLE;
	public static List<String> L2JMOD_MULTILANG_ALLOWED = new ArrayList<>();
	public static String L2JMOD_MULTILANG_DEFAULT;
	public static boolean L2JMOD_MULTILANG_VOICED_ALLOW;
	public static boolean L2JMOD_MULTILANG_SM_ENABLE;
	public static List<String> L2JMOD_MULTILANG_SM_ALLOWED = new ArrayList<>();
	public static boolean L2JMOD_MULTILANG_NS_ENABLE;
	public static List<String> L2JMOD_MULTILANG_NS_ALLOWED = new ArrayList<>();
	public static boolean L2WALKER_PROTECTION;
	public static int L2JMOD_DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
	public static int L2JMOD_DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	public static int L2JMOD_DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
	public static Map<Integer, Integer> L2JMOD_DUALBOX_CHECK_WHITELIST;
	
	// RWHO system (off emulation)
	public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
	public static double SENDSTATUS_TRADE_MOD;
	public static boolean SHOW_OFFLINE_MODE_IN_ONLINE;
	public static boolean RWHO_LOG;
	public static int RWHO_FORCE_INC;
	public static int RWHO_KEEP_STAT;
	public static int RWHO_MAX_ONLINE;
	public static boolean RWHO_SEND_TRASH;
	public static int RWHO_ONLINE_INCREMENT;
	public static float RWHO_PRIV_STORE_FACTOR;
	public static int RWHO_ARRAY[] = new int[13];
	
	// --------------------------------------------------
	// NPC Settings
	// --------------------------------------------------
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean ALT_ATTACKABLE_NPCS;
	public static boolean ALT_GAME_VIEWNPC;
	public static int MAX_DRIFT_RANGE;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean DEEPBLUE_DROP_RULES_RAID;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_CREST_WITHOUT_QUEST;
	public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
	public static int MIN_NPC_LVL_DMG_PENALTY;
	public static Map<Integer, Float> NPC_DMG_PENALTY;
	public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
	public static int MIN_NPC_LVL_MAGIC_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
	public static int DECAY_TIME_TASK;
	public static int DEFAULT_CORPSE_TIME;
	public static int SPOILED_CORPSE_EXTEND_TIME;
	public static int CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY;
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static boolean ALLOW_WYVERN_UPGRADER;
	public static List<Integer> LIST_PET_RENT_NPC;
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_PDEFENCE_MULTIPLIER;
	public static double RAID_MDEFENCE_MULTIPLIER;
	public static double RAID_PATTACK_MULTIPLIER;
	public static double RAID_MATTACK_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
	public static float RAID_MIN_RESPAWN_MULTIPLIER;
	public static float RAID_MAX_RESPAWN_MULTIPLIER;
	public static boolean RAID_DISABLE_CURSE;
	public static int RAID_CHAOS_TIME;
	public static int GRAND_CHAOS_TIME;
	public static int MINION_CHAOS_TIME;
	public static int INVENTORY_MAXIMUM_PET;
	public static double PET_HP_REGEN_MULTIPLIER;
	public static double PET_MP_REGEN_MULTIPLIER;
	public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
	public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
	public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
	public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
	public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
	public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
	
	// --------------------------------------------------
	// Rate Settings
	// --------------------------------------------------
	public static float RATE_XP;
	public static float RATE_SP;
	public static float RATE_PARTY_XP;
	public static float RATE_PARTY_SP;
	public static float RATE_HB_TRUST_INCREASE;
	public static float RATE_HB_TRUST_DECREASE;
	public static float RATE_EXTRACTABLE;
	public static int RATE_DROP_MANOR;
	public static float RATE_QUEST_DROP;
	public static float RATE_QUEST_REWARD;
	public static float RATE_QUEST_REWARD_XP;
	public static float RATE_QUEST_REWARD_SP;
	public static float RATE_QUEST_REWARD_ADENA;
	public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
	public static float RATE_QUEST_REWARD_POTION;
	public static float RATE_QUEST_REWARD_SCROLL;
	public static float RATE_QUEST_REWARD_RECIPE;
	public static float RATE_QUEST_REWARD_MATERIAL;
	public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_CORPSE_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_RAID_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
	public static float RATE_CORPSE_DROP_CHANCE_MULTIPLIER;
	public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
	public static float RATE_RAID_DROP_CHANCE_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_AMOUNT_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_CHANCE_MULTIPLIER;
	public static float RATE_KARMA_LOST;
	public static float RATE_KARMA_EXP_LOST;
	public static float RATE_SIEGE_GUARDS_PRICE;
	public static float RATE_DROP_COMMON_HERBS;
	public static float RATE_DROP_HP_HERBS;
	public static float RATE_DROP_MP_HERBS;
	public static float RATE_DROP_SPECIAL_HERBS;
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static float PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static float SINEATER_XP_RATE;
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	// --------------------------------------------------
	// Vitality Settings
	// --------------------------------------------------
	public static boolean ENABLE_VITALITY;
	public static boolean RECOVER_VITALITY_ON_RECONNECT;
	public static boolean ENABLE_DROP_VITALITY_HERBS;
	public static float RATE_VITALITY_LEVEL_1;
	public static float RATE_VITALITY_LEVEL_2;
	public static float RATE_VITALITY_LEVEL_3;
	public static float RATE_VITALITY_LEVEL_4;
	public static float RATE_DROP_VITALITY_HERBS;
	public static float RATE_RECOVERY_VITALITY_PEACE_ZONE;
	public static float RATE_VITALITY_LOST;
	public static float RATE_VITALITY_GAIN;
	public static float RATE_RECOVERY_ON_RECONNECT;
	public static int STARTING_VITALITY_POINTS;
	
	// --------------------------------------------------
	// No classification assigned to the following yet
	// --------------------------------------------------
	public static int MAX_ITEM_IN_PACKET;
	public static boolean CHECK_KNOWN;
	
	public static double ENCHANT_CHANCE_ELEMENT_STONE;
	public static double ENCHANT_CHANCE_ELEMENT_CRYSTAL;
	public static double ENCHANT_CHANCE_ELEMENT_JEWEL;
	public static double ENCHANT_CHANCE_ELEMENT_ENERGY;
	public static int[] ENCHANT_BLACKLIST;
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	public static int AUGMENTATION_ACC_SKILL_CHANCE;
	public static boolean RETAIL_LIKE_AUGMENTATION;
	public static int[] RETAIL_LIKE_AUGMENTATION_NG_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_MID_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_TOP_CHANCE;
	public static boolean RETAIL_LIKE_AUGMENTATION_ACCESSORY;
	public static int[] AUGMENTATION_BLACKLIST;
	public static boolean ALT_ALLOW_AUGMENT_PVP_ITEMS;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	
	public static boolean L2_TOP_MANAGER_ENABLED;
	public static String L2_TOP_WEB_ADDRESS;
	public static String L2_TOP_SMS_ADDRESS;
	public static int L2_TOP_REWARD_ID;
	public static int L2_TOP_REWARD_COUNT;
	public static String L2_TOP_SERVER_PREFIX;
	public static boolean MMO_TOP_MANAGER_ENABLED;
	public static String MMO_TOP_WEB_ADDRESS;
	public static int MMO_TOP_REWARD_ID;
	public static int MMO_TOP_REWARD_COUNT;
	public static int TOP_MANAGER_INTERVAL;
	public static String TOP_SERVER_ADDRESS;
	public static int TOP_SAVE_DAYS;
	
	/**
	 * This class initializes all global variables for configuration.<br>
	 * If the key doesn't appear in properties file, a default value is set by this class. {@link #CONFIGURATION_FILE} (properties file) for configuring your server.
	 */
	public static void load()
	{
		FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
		FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
		FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
		FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
		FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
		FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
		FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
		FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
		FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
		FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
		FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
		FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
		FLOOD_PROTECTOR_MANOR = new FloodProtectorConfig("ManorFloodProtector");
		FLOOD_PROTECTOR_SENDMAIL = new FloodProtectorConfig("SendMailFloodProtector");
		FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
		FLOOD_PROTECTOR_ITEM_AUCTION = new FloodProtectorConfig("ItemAuctionFloodProtector");
		
		// Load Character L2Properties file (if exists)
		final PropertiesParser character = new PropertiesParser(CHARACTER_CONFIG_FILE);
		
		ALT_GAME_DELEVEL = character.getBoolean("Delevel", true);
		DECREASE_SKILL_LEVEL = character.getBoolean("DecreaseSkillOnDelevel", true);
		ALT_WEIGHT_LIMIT = character.getDouble("AltWeightLimit", 1);
		RUN_SPD_BOOST = character.getInt("RunSpeedBoost", 0);
		DEATH_PENALTY_CHANCE = character.getInt("DeathPenaltyChance", 20);
		RESPAWN_RESTORE_CP = character.getDouble("RespawnRestoreCP", 0) / 100;
		RESPAWN_RESTORE_HP = character.getDouble("RespawnRestoreHP", 65) / 100;
		RESPAWN_RESTORE_MP = character.getDouble("RespawnRestoreMP", 0) / 100;
		HP_REGEN_MULTIPLIER = character.getDouble("HpRegenMultiplier", 100) / 100;
		MP_REGEN_MULTIPLIER = character.getDouble("MpRegenMultiplier", 100) / 100;
		CP_REGEN_MULTIPLIER = character.getDouble("CpRegenMultiplier", 100) / 100;
		ENABLE_MODIFY_SKILL_DURATION = character.getBoolean("EnableModifySkillDuration", false);
		
		// Create Map only if enabled
		if (ENABLE_MODIFY_SKILL_DURATION)
		{
			String[] propertySplit = character.getString("SkillDurationList", "").split(";");
			SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					LOG.warn("[SkillDurationList]: invalid config property -> SkillDurationList {}", skill);
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							LOG.warn("[SkillDurationList]: invalid config property -> SkillList {}", skill);
						}
					}
				}
			}
		}
		ENABLE_MODIFY_SKILL_REUSE = character.getBoolean("EnableModifySkillReuse", false);
		// Create Map only if enabled
		if (ENABLE_MODIFY_SKILL_REUSE)
		{
			String[] propertySplit = character.getString("SkillReuseList", "").split(";");
			SKILL_REUSE_LIST = new HashMap<>(propertySplit.length);
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					LOG.warn("[SkillReuseList]: invalid config property -> SkillReuseList {}", skill);
				}
				else
				{
					try
					{
						SKILL_REUSE_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!skill.isEmpty())
						{
							LOG.warn("[SkillReuseList]: invalid config property -> SkillList {}", skill);
						}
					}
				}
			}
		}
		
		AUTO_LEARN_SKILLS = character.getBoolean("AutoLearnSkills", false);
		AUTO_LEARN_FS_SKILLS = character.getBoolean("AutoLearnForgottenScrollSkills", false);
		AUTO_LOOT_HERBS = character.getBoolean("AutoLootHerbs", false);
		BUFFS_MAX_AMOUNT = character.getByte("MaxBuffAmount", (byte) 20);
		TRIGGERED_BUFFS_MAX_AMOUNT = character.getByte("MaxTriggeredBuffAmount", (byte) 12);
		DANCES_MAX_AMOUNT = character.getByte("MaxDanceAmount", (byte) 12);
		DANCE_CANCEL_BUFF = character.getBoolean("DanceCancelBuff", false);
		DANCE_CONSUME_ADDITIONAL_MP = character.getBoolean("DanceConsumeAdditionalMP", true);
		ALT_STORE_DANCES = character.getBoolean("AltStoreDances", false);
		AUTO_LEARN_DIVINE_INSPIRATION = character.getBoolean("AutoLearnDivineInspiration", false);
		ALT_GAME_CANCEL_BOW = character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
		ALT_GAME_CANCEL_CAST = character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
		ALT_GAME_MAGICFAILURES = character.getBoolean("MagicFailures", true);
		PLAYER_FAKEDEATH_UP_PROTECTION = character.getInt("PlayerFakeDeathUpProtection", 0);
		STORE_SKILL_COOLTIME = character.getBoolean("StoreSkillCooltime", true);
		SUBCLASS_STORE_SKILL_COOLTIME = character.getBoolean("SubclassStoreSkillCooltime", false);
		SUMMON_STORE_SKILL_COOLTIME = character.getBoolean("SummonStoreSkillCooltime", true);
		ALT_GAME_SHIELD_BLOCKS = character.getBoolean("AltShieldBlocks", false);
		ALT_PERFECT_SHLD_BLOCK = character.getInt("AltPerfectShieldBlockRate", 10);
		EFFECT_TICK_RATIO = character.getLong("EffectTickRatio", 666);
		ALLOW_CLASS_MASTERS = character.getBoolean("AllowClassMasters", false);
		ALLOW_ENTIRE_TREE = character.getBoolean("AllowEntireTree", false);
		ALTERNATE_CLASS_MASTER = character.getBoolean("AlternateClassMaster", false);
		if (ALLOW_CLASS_MASTERS || ALTERNATE_CLASS_MASTER)
		{
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(character.getString("ConfigClassMaster", ""));
		}
		LIFE_CRYSTAL_NEEDED = character.getBoolean("LifeCrystalNeeded", true);
		ES_SP_BOOK_NEEDED = character.getBoolean("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = character.getBoolean("DivineInspirationSpBookNeeded", true);
		ALT_GAME_SKILL_LEARN = character.getBoolean("AltGameSkillLearn", false);
		ALT_GAME_SUBCLASS_WITHOUT_QUESTS = character.getBoolean("AltSubClassWithoutQuests", false);
		ALT_GAME_SUBCLASS_EVERYWHERE = character.getBoolean("AltSubclassEverywhere", false);
		RESTORE_SERVITOR_ON_RECONNECT = character.getBoolean("RestoreServitorOnReconnect", true);
		RESTORE_PET_ON_RECONNECT = character.getBoolean("RestorePetOnReconnect", true);
		ALLOW_TRANSFORM_WITHOUT_QUEST = character.getBoolean("AltTransformationWithoutQuest", false);
		FEE_DELETE_TRANSFER_SKILLS = character.getInt("FeeDeleteTransferSkills", 10000000);
		FEE_DELETE_SUBCLASS_SKILLS = character.getInt("FeeDeleteSubClassSkills", 10000000);
		ENABLE_VITALITY = character.getBoolean("EnableVitality", true);
		RECOVER_VITALITY_ON_RECONNECT = character.getBoolean("RecoverVitalityOnReconnect", true);
		STARTING_VITALITY_POINTS = character.getInt("StartingVitalityPoints", 20000);
		MAX_BONUS_EXP = character.getDouble("MaxExpBonus", 3.5);
		MAX_BONUS_SP = character.getDouble("MaxSpBonus", 3.5);
		MAX_RUN_SPEED = character.getInt("MaxRunSpeed", 250);
		MAX_PCRIT_RATE = character.getInt("MaxPCritRate", 500);
		MAX_MCRIT_RATE = character.getInt("MaxMCritRate", 200);
		MAX_PATK_SPEED = character.getInt("MaxPAtkSpeed", 1500);
		MAX_MATK_SPEED = character.getInt("MaxMAtkSpeed", 1999);
		MAX_EVASION = character.getInt("MaxEvasion", 250);
		MIN_ABNORMAL_STATE_SUCCESS_RATE = character.getInt("MinAbnormalStateSuccessRate", 10);
		MAX_ABNORMAL_STATE_SUCCESS_RATE = character.getInt("MaxAbnormalStateSuccessRate", 90);
		MAX_PLAYER_LEVEL = character.getInt("MaxPlayerLevel", 85);
		MAX_PET_LEVEL = character.getInt("MaxPetLevel", 86);
		MAX_SUBCLASS = character.getByte("MaxSubclass", (byte) 3);
		BASE_SUBCLASS_LEVEL = character.getInt("BaseSubclassLevel", 40);
		MAX_SUBCLASS_LEVEL = character.getInt("MaxSubclassLevel", 80);
		MAX_PVTSTORESELL_SLOTS_DWARF = character.getInt("MaxPvtStoreSellSlotsDwarf", 4);
		MAX_PVTSTORESELL_SLOTS_OTHER = character.getInt("MaxPvtStoreSellSlotsOther", 3);
		MAX_PVTSTOREBUY_SLOTS_DWARF = character.getInt("MaxPvtStoreBuySlotsDwarf", 5);
		MAX_PVTSTOREBUY_SLOTS_OTHER = character.getInt("MaxPvtStoreBuySlotsOther", 4);
		INVENTORY_MAXIMUM_NO_DWARF = character.getInt("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = character.getInt("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_GM = character.getInt("MaximumSlotsForGMPlayer", 250);
		INVENTORY_MAXIMUM_QUEST_ITEMS = character.getInt("MaximumSlotsForQuestItems", 100);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
		WAREHOUSE_SLOTS_DWARF = character.getInt("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_NO_DWARF = character.getInt("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_CLAN = character.getInt("MaximumWarehouseSlotsForClan", 150);
		ALT_FREIGHT_SLOTS = character.getInt("MaximumFreightSlots", 200);
		ALT_FREIGHT_PRICE = character.getInt("FreightPrice", 1000);
		ENCHANT_CHANCE_ELEMENT_STONE = character.getDouble("EnchantChanceElementStone", 50);
		ENCHANT_CHANCE_ELEMENT_CRYSTAL = character.getDouble("EnchantChanceElementCrystal", 30);
		ENCHANT_CHANCE_ELEMENT_JEWEL = character.getDouble("EnchantChanceElementJewel", 20);
		ENCHANT_CHANCE_ELEMENT_ENERGY = character.getDouble("EnchantChanceElementEnergy", 10);
		String[] notenchantable = character.getString("EnchantBlackList", "7816,7817,7818,7819,7820,7821,7822,7823,7824,7825,7826,7827,7828,7829,7830,7831,13293,13294,13296").split(",");
		ENCHANT_BLACKLIST = new int[notenchantable.length];
		for (int i = 0; i < notenchantable.length; i++)
		{
			ENCHANT_BLACKLIST[i] = Integer.parseInt(notenchantable[i]);
		}
		Arrays.sort(ENCHANT_BLACKLIST);
		
		AUGMENTATION_NG_SKILL_CHANCE = character.getInt("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = character.getInt("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = character.getInt("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = character.getInt("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = character.getInt("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = character.getInt("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = character.getInt("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = character.getInt("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = character.getInt("AugmentationBaseStatChance", 1);
		AUGMENTATION_ACC_SKILL_CHANCE = character.getInt("AugmentationAccSkillChance", 0);
		
		RETAIL_LIKE_AUGMENTATION = character.getBoolean("RetailLikeAugmentation", true);
		String[] array = character.getString("RetailLikeAugmentationNoGradeChance", "55,35,7,3").split(",");
		RETAIL_LIKE_AUGMENTATION_NG_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_NG_CHANCE[i] = Integer.parseInt(array[i]);
		}
		array = character.getString("RetailLikeAugmentationMidGradeChance", "55,35,7,3").split(",");
		RETAIL_LIKE_AUGMENTATION_MID_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_MID_CHANCE[i] = Integer.parseInt(array[i]);
		}
		array = character.getString("RetailLikeAugmentationHighGradeChance", "55,35,7,3").split(",");
		RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE[i] = Integer.parseInt(array[i]);
		}
		array = character.getString("RetailLikeAugmentationTopGradeChance", "55,35,7,3").split(",");
		RETAIL_LIKE_AUGMENTATION_TOP_CHANCE = new int[array.length];
		for (int i = 0; i < 4; i++)
		{
			RETAIL_LIKE_AUGMENTATION_TOP_CHANCE[i] = Integer.parseInt(array[i]);
		}
		RETAIL_LIKE_AUGMENTATION_ACCESSORY = character.getBoolean("RetailLikeAugmentationAccessory", true);
		
		array = character.getString("AugmentationBlackList", "6656,6657,6658,6659,6660,6661,6662,8191,10170,10314,13740,13741,13742,13743,13744,13745,13746,13747,13748,14592,14593,14594,14595,14596,14597,14598,14599,14600,14664,14665,14666,14667,14668,14669,14670,14671,14672,14801,14802,14803,14804,14805,14806,14807,14808,14809,15282,15283,15284,15285,15286,15287,15288,15289,15290,15291,15292,15293,15294,15295,15296,15297,15298,15299,16025,16026,21712,22173,22174,22175").split(",");
		AUGMENTATION_BLACKLIST = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
		{
			AUGMENTATION_BLACKLIST[i] = Integer.parseInt(array[i]);
		}
		
		Arrays.sort(AUGMENTATION_BLACKLIST);
		ALT_ALLOW_AUGMENT_PVP_ITEMS = character.getBoolean("AltAllowAugmentPvPItems", false);
		ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = character.getBoolean("AltKarmaPlayerCanBeKilledInPeaceZone", false);
		ALT_GAME_KARMA_PLAYER_CAN_SHOP = character.getBoolean("AltKarmaPlayerCanShop", true);
		ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = character.getBoolean("AltKarmaPlayerCanTeleport", true);
		ALT_GAME_KARMA_PLAYER_CAN_USE_GK = character.getBoolean("AltKarmaPlayerCanUseGK", false);
		ALT_GAME_KARMA_PLAYER_CAN_TRADE = character.getBoolean("AltKarmaPlayerCanTrade", true);
		ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = character.getBoolean("AltKarmaPlayerCanUseWareHouse", true);
		MAX_PERSONAL_FAME_POINTS = character.getInt("MaxPersonalFamePoints", 100000);
		FORTRESS_ZONE_FAME_TASK_FREQUENCY = character.getInt("FortressZoneFameTaskFrequency", 300);
		FORTRESS_ZONE_FAME_AQUIRE_POINTS = character.getInt("FortressZoneFameAquirePoints", 31);
		CASTLE_ZONE_FAME_TASK_FREQUENCY = character.getInt("CastleZoneFameTaskFrequency", 300);
		CASTLE_ZONE_FAME_AQUIRE_POINTS = character.getInt("CastleZoneFameAquirePoints", 125);
		FAME_FOR_DEAD_PLAYERS = character.getBoolean("FameForDeadPlayers", true);
		IS_CRAFTING_ENABLED = character.getBoolean("CraftingEnabled", true);
		CRAFT_MASTERWORK = character.getBoolean("CraftMasterwork", true);
		DWARF_RECIPE_LIMIT = character.getInt("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = character.getInt("CommonRecipeLimit", 50);
		ALT_GAME_CREATION = character.getBoolean("AltGameCreation", false);
		ALT_GAME_CREATION_SPEED = character.getDouble("AltGameCreationSpeed", 1);
		ALT_GAME_CREATION_XP_RATE = character.getDouble("AltGameCreationXpRate", 1);
		ALT_GAME_CREATION_SP_RATE = character.getDouble("AltGameCreationSpRate", 1);
		ALT_GAME_CREATION_RARE_XPSP_RATE = character.getDouble("AltGameCreationRareXpSpRate", 2);
		ALT_BLACKSMITH_USE_RECIPES = character.getBoolean("AltBlacksmithUseRecipes", true);
		ALT_CLAN_LEADER_DATE_CHANGE = character.getInt("AltClanLeaderDateChange", 3);
		if ((ALT_CLAN_LEADER_DATE_CHANGE < 1) || (ALT_CLAN_LEADER_DATE_CHANGE > 7))
		{
			LOG.warn("Wrong value specified for AltClanLeaderDateChange: {}", ALT_CLAN_LEADER_DATE_CHANGE);
			ALT_CLAN_LEADER_DATE_CHANGE = 3;
		}
		ALT_CLAN_LEADER_HOUR_CHANGE = character.getString("AltClanLeaderHourChange", "00:00:00");
		ALT_CLAN_LEADER_INSTANT_ACTIVATION = character.getBoolean("AltClanLeaderInstantActivation", false);
		ALT_CLAN_JOIN_DAYS = character.getInt("DaysBeforeJoinAClan", 1);
		ALT_CLAN_CREATE_DAYS = character.getInt("DaysBeforeCreateAClan", 10);
		ALT_CLAN_DISSOLVE_DAYS = character.getInt("DaysToPassToDissolveAClan", 7);
		ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = character.getInt("DaysBeforeJoinAllyWhenLeaved", 1);
		ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = character.getInt("DaysBeforeJoinAllyWhenDismissed", 1);
		ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = character.getInt("DaysBeforeAcceptNewClanWhenDismissed", 1);
		ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = character.getInt("DaysBeforeCreateNewAllyWhenDissolved", 1);
		ALT_MAX_NUM_OF_CLANS_IN_ALLY = character.getInt("AltMaxNumOfClansInAlly", 3);
		ALT_CLAN_MEMBERS_FOR_WAR = character.getInt("AltClanMembersForWar", 15);
		ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = character.getBoolean("AltMembersCanWithdrawFromClanWH", false);
		REMOVE_CASTLE_CIRCLETS = character.getBoolean("RemoveCastleCirclets", true);
		ALT_PARTY_RANGE = character.getInt("AltPartyRange", 1600);
		ALT_PARTY_RANGE2 = character.getInt("AltPartyRange2", 1400);
		ALT_LEAVE_PARTY_LEADER = character.getBoolean("AltLeavePartyLeader", false);
		INITIAL_EQUIPMENT_EVENT = character.getBoolean("InitialEquipmentEvent", false);
		STARTING_ADENA = character.getLong("StartingAdena", 0);
		STARTING_LEVEL = character.getInt("StartingLevel", 1);
		STARTING_SP = character.getInt("StartingSP", 0);
		MAX_ADENA = character.getLong("MaxAdena", 99900000000L);
		if (MAX_ADENA < 0)
		{
			MAX_ADENA = Long.MAX_VALUE;
		}
		AUTO_LOOT = character.getBoolean("AutoLoot", false);
		AUTO_LOOT_RAIDS = character.getBoolean("AutoLootRaids", false);
		LOOT_RAIDS_PRIVILEGE_INTERVAL = character.getInt("RaidLootRightsInterval", 900) * 1000;
		LOOT_RAIDS_PRIVILEGE_CC_SIZE = character.getInt("RaidLootRightsCCSize", 45);
		UNSTUCK_INTERVAL = character.getInt("UnstuckInterval", 300);
		TELEPORT_WATCHDOG_TIMEOUT = character.getInt("TeleportWatchdogTimeout", 0);
		PLAYER_SPAWN_PROTECTION = character.getInt("PlayerSpawnProtection", 0);
		String[] items = character.getString("PlayerSpawnProtectionAllowedItems", "").split(",");
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
		
		PLAYER_TELEPORT_PROTECTION = character.getInt("PlayerTeleportProtection", 0);
		RANDOM_RESPAWN_IN_TOWN_ENABLED = character.getBoolean("RandomRespawnInTownEnabled", true);
		OFFSET_ON_TELEPORT_ENABLED = character.getBoolean("OffsetOnTeleportEnabled", true);
		MAX_OFFSET_ON_TELEPORT = character.getInt("MaxOffsetOnTeleport", 50);
		PETITIONING_ALLOWED = character.getBoolean("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = character.getInt("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = character.getInt("MaxPetitionsPending", 25);
		ALT_GAME_FREE_TELEPORT = character.getBoolean("AltFreeTeleporting", false);
		DELETE_DAYS = character.getInt("DeleteCharAfterDays", 7);
		ALT_GAME_EXPONENT_XP = character.getFloat("AltGameExponentXp", 0);
		ALT_GAME_EXPONENT_SP = character.getFloat("AltGameExponentSp", 0);
		PARTY_XP_CUTOFF_METHOD = character.getString("PartyXpCutoffMethod", "highfive");
		PARTY_XP_CUTOFF_PERCENT = character.getDouble("PartyXpCutoffPercent", 3);
		PARTY_XP_CUTOFF_LEVEL = character.getInt("PartyXpCutoffLevel", 20);
		final String[] gaps = character.getString("PartyXpCutoffGaps", "0,9;10,14;15,99").split(";");
		PARTY_XP_CUTOFF_GAPS = new int[gaps.length][2];
		for (int i = 0; i < gaps.length; i++)
		{
			PARTY_XP_CUTOFF_GAPS[i] = new int[]
			{
				Integer.parseInt(gaps[i].split(",")[0]),
				Integer.parseInt(gaps[i].split(",")[1])
			};
		}
		final String[] percents = character.getString("PartyXpCutoffGapPercent", "100;30;0").split(";");
		PARTY_XP_CUTOFF_GAP_PERCENTS = new int[percents.length];
		for (int i = 0; i < percents.length; i++)
		{
			PARTY_XP_CUTOFF_GAP_PERCENTS[i] = Integer.parseInt(percents[i]);
		}
		DISABLE_TUTORIAL = character.getBoolean("DisableTutorial", false);
		EXPERTISE_PENALTY = character.getBoolean("ExpertisePenalty", true);
		STORE_RECIPE_SHOPLIST = character.getBoolean("StoreRecipeShopList", false);
		STORE_UI_SETTINGS = character.getBoolean("StoreCharUiSettings", false);
		FORBIDDEN_NAMES = new HashSet<>(Arrays.asList(character.getString("ForbiddenNames", "annou,ammou,amnou,anmou,anou,amou,announcements,announce").split(",")));
		SILENCE_MODE_EXCLUDE = character.getBoolean("SilenceModeExclude", false);
		ALT_VALIDATE_TRIGGER_SKILLS = character.getBoolean("AltValidateTriggerSkills", false);
		PLAYER_MOVEMENT_BLOCK_TIME = character.getInt("NpcTalkBlockingTime", 0) * 1000;
		
		// Load General L2Properties file (if exists)
		final PropertiesParser General = new PropertiesParser(GENERAL_CONFIG_FILE);
		EVERYBODY_HAS_ADMIN_RIGHTS = General.getBoolean("EverybodyHasAdminRights", false);
		SERVER_LIST_BRACKET = General.getBoolean("ServerListBrackets", false);
		SERVER_LIST_TYPE = getServerTypeId(General.getString("ServerListType", "Normal").split(","));
		SERVER_LIST_AGE = General.getInt("ServerListAge", 0);
		SERVER_GMONLY = General.getBoolean("ServerGMOnly", false);
		GM_HERO_AURA = General.getBoolean("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = General.getBoolean("GMStartupInvulnerable", false);
		GM_STARTUP_INVISIBLE = General.getBoolean("GMStartupInvisible", false);
		GM_STARTUP_SILENCE = General.getBoolean("GMStartupSilence", false);
		GM_STARTUP_AUTO_LIST = General.getBoolean("GMStartupAutoList", false);
		GM_STARTUP_DIET_MODE = General.getBoolean("GMStartupDietMode", false);
		GM_ITEM_RESTRICTION = General.getBoolean("GMItemRestriction", true);
		GM_SKILL_RESTRICTION = General.getBoolean("GMSkillRestriction", true);
		GM_TRADE_RESTRICTED_ITEMS = General.getBoolean("GMTradeRestrictedItems", false);
		GM_RESTART_FIGHTING = General.getBoolean("GMRestartFighting", true);
		GM_ANNOUNCER_NAME = General.getBoolean("GMShowAnnouncerName", false);
		GM_CRITANNOUNCER_NAME = General.getBoolean("GMShowCritAnnouncerName", false);
		GM_GIVE_SPECIAL_SKILLS = General.getBoolean("GMGiveSpecialSkills", false);
		GM_GIVE_SPECIAL_AURA_SKILLS = General.getBoolean("GMGiveSpecialAuraSkills", false);
		GAMEGUARD_ENFORCE = General.getBoolean("GameGuardEnforce", false);
		GAMEGUARD_PROHIBITACTION = General.getBoolean("GameGuardProhibitAction", false);
		LOG_CHAT = General.getBoolean("LogChat", false);
		LOG_AUTO_ANNOUNCEMENTS = General.getBoolean("LogAutoAnnouncements", false);
		LOG_ITEMS = General.getBoolean("LogItems", false);
		LOG_ITEMS_SMALL_LOG = General.getBoolean("LogItemsSmallLog", false);
		LOG_ITEM_ENCHANTS = General.getBoolean("LogItemEnchants", false);
		LOG_SKILL_ENCHANTS = General.getBoolean("LogSkillEnchants", false);
		GMAUDIT = General.getBoolean("GMAudit", false);
		SKILL_CHECK_ENABLE = General.getBoolean("SkillCheckEnable", false);
		SKILL_CHECK_REMOVE = General.getBoolean("SkillCheckRemove", false);
		SKILL_CHECK_GM = General.getBoolean("SkillCheckGM", true);
		DEBUG = General.getBoolean("Debug", false);
		DEBUG_INSTANCES = General.getBoolean("InstanceDebug", false);
		HTML_ACTION_CACHE_DEBUG = General.getBoolean("HtmlActionCacheDebug", false);
		PACKET_HANDLER_DEBUG = General.getBoolean("PacketHandlerDebug", false);
		DEVELOPER = General.getBoolean("Developer", false);
		ALT_DEV_NO_HANDLERS = General.getBoolean("AltDevNoHandlers", false) || Boolean.getBoolean("nohandlers");
		ALT_DEV_NO_QUESTS = General.getBoolean("AltDevNoQuests", false) || Boolean.getBoolean("noquests");
		ALT_DEV_NO_SPAWNS = General.getBoolean("AltDevNoSpawns", false) || Boolean.getBoolean("nospawns");
		ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowQuestsLoadInLogs", false);
		ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowScriptsLoadInLogs", false);
		SCHEDULED_THREAD_CORE_POOL_SIZE_AI = General.getInt("ScheduledThreadCorePoolSizeForAI", 0);
		SCHEDULED_THREAD_CORE_POOL_SIZE_EFFECTS = General.getInt("ScheduledThreadCorePoolSizeForEffects", 0);
		SCHEDULED_THREAD_CORE_POOL_SIZE_EVENTS = General.getInt("ScheduledThreadCorePoolSizeForEvents", 0);
		SCHEDULED_THREAD_CORE_POOL_SIZE_GENERAL = General.getInt("ScheduledThreadCorePoolSizeForGeneral", 0);
		THREAD_CORE_POOL_SIZE_EVENT = General.getInt("ThreadCorePoolSizeForEvents", 0);
		THREAD_CORE_POOL_SIZE_GENERAL = General.getInt("ThreadCorePoolSizeForGeneral", 0);
		THREAD_CORE_POOL_SIZE_GENERAL_PACKETS = General.getInt("ThreadCorePoolSizeForGeneralPackets", 0);
		THREAD_CORE_POOL_SIZE_IO_PACKETS = General.getInt("ThreadCorePoolSizeForIOPackets", 0);
		CLIENT_PACKET_QUEUE_SIZE = General.getInt("ClientPacketQueueSize", 0);
		if (CLIENT_PACKET_QUEUE_SIZE == 0)
		{
			CLIENT_PACKET_QUEUE_SIZE = MMOConfig.MMO_MAX_READ_PER_PASS + 2;
		}
		CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = General.getInt("ClientPacketQueueMaxBurstSize", 0);
		if (CLIENT_PACKET_QUEUE_MAX_BURST_SIZE == 0)
		{
			CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMOConfig.MMO_MAX_READ_PER_PASS + 1;
		}
		CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = General.getInt("ClientPacketQueueMaxPacketsPerSecond", 80);
		CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = General.getInt("ClientPacketQueueMeasureInterval", 5);
		CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = General.getInt("ClientPacketQueueMaxAveragePacketsPerSecond", 40);
		CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = General.getInt("ClientPacketQueueMaxFloodsPerMin", 2);
		CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = General.getInt("ClientPacketQueueMaxOverflowsPerMin", 1);
		CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = General.getInt("ClientPacketQueueMaxUnderflowsPerMin", 1);
		CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = General.getInt("ClientPacketQueueMaxUnknownPerMin", 5);
		DEADLOCK_DETECTOR = General.getBoolean("DeadLockDetector", true);
		DEADLOCK_CHECK_INTERVAL = General.getInt("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = General.getBoolean("RestartOnDeadlock", false);
		ALLOW_DISCARDITEM = General.getBoolean("AllowDiscardItem", true);
		AUTODESTROY_ITEM_AFTER = General.getInt("AutoDestroyDroppedItemAfter", 600);
		HERB_AUTO_DESTROY_TIME = General.getInt("AutoDestroyHerbTime", 60) * 1000;
		String[] split = General.getString("ListOfProtectedItems", "0").split(",");
		LIST_PROTECTED_ITEMS = new ArrayList<>(split.length);
		for (String id : split)
		{
			LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
		}
		DATABASE_CLEAN_UP = General.getBoolean("DatabaseCleanUp", true);
		CONNECTION_CLOSE_TIME = General.getLong("ConnectionCloseTime", 60000);
		CHAR_STORE_INTERVAL = General.getInt("CharacterDataStoreInterval", 15);
		LAZY_ITEMS_UPDATE = General.getBoolean("LazyItemsUpdate", false);
		UPDATE_ITEMS_ON_CHAR_STORE = General.getBoolean("UpdateItemsOnCharStore", false);
		DESTROY_DROPPED_PLAYER_ITEM = General.getBoolean("DestroyPlayerDroppedItem", false);
		DESTROY_EQUIPABLE_PLAYER_ITEM = General.getBoolean("DestroyEquipableItem", false);
		SAVE_DROPPED_ITEM = General.getBoolean("SaveDroppedItem", false);
		EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = General.getBoolean("EmptyDroppedItemTableAfterLoad", false);
		SAVE_DROPPED_ITEM_INTERVAL = General.getInt("SaveDroppedItemInterval", 60) * 60000;
		CLEAR_DROPPED_ITEM_TABLE = General.getBoolean("ClearDroppedItemTable", false);
		AUTODELETE_INVALID_QUEST_DATA = General.getBoolean("AutoDeleteInvalidQuestData", false);
		PRECISE_DROP_CALCULATION = General.getBoolean("PreciseDropCalculation", true);
		MULTIPLE_ITEM_DROP = General.getBoolean("MultipleItemDrop", true);
		FORCE_INVENTORY_UPDATE = General.getBoolean("ForceInventoryUpdate", false);
		LAZY_CACHE = General.getBoolean("LazyCache", true);
		CACHE_CHAR_NAMES = General.getBoolean("CacheCharNames", true);
		MIN_NPC_ANIMATION = General.getInt("MinNPCAnimation", 10);
		MAX_NPC_ANIMATION = General.getInt("MaxNPCAnimation", 20);
		MIN_MONSTER_ANIMATION = General.getInt("MinMonsterAnimation", 5);
		MAX_MONSTER_ANIMATION = General.getInt("MaxMonsterAnimation", 20);
		MOVE_BASED_KNOWNLIST = General.getBoolean("MoveBasedKnownlist", false);
		KNOWNLIST_UPDATE_INTERVAL = General.getLong("KnownListUpdateInterval", 1250);
		GRIDS_ALWAYS_ON = General.getBoolean("GridsAlwaysOn", false);
		GRID_NEIGHBOR_TURNON_TIME = General.getInt("GridNeighborTurnOnTime", 1);
		GRID_NEIGHBOR_TURNOFF_TIME = General.getInt("GridNeighborTurnOffTime", 90);
		PEACE_ZONE_MODE = General.getInt("PeaceZoneMode", 0);
		DEFAULT_GLOBAL_CHAT = General.getString("GlobalChat", "ON");
		DEFAULT_TRADE_CHAT = General.getString("TradeChat", "ON");
		ALLOW_WAREHOUSE = General.getBoolean("AllowWarehouse", true);
		WAREHOUSE_CACHE = General.getBoolean("WarehouseCache", false);
		WAREHOUSE_CACHE_TIME = General.getInt("WarehouseCacheTime", 15);
		ALLOW_REFUND = General.getBoolean("AllowRefund", true);
		ALLOW_MAIL = General.getBoolean("AllowMail", true);
		ALLOW_ATTACHMENTS = General.getBoolean("AllowAttachments", true);
		ALLOW_WEAR = General.getBoolean("AllowWear", true);
		WEAR_DELAY = General.getInt("WearDelay", 5);
		WEAR_PRICE = General.getInt("WearPrice", 10);
		INSTANCE_FINISH_TIME = 1000 * General.getInt("DefaultFinishTime", 300);
		RESTORE_PLAYER_INSTANCE = General.getBoolean("RestorePlayerInstance", false);
		ALLOW_SUMMON_IN_INSTANCE = General.getBoolean("AllowSummonInInstance", false);
		EJECT_DEAD_PLAYER_TIME = 1000 * General.getInt("EjectDeadPlayerTime", 60);
		ALLOW_LOTTERY = General.getBoolean("AllowLottery", true);
		ALLOW_RACE = General.getBoolean("AllowRace", true);
		ALLOW_WATER = General.getBoolean("AllowWater", true);
		ALLOW_RENTPET = General.getBoolean("AllowRentPet", false);
		ALLOWFISHING = General.getBoolean("AllowFishing", true);
		ALLOW_MANOR = General.getBoolean("AllowManor", true);
		ALLOW_BOAT = General.getBoolean("AllowBoat", true);
		BOAT_BROADCAST_RADIUS = General.getInt("BoatBroadcastRadius", 20000);
		ALLOW_CURSED_WEAPONS = General.getBoolean("AllowCursedWeapons", true);
		ALLOW_PET_WALKERS = General.getBoolean("AllowPetWalkers", true);
		SERVER_NEWS = General.getBoolean("ShowServerNews", false);
		USE_SAY_FILTER = General.getBoolean("UseChatFilter", false);
		CHAT_FILTER_CHARS = General.getString("ChatFilterChars", "^_^");
		String[] propertySplit4 = General.getString("BanChatChannels", "0;1;8;17").trim().split(";");
		BAN_CHAT_CHANNELS = new int[propertySplit4.length];
		try
		{
			int i = 0;
			for (String chatId : propertySplit4)
			{
				BAN_CHAT_CHANNELS[i++] = Integer.parseInt(chatId);
			}
		}
		catch (NumberFormatException nfe)
		{
			LOG.warn("Unable to load banned channels!", nfe);
		}
		ALT_MANOR_REFRESH_TIME = General.getInt("AltManorRefreshTime", 20);
		ALT_MANOR_REFRESH_MIN = General.getInt("AltManorRefreshMin", 0);
		ALT_MANOR_APPROVE_TIME = General.getInt("AltManorApproveTime", 4);
		ALT_MANOR_APPROVE_MIN = General.getInt("AltManorApproveMin", 30);
		ALT_MANOR_MAINTENANCE_MIN = General.getInt("AltManorMaintenanceMin", 6);
		ALT_MANOR_SAVE_ALL_ACTIONS = General.getBoolean("AltManorSaveAllActions", false);
		ALT_MANOR_SAVE_PERIOD_RATE = General.getInt("AltManorSavePeriodRate", 2);
		ALT_LOTTERY_PRIZE = General.getLong("AltLotteryPrize", 50000);
		ALT_LOTTERY_TICKET_PRICE = General.getLong("AltLotteryTicketPrice", 2000);
		ALT_LOTTERY_5_NUMBER_RATE = General.getFloat("AltLottery5NumberRate", 0.6f);
		ALT_LOTTERY_4_NUMBER_RATE = General.getFloat("AltLottery4NumberRate", 0.2f);
		ALT_LOTTERY_3_NUMBER_RATE = General.getFloat("AltLottery3NumberRate", 0.2f);
		ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = General.getLong("AltLottery2and1NumberPrize", 200);
		ALT_ITEM_AUCTION_ENABLED = General.getBoolean("AltItemAuctionEnabled", true);
		ALT_ITEM_AUCTION_EXPIRED_AFTER = General.getInt("AltItemAuctionExpiredAfter", 14);
		ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID = General.getInt("AltItemAuctionTimeExtendsOnBid", 0) * 1000;
		FS_TIME_ATTACK = General.getInt("TimeOfAttack", 50);
		FS_TIME_COOLDOWN = General.getInt("TimeOfCoolDown", 5);
		FS_TIME_ENTRY = General.getInt("TimeOfEntry", 3);
		FS_TIME_WARMUP = General.getInt("TimeOfWarmUp", 2);
		FS_PARTY_MEMBER_COUNT = General.getInt("NumberOfNecessaryPartyMembers", 4);
		if (FS_TIME_ATTACK <= 0)
		{
			FS_TIME_ATTACK = 50;
		}
		if (FS_TIME_COOLDOWN <= 0)
		{
			FS_TIME_COOLDOWN = 5;
		}
		if (FS_TIME_ENTRY <= 0)
		{
			FS_TIME_ENTRY = 3;
		}
		if (FS_TIME_ENTRY <= 0)
		{
			FS_TIME_ENTRY = 3;
		}
		if (FS_TIME_ENTRY <= 0)
		{
			FS_TIME_ENTRY = 3;
		}
		RIFT_MIN_PARTY_SIZE = General.getInt("RiftMinPartySize", 5);
		RIFT_MAX_JUMPS = General.getInt("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = General.getInt("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = General.getInt("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = General.getInt("AutoJumpsDelayMax", 600);
		RIFT_BOSS_ROOM_TIME_MUTIPLY = General.getFloat("BossRoomTimeMultiply", 1.5f);
		RIFT_ENTER_COST_RECRUIT = General.getInt("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = General.getInt("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = General.getInt("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = General.getInt("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = General.getInt("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = General.getInt("HeroCost", 33);
		DEFAULT_PUNISH = IllegalActionPunishmentType.findByName(General.getString("DefaultPunish", "KICK"));
		DEFAULT_PUNISH_PARAM = General.getInt("DefaultPunishParam", 0);
		ONLY_GM_ITEMS_FREE = General.getBoolean("OnlyGMItemsFree", true);
		JAIL_IS_PVP = General.getBoolean("JailIsPvp", false);
		JAIL_DISABLE_CHAT = General.getBoolean("JailDisableChat", true);
		JAIL_DISABLE_TRANSACTION = General.getBoolean("JailDisableTransaction", false);
		CUSTOM_SPAWNLIST_TABLE = General.getBoolean("CustomSpawnlistTable", false);
		SAVE_GMSPAWN_ON_CUSTOM = General.getBoolean("SaveGmSpawnOnCustom", false);
		CUSTOM_NPC_DATA = General.getBoolean("CustomNpcData", false);
		CUSTOM_TELEPORT_TABLE = General.getBoolean("CustomTeleportTable", false);
		CUSTOM_NPCBUFFER_TABLES = General.getBoolean("CustomNpcBufferTables", false);
		CUSTOM_SKILLS_LOAD = General.getBoolean("CustomSkillsLoad", false);
		CUSTOM_ITEMS_LOAD = General.getBoolean("CustomItemsLoad", false);
		CUSTOM_MULTISELL_LOAD = General.getBoolean("CustomMultisellLoad", false);
		CUSTOM_BUYLIST_LOAD = General.getBoolean("CustomBuyListLoad", false);
		ALT_BIRTHDAY_GIFT = General.getInt("AltBirthdayGift", 22187);
		ALT_BIRTHDAY_MAIL_SUBJECT = General.getString("AltBirthdayMailSubject", "Happy Birthday!");
		ALT_BIRTHDAY_MAIL_TEXT = General.getString("AltBirthdayMailText", "Hello Adventurer!! Seeing as you're one year older now, I thought I would send you some birthday cheer :) Please find your birthday pack attached. May these gifts bring you joy and happiness on this very special day." + EOL
			+ EOL + "Sincerely, Alegria");
		ENABLE_BLOCK_CHECKER_EVENT = General.getBoolean("EnableBlockCheckerEvent", false);
		MIN_BLOCK_CHECKER_TEAM_MEMBERS = General.getInt("BlockCheckerMinTeamMembers", 2);
		if (MIN_BLOCK_CHECKER_TEAM_MEMBERS < 1)
		{
			MIN_BLOCK_CHECKER_TEAM_MEMBERS = 1;
		}
		else if (MIN_BLOCK_CHECKER_TEAM_MEMBERS > 6)
		{
			MIN_BLOCK_CHECKER_TEAM_MEMBERS = 6;
		}
		HBCE_FAIR_PLAY = General.getBoolean("HBCEFairPlay", false);
		HELLBOUND_WITHOUT_QUEST = General.getBoolean("HellboundWithoutQuest", false);
		
		NORMAL_ENCHANT_COST_MULTIPLIER = General.getInt("NormalEnchantCostMultipiler", 1);
		SAFE_ENCHANT_COST_MULTIPLIER = General.getInt("SafeEnchantCostMultipiler", 5);
		
		BOTREPORT_ENABLE = General.getBoolean("EnableBotReportButton", false);
		BOTREPORT_RESETPOINT_HOUR = General.getString("BotReportPointsResetHour", "00:00").split(":");
		BOTREPORT_REPORT_DELAY = General.getInt("BotReportDelay", 30) * 60000;
		BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS = General.getBoolean("AllowReportsFromSameClanMembers", false);
		ENABLE_FALLING_DAMAGE = General.getBoolean("EnableFallingDamage", true);
		
		ENABLE_ITEM_MALL = General.getBoolean("EnableItemMall", false);
		EX_JAPAN_MINIGAME = General.getBoolean("JapanMinigame", false);
		
		// Load FloodProtector L2Properties file
		final PropertiesParser FloodProtectors = new PropertiesParser(FLOOD_PROTECTOR_FILE);
		
		loadFloodProtectorConfigs(FloodProtectors);
		
		// Load NPC L2Properties file (if exists)
		final PropertiesParser NPC = new PropertiesParser(NPC_CONFIG_FILE);
		
		ANNOUNCE_MAMMON_SPAWN = NPC.getBoolean("AnnounceMammonSpawn", false);
		ALT_MOB_AGRO_IN_PEACEZONE = NPC.getBoolean("AltMobAgroInPeaceZone", true);
		ALT_ATTACKABLE_NPCS = NPC.getBoolean("AltAttackableNpcs", true);
		ALT_GAME_VIEWNPC = NPC.getBoolean("AltGameViewNpc", false);
		MAX_DRIFT_RANGE = NPC.getInt("MaxDriftRange", 300);
		DEEPBLUE_DROP_RULES = NPC.getBoolean("UseDeepBlueDropRules", true);
		DEEPBLUE_DROP_RULES_RAID = NPC.getBoolean("UseDeepBlueDropRulesRaid", true);
		SHOW_NPC_LVL = NPC.getBoolean("ShowNpcLevel", false);
		SHOW_CREST_WITHOUT_QUEST = NPC.getBoolean("ShowCrestWithoutQuest", false);
		ENABLE_RANDOM_ENCHANT_EFFECT = NPC.getBoolean("EnableRandomEnchantEffect", false);
		MIN_NPC_LVL_DMG_PENALTY = NPC.getInt("MinNPCLevelForDmgPenalty", 78);
		NPC_DMG_PENALTY = parseConfigLine(NPC.getString("DmgPenaltyForLvLDifferences", "0.7, 0.6, 0.6, 0.55"));
		NPC_CRIT_DMG_PENALTY = parseConfigLine(NPC.getString("CritDmgPenaltyForLvLDifferences", "0.75, 0.65, 0.6, 0.58"));
		NPC_SKILL_DMG_PENALTY = parseConfigLine(NPC.getString("SkillDmgPenaltyForLvLDifferences", "0.8, 0.7, 0.65, 0.62"));
		MIN_NPC_LVL_MAGIC_PENALTY = NPC.getInt("MinNPCLevelForMagicPenalty", 78);
		NPC_SKILL_CHANCE_PENALTY = parseConfigLine(NPC.getString("SkillChancePenaltyForLvLDifferences", "2.5, 3.0, 3.25, 3.5"));
		DECAY_TIME_TASK = NPC.getInt("DecayTimeTask", 5000);
		DEFAULT_CORPSE_TIME = NPC.getInt("DefaultCorpseTime", 7);
		SPOILED_CORPSE_EXTEND_TIME = NPC.getInt("SpoiledCorpseExtendTime", 10);
		CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY = NPC.getInt("CorpseConsumeSkillAllowedTimeBeforeDecay", 2000);
		GUARD_ATTACK_AGGRO_MOB = NPC.getBoolean("GuardAttackAggroMob", false);
		ALLOW_WYVERN_UPGRADER = NPC.getBoolean("AllowWyvernUpgrader", false);
		String[] listPetRentNpc = NPC.getString("ListPetRentNpc", "30827").split(",");
		LIST_PET_RENT_NPC = new ArrayList<>(listPetRentNpc.length);
		for (String id : listPetRentNpc)
		{
			LIST_PET_RENT_NPC.add(Integer.valueOf(id));
		}
		RAID_HP_REGEN_MULTIPLIER = NPC.getDouble("RaidHpRegenMultiplier", 100) / 100;
		RAID_MP_REGEN_MULTIPLIER = NPC.getDouble("RaidMpRegenMultiplier", 100) / 100;
		RAID_PDEFENCE_MULTIPLIER = NPC.getDouble("RaidPDefenceMultiplier", 100) / 100;
		RAID_MDEFENCE_MULTIPLIER = NPC.getDouble("RaidMDefenceMultiplier", 100) / 100;
		RAID_PATTACK_MULTIPLIER = NPC.getDouble("RaidPAttackMultiplier", 100) / 100;
		RAID_MATTACK_MULTIPLIER = NPC.getDouble("RaidMAttackMultiplier", 100) / 100;
		RAID_MIN_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMinRespawnMultiplier", 1.0f);
		RAID_MAX_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMaxRespawnMultiplier", 1.0f);
		RAID_MINION_RESPAWN_TIMER = NPC.getInt("RaidMinionRespawnTime", 300000);
		final String[] propertySplit = NPC.getString("CustomMinionsRespawnTime", "").split(";");
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
		
		RAID_DISABLE_CURSE = NPC.getBoolean("DisableRaidCurse", false);
		RAID_CHAOS_TIME = NPC.getInt("RaidChaosTime", 10);
		GRAND_CHAOS_TIME = NPC.getInt("GrandChaosTime", 10);
		MINION_CHAOS_TIME = NPC.getInt("MinionChaosTime", 10);
		INVENTORY_MAXIMUM_PET = NPC.getInt("MaximumSlotsForPet", 12);
		PET_HP_REGEN_MULTIPLIER = NPC.getDouble("PetHpRegenMultiplier", 100) / 100;
		PET_MP_REGEN_MULTIPLIER = NPC.getDouble("PetMpRegenMultiplier", 100) / 100;
		
		DROP_ADENA_MIN_LEVEL_DIFFERENCE = NPC.getInt("DropAdenaMinLevelDifference", 8);
		DROP_ADENA_MAX_LEVEL_DIFFERENCE = NPC.getInt("DropAdenaMaxLevelDifference", 15);
		DROP_ADENA_MIN_LEVEL_GAP_CHANCE = NPC.getDouble("DropAdenaMinLevelGapChance", 10);
		
		DROP_ITEM_MIN_LEVEL_DIFFERENCE = NPC.getInt("DropItemMinLevelDifference", 5);
		DROP_ITEM_MAX_LEVEL_DIFFERENCE = NPC.getInt("DropItemMaxLevelDifference", 10);
		DROP_ITEM_MIN_LEVEL_GAP_CHANCE = NPC.getDouble("DropItemMinLevelGapChance", 10);
		
		// Load Rates L2Properties file (if exists)
		final PropertiesParser RatesSettings = new PropertiesParser(RATES_CONFIG_FILE);
		
		RATE_XP = RatesSettings.getFloat("RateXp", 1);
		RATE_SP = RatesSettings.getFloat("RateSp", 1);
		RATE_PARTY_XP = RatesSettings.getFloat("RatePartyXp", 1);
		RATE_PARTY_SP = RatesSettings.getFloat("RatePartySp", 1);
		RATE_EXTRACTABLE = RatesSettings.getFloat("RateExtractable", 1);
		RATE_DROP_MANOR = RatesSettings.getInt("RateDropManor", 1);
		RATE_QUEST_DROP = RatesSettings.getFloat("RateQuestDrop", 1);
		RATE_QUEST_REWARD = RatesSettings.getFloat("RateQuestReward", 1);
		RATE_QUEST_REWARD_XP = RatesSettings.getFloat("RateQuestRewardXP", 1);
		RATE_QUEST_REWARD_SP = RatesSettings.getFloat("RateQuestRewardSP", 1);
		RATE_QUEST_REWARD_ADENA = RatesSettings.getFloat("RateQuestRewardAdena", 1);
		RATE_QUEST_REWARD_USE_MULTIPLIERS = RatesSettings.getBoolean("UseQuestRewardMultipliers", false);
		RATE_QUEST_REWARD_POTION = RatesSettings.getFloat("RateQuestRewardPotion", 1);
		RATE_QUEST_REWARD_SCROLL = RatesSettings.getFloat("RateQuestRewardScroll", 1);
		RATE_QUEST_REWARD_RECIPE = RatesSettings.getFloat("RateQuestRewardRecipe", 1);
		RATE_QUEST_REWARD_MATERIAL = RatesSettings.getFloat("RateQuestRewardMaterial", 1);
		RATE_HB_TRUST_INCREASE = RatesSettings.getFloat("RateHellboundTrustIncrease", 1);
		RATE_HB_TRUST_DECREASE = RatesSettings.getFloat("RateHellboundTrustDecrease", 1);
		
		RATE_VITALITY_LEVEL_1 = RatesSettings.getFloat("RateVitalityLevel1", 1.5f);
		RATE_VITALITY_LEVEL_2 = RatesSettings.getFloat("RateVitalityLevel2", 2);
		RATE_VITALITY_LEVEL_3 = RatesSettings.getFloat("RateVitalityLevel3", 2.5f);
		RATE_VITALITY_LEVEL_4 = RatesSettings.getFloat("RateVitalityLevel4", 3);
		RATE_RECOVERY_VITALITY_PEACE_ZONE = RatesSettings.getFloat("RateRecoveryPeaceZone", 1);
		RATE_VITALITY_LOST = RatesSettings.getFloat("RateVitalityLost", 1);
		RATE_VITALITY_GAIN = RatesSettings.getFloat("RateVitalityGain", 1);
		RATE_RECOVERY_ON_RECONNECT = RatesSettings.getFloat("RateRecoveryOnReconnect", 4);
		RATE_KARMA_LOST = RatesSettings.getFloat("RateKarmaLost", -1);
		if (RATE_KARMA_LOST == -1)
		{
			RATE_KARMA_LOST = RATE_XP;
		}
		RATE_KARMA_EXP_LOST = RatesSettings.getFloat("RateKarmaExpLost", 1);
		RATE_SIEGE_GUARDS_PRICE = RatesSettings.getFloat("RateSiegeGuardsPrice", 1);
		PLAYER_DROP_LIMIT = RatesSettings.getInt("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = RatesSettings.getInt("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = RatesSettings.getInt("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = RatesSettings.getInt("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = RatesSettings.getFloat("PetXpRate", 1);
		PET_FOOD_RATE = RatesSettings.getInt("PetFoodRate", 1);
		SINEATER_XP_RATE = RatesSettings.getFloat("SinEaterXpRate", 1);
		KARMA_DROP_LIMIT = RatesSettings.getInt("KarmaDropLimit", 10);
		KARMA_RATE_DROP = RatesSettings.getInt("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = RatesSettings.getInt("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = RatesSettings.getInt("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("KarmaRateDropEquipWeapon", 10);
		
		RATE_DEATH_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("DeathDropAmountMultiplier", 1);
		RATE_CORPSE_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("CorpseDropAmountMultiplier", 1);
		RATE_HERB_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("HerbDropAmountMultiplier", 1);
		RATE_RAID_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("RaidDropAmountMultiplier", 1);
		RATE_DEATH_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("DeathDropChanceMultiplier", 1);
		RATE_CORPSE_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("CorpseDropChanceMultiplier", 1);
		RATE_HERB_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("HerbDropChanceMultiplier", 1);
		RATE_RAID_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("RaidDropChanceMultiplier", 1);
		String[] dropAmountMultiplier = RatesSettings.getString("DropAmountMultiplierByItemId", "").split(";");
		RATE_DROP_AMOUNT_MULTIPLIER = new HashMap<>(dropAmountMultiplier.length);
		if (!dropAmountMultiplier[0].isEmpty())
		{
			for (String item : dropAmountMultiplier)
			{
				String[] itemSplit = item.split(",");
				if (itemSplit.length != 2)
				{
					LOG.warn("Config.load(): invalid config property -> RateDropItemsById {}", item);
				}
				else
				{
					try
					{
						RATE_DROP_AMOUNT_MULTIPLIER.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!item.isEmpty())
						{
							LOG.warn("Config.load(): invalid config property -> RateDropItemsById {}", item);
						}
					}
				}
			}
		}
		
		String[] dropChanceMultiplier = RatesSettings.getString("DropChanceMultiplierByItemId", "").split(";");
		RATE_DROP_CHANCE_MULTIPLIER = new HashMap<>(dropChanceMultiplier.length);
		if (!dropChanceMultiplier[0].isEmpty())
		{
			for (String item : dropChanceMultiplier)
			{
				String[] itemSplit = item.split(",");
				if (itemSplit.length != 2)
				{
					LOG.warn("Config.load(): invalid config property -> RateDropItemsById {}", item);
				}
				else
				{
					try
					{
						RATE_DROP_CHANCE_MULTIPLIER.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (!item.isEmpty())
						{
							LOG.warn("Config.load(): invalid config property -> RateDropItemsById {}", item);
						}
					}
				}
			}
		}
		
		// Load L2JMod L2Properties file (if exists)
		final PropertiesParser L2JModSettings = new PropertiesParser(L2JMOD_CONFIG_FILE);
		
		TVT_EVENT_ENABLED = L2JModSettings.getBoolean("TvTEventEnabled", false);
		TVT_EVENT_IN_INSTANCE = L2JModSettings.getBoolean("TvTEventInInstance", false);
		TVT_EVENT_INSTANCE_FILE = L2JModSettings.getString("TvTEventInstanceFile", "coliseum.xml");
		TVT_EVENT_INTERVAL = L2JModSettings.getString("TvTEventInterval", "20:00").split(",");
		TVT_EVENT_PARTICIPATION_TIME = L2JModSettings.getInt("TvTEventParticipationTime", 3600);
		TVT_EVENT_RUNNING_TIME = L2JModSettings.getInt("TvTEventRunningTime", 1800);
		TVT_EVENT_PARTICIPATION_NPC_ID = L2JModSettings.getInt("TvTEventParticipationNpcId", 0);
		
		if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			TVT_EVENT_ENABLED = false;
			LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
		}
		else
		{
			String[] tvtNpcCoords = L2JModSettings.getString("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (tvtNpcCoords.length < 3)
			{
				TVT_EVENT_ENABLED = false;
				LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
			}
			else
			{
				TVT_EVENT_REWARDS = new ArrayList<>();
				TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
				TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
				TVT_EVENT_TEAM_1_COORDINATES = new int[3];
				TVT_EVENT_TEAM_2_COORDINATES = new int[3];
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
				if (tvtNpcCoords.length == 4)
				{
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(tvtNpcCoords[3]);
				}
				TVT_EVENT_MIN_PLAYERS_IN_TEAMS = L2JModSettings.getInt("TvTEventMinPlayersInTeams", 1);
				TVT_EVENT_MAX_PLAYERS_IN_TEAMS = L2JModSettings.getInt("TvTEventMaxPlayersInTeams", 20);
				TVT_EVENT_MIN_LVL = L2JModSettings.getByte("TvTEventMinPlayerLevel", (byte) 1);
				TVT_EVENT_MAX_LVL = L2JModSettings.getByte("TvTEventMaxPlayerLevel", (byte) 80);
				TVT_EVENT_RESPAWN_TELEPORT_DELAY = L2JModSettings.getInt("TvTEventRespawnTeleportDelay", 20);
				TVT_EVENT_START_LEAVE_TELEPORT_DELAY = L2JModSettings.getInt("TvTEventStartLeaveTeleportDelay", 20);
				TVT_EVENT_EFFECTS_REMOVAL = L2JModSettings.getInt("TvTEventEffectsRemoval", 0);
				TVT_EVENT_MAX_PARTICIPANTS_PER_IP = L2JModSettings.getInt("TvTEventMaxParticipantsPerIP", 0);
				TVT_ALLOW_VOICED_COMMAND = L2JModSettings.getBoolean("TvTAllowVoicedInfoCommand", false);
				TVT_EVENT_TEAM_1_NAME = L2JModSettings.getString("TvTEventTeam1Name", "Team1");
				tvtNpcCoords = L2JModSettings.getString("TvTEventTeam1Coordinates", "0,0,0").split(",");
				if (tvtNpcCoords.length < 3)
				{
					TVT_EVENT_ENABLED = false;
					LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
				}
				else
				{
					TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
					TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
					TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
					TVT_EVENT_TEAM_2_NAME = L2JModSettings.getString("TvTEventTeam2Name", "Team2");
					tvtNpcCoords = L2JModSettings.getString("TvTEventTeam2Coordinates", "0,0,0").split(",");
					if (tvtNpcCoords.length < 3)
					{
						TVT_EVENT_ENABLED = false;
						LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
					}
					else
					{
						TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
						TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
						TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
						tvtNpcCoords = L2JModSettings.getString("TvTEventParticipationFee", "0,0").split(",");
						try
						{
							TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(tvtNpcCoords[0]);
							TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(tvtNpcCoords[1]);
						}
						catch (NumberFormatException nfe)
						{
							if (tvtNpcCoords.length > 0)
							{
								LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
							}
						}
						tvtNpcCoords = L2JModSettings.getString("TvTEventReward", "57,100000").split(";");
						for (String reward : tvtNpcCoords)
						{
							String[] rewardSplit = reward.split(",");
							if (rewardSplit.length != 2)
							{
								LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward {}", reward);
							}
							else
							{
								try
								{
									TVT_EVENT_REWARDS.add(new int[]
									{
										Integer.parseInt(rewardSplit[0]),
										Integer.parseInt(rewardSplit[1])
									});
								}
								catch (NumberFormatException nfe)
								{
									if (!reward.isEmpty())
									{
										LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward {}", reward);
									}
								}
							}
						}
						
						TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = L2JModSettings.getBoolean("TvTEventTargetTeamMembersAllowed", true);
						TVT_EVENT_SCROLL_ALLOWED = L2JModSettings.getBoolean("TvTEventScrollsAllowed", false);
						TVT_EVENT_POTIONS_ALLOWED = L2JModSettings.getBoolean("TvTEventPotionsAllowed", false);
						TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = L2JModSettings.getBoolean("TvTEventSummonByItemAllowed", false);
						TVT_REWARD_TEAM_TIE = L2JModSettings.getBoolean("TvTRewardTeamTie", false);
						tvtNpcCoords = L2JModSettings.getString("TvTDoorsToOpen", "").split(";");
						for (String door : tvtNpcCoords)
						{
							try
							{
								TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen {}", door);
								}
							}
						}
						
						tvtNpcCoords = L2JModSettings.getString("TvTDoorsToClose", "").split(";");
						for (String door : tvtNpcCoords)
						{
							try
							{
								TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose {}", door);
								}
							}
						}
						
						tvtNpcCoords = L2JModSettings.getString("TvTEventFighterBuffs", "").split(";");
						if (!tvtNpcCoords[0].isEmpty())
						{
							TVT_EVENT_FIGHTER_BUFFS = new HashMap<>(tvtNpcCoords.length);
							for (String skill : tvtNpcCoords)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs {}", skill);
								}
								else
								{
									try
									{
										TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs {}", skill);
										}
									}
								}
							}
						}
						
						tvtNpcCoords = L2JModSettings.getString("TvTEventMageBuffs", "").split(";");
						if (!tvtNpcCoords[0].isEmpty())
						{
							TVT_EVENT_MAGE_BUFFS = new HashMap<>(tvtNpcCoords.length);
							for (String skill : tvtNpcCoords)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs {}", skill);
								}
								else
								{
									try
									{
										TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs {}", skill);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		L2JMOD_ANTIFEED_ENABLE = L2JModSettings.getBoolean("AntiFeedEnable", false);
		L2JMOD_ANTIFEED_DUALBOX = L2JModSettings.getBoolean("AntiFeedDualbox", true);
		L2JMOD_ANTIFEED_DISCONNECTED_AS_DUALBOX = L2JModSettings.getBoolean("AntiFeedDisconnectedAsDualbox", true);
		L2JMOD_ANTIFEED_INTERVAL = L2JModSettings.getInt("AntiFeedInterval", 120) * 1000;
		
		L2JMOD_MULTILANG_DEFAULT = L2JModSettings.getString("MultiLangDefault", "en");
		L2JMOD_MULTILANG_ENABLE = L2JModSettings.getBoolean("MultiLangEnable", false);
		String[] allowed = L2JModSettings.getString("MultiLangAllowed", L2JMOD_MULTILANG_DEFAULT).split(";");
		L2JMOD_MULTILANG_ALLOWED = new ArrayList<>(allowed.length);
		for (String lang : allowed)
		{
			L2JMOD_MULTILANG_ALLOWED.add(lang);
		}
		
		if (!L2JMOD_MULTILANG_ALLOWED.contains(L2JMOD_MULTILANG_DEFAULT))
		{
			LOG.warn("MultiLang[Config.load()]: default language: {} is not in allowed list !", L2JMOD_MULTILANG_DEFAULT);
		}
		
		L2JMOD_MULTILANG_VOICED_ALLOW = L2JModSettings.getBoolean("MultiLangVoiceCommand", true);
		L2JMOD_MULTILANG_SM_ENABLE = L2JModSettings.getBoolean("MultiLangSystemMessageEnable", false);
		allowed = L2JModSettings.getString("MultiLangSystemMessageAllowed", "").split(";");
		L2JMOD_MULTILANG_SM_ALLOWED = new ArrayList<>(allowed.length);
		for (String lang : allowed)
		{
			if (!lang.isEmpty())
			{
				L2JMOD_MULTILANG_SM_ALLOWED.add(lang);
			}
		}
		L2JMOD_MULTILANG_NS_ENABLE = L2JModSettings.getBoolean("MultiLangNpcStringEnable", false);
		allowed = L2JModSettings.getString("MultiLangNpcStringAllowed", "").split(";");
		L2JMOD_MULTILANG_NS_ALLOWED = new ArrayList<>(allowed.length);
		for (String lang : allowed)
		{
			if (!lang.isEmpty())
			{
				L2JMOD_MULTILANG_NS_ALLOWED.add(lang);
			}
		}
		
		L2WALKER_PROTECTION = L2JModSettings.getBoolean("L2WalkerProtection", false);
		
		L2JMOD_DUALBOX_CHECK_MAX_PLAYERS_PER_IP = L2JModSettings.getInt("DualboxCheckMaxPlayersPerIP", 0);
		L2JMOD_DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = L2JModSettings.getInt("DualboxCheckMaxOlympiadParticipantsPerIP", 0);
		L2JMOD_DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP = L2JModSettings.getInt("DualboxCheckMaxL2EventParticipantsPerIP", 0);
		String[] dualboxCheckWhiteList = L2JModSettings.getString("DualboxCheckWhitelist", "127.0.0.1,0").split(";");
		L2JMOD_DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
		for (String entry : dualboxCheckWhiteList)
		{
			String[] entrySplit = entry.split(",");
			if (entrySplit.length != 2)
			{
				LOG.warn("DualboxCheck[Config.load()]: invalid config property -> DualboxCheckWhitelist {}", entry);
			}
			else
			{
				try
				{
					int num = Integer.parseInt(entrySplit[1]);
					num = (num == 0) ? -1 : num;
					L2JMOD_DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
				}
				catch (UnknownHostException e)
				{
					LOG.warn("DualboxCheck[Config.load()]: invalid address -> DualboxCheckWhitelist {}", entrySplit[0]);
				}
				catch (NumberFormatException e)
				{
					LOG.warn("DualboxCheck[Config.load()]: invalid number -> DualboxCheckWhitelist {}", entrySplit[1]);
				}
			}
		}
		
		SENDSTATUS_TRADE_JUST_OFFLINE = L2JModSettings.getBoolean("SendStatusTradeJustOffline", false);
		SENDSTATUS_TRADE_MOD = L2JModSettings.getDouble("SendStatusTradeMod", 1);
		SHOW_OFFLINE_MODE_IN_ONLINE = L2JModSettings.getBoolean("ShowOfflineTradeInOnline", false);
		Random ppc = new Random();
		int z = ppc.nextInt(6);
		if (z == 0)
		{
			z += 2;
		}
		for (int x = 0; x < 8; x++)
		{
			if (x == 4)
			{
				RWHO_ARRAY[x] = 44;
			}
			else
			{
				RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
			}
		}
		RWHO_ARRAY[11] = 37265 + ppc.nextInt((z * 2) + 3);
		RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
		z = 36224 + ppc.nextInt(z * 2);
		RWHO_ARRAY[9] = z;
		RWHO_ARRAY[10] = z;
		RWHO_ARRAY[12] = 1;
		RWHO_LOG = L2JModSettings.getBoolean("RemoteWhoLog", false);
		RWHO_SEND_TRASH = L2JModSettings.getBoolean("RemoteWhoSendTrash", false);
		RWHO_MAX_ONLINE = L2JModSettings.getInt("RemoteWhoMaxOnline", 0);
		RWHO_KEEP_STAT = L2JModSettings.getInt("RemoteOnlineKeepStat", 5);
		RWHO_ONLINE_INCREMENT = L2JModSettings.getInt("RemoteOnlineIncrement", 0);
		RWHO_PRIV_STORE_FACTOR = L2JModSettings.getFloat("RemotePrivStoreFactor", 0);
		RWHO_FORCE_INC = L2JModSettings.getInt("RemoteWhoForceInc", 0);
		
		final PropertiesParser topSettings = new PropertiesParser(TOP_CONFIG_FILE);
		L2_TOP_MANAGER_ENABLED = topSettings.getBoolean("L2TopManagerEnabled", false);
		
		L2_TOP_WEB_ADDRESS = topSettings.getString("L2TopWebAddress", "");
		L2_TOP_SMS_ADDRESS = topSettings.getString("L2TopSmsAddress", "");
		L2_TOP_SERVER_PREFIX = topSettings.getString("L2TopServerPrefix", "");
		L2_TOP_REWARD_ID = topSettings.getInt("L2TopRewardId", 57);
		L2_TOP_REWARD_COUNT = topSettings.getInt("L2TopRewardCount", 1000);
		MMO_TOP_MANAGER_ENABLED = topSettings.getBoolean("MMOTopEnable", false);
		MMO_TOP_WEB_ADDRESS = topSettings.getString("MMOTopUrl", "");
		MMO_TOP_REWARD_ID = topSettings.getInt("MMOTopRewardId", 57);
		MMO_TOP_REWARD_COUNT = topSettings.getInt("MMOTopRewardCount", 1000);
		
		TOP_MANAGER_INTERVAL = topSettings.getInt("TopManagerInterval", 300000);
		TOP_SERVER_ADDRESS = topSettings.getString("TopServerAddress", "L2jenergy");
		TOP_SAVE_DAYS = topSettings.getInt("TopSaveDays", 30);
	}
	
	/**
	 * Set a new value to a config parameter.
	 * @param pName the name of the parameter whose value to change
	 * @param pValue the new value of the parameter
	 * @return {@code true} if the value of the parameter was changed, {@code false} otherwise
	 */
	public static boolean setParameterValue(String pName, String pValue)
	{
		switch (pName.trim().toLowerCase())
		{
			// rates.properties
			case "ratexp":
				RATE_XP = Float.parseFloat(pValue);
				break;
			case "ratesp":
				RATE_SP = Float.parseFloat(pValue);
				break;
			case "ratepartyxp":
				RATE_PARTY_XP = Float.parseFloat(pValue);
				break;
			case "rateextractable":
				RATE_EXTRACTABLE = Float.parseFloat(pValue);
				break;
			case "ratedropadena":
				RATE_DROP_AMOUNT_MULTIPLIER.put(Inventory.ADENA_ID, Float.parseFloat(pValue));
				break;
			case "ratedropmanor":
				RATE_DROP_MANOR = Integer.parseInt(pValue);
				break;
			case "ratequestdrop":
				RATE_QUEST_DROP = Float.parseFloat(pValue);
				break;
			case "ratequestreward":
				RATE_QUEST_REWARD = Float.parseFloat(pValue);
				break;
			case "ratequestrewardxp":
				RATE_QUEST_REWARD_XP = Float.parseFloat(pValue);
				break;
			case "ratequestrewardsp":
				RATE_QUEST_REWARD_SP = Float.parseFloat(pValue);
				break;
			case "ratequestrewardadena":
				RATE_QUEST_REWARD_ADENA = Float.parseFloat(pValue);
				break;
			case "usequestrewardmultipliers":
				RATE_QUEST_REWARD_USE_MULTIPLIERS = Boolean.parseBoolean(pValue);
				break;
			case "ratequestrewardpotion":
				RATE_QUEST_REWARD_POTION = Float.parseFloat(pValue);
				break;
			case "ratequestrewardscroll":
				RATE_QUEST_REWARD_SCROLL = Float.parseFloat(pValue);
				break;
			case "ratequestrewardrecipe":
				RATE_QUEST_REWARD_RECIPE = Float.parseFloat(pValue);
				break;
			case "ratequestrewardmaterial":
				RATE_QUEST_REWARD_MATERIAL = Float.parseFloat(pValue);
				break;
			case "ratehellboundtrustincrease":
				RATE_HB_TRUST_INCREASE = Float.parseFloat(pValue);
				break;
			case "ratehellboundtrustdecrease":
				RATE_HB_TRUST_DECREASE = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel1":
				RATE_VITALITY_LEVEL_1 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel2":
				RATE_VITALITY_LEVEL_2 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel3":
				RATE_VITALITY_LEVEL_3 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel4":
				RATE_VITALITY_LEVEL_4 = Float.parseFloat(pValue);
				break;
			case "raterecoverypeacezone":
				RATE_RECOVERY_VITALITY_PEACE_ZONE = Float.parseFloat(pValue);
				break;
			case "ratevitalitylost":
				RATE_VITALITY_LOST = Float.parseFloat(pValue);
				break;
			case "ratevitalitygain":
				RATE_VITALITY_GAIN = Float.parseFloat(pValue);
				break;
			case "raterecoveryonreconnect":
				RATE_RECOVERY_ON_RECONNECT = Float.parseFloat(pValue);
				break;
			case "ratekarmaexplost":
				RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
				break;
			case "ratesiegeguardsprice":
				RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);
				break;
			case "ratecommonherbs":
				RATE_DROP_COMMON_HERBS = Float.parseFloat(pValue);
				break;
			case "ratehpherbs":
				RATE_DROP_HP_HERBS = Float.parseFloat(pValue);
				break;
			case "ratempherbs":
				RATE_DROP_MP_HERBS = Float.parseFloat(pValue);
				break;
			case "ratespecialherbs":
				RATE_DROP_SPECIAL_HERBS = Float.parseFloat(pValue);
				break;
			case "ratevitalityherbs":
				RATE_DROP_VITALITY_HERBS = Float.parseFloat(pValue);
				break;
			case "playerdroplimit":
				PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
				break;
			case "playerratedrop":
				PLAYER_RATE_DROP = Integer.parseInt(pValue);
				break;
			case "playerratedropitem":
				PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
				break;
			case "playerratedropequip":
				PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
				break;
			case "playerratedropequipweapon":
				PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
				break;
			case "petxprate":
				PET_XP_RATE = Float.parseFloat(pValue);
				break;
			case "petfoodrate":
				PET_FOOD_RATE = Integer.parseInt(pValue);
				break;
			case "sineaterxprate":
				SINEATER_XP_RATE = Float.parseFloat(pValue);
				break;
			case "karmadroplimit":
				KARMA_DROP_LIMIT = Integer.parseInt(pValue);
				break;
			case "karmaratedrop":
				KARMA_RATE_DROP = Integer.parseInt(pValue);
				break;
			case "karmaratedropitem":
				KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
				break;
			case "karmaratedropequip":
				KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
				break;
			case "karmaratedropequipweapon":
				KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
				break;
			case "deletecharafterdays":
				DELETE_DAYS = Integer.parseInt(pValue);
				break;
			case "shownpclevel":
				SHOW_NPC_LVL = Boolean.parseBoolean(pValue);
				break;
			case "showcrestwithoutquest":
				SHOW_CREST_WITHOUT_QUEST = Boolean.parseBoolean(pValue);
				break;
			case "checkknownlist":
				CHECK_KNOWN = Boolean.parseBoolean(pValue);
				break;
			case "maxdriftrange":
				MAX_DRIFT_RANGE = Integer.parseInt(pValue);
				break;
			case "usedeepbluedroprules":
				DEEPBLUE_DROP_RULES = Boolean.parseBoolean(pValue);
				break;
			case "usedeepbluedroprulesraid":
				DEEPBLUE_DROP_RULES_RAID = Boolean.parseBoolean(pValue);
				break;
			case "guardattackaggromob":
				GUARD_ATTACK_AGGRO_MOB = Boolean.parseBoolean(pValue);
				break;
			case "maximumslotsfornodwarf":
				INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumslotsfordwarf":
				INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumslotsforgmplayer":
				INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);
				break;
			case "maximumslotsforquestitems":
				INVENTORY_MAXIMUM_QUEST_ITEMS = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsfornodwarf":
				WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsfordwarf":
				WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsforclan":
				WAREHOUSE_SLOTS_CLAN = Integer.parseInt(pValue);
				break;
			case "enchantchanceelementstone":
				ENCHANT_CHANCE_ELEMENT_STONE = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementcrystal":
				ENCHANT_CHANCE_ELEMENT_CRYSTAL = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementjewel":
				ENCHANT_CHANCE_ELEMENT_JEWEL = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementenergy":
				ENCHANT_CHANCE_ELEMENT_ENERGY = Double.parseDouble(pValue);
				break;
			case "augmentationngskillchance":
				AUGMENTATION_NG_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationngglowchance":
				AUGMENTATION_NG_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationmidskillchance":
				AUGMENTATION_MID_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationmidglowchance":
				AUGMENTATION_MID_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationhighskillchance":
				AUGMENTATION_HIGH_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationhighglowchance":
				AUGMENTATION_HIGH_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationtopskillchance":
				AUGMENTATION_TOP_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationtopglowchance":
				AUGMENTATION_TOP_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationbasestatchance":
				AUGMENTATION_BASESTAT_CHANCE = Integer.parseInt(pValue);
				break;
			case "hpregenmultiplier":
				HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "mpregenmultiplier":
				MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "cpregenmultiplier":
				CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidhpregenmultiplier":
				RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidmpregenmultiplier":
				RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidpdefencemultiplier":
				RAID_PDEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidmdefencemultiplier":
				RAID_MDEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidpattackmultiplier":
				RAID_PATTACK_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidmattackmultiplier":
				RAID_MATTACK_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidminionrespawntime":
				RAID_MINION_RESPAWN_TIMER = Integer.parseInt(pValue);
				break;
			case "raidchaostime":
				RAID_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "grandchaostime":
				GRAND_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "minionchaostime":
				MINION_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "startingadena":
				STARTING_ADENA = Long.parseLong(pValue);
				break;
			case "startinglevel":
				STARTING_LEVEL = Byte.parseByte(pValue);
				break;
			case "startingsp":
				STARTING_SP = Integer.parseInt(pValue);
				break;
			case "unstuckinterval":
				UNSTUCK_INTERVAL = Integer.parseInt(pValue);
				break;
			case "teleportwatchdogtimeout":
				TELEPORT_WATCHDOG_TIMEOUT = Integer.parseInt(pValue);
				break;
			case "playerspawnprotection":
				PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
				break;
			case "playerfakedeathupprotection":
				PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);
				break;
			case "partyxpcutoffmethod":
				PARTY_XP_CUTOFF_METHOD = pValue;
				break;
			case "partyxpcutoffpercent":
				PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
				break;
			case "partyxpcutofflevel":
				PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);
				break;
			case "respawnrestorecp":
				RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
				break;
			case "respawnrestorehp":
				RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
				break;
			case "respawnrestoremp":
				RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;
				break;
			case "maxpvtstoresellslotsdwarf":
				MAX_PVTSTORESELL_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maxpvtstoresellslotsother":
				MAX_PVTSTORESELL_SLOTS_OTHER = Integer.parseInt(pValue);
				break;
			case "maxpvtstorebuyslotsdwarf":
				MAX_PVTSTOREBUY_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maxpvtstorebuyslotsother":
				MAX_PVTSTOREBUY_SLOTS_OTHER = Integer.parseInt(pValue);
				break;
			case "storeskillcooltime":
				STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
				break;
			case "subclassstoreskillcooltime":
				SUBCLASS_STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
				break;
			case "announcemammonspawn":
				ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(pValue);
				break;
			case "altgamecreation":
				ALT_GAME_CREATION = Boolean.parseBoolean(pValue);
				break;
			case "altgamecreationspeed":
				ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
				break;
			case "altgamecreationxprate":
				ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
				break;
			case "altgamecreationrarexpsprate":
				ALT_GAME_CREATION_RARE_XPSP_RATE = Double.parseDouble(pValue);
				break;
			case "altgamecreationsprate":
				ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue);
				break;
			case "altweightlimit":
				ALT_WEIGHT_LIMIT = Double.parseDouble(pValue);
				break;
			case "altblacksmithuserecipes":
				ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(pValue);
				break;
			case "altgameskilllearn":
				ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(pValue);
				break;
			case "removecastlecirclets":
				REMOVE_CASTLE_CIRCLETS = Boolean.parseBoolean(pValue);
				break;
			case "altgamecancelbyhit":
				ALT_GAME_CANCEL_BOW = pValue.equalsIgnoreCase("bow") || pValue.equalsIgnoreCase("all");
				ALT_GAME_CANCEL_CAST = pValue.equalsIgnoreCase("cast") || pValue.equalsIgnoreCase("all");
				break;
			case "altshieldblocks":
				ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(pValue);
				break;
			case "altperfectshieldblockrate":
				ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(pValue);
				break;
			case "delevel":
				ALT_GAME_DELEVEL = Boolean.parseBoolean(pValue);
				break;
			case "magicfailures":
				ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(pValue);
				break;
			case "altmobagroinpeacezone":
				ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(pValue);
				break;
			case "altgameexponentxp":
				ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
				break;
			case "altgameexponentsp":
				ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);
				break;
			case "allowclassmasters":
				ALLOW_CLASS_MASTERS = Boolean.parseBoolean(pValue);
				break;
			case "allowentiretree":
				ALLOW_ENTIRE_TREE = Boolean.parseBoolean(pValue);
				break;
			case "alternateclassmaster":
				ALTERNATE_CLASS_MASTER = Boolean.parseBoolean(pValue);
				break;
			case "altpartyrange":
				ALT_PARTY_RANGE = Integer.parseInt(pValue);
				break;
			case "altpartyrange2":
				ALT_PARTY_RANGE2 = Integer.parseInt(pValue);
				break;
			case "altleavepartyleader":
				ALT_LEAVE_PARTY_LEADER = Boolean.parseBoolean(pValue);
				break;
			case "craftingenabled":
				IS_CRAFTING_ENABLED = Boolean.parseBoolean(pValue);
				break;
			case "craftmasterwork":
				CRAFT_MASTERWORK = Boolean.parseBoolean(pValue);
				break;
			case "lifecrystalneeded":
				LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(pValue);
				break;
			case "autoloot":
				AUTO_LOOT = Boolean.parseBoolean(pValue);
				break;
			case "autolootraids":
				AUTO_LOOT_RAIDS = Boolean.parseBoolean(pValue);
				break;
			case "autolootherbs":
				AUTO_LOOT_HERBS = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanbekilledinpeacezone":
				ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanshop":
				ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanusegk":
				ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanteleport":
				ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercantrade":
				ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanusewarehouse":
				ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.parseBoolean(pValue);
				break;
			case "maxpersonalfamepoints":
				MAX_PERSONAL_FAME_POINTS = Integer.parseInt(pValue);
				break;
			case "fortresszonefametaskfrequency":
				FORTRESS_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "fortresszonefameaquirepoints":
				FORTRESS_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(pValue);
				break;
			case "castlezonefametaskfrequency":
				CASTLE_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "castlezonefameaquirepoints":
				CASTLE_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(pValue);
				break;
			case "altfreeteleporting":
				ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "altsubclasswithoutquests":
				ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(pValue);
				break;
			case "altsubclasseverywhere":
				ALT_GAME_SUBCLASS_EVERYWHERE = Boolean.parseBoolean(pValue);
				break;
			case "altmemberscanwithdrawfromclanwh":
				ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(pValue);
				break;
			case "dwarfrecipelimit":
				DWARF_RECIPE_LIMIT = Integer.parseInt(pValue);
				break;
			case "commonrecipelimit":
				COMMON_RECIPE_LIMIT = Integer.parseInt(pValue);
				break;
			case "tvteventenabled":
				TVT_EVENT_ENABLED = Boolean.parseBoolean(pValue);
				break;
			case "tvteventinterval":
				TVT_EVENT_INTERVAL = pValue.split(",");
				break;
			case "tvteventparticipationtime":
				TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(pValue);
				break;
			case "tvteventrunningtime":
				TVT_EVENT_RUNNING_TIME = Integer.parseInt(pValue);
				break;
			case "tvteventparticipationnpcid":
				TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(pValue);
				break;
			case "antifeedenable":
				L2JMOD_ANTIFEED_ENABLE = Boolean.parseBoolean(pValue);
				break;
			case "antifeeddualbox":
				L2JMOD_ANTIFEED_DUALBOX = Boolean.parseBoolean(pValue);
				break;
			case "antifeeddisconnectedasdualbox":
				L2JMOD_ANTIFEED_DISCONNECTED_AS_DUALBOX = Boolean.parseBoolean(pValue);
				break;
			case "antifeedinterval":
				L2JMOD_ANTIFEED_INTERVAL = 1000 * Integer.parseInt(pValue);
				break;
			default:
				try
				{
					// TODO: stupid GB configs...
					if (!pName.startsWith("Interval_") && !pName.startsWith("Random_"))
					{
						pName = pName.toUpperCase();
					}
					Field clazField = Config.class.getField(pName);
					int modifiers = clazField.getModifiers();
					// just in case :)
					if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers))
					{
						throw new SecurityException("Cannot modify non public, non static or final config!");
					}
					
					if (clazField.getType() == int.class)
					{
						clazField.setInt(clazField, Integer.parseInt(pValue));
					}
					else if (clazField.getType() == short.class)
					{
						clazField.setShort(clazField, Short.parseShort(pValue));
					}
					else if (clazField.getType() == byte.class)
					{
						clazField.setByte(clazField, Byte.parseByte(pValue));
					}
					else if (clazField.getType() == long.class)
					{
						clazField.setLong(clazField, Long.parseLong(pValue));
					}
					else if (clazField.getType() == float.class)
					{
						clazField.setFloat(clazField, Float.parseFloat(pValue));
					}
					else if (clazField.getType() == double.class)
					{
						clazField.setDouble(clazField, Double.parseDouble(pValue));
					}
					else if (clazField.getType() == boolean.class)
					{
						clazField.setBoolean(clazField, Boolean.parseBoolean(pValue));
					}
					else if (clazField.getType() == String.class)
					{
						clazField.set(clazField, pValue);
					}
					else
					{
						return false;
					}
				}
				catch (NoSuchFieldException e)
				{
					return false;
				}
				catch (Exception e)
				{
					LOG.warn("Unable to set parameter value!", e);
					return false;
				}
		}
		return true;
	}
	
	/**
	 * Loads flood protector configurations.
	 * @param properties the properties object containing the actual values of the flood protector configs
	 */
	private static void loadFloodProtectorConfigs(final PropertiesParser properties)
	{
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_USE_ITEM, "UseItem", 4);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ROLL_DICE, "RollDice", 42);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_FIREWORK, "Firework", 42);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", 16);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", 100);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", 5);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SUBCLASS, "Subclass", 20);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", 10);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", 5);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MULTISELL, "MultiSell", 1);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_TRANSACTION, "Transaction", 10);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", 3);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANOR, "Manor", 30);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SENDMAIL, "SendMail", 100);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_CHARACTER_SELECT, "CharacterSelect", 30);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_AUCTION, "ItemAuction", 9);
	}
	
	/**
	 * Loads single flood protector configuration.
	 * @param properties properties file reader
	 * @param config flood protector configuration instance
	 * @param configString flood protector configuration string that determines for which flood protector configuration should be read
	 * @param defaultInterval default flood protector interval
	 */
	private static void loadFloodProtectorConfig(final PropertiesParser properties, final FloodProtectorConfig config, final String configString, final int defaultInterval)
	{
		config.FLOOD_PROTECTION_INTERVAL = properties.getInt(StringUtil.concat("FloodProtector", configString, "Interval"), defaultInterval);
		config.LOG_FLOODING = properties.getBoolean(StringUtil.concat("FloodProtector", configString, "LogFlooding"), false);
		config.PUNISHMENT_LIMIT = properties.getInt(StringUtil.concat("FloodProtector", configString, "PunishmentLimit"), 0);
		config.PUNISHMENT_TYPE = properties.getString(StringUtil.concat("FloodProtector", configString, "PunishmentType"), "none");
		config.PUNISHMENT_TIME = properties.getInt(StringUtil.concat("FloodProtector", configString, "PunishmentTime"), 0) * 60000;
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, List<ItemHolder>> _claimItems = new HashMap<>(3);
		private final Map<Integer, List<ItemHolder>> _rewardItems = new HashMap<>(3);
		private final Map<Integer, Boolean> _allowedClassChange = new HashMap<>(3);
		
		public ClassMasterSettings(String configLine)
		{
			parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			if (configLine.isEmpty())
			{
				return;
			}
			
			final StringTokenizer st = new StringTokenizer(configLine, ";");
			
			while (st.hasMoreTokens())
			{
				// get allowed class change
				final int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				final List<ItemHolder> requiredItems = new ArrayList<>();
				// parse items needed for class change
				if (st.hasMoreTokens())
				{
					final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						final int itemId = Integer.parseInt(st3.nextToken());
						final int quantity = Integer.parseInt(st3.nextToken());
						requiredItems.add(new ItemHolder(itemId, quantity));
					}
				}
				
				_claimItems.put(job, requiredItems);
				
				final List<ItemHolder> rewardItems = new ArrayList<>();
				// parse gifts after class change
				if (st.hasMoreTokens())
				{
					final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						final int itemId = Integer.parseInt(st3.nextToken());
						final int quantity = Integer.parseInt(st3.nextToken());
						rewardItems.add(new ItemHolder(itemId, quantity));
					}
				}
				
				_rewardItems.put(job, rewardItems);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if ((_allowedClassChange == null) || !_allowedClassChange.containsKey(job))
			{
				return false;
			}
			return _allowedClassChange.get(job);
		}
		
		public List<ItemHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<ItemHolder> getRequireItems(int job)
		{
			return _claimItems.get(job);
		}
	}
	
	public static int getServerTypeId(String[] serverTypes)
	{
		int tType = 0;
		for (String cType : serverTypes)
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
	
	/**
	 * @param line the string line to parse
	 * @return a parsed float map
	 */
	private static Map<Integer, Float> parseConfigLine(String line)
	{
		String[] propertySplit = line.split(",");
		Map<Integer, Float> ret = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			ret.put(i++, Float.parseFloat(value));
		}
		return ret;
	}
}
