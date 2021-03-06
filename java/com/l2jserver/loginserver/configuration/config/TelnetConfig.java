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
package com.l2jserver.loginserver.configuration.config;

import java.util.List;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("telnet.json")
public class TelnetConfig
{
	@Setting(name = "EnableTelnet")
	public static boolean IS_TELNET_ENABLED;
	@Setting(name = "Port", canNull = true)
	public static int TELNET_PORT;
	@Setting(name = "Password", canNull = true)
	public static String TELNET_PASSWORD;
	@Setting(name = "ListOfHosts", splitter = ",")
	public static List<String> TELNET_HOSTS; // TODO: fix
}
