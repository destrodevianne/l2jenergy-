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
package com.l2jserver.gameserver.configuration.config;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("mmo.json")
public class MMOConfig
{
	@Setting(name = "SleepTime")
	public static int MMO_SELECTOR_SLEEP_TIME;
	
	@Setting(name = "MaxSendPerPass")
	public static int MMO_MAX_SEND_PER_PASS;
	
	@Setting(name = "MaxReadPerPass")
	public static int MMO_MAX_READ_PER_PASS;
	
	@Setting(name = "HelperBufferCount")
	public static int MMO_HELPER_BUFFER_COUNT;
	
	@Setting(name = "TcpNoDelay")
	public static boolean MMO_TCP_NODELAY;
}
