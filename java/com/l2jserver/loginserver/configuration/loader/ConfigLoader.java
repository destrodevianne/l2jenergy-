package com.l2jserver.loginserver.configuration.loader;

public class ConfigLoader
{
	public static void loading()
	{
		com.l2jserver.commons.configuration.ConfigLoader loader = com.l2jserver.commons.configuration.ConfigLoader.createConfigLoader();
		loader.load();
	}
}
