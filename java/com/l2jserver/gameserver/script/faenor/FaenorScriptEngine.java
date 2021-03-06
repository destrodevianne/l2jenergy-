/*
 * Copyright (C) 2004-2020 L2jEnergy Server
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
package com.l2jserver.gameserver.script.faenor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.script.ScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.l2jserver.commons.util.filter.XMLFilter;
import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.script.Parser;
import com.l2jserver.gameserver.script.ParserNotCreatedException;
import com.l2jserver.gameserver.script.ScriptDocument;
import com.l2jserver.gameserver.script.ScriptEngine;

/**
 * @author Luis Arias
 */
public class FaenorScriptEngine extends ScriptEngine
{
	private static final Logger LOG = LoggerFactory.getLogger(FaenorScriptEngine.class);
	
	public static final String PACKAGE_DIRECTORY = "data/faenor/";
	
	protected FaenorScriptEngine()
	{
		final File packDirectory = new File(ServerConfig.DATAPACK_ROOT, PACKAGE_DIRECTORY);
		final File[] files = packDirectory.listFiles(new XMLFilter());
		if (files != null)
		{
			for (File file : files)
			{
				try (InputStream in = new FileInputStream(file))
				{
					parseScript(new ScriptDocument(file.getName(), in), null);
				}
				catch (IOException e)
				{
					LOG.warn("", e);
				}
			}
		}
	}
	
	public void parseScript(ScriptDocument script, ScriptContext context)
	{
		Node node = script.getDocument().getFirstChild();
		String parserClass = "faenor.Faenor" + node.getNodeName() + "Parser";
		
		Parser parser = null;
		try
		{
			parser = createParser(parserClass);
		}
		catch (ParserNotCreatedException e)
		{
			LOG.warn("ERROR: No parser registered for Script: {}", parserClass, e);
		}
		
		if (parser == null)
		{
			LOG.warn("Unknown Script Type: {}", script.getName());
			return;
		}
		
		try
		{
			parser.parseScript(node, context);
			LOG.info("{}: Loaded  {} successfully.", getClass().getSimpleName(), script.getName());
		}
		catch (Exception e)
		{
			LOG.warn("Script Parsing Failed!", e);
		}
	}
	
	public static FaenorScriptEngine getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FaenorScriptEngine _instance = new FaenorScriptEngine();
	}
}
