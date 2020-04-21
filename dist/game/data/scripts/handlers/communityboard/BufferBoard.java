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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.configuration.config.community.CBasicConfig;
import com.l2jserver.gameserver.configuration.config.community.CBufferConfig;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.BufferBBSData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.ZoneId;
import com.l2jserver.gameserver.enums.events.Team;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.DifferentMethods;
import com.l2jserver.gameserver.util.bbs.Buff;
import com.l2jserver.gameserver.util.bbs.BuffScheme;
import com.l2jserver.gameserver.util.bbs.BuffTask;
import com.l2jserver.gameserver.util.bbs.Scheme;

import gnu.trove.list.array.TIntArrayList;

public class BufferBoard implements IParseBoardHandler
{
	private static final TIntArrayList allowBuff = new TIntArrayList(CBufferConfig.CB_BUFFER_ALLOWED_BUFFS);
	
	private static List<String> pageBuffPlayer;
	private static List<String> pageBuffPet;
	
	private static final StringBuilder buffSchemes = new StringBuilder();
	
	private static final String[] COMMANDS =
	{
		"_bbsbuffer",
		"_bbsplayerbuffer",
		"_bbspetbuffer",
		"_bbscastbuff",
		"_bbscastgroupbuff",
		"_bbsbuffersave",
		"_bbsbufferuse",
		"_bbsbufferdelete",
		"_bbsbufferheal",
		"_bbsbufferremovebuffs"
	};
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance player)
	{
		if (!CBufferConfig.ALLOW_CB_BUFFER)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_services_disabled"));
			parseCommunityBoardCommand("_bbshome", player);
			return false;
		}
		
		if (!checkCondition(player))
		{
			parseCommunityBoardCommand("_bbshome", player);
			return false;
		}
		
		String html;
		final String customPath = CBasicConfig.CUSTOM_CB_ENABLED ? "Custom/" : "";
		
		if (command.equals("_bbsbuffer"))
		{
			html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "buffer/index.html");
			html = html.replace("%scheme%", buffSchemes.toString());
			String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "buffer/my-sheme.html");
			String block;
			String list = null;
			html = html.replace("{buffTime}", String.valueOf(CBufferConfig.CB_BUFFER_TIME));
			for (final String name : player.getBuffSchemes().keySet())
			{
				block = template;
				block = block.replace("{bypass}", "bypass _bbsbufferuse " + name + " $Who");
				block = block.replace("{name}", name);
				block = block.replace("{delete}", "bypass _bbsbufferdelete " + name);
				list = list + block;
			}
			final int priceId = CBufferConfig.CB_BUFFER_PRICE[0];
			int priceCount = CBufferConfig.CB_BUFFER_PRICE[1];
			
			if (CBufferConfig.ALLOW_CB_BUFFER_PRICE_MOD)
			{
				priceCount *= player.getLevel();
			}
			
			if (CBufferConfig.ALLOW_CB_BUFFER_PREMIUM_MOD && player.isPremium())
			{
				priceCount = (int) (priceCount * CBufferConfig.CB_BUFFER_PA_MOD);
			}
			
			if (player.getLevel() < CBufferConfig.CB_BUFFER_PLAYER_FREE_LEVEL)
			{
				priceCount = 0;
			}
			
			html = html.replace("%price%", DifferentMethods.formatPay(player, priceCount, priceId));
			
			if (list != null)
			{
				html = html.replace("%buffgrps%", list);
			}
			else
			{
				html = html.replace("%buffgrps%", HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "buffer/my-sheme-empty.html"));
			}
			onInit();
		}
		else if (command.startsWith("_bbsplayerbuffer"))
		{
			html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "buffer/scheme.html");
			final StringTokenizer st1 = new StringTokenizer(command, ":");
			st1.nextToken();
			final int page = Integer.parseInt(st1.nextToken());
			if (pageBuffPlayer.get(page) != null)
			{
				html = html.replace("%content%", pageBuffPlayer.get(page));
			}
		}
		else if (command.startsWith("_bbspetbuffer"))
		{
			html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "buffer/scheme.html");
			StringTokenizer st1 = new StringTokenizer(command, ":");
			st1.nextToken();
			int page = Integer.parseInt(st1.nextToken());
			if (pageBuffPet.get(page) != null)
			{
				html = html.replace("%content%", pageBuffPet.get(page));
			}
		}
		else if (command.startsWith("_bbscastbuff"))
		{
			html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/CommunityBoard/" + customPath + "buffer/scheme.html");
			StringTokenizer st1 = new StringTokenizer(command, ":");
			st1.nextToken();
			
			final int id = Integer.parseInt(st1.nextToken());
			final int level = Integer.parseInt(st1.nextToken());
			final int page = Integer.parseInt(st1.nextToken());
			final String type = st1.nextToken();
			
			L2Playable playable = null;
			if ("Player".equals(type))
			{
				playable = player;
			}
			else if ("Pet".equals(type))
			{
				playable = player.getSummon();
			}
			
			final int check = allowBuff.indexOf(id);
			final int priceId = CBufferConfig.CB_BUFFER_PRICE[0];
			int priceCount = CBufferConfig.CB_BUFFER_PRICE[1];
			
			if (CBufferConfig.ALLOW_CB_BUFFER_PRICE_MOD)
			{
				priceCount *= player.getLevel();
			}
			
			if ((playable != null) && (check != -1) && (allowBuff.get(check + 1) <= level))
			{
				if (player.getLevel() < CBufferConfig.CB_BUFFER_PLAYER_FREE_LEVEL)
				{
					buffList(Collections.singletonList(new Buff(id, level)), playable);
				}
				else if (DifferentMethods.getPay(player, priceId, priceCount))
				{
					buffList(Collections.singletonList(new Buff(id, level)), playable);
				}
			}
			
			if ("Player".equals(type))
			{
				html = html.replace("%content%", pageBuffPlayer.get(page));
			}
			else if ("Pet".equals(type))
			{
				html = html.replace("%content%", pageBuffPet.get(page));
			}
		}
		else
		{
			if (command.startsWith("_bbscastgroupbuff"))
			{
				final StringTokenizer st1 = new StringTokenizer(command, " ");
				st1.nextToken();
				final int id = Integer.parseInt(st1.nextToken());
				final String type = st1.nextToken();
				final BuffScheme buffScheme = BufferBBSData.getInstance().getBuffScheme(id);
				final int priceId = buffScheme.getPriceId();
				final long priceCount = buffScheme.getPriceCount();
				
				L2Playable playable = null;
				if ("Player".equals(type))
				{
					playable = player;
				}
				else if ("Pet".equals(type))
				{
					playable = player.getSummon();
				}
				
				if (playable != null)
				{
					if (player.getLevel() < CBufferConfig.CB_BUFFER_PLAYER_FREE_LEVEL)
					{
						buffList(buffScheme.getBuffIds(), playable);
					}
					else if (DifferentMethods.getPay(player, priceId, priceCount))
					{
						buffList(buffScheme.getBuffIds(), playable);
					}
				}
				parseCommunityBoardCommand("_bbsbuffer", player);
				return false;
			}
			
			if (command.startsWith("_bbsbuffersave"))
			{
				if (player.getBuffSchemes().size() >= 5)
				{
					player.sendMessage("You can create only 5 scheme!");
					parseCommunityBoardCommand("_bbsbuffer", player);
					return false;
				}
				
				final StringTokenizer st1 = new StringTokenizer(command, " ");
				if (st1.countTokens() < 3)
				{
					parseCommunityBoardCommand("_bbsbuffer", player);
					return false;
				}
				st1.nextToken();
				
				final String name = st1.nextToken();
				final String type = st1.nextToken();
				
				L2Playable playable = null;
				if ("Player".equals(type))
				{
					playable = player;
				}
				else if ("Pet".equals(type))
				{
					playable = player.getSummon();
				}
				
				if (playable == null)
				{
					return false;
				}
				
				if (playable.getEffectList().getEffects().size() == 0)
				{
					parseCommunityBoardCommand("_bbsbuffer", player);
					return false;
				}
				
				if (player.getBuffScheme(name).isPresent())
				{
					player.sendMessage("Такое название уже существует!");
					parseCommunityBoardCommand("_bbsbuffer", player);
					return false;
				}
				
				if (DifferentMethods.getPay(player, CBufferConfig.CB_BUFFER_SAVED_ID, CBufferConfig.CB_BUFFER_SAVED_PRICE))
				{
					final StringBuilder buffs = new StringBuilder();
					final Scheme scheme = new Scheme(name);
					for (final BuffInfo effect : playable.getEffectList().getEffects())
					{
						final Skill skill = effect.getSkill();
						final int id = skill.getId();
						int level = skill.getLevel();
						final int check = allowBuff.indexOf(skill.getId());
						level = level > allowBuff.get(check + 1) ? allowBuff.get(check + 1) : level;
						if (check != -1)
						{
							buffs.append(id).append(",").append(level).append(";");
							scheme.addBuff(id, level);
						}
					}
					DAOFactory.getInstance().getCommunityBufferDAO().insert(player, buffs.toString(), scheme);
				}
				parseCommunityBoardCommand("_bbsbuffer", player);
				return false;
			}
			
			if (command.startsWith("_bbsbufferuse"))
			{
				StringTokenizer st1 = new StringTokenizer(command, " ");
				st1.nextToken();
				String name = st1.nextToken();
				String type = st1.nextToken();
				
				L2Playable playable = null;
				if ("Player".equals(type))
				{
					playable = player;
				}
				else if ("Pet".equals(type))
				{
					playable = player.getSummon();
				}
				
				int priceId = CBufferConfig.CB_BUFFER_PRICE[0];
				int priceCount = CBufferConfig.CB_BUFFER_PRICE[1];
				
				if (CBufferConfig.ALLOW_CB_BUFFER_PRICE_MOD)
				{
					priceCount *= player.getLevel();
				}
				
				if (CBufferConfig.ALLOW_CB_BUFFER_PRICE_MOD && player.isPremium())
				{
					priceCount = (int) (priceCount * CBufferConfig.CB_BUFFER_PA_MOD);
				}
				
				if (playable != null)
				{
					final List<Buff> buffs = new ArrayList<>();
					final Optional<Scheme> my = player.getBuffScheme(name);
					if (my.isPresent())
					{
						if (player.getLevel() > CBufferConfig.CB_BUFFER_PLAYER_FREE_LEVEL)
						{
							priceCount *= my.get().getBuffs().size();
						}
						else
						{
							priceCount = 0;
						}
						
						if (DifferentMethods.getPay(player, priceId, priceCount))
						{
							buffs.addAll(my.get().getBuffs().entrySet().stream().filter(scheme -> allowBuff.indexOf(scheme.getKey()) != -1).map(scheme -> new Buff(scheme.getKey(), scheme.getValue())).collect(Collectors.toList()));
						}
						buffList(buffs, playable);
					}
				}
				parseCommunityBoardCommand("_bbsbuffer", player);
				return false;
			}
			
			if (command.startsWith("_bbsbufferdelete"))
			{
				final StringTokenizer st1 = new StringTokenizer(command, " ");
				st1.nextToken();
				final String name = st1.nextToken();
				DAOFactory.getInstance().getCommunityBufferDAO().delete(player, name);
				parseCommunityBoardCommand("_bbsbuffer", player);
				return false;
			}
			
			if (command.startsWith("_bbsbufferheal"))
			{
				if (!CBufferConfig.ALLOW_CB_BUFFER_RECOVER)
				{
					player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_services_disabled"));
				}
				else if ((CBufferConfig.ALLOW_CB_BUFFER_RECOVER_PVP_FLAG && (player.getPvpFlag() > 0)) || (CBufferConfig.ALLOW_CB_BUFFER_RECOVER_BATTLE && player.isInCombat()))
				{
					player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_combat_enable"));
				}
				else
				{
					StringTokenizer st1 = new StringTokenizer(command, " ");
					st1.nextToken();
					String type = st1.nextToken();
					String target = st1.nextToken();
					
					L2Playable playable = null;
					if ("Player".equals(target))
					{
						playable = player;
					}
					else if ("Pet".equals(target))
					{
						playable = player.getSummon();
					}
					
					if (playable != null)
					{
						if ((CBufferConfig.ALLOW_CB_BUFFER_PEACE_RECOVER) && (!playable.isInsideZone(ZoneId.PEACE)))
						{
							if (playable.isPlayer())
							{
								player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_buffer_peaceZone"));
							}
							parseCommunityBoardCommand("_bbsbuffer", player);
							return false;
						}
						
						if (DifferentMethods.getPay(player, CBufferConfig.CB_BUFFER_RECOVER_PRICE[0], CBufferConfig.CB_BUFFER_RECOVER_PRICE[1]))
						{
							if ("HP".equals(type))
							{
								if (playable.getCurrentHp() != playable.getMaxHp())
								{
									playable.setCurrentHp(playable.getMaxHp());
									playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
								}
							}
							else if ("MP".equals(type))
							{
								if (playable.getCurrentMp() != playable.getMaxMp())
								{
									playable.setCurrentMp(playable.getMaxMp());
									playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
								}
							}
							else if ("CP".equals(type))
							{
								if (playable.getCurrentCp() != playable.getMaxCp())
								{
									playable.setCurrentCp(playable.getMaxCp());
									playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
								}
							}
						}
					}
				}
				parseCommunityBoardCommand("_bbsbuffer", player);
				return false;
			}
			
			if (command.startsWith("_bbsbufferremovebuffs"))
			{
				if (!CBufferConfig.ALLOW_CB_BUFFER_CLEAR)
				{
					player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_services_disabled"));
				}
				else
				{
					StringTokenizer st1 = new StringTokenizer(command, " ");
					st1.nextToken();
					String type = st1.nextToken();
					
					L2Playable playable = null;
					if ("Player".equals(type))
					{
						playable = player;
					}
					else if ("Pet".equals(type))
					{
						playable = player.getSummon();
					}
					if (playable != null)
					{
						playable.stopAllEffects();
						playable.broadcastPacket(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0));
					}
				}
				parseCommunityBoardCommand("_bbsbuffer", player);
				return false;
			}
			player.sendMessage("Error");
			return false;
		}
		CommunityBoardHandler.separateAndSend(html, player);
		return true;
	}
	
	public void writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		
	}
	
	public void genPage(final List<String> list, final String type)
	{
		final StringBuilder sb = new StringBuilder("<table><tr>");
		sb.append("<td width=70>Navigation: </td>");
		for (int i = 0; i < list.size(); i++)
		{
			sb.append(DifferentMethods.buttonTD(String.valueOf(i + 1), "_bbs" + type + ("") + "buffer:" + i, 30, 25, "L2UI_CT1.ListCTRL_DF_Title_Down", "L2UI_CT1.ListCTRL_DF_Title"));
		}
		sb.append("<td>").append(DifferentMethods.button("Back", "_bbsbuffer", 60, 25, "L2UI_CT1.ListCTRL_DF_Title_Down", "L2UI_CT1.ListCTRL_DF_Title")).append("</td></tr></table><br><br>");
		for (int i = 0; i < list.size(); i++)
		{
			list.set(i, sb.toString() + list.get(i));
		}
	}
	
	public void genPageBuff(final List<String> list, final int start, final String type)
	{
		final StringBuilder buffPages = new StringBuilder("<table><tr>");
		int i = start;
		boolean next = false;
		for (; i < allowBuff.size(); i += 2)
		{
			if (next && ((i % 12) == 0))
			{
				buffPages.append("</tr><tr>");
			}
			if (next && ((i % 48) == 0))
			{
				break;
			}
			buffPages.append("<td>").append(buttonBuff(allowBuff.get(i), allowBuff.get(i + 1), list.size(), type)).append("</td>");
			next = true;
		}
		buffPages.append("</tr></table>");
		list.add(buffPages.toString());
		if ((i + 2) <= allowBuff.size())
		{
			genPageBuff(list, i, type);
		}
	}
	
	public String buttonBuff(int id, int level, int page, String type)
	{
		return buttonBuff(id, level, page, type, 0, 0);
	}
	
	public String buttonBuff(int id, int level, int page, String type, int itemId, int itemCount)
	{
		String skillId = Integer.toString(id);
		StringBuilder sb = new StringBuilder("<table width=100>");
		String icon;
		
		if (skillId.length() < 4)
		{
			icon = 0 + skillId;
		}
		else if (skillId.length() < 3)
		{
			icon = 00 + skillId;
		}
		else if ((id == 4700) || (id == 4699))
		{
			icon = "1331";
		}
		else if ((id == 4702) || (id == 4703))
		{
			icon = "1332";
		}
		else if (id == 1517)
		{
			icon = "1499";
		}
		else if (id == 1518)
		{
			icon = "1502";
		}
		else
		{
			icon = skillId;
		}
		
		String name = SkillData.getInstance().getSkill(id, level).getName();
		name = name.replace("Dance of the", "D.");
		name = name.replace("Dance of", "D.");
		name = name.replace("Song of", "S.");
		name = name.replace("Improved", "I.");
		name = name.replace("Awakening", "A.");
		name = name.replace("Blessing", "Bless.");
		name = name.replace("Protection", "Protect.");
		name = name.replace("Critical", "C.");
		name = name.replace("Condition", "Con.");
		sb.append("<tr><td><center><img src=icon.skill").append(icon).append(" width=32 height=32><br>");
		if (itemId != 0)
		{
			sb.append("<font color=437AD0>Price: </font>" + itemCount + " ").append(ItemTable.getInstance().getTemplate(itemId).getName()).append("<br>");
		}
		sb.append("<font color=F2C202>Level ").append(level).append("</font></center></td></tr>");
		sb.append(DifferentMethods.buttonTR(name, "_bbscastbuff:" + id + ":" + level + ":" + page + ":" + type + (""), 100, 25, "L2UI_CT1.ListCTRL_DF_Title_Down", "L2UI_CT1.ListCTRL_DF_Title"));
		sb.append("</table>");
		return sb.toString();
	}
	
	public static void buffList(List<Buff> list, L2Playable playable)
	{
		new BuffTask(list, playable).run();
	}
	
	public static boolean checkCondition(L2PcInstance player)
	{
		if (player.isAlikeDead())
		{
			player.sendMessage("Баф в режиме смерти запрещен.");
			return false;
		}
		else if (player.isJailed())
		{
			player.sendMessage("Данное действие запрещенно в Тюрьме.");
			return false;
		}
		else if (player.isInOlympiadMode())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_olympiad_enable"));
			return false;
		}
		else if (!CBufferConfig.ALLOW_CB_BUFFER_IN_SIEGE && (player.isInSiege() || (player.getSiegeState() != 0)))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_siege_disable"));
			return false;
		}
		else if ((!CBufferConfig.ALLOW_CB_BUFFER_IN_PVP && (player.getPvpFlag() > 0)) || (!CBufferConfig.ALLOW_CB_BUFFER_IN_BATTLE && (player.isInCombat() || player.isMovementDisabled())))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_combat_enable"));
			return false;
		}
		else if ((!CBufferConfig.ALLOW_CB_BUFFER_IN_EVENTS && (player.getTeam() != Team.NONE)) || (player.getBlockCheckerArena() >= 0))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_events"));
			return false;
		}
		else if (player.isFlying())
		{
			player.sendMessage("Во время полёта Бафф запрещен.");
			return false;
		}
		else if (player.getInstanceId() > 0)
		{
			player.sendMessage("Бафф в Инстансе запрещен.");
			return false;
		}
		else if (!checkLevel(player.getLevel()))
		{
			final int min = CBufferConfig.CB_BUFFER_PLAYER_MIN_LEVEL;
			final int max = CBufferConfig.CB_BUFFER_PLAYER_MAX_LEVEL;
			player.sendMessage(MessagesData.getInstance().getMessage(player, "community_board_minMaxLevel").replace("%s%", min + "").replace("%c%", max + ""));
			return false;
		}
		else if (player.inObserverMode())
		{
			return false;
		}
		return true;
	}
	
	public static boolean checkLevel(int level)
	{
		return (level >= CBufferConfig.CB_BUFFER_PLAYER_MIN_LEVEL) && (level <= CBufferConfig.CB_BUFFER_PLAYER_MAX_LEVEL);
	}
	
	public void onInit()
	{
		pageBuffPlayer = new ArrayList<>();
		pageBuffPet = new ArrayList<>();
		genPageBuff(pageBuffPlayer, 0, "Player");
		genPage(pageBuffPlayer, "player");
		genPageBuff(pageBuffPet, 0, "Pet");
		genPage(pageBuffPet, "pet");
	}
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
}
