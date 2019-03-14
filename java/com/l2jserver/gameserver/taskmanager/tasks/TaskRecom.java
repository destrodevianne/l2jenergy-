/*
 * Copyright (C) 2004-2018 L2J Server
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
package com.l2jserver.gameserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.ExVoteSystemInfo;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jserver.gameserver.taskmanager.TaskTypes;

/**
 * @author Layane
 */
public class TaskRecom extends Task
{
	private static final String NAME = "recommendations";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		final String UPDATE_CHARACTERS_RECO = "UPDATE character_reco_bonus cr, characters c SET cr.time_left = 3600, cr.rec_left = 20, rec_have = IF(rec_have > 20, rec_have - 20, 0) WHERE c.online = 0 AND c.charId = cr.charId";
		
		try (Connection con = ConnectionFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(UPDATE_CHARACTERS_RECO);
			statement.execute();
		}
		catch (Exception e)
		{
			LOG.error("{}: Could not reset Recommendations System!", getClass().getSimpleName(), e);
		}
		
		for (L2PcInstance player : L2World.getInstance().getPlayers())
		{
			if ((player != null))
			{
				player.stopRecoBonusTask();
				player.setRecomBonusTime(3600);
				player.setRecomLeft(20);
				player.setRecomHave(player.getRecomHave() - 20);
				if (!player.isInOfflineMode())
				{
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new ExBrExtraUserInfo(player));
					player.sendPacket(new ExVoteSystemInfo(player));
				}
			}
		}
		
		LOG.info("Recommendations System reseted");
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}
