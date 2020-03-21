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
package com.l2jserver.gameserver.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Util;

/**
 * Audits Game Master's actions.
 */
public class GMAudit
{
	private static final Logger LOG = LoggerFactory.getLogger(GMAudit.class);
	
	static
	{
		new File("logs/GMAudit").mkdirs();
	}
	
	/**
	 * Logs a Game Master's action into a file.
	 * @param gmName the Game Master's name
	 * @param action the performed action
	 * @param target the target's name
	 * @param params the parameters
	 */
	public static void auditGMAction(String gmName, String action, String target, String params)
	{
		final SimpleDateFormat _formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
		final String date = _formatter.format(new Date());
		String name = Util.replaceIllegalCharacters(gmName);
		if (!Util.isValidFileName(name))
		{
			name = "INVALID_GM_NAME_" + date;
		}
		
		final File file = new File("logs/GMAudit/" + name + ".txt");
		try (FileWriter save = new FileWriter(file, true))
		{
			save.write(date + ">" + gmName + ">" + action + ">" + target + ">" + params + System.lineSeparator());
		}
		catch (IOException e)
		{
			LOG.error("GMAudit for GM {} could not be saved!", gmName, e);
		}
	}
	
	/**
	 * Wrapper method.
	 * @param gmName the Game Master's name
	 * @param action the performed action
	 * @param target the target's name
	 */
	public static void auditGMAction(String gmName, String action, String target)
	{
		auditGMAction(gmName, action, target, "");
	}
}