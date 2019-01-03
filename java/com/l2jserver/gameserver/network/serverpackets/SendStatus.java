/*
 * Copyright (C) 2004-2019 L2J Server
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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.Random;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Мо3олЬ
 */
public class SendStatus extends L2GameServerPacket
{
	private static final long MIN_UPDATE_PERIOD = 30000;
	private static int online_players = 0;
	private static int max_online_players = 0;
	private static int online_priv_store = 0;
	private static long last_update = 0;
	
	public SendStatus()
	{
		if ((System.currentTimeMillis() - last_update) < MIN_UPDATE_PERIOD)
		{
			return;
		}
		last_update = System.currentTimeMillis();
		int i = 0;
		int j = 0;
		for (L2PcInstance player : L2World.getInstance().getPlayers())
		{
			i++;
			if (player.isInStoreMode() && (!Config.SENDSTATUS_TRADE_JUST_OFFLINE || player.isInOfflineMode()))
			{
				j++;
			}
		}
		online_players = i;
		online_priv_store = (int) Math.floor(j * Config.SENDSTATUS_TRADE_MOD);
		max_online_players = Math.max(max_online_players, online_players);
	}
	
	@Override
	protected final void writeImpl()
	{
		Random ppc = new Random();
		
		writeC(0x00); // Packet ID
		writeD(0x01); // World ID
		writeD(max_online_players); // Max Online
		writeD(online_players); // Current Online
		writeD(online_players); // Current Online
		writeD(online_priv_store); // Priv.Store Chars
		if (Config.RWHO_SEND_TRASH)
		{
			writeH(0x30);
			writeH(0x2C);
			writeH(0x36);
			writeH(0x2C);
			
			if (Config.RWHO_ARRAY[12] == Config.RWHO_KEEP_STAT)
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
						Config.RWHO_ARRAY[x] = 44;
					}
					else
					{
						Config.RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
					}
				}
				Config.RWHO_ARRAY[11] = 37265 + ppc.nextInt((z * 2) + 3);
				Config.RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
				z = 36224 + ppc.nextInt(z * 2);
				Config.RWHO_ARRAY[9] = z;
				Config.RWHO_ARRAY[10] = z;
				Config.RWHO_ARRAY[12] = 1;
			}
			
			for (int z = 0; z < 8; z++)
			{
				if (z == 3)
				{
					Config.RWHO_ARRAY[z] -= 1;
				}
				writeH(Config.RWHO_ARRAY[z]);
			}
			writeD(Config.RWHO_ARRAY[8]);
			writeD(Config.RWHO_ARRAY[9]);
			writeD(Config.RWHO_ARRAY[10]);
			writeD(Config.RWHO_ARRAY[11]);
			Config.RWHO_ARRAY[12]++;
			writeD(0x00);
			writeD(0x02);
		}
	}
}