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
package handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.SevenSignsFestival;
import com.l2jserver.gameserver.configuration.config.custom.OfflineConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.events.Team;
import com.l2jserver.gameserver.enums.skills.AbnormalVisualEffect;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.TradeList;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.taskmanager.AttackStanceTaskManager;

public class OfflineShop implements IVoicedCommandHandler
{
	private static String[] VOICED_COMMANDS =
	{
		"offline"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance player, String params)
	{
		if (player == null)
		{
			return false;
		}
		
		if ((!player.isInStoreMode() && (!player.isInCraftMode())) || !player.isSitting())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_not_running_private_store_or_private_work_shop"));
			return false;
		}
		
		if (player.isOnEvent() && !player.isGM())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_cannot_logout_while_registered_event"));
			return false;
		}
		
		final TradeList storeListBuy = player.getBuyList();
		if ((storeListBuy == null))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_buy_list_empty"));
			return false;
		}
		
		final TradeList storeListSell = player.getSellList();
		if ((storeListSell == null))
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_sell_list_empty"));
			return false;
		}
		
		player.getInventory().updateDatabase();
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) && !(player.isGM()))
		{
			player.sendPacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING);
			return false;
		}
		
		// Dont allow leaving if player is in combat
		if (player.isInCombat() && !player.isGM())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_cannot_logout_while_combat_mode"));
			return false;
		}
		
		// Dont allow leaving if player is teleporting
		if (player.isTeleporting() && !player.isGM())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_cannot_logout_while_teleporting"));
			return false;
		}
		
		if (player.getTeam() != Team.NONE)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_superior_power_doesnt_allow_leave_event"));
			return false;
		}
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_cannot_logout_while_olympiad"));
			return false;
		}
		
		// Prevent player from logging out if they are a festival participant nd it is in progress,
		// otherwise notify party members that the player is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_cannot_logout_while_festival"));
				return false;
			}
			
			final L2Party playerParty = player.getParty();
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(player, SystemMessage.sendString(MessagesData.getInstance().getMessage(player, "offline_voiced_has_been_removed_from_festival").replace("%s%", player.getName() + "")));
			}
		}
		
		if (player.isFlying())
		{
			player.removeSkill(SkillData.getInstance().getSkill(4289, 1));
		}
		
		if ((player.isInStoreMode() && OfflineConfig.OFFLINE_TRADE_ENABLE) || (player.isInCraftMode() && OfflineConfig.OFFLINE_CRAFT_ENABLE))
		{
			// Sleep effect, not official feature but however L2OFF features (like offline trade)
			if (OfflineConfig.OFFLINE_SLEEP_EFFECT)
			{
				player.startAbnormalVisualEffect(true, AbnormalVisualEffect.SLEEP);
			}
			
			player.sendMessage(MessagesData.getInstance().getMessage(player, "offline_voiced_you_private_store_has_succesfully_been"));
			player.logout();
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}