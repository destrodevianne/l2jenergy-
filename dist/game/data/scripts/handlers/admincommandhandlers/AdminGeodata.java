/*
 * Copyright (C) 2004-2019 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.util.GeoUtils;

/**
 * @author -Nemesiss-, HorridoJoho
 */
public class AdminGeodata implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_geo_pos",
		"admin_geo_spawn_pos",
		"admin_geo_can_move",
		"admin_geo_can_see",
		"admin_geogrid",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_geo_pos":
			{
				final int worldX = activeChar.getX();
				final int worldY = activeChar.getY();
				final int worldZ = activeChar.getZ();
				final int geoX = GeoData.getInstance().getGeoX(worldX);
				final int geoY = GeoData.getInstance().getGeoY(worldY);
				
				if (GeoData.getInstance().hasGeoPos(geoX, geoY))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_world_x_y_z_geo_x_y_z").replace("%i%", worldX + "").replace("%s%", worldY + "").replace("%c%", worldZ + "").replace("%t%", geoX + "").replace("%y%", geoY
						+ "").replace("%z%", GeoData.getInstance().getNearestZ(geoX, geoY, worldZ) + ""));
				}
				else
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_there_no_geodata_position"));
				}
				break;
			}
			case "admin_geo_spawn_pos":
			{
				final int worldX = activeChar.getX();
				final int worldY = activeChar.getY();
				final int worldZ = activeChar.getZ();
				final int geoX = GeoData.getInstance().getGeoX(worldX);
				final int geoY = GeoData.getInstance().getGeoY(worldY);
				
				if (GeoData.getInstance().hasGeoPos(geoX, geoY))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_world_x_y_z_geo_x_y_z").replace("%i%", worldX + "").replace("%s%", worldY + "").replace("%c%", worldZ + "").replace("%t%", geoX + "").replace("%y%", geoY
						+ "").replace("%z%", GeoData.getInstance().getSpawnHeight(worldX, worldY, worldZ) + ""));
				}
				else
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_there_no_geodata_position"));
				}
				break;
			}
			case "admin_geo_can_move":
			{
				final L2Object target = activeChar.getTarget();
				if (target != null)
				{
					if (GeoData.getInstance().canSeeTarget(activeChar, target))
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_can_move_beeline"));
					}
					else
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_can_not_move_beeline"));
					}
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
				break;
			}
			case "admin_geo_can_see":
			{
				final L2Object target = activeChar.getTarget();
				if (target != null)
				{
					if (GeoData.getInstance().canSeeTarget(activeChar, target))
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_can_see_target"));
					}
					else
					{
						activeChar.sendPacket(SystemMessageId.CANT_SEE_TARGET);
					}
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
				break;
			}
			case "admin_geogrid":
			{
				GeoUtils.debugGrid(activeChar);
				break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
