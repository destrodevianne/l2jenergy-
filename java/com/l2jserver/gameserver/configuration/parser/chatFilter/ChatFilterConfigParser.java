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
package com.l2jserver.gameserver.configuration.parser.chatFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Мо3олЬ
 */
public class ChatFilterConfigParser
{
	private static final Logger LOG = LoggerFactory.getLogger(ChatFilterConfigParser.class);
	
	private final String CHAT_FILTER_FILE = "configuration/chatFilter/chatfilter.txt";
	
	public List<String> FILTER_LIST;
	
	public void load()
	{
		
		try
		{
			//@formatter:off
				FILTER_LIST = Files.lines(Paths.get(CHAT_FILTER_FILE), StandardCharsets.UTF_8)
					.map(String::trim)
					.filter(line -> (!line.isEmpty() && (line.charAt(0) != '#')))
					.collect(Collectors.toList());
				//@formatter:on
			LOG.info("Loaded {} filter words.", FILTER_LIST.size());
		}
		catch (IOException ioe)
		{
			LOG.warn("Error while loading chat filter words!", ioe);
		}
	}
	
	public void reload()
	{
		load();
	}
	
	public static ChatFilterConfigParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChatFilterConfigParser INSTANCE = new ChatFilterConfigParser();
	}
}
