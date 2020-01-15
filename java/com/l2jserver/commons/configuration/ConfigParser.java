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
package com.l2jserver.commons.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.configuration.annotations.Configuration;
import com.l2jserver.commons.configuration.annotations.Setting;
import com.l2jserver.commons.configuration.setup.ISetup;

/**
 * Create by Mangol on 24.11.2015.
 */
public class ConfigParser
{
	private static final Logger LOG = LoggerFactory.getLogger(ConfigParser.class);
	
	private final Map<String, Method> methodMap = new HashMap<>();
	private final Map<Object, Object> _objectMap;
	private final Class<?> _clazz;
	
	private ConfigParser(final Class<?> clazz, final Map<Object, Object> objectMap)
	{
		_clazz = clazz;
		_objectMap = objectMap;
	}
	
	public static ConfigParser createConfigParser(final Class<?> clazz, final Map<Object, Object> objectMap)
	{
		return new ConfigParser(clazz, objectMap);
	}
	
	private static Optional<?> toConverter(final String fieldName, final Class<?> clazz, final String value, final Optional<Double> minValue, final Optional<Double> maxValue, final Optional<Double> increase)
	{
		final Optional<?> converted = ConfigUtils.toObject(clazz, value, minValue, maxValue, increase);
		if (converted.isPresent())
		{
			return converted;
		}
		LOG.error("[{}]" + " field type : {} not registered", fieldName, clazz.getSimpleName());
		return Optional.empty();
	}
	
	public void parse()
	{
		final boolean isConfiguration = getClazz().isAnnotationPresent(Configuration.class);
		if (!isConfiguration)
		{
			LOG.error("class not annotation  \"Configuration\"");
			return;
		}
		try
		{
			final Object object = getClazz().newInstance();
			if (getClazz().getDeclaredMethods().length > 0)
			{
				for (final Method method : getClazz().getDeclaredMethods())
				{
					if (method.getParameterCount() == 1)
					{
						methodMap.put(method.getName(), method);
					}
				}
			}
			for (final Field field : getClazz().getDeclaredFields())
			{
				if (!Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				final boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				final Optional<Annotation> annotation = Optional.ofNullable(field.getAnnotation(Setting.class));
				if (annotation.isPresent())
				{
					final Setting setting = (Setting) annotation.get();
					if (setting.ignore())
					{
						continue;
					}
				}
				doValue(object, field, annotation);
				field.setAccessible(isAccessible);
			}
			
			if (getClazz().getInterfaces().length > 0)
			{
				for (final Class<?> _interface : getClazz().getInterfaces())
				{
					if (_interface.isAssignableFrom(ISetup.class))
					{
						final Method method = _interface.getDeclaredMethod("setup");
						method.invoke(object);
						break;
					}
				}
			}
		}
		catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e)
		{
			e.printStackTrace();
		}
	}
	
	private void doValue(final Object object, final Field field, final Optional<Annotation> annotation)
	{
		try
		{
			Optional<String> method = Optional.empty();
			Optional<String> name = Optional.empty();
			Optional<String> splitter = Optional.empty();
			Optional<Double> minValue = Optional.empty();
			Optional<Double> maxValue = Optional.empty();
			Optional<Double> increase = Optional.empty();
			boolean canNull = false;
			if (annotation.isPresent())
			{
				final Setting setting = (Setting) annotation.get();
				if (!setting.name().isEmpty())
				{
					name = Optional.of(setting.name());
				}
				splitter = Optional.of(setting.splitter());
				if (setting.minValue() != -1)
				{
					minValue = Optional.of(setting.minValue());
				}
				if (setting.maxValue() != -1)
				{
					maxValue = Optional.of(setting.maxValue());
				}
				if (setting.canNull())
				{
					canNull = setting.canNull();
				}
				if (setting.increase() != 1)
				{
					increase = Optional.of(setting.increase());
				}
				if (!setting.method().isEmpty())
				{
					method = Optional.of(setting.method());
				}
			}
			name = name.isPresent() ? name : Optional.of(field.getName());
			splitter = splitter.isPresent() ? splitter : Optional.of(";");
			final Optional<String> value = Optional.ofNullable((String) getObjectMap().get(name.get()));
			if (value.isPresent() && !value.get().isEmpty())
			{
				final String valueStr = value.get();
				if (method.isPresent())
				{
					setMethodValue(object, method.get(), valueStr, splitter, minValue, maxValue, increase);
				}
				else
				{
					setValue(field, valueStr, splitter, minValue, maxValue, increase);
				}
			}
			else if (canNull)
			{
				final Class<?> typeClazz = field.getType();
				if (!typeClazz.isArray() && !typeClazz.isAssignableFrom(List.class))
				{
					final Optional<Object> objectValue = Optional.ofNullable(field.get(name.get()));
					if (objectValue.isPresent())
					{
						final Optional<?> objectOptional = toConverter(field.getName(), typeClazz, String.valueOf(objectValue.get()), minValue, maxValue, increase);
						if (objectOptional.isPresent())
						{
							field.set(name.get(), objectOptional.get());
						}
					}
					else
					{
						LOG.error("Class : {} : canNull value name - {} is null", field.getDeclaringClass().getSimpleName(), name.get());
					}
				}
			}
			else
			{
				LOG.error("Class : {} : value name - {} is null", field.getDeclaringClass().getSimpleName(), name.get());
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void setMethodValue(final Object object, final String methodName, final String value, final Optional<String> splitter, final Optional<Double> minValue, final Optional<Double> maxValue, final Optional<Double> increase)
	{
		try
		{
			final Optional<Method> method = Optional.ofNullable(methodMap.get(methodName));
			if (method.isPresent())
			{
				final boolean isAccessible = method.get().isAccessible();
				method.get().setAccessible(true);
				final Class<?> typeClazz = method.get().getParameterTypes()[0];
				if (typeClazz.isArray())
				{
					final Class<?> componentType = typeClazz.getComponentType();
					final String[] arr = value.split(splitter.get());
					final Object array = Array.newInstance(componentType, arr.length);
					IntStream.range(0, arr.length).forEach(i ->
					{
						final Optional<?> optional = toConverter(method.get().getName(), componentType, arr[i], Optional.empty(), Optional.empty(), Optional.empty());
						if (optional.isPresent())
						{
							Array.set(array, i, optional.get());
						}
					});
					method.get().invoke(object, array);
				}
				else
				{
					final Optional<?> optional = toConverter(method.get().getName(), typeClazz, value, minValue, maxValue, increase);
					if (optional.isPresent())
					{
						method.get().invoke(object, optional.get());
					}
				}
				method.get().setAccessible(isAccessible);
			}
			else
			{
				LOG.error("Method parameter type length error : method name -  : {}", methodName);
			}
		}
		catch (final InvocationTargetException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	private void setValue(final Field field, final String value, final Optional<String> splitter, final Optional<Double> minValue, final Optional<Double> maxValue, final Optional<Double> increase) throws Exception
	{
		final Class typeClazz = field.getType();
		if (typeClazz.isArray())
		{
			final Class<?> componentType = typeClazz.getComponentType();
			final String[] arr = value.replaceAll("\\s+", "").split(splitter.get());
			if (componentType.isEnum())
			{
				final Class<? extends Enum> clazz = (Class<? extends Enum>) field.getType().getComponentType();
				Enum[] array = (Enum[]) Array.newInstance(clazz, arr.length);
				IntStream.range(0, arr.length).forEach(i -> array[i] = Enum.valueOf(clazz, arr[i]));
				field.set(field.getName(), array);
			}
			else
			{
				final Object array = Array.newInstance(componentType, arr.length);
				field.set(field.getName(), array);
				IntStream.range(0, arr.length).forEach(i ->
				{
					final Optional<?> object = toConverter(field.getName(), componentType, arr[i], minValue, maxValue, Optional.empty());
					if (object.isPresent())
					{
						Array.set(array, i, object.get());
					}
				});
				field.set(field.getName(), array);
			}
		}
		else if (typeClazz.isAssignableFrom(List.class))
		{
			final Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			final String[] values = value.replaceAll("\\s+", "").split(splitter.get());
			final List<Object> list = new ArrayList<>();
			for (final String val : values)
			{
				final Optional<?> object = toConverter(field.getName(), genericType, val, minValue, maxValue, Optional.empty());
				if (object.isPresent())
				{
					list.add(object.get());
				}
			}
			field.set(field.getName(), list);
		}
		else if (typeClazz.isEnum())
		{
			final Enum<?> enums = Enum.valueOf(typeClazz, value);
			field.set(field.getName(), enums);
		}
		else
		{
			final Optional<?> object = toConverter(field.getName(), typeClazz, value, minValue, maxValue, increase);
			if (object.isPresent())
			{
				field.set(field.getName(), object.get());
			}
		}
	}
	
	public Class<?> getClazz()
	{
		return _clazz;
	}
	
	public Map<Object, Object> getObjectMap()
	{
		return _objectMap;
	}
}