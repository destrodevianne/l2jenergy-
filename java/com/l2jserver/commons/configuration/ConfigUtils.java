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
package com.l2jserver.commons.configuration;

import java.util.Optional;

import com.l2jserver.commons.converter.Converter;

/**
 * @author Mangol
 * @since 08.01.2016
 */
public class ConfigUtils
{
	public static Optional<?> toObject(final Class<?> clazz, final String value, final Optional<Double> minValue, final Optional<Double> maxValue, final Optional<Double> increase)
	{
		if ((Integer.class == clazz) || (int.class == clazz))
		{
			Integer converted = (Integer) Converter.convert(clazz, value);
			if (increase.isPresent())
			{
				converted *= increase.get().intValue();
			}
			if (minValue.isPresent())
			{
				converted = Math.max(converted, minValue.get().intValue());
			}
			if (maxValue.isPresent())
			{
				converted = Math.min(converted, maxValue.get().intValue());
			}
			converted = Math.toIntExact(converted);
			return Optional.of(converted);
		}
		else if ((Long.class == clazz) || (long.class == clazz))
		{
			Long converted = (Long) Converter.convert(clazz, value);
			if (increase.isPresent())
			{
				converted *= increase.get().intValue();
			}
			if (minValue.isPresent())
			{
				converted = Math.max(converted, minValue.get().longValue());
			}
			if (maxValue.isPresent())
			{
				converted = Math.min(converted, maxValue.get().longValue());
			}
			return Optional.of(converted);
		}
		else if ((Double.class == clazz) || (double.class == clazz))
		{
			Double converted = (Double) Converter.convert(clazz, value);
			if (increase.isPresent())
			{
				converted *= increase.get();
			}
			if (minValue.isPresent())
			{
				converted = Math.max(converted, minValue.get());
			}
			if (maxValue.isPresent())
			{
				converted = Math.min(converted, maxValue.get());
			}
			return Optional.of(converted);
		}
		else if (String.class == clazz)
		{
			return Optional.of(value);
		}
		return Optional.ofNullable(Converter.convert(clazz, value));
	}
}
