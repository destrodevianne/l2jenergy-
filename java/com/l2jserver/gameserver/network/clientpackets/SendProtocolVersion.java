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
package com.l2jserver.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.SendStatus;
import com.l2jserver.gameserver.network.serverpackets.VersionCheck;
import com.l2jserver.gameserver.util.LoggingUtils;

public final class SendProtocolVersion extends L2GameClientPacket
{
	private static final String _C__0E_SENDPROTOCOLVERSION = "[C] 0E SendProtocolVersion";
	private static final Logger LOG_ACCOUNTING = LoggerFactory.getLogger("accounting");
	
	private int _version;
	
	@Override
	protected void readImpl()
	{
		_version = readD();
	}
	
	@Override
	protected void runImpl()
	{
		// this packet is never encrypted
		if (_version == -2)
		{
			// this is just a ping attempt from the new C2 client
			getClient().close((L2GameServerPacket) null);
		}
		else if (_version == -3)
		{
			LoggingUtils.logAccounting(LOG_ACCOUNTING, "Status request from IP:", new Object[]
			{
				_version,
				getClient().getConnectionAddress()
			});
			getClient().close(new SendStatus());
			return;
		}
		else if ((_version < ServerConfig.MIN_PROTOCOL_REVISION) || (_version > ServerConfig.MAX_PROTOCOL_REVISION))
		{
			LoggingUtils.logAccounting(LOG_ACCOUNTING, "Wrong protocol", new Object[]
			{
				_version,
				getClient()
			});
			getClient().setProtocolOk(false);
			getClient().close(new VersionCheck(getClient().enableCrypt(), 0));
		}
		else
		{
			getClient().sendPacket(new VersionCheck(getClient().enableCrypt(), 1));
			getClient().setProtocolOk(true);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__0E_SENDPROTOCOLVERSION;
	}
}
