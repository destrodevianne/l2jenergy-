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

import com.l2jserver.gameserver.enums.ClanPrivilege;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ManagePledgePower;

public final class RequestPledgePower extends L2GameClientPacket
{
	private static final String _C__CC_REQUESTPLEDGEPOWER = "[C] CC RequestPledgePower";
	
	private int _rank;
	private int _action;
	private int _privs;
	
	@Override
	protected void readImpl()
	{
		_rank = readD();
		_action = readD();
		if (_action == 2)
		{
			_privs = readD();
		}
		else
		{
			_privs = 0;
		}
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (_action == 2)
		{
			if ((_rank < L2Clan.RANK_FIRST) || (_rank > L2Clan.RANK_LAST))
			{
				return;
			}
			
			if ((player.getClan() != null) && player.isClanLeader()) // TODO: if (activeChar.getClan() != null && (activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS)
			// if (player.isClanLeader())
			{
				if (_rank == 9)
				{
					// The rights below cannot be bestowed upon Academy members:
					// Join a clan or be dismissed
					// Title management, crest management, master management, level management,
					// bulletin board administration
					// Clan war, right to dismiss, set functions
					// Auction, manage taxes, attack/defend registration, mercenary management
					// => Leaves only CP_CL_VIEW_WAREHOUSE, CP_CH_OPEN_DOOR, CP_CS_OPEN_DOOR? // CS_USE_FUNCTIONS взято из jts, CH_USE_FUNCTIONS - нету (взято из jts)
					_privs &= ClanPrivilege.CL_VIEW_WAREHOUSE.getBitmask() | ClanPrivilege.CH_OPEN_DOOR.getBitmask() | ClanPrivilege.CS_OPEN_DOOR.getBitmask() | ClanPrivilege.CS_USE_FUNCTIONS.getBitmask();
				}
				player.getClan().setRankPrivs(_rank, _privs);
				player.getClan().updatePrivsForRank(_rank); // TODO: test
			}
		}
		else if (player.getClan() != null)
		{
			player.sendPacket(new ManagePledgePower(player, _action, _rank));
		}
		else
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__CC_REQUESTPLEDGEPOWER;
	}
}