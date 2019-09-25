/*
 * Copyright (C) 2004-2019 L2J Server
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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.configuration.config.events.PcCafeConfig;
import com.l2jserver.gameserver.dao.PcCafeDAO;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.variables.PlayerVariables;

/**
 * PcCafe DAO MySQL implementation.
 * @author Мо3олЬ
 */
public class PcCafeDAOMySQLImpl implements PcCafeDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(PcCafeDAOMySQLImpl.class);
	
	private static final String SELECT = "SELECT serial_code, coupon_use, coupon_value FROM pccafe_coupons WHERE serial_code=?";
	private static final String UPDATE = "UPDATE pccafe_coupons SET coupon_use=?, used_by=? WHERE serial_code=?";
	
	@Override
	public void requestEnterCode(L2PcInstance player, String couponCode)
	{
		int use, value = 0;
		
		final long newcode = Math.min(player.getVariables().getInt(PlayerVariables.PCC_CODE_ATTEMPTS, 0) + 1, (System.currentTimeMillis() / 1000) + (PcCafeConfig.ALT_PCBANG_POINTS_BAN_TIME * 60));
		
		if (!isValidName(couponCode))
		{
			player.getVariables().set(PlayerVariables.PCC_CODE_ATTEMPTS, newcode);
			player.sendMessage(MessagesData.getInstance().getMessage(player, "pc_cafe_wrong_coupon_code_format"));
			return;
		}
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT))
		{
			ps.setString(1, couponCode);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					use = rs.getInt("coupon_use");
					value = rs.getInt("coupon_value");
					
					if (use != 0)
					{
						player.getVariables().set(PlayerVariables.PCC_CODE_ATTEMPTS, newcode);
						player.sendMessage(MessagesData.getInstance().getMessage(player, "pc_cafe_coupon_code_already_been_used"));
						
					}
					else
					{
						calculateCodeReward(player, couponCode, value);
					}
					
				}
				player.getVariables().set(PlayerVariables.PCC_CODE_ATTEMPTS, newcode);
				player.sendMessage(MessagesData.getInstance().getMessage(player, "pc_cafe_wrong_coupon_code"));
			}
		}
		catch (Exception e)
		{
			LOG.error("Error while reading serial code.", e);
		}
	}
	
	public void calculateCodeReward(L2PcInstance player, String couponCode, int value)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE))
		{
			ps.setInt(1, 1);
			ps.setString(2, player.getName());
			ps.setString(3, couponCode);
			ps.execute();
			
			player.getVariables().hasVariable(PlayerVariables.PCC_CODE_ATTEMPTS);
			player.increasePcCafePoints(value, false);
		}
		catch (Exception e)
		{
			LOG.error("Error while calculating reward for serial code.", e);
		}
	}
	
	private boolean isValidName(String text)
	{
		return PcCafeConfig.ALT_PCBANG_POINTS_COUPON_TEMPLATE.matcher(text).matches();
	}
}
