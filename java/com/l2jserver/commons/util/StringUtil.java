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
package com.l2jserver.commons.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StringUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);
	
	/**
	 * Concatenates strings.
	 * @param strings strings to be concatenated
	 * @return concatenated string
	 */
	public static String concat(final String... strings)
	{
		final StringBuilder sbString = new StringBuilder();
		for (final String string : strings)
		{
			sbString.append(string);
		}
		return sbString.toString();
	}
	
	/**
	 * Creates new string builder with size initializated to <code>sizeHint</code>, unless total length of strings is greater than <code>sizeHint</code>.
	 * @param sizeHint hint for string builder size allocation
	 * @param strings strings to be appended
	 * @return created string builder
	 */
	public static StringBuilder startAppend(final int sizeHint, final String... strings)
	{
		final int length = getLength(strings);
		final StringBuilder sbString = new StringBuilder(sizeHint > length ? sizeHint : length);
		for (final String string : strings)
		{
			sbString.append(string);
		}
		return sbString;
	}
	
	/**
	 * Appends strings to existing string builder.
	 * @param sbString string builder
	 * @param strings strings to be appended
	 */
	public static void append(final StringBuilder sbString, final String... strings)
	{
		sbString.ensureCapacity(sbString.length() + getLength(strings));
		
		for (final String string : strings)
		{
			sbString.append(string);
		}
	}
	
	public static int getLength(final Iterable<String> strings)
	{
		int length = 0;
		for (final String string : strings)
		{
			length += (string == null) ? 4 : string.length();
		}
		return length;
	}
	
	/**
	 * Counts total length of all the strings.
	 * @param strings array of strings
	 * @return total length of all the strings
	 */
	public static int getLength(final String[] strings)
	{
		int length = 0;
		for (final String string : strings)
		{
			length += (string == null) ? 4 : string.length();
		}
		return length;
	}
	
	public static String getTraceString(StackTraceElement[] trace)
	{
		final StringBuilder sbString = new StringBuilder();
		for (final StackTraceElement element : trace)
		{
			sbString.append(element.toString()).append(System.lineSeparator());
		}
		return sbString.toString();
	}
	
	/**
	 * Verify if the given text matches with the regex pattern.
	 * @param text : the text to test.
	 * @param regex : the regex pattern to make test with.
	 * @return true if matching.
	 */
	public static boolean isValidString(String text, String regex)
	{
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(regex);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			pattern = Pattern.compile(".*");
		}
		
		Matcher regexp = pattern.matcher(text);
		return regexp.matches();
	}
	
	/**
	 * Format a given text to fit with logging "title" criterias, and send it.
	 * @param text : the String to format.
	 */
	public static void printSection(String text)
	{
		final StringBuilder sb = new StringBuilder(60);
		for (int i = 0; i < (54 - text.length()); i++)
		{
			sb.append("-");
		}
		StringUtil.append(sb, "=[ ", text, " ]");
		LOG.info(sb.toString());
	}
}