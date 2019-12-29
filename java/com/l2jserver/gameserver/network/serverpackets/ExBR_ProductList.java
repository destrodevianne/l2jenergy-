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

import java.util.Collection;
import java.util.Collections;

import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;

public class ExBR_ProductList extends L2GameServerPacket
{
	private Collection<L2ProductItem> _items = Collections.emptyList();
	
	public ExBR_ProductList()
	{
		_items = ProductItemData.getInstance().getProducts();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xD6);
		writeD(_items.size());
		
		for (final L2ProductItem template : _items)
		{
			if (!ProductItemData.getInstance().calcStartEndTime(template.getProductId()))
			{
				continue;
			}
			
			writeD(template.getProductId()); // product id
			writeH(template.getCategory()); // category 1 - enchant 2 - supplies 3 - decoration 4 - package 5 - other
			writeD(template.getPrice()); // points
			writeD(template.getEventFlag().ordinal());// 1 - event 2 - best 3 - event & best
			writeD((int) template.getStartSaleDate()); // start sale
			writeD((int) template.getEndSaleDate()); // end sale
			writeC(template.getDayWeek()); // day week
			writeC(template.getStartSaleHour()); // start hour
			writeC(template.getStartSaleMin()); // start min
			writeC(template.getEndSaleHour()); // end hour
			writeC(template.getEndSaleMin()); // end min
			writeD(template.getCurrentStock()); // current stock
			writeD(template.getMaxStock()); // max stock
		}
	}
}
