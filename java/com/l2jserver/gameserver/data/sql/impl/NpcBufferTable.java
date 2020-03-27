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
package com.l2jserver.gameserver.data.sql.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.configuration.config.GeneralConfig;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;

public class NpcBufferTable
{
	private static final Logger LOG = LoggerFactory.getLogger(NpcBufferTable.class);
	
	private final Map<Integer, NpcBufferSkills> _buffers = new HashMap<>();
	
	public static class NpcBufferData
	{
		private final SkillHolder _skill;
		private final ItemHolder _fee;
		
		protected NpcBufferData(int skillId, int skillLevel, int feeId, int feeAmount)
		{
			_skill = new SkillHolder(skillId, skillLevel);
			_fee = new ItemHolder(feeId, feeAmount);
		}
		
		public SkillHolder getSkill()
		{
			return _skill;
		}
		
		public ItemHolder getFee()
		{
			return _fee;
		}
	}
	
	private static class NpcBufferSkills
	{
		private final int _npcId;
		private final Map<Integer, NpcBufferData> _skills = new HashMap<>();
		
		protected NpcBufferSkills(int npcId)
		{
			_npcId = npcId;
		}
		
		public void addSkill(int skillId, int skillLevel, int skillFeeId, int skillFeeAmount, int buffGroup)
		{
			_skills.put(buffGroup, new NpcBufferData(skillId, skillLevel, skillFeeId, skillFeeAmount));
		}
		
		public NpcBufferData getSkillGroupInfo(int buffGroup)
		{
			return _skills.get(buffGroup);
		}
		
		@SuppressWarnings("unused")
		public int getNpcId()
		{
			return _npcId;
		}
	}
	
	protected NpcBufferTable()
	{
		int skillCount = 0;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement s = con.createStatement();
			ResultSet rset = s.executeQuery("SELECT `npc_id`,`skill_id`,`skill_level`,`skill_fee_id`,`skill_fee_amount`,`buff_group` FROM `npc_buffer` ORDER BY `npc_id` ASC"))
		{
			int lastNpcId = 0;
			NpcBufferSkills skills = null;
			
			while (rset.next())
			{
				int npcId = rset.getInt("npc_id");
				int skillId = rset.getInt("skill_id");
				int skillLevel = rset.getInt("skill_level");
				int skillFeeId = rset.getInt("skill_fee_id");
				int skillFeeAmount = rset.getInt("skill_fee_amount");
				int buffGroup = rset.getInt("buff_group");
				
				if (npcId != lastNpcId)
				{
					if (lastNpcId != 0)
					{
						_buffers.put(lastNpcId, skills);
					}
					
					skills = new NpcBufferSkills(npcId);
					skills.addSkill(skillId, skillLevel, skillFeeId, skillFeeAmount, buffGroup);
				}
				else if (skills != null)
				{
					skills.addSkill(skillId, skillLevel, skillFeeId, skillFeeAmount, buffGroup);
				}
				
				lastNpcId = npcId;
				skillCount++;
			}
			
			if (lastNpcId != 0)
			{
				_buffers.put(lastNpcId, skills);
			}
		}
		catch (SQLException e)
		{
			LOG.error("{}: Error reading npc_buffer table!", getClass().getSimpleName(), e);
		}
		
		if (GeneralConfig.CUSTOM_NPCBUFFER_TABLES)
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				Statement s = con.createStatement();
				ResultSet rset = s.executeQuery("SELECT `npc_id`,`skill_id`,`skill_level`,`skill_fee_id`,`skill_fee_amount`,`buff_group` FROM `custom_npc_buffer` ORDER BY `npc_id` ASC"))
			{
				int lastNpcId = 0;
				NpcBufferSkills skills = null;
				while (rset.next())
				{
					int npcId = rset.getInt("npc_id");
					int skillId = rset.getInt("skill_id");
					int skillLevel = rset.getInt("skill_level");
					int skillFeeId = rset.getInt("skill_fee_id");
					int skillFeeAmount = rset.getInt("skill_fee_amount");
					int buffGroup = rset.getInt("buff_group");
					
					if (npcId != lastNpcId)
					{
						if (lastNpcId != 0)
						{
							_buffers.put(lastNpcId, skills);
						}
						
						skills = new NpcBufferSkills(npcId);
						skills.addSkill(skillId, skillLevel, skillFeeId, skillFeeAmount, buffGroup);
					}
					else if (skills != null)
					{
						skills.addSkill(skillId, skillLevel, skillFeeId, skillFeeAmount, buffGroup);
					}
					lastNpcId = npcId;
					skillCount++;
				}
				
				if (lastNpcId != 0)
				{
					_buffers.put(lastNpcId, skills);
				}
			}
			catch (SQLException e)
			{
				LOG.error("{}: Error reading custom_npc_buffer table!", getClass().getSimpleName(), e);
			}
		}
		LOG.info("{}: Loaded {} buffers and {} skills.", getClass().getSimpleName(), _buffers.size(), skillCount);
	}
	
	public NpcBufferData getSkillInfo(int npcId, int buffGroup)
	{
		final NpcBufferSkills skills = _buffers.get(npcId);
		if (skills != null)
		{
			return skills.getSkillGroupInfo(buffGroup);
		}
		return null;
	}
	
	public static NpcBufferTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcBufferTable INSTANCE = new NpcBufferTable();
	}
}