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
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerDelete;
import com.l2jserver.gameserver.network.serverpackets.CharacterDeleteFail;
import com.l2jserver.gameserver.network.serverpackets.CharacterDeleteSuccess;
import com.l2jserver.gameserver.network.serverpackets.CharacterSelectionInfo;
import com.l2jserver.gameserver.util.FloodProtectors;
import com.l2jserver.gameserver.util.FloodProtectors.Action;

public final class RequestCharacterDelete extends L2GameClientPacket
{
	private static final String _C__0C_REQUESTCHARACTERDELETE = "[C] 0D RequestCharacterDelete";
	
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
			sendPacket(CharacterDeleteFail.REASON_DELETION_FAILED);
			return;
		}
		
		try
		{
			byte answer = getClient().markToDeleteChar(_slot);
			
			switch (answer)
			{
				default:
				case -1: // Error
					break;
				case 0: // Success!
					sendPacket(CharacterDeleteSuccess.STATIC_PACKET);
					final CharSelectInfoPackage charInfo = getClient().getCharSelection(_slot);
					EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), getClient()), Containers.Players());
					break;
				case 1:
					sendPacket(CharacterDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER);
					break;
				case 2:
					sendPacket(CharacterDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED);
					break;
			}
		}
		catch (Exception e)
		{
			LOG.error("", e);
		}
		
		CharacterSelectionInfo csi = new CharacterSelectionInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1, 0);
		sendPacket(csi);
		getClient().setCharSelection(csi.getCharInfo());
	}
	
	@Override
	public String getType()
	{
		return _C__0C_REQUESTCHARACTERDELETE;
	}
}
