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
package com.l2jserver.gameserver.dao.factory;

import com.l2jserver.gameserver.dao.ClanDAO;
import com.l2jserver.gameserver.dao.CommunityBufferDAO;
import com.l2jserver.gameserver.dao.ForumDAO;
import com.l2jserver.gameserver.dao.FriendDAO;
import com.l2jserver.gameserver.dao.HennaDAO;
import com.l2jserver.gameserver.dao.ItemAuctionDAO;
import com.l2jserver.gameserver.dao.ItemDAO;
import com.l2jserver.gameserver.dao.ItemMallDAO;
import com.l2jserver.gameserver.dao.ItemReuseDAO;
import com.l2jserver.gameserver.dao.MonsterRaceDAO;
import com.l2jserver.gameserver.dao.PcCafeDAO;
import com.l2jserver.gameserver.dao.PetDAO;
import com.l2jserver.gameserver.dao.PetSkillSaveDAO;
import com.l2jserver.gameserver.dao.PlayerDAO;
import com.l2jserver.gameserver.dao.PlayerMinigameScoreDAO;
import com.l2jserver.gameserver.dao.PlayerPostFriendDAO;
import com.l2jserver.gameserver.dao.PlayerSkillSaveDAO;
import com.l2jserver.gameserver.dao.PostDAO;
import com.l2jserver.gameserver.dao.PremiumItemDAO;
import com.l2jserver.gameserver.dao.RecipeBookDAO;
import com.l2jserver.gameserver.dao.RecipeShopListDAO;
import com.l2jserver.gameserver.dao.RecommendationBonusDAO;
import com.l2jserver.gameserver.dao.ServitorSkillSaveDAO;
import com.l2jserver.gameserver.dao.ShortcutDAO;
import com.l2jserver.gameserver.dao.SiegeDAO;
import com.l2jserver.gameserver.dao.SkillDAO;
import com.l2jserver.gameserver.dao.SubclassDAO;
import com.l2jserver.gameserver.dao.TeleportBookmarkDAO;
import com.l2jserver.gameserver.dao.TopicDAO;

/**
 * DAO Factory interface.
 * @author Zoey76, Мо3олЬ
 */
public interface IDAOFactory
{
	ClanDAO getClanDAO();
	
	CommunityBufferDAO getCommunityBufferDAO();
	
	ForumDAO getForumDAO();
	
	FriendDAO getFriendDAO();
	
	HennaDAO getHennaDAO();
	
	ItemAuctionDAO getItemAuctionDAO();
	
	ItemDAO getItemDAO();
	
	ItemMallDAO getItemMallDAO();
	
	ItemReuseDAO getItemReuseDAO();
	
	MonsterRaceDAO getMonsterRaceDAO();
	
	PcCafeDAO getPcCafeDAO();
	
	PetDAO getPetDAO();
	
	PetSkillSaveDAO getPetSkillSaveDAO();
	
	PlayerDAO getPlayerDAO();
	
	PlayerPostFriendDAO getPlayerPostFriendDAO();
	
	PlayerMinigameScoreDAO getPlayerMinigameScoreDAO();
	
	PlayerSkillSaveDAO getPlayerSkillSaveDAO();
	
	PostDAO getPostDAO();
	
	PremiumItemDAO getPremiumItemDAO();
	
	RecipeBookDAO getRecipeBookDAO();
	
	RecipeShopListDAO getRecipeShopListDAO();
	
	RecommendationBonusDAO getRecommendationBonusDAO();
	
	ServitorSkillSaveDAO getServitorSkillSaveDAO();
	
	ShortcutDAO getShortcutDAO();
	
	SiegeDAO getSiegeDAO();
	
	SkillDAO getSkillDAO();
	
	SubclassDAO getSubclassDAO();
	
	TeleportBookmarkDAO getTeleportBookmarkDAO();
	
	TopicDAO getTopicDAO();
}
