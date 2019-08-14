package com.l2jserver.gameserver.configuration.config;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("server.json")
public class ServerConfig
{
	@Setting(name = "EnableUPnP")
	public static boolean ENABLE_UPNP;
	
	@Setting(name = "GameserverHostname")
	public static String GAMESERVER_HOSTNAME;
	
	@Setting(name = "GameserverPort")
	public static int PORT_GAME;
	
	@Setting(name = "LoginPort")
	public static int GAME_SERVER_LOGIN_PORT;
	
	@Setting(name = "LoginHost")
	public static String GAME_SERVER_LOGIN_HOST;
	
	@Setting(name = "RequestServerID")
	public static int REQUEST_ID;
	
	@Setting(name = "AcceptAlternateID")
	public static boolean ACCEPT_ALTERNATE_ID;
	
	@Setting(name = "Database", canNull = true)
	public static String DATABASE_ENGINE;
	
	@Setting(name = "Driver", canNull = true)
	public static String DATABASE_DRIVER;
	
	@Setting(name = "URL", canNull = true)
	public static String DATABASE_URL;
	
	@Setting(name = "Login", canNull = true)
	public static String DATABASE_LOGIN;
	
	@Setting(name = "Password", canNull = true)
	public static String DATABASE_PASSWORD;
	
	@Setting(name = "ConnectionPool")
	public static String DATABASE_CONNECTION_POOL;
	
	@Setting(name = "MaximumDbConnections")
	public static int DATABASE_MAX_CONNECTIONS;
	
	@Setting(name = "MaximumDbIdleTime")
	public static int DATABASE_MAX_IDLE_TIME;
	
	@Setting(name = "DatapackRoot", method = "datapackRoot")
	public static File DATAPACK_ROOT;
	
	@Setting(name = "PlayerNameTemplate", method = "patternPlayerName")
	public static Pattern PLAYER_NAME_TEMPLATE;
	
	@Setting(name = "PetNameTemplate", method = "patternPetName")
	public static Pattern PET_NAME_TEMPLATE;
	
	@Setting(name = "ClanNameTemplate", method = "patternClanName")
	public static Pattern CLAN_NAME_TEMPLATE;
	
	@Setting(name = "CharMaxNumber")
	public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	
	@Setting(name = "MaximumOnlineUsers")
	public static int MAXIMUM_ONLINE_USERS;
	
	@Setting(name = "MinProtocolRevision", minValue = 267)
	public static int MIN_PROTOCOL_REVISION;
	
	@Setting(name = "MaxProtocolRevision", maxValue = 273)
	public static int MAX_PROTOCOL_REVISION;
	
	@Setting(ignore = true)
	public static boolean RESERVE_HOST_ON_LOGIN;
	
	public void patternPlayerName(final String value)
	{
		PLAYER_NAME_TEMPLATE = Pattern.compile(value);
	}
	
	public void patternPetName(final String value)
	{
		PET_NAME_TEMPLATE = Pattern.compile(value);
	}
	
	public void patternClanName(final String value)
	{
		CLAN_NAME_TEMPLATE = Pattern.compile(value);
	}
	
	public void datapackRoot(final String value)
	{
		try
		{
			DATAPACK_ROOT = new File(value).getCanonicalFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
