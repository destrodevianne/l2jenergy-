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
package com.l2jserver.gameserver.model.augmentation;

public class AugmentationChance
{
	private final String _WeaponType;
	private final int _StoneId;
	private final int _VariationId;
	private final int _CategoryChance;
	private final int _AugmentId;
	private final float _AugmentChance;
	
	public AugmentationChance(String WeaponType, int StoneId, int VariationId, int CategoryChance, int AugmentId, float AugmentChance)
	{
		_WeaponType = WeaponType;
		_StoneId = StoneId;
		_VariationId = VariationId;
		_CategoryChance = CategoryChance;
		_AugmentId = AugmentId;
		_AugmentChance = AugmentChance;
	}
	
	public String getWeaponType()
	{
		return _WeaponType;
	}
	
	public int getStoneId()
	{
		return _StoneId;
	}
	
	public int getVariationId()
	{
		return _VariationId;
	}
	
	public int getCategoryChance()
	{
		return _CategoryChance;
	}
	
	public int getAugmentId()
	{
		return _AugmentId;
	}
	
	public float getAugmentChance()
	{
		return _AugmentChance;
	}
}
