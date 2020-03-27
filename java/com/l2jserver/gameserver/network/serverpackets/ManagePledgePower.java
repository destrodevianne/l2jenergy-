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

import com.l2jserver.commons.util.EnumIntBitmask;
import com.l2jserver.gameserver.enums.ClanPrivilege;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class ManagePledgePower extends L2GameServerPacket
{
	private final int _action;
	private final int _clanId;
	private final int _privs;
	
	public ManagePledgePower(final L2PcInstance player, final int action, final int rank)
	{
		_clanId = player.getClanId();
		_action = action;
		final EnumIntBitmask<ClanPrivilege> temp = player.getClan().getRankPrivs(rank);
		_privs = temp == null ? 0 : temp.getBitmask();
		player.sendPacket(new PledgeReceiveUpdatePower(_privs));
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_action == 1)
		{
			writeC(0x2a);
			writeD(_clanId);
			writeD(_action);
			writeD(_privs);
		}
	}
}
