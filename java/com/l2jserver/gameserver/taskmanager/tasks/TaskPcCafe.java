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
import com.l2jserver.gameserver.model.variables.AccountVariables;
import com.l2jserver.gameserver.model.variables.PlayerVariables;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jserver.gameserver.taskmanager.TaskTypes;

/**
 * @author Мо3олЬ
 */
public class TaskPcCafe extends Task
{
	private static final String NAME = "pc_cafe";
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		// Update data for offline players.
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ?");
			PreparedStatement ps2 = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
		{
			ps.setString(1, AccountVariables.PC_CAFE_POINTS_TODAY);
			ps.executeUpdate();
			ps2.setString(1, PlayerVariables.USED_PC_LOTTERY_TICKET);
			ps2.executeUpdate();
		}
		catch (Exception e)
		{
			_log.warning("Could not reset pc cafe daily bonus: " + e); // error
		}
		
		// Update data for online players.
		L2World.getInstance().getPlayers().stream().forEach(player ->
		{
			player.getAccountVariables().remove(AccountVariables.PC_CAFE_POINTS_TODAY);
			player.getAccountVariables().storeMe();
			player.getVariables().remove(PlayerVariables.USED_PC_LOTTERY_TICKET);
			player.getVariables().storeMe();
		});
		
		_log.info("Daily pc cafe daily bonus has been resetted.");
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
	
	@Override
	public String getName()
	{
		return NAME;
	}
}
