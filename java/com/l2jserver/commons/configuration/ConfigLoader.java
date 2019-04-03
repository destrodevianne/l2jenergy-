/*
 * Copyright (C) 2004-2019 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.util.ReflectionUtils;

/**
 * @author Java-man
 */
public class ConfigLoader
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	/**
	 * Конфиги для кастомных расширений клиентов. Не показываем варнинги об их отсутствии TODO: вынести в аннотацию конфига
	 */
	private final String[] clientCustoms =
	{
		"configuration\\custom\\custom.json", // EVA
		"configuration\\LostDreamCustom.json", // DarkEmpire
		"configuration\\phantoms.json", // DarkEmpire
		"configuration\\customBossSpawn.json" // DarkEmpire
	};
	
	private ConfigLoader()
	{
	}
	
	public static ConfigLoader createConfigLoader()
	{
		return new ConfigLoader();
	}
	
	public void load()
	{
		Reflections reflections = new Reflections("com.l2jserver");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Configuration.class);
		ReflectionUtils.loadClassesWithConsumer(classes, object ->
		{
			Class<?> clazz = object.getClass();
			Configuration annotation = clazz.getAnnotation(Configuration.class);
			String path = annotation.value();
			Path configurationPath = Paths.get("configuration").resolve(path);
			
			if (Files.notExists(configurationPath))
			{
				if (!Arrays.asList(clientCustoms).contains(configurationPath.toString()))
				{
					LOG.warn("File {} not exists", configurationPath);
				}
				return;
			}
			
			Map<Object, Object> objectMap = new HashMap<>();
			try (BufferedReader reader = Files.newBufferedReader(configurationPath))
			{
				read(clazz.getSimpleName(), objectMap, reader);
			}
			catch (Exception e)
			{
				LOG.warn("Exception: {}", e, e);
			}
			configurationClassWrite(clazz, objectMap);
		});
	}
	
	private void read(String className, Map<Object, Object> objectMap, BufferedReader reader) throws IOException
	{
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.enable(Feature.ALLOW_YAML_COMMENTS);
		JsonParser parser = jsonFactory.createParser(reader);
		
		LOG.info("Loading {} config.", className);
		ObjectMapper mapper = new ObjectMapper();
		readElement(mapper.readTree(parser), objectMap);
	}
	
	private void readElement(final ObjectNode node, Map<Object, Object> objectMap)
	{
		node.fields().forEachRemaining(m -> addObject(objectMap, m.getKey(), m.getValue().textValue()));
	}
	
	private void configurationClassWrite(Class<?> clazz, Map<Object, Object> objectMap)
	{
		final ConfigParser configParser = ConfigParser.createConfigParser(clazz, objectMap);
		configParser.parse();
		objectMap.clear();
	}
	
	private void addObject(Map<Object, Object> objectMap, final Object name, final Object value)
	{
		if (objectMap.containsKey(name))
		{
			LOG.error("object map contains! key - {} rename key!", name);
		}
		else
		{
			objectMap.put(name, value);
		}
	}
}
