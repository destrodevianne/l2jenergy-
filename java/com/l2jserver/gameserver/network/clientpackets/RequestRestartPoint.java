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

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.enums.TeleportWhereType;
import com.l2jserver.gameserver.instancemanager.CHSiegeManager;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.ClanHallManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.L2SiegeClan;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.ClanHall;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.entity.clanhall.SiegableHall;

public final class RequestRestartPoint extends L2GameClientPacket
{
	private static final String _C__7D_REQUESTRESTARTPOINT = "[C] 7D RequestRestartPoint";
	
	protected int _requestedPointType;
	protected boolean _continuation;
	
	@Override
	protected void readImpl()
	{
		_requestedPointType = readD();
	}
	
	class DeathTask implements Runnable
	{
		final L2PcInstance activeChar;
		
		DeathTask(L2PcInstance _activeChar)
		{
			activeChar = _activeChar;
		}
		
		@Override
		public void run()
		{
			portPlayer(activeChar);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!activeChar.canRevive())
		{
			return;
		}
		
		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(true);
			return;
		}
		else if (!activeChar.isDead())
		{
			LOG.warn("Living player [{}] called RestartPointPacket! Ban this player!", activeChar.getName());
			return;
		}
		
		Castle castle = CastleManager.getInstance().getCastle(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		if ((castle != null) && castle.getSiege().isInProgress())
		{
			if ((activeChar.getClan() != null) && castle.getSiege().checkIsAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getAttackerRespawnDelay());
				if (castle.getSiege().getAttackerRespawnDelay() > 0)
				{
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "respawn_in_seconds").replace("%s%", (castle.getSiege().getAttackerRespawnDelay() / 1000) + ""));
				}
				return;
			}
		}
		
		portPlayer(activeChar);
	}
	
	protected final void portPlayer(final L2PcInstance activeChar)
	{
		Location loc = null;
		Castle castle = null;
		Fort fort = null;
		SiegableHall hall = null;
		boolean isInDefense = false;
		int instanceId = 0;
		
		// force jail
		if (activeChar.isJailed())
		{
			_requestedPointType = 27;
		}
		else if (activeChar.isFestivalParticipant())
		{
			_requestedPointType = 5;
		}
		switch (_requestedPointType)
		{
			case 1: // to clanhall
			{
				if ((activeChar.getClan() == null) || (activeChar.getClan().getHideoutId() == 0))
				{
					LOG.warn("Player [{}] called RestartPointPacket - To Clanhall and he doesn't have Clanhall!", activeChar.getName());
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CLANHALL);
				
				if ((ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()) != null) && (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case 2: // to castle
			{
				castle = CastleManager.getInstance().getCastle(activeChar);
				
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					// Siege in progress
					if (castle.getSiege().checkIsDefender(activeChar.getClan()))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CASTLE);
					}
					else if (castle.getSiege().checkIsAttacker(activeChar.getClan()))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
					}
					else
					{
						LOG.warn("Player [{}] called RestartPointPacket - To Castle and he doesn't have Castle!", activeChar.getName());
						return;
					}
				}
				else
				{
					if ((activeChar.getClan() == null) || (activeChar.getClan().getCastleId() == 0))
					{
						return;
					}
					loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CASTLE);
				}
				if ((CastleManager.getInstance().getCastleByOwner(activeChar.getClan()) != null) && (CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getFunction(Castle.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getFunction(Castle.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case 3: // to fortress
			{
				if (((activeChar.getClan() == null) || (activeChar.getClan().getFortId() == 0)) && !isInDefense)
				{
					LOG.warn("Player [{}] called RestartPointPacket - To Fortress and he doesn't have Fortress!", activeChar.getName());
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.FORTRESS);
				if ((FortManager.getInstance().getFortByOwner(activeChar.getClan()) != null) && (FortManager.getInstance().getFortByOwner(activeChar.getClan()).getFunction(Fort.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(FortManager.getInstance().getFortByOwner(activeChar.getClan()).getFunction(Fort.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case 4: // to siege HQ
			{
				L2SiegeClan siegeClan = null;
				castle = CastleManager.getInstance().getCastle(activeChar);
				fort = FortManager.getInstance().getFort(activeChar);
				hall = CHSiegeManager.getInstance().getNearbyClanHall(activeChar);
				L2SiegeFlagInstance flag = TerritoryWarManager.getInstance().getHQForClan(activeChar.getClan());
				
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					siegeClan = castle.getSiege().getAttackerClan(activeChar.getClan());
				}
				else if ((fort != null) && fort.getSiege().isInProgress())
				{
					siegeClan = fort.getSiege().getAttackerClan(activeChar.getClan());
				}
				else if ((hall != null) && hall.isInSiege())
				{
					siegeClan = hall.getSiege().getAttackerClan(activeChar.getClan());
				}
				if (((siegeClan == null) || siegeClan.getFlag().isEmpty()) && (flag == null))
				{
					// Check if clan hall has inner spawns loc
					if (hall != null)
					{
						loc = hall.getSiege().getInnerSpawnLoc(activeChar);
						
						if (loc != null)
						{
							break;
						}
					}
					
					LOG.warn("Player [{}] called RestartPointPacket - To Siege HQ and he doesn't have Siege HQ!", activeChar.getName());
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.SIEGEFLAG);
				break;
			}
			case 5: // Fixed or Player is a festival participant
			{
				if (!activeChar.isGM() && !activeChar.isFestivalParticipant() && !activeChar.getInventory().haveItemForSelfResurrection())
				{
					LOG.warn("Player [{}] called RestartPointPacket - Fixed and he isn't festival participant!", activeChar.getName());
					return;
				}
				if (activeChar.isGM() || activeChar.destroyItemByItemId("Feather", 10649, 1, activeChar, false) || activeChar.destroyItemByItemId("Feather", 13300, 1, activeChar, false) || activeChar.destroyItemByItemId("Feather", 13128, 1, activeChar, false))
				{
					activeChar.doRevive(100.00);
				}
				else
				// Festival Participant
				{
					instanceId = activeChar.getInstanceId();
					loc = new Location(activeChar);
				}
				break;
			}
			case 6: // TODO: agathion ress
			{
				break;
			}
			case 27: // to jail
			{
				if (!activeChar.isJailed())
				{
					return;
				}
				loc = new Location(-114356, -249645, -2984);
				break;
			}
			default:
			{
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
				break;
			}
		}
		
		// Teleport and revive
		if (loc != null)
		{
			activeChar.setInstanceId(instanceId);
			activeChar.setIsIn7sDungeon(false);
			activeChar.setIsPendingRevive(true);
			activeChar.teleToLocation(loc, true);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__7D_REQUESTRESTARTPOINT;
	}
}
