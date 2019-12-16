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
package com.l2jserver.commons.converter;

import java.util.Optional;

import com.l2jserver.commons.converter.converters.IConverter;
import com.l2jserver.commons.converter.numberConverters.ByteConverter;
import com.l2jserver.commons.converter.numberConverters.DoubleConverter;
import com.l2jserver.commons.converter.numberConverters.FloatConverter;
import com.l2jserver.commons.converter.numberConverters.INumberConverter;
import com.l2jserver.commons.converter.numberConverters.IntegerConverter;
import com.l2jserver.commons.converter.numberConverters.LongConverter;
import com.l2jserver.commons.converter.numberConverters.ShortConverter;

/**
 * @author Mangol
 * @since 09.01.2016
 */
public class Converter
{
	@SuppressWarnings("unchecked")
	public static <T> T convert(final Class<T> type, final String value)
	{
		final Optional<INumberConverter<?>> numberConverter = Optional.ofNullable(RegisterObject.getNumberConverter(type));
		if (numberConverter.isPresent())
		{
			return (T) numberConverter.get().toObject(value);
		}
		final Optional<IConverter<?>> converter = Optional.ofNullable(RegisterObject.getConverter(type));
		if (converter.isPresent())
		{
			return (T) converter.get().toObject(value);
		}
		throw new NullPointerException();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] convertArraysNumber(final Class<?> type, final String value, final String split)
	{
		final Optional<INumberConverter<?>> numberConverter = Optional.ofNullable(RegisterObject.getNumberConverter(type));
		if (numberConverter.isPresent())
		{
			return (T[]) numberConverter.get().toArrays(value, split);
		}
		throw new NullPointerException();
	}
	
	public static int[] toIntArray(final String value, final String split)
	{
		final INumberConverter<?> converter = RegisterObject.getNumberConverter(Integer.class);
		final IntegerConverter integerConverter = (IntegerConverter) converter;
		return integerConverter.toIntArraysPrimitive(value, split);
	}
	
	public static double[] toDoubleArray(final String value, final String split)
	{
		final INumberConverter<?> converter = RegisterObject.getNumberConverter(Double.class);
		final DoubleConverter doubleConverter = (DoubleConverter) converter;
		return doubleConverter.toDoubleArraysPrimitive(value, split);
	}
	
	public static float[] toFloatArray(final String value, final String split)
	{
		final INumberConverter<?> converter = RegisterObject.getNumberConverter(Float.class);
		final FloatConverter floatConverter = (FloatConverter) converter;
		return floatConverter.toFloatPrimitive(value, split);
	}
	
	public static long[] toLongArray(final String value, final String split)
	{
		final INumberConverter<?> converter = RegisterObject.getNumberConverter(Long.class);
		final LongConverter longConverter = (LongConverter) converter;
		return longConverter.toLongPrimitive(value, split);
	}
	
	public static byte[] toByteArray(final String value, final String split)
	{
		final INumberConverter<?> converter = RegisterObject.getNumberConverter(Byte.class);
		final ByteConverter byteConverter = (ByteConverter) converter;
		return byteConverter.toBytePrimitive(value, split);
	}
	
	public static short[] toShortArray(final String value, final String split)
	{
		final INumberConverter<?> converter = RegisterObject.getNumberConverter(Short.class);
		final ShortConverter shortConverter = (ShortConverter) converter;
		return shortConverter.toShortPrimitive(value, split);
	}
}
