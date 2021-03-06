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

import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Мо3олЬ
 */
public class RequestExBR_BuyProduct extends L2GameClientPacket
{
	private static final String _C__D0_8C_REQUESTBRBUYPRODUCT = "[C] D0 8C RequestBrBuyProduct";
	
	private int _productId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		_productId = readD();
		_count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		ProductItemData.getInstance().giveProduct(activeChar, _productId, _count);
	}
	
	@Override
	public String getType()
	{
		return _C__D0_8C_REQUESTBRBUYPRODUCT;
	}
}