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
