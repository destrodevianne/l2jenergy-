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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.TeleportWhereType;
import com.l2jserver.gameserver.enums.ZoneId;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.L2WorldRegion;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.type.NpcSpawnTerritory;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Small typo fix by Zoey76 24/02/2011
 */
public class AdminZone implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_zone_check",
		"admin_zone_visual",
		"admin_zone_visual_clear"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		// String val = "";
		// if (st.countTokens() >= 1) {val = st.nextToken();}
		
		if (actualCommand.equalsIgnoreCase("admin_zone_check"))
		{
			showHtml(activeChar);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_map_region").replace("%i%", MapRegionManager.getInstance().getMapRegionX(activeChar.getX()) + "").replace("%s%", MapRegionManager.getInstance().getMapRegionY(activeChar.getY())
				+ "").replace("%t%", MapRegionManager.getInstance().getMapRegionLocId(activeChar) + ""));
			getGeoRegionXY(activeChar);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_closest_town").replace("%i%", MapRegionManager.getInstance().getClosestTownName(activeChar) + ""));
			
			Location loc;
			
			loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CASTLE);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_teletolocation_castle").replace("%i%", loc.getX() + "").replace("%s%", loc.getY() + "").replace("%t%", loc.getZ() + ""));
			
			loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CLANHALL);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_teletolocation_clanhall").replace("%i%", loc.getX() + "").replace("%s%", loc.getY() + "").replace("%t%", loc.getZ() + ""));
			
			loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.SIEGEFLAG);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_teletolocation_siegeflag").replace("%i%", loc.getX() + "").replace("%s%", loc.getY() + "").replace("%t%", loc.getZ() + ""));
			
			loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_teletolocation_town").replace("%i%", loc.getX() + "").replace("%s%", loc.getY() + "").replace("%t%", loc.getZ() + ""));
		}
		else if (actualCommand.equalsIgnoreCase("admin_zone_visual"))
		{
			String next = st.nextToken();
			if (next.equalsIgnoreCase("all"))
			{
				for (L2ZoneType zone : ZoneManager.getInstance().getZones(activeChar))
				{
					zone.visualizeZone(activeChar.getZ());
				}
				for (NpcSpawnTerritory territory : ZoneManager.getInstance().getSpawnTerritories(activeChar))
				{
					territory.visualizeZone(activeChar.getZ());
				}
				showHtml(activeChar);
			}
			else
			{
				int zoneId = Integer.parseInt(next);
				ZoneManager.getInstance().getZoneById(zoneId).visualizeZone(activeChar.getZ());
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_zone_visual_clear"))
		{
			ZoneManager.getInstance().clearDebugItems();
			showHtml(activeChar);
		}
		return true;
	}
	
	private static void showHtml(L2PcInstance activeChar)
	{
		final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/zone.htm");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setHtml(htmContent);
		adminReply.replace("%PEACE%", (activeChar.isInsideZone(ZoneId.PEACE) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%PVP%", (activeChar.isInsideZone(ZoneId.PVP) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%SIEGE%", (activeChar.isInsideZone(ZoneId.SIEGE) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%TOWN%", (activeChar.isInsideZone(ZoneId.TOWN) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%CASTLE%", (activeChar.isInsideZone(ZoneId.CASTLE) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%FORT%", (activeChar.isInsideZone(ZoneId.FORT) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%HQ%", (activeChar.isInsideZone(ZoneId.HQ) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%CLANHALL%", (activeChar.isInsideZone(ZoneId.CLAN_HALL) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%LAND%", (activeChar.isInsideZone(ZoneId.LANDING) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%NOLAND%", (activeChar.isInsideZone(ZoneId.NO_LANDING) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%NOSUMMON%", (activeChar.isInsideZone(ZoneId.NO_SUMMON_FRIEND) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%WATER%", (activeChar.isInsideZone(ZoneId.WATER) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%SWAMP%", (activeChar.isInsideZone(ZoneId.SWAMP) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%DANGER%", (activeChar.isInsideZone(ZoneId.DANGER_AREA) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%NOSTORE%", (activeChar.isInsideZone(ZoneId.NO_STORE) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		adminReply.replace("%SCRIPT%", (activeChar.isInsideZone(ZoneId.SCRIPT) ? "<font color=\"LEVEL\">" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_yes") + "</font>" : "" + MessagesData.getInstance().getMessage(activeChar, "admin_htm_no") + ""));
		StringBuilder zones = new StringBuilder(100);
		L2WorldRegion region = L2World.getInstance().getRegion(activeChar.getX(), activeChar.getY());
		for (L2ZoneType zone : region.getZones())
		{
			if (zone.isCharacterInZone(activeChar))
			{
				if (zone.getName() != null)
				{
					StringUtil.append(zones, zone.getName() + "<br1>");
					if (zone.getId() < 300000)
					{
						StringUtil.append(zones, "(", String.valueOf(zone.getId()), ")");
					}
				}
				else
				{
					StringUtil.append(zones, String.valueOf(zone.getId()));
				}
				StringUtil.append(zones, " ");
			}
		}
		for (NpcSpawnTerritory territory : ZoneManager.getInstance().getSpawnTerritories(activeChar))
		{
			StringUtil.append(zones, territory.getName() + "<br1>");
		}
		adminReply.replace("%ZLIST%", zones.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private static void getGeoRegionXY(L2PcInstance activeChar)
	{
		int worldX = activeChar.getX();
		int worldY = activeChar.getY();
		int geoX = ((((worldX - (-327680)) >> 4) >> 11) + 10);
		int geoY = ((((worldY - (-262144)) >> 4) >> 11) + 10);
		activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_geo_region").replace("%i%", geoX + "").replace("%s%", geoY + ""));
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
