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

import com.l2jserver.commons.util.StringUtil;
import com.l2jserver.gameserver.enums.ClanPrivilege;
import com.l2jserver.gameserver.model.L2ClanMember;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public class RequestGiveNickName extends L2GameClientPacket
{
	private static final String _C__0B_REQUESTGIVENICKNAME = "[C] 0B RequestGiveNickName";
	
	private String _name;
	private String _title;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_title = readS();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!StringUtil.isValidString(_title, "^[a-zA-Z0-9 !@#$&()\\-`.+,/\"]*{0,16}$"))
		{
			activeChar.sendPacket(SystemMessageId.NOT_WORKING_PLEASE_TRY_AGAIN_LATER);
			return;
		}
		
		// Noblesse can bestow a title to themselves
		if (activeChar.isNoble() && _name.equalsIgnoreCase(activeChar.getName()))
		{
			activeChar.setTitle(_title);
			activeChar.sendPacket(SystemMessageId.TITLE_CHANGED);
			activeChar.broadcastTitleInfo();
		}
		else
		{
			// Can the player change/give a title?
			if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_GIVE_TITLE))
			{
				activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			if (activeChar.getClan().getLevel() < 3)
			{
				activeChar.sendPacket(SystemMessageId.CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE);
				return;
			}
			
			L2ClanMember member = activeChar.getClan().getClanMember(_name);
			if (member != null)
			{
				L2PcInstance playerMember = member.getPlayerInstance();
				if (playerMember != null)
				{
					// is target from the same clan?
					playerMember.setTitle(_title);
					playerMember.sendPacket(SystemMessageId.TITLE_CHANGED);
					if (activeChar != playerMember)
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_TITLE_CHANGED_TO_S2).addCharName(playerMember).addString(_title));
					}
					playerMember.broadcastTitleInfo();
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
				}
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.TARGET_MUST_BE_IN_CLAN);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return _C__0B_REQUESTGIVENICKNAME;
	}
}
