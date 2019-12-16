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
package com.l2jserver.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 16.01.2016
 */
public class IntegerConverter implements INumberConverter<Integer>
{
	@Override
	public Integer toObject(final String value)
	{
		return Integer.valueOf(value);
	}
	
	@Override
	public int[] toIntArraysPrimitive(final String value, final String split)
	{
		final String[] str = value.split(split);
		final int[] array = new int[str.length];
		IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
		return array;
	}
	
	@Override
	public Integer[] toArrays(final String value, final String split)
	{
		final String[] str = value.split(split);
		final Integer[] array = new Integer[str.length];
		IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
		return array;
	}
}
