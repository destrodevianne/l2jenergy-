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

import java.util.Map;

import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.events.MessageType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.gameeventengine.GameEventManager;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.network.serverpackets.ExEventMatchMessage;

public class EventUtil
{
	// TODO: протестировать и доработать
	public static boolean checkPlayer(L2PcInstance player, boolean firstCheck)
	{
		if (firstCheck && GameEventManager.getInstance().getEvent().getConfigs().EVENT_PARTICIPATION_FEE)
		{
			if (player.getInventory().getInventoryItemCount(GameEventManager.getInstance().getEvent().getConfigs().EVENT_TAKE_ITEM_ID, -1) < GameEventManager.getInstance().getEvent().getConfigs().EVENT_TAKE_COUNT)
			{
				player.sendMessage("You don't have enough " + ItemTable.getInstance().getTemplate(GameEventManager.getInstance().getEvent().getConfigs().EVENT_TAKE_ITEM_ID).getName() + " to participate.");
				return false;
			}
		}
		
		if (firstCheck && !GameEventManager.getInstance().getEvent().isParticipating())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Процесс регистрации не активен.", null, MessageType.TEXT);
			return false;
		}
		
		if (firstCheck && GameEventManager.getInstance().getEvent().isParticipant(player.getObjectId()))
		{
			showMessage(player, "event_you_alredy_registred", null, MessageType.CUSTOM);
			return false;
		}
		
		if (player.isTeleporting())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в эвенте во время телепортации.", null, MessageType.TEXT);
			return false;
		}
		
		if (!player.isOnline())
		{
			showMessage(player, "You have to be online to register.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isMounted())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в Ивенте, сидя на пете.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isInDuel())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в Ивенте во время дуэли.", null, MessageType.TEXT);
			return false;
		}
		
		else if (player.isJailed())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Вы не можете зарегистрироваться, потому что в тюрьме.", null, MessageType.TEXT);
			return false;
		}
		
		if (firstCheck && (player.isOnEvent()))
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в нескольких Ивентах одновременно.", null, MessageType.TEXT);
			return false;
		}
		
		else if (player.getBlockCheckerArena() >= 0) // || HandysBlockCheckerManager.getInstance().isRegistered(player))
		{
			showMessage(player, "Регистрация в Ивенте отменена. Вы не можете зарегистрироваться, потому что участвуете в ивенте Handy Block Checker.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в эвенте во время олимпиады.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isInParty() && player.getParty().isInDimensionalRift())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в эвенте во время прохождения Dimensional Rift или Delusion Chambler.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isCursedWeaponEquipped())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в эвенте, владея проклятым оружием.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isDead())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в Ивенте, будучи мертвым.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.inObserverMode())
		{
			showMessage(player, "Регистрация в Ивенте отменена. Вы не можете участвовать в эвенте, пока находитесь в режиме обзора.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.getInstanceId() > 0)
		{
			showMessage(player, "Регистрация в Ивенте отменена. Нельзя участвовать в эвенте, находясь в инстанс-зоне.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.getKarma() > 0)
		{
			showMessage(player, "PK's can't participate in the opening event.", null, MessageType.TEXT);
			return false;
		}
		
		if ((player.getLevel() < GameEventManager.getInstance().getEvent().getConfigs().EVENT_MIN_LVL) || (player.getLevel() > GameEventManager.getInstance().getEvent().getConfigs().EVENT_MAX_LVL))
		{
			showMessage(player, "Регистрация в Ивенте отменена. Вы не проходите на ивент по уровню для Ивента.", null, MessageType.TEXT);
			return false;
		}
		
		if (firstCheck && (GameEventManager.getInstance().getEvent().getCountPlayers() >= GameEventManager.getInstance().getEvent().getConfigs().EVENT_MAX_PLAYERS_IN_TEAMS))
		{
			showMessage(player, "Регистрация в Ивенте отменена. Кол-во зарегистрированных в Ивенте достигло максимума.", null, MessageType.TEXT);
			return false;
		}
		
		if (player.isFestivalParticipant())
		{
			showMessage(player, "Вы не можете зарегистрироваться на ивент, потому что зарегистрированы на фестиваль 7 печатей.", null, MessageType.TEXT);
			return false;
		}
		return true;
	}
	
	public static void showEventMessage(L2PcInstance player, String textId, MessageType type)
	{
		player.sendPacket(new ExEventMatchMessage(type.getType(), MessagesData.getInstance().getMessage(player, textId)));
	}
	
	public static void showMessage(L2PcInstance player, String textId, Map<String, String> map, MessageType type)
	{
		String announce = MessagesData.getInstance().getMessage(player, textId);
		
		if (player == null)
		{
			return;
		}
		
		if (map != null)
		{
			for (String key : map.keySet())
			{
				announce = announce.replace(key, map.get(key));
			}
		}
		
		if (type == MessageType.CUSTOM)
		{
			player.sendMessage(announce);
		}
		else
		{
			player.sendMessage(String.format(textId, map));
		}
	}
}
