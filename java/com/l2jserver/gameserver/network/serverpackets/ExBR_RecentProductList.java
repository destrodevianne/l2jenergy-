/*
 * Copyright (C) 2004-2018 L2J Server
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

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;

public class ExBR_RecentProductList extends L2GameServerPacket
{
	private List<L2ProductItem> _itemsList = new ArrayList<>();
	
	public ExBR_RecentProductList(int charId)
	{
		_itemsList = ProductItemData.getInstance().getRecentListByOID(charId);
		DAOFactory.getInstance().getItemMallDAO().recentListByItem(charId);
	}
	
	@Override
	protected void writeImpl()
	{
		if ((_itemsList == null) || _itemsList.isEmpty())
		{
			return;
		}
		
		writeC(0xFE);
		writeH(0xDC);
		writeD(_itemsList.size());
		
		for (L2ProductItem template : _itemsList)
		{
			writeD(template.getProductId());
			writeH(template.getCategory());
			writeD(template.getPrice());
			writeD(template.getEventFlag().ordinal());// show tab 2-th group 1 - event 2 - best 3 - event & best
			writeD((int) template.getStartSaleDate()); // start sale
			writeD((int) template.getEndSaleDate()); // end sale
			writeC(template.getDayWeek()); // day week
			writeC(template.getStartSaleHour());
			writeC(template.getStartSaleMin());
			writeC(template.getEndSaleHour());
			writeC(template.getEndSaleMin());
			writeD(template.getCurrentStock()); // current stock
			writeD(template.getMaxStock()); // max stock
		}
	}
}
