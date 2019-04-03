package com.l2jserver.loginserver.configuration.config;

import java.io.File;
import java.io.IOException;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("loginserver.json")
public class LoginConfig
{
	@Setting(name = "EnableUPnP")
	public static boolean ENABLE_UPNP;
	@Setting(name = "LoginHostname")
	public static String GAME_SERVER_LOGIN_HOST;
	@Setting(name = "LoginPort", canNull = true)
	public static int GAME_SERVER_LOGIN_PORT;
	@Setting(name = "LoginserverHostname")
	public static String LOGIN_BIND_ADDRESS;
	@Setting(name = "LoginserverPort", canNull = true)
	public static int PORT_LOGIN;
	@Setting(name = "Debug")
	public static boolean DEBUG;
	@Setting(name = "AcceptNewGameServer")
	public static boolean ACCEPT_NEW_GAMESERVER;
	@Setting(name = "LoginTryBeforeBan")
	public static int LOGIN_TRY_BEFORE_BAN;
	@Setting(name = "LoginBlockAfterBan")
	public static int LOGIN_BLOCK_AFTER_BAN;
	@Setting(name = "LoginRestartSchedule")
	public static boolean LOGIN_SERVER_SCHEDULE_RESTART;
	@Setting(name = "LoginRestartTime")
	public static long LOGIN_SERVER_SCHEDULE_RESTART_TIME;
	@Setting(name = "Driver", canNull = true)
	public static String DATABASE_DRIVER;
	@Setting(name = "URL", canNull = true)
	public static String DATABASE_URL;
	@Setting(name = "Login", canNull = true)
	public static String DATABASE_LOGIN;
	@Setting(name = "Password", canNull = true)
	public static String DATABASE_PASSWORD;
	@Setting(name = "ConnectionPool", canNull = true)
	public static String DATABASE_CONNECTION_POOL;
	@Setting(name = "MaximumDbConnections")
	public static int DATABASE_MAX_CONNECTIONS;
	@Setting(name = "MaximumDbIdleTime")
	public static int DATABASE_MAX_IDLE_TIME;
	@Setting(name = "ConnectionCloseTime")
	public static long CONNECTION_CLOSE_TIME;
	@Setting(name = "ShowLicence")
	public static boolean SHOW_LICENCE;
	@Setting(name = "AutoCreateAccounts")
	public static boolean AUTO_CREATE_ACCOUNTS;
	@Setting(name = "EnableFloodProtection")
	public static boolean FLOOD_PROTECTION;
	@Setting(name = "FastConnectionLimit")
	public static int FAST_CONNECTION_LIMIT;
	@Setting(name = "NormalConnectionTime")
	public static int NORMAL_CONNECTION_TIME;
	@Setting(name = "FastConnectionTime")
	public static int FAST_CONNECTION_TIME;
	@Setting(name = "MaxConnectionPerIP")
	public static int MAX_CONNECTION_PER_IP;
	@Setting(name = "DatapackRoot", method = "datapackRoot")
	public static File DATAPACK_ROOT;
	
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
