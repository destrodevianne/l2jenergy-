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

import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExGoodsInventoryInfo;
import com.l2jserver.gameserver.network.serverpackets.ExGoodsInventoryResult;
import com.l2jserver.gameserver.util.FloodProtectors;
import com.l2jserver.gameserver.util.FloodProtectors.Action;

public class RequestGoodsInventoryInfo extends L2GameClientPacket
{
	private static final String _C__DO_91_REQUESTGOODSINVENTORYINFO = "[C] D0:91 RequestGoodsInventoryInfo";
	
	public int _timeUpdate;
	
	@Override
	protected void readImpl()
	{
		_timeUpdate = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!FloodProtectors.performAction(getClient(), Action.TRANSACTION))
		{
			return;
		}
		if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			activeChar.sendPacket(new ExGoodsInventoryResult(ExGoodsInventoryResult.CANT_USE_AT_TRADE_OR_PRIVATE_SHOP));
			return;
		}
		if (activeChar.getPremiumItemList().isEmpty())
		{
			activeChar.sendPacket(new ExGoodsInventoryResult(ExGoodsInventoryResult.NOT_EXISTS));
			return;
		}
		activeChar.sendPacket(new ExGoodsInventoryInfo(activeChar.getPremiumItemList()));
	}
	
	@Override
	public String getType()
	{
		return _C__DO_91_REQUESTGOODSINVENTORYINFO;
	}
}
