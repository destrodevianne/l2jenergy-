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
package com.l2jserver.gameserver.configuration.config;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("manor.json")
public class ManorConfig
{
	@Setting(name = "AllowManor")
	public static boolean ALLOW_MANOR;
	
	@Setting(name = "AltManorRefreshTime")
	public static int ALT_MANOR_REFRESH_TIME;
	
	@Setting(name = "AltManorRefreshMin")
	public static int ALT_MANOR_REFRESH_MIN;
	
	@Setting(name = "AltManorApproveTime")
	public static int ALT_MANOR_APPROVE_TIME;
	
	@Setting(name = "AltManorApproveMin")
	public static int ALT_MANOR_APPROVE_MIN;
	
	@Setting(name = "AltManorMaintenanceMin")
	public static int ALT_MANOR_MAINTENANCE_MIN;
	
	@Setting(name = "AltManorSaveAllActions")
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	
	@Setting(name = "AltManorSavePeriodRate")
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
}
