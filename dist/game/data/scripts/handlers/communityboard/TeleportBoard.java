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
package handlers.communityboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.configuration.config.community.CBasicConfig;
import com.l2jserver.gameserver.configuration.config.community.CTeleportConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.data.xml.impl.TeleportBBSData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;
import com.l2jserver.gameserver.util.DifferentMethods;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.gameserver.util.bbs.TeleportPoint;

public class TeleportBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbsteleport"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		if (!CTeleportConfig.BBS_TELEPORTS_ENABLE)
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbshome", activeChar);
			return false;
		}
		
		String html = null;
		if (command.equals("_bbsteleport"))
		{
			final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "teleport/index.html");
		}
		else if (command.startsWith("_bbsteleport:page"))
		{
			final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
			final String[] path = command.split(":");
			if (path.length > 3)
			{
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "teleport/" + path[2] + "/" + path[3] + ".html");
			}
			else
			{
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "teleport/" + path[2] + ".html");
			}
		}
		else if (command.equals("_bbsteleport:save_page"))
		{
			showTeleportPoint(activeChar);
			return true;
		}
		else if (command.startsWith("_bbsteleport:delete"))
		{
			deleteTeleportPoint(activeChar, Integer.parseInt(command.split(":")[2]));
			showTeleportPoint(activeChar);
			return true;
		}
		else if (command.startsWith("_bbsteleport:save"))
		{
			String point = "";
			final String[] next = command.split(" ");
			if (next.length > 1)
			{
				for (int i = 1; i < next.length; i++)
				{
					point += " " + next[i];
				}
			}
			if (point.length() > 0)
			{
				addTeleportPoint(activeChar, point);
			}
			showTeleportPoint(activeChar);
			return true;
		}
		else if (command.startsWith("_bbsteleport:id"))
		{
			final int id = Integer.parseInt(command.split(":")[2]);
			final Optional<TeleportPoint> point = TeleportBBSData.getInstance().getTeleportId(id);
			if (point.isPresent())
			{
				goToTeleportID(activeChar, point.get());
			}
			activeChar.sendPacket(new ShowBoard());
		}
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}
	
	private void goToTeleportID(final L2PcInstance player, final TeleportPoint teleportPoint)
	{
		final int level = player.getLevel();
		final String name = teleportPoint.getName();
		final int priceId = teleportPoint.getPriceId();
		final int count = teleportPoint.getPriceCount();
		final int minLevel = teleportPoint.getMinLevel();
		final int maxLevel = teleportPoint.getMaxLevel();
		final boolean pk = teleportPoint.isPk();
		final boolean premium = teleportPoint.isPremium();
		final int premiumPriceId = teleportPoint.getPremiumPriceId();
		final int premiumCount = teleportPoint.getPremiumPriceCount();
		final Location location = teleportPoint.getLocation();
		final boolean isConfirm = false; // TODO: Не включать
		
		if ((level < minLevel) || (level > maxLevel))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "communityboard_teleport_point_level_min_max").replace("%s%", minLevel + "").replace("%c%", maxLevel + ""));
			return;
		}
		if ((pk) && (player.getKarma() > 0))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "communityboard_teleport_point_pk_denied"));
			return;
		}
		if ((premium) && (!player.isPremium()))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "communityboard_teleport_personal_point_only_premium"));
			return;
		}
		if (!DifferentMethods.checkFirstConditions(player))
		{
			return;
		}
		
		final int item = player.isPremium() ? premiumPriceId : priceId;
		final int price = player.isPremium() ? premiumCount : count;
		final boolean freeLevel = player.getLevel() <= CTeleportConfig.BBS_TELEPORT_FREE_LEVEL;
		
		if (isConfirm)
		{
			teleportByAsk(player, teleportPoint, item, price);
		}
		else if (DifferentMethods.getPay(player, item, freeLevel ? 0 : price))
		{
			player.teleToLocation(location);
			player.sendMessage(MessagesData.getInstance().getMessage(player, "communityboard_teleport_point_success_location").replace("%s%", name + ""));
		}
	}
	
	private void teleportByAsk(L2PcInstance player, TeleportPoint tp, int priceId, int priceCount) // TODO: Доработать
	{
		String itemName = DifferentMethods.getItemName(priceId);
		ConfirmDlg ask = new ConfirmDlg(SystemMessageId.S1.getId());
		ask.addString("Желаете ли вы телепортироваться за " + priceCount + " " + itemName + "?");
		ask.addTime(30000);
		player.sendPacket(ask);
	}
	
	// TODO: Доработать
	private void showTeleportPoint(L2PcInstance activeChar)
	{
		if (CTeleportConfig.BBS_TELEPORTS_POINT_FOR_PREMIUM && !activeChar.isPremium())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_personal_point_only_premium"));
			parseCommunityBoardCommand("_bbsteleport", activeChar);
			return;
		}
		
		String points = "";
		StringBuilder html = new StringBuilder();
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM bbs_teleport WHERE charId=?;"))
		{
			ps.setLong(1, activeChar.getObjectId());
			try (ResultSet rset = ps.executeQuery())
			{
				html.append("<table width=220>");
				while (rset.next())
				{
					TeleportPoint tp = new TeleportPoint(rset.getInt("id"), rset.getString("name"), 57, 0, 1, 85, false, false, 57, 0, new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")));
					
					html.append("<tr>");
					html.append("<td>");
					html.append("<button value=\"" + tp.getName() + "\" action=\"bypass _bbsteleport:id " + tp.getId() + " " + 100000 + "\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					html.append("</td>");
					html.append("<td>");
					html.append("<button value=\"" + MessagesData.getInstance().getMessage(activeChar, "admin_button_delete") + "\" action=\"bypass _bbsteleport:delete " + tp.getId() + "\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					html.append("</td>)");
					html.append("</tr>");
				}
			}
			html.append("</table>");
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
		
		final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
		String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "teleport/save.html");
		content = content.replace("{points}", points.equals("") ? "<center><font color=\"FF0000\">" + MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_dont_have_points") + "</font></center>" : points);
		content = content.replace("{all_price}", Util.formatAdena(CTeleportConfig.BBS_TELEPORT_SAVE_PRICE) + " " + String.valueOf(DifferentMethods.getItemName(CTeleportConfig.BBS_TELEPORT_SAVE_ITEM_ID)));
		content = content.replace("{premium_price}", Util.formatAdena(CTeleportConfig.BBS_TELEPORT_PREMIUM_SAVE_PRICE) + " " + String.valueOf(DifferentMethods.getItemName(CTeleportConfig.BBS_TELEPORT_PREMIUM_SAVE_ITEM_ID)));
		content = content.replace("{point_count}", String.valueOf(CTeleportConfig.BBS_TELEPORT_MAX_COUNT));
		CommunityBoardHandler.separateAndSend(content, activeChar);
		return;
	}
	
	// TODO: Доработать
	private void deleteTeleportPoint(final L2PcInstance activeChar, final int id)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM bbs_teleport WHERE charId=? AND TpId=?;"))
		{
			ps.setInt(1, activeChar.getObjectId());
			ps.setInt(2, id);
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
	}
	
	// TODO: Доработать
	private void addTeleportPoint(L2PcInstance activeChar, final String point)
	{
		if (!DifferentMethods.getPay(activeChar, CTeleportConfig.BBS_TELEPORT_SAVE_ITEM_ID, CTeleportConfig.BBS_TELEPORT_SAVE_PRICE))
		{
			return;
		}
		
		if (!DifferentMethods.checkFirstConditions(activeChar))
		{
			return;
		}
		
		if (CTeleportConfig.BBS_TELEPORTS_POINT_FOR_PREMIUM && !activeChar.isPremium())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_personal_point_only_premium"));
			return;
		}
		
		if (activeChar.isMovementDisabled() || activeChar.isOutOfControl())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_persoanl_point_outofcontrol"));
			return;
		}
		
		if (activeChar.isInCombat())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_persoanl_point_incombat"));
			return;
		}
		
		if (activeChar.isCursedWeaponEquipped() || activeChar.isJailed() || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isAttackingNow() || activeChar.isOlympiadStart())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_persoanl_point_state"));
			return;
		}
		
		if (point.equals("") || point.equals(null))
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_persoanl_point_name"));
			return;
		}
		
		if (activeChar.isInsideZone(ZoneId.CASTLE) || activeChar.isInsideZone(ZoneId.CLAN_HALL) || activeChar.isInsideZone(ZoneId.FORT) || activeChar.isInsideZone(ZoneId.NO_SUMMON_FRIEND) || activeChar.isInsideZone(ZoneId.SIEGE) || activeChar.isInsideZone(ZoneId.WATER)
			|| activeChar.isInsideZone(ZoneId.JAIL))
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_persoanl_point_forbidden_zone"));
			return;
		}
		
		try (Connection con = ConnectionFactory.getInstance().getConnection())
		{
			PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM bbs_teleport WHERE charId=?;");
			stmt.setLong(1, activeChar.getObjectId());
			try (ResultSet rset = stmt.executeQuery())
			{
				if (rset.next())
				{
					if (rset.getInt(1) < CTeleportConfig.BBS_TELEPORT_MAX_COUNT)
					{
						stmt = con.prepareStatement("SELECT COUNT(*) FROM bbs_teleport WHERE charId=? AND name=?;");
						stmt.setLong(1, activeChar.getObjectId());
						stmt.setString(2, point);
						try (ResultSet rset1 = stmt.executeQuery())
						{
							if (rset1.next())
							{
								stmt = con.prepareStatement(rset1.getInt(1) == 0 ? "INSERT INTO bbs_teleport (charId, xPos, yPos, zPos, name) VALUES(?,?,?,?,?)" : "UPDATE bbs_teleport SET xPos=?, yPos=?, zPos=? WHERE charId=? AND name=?;");
								stmt.setInt(1, activeChar.getObjectId());
								stmt.setInt(2, activeChar.getX());
								stmt.setInt(3, activeChar.getY());
								stmt.setInt(4, activeChar.getZ());
								stmt.setString(5, point);
								stmt.execute();
							}
						}
					}
				}
				else
				{
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "communityboard_teleport_personal_point").replace("%s%", CTeleportConfig.BBS_TELEPORT_MAX_COUNT + ""));
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
	}
	
	public void writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		
	}
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
}