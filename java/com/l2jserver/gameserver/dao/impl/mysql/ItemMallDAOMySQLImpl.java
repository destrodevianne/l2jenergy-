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
import com.l2jserver.gameserver.model.primeshop.L2ProductItem;

/**
 * @author Мо3олЬ
 */
public class ItemMallDAOMySQLImpl implements ItemMallDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemMallDAOMySQLImpl.class);
	
	private static final String INSERT_ITEM_MALL_PRODUCT = "INSERT INTO character_item_mall_transactions (charId, productId, quantity, maxStock) values (?,?,?,?)";
	private static final String SELECT_ITEM_MALL_PRODUCT = "SELECT productId FROM character_item_mall_transactions WHERE charId=? ORDER BY transactionTime DESC";
	private static final String SELECT_ITEM_MALL_MAX_STOCK = "SELECT maxStock FROM character_item_mall_transactions WHERE productId=?";
	private static final String SELECT_ITEM_MALL_ACTUAL_STOCK = "SELECT quantity FROM character_item_mall_transactions WHERE productId=?";
	
	private final List<L2ProductItem> _itemsList = new ArrayList<>();
	
	@Override
	public void addPoduct(int objectId, int productId, int quantity, int maxStock)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(INSERT_ITEM_MALL_PRODUCT))
		{
			st.setInt(1, objectId);
			st.setInt(2, productId);
			st.setInt(3, quantity);
			st.setInt(4, maxStock);
			st.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.error("Could not save Item Mall transaction!", e);
		}
	}
	
	@Override
	public void loadPoducts(int objectId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_ITEM_MALL_PRODUCT))
		{
			statement.setInt(1, objectId);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final L2ProductItem product = ProductItemData.getInstance().getProduct(rset.getInt("productId"));
					if ((product != null) && !_itemsList.contains(product))
					{
						_itemsList.add(product);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not restore Item Mall transaction!", e);
		}
	}
	
	@Override
	public int setMaxStock(int productId)
	{
		int maxStock = 0;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_ITEM_MALL_MAX_STOCK))
		{
			ps.setInt(1, productId);
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					return maxStock = rset.getInt("maxStock");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Could not get max stock of products in Item Mall!", e);
		}
		return maxStock;
	}
	
	@Override
	public int setCurrentStock(int productId)
	{
		int getCurrentStock = 0;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_ITEM_MALL_ACTUAL_STOCK))
		{
			ps.setInt(1, productId);
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					return getCurrentStock = rset.getInt("quantity");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Could not get actual stock of products in Item Mall!", e);
		}
		return getCurrentStock;
	}
}
