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
package com.l2jserver.gameserver.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public enum AnnouncementType
{
	NORMAL,
	CRITICAL,
	EVENT,
	AUTO_NORMAL,
	AUTO_CRITICAL;
	
	private static final Logger LOG = LoggerFactory.getLogger(AnnouncementType.class);
	
	public static AnnouncementType findById(int id)
	{
		for (AnnouncementType type : values())
		{
			if (type.ordinal() == id)
			{
				return type;
			}
		}
		LOG.warn("{}: Unexistent id specified: {}!", AnnouncementType.class.getSimpleName(), id, new IllegalStateException());
		return NORMAL;
	}
	
	public static AnnouncementType findByName(String name)
	{
		for (AnnouncementType type : values())
		{
			if (type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		LOG.warn("{}: Unexistent name specified: {}!", AnnouncementType.class.getSimpleName(), name, new IllegalStateException());
		return NORMAL;
	}
}
