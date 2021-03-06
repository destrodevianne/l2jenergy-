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
package com.l2jserver.gameserver.engines;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.filter.XMLFilter;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.engines.items.DocumentItem;
import com.l2jserver.gameserver.engines.skills.DocumentSkill;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * @author mkizub
 */
public class DocumentEngine
{
	private static final Logger LOG = LoggerFactory.getLogger(DocumentEngine.class);
	
	private final List<File> _itemFiles = new ArrayList<>();
	private final List<File> _skillFiles = new ArrayList<>();
	
	protected DocumentEngine()
	{
		hashFiles("data/xml/stats/items", _itemFiles);
		if (GeneralConfig.CUSTOM_ITEMS_LOAD)
		{
			hashFiles("data/xml/stats/items/custom", _itemFiles);
		}
		hashFiles("data/xml/stats/skills", _skillFiles);
		if (GeneralConfig.CUSTOM_SKILLS_LOAD)
		{
			hashFiles("data/xml/stats/skills/custom", _skillFiles);
		}
	}
	
	private void hashFiles(String dirname, List<File> hash)
	{
		File dir = new File(ServerConfig.DATAPACK_ROOT, dirname);
		if (!dir.exists())
		{
			LOG.warn("Dir {} not exists", dir.getAbsolutePath());
			return;
		}
		
		final File[] files = dir.listFiles(new XMLFilter());
		if (files != null)
		{
			for (File f : files)
			{
				hash.add(f);
			}
		}
	}
	
	public List<Skill> loadSkills(File file)
	{
		if (file == null)
		{
			LOG.warn("Skill file not found.");
			return null;
		}
		DocumentSkill doc = new DocumentSkill(file);
		doc.parse();
		return doc.getSkills();
	}
	
	public void loadAllSkills(final Map<Integer, Skill> allSkills)
	{
		int count = 0;
		for (File file : _skillFiles)
		{
			List<Skill> s = loadSkills(file);
			if (s == null)
			{
				continue;
			}
			for (Skill skill : s)
			{
				allSkills.put(SkillData.getSkillHashCode(skill), skill);
				count++;
			}
		}
		LOG.info("{}: Loaded {} Skill templates from XML files.", getClass().getSimpleName(), count);
	}
	
	/**
	 * Return created items
	 * @return List of {@link L2Item}
	 */
	public List<L2Item> loadItems()
	{
		List<L2Item> list = new ArrayList<>();
		for (File f : _itemFiles)
		{
			DocumentItem document = new DocumentItem(f);
			document.parse();
			list.addAll(document.getItemList());
		}
		return list;
	}
	
	public static DocumentEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DocumentEngine INSTANCE = new DocumentEngine();
	}
}
