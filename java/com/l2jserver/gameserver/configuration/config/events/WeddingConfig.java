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
package com.l2jserver.gameserver.configuration.config.events;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("events/wedding.json")
public class WeddingConfig
{
	@Setting(name = "AllowWedding")
	public static boolean ALLOW_WEDDING;
	@Setting(name = "WeddingPrice")
	public static int WEDDING_PRICE;
	@Setting(name = "WeddingPunishInfidelity")
	public static boolean WEDDING_PUNISH_INFIDELITY;
	@Setting(name = "WeddingTeleport")
	public static boolean WEDDING_TELEPORT;
	@Setting(name = "WeddingTeleportPrice")
	public static int WEDDING_TELEPORT_PRICE;
	@Setting(name = "WeddingTeleportDuration")
	public static int WEDDING_TELEPORT_DURATION;
	@Setting(name = "WeddingAllowSameSex")
	public static boolean WEDDING_SAMESEX;
	@Setting(name = "WeddingFormalWear")
	public static boolean WEDDING_FORMALWEAR;
	@Setting(name = "WeddingDivorceCosts")
	public static int WEDDING_DIVORCE_COSTS;
}
