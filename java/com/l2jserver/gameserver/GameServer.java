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
package com.l2jserver.gameserver;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.UPnPService;
import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.IPv4Filter;
import com.l2jserver.commons.util.MemoryWatchDog;
import com.l2jserver.commons.util.Util;
import com.l2jserver.commons.versioning.Version;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.configuration.config.GeoDataConfig;
import com.l2jserver.gameserver.configuration.config.MMOConfig;
import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.configuration.config.TelnetConfig;
import com.l2jserver.gameserver.configuration.config.community.CTeleportConfig;
import com.l2jserver.gameserver.configuration.config.custom.OfflineConfig;
import com.l2jserver.gameserver.configuration.config.custom.PremiumConfig;
import com.l2jserver.gameserver.configuration.config.events.WeddingConfig;
import com.l2jserver.gameserver.configuration.loader.ConfigLoader;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.data.sql.impl.AnnouncementsTable;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.sql.impl.CharSummonTable;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.data.sql.impl.CrestTable;
import com.l2jserver.gameserver.data.sql.impl.NpcBufferTable;
import com.l2jserver.gameserver.data.sql.impl.OfflineTradersTable;
import com.l2jserver.gameserver.data.sql.impl.SummonSkillsTable;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.ArmorSetsData;
import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.data.xml.impl.CategoryData;
import com.l2jserver.gameserver.data.xml.impl.ClassListData;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemData;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemGroupsData;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemHPBonusData;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemOptionsData;
import com.l2jserver.gameserver.data.xml.impl.EnchantSkillGroupsData;
import com.l2jserver.gameserver.data.xml.impl.FishData;
import com.l2jserver.gameserver.data.xml.impl.FishingMonstersData;
import com.l2jserver.gameserver.data.xml.impl.FishingRodsData;
import com.l2jserver.gameserver.data.xml.impl.HennaData;
import com.l2jserver.gameserver.data.xml.impl.HitConditionBonusData;
import com.l2jserver.gameserver.data.xml.impl.InitialEquipmentData;
import com.l2jserver.gameserver.data.xml.impl.InitialShortcutData;
import com.l2jserver.gameserver.data.xml.impl.KarmaData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.OptionData;
import com.l2jserver.gameserver.data.xml.impl.PetDataTable;
import com.l2jserver.gameserver.data.xml.impl.PlayerCreationPointData;
import com.l2jserver.gameserver.data.xml.impl.PlayerTemplateData;
import com.l2jserver.gameserver.data.xml.impl.PlayerXpPercentLostData;
import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.data.xml.impl.SecondaryAuthData;
import com.l2jserver.gameserver.data.xml.impl.SiegeScheduleData;
import com.l2jserver.gameserver.data.xml.impl.SkillLearnData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.data.xml.impl.StaticObjectData;
import com.l2jserver.gameserver.data.xml.impl.TeleportBBSData;
import com.l2jserver.gameserver.data.xml.impl.TransformData;
import com.l2jserver.gameserver.data.xml.impl.UIData;
import com.l2jserver.gameserver.datatables.AugmentationData;
import com.l2jserver.gameserver.datatables.BotReportTable;
import com.l2jserver.gameserver.datatables.EventDroplist;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.MerchantPriceConfigTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.handler.EffectHandler;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.AirShipManager;
import com.l2jserver.gameserver.instancemanager.AntiFeedManager;
import com.l2jserver.gameserver.instancemanager.AuctionManager;
import com.l2jserver.gameserver.instancemanager.BoatManager;
import com.l2jserver.gameserver.instancemanager.CHSiegeManager;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.CastleManorManager;
import com.l2jserver.gameserver.instancemanager.ClanHallManager;
import com.l2jserver.gameserver.instancemanager.CoupleManager;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jserver.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.instancemanager.FortSiegeManager;
import com.l2jserver.gameserver.instancemanager.FourSepulchersManager;
import com.l2jserver.gameserver.instancemanager.GlobalVariablesManager;
import com.l2jserver.gameserver.instancemanager.GraciaSeedsManager;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.ItemAuctionManager;
import com.l2jserver.gameserver.instancemanager.ItemsOnGroundManager;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.instancemanager.MercTicketManager;
import com.l2jserver.gameserver.instancemanager.PetitionManager;
import com.l2jserver.gameserver.instancemanager.PremiumManager;
import com.l2jserver.gameserver.instancemanager.PunishmentManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jserver.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jserver.gameserver.instancemanager.SiegeManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.instancemanager.WalkingManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.instancemanager.games.FishingChampionshipManager;
import com.l2jserver.gameserver.instancemanager.games.Lottery;
import com.l2jserver.gameserver.instancemanager.games.MiniGameScoreManager;
import com.l2jserver.gameserver.instancemanager.vote.L2TopManager;
import com.l2jserver.gameserver.instancemanager.vote.MMOTopManager;
import com.l2jserver.gameserver.model.AutoSpawnHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.PartyMatchRoomList;
import com.l2jserver.gameserver.model.PartyMatchWaitingList;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.gameeventengine.GameEventManager;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.L2GamePacketHandler;
import com.l2jserver.gameserver.pathfinding.PathFinding;
import com.l2jserver.gameserver.script.faenor.FaenorScriptEngine;
import com.l2jserver.gameserver.scripting.L2ScriptEngineManager;
import com.l2jserver.gameserver.taskmanager.KnownListUpdateTaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.util.DeadLockDetector;
import com.l2jserver.mmocore.SelectorConfig;
import com.l2jserver.mmocore.SelectorThread;

public final class GameServer
{
	private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
	
	private static final String DATAPACK = "-dp";
	private static final String GEODATA = "-gd";
	
	private final Version _version;
	public static long _upTime = 0L;
	private final SelectorThread<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	public static GameServer gameServer;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public GameServer() throws Exception
	{
		long serverLoadStart = System.currentTimeMillis();
		_version = new Version(GameServer.class);
		
		if (!IdFactory.getInstance().isInitialized())
		{
			LOG.error("{}: Could not read object IDs from database. Please check your configuration.", getClass().getSimpleName());
			throw new Exception("Could not initialize the ID factory!");
		}
		
		ThreadPoolManager.getInstance();
		EventDispatcher.getInstance();
		
		new File("logs/game").mkdirs();
		
		// load script engines
		printSection("Engines");
		L2ScriptEngineManager.getInstance();
		
		printSection("World");
		// start game time control early
		GameTimeController.init();
		InstanceManager.getInstance();
		L2World.getInstance();
		MapRegionManager.getInstance();
		AnnouncementsTable.getInstance();
		GlobalVariablesManager.getInstance();
		
		printSection("Data");
		CategoryData.getInstance();
		SecondaryAuthData.getInstance();
		
		printSection("Effects");
		EffectHandler.getInstance().executeScript();
		printSection("Enchant Skill Groups");
		EnchantSkillGroupsData.getInstance();
		printSection("Skill Trees");
		SkillTreesData.getInstance();
		printSection("Skills");
		SkillData.getInstance();
		SummonSkillsTable.getInstance();
		
		printSection("Items");
		ItemTable.getInstance();
		EnchantItemGroupsData.getInstance();
		EnchantItemData.getInstance();
		EnchantItemOptionsData.getInstance();
		OptionData.getInstance();
		EnchantItemHPBonusData.getInstance();
		MerchantPriceConfigTable.getInstance().loadInstances();
		BuyListData.getInstance();
		if (Config.ENABLE_ITEM_MALL)
		{
			ProductItemData.getInstance();
		}
		MultisellData.getInstance();
		RecipeData.getInstance();
		ArmorSetsData.getInstance();
		FishData.getInstance();
		FishingMonstersData.getInstance();
		FishingRodsData.getInstance();
		HennaData.getInstance();
		
		printSection("Characters");
		ClassListData.getInstance();
		InitialEquipmentData.getInstance();
		InitialShortcutData.getInstance();
		ExperienceData.getInstance();
		PlayerXpPercentLostData.getInstance();
		KarmaData.getInstance();
		HitConditionBonusData.getInstance();
		PlayerTemplateData.getInstance();
		PlayerCreationPointData.getInstance();
		CharNameTable.getInstance();
		AdminData.getInstance();
		RaidBossPointsManager.getInstance();
		PetDataTable.getInstance();
		CharSummonTable.getInstance().init();
		
		// Multi-Language System
		printSection("Messages");
		MessagesData.getInstance();
		
		printSection("Clans");
		ClanTable.getInstance();
		CHSiegeManager.getInstance();
		ClanHallManager.getInstance();
		AuctionManager.getInstance();
		
		printSection("Geodata");
		GeoData.getInstance();
		
		if (GeoDataConfig.PATHFINDING > 0)
		{
			PathFinding.getInstance();
		}
		
		printSection("NPCs");
		SkillLearnData.getInstance();
		NpcData.getInstance();
		WalkingManager.getInstance();
		StaticObjectData.getInstance();
		ZoneManager.getInstance();
		DoorData.getInstance();
		CastleManager.getInstance().loadInstances();
		NpcBufferTable.getInstance();
		GrandBossManager.getInstance().initZones();
		EventDroplist.getInstance();
		printSection("Auction Manager");
		ItemAuctionManager.getInstance();
		
		printSection("Olympiad");
		Olympiad.getInstance();// TODO: log
		Hero.getInstance();// TODO: log
		
		printSection("Seven Signs");
		SevenSigns.getInstance();
		
		// Call to load caches
		printSection("Cache");
		HtmCache.getInstance();
		CrestTable.getInstance();
		TeleportLocationTable.getInstance();
		if (CTeleportConfig.BBS_TELEPORTS_ENABLE)
		{
			TeleportBBSData.getInstance();
		}
		UIData.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		PetitionManager.getInstance();
		AugmentationData.getInstance(); // TODO: log
		CursedWeaponsManager.getInstance();
		Lottery.getInstance();
		TransformData.getInstance();
		BotReportTable.getInstance();
		
		printSection("Bonus Tops");
		L2TopManager.getInstance();
		MMOTopManager.getInstance();
		
		printSection("Scripts");
		QuestManager.getInstance();
		BoatManager.getInstance();
		AirShipManager.getInstance();
		GraciaSeedsManager.getInstance();
		
		try
		{
			LOG.info("{}: Loading server scripts:", getClass().getSimpleName());
			if (!Config.ALT_DEV_NO_HANDLERS || !Config.ALT_DEV_NO_QUESTS)
			{
				L2ScriptEngineManager.getInstance().executeScriptList(new File(ServerConfig.DATAPACK_ROOT, "data/scripts.cfg"));
			}
		}
		catch (IOException ioe)
		{
			LOG.error("{}: Failed loading scripts.cfg, scripts are not going to be loaded!", getClass().getSimpleName());
		}
		
		SpawnTable.getInstance().load();
		DayNightSpawnManager.getInstance().trim().notifyChangeMode();
		FourSepulchersManager.getInstance().init();
		DimensionalRiftManager.getInstance();
		RaidBossSpawnManager.getInstance();
		
		printSection("Siege");
		SiegeManager.getInstance().getSieges();
		CastleManager.getInstance().activateInstances();
		FortManager.getInstance().loadInstances();
		FortManager.getInstance().activateInstances();
		FortSiegeManager.getInstance();
		SiegeScheduleData.getInstance();
		
		MerchantPriceConfigTable.getInstance().updateReferences();
		TerritoryWarManager.getInstance();
		CastleManorManager.getInstance();
		MercTicketManager.getInstance();
		
		if (PremiumConfig.PREMIUM_SYSTEM_ENABLED)
		{
			PremiumManager.getInstance();
		}
		
		QuestManager.getInstance().report();
		
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance();
		}
		
		if ((Config.AUTODESTROY_ITEM_AFTER > 0) || (Config.HERB_AUTO_DESTROY_TIME > 0))
		{
			ItemsAutoDestroy.getInstance();
		}
		
		MonsterRace.getInstance();
		FishingChampionshipManager.getInstance();
		SevenSigns.getInstance().spawnSevenSignsNPC();
		SevenSignsFestival.getInstance();
		AutoSpawnHandler.getInstance();
		
		FaenorScriptEngine.getInstance();
		// Init of a cursed weapon manager
		
		LOG.info("AutoSpawnHandler: Loaded {} handlers in total.", AutoSpawnHandler.getInstance().size());
		
		if (WeddingConfig.ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
		}
		
		if (Config.EX_JAPAN_MINIGAME)
		{
			MiniGameScoreManager.getInstance();
		}
		TaskManager.getInstance();
		
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.GAME_ID);
		
		if (Config.ALLOW_MAIL)
		{
			MailManager.getInstance();
		}
		
		PunishmentManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		LOG.info("IdFactory: Free ObjectID's remaining: {}", IdFactory.getInstance().size());
		GameEventManager.getEventsInstances();
		KnownListUpdateTaskManager.getInstance();
		
		if ((OfflineConfig.OFFLINE_TRADE_ENABLE || OfflineConfig.OFFLINE_CRAFT_ENABLE) && OfflineConfig.RESTORE_OFFLINERS)
		{
			OfflineTradersTable.getInstance().restoreOfflineTraders();
		}
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_deadDetectThread = null;
		}
		System.gc();
		Toolkit.getDefaultToolkit().beep();
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = MMOConfig.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = MMOConfig.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = MMOConfig.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = MMOConfig.MMO_HELPER_BUFFER_COUNT;
		sc.TCP_NODELAY = MMOConfig.MMO_TCP_NODELAY;
		
		_gamePacketHandler = new L2GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!ServerConfig.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(ServerConfig.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				LOG.error("{}: The GameServer bind address is invalid, using all avaliable IPs!", getClass().getSimpleName(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, ServerConfig.PORT_GAME);
			_selectorThread.start();
			LOG.info("{}: is now listening on: {}:{}", getClass().getSimpleName(), ServerConfig.GAMESERVER_HOSTNAME, ServerConfig.PORT_GAME);
		}
		catch (IOException e)
		{
			LOG.error("{}: Failed to open server socket!", getClass().getSimpleName(), e);
			System.exit(1);
		}
		
		LOG.info("{}: Maximum numbers of connected players: {}", getClass().getSimpleName(), ServerConfig.MAXIMUM_ONLINE_USERS);
		LOG.info("{}: Started, free memory {} of {}", getClass().getSimpleName(), MemoryWatchDog.getMemFreeMb(), MemoryWatchDog.getMemMaxMb());
		LOG.info("{}: Used memory: {}", getClass().getSimpleName(), MemoryWatchDog.getMemUsedMb());
		LOG.info("Revision: ................ {}", getVersion().getRevisionNumber());
		LOG.info("Builded: ................. {}", getVersion().getBuildDate());
		LOG.info("Compiler version: ........ {}", getVersion().getBuildJdk());
		LOG.info("Forum: ................... L2jEnergy.ru");
		LOG.info("{}: Server loaded in {} seconds.", getClass().getSimpleName(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - serverLoadStart));
		_upTime = System.currentTimeMillis();
		if (ServerConfig.ENABLE_UPNP)
		{
			printSection("UPnP");
			UPnPService.getInstance().load(ServerConfig.PORT_GAME, "L2J Game Server");
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		// Initialize configurations.
		ConfigLoader.loading();
		Config.load();
		final String dp = Util.parseArg(args, DATAPACK, true);
		if (dp != null)
		{
			ServerConfig.DATAPACK_ROOT = new File(dp);
		}
		
		final String gd = Util.parseArg(args, GEODATA, true);
		if (gd != null)
		{
			GeoDataConfig.GEODATA_PATH = Paths.get(gd);
		}
		
		final String LOG_FOLDER = "./logs"; // Name of folder for log file
		// Create log folder
		File logFolder = new File(LOG_FOLDER);
		logFolder.mkdir();
		
		printSection("Database");
		DAOFactory.getInstance();
		
		ConnectionFactory.builder() //
			.withDriver(ServerConfig.DATABASE_DRIVER) //
			.withUrl(ServerConfig.DATABASE_URL) //
			.withUser(ServerConfig.DATABASE_LOGIN) //
			.withPassword(ServerConfig.DATABASE_PASSWORD) //
			.withConnectionPool(ServerConfig.DATABASE_CONNECTION_POOL) //
			.withMaxIdleTime(ServerConfig.DATABASE_MAX_IDLE_TIME) //
			.withMaxPoolSize(ServerConfig.DATABASE_MAX_CONNECTIONS) //
			.build();
		
		gameServer = new GameServer();
		
		if (TelnetConfig.IS_TELNET_ENABLED)
		{
			// new Status().start();
		}
		else
		{
			LOG.info("{}: Telnet server is currently disabled.", GameServer.class.getSimpleName());
		}
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public L2GamePacketHandler getL2GamePacketHandler()
	{
		return _gamePacketHandler;
	}
	
	public DeadLockDetector getDeadLockDetectorThread()
	{
		return _deadDetectThread;
	}
	
	public Version getVersion()
	{
		return _version;
	}
	
	public static void printSection(String s)
	{
		s = "=[ " + s + " ]";
		while (s.length() < 61)
		{
			s = "-" + s;
		}
		LOG.info(s);
	}
}
