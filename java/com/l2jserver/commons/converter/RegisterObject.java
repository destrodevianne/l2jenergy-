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
package com.l2jserver.commons.converter;

import java.util.HashMap;
import java.util.Map;

//import gnu.trove.map.TMap;
//import gnu.trove.map.hash.THashMap;
import com.l2jserver.commons.converter.converters.BooleanConverter;
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
 * @since 22.01.2016
 */
public final class RegisterObject
{
	private static final Map<Class<?>, INumberConverter<?>> numberConverter = new HashMap<>();
	private static final Map<Class<?>, IConverter<?>> converter = new HashMap<>();
	private static final IntegerConverter integerConverter = new IntegerConverter();
	private static final LongConverter longConverter = new LongConverter();
	private static final ByteConverter byteConverter = new ByteConverter();
	private static final DoubleConverter doubleConverter = new DoubleConverter();
	private static final FloatConverter floatConverter = new FloatConverter();
	private static final ShortConverter shortConverter = new ShortConverter();
	private static final BooleanConverter booleanConverter = new BooleanConverter();
	
	static
	{
		registerObject();
	}
	
	private static void registerObject()
	{
		converter.put(Boolean.class, booleanConverter);
		numberConverter.put(Integer.class, integerConverter);
		numberConverter.put(Long.class, longConverter);
		numberConverter.put(Byte.class, byteConverter);
		numberConverter.put(Double.class, doubleConverter);
		numberConverter.put(Float.class, floatConverter);
		numberConverter.put(Short.class, shortConverter);
		// primitive
		numberConverter.put(int.class, integerConverter);
		numberConverter.put(long.class, longConverter);
		numberConverter.put(byte.class, byteConverter);
		numberConverter.put(double.class, doubleConverter);
		numberConverter.put(float.class, floatConverter);
		numberConverter.put(short.class, shortConverter);
		converter.put(boolean.class, booleanConverter);
	}
	
	public static INumberConverter<?> getNumberConverter(final Class<?> clazz)
	{
		return numberConverter.get(clazz);
	}
	
	public static IConverter<?> getConverter(final Class<?> clazz)
	{
		return converter.get(clazz);
	}
}
