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
package handlers.admincommandhandlers;

import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.network.SystemMessageId;

public class AdminDoorControl implements IAdminCommandHandler
{
	private static DoorData _doorTable = DoorData.getInstance();
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open",
		"admin_close",
		"admin_openall",
		"admin_closeall"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		try
		{
			if (command.startsWith("admin_open "))
			{
				int doorId = Integer.parseInt(command.substring(11));
				if (_doorTable.getDoor(doorId) != null)
				{
					_doorTable.getDoor(doorId).openMe();
				}
				else
				{
					for (Castle castle : CastleManager.getInstance().getCastles())
					{
						if (castle.getDoor(doorId) != null)
						{
							castle.getDoor(doorId).openMe();
						}
					}
				}
			}
			else if (command.startsWith("admin_close "))
			{
				int doorId = Integer.parseInt(command.substring(12));
				if (_doorTable.getDoor(doorId) != null)
				{
					_doorTable.getDoor(doorId).closeMe();
				}
				else
				{
					for (Castle castle : CastleManager.getInstance().getCastles())
					{
						if (castle.getDoor(doorId) != null)
						{
							castle.getDoor(doorId).closeMe();
						}
					}
				}
			}
			if (command.equals("admin_closeall"))
			{
				for (L2DoorInstance door : _doorTable.getDoors())
				{
					door.closeMe();
				}
				for (Castle castle : CastleManager.getInstance().getCastles())
				{
					for (L2DoorInstance door : castle.getDoors())
					{
						door.closeMe();
					}
				}
			}
			if (command.equals("admin_openall"))
			{
				for (L2DoorInstance door : _doorTable.getDoors())
				{
					door.openMe();
				}
				for (Castle castle : CastleManager.getInstance().getCastles())
				{
					for (L2DoorInstance door : castle.getDoors())
					{
						door.openMe();
					}
				}
			}
			if (command.equals("admin_open"))
			{
				L2Object target = activeChar.getTarget();
				if (target instanceof L2DoorInstance)
				{
					((L2DoorInstance) target).openMe();
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
			}
			
			if (command.equals("admin_close"))
			{
				L2Object target = activeChar.getTarget();
				if (target instanceof L2DoorInstance)
				{
					((L2DoorInstance) target).closeMe();
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
