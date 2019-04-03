/*
 * Copyright (C) 2004-2019 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.Reflection;

/**
 * @author Java-man
 */
public class ReflectionUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);
	
	public static void loadClasses(final String prefix)
	{
		loadClasses(getSubTypesOf(prefix, Object.class));
	}
	
	public static <T> void loadClasses(final String prefix, final Class<T> type)
	{
		loadClasses(getSubTypesOf(prefix, type));
	}
	
	public static <T> void loadClassesWithConsumer(final String prefix, final Class<T> type, final Consumer<T> consumer)
	{
		loadClassesWithConsumer(getSubTypesOf(prefix, type), consumer);
	}
	
	public static void loadClassesFromPackage(final Package pack)
	{
		final Thread thread = Thread.currentThread();
		final ClassPath classPath;
		try
		{
			classPath = ClassPath.from(thread.getContextClassLoader());
		}
		catch (IOException e)
		{
			LOG.error("Can't load classes.", e);
			return;
		}
		
		for (final ClassInfo classInfo : classPath.getTopLevelClasses(pack.getName()))
		{
			final Class<?> clazz = classInfo.load();
			
			final int modifiers = clazz.getModifiers();
			if (Modifier.isAbstract(modifiers))
			{
				continue;
			}
			
			Reflection.initialize(clazz);
		}
	}
	
	public static <T> void loadClasses(final Iterable<Class<? extends T>> classes)
	{
		for (final Class<? extends T> clazz : classes)
		{
			try
			{
				clazz.getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
			{
				LOG.warn("{} can't be loaded.", clazz.getSimpleName(), e);
			}
		}
	}
	
	public static <T> void loadClassesWithConsumer(final Iterable<Class<? extends T>> classes, final Consumer<T> consumer)
	{
		for (final Class<? extends T> clazz : classes)
		{
			try
			{
				final T instance = clazz.getConstructor().newInstance();
				if (consumer != null)
				{
					consumer.accept(instance);
				}
			}
			catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
			{
				LOG.warn("{} can't be loaded.", clazz.getSimpleName(), e);
			}
		}
	}
	
	private static <T> Iterable<Class<? extends T>> getSubTypesOf(final String prefix, final Class<T> type)
	{
		final Reflections reflections = new Reflections(prefix);
		return reflections.getSubTypesOf(type);
	}
}
