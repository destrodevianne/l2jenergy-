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
package com.l2jserver.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;
import com.l2jserver.gameserver.model.primeshop.L2ProductItemComponent;
import com.l2jserver.gameserver.network.serverpackets.ExBR_BuyProduct;
import com.l2jserver.gameserver.network.serverpackets.ExBR_GamePoint;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;

/**
 * @author Мо3олЬ
 */
public class RequestExBR_BuyProduct extends L2GameClientPacket
{
	private static final String _C__D0_8B_REQUESTBRBUYPRODUCT = "[C] D0 8C RequestBrBuyProduct";
	
	private final ConcurrentHashMap<Integer, List<L2ProductItem>> _recentList = new ConcurrentHashMap<>();
	
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
		
		if ((_count > 99) || (_count < 0))
		{
			return;
		}
		L2ProductItem product = ProductItemData.getInstance().getItem(_productId);
		if (product == null)
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
			return;
		}
		
		if (!ProductItemData.getInstance().calcStartEndTime(product.getProductId()))
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_SALE_PERIOD_ENDED));
			return;
		}
		
		final long totalPoints = product.getPrice() * _count;
		
		if (totalPoints < 0)
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
			return;
		}
		
		if (totalPoints > activeChar.getPrimePoints())
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_ENOUGH_POINTS));
			return;
		}
		
		int totalWeight = 0;
		for (final L2ProductItemComponent com : product.getComponents())
		{
			totalWeight += com.getWeight();
		}
		totalWeight *= _count; // увеличиваем вес согласно количеству
		int totalCount = 0;
		
		for (final L2ProductItemComponent com : product.getComponents())
		{
			final L2Item item = ItemTable.getInstance().getTemplate(com.getId());
			if (item == null)
			{
				activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT_ITEM));
				return; // what
			}
			
			totalCount += item.isStackable() ? 1 : com.getCount() * _count;
		}
		
		if (!activeChar.getInventory().validateCapacity(totalCount) || !activeChar.getInventory().validateWeight(totalWeight))
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_INVENTORY_FULL));
			return;
		}
		for (L2ProductItemComponent comp : product.getComponents())
		{
			activeChar.addItem("Buy Product" + _productId, comp.getId(), comp.getCount() * _count, activeChar, true);
		}
		
		activeChar.setPrimePoints(activeChar.getPrimePoints() - totalPoints);
		
		if (_recentList.get(activeChar.getObjectId()) == null)
		{
			List<L2ProductItem> charList = new ArrayList<>();
			charList.add(product);
			_recentList.put(activeChar.getObjectId(), charList);
		}
		else
		{
			_recentList.get(activeChar.getObjectId()).add(product);
		}
		
		final StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);
		
		activeChar.sendPacket(new ExBR_GamePoint(activeChar));
		activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_OK));
		activeChar.broadcastUserInfo();
		DAOFactory.getInstance().getItemMallDAO().requestBuyItem(activeChar, _productId, (byte) _count);
	}
	
	@Override
	public String getType()
	{
		return _C__D0_8B_REQUESTBRBUYPRODUCT;
	}
}