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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.ItemMallDAO;
import com.l2jserver.gameserver.data.xml.impl.ProductItemData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;
import com.l2jserver.gameserver.network.serverpackets.ExBR_BuyProduct;

/**
 * @author Мо3олЬ
 */
public class ItemMallDAOMySQLImpl implements ItemMallDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemMallDAOMySQLImpl.class);
	
	private static final String INSERT_ITEM_MALL_PRODUCT = "INSERT INTO character_item_mall_transactions (charId, productId, quantity) values (?,?,?)";
	private static final String SELECT_ITEM_MALL_PRODUCT = "SELECT productId FROM character_item_mall_transactions WHERE charId=? ORDER BY transactionTime DESC";
	
	private final List<L2ProductItem> _itemsList = new ArrayList<>();
	
	@Override
	public void requestBuyItem(L2PcInstance activeChar, int productId, int count)
	{
		L2ProductItem product = ProductItemData.getInstance().getItem(productId);
		if (product == null)
		{
			activeChar.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
			return;
		}
		
		// Save transaction info at SQL table character_item_mall_transactions
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_ITEM_MALL_PRODUCT))
		{
			statement.setLong(1, activeChar.getObjectId());
			statement.setInt(2, product.getProductId());
			statement.setLong(3, count);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.error("Could not save Item Mall transaction: {}", e);
		}
	}
	
	@Override
	public void recentListByItem(int objId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_ITEM_MALL_PRODUCT))
		{
			statement.setInt(1, objId);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final L2ProductItem product = ProductItemData.getInstance().getItem(rset.getInt("productId"));
					if ((product != null) && !_itemsList.contains(product))
					{
						_itemsList.add(product);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not restore Item Mall transaction: {}", e);
		}
	}
}
