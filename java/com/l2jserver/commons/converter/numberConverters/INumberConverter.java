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

/**
 * @author Mangol
 * @since 09.01.2016
 */
public interface INumberConverter<T extends Number>
{
	default int[] toIntArraysPrimitive(final String value, final String split)
	{
		throw new NullPointerException();
	}
	
	default double[] toDoubleArraysPrimitive(final String value, final String split)
	{
		throw new NullPointerException();
	}
	
	default long[] toLongPrimitive(final String value, final String split)
	{
		throw new NullPointerException();
	}
	
	default float[] toFloatPrimitive(final String value, final String split)
	{
		throw new NullPointerException();
	}
	
	default byte[] toBytePrimitive(final String value, final String split)
	{
		throw new NullPointerException();
	}
	
	default short[] toShortPrimitive(final String value, final String split)
	{
		throw new NullPointerException();
	}
	
	T[] toArrays(final String value, final String split);
	
	T toObject(final String value);
}
