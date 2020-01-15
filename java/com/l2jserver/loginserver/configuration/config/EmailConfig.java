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

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("email.json")
public class EmailConfig
{
	@Setting(name = "ServerInfoName")
	public static String EMAIL_SERVERINFO_NAME;
	@Setting(name = "ServerInfoAddress")
	public static String EMAIL_SERVERINFO_ADDRESS;
	@Setting(name = "EmailSystemEnabled")
	public static boolean EMAIL_SYS_ENABLED;
	@Setting(name = "SmtpServerHost", canNull = true)
	public static String EMAIL_SYS_HOST;
	@Setting(name = "SmtpServerPort", canNull = true)
	public static int EMAIL_SYS_PORT;
	@Setting(name = "SmtpAuthRequired")
	public static boolean EMAIL_SYS_SMTP_AUTH;
	@Setting(name = "SmtpFactory", canNull = true)
	public static String EMAIL_SYS_FACTORY;
	@Setting(name = "SmtpFactoryCallback")
	public static boolean EMAIL_SYS_FACTORY_CALLBACK;
	@Setting(name = "SmtpUsername")
	public static String EMAIL_SYS_USERNAME;
	@Setting(name = "SmtpPassword", canNull = true)
	public static String EMAIL_SYS_PASSWORD;
	@Setting(name = "EmailSystemAddress")
	public static String EMAIL_SYS_ADDRESS;
	@Setting(name = "EmailDBSelectQuery", canNull = true)
	public static String EMAIL_SYS_SELECTQUERY;
	@Setting(name = "EmailDBField", canNull = true)
	public static String EMAIL_SYS_DBFIELD;
}
