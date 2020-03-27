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

import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;
import com.l2jserver.gameserver.model.primeshop.L2ProductItemComponent;

public class ExBR_ProductInfo extends L2GameServerPacket
{
	private final L2ProductItem _productId;
	
	public ExBR_ProductInfo(final int productId)
	{
		_productId = ProductItemData.getInstance().getProduct(productId);
	}
	
	@Override
	protected void writeImpl()
	{
		if (_productId == null)
		{
			return;
		}
		
		writeC(0xFE);
		writeH(0xD7);
		
		writeD(_productId.getProductId());
		writeD(_productId.getPrice());
		writeD(_productId.getComponents().size());
		
		for (L2ProductItemComponent com : _productId.getComponents())
		{
			writeD(com.getId());
			writeD(com.getCount());
			writeD(com.getWeight());
			writeD(com.isDropable() ? 1 : 0); // 0 - dont drop/trade
		}
	}
}
