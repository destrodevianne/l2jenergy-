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

import java.util.Random;

import com.l2jserver.gameserver.configuration.config.custom.CustomConfig;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class SendStatus extends L2GameServerPacket
{
	private static final long MIN_UPDATE_PERIOD = 30000;
	private static int ONLINE_PLAYERS = 0;
	private static int MAX_ONLINE_PLAYERS = 0;
	private static int ONLINE_PRIVS_STORE = 0;
	private static long LAST_UPDATE = 0;
	
	public static int RWHO_ARRAY[] = new int[13];
	
	public SendStatus()
	{
		if ((System.currentTimeMillis() - LAST_UPDATE) < MIN_UPDATE_PERIOD)
		{
			return;
		}
		LAST_UPDATE = System.currentTimeMillis();
		int i = 0;
		int j = 0;
		for (L2PcInstance player : L2World.getInstance().getPlayers())
		{
			i++;
			if (player.isInStoreMode() && (!CustomConfig.SENDSTATUS_TRADE_JUST_OFFLINE || player.isInOfflineMode()))
			{
				j++;
			}
		}
		ONLINE_PLAYERS = i;
		ONLINE_PRIVS_STORE = (int) Math.floor(j * CustomConfig.SENDSTATUS_TRADE_MOD);
		MAX_ONLINE_PLAYERS = Math.max(MAX_ONLINE_PLAYERS, ONLINE_PLAYERS);
	}
	
	@Override
	protected final void writeImpl()
	{
		Random ppc = new Random();
		
		writeC(0x00); // Packet ID
		writeD(0x01); // World ID
		writeD(MAX_ONLINE_PLAYERS); // Max Online
		writeD(ONLINE_PLAYERS); // Current Online
		writeD(ONLINE_PLAYERS); // Current Online
		writeD(ONLINE_PRIVS_STORE); // Priv.Store Chars
		if (CustomConfig.RWHO_SEND_TRASH)
		{
			writeH(0x30);
			writeH(0x2C);
			writeH(0x36);
			writeH(0x2C);
			
			if (RWHO_ARRAY[12] == CustomConfig.RWHO_KEEP_STAT)
			{
				int z;
				z = ppc.nextInt(6);
				if (z == 0)
				{
					z += 2;
				}
				for (int x = 0; x < 8; x++)
				{
					if (x == 4)
					{
						RWHO_ARRAY[x] = 44;
					}
					else
					{
						RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
					}
				}
				RWHO_ARRAY[11] = 37265 + ppc.nextInt((z * 2) + 3);
				RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
				z = 36224 + ppc.nextInt(z * 2);
				RWHO_ARRAY[9] = z;
				RWHO_ARRAY[10] = z;
				RWHO_ARRAY[12] = 1;
			}
			
			for (int z = 0; z < 8; z++)
			{
				if (z == 3)
				{
					RWHO_ARRAY[z] -= 1;
				}
				writeH(RWHO_ARRAY[z]);
			}
			writeD(RWHO_ARRAY[8]);
			writeD(RWHO_ARRAY[9]);
			writeD(RWHO_ARRAY[10]);
			writeD(RWHO_ARRAY[11]);
			RWHO_ARRAY[12]++;
			writeD(0x00);
			writeD(0x02);
		}
	}
}