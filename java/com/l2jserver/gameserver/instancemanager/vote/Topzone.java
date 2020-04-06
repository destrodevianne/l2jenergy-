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
package com.l2jserver.gameserver.instancemanager.vote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.l2jserver.gameserver.configuration.config.custom.TopsConfig;

public class Topzone extends VoteSystem
{
	public Topzone(int votesDiff, boolean allowReport, int boxes, Map<Integer, Integer> rewards, int checkMins)
	{
		super(votesDiff, allowReport, boxes, rewards, checkMins);
	}
	
	@Override
	public void run()
	{
		reward();
	}
	
	@Override
	public int getVotes()
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try
		{
			URLConnection con = new URL(TopsConfig.TOPZONE_SERVER_LINK).openConnection();
			con.addRequestProperty("User-Agent", "L2TopZone");
			isr = new InputStreamReader(con.getInputStream());
			br = new BufferedReader(isr);
			
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.contains("</span></small>"))
				{
					int votes = Integer.valueOf(line.split(">")[9].replace("</span", ""));
					return votes;
				}
			}
			
			br.close();
			isr.close();
		}
		catch (Exception e)
		{
			LOG.warn("Error while getting server vote count from {}.", getSiteName(), e);
		}
		return -1;
	}
	
	@Override
	public String getSiteName()
	{
		return "Topzone";
	}
}
