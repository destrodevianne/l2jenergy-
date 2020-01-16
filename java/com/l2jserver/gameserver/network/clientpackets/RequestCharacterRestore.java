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

import com.l2jserver.gameserver.model.CharSelectInfoPackage;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerRestore;
import com.l2jserver.gameserver.network.serverpackets.CharacterSelectionInfo;
import com.l2jserver.gameserver.util.FloodProtectors;
import com.l2jserver.gameserver.util.FloodProtectors.Action;

public final class RequestCharacterRestore extends L2GameClientPacket
{
	private static final String _C__7B_REQUESTCHARACTERRESTORE = "[C] 7B RequestCharacterRestore";
	
	private int _slot;
	
	@Override
	protected void readImpl()
	{
		_slot = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.CHARACTER_SELECT))
		{
			return;
		}
		
		getClient().markRestoredChar(_slot);
		CharacterSelectionInfo cl = new CharacterSelectionInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1, 0);
		sendPacket(cl);
		getClient().setCharSelection(cl.getCharInfo());
		final CharSelectInfoPackage charInfo = getClient().getCharSelection(_slot);
		EventDispatcher.getInstance().notifyEvent(new OnPlayerRestore(charInfo.getObjectId(), charInfo.getName(), getClient()));
	}
	
	@Override
	public String getType()
	{
		return _C__7B_REQUESTCHARACTERRESTORE;
	}
}
