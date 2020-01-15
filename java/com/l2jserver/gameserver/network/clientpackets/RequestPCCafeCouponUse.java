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
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: (ch) S
 * @author -Wooden-, Мо3олЬ
 */
public final class RequestPCCafeCouponUse extends L2GameClientPacket
{
	private static final String _C__D0_19_REQUESTPCCAFECOUPONUSE = "[C] D0:19 RequestPCCafeCouponUse";
	
	private String _couponCode;
	
	@Override
	protected void readImpl()
	{
		_couponCode = readS();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		DAOFactory.getInstance().getPcCafeDAO().requestEnterCode(player, _couponCode);
	}
	
	@Override
	public String getType()
	{
		return _C__D0_19_REQUESTPCCAFECOUPONUSE;
	}
}
