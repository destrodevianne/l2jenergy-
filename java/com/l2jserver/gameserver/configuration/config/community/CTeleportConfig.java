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
package com.l2jserver.gameserver.configuration.config.community;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("community/communityTeleport.json")
public class CTeleportConfig
{
	@Setting(name = "CommunityEnableTeleports")
	public static boolean BBS_TELEPORTS_ENABLE;
	
	@Setting(name = "CommunityTeleportsPointForPremium")
	public static boolean BBS_TELEPORTS_POINT_FOR_PREMIUM;
	
	@Setting(name = "CommunityTeleportsMaxPointsCount")
	public static int BBS_TELEPORT_MAX_COUNT;
	
	@Setting(name = "CommunityTeleportsFreeByLevel", minValue = 1, maxValue = 86)
	public static int BBS_TELEPORT_FREE_LEVEL;
	
	@Setting(name = "CommunityTeleportsSaveItemId")
	public static int BBS_TELEPORT_SAVE_ITEM_ID;
	
	@Setting(name = "CommunityTeleportsSavePrice")
	public static int BBS_TELEPORT_SAVE_PRICE;
	
	@Setting(name = "CommunityTeleportsPremiumSaveItemId")
	public static int BBS_TELEPORT_PREMIUM_SAVE_ITEM_ID;
	
	@Setting(name = "CommunityTeleportPremiumSavePrice")
	public static int BBS_TELEPORT_PREMIUM_SAVE_PRICE;
}
