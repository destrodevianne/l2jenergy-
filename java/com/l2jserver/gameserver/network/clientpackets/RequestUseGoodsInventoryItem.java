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

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.model.L2PremiumItem;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExGoodsInventoryInfo;
import com.l2jserver.gameserver.network.serverpackets.ExGoodsInventoryResult;

public class RequestUseGoodsInventoryItem extends L2GameClientPacket
{
	private static final String _C__DO_92_REQUESTUSEGOODSINVENTORYITEM = "[C] D0:92 RequestUseGoodsInventoryItem";
	
	private long _itemId;
	private int _goodsType;
	private long _itemCount;
	
	@Override
	protected void readImpl()
	{
		_goodsType = readC();
		_itemId = readQ();
		if (_goodsType != 1)
		{
			_itemCount = readQ();
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(ExGoodsInventoryResult.PREVIOS_REQUEST_IS_NOT_COMPLETE);
		}
		
		if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			activeChar.sendPacket(ExGoodsInventoryResult.CANT_USE_AT_TRADE_OR_PRIVATE_SHOP);
			return;
		}
		
		if (activeChar.getPremiumItemList().isEmpty())
		{
			activeChar.sendPacket(ExGoodsInventoryResult.NOT_EXISTS);
			return;
		}
		
		if (activeChar.getInventory().getSize(false) >= (activeChar.getInventoryLimit() * 0.8))
		{
			activeChar.sendPacket(ExGoodsInventoryResult.INVENTORY_FULL);
			return;
		}
		
		// TODO: need fix
		// if (LoginServerThread.getInstance().isShutdown())
		// {
		// activeChar.sendPacket(new ExGoodsInventoryResult(ExGoodsInventoryResult.NOT_CONNECT_TO_PRODUCT_SERVER));
		// }
		
		final L2PremiumItem _premiumItem = activeChar.getPremiumItemList().get((int) _itemId);
		
		if (_premiumItem == null)
		{
			return;
		}
		
		final boolean stackable = ItemTable.getInstance().getTemplate(_premiumItem.getItemId()).isStackable();
		
		if ((_itemCount != 0) && (_premiumItem.getCount() < _itemCount))
		{
			return;
		}
		
		if (!stackable)
		{
			for (int i = 0; i < _itemCount; i++)
			{
				activeChar.addItem("premiumItem", _premiumItem.getItemId(), _itemCount, activeChar.getTarget(), true);
			}
		}
		else
		{
			activeChar.addItem("premiumItem", _premiumItem.getItemId(), _itemCount, activeChar.getTarget(), true);
		}
		
		long itemsLeft = _premiumItem.getCount() - _itemCount;
		
		if (_itemCount < _premiumItem.getCount())
		{
			_premiumItem.updateCount(itemsLeft);
			DAOFactory.getInstance().getPremiumItemDAO().update(activeChar, (int) _itemId, _premiumItem.getCount() - _itemCount);
		}
		else
		{
			DAOFactory.getInstance().getPremiumItemDAO().delete(activeChar, (int) _itemId);
		}
		activeChar.sendPacket(new ExGoodsInventoryInfo(activeChar.getPremiumItemList()));
	}
	
	@Override
	public String getType()
	{
		return _C__DO_92_REQUESTUSEGOODSINVENTORYITEM;
	}
}
