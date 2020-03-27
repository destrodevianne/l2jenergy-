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
package com.l2jserver.gameserver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.model.L2RecipeList;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.tasks.character.RecipeItemMakerTask;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.RecipeBookItemList;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

public class RecipeController
{
	protected static final Map<Integer, RecipeItemMakerTask> _activeMakers = new ConcurrentHashMap<>();
	
	protected RecipeController()
	{
		// Prevent external initialization.
	}
	
	public void requestBookOpen(L2PcInstance player, boolean isDwarvenCraft)
	{
		// Check if player is trying to alter recipe book while engaged in manufacturing.
		if (!_activeMakers.containsKey(player.getObjectId()))
		{
			RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
			response.addRecipes(isDwarvenCraft ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
			player.sendPacket(response);
			return;
		}
		player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
	}
	
	public void requestMakeItemAbort(L2PcInstance player)
	{
		_activeMakers.remove(player.getObjectId()); // TODO: anything else here?
	}
	
	public void requestManufactureItem(L2PcInstance manufacturer, int recipeListId, L2PcInstance player)
	{
		final L2RecipeList recipeList = RecipeData.getInstance().getValidRecipeList(player, recipeListId);
		if (recipeList == null)
		{
			return;
		}
		
		List<L2RecipeList> dwarfRecipes = Arrays.asList(manufacturer.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(manufacturer.getCommonRecipeBook());
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		// Check if manufacturer is under manufacturing store or private store.
		if (CharacterConfig.ALT_GAME_CREATION && _activeMakers.containsKey(manufacturer.getObjectId()))
		{
			player.sendPacket(SystemMessageId.CLOSE_STORE_WINDOW_AND_TRY_AGAIN);
			return;
		}
		
		final RecipeItemMakerTask maker = new RecipeItemMakerTask(manufacturer, recipeList, player);
		if (maker.getValid())
		{
			if (CharacterConfig.ALT_GAME_CREATION)
			{
				_activeMakers.put(manufacturer.getObjectId(), maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else
			{
				maker.run();
			}
		}
	}
	
	public void requestMakeItem(L2PcInstance player, int recipeListId)
	{
		// Check if player is trying to operate a private store or private workshop while engaged in combat.
		if (player.isInCombat() || player.isInDuel())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		final L2RecipeList recipeList = RecipeData.getInstance().getValidRecipeList(player, recipeListId);
		if (recipeList == null)
		{
			return;
		}
		
		List<L2RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", GeneralConfig.DEFAULT_PUNISH);
			return;
		}
		
		// Check if player is busy (possible if alt game creation is enabled)
		if (CharacterConfig.ALT_GAME_CREATION && _activeMakers.containsKey(player.getObjectId()))
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1);
			sm.addItemName(recipeList.getItemId());
			sm.addString("You are busy creating.");
			player.sendPacket(sm);
			return;
		}
		
		final RecipeItemMakerTask maker = new RecipeItemMakerTask(player, recipeList, player);
		if (maker.getValid())
		{
			if (CharacterConfig.ALT_GAME_CREATION)
			{
				_activeMakers.put(player.getObjectId(), maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else
			{
				maker.run();
			}
		}
	}
	
	public static RecipeController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RecipeController INSTANCE = new RecipeController();
	}
}
