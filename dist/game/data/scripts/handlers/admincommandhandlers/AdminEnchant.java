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

import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

public class AdminEnchant implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_seteh", // 6
		"admin_setec", // 10
		"admin_seteg", // 9
		"admin_setel", // 11
		"admin_seteb", // 12
		"admin_setew", // 7
		"admin_setes", // 8
		"admin_setle", // 1
		"admin_setre", // 2
		"admin_setlf", // 4
		"admin_setrf", // 5
		"admin_seten", // 3
		"admin_setun", // 0
		"admin_setba", // 13
		"admin_setbe",
		"admin_enchant"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_enchant"))
		{
			showMainPage(activeChar);
		}
		else
		{
			int armorType = -1;
			
			if (command.startsWith("admin_seteh"))
			{
				armorType = Inventory.PAPERDOLL_HEAD;
			}
			else if (command.startsWith("admin_setec"))
			{
				armorType = Inventory.PAPERDOLL_CHEST;
			}
			else if (command.startsWith("admin_seteg"))
			{
				armorType = Inventory.PAPERDOLL_GLOVES;
			}
			else if (command.startsWith("admin_seteb"))
			{
				armorType = Inventory.PAPERDOLL_FEET;
			}
			else if (command.startsWith("admin_setel"))
			{
				armorType = Inventory.PAPERDOLL_LEGS;
			}
			else if (command.startsWith("admin_setew"))
			{
				armorType = Inventory.PAPERDOLL_RHAND;
			}
			else if (command.startsWith("admin_setes"))
			{
				armorType = Inventory.PAPERDOLL_LHAND;
			}
			else if (command.startsWith("admin_setle"))
			{
				armorType = Inventory.PAPERDOLL_LEAR;
			}
			else if (command.startsWith("admin_setre"))
			{
				armorType = Inventory.PAPERDOLL_REAR;
			}
			else if (command.startsWith("admin_setlf"))
			{
				armorType = Inventory.PAPERDOLL_LFINGER;
			}
			else if (command.startsWith("admin_setrf"))
			{
				armorType = Inventory.PAPERDOLL_RFINGER;
			}
			else if (command.startsWith("admin_seten"))
			{
				armorType = Inventory.PAPERDOLL_NECK;
			}
			else if (command.startsWith("admin_setun"))
			{
				armorType = Inventory.PAPERDOLL_UNDER;
			}
			else if (command.startsWith("admin_setba"))
			{
				armorType = Inventory.PAPERDOLL_CLOAK;
			}
			else if (command.startsWith("admin_setbe"))
			{
				armorType = Inventory.PAPERDOLL_BELT;
			}
			
			if (armorType != -1)
			{
				try
				{
					int ench = Integer.parseInt(command.substring(12));
					
					// check value
					if ((ench < 0) || (ench > 65535))
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_you_must_set_enchant_level_between"));
					}
					else
					{
						setEnchant(activeChar, ench, armorType);
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_please_specify_new_enchant_value"));
				}
				catch (NumberFormatException e)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_please_specify_valid_new_enchant_value"));
				}
			}
			// show the enchant menu after an action
			showMainPage(activeChar);
		}
		return true;
	}
	
	private void setEnchant(L2PcInstance activeChar, int ench, int armorType)
	{
		// get the target
		L2Object target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only
		L2ItemInstance itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
		{
			itemInstance = parmorInstance;
		}
		
		if (itemInstance != null)
		{
			curEnchant = itemInstance.getEnchantLevel();
			
			// set enchant value
			player.getInventory().unEquipItemInSlot(armorType);
			itemInstance.setEnchantLevel(ench);
			player.getInventory().equipItem(itemInstance);
			
			// send packets
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			player.sendPacket(iu);
			player.broadcastPacket(new CharInfo(player));
			player.sendPacket(new UserInfo(player));
			player.broadcastPacket(new ExBrExtraUserInfo(player));
			
			// informations
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_changed_enchantment_player").replace("%i%", player.getName() + "").replace("%s%", itemInstance.getItem().getName() + "").replace("%t%", curEnchant + "").replace("%c%", ench + ""));
			player.sendMessage(MessagesData.getInstance().getMessage(player, "admin_has_changed_enchantment_your").replace("%s%", itemInstance.getItem().getName() + "").replace("%t%", curEnchant + "").replace("%c%", ench + ""));
		}
	}
	
	private void showMainPage(L2PcInstance activeChar)
	{
		AdminHtml.showAdminHtml(activeChar, "enchant.htm");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}