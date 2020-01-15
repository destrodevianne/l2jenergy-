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
package com.l2jserver.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 22.01.2016
 */
public class FloatConverter implements INumberConverter<Float>
{
	@Override
	public Float toObject(final String value)
	{
		return Float.valueOf(value);
	}
	
	@Override
	public Float[] toArrays(final String value, final String split)
	{
		final String[] str = value.split(split);
		final Float[] array = new Float[str.length];
		IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
		return array;
	}
	
	@Override
	public float[] toFloatPrimitive(final String value, final String split)
	{
		final String[] str = value.split(split);
		final float[] array = new float[str.length];
		IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
		return array;
	}
}
