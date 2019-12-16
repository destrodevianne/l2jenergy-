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
package com.l2jserver.gameserver.configuration.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.util.Util;

/**
 * @author Мо3олЬ
 */
@Configuration("geoData.json")
public class GeoDataConfig
{
	@Setting(ignore = true)
	private static final Logger LOG = LoggerFactory.getLogger(GeoDataConfig.class);
	
	@Setting(name = "PathFinding")
	public static int PATHFINDING;
	
	@Setting(name = "PathnodeDirectory", method = "pathnode")
	public static File PATHNODE_DIR;
	
	@Setting(name = "PathFindBuffers")
	public static String PATHFIND_BUFFERS;
	
	@Setting(name = "LowWeight")
	public static float LOW_WEIGHT;
	
	@Setting(name = "MediumWeight")
	public static float MEDIUM_WEIGHT;
	
	@Setting(name = "HighWeight")
	public static float HIGH_WEIGHT;
	
	@Setting(name = "AdvancedDiagonalStrategy")
	public static boolean ADVANCED_DIAGONAL_STRATEGY;
	
	@Setting(name = "DiagonalWeight")
	public static float DIAGONAL_WEIGHT;
	
	@Setting(name = "MaxPostfilterPasses")
	public static int MAX_POSTFILTER_PASSES;
	
	@Setting(name = "DebugPath")
	public static boolean DEBUG_PATH;
	
	@Setting(name = "ForceGeoData")
	public static boolean FORCE_GEODATA;
	
	@Setting(name = "CoordSynchronize")
	public static int COORD_SYNCHRONIZE;
	
	@Setting(name = "GeoDataPath", method = "geodata")
	public static Path GEODATA_PATH;
	
	@Setting(name = "TryLoadUnspecifiedRegions")
	public static boolean TRY_LOAD_UNSPECIFIED_REGIONS;
	
	@Setting(name = "region", method = "region2", canNull = true)
	public static Map<String, Boolean> GEODATA_REGIONS;
	
	public void pathnode(final String value)
	{
		try
		{
			PATHNODE_DIR = new File(value).getCanonicalFile();
		}
		catch (IOException e)
		{
			LOG.warn("Error setting pathnode directory!", e);
			PATHNODE_DIR = new File("data/pathnode");
		}
	}
	
	public void geodata(final String value)
	{
		GEODATA_PATH = Paths.get(value);
	}
	
	public void region(String value)
	{
		GEODATA_REGIONS = Util.parseConfigMap(value, String.class, Boolean.class);
	}
	
	public void region2(final String value) // TODO: test
	{
		GEODATA_REGIONS = new HashMap<>();
		for (int regionX = L2World.TILE_X_MIN; regionX <= L2World.TILE_X_MAX; regionX++)
		{
			for (int regionY = L2World.TILE_Y_MIN; regionY <= L2World.TILE_Y_MAX; regionY++)
			{
				String key = regionX + "_" + regionY;
				if (value.contains(regionX + "_" + regionY))
				{
					GEODATA_REGIONS.put(key, false);
				}
			}
		}
	}
}
