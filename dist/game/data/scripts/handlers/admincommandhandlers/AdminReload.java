/*
 * Copyright (C) 2004-2018 L2J DataPack
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

import java.io.File;
import java.util.StringTokenizer;

import javax.script.ScriptException;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.data.sql.impl.CrestTable;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemData;
import com.l2jserver.gameserver.data.xml.impl.EnchantItemGroupsData;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.PlayerCreationPointData;
import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.data.xml.impl.TransformData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.WalkingManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.scripting.L2ScriptEngineManager;
import com.l2jserver.gameserver.util.Util;

/**
 * @author NosBit
 */
public class AdminReload implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_reload"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		if (actualCommand.equalsIgnoreCase("admin_reload"))
		{
			if (!st.hasMoreTokens())
			{
				AdminHtml.showAdminHtml(activeChar, "reload.htm");
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_reload"));
				return true;
			}
			
			final String type = st.nextToken();
			switch (type.toLowerCase())
			{
				case "config":
				{
					Config.load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Configs.");
					break;
				}
				case "access":
				{
					AdminData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Access.");
					break;
				}
				case "npc":
				{
					NpcData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Npcs.");
					break;
				}
				case "quest":
				{
					if (st.hasMoreElements())
					{
						String value = st.nextToken();
						if (!Util.isDigit(value))
						{
							QuestManager.getInstance().reload(value);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest Name:" + value + ".");
						}
						else
						{
							final int questId = Integer.parseInt(value);
							QuestManager.getInstance().reload(questId);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest ID:" + questId + ".");
						}
					}
					else
					{
						QuestManager.getInstance().reloadAllScripts();
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_all_scripts_have_been_reloaded"));
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quests.");
					}
					break;
				}
				case "walker":
				{
					WalkingManager.getInstance().load();
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_all_walkers_have_been_reloaded"));
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Walkers.");
					break;
				}
				case "htm":
				case "html":
				{
					if (st.hasMoreElements())
					{
						final String path = st.nextToken();
						final File file = new File(ServerConfig.DATAPACK_ROOT, "data/html/" + path);
						if (file.exists())
						{
							HtmCache.getInstance().reload(file);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htm File:" + file.getName() + ".");
						}
						else
						{
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_file_or_directory_does_not_exist"));
						}
					}
					else
					{
						HtmCache.getInstance().reload();
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_cache_html_megabytes_on_files_loaded").replace("%s%", HtmCache.getInstance().getMemoryUsage() + "").replace("%i%", HtmCache.getInstance().getLoadedFiles() + ""));
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htms.");
					}
					break;
				}
				case "multisell":
				{
					MultisellData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Multisells.");
					break;
				}
				case "messages":
				{
					MessagesData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Messages.");
					break;
				}
				case "buylist":
				{
					BuyListData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Buylists.");
					break;
				}
				case "teleport":
				{
					TeleportLocationTable.getInstance().reloadAll();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Teleports.");
					break;
				}
				case "skill":
				{
					SkillData.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Skills.");
					break;
				}
				case "item":
				{
					ItemTable.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Items.");
					break;
				}
				case "door":
				{
					DoorData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Doors.");
					break;
				}
				case "zone":
				{
					ZoneManager.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Zones.");
					break;
				}
				case "cw":
				{
					CursedWeaponsManager.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Cursed Weapons.");
					break;
				}
				case "crest":
				{
					CrestTable.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Crests.");
					break;
				}
				case "effect":
				{
					final File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "handlers/EffectMasterHandler.java");
					try
					{
						L2ScriptEngineManager.getInstance().executeScript(file);
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Effects.");
					}
					catch (ScriptException e)
					{
						L2ScriptEngineManager.getInstance().reportScriptFileError(file, e);
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_there_was_error_while_loading_handlers"));
					}
					break;
				}
				case "handler":
				{
					final File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "handlers/MasterHandler.java");
					try
					{
						L2ScriptEngineManager.getInstance().executeScript(file);
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Handlers.");
					}
					catch (ScriptException e)
					{
						L2ScriptEngineManager.getInstance().reportScriptFileError(file, e);
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_there_was_error_while_loading_handlers"));
					}
					break;
				}
				case "enchant":
				{
					EnchantItemGroupsData.getInstance().load();
					EnchantItemData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded item enchanting data.");
					break;
				}
				case "transform":
				{
					TransformData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded transform data.");
					break;
				}
				case "creationpoint":
				{
					PlayerCreationPointData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded creation points data.");
					break;
				}
				case "itemmall":
				{
					ProductItemData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded item mall data.");
					break;
				}
				default:
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_usage_reload"));
					return true;
				}
			}
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_warn_reloding"));
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
