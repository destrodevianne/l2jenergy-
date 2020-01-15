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

import java.time.ZoneId;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("community/communityBasic.json")
public class CBasicConfig
{
	@Setting(name = "AllowCommunityBoard")
	public static boolean ENABLE_COMMUNITY_BOARD;
	
	@Setting(name = "BBSDefault", canNull = true)
	public static String BBS_DEFAULT;
	
	@Setting(name = "CustomCommunityBoard")
	public static boolean CUSTOM_CB_ENABLED;
	
	@Setting(name = "TimeZone", method = "timeZoneId")
	public static ZoneId timeZoneId;
	
	public void timeZoneId(final String value)
	{
		timeZoneId = ZoneId.of(value);
	}
}
