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
package com.l2jserver.gameserver.configuration.config.protection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;

/**
 * @author Мо3олЬ
 */
@Configuration("protection/baseSetting.json")
public class BaseProtectionConfig
{
	@Setting(ignore = true)
	private static final Logger LOG = LoggerFactory.getLogger(BaseProtectionConfig.class);
	
	@Setting(name = "AntiFeedEnable")
	public static boolean ANTIFEED_ENABLE;
	
	@Setting(name = "AntiFeedDualbox")
	public static boolean ANTIFEED_DUALBOX;
	
	@Setting(name = "AntiFeedDisconnectedAsDualbox")
	public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
	
	@Setting(name = "AntiFeedInterval", increase = 1000)
	public static int ANTIFEED_INTERVAL;
	
	@Setting(name = "L2WalkerProtection")
	public static boolean L2WALKER_PROTECTION;
	
	@Setting(name = "DualboxCheckMaxPlayersPerIP")
	public static int DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
	
	@Setting(name = "DualboxCheckMaxOlympiadParticipantsPerIP")
	public static int DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	
	@Setting(name = "DualboxCheckMaxL2EventParticipantsPerIP")
	public static int DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
	
	@Setting(name = "DualboxCheckWhitelist", method = "whitelist")
	public static Map<Integer, Integer> DUALBOX_CHECK_WHITELIST;
	
	public void whitelist(final String value)
	{
		final String[] dualboxCheckWhiteList = value.split(";");
		DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
		
		for (String entry : dualboxCheckWhiteList)
		{
			String[] entrySplit = entry.split(",");
			if (entrySplit.length != 2)
			{
				LOG.warn("DualboxCheck[BaseProtectionConfig.load()]: invalid config property -> DualboxCheckWhitelist {}", entry);
			}
			else
			{
				try
				{
					int num = Integer.parseInt(entrySplit[1]);
					num = (num == 0) ? -1 : num;
					DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
				}
				catch (UnknownHostException e)
				{
					LOG.warn("DualboxCheck[BaseProtectionConfig.load()]: invalid address -> DualboxCheckWhitelist {}", entrySplit[0]);
				}
				catch (NumberFormatException e)
				{
					LOG.warn("DualboxCheck[BaseProtectionConfig.load()]: invalid number -> DualboxCheckWhitelist {}", entrySplit[1]);
				}
			}
		}
	}
}
