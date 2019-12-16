/*
 * Copyright (C) 2004-2019 L2jEnergy Server
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
package com.l2jserver.gameserver.dao;

import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;

/**
 * Pet Skill Save interface.
 * @author Zoey76
 */
public interface PetSkillSaveDAO
{
	/**
	 * Stores the pet skills in the database.
	 * @param pet the pets
	 * @param storeEffects if {@code true} effects will be stored
	 */
	void insert(L2PetInstance pet, boolean storeEffects);
	
	/**
	 * Restores the pet skills from the database.
	 * @param pet the pet
	 */
	void load(L2PetInstance pet);
}
