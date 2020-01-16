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
package com.l2jserver.gameserver.network.serverpackets;

public class ExGoodsInventoryResult extends L2GameServerPacket
{
	public static final ExGoodsInventoryResult NOTHING = new ExGoodsInventoryResult(1);
	public static final ExGoodsInventoryResult SUCCESS = new ExGoodsInventoryResult(2);
	public static final ExGoodsInventoryResult ERROR = new ExGoodsInventoryResult(-1);
	public static final ExGoodsInventoryResult TRY_AGAIN_LATER = new ExGoodsInventoryResult(-2);
	public static final ExGoodsInventoryResult INVENTORY_FULL = new ExGoodsInventoryResult(-3);
	public static final ExGoodsInventoryResult NOT_CONNECT_TO_PRODUCT_SERVER = new ExGoodsInventoryResult(-4);
	public static final ExGoodsInventoryResult CANT_USE_AT_TRADE_OR_PRIVATE_SHOP = new ExGoodsInventoryResult(-5);
	public static final ExGoodsInventoryResult NOT_EXISTS = new ExGoodsInventoryResult(-6);
	public static final ExGoodsInventoryResult TO_MANY_USERS_TRY_AGAIN_INVENTORY = new ExGoodsInventoryResult(-101);
	public static final ExGoodsInventoryResult TO_MANY_USERS_TRY_AGAIN = new ExGoodsInventoryResult(-102);
	public static final ExGoodsInventoryResult PREVIOS_REQUEST_IS_NOT_COMPLETE = new ExGoodsInventoryResult(-103);
	public static final ExGoodsInventoryResult NOTHING2 = new ExGoodsInventoryResult(-104);
	public static final ExGoodsInventoryResult ALREADY_RETRACTED = new ExGoodsInventoryResult(-105);
	public static final ExGoodsInventoryResult ALREADY_RECIVED = new ExGoodsInventoryResult(-106);
	public static final ExGoodsInventoryResult PRODUCT_CANNOT_BE_RECEIVED_AT_CURRENT_SERVER = new ExGoodsInventoryResult(-107);
	public static final ExGoodsInventoryResult PRODUCT_CANNOT_BE_RECEIVED_AT_CURRENT_PLAYER = new ExGoodsInventoryResult(-108);
	
	private final int _result;
	
	public ExGoodsInventoryResult(int result)
	{
		_result = result;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xE4);
		writeD(_result);
	}
}
