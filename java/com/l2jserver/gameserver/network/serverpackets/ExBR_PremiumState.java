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
package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author GodKratos
 */
public class ExBR_PremiumState extends L2GameServerPacket
{
	private final L2PcInstance _player;
	
	public ExBR_PremiumState(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xD9);
		writeD(_player.getObjectId());
		writeC(_player.isPremium() ? 0x01 : 0x00);
	}
}
