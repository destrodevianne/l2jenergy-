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

import java.util.Map;

import com.l2jserver.gameserver.model.L2PremiumItem;

public class ExGoodsInventoryInfo extends L2GameServerPacket
{
	private final Map<Integer, L2PremiumItem> _premiumItemMap;
	
	public ExGoodsInventoryInfo(Map<Integer, L2PremiumItem> premiumItemMap)
	{
		_premiumItemMap = premiumItemMap;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xE3);
		
		if (_premiumItemMap.isEmpty())
		{
			writeH(0x00);
		}
		else
		{
			writeH(_premiumItemMap.size());
			for (Map.Entry<Integer, L2PremiumItem> entry : _premiumItemMap.entrySet())
			{
				writeQ(entry.getKey());
				writeC(0x00); // goodsType 0 - берем из датки GoodsIcon / 1 - берем из обычного места
				writeD(10003); // goodsIconID ид иконки
				writeS(entry.getValue().getSender()); // goodsName
				writeS(null); // goodsDesc //entry.getValue().getSenderMessage()
				writeQ(0x00); // goodsDate время до удаления итема
				writeC(0x02); // goodsCondition 0 1 - COLOR_DEFAULT 2 - COLOR_YELLOW
				writeC(0x00); // goodsGift
				
				writeS(null); // goodsSender
				writeS(null); // goodsSenderMessage
				
				writeH(0x01); // size
				writeD(entry.getValue().getItemId());
				writeD((int) entry.getValue().getCount());
			}
		}
	}
}