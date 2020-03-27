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
package com.l2jserver.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.PropertiesParser;
import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.TowerSpawn;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.Siege;
import com.l2jserver.gameserver.model.interfaces.ILocational;
import com.l2jserver.gameserver.model.skills.Skill;

public final class SiegeManager
{
	private static final Logger LOG = LoggerFactory.getLogger(SiegeManager.class);
	
	private final Map<Integer, List<TowerSpawn>> _controlTowers = new HashMap<>();
	private final Map<Integer, List<TowerSpawn>> _flameTowers = new HashMap<>();
	
	protected SiegeManager()
	{
		load();
	}
	
	public final void addSiegeSkills(L2PcInstance character)
	{
		for (Skill sk : SkillData.getInstance().getSiegeSkills(character.isNoble(), character.getClan().getCastleId() > 0))
		{
			character.addSkill(sk, false);
		}
	}
	
	public final void removeSiegeSkills(L2PcInstance character)
	{
		for (Skill sk : SkillData.getInstance().getSiegeSkills(character.isNoble(), character.getClan().getCastleId() > 0))
		{
			character.removeSkill(sk);
		}
	}
	
	private final void load()
	{
		final PropertiesParser siegeSettings = new PropertiesParser(Config.SIEGE_CONFIGURATION_FILE);
		
		// Siege setting
		
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			final List<TowerSpawn> controlTowers = new ArrayList<>();
			for (int i = 1; i < 0xFF; i++)
			{
				final String settingsKeyName = castle.getName() + "ControlTower" + i;
				if (!siegeSettings.containskey(settingsKeyName))
				{
					break;
				}
				
				final StringTokenizer st = new StringTokenizer(siegeSettings.getString(settingsKeyName, ""), ",");
				try
				{
					final int x = Integer.parseInt(st.nextToken());
					final int y = Integer.parseInt(st.nextToken());
					final int z = Integer.parseInt(st.nextToken());
					final int npcId = Integer.parseInt(st.nextToken());
					
					controlTowers.add(new TowerSpawn(npcId, new Location(x, y, z)));
				}
				catch (Exception e)
				{
					LOG.warn("{}: Error while loading control tower(s) for {} castle.", getClass().getSimpleName(), castle.getName());
				}
			}
			
			final List<TowerSpawn> flameTowers = new ArrayList<>();
			for (int i = 1; i < 0xFF; i++)
			{
				final String settingsKeyName = castle.getName() + "FlameTower" + i;
				if (!siegeSettings.containskey(settingsKeyName))
				{
					break;
				}
				
				final StringTokenizer st = new StringTokenizer(siegeSettings.getString(settingsKeyName, ""), ",");
				try
				{
					final int x = Integer.parseInt(st.nextToken());
					final int y = Integer.parseInt(st.nextToken());
					final int z = Integer.parseInt(st.nextToken());
					final int npcId = Integer.parseInt(st.nextToken());
					final List<Integer> zoneList = new ArrayList<>();
					
					while (st.hasMoreTokens())
					{
						zoneList.add(Integer.parseInt(st.nextToken()));
					}
					
					flameTowers.add(new TowerSpawn(npcId, new Location(x, y, z), zoneList));
				}
				catch (Exception e)
				{
					LOG.warn("{}: Error while loading flame tower(s) for {} castle.", getClass().getSimpleName(), castle.getName());
				}
			}
			_controlTowers.put(castle.getResidenceId(), controlTowers);
			_flameTowers.put(castle.getResidenceId(), flameTowers);
			MercTicketManager.MERCS_MAX_PER_CASTLE[castle.getResidenceId() - 1] = siegeSettings.getInt(castle.getName() + "MaxMercenaries", MercTicketManager.MERCS_MAX_PER_CASTLE[castle.getResidenceId() - 1]);
			
			if (castle.getOwnerId() != 0)
			{
				DAOFactory.getInstance().getSiegeDAO().loadTrapUpgrade(castle.getResidenceId());
			}
		}
	}
	
	public final List<TowerSpawn> getControlTowers(int castleId)
	{
		return _controlTowers.get(castleId);
	}
	
	public final List<TowerSpawn> getFlameTowers(int castleId)
	{
		return _flameTowers.get(castleId);
	}
	
	public final Siege getSiege(ILocational loc)
	{
		return getSiege(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public final Siege getSiege(L2Object activeObject)
	{
		return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public final Siege getSiege(int x, int y, int z)
	{
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (castle.getSiege().checkIfInZone(x, y, z))
			{
				return castle.getSiege();
			}
		}
		return null;
	}
	
	public final List<Siege> getSieges()
	{
		List<Siege> sieges = new ArrayList<>();
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			sieges.add(castle.getSiege());
		}
		return sieges;
	}
	
	public static final SiegeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SiegeManager INSTANCE = new SiegeManager();
	}
}