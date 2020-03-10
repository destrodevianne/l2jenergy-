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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.ItemAuctionDAO;
import com.l2jserver.gameserver.model.itemauction.ItemAuctionBid;

/**
 * @author Мо3олЬ
 */
public class ItemAuctionDAOMySQLImpl implements ItemAuctionDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemAuctionDAOMySQLImpl.class);
	
	private static final String SELECT_ITEM_AUCTION_ID = "SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1";
	private static final String DELETE_ITEM_AUCTION_BID = "DELETE FROM item_auction_bid WHERE auctionId = ? AND playerObjId = ?";
	private static final String INSERT_ITEM_AUCTION_BID = "INSERT INTO item_auction_bid (auctionId, playerObjId, playerBid) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playerBid = ?";
	private static final String DELETE_ITEM_AUCTION_ID = "DELETE FROM item_auction WHERE auctionId=?";
	private static final String DELETE_ITEM_AUCTION_BID_ID = "DELETE FROM item_auction_bid WHERE auctionId=?";
	
	@Override
	public void selectAuction(AtomicInteger auctionId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(SELECT_ITEM_AUCTION_ID))
		{
			if (rs.next())
			{
				auctionId.set(rs.getInt(1) + 1);
			}
		}
		catch (Exception e)
		{
			LOG.error("Failed loading auctions!", e);
		}
	}
	
	@Override
	public void deleteAuction(final int auctionId)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(DELETE_ITEM_AUCTION_ID))
			{
				ps.setInt(1, auctionId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_ITEM_AUCTION_BID_ID))
			{
				ps.setInt(1, auctionId);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOG.error("Failed deleting auction ID {}!", auctionId, e);
		}
	}
	
	@Override
	public void updatePlayerBidInternal(int auctionId, final ItemAuctionBid bid, final boolean delete)
	{
		final String query = delete ? DELETE_ITEM_AUCTION_BID : INSERT_ITEM_AUCTION_BID;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setInt(1, auctionId);
			ps.setInt(2, bid.getPlayerObjId());
			if (!delete)
			{
				ps.setLong(3, bid.getLastBid());
				ps.setLong(4, bid.getLastBid());
			}
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("", e);
		}
	}
}
