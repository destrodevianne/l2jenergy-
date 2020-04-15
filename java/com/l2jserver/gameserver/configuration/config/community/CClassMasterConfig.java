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
package com.l2jserver.gameserver.configuration.config.community;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

@Configuration("community/communityClassMaster.json")
public class CClassMasterConfig
{
	@Setting(name = "CommunityClassMasterEnable")
	public static boolean ALLOW_CLASS_MASTERS;
	
	@Setting(name = "CommunityClassMasterLevel", splitter = ",", method = "classLevel")
	public static List<Integer> ALLOW_CLASS_MASTERS_LIST = new ArrayList<>();
	
	@Setting(name = "CommunityClassMasterItemId", splitter = ",")
	public static int[] ALLOW_CLASS_MASTERS_PRICE_ITEM_ID;
	
	@Setting(name = "CommunityClassMasterCount", splitter = ",")
	public static long[] ALLOW_CLASS_MASTERS_PRICE_COUNT;
	
	@Setting(name = "CommunityClassMasterSecondItemId", splitter = ",")
	public static int[] ALLOW_CLASS_MASTERS_PRICE_SECOND_ITEM_ID;
	
	@Setting(name = "CommunityClassMasterSecondCount", splitter = ",")
	public static long[] ALLOW_CLASS_MASTERS_PRICE_SECOND_COUNT;
	
	public void classLevel(final int[] value)
	{
		for (final int cLevel : value)
		{
			if (cLevel != 0)
			{
				ALLOW_CLASS_MASTERS_LIST.add(cLevel);
			}
		}
	}
}
