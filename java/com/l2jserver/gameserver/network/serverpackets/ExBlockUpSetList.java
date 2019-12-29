/*
 * Copyright (C) 2004-2019 L2jEnergy Server
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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.List;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class ExBlockUpSetList extends L2GameServerPacket
{
	// Players Lists
	private List<L2PcInstance> _bluePlayers;
	private List<L2PcInstance> _redPlayers;
	// Common Values
	private int _roomNumber;
	private L2PcInstance _player;
	private boolean _isRedTeam;
	private int _seconds;
	private final int _type;
	
	/**
	 * Show Minigame Waiting List to Player
	 * @param redPlayers Red Players List
	 * @param bluePlayers Blue Players List
	 * @param roomNumber Arena/Room ID
	 */
	public ExBlockUpSetList(List<L2PcInstance> redPlayers, List<L2PcInstance> bluePlayers, int roomNumber)
	{
		_redPlayers = redPlayers;
		_bluePlayers = bluePlayers;
		_roomNumber = roomNumber - 1;
		_type = 0;
	}
	
	/**
	 * @param player Player Instance
	 * @param isRedTeam Is Player from Red Team?
	 */
	public ExBlockUpSetList(L2PcInstance player, boolean isRedTeam, boolean remove)
	{
		_player = player;
		_isRedTeam = isRedTeam;
		
		_type = !remove ? 1 : 2;
	}
	
	/**
	 * Update Minigame Waiting List Time to Start
	 * @param seconds
	 */
	public ExBlockUpSetList(int seconds)
	{
		_seconds = seconds;
		_type = 3;
	}
	
	public ExBlockUpSetList(boolean isExCubeGameCloseUI)
	{
		_type = isExCubeGameCloseUI ? -1 : 4;
	}
	
	/**
	 * Add Player To Minigame Waiting List
	 * @param player Player Instance
	 * @param isRedTeam Is Player from Red Team?
	 */
	public ExBlockUpSetList(L2PcInstance player, boolean isRedTeam)
	{
		_player = player;
		_isRedTeam = isRedTeam;
		_type = 5;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x97);
		
		if (_type == -1) // ExCubeGameCloseUI
		{
			writeD(0xffffffff);
			return;
		}
		
		writeD(_type);
		switch (_type)
		{
			case 0:
				writeD(0xffffffff);
				writeD(_roomNumber);
				
				writeD(_bluePlayers.size());
				for (L2PcInstance player : _bluePlayers)
				{
					writeD(player.getObjectId());
					writeS(player.getName());
				}
				writeD(_redPlayers.size());
				for (L2PcInstance player : _redPlayers)
				{
					writeD(player.getObjectId());
					writeS(player.getName());
				}
				break;
			case 1: // ExCubeGameAddPlayer
				writeD(0xffffffff);
				writeD(_isRedTeam ? 0x01 : 0x00);
				writeD(_player.getObjectId());
				writeS(_player.getName());
				break;
			case 2: // ExCubeGameRemovePlayer
				writeD(0xffffffff);
				writeD(_isRedTeam ? 0x01 : 0x00);
				writeD(_player.getObjectId());
				break;
			case 3: // ExCubeGameChangeTimeToStart
				writeD(_seconds);
				break;
			case 4: // ExCubeGameRequestReady
				break;
			case 5: // ExCubeGameChangeTeam
				writeD(_player.getObjectId());
				writeD(_isRedTeam ? 0x01 : 0x00);
				writeD(_isRedTeam ? 0x00 : 0x01);
				break;
		}
	}
}