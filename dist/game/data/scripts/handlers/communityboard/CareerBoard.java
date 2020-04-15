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
package handlers.communityboard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.configuration.config.community.CBasicConfig;
import com.l2jserver.gameserver.configuration.config.community.CClassMasterConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.actors.ClassId;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.util.DifferentMethods;

public class CareerBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbscareer"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance player)
	{
		if (!CClassMasterConfig.ALLOW_CLASS_MASTERS)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbshome", player);
			return false;
		}
		if (command.equals("_bbscareer"))
		{
			showClassPage(player);
		}
		else if (command.startsWith("_bbscareer:change_class"))
		{
			final String[] data = command.split(":");
			changeClass(player, Integer.parseInt(data[2]), Integer.parseInt(data[3]), 0);
		}
		return true;
	}
	
	public void showClassPage(final L2PcInstance player)
	{
		if (!CClassMasterConfig.ALLOW_CLASS_MASTERS)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbshome", player);
			return;
		}
		final ClassId classId = player.getClassId();
		int jobLevel = classId.level();
		final int level = player.getLevel();
		String html = "";
		final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
		
		if (CClassMasterConfig.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !CClassMasterConfig.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
		{
			jobLevel = 4;
		}
		String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "classMaster/index.html");
		String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "classMaster/template.html");
		String block;
		if (((level >= 20) && (jobLevel == 1)) || ((level >= 40) && (jobLevel == 2)) || (((level >= 76) && (jobLevel == 3)) && (CClassMasterConfig.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))))
		{
			html = html + "<table width=755>";
			html = html + page("<center><font color=FF6600>" + MessagesData.getInstance().getMessage(player, "community_board_classMaster_choiceClass") + "</font></center>");
			html = html + "</table>";
			int id = jobLevel - 1;
			final List<ClassId> classIds = getAvailClasses(classId);
			if (classIds.isEmpty())
			{
				content = content.replace("{info}", "");
			}
			else
			{
				for (ClassId cid : classIds)
				{
					block = template;
					block = block.replace("{icon}", "icon.etc_royal_membership_i00");
					block = block.replace("{name}", DifferentMethods.className(player, cid.getId()));
					switch (payType(id))
					{
						case 0:
							block = block.replace("{action_name}", "<font color=99CC66>"
								+ MessagesData.getInstance().getMessage(player, "community_board_classMaster_cost").replace("%s%", DifferentMethods.formatPay(player, CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_COUNT[id], CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_ITEM_ID[id])) + "</font>");
							block = block.replace("{link}", "bypass _bbscareer:change_class:" + cid.getId() + ":" + id + ":0");
							break;
						case 1:
							block = block.replace("{action_name}", "<font color=99CC66>"
								+ MessagesData.getInstance().getMessage(player, "community_board_classMaster_cost").replace("%s%", DifferentMethods.formatPay(player, CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_COUNT[id], CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_ITEM_ID[id]))
								+ "</font>");
							block = block.replace("{link}", "bypass _bbscareer:change_class:" + cid.getId() + ":" + id + ":1");
							break;
						case 2:
							block = block.replace("{action_name}", "<font color=99CC66>" + MessagesData.getInstance().getMessage(player, "community_board_classMaster_costChoice") + "</font>");
							block = block.replace("{link}", "bypass _bbsbypass:services.Class:choice " + cid.getId() + " " + id + ";_bbscareer");
					}
					html += block.replace("{value}", MessagesData.getInstance().getMessage(player, "community_board_classMaster_change"));
				}
				content = content.replace("{info}", "");
				
				;
			}
		}
		else
		{
			String info = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "classMaster/info.html");
			info = info.replace("{current}", DifferentMethods.className(player, player.getClassId().getId()));
			html = html + "<table width=755>";
			
			switch (jobLevel)
			{
				case 1:
				{
					info = info.replace("{info}", MessagesData.getInstance().getMessage(player, "community_board_classMaster_needLevel_20"));
					break;
				}
				case 2:
				{
					info = info.replace("{info}", MessagesData.getInstance().getMessage(player, "community_board_classMaster_needLevel_40"));
					break;
				}
				case 3:
				{
					info = info.replace("{info}", MessagesData.getInstance().getMessage(player, "community_board_classMaster_needLevel_76"));
					break;
				}
				case 4:
				{
					info = info.replace("{info}", MessagesData.getInstance().getMessage(player, "community_board_classMaster_you_have_learned_all_available_professions"));
					break;
				}
			}
			content = content.replace("{info}", info);
			html = html + "</table>";
		}
		content = content.replace("{classmaster}", html);
		CommunityBoardHandler.separateAndSend(content, player);
	}
	
	public void changeClass(final L2PcInstance player, final int classID, final int id, final int pay)
	{
		if (player == null)
		{
			return;
		}
		
		if (!CClassMasterConfig.ALLOW_CLASS_MASTERS)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbshome", player);
			return;
		}
		
		int item = 0;
		long count = -1L;
		switch (pay)
		{
			case 0:
				item = CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_ITEM_ID[id];
				count = CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_COUNT[id];
				break;
			case 1:
				item = CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_ITEM_ID[id];
				count = CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_COUNT[id];
				break;
		}
		
		if (DifferentMethods.getPay(player, item, count))
		{
			Optional<ClassId> RequestClassId = Optional.empty();
			List<ClassId> availClasses = getAvailClasses(player.getClassId());
			for (ClassId _class : availClasses)
			{
				if (_class.getId() == classID)
				{
					RequestClassId = Optional.of(_class);
					break;
				}
			}
			if (!RequestClassId.isPresent())
			{
				return;
			}
			if (player.getClassId().level() == 3)
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
			}
			else
			{
				player.sendPacket(SystemMessageId.CONGRATULATIONS_YOU_HAVE_COMPLETED_A_CLASS_TRANSFER);
			}
			player.setClassId(classID);
			if (player.isSubClassActive())
			{
				player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
			}
			else
			{
				player.setBaseClass(player.getActiveClass());
			}
			player.broadcastUserInfo();
		}
		showClassPage(player);
	}
	
	public String page(final String text)
	{
		final StringBuilder html = new StringBuilder();
		html.append("<tr>");
		html.append("<td width=20></td>");
		html.append("<td width=690 height=15 align=left valign=top>");
		html.append(text);
		html.append("</td>");
		html.append("</tr>");
		return html.toString();
	}
	
	public int payType(final int id)
	{
		if ((CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_ITEM_ID[id] != 0) && (CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_ITEM_ID[id] == 0))
		{
			return 0;
		}
		if ((CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_ITEM_ID[id] == 0) && (CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_ITEM_ID[id] != 0))
		{
			return 1;
		}
		if ((CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_ITEM_ID[id] != 0) && (CClassMasterConfig.ALLOW_CLASS_MASTERS_PRICE_SECOND_ITEM_ID[id] != 0))
		{
			return 2;
		}
		return 0;
	}
	
	public static List<ClassId> getAvailClasses(ClassId playerClass)
	{
		return Stream.of(ClassId.values()).filter(_class -> (_class.level() == (playerClass.level() + 1)) && _class.childOf(playerClass) && (_class != ClassId.INSPECTOR)).collect(Collectors.toList());
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
