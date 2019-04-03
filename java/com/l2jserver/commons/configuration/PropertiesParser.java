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
package com.l2jserver.commons.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author G1ta0
 */
public class PropertiesParser extends Properties
{
	public static final String DEFAULT_DELIMITER = "[\\s,;]+";
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesParser.class);
	private static final long serialVersionUID = 1L;
	
	public static PropertiesParser parse(final String fileName)
	{
		return parse(new File(fileName));
	}
	
	public static PropertiesParser parse(File file)
	{
		PropertiesParser result = new PropertiesParser();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			LOG.error("", e);
		}
		
		return result;
	}
	
	public static boolean parseBoolean(final CharSequence s)
	{
		switch (s.length())
		{
			case 1:
			{
				final char ch0 = s.charAt(0);
				if ((ch0 == 'y') || (ch0 == 'Y') || (ch0 == '1'))
				{
					return true;
				}
				if ((ch0 == 'n') || (ch0 == 'N') || (ch0 == '0'))
				{
					return false;
				}
				break;
			}
			case 2:
			{
				final char ch0 = s.charAt(0);
				final char ch1 = s.charAt(1);
				if (((ch0 == 'o') || (ch0 == 'O')) && ((ch1 == 'n') || (ch1 == 'N')))
				{
					return true;
				}
				if (((ch0 == 'n') || (ch0 == 'N')) && ((ch1 == 'o') || (ch1 == 'O')))
				{
					return false;
				}
				break;
			}
			case 3:
			{
				final char ch0 = s.charAt(0);
				final char ch1 = s.charAt(1);
				final char ch2 = s.charAt(2);
				if (((ch0 == 'y') || (ch0 == 'Y')) && ((ch1 == 'e') || (ch1 == 'E')) && ((ch2 == 's') || (ch2 == 'S')))
				{
					return true;
				}
				if (((ch0 == 'o') || (ch0 == 'O')) && ((ch1 == 'f') || (ch1 == 'F')) && ((ch2 == 'f') || (ch2 == 'F')))
				{
					return false;
				}
				break;
			}
			case 4:
			{
				final char ch0 = s.charAt(0);
				final char ch1 = s.charAt(1);
				final char ch2 = s.charAt(2);
				final char ch3 = s.charAt(3);
				if (((ch0 == 't') || (ch0 == 'T')) && ((ch1 == 'r') || (ch1 == 'R')) && ((ch2 == 'u') || (ch2 == 'U')) && ((ch3 == 'e') || (ch3 == 'E')))
				{
					return true;
				}
				break;
			}
			case 5:
				final char ch0 = s.charAt(0);
				final char ch1 = s.charAt(1);
				final char ch2 = s.charAt(2);
				final char ch3 = s.charAt(3);
				final char ch4 = s.charAt(4);
				if (((ch0 == 'f') || (ch0 == 'F')) && ((ch1 == 'a') || (ch1 == 'A')) && ((ch2 == 'l') || (ch2 == 'L')) && ((ch3 == 's') || (ch3 == 'S')) && ((ch4 == 'e') || (ch4 == 'E')))
				{
					return false;
				}
				break;
		}
		
		throw new IllegalArgumentException("For input string: \"" + s + '"');
	}
	
	private void load(final File file) throws IOException
	{
		try (FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
		{
			load(bufferedReader);
		}
	}
	
	public boolean getProperty(final String name, final boolean defaultValue)
	{
		boolean val = defaultValue;
		
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			val = parseBoolean(value);
		}
		
		return val;
	}
	
	public int getProperty(final String name, final int defaultValue)
	{
		int val = defaultValue;
		
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			val = Integer.parseInt(value);
		}
		
		return val;
	}
	
	public long getProperty(final String name, final long defaultValue)
	{
		long val = defaultValue;
		
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			val = Long.parseLong(value);
		}
		
		return val;
	}
	
	public double getProperty(final String name, final double defaultValue)
	{
		double val = defaultValue;
		
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			val = Double.parseDouble(value);
		}
		
		return val;
	}
	
	public String[] getProperty(final String name, final String... defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public String[] getProperty(final String name, final String[] defaultValue, final String delimiter)
	{
		String[] val = defaultValue;
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			val = value.split(delimiter);
		}
		
		return val;
	}
	
	public boolean[] getProperty(final String name, final boolean... defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public boolean[] getProperty(final String name, final boolean[] defaultValue, final String delimiter)
	{
		boolean[] val = defaultValue;
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			final String[] values = value.split(delimiter);
			val = new boolean[values.length];
			for (int i = 0; i < val.length; i++)
			{
				val[i] = parseBoolean(values[i]);
			}
		}
		
		return val;
	}
	
	public int[] getProperty(final String name, final int... defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public int[] getProperty(final String name, final int[] defaultValue, final String delimiter)
	{
		int[] val = defaultValue;
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			final String[] values = value.split(delimiter);
			val = new int[values.length];
			for (int i = 0; i < val.length; i++)
			{
				val[i] = Integer.parseInt(values[i]);
			}
		}
		
		return val;
	}
	
	public long[] getProperty(final String name, final long... defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public long[] getProperty(final String name, final long[] defaultValue, final String delimiter)
	{
		long[] val = defaultValue;
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			final String[] values = value.split(delimiter);
			val = new long[values.length];
			for (int i = 0; i < val.length; i++)
			{
				val[i] = Long.parseLong(values[i]);
			}
		}
		
		return val;
	}
	
	public double[] getProperty(final String name, final double... defaultValue)
	{
		return getProperty(name, defaultValue, DEFAULT_DELIMITER);
	}
	
	public double[] getProperty(final String name, final double[] defaultValue, final String delimiter)
	{
		double[] val = defaultValue;
		final String value;
		
		if ((value = super.getProperty(name, null)) != null)
		{
			final String[] values = value.split(delimiter);
			val = new double[values.length];
			for (int i = 0; i < val.length; i++)
			{
				val[i] = Double.parseDouble(values[i]);
			}
		}
		
		return val;
	}
}