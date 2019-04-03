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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryWatchDog extends Thread
{
	private static final MemoryMXBean mem_bean = ManagementFactory.getMemoryMXBean();
	private static MemoryWatchDog instance = null;
	
	public static long getMemInit()
	{
		return mem_bean.getHeapMemoryUsage().getInit();
	}
	
	public static long getMemUsed()
	{
		return mem_bean.getHeapMemoryUsage().getUsed();
	}
	
	public static long getMemCommitted()
	{
		return mem_bean.getHeapMemoryUsage().getCommitted();
	}
	
	public static long getMemMax()
	{
		return mem_bean.getHeapMemoryUsage().getMax();
	}
	
	public static double getMemUsedPercentage()
	{
		MemoryUsage heapMemoryUsage = mem_bean.getHeapMemoryUsage();
		return (100f * heapMemoryUsage.getUsed()) / heapMemoryUsage.getMax();
	}
	
	public static String getMemUsedPerc()
	{
		return String.format("%.2f%%", getMemUsedPercentage());
	}
	
	public static String getMemUsedMb()
	{
		return (getMemUsed() / 0x100000) + " Mb";
	}
	
	public static String getMemMaxMb()
	{
		return (getMemMax() / 0x100000) + " Mb";
	}
	
	public static String getMemFreeMb()
	{
		return (getMemFree() / 0x100000) + " Mb";
	}
	
	public static long getMemFree()
	{
		MemoryUsage heapMemoryUsage = mem_bean.getHeapMemoryUsage();
		return heapMemoryUsage.getMax() - heapMemoryUsage.getUsed();
	}
	
	public static double getMemFreePercentage()
	{
		return 100f - getMemUsedPercentage();
	}
	
	public static String getMemFreePerc()
	{
		return String.format("%.2f%%", getMemFreePercentage());
	}
	
	public static MemoryWatchDog getInstance()
	{
		if (instance == null)
		{
			instance = new MemoryWatchDog(1000);
		}
		return instance;
	}
	
	private final long sleepInterval;
	
	public MemoryWatchDog(long interval)
	{
		sleepInterval = interval;
	}
	
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(sleepInterval);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}