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
package com.l2jserver.gameserver.network.serverpackets;

public class ExGoodsInventoryResult extends L2GameServerPacket
{
	public static int NOTHING = 1;
	public static int SUCCESS = 2;
	public static int ERROR = -1;
	public static int TRY_AGAIN_LATER = -2;
	public static int INVENTORY_FULL = -3;
	public static int NOT_CONNECT_TO_PRODUCT_SERVER = -4;
	public static int CANT_USE_AT_TRADE_OR_PRIVATE_SHOP = -5;
	public static int NOT_EXISTS = -6;
	public static int TO_MANY_USERS_TRY_AGAIN_INVENTORY = -101;
	public static int TO_MANY_USERS_TRY_AGAIN = -102;
	public static int PREVIOS_REQUEST_IS_NOT_COMPLETE = -103;
	public static int NOTHING2 = -104;
	public static int ALREADY_RETRACTED = -105;
	public static int ALREADY_RECIVED = -106;
	public static int PRODUCT_CANNOT_BE_RECEIVED_AT_CURRENT_SERVER = -107;
	public static int PRODUCT_CANNOT_BE_RECEIVED_AT_CURRENT_PLAYER = -108;
	
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
