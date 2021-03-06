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

import com.l2jserver.gameserver.configuration.config.community.CBasicConfig;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;

/**
 * RequestShowBoard client packet implementation.
 * @author Zoey76
 */
public final class RequestShowBoard extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected final void readImpl()
	{
		_unknown = readD();
	}
	
	@Override
	protected void runImpl()
	{
		CommunityBoardHandler.getInstance().handleParseCommand(CBasicConfig.BBS_DEFAULT, getActiveChar());
	}
	
	@Override
	public final String getType()
	{
		return "[C] 5E RequestShowBoard";
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}
