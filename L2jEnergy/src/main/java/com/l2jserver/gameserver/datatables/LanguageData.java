/*
 * Copyright (C) 2015-2016 L2JDevs
 *
 * This file is part of L2JDevs.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.datatables;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author U3Games
 */
public final class LanguageData
{
	// Logger Class
	private static final Logger LOGGER = Logger.getLogger(LanguageData.class.getName());
	
	// Default
	private static final String DIRECTORY = "config/Language";
	private static final String DEFAULT_LANG = "en";
	
	// Maps
	private final Map<L2PcInstance, String> _playerCurrentLang = new HashMap<>();
	private final Map<String, String> _msgMap = new HashMap<>();
	private final Map<String, String> _languages = new HashMap<>();
	
	// Mist
	private static Document doc;
	private static Node n;
	
	public LanguageData()
	{
		load();
	}
	
	private void load()
	{
		try
		{
			File dir = new File(DIRECTORY);
			
			for (File file : dir.listFiles((FileFilter) pathname ->
			{
				if (pathname.getName().endsWith(".xml"))
				{
					return true;
				}
				return false;
			}))
			{
				if (file.getName().startsWith("lang_"))
				{
					loadXml(file, file.getName().substring(5, file.getName().indexOf(".xml")));
				}
			}
			
			LOGGER.info(LanguageData.class.getSimpleName() + ": Loaded " + _languages.size() + " languages.");
		}
		catch (Exception e)
		{
			LOGGER.warning(LanguageData.class.getSimpleName() + ": -> Error while loading language files: " + e);
			e.printStackTrace();
		}
	}
	
	private void loadXml(File file, String lang)
	{
		int count = 0;
		String langName = "";
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		doc = null;
		if (file.exists())
		{
			try
			{
				doc = factory.newDocumentBuilder().parse(file);
			}
			catch (Exception e)
			{
				LOGGER.warning(LanguageData.class.getSimpleName() + ": -> Could not load language (" + lang + ") file for event engine: " + e);
				e.printStackTrace();
			}
			
			n = doc.getFirstChild();
			NamedNodeMap docAttr = n.getAttributes();
			if (docAttr.getNamedItem("lang") != null)
			{
				langName = docAttr.getNamedItem("lang").getNodeValue();
			}
			
			if (!_languages.containsKey(lang))
			{
				_languages.put(lang, langName);
			}
			
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equals("message"))
				{
					NamedNodeMap attrs = d.getAttributes();
					String id = attrs.getNamedItem("id").getNodeValue();
					String text = attrs.getNamedItem("text").getNodeValue();
					
					_msgMap.put(lang + "_" + id, text);
					count++;
				}
			}
		}
		
		LOGGER.info("Loaded language file for language: " + lang.toUpperCase() + " with " + count + " messages.");
	}
	
	/**
	 * @param player
	 * @param text
	 * @return String
	 */
	public String getMsgByLang(L2PcInstance player, String text)
	{
		String lang = getLanguage(player);
		
		StringBuilder msg = new StringBuilder(50);
		StringTokenizer st = new StringTokenizer(text, " ");
		
		while (st.hasMoreTokens())
		{
			String textLang = st.nextToken();
			if (_msgMap.containsKey(lang + "_" + textLang))
			{
				msg.append(_msgMap.get(lang + "_" + textLang));
			}
			else if (_msgMap.containsKey(lang + "_" + textLang))
			{
				msg.append(_msgMap.get("en_" + textLang));
			}
			else
			{
				msg.append(textLang);
			}
		}
		
		return msg.toString();
	}
	
	/**
	 * @param player
	 * @param text
	 * @return String
	 */
	public String getMsgByLang(L2Character character, String text)
	{
		String lang = getLanguage(character);
		
		StringBuilder msg = new StringBuilder(50);
		StringTokenizer st = new StringTokenizer(text, " ");
		
		while (st.hasMoreTokens())
		{
			String textLang = st.nextToken();
			if (_msgMap.containsKey(lang + "_" + textLang))
			{
				msg.append(_msgMap.get(lang + "_" + textLang));
			}
			else if (_msgMap.containsKey(lang + "_" + textLang))
			{
				msg.append(_msgMap.get("en_" + textLang));
			}
			else
			{
				msg.append(textLang);
			}
		}
		
		return msg.toString();
	}
	
	/**
	 * Define the language a character wants.
	 * @param player
	 * @param lang
	 */
	public void setLanguage(L2PcInstance player, String lang)
	{
		_playerCurrentLang.put(player, lang);
	}
	
	/**
	 * @param player
	 * @return String
	 */
	public String getLanguage(L2PcInstance player)
	{
		if (_playerCurrentLang.containsKey(player))
		{
			return _playerCurrentLang.get(player);
		}
		return DEFAULT_LANG;
	}
	
	/**
	 * @param character
	 * @return String
	 */
	public String getLanguage(L2Character character)
	{
		if (_playerCurrentLang.containsKey(character))
		{
			return _playerCurrentLang.get(character);
		}
		return DEFAULT_LANG;
	}
	
	/**
	 * Get a map with all languages were created.
	 * @return
	 */
	public Map<String, String> getLanguages()
	{
		return _languages;
	}
	
	public static LanguageData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final LanguageData _instance = new LanguageData();
	}
}