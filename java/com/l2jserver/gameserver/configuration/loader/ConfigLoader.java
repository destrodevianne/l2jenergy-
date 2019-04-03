package com.l2jserver.gameserver.configuration.loader;

import com.l2jserver.gameserver.configuration.parser.IPConfigDataParser;
import com.l2jserver.gameserver.configuration.parser.chatFilter.ChatFilterConfigParser;
import com.l2jserver.gameserver.configuration.parser.hexid.HexidConfigParser;

/**
 * Create by Mangol on 24.11.2015.
 */
public class ConfigLoader
{
	private static final com.l2jserver.commons.configuration.ConfigLoader loader = com.l2jserver.commons.configuration.ConfigLoader.createConfigLoader();
	
	public static void loading()
	{
		loader.load();
		IPConfigDataParser.getInstance().load();
		ChatFilterConfigParser.getInstance().load();
		HexidConfigParser.getInstance().load();
	}
	
	public static void reloading()
	{
		loading();
	}
}
