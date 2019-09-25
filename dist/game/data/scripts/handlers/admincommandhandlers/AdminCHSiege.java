/*
 * Copyright (C) 2004-2019 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import java.util.Calendar;

import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CHSiegeManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import com.l2jserver.gameserver.model.entity.clanhall.SiegableHall;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SiegeInfo;

/**
 * @author BiggBoss
 */
public final class AdminCHSiege implements IAdminCommandHandler
{
	private static final String[] COMMANDS =
	{
		"admin_chsiege_siegablehall",
		"admin_chsiege_startSiege",
		"admin_chsiege_endsSiege",
		"admin_chsiege_setSiegeDate",
		"admin_chsiege_addAttacker",
		"admin_chsiege_removeAttacker",
		"admin_chsiege_clearAttackers",
		"admin_chsiege_listAttackers",
		"admin_chsiege_forwardSiege"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final String[] split = command.split(" ");
		SiegableHall hall = null;
		if (Config.ALT_DEV_NO_QUESTS)
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_clan_hall_sieges_disabled"));
			return false;
		}
		if (split.length < 2)
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_have_specify_hall_least"));
			return false;
		}
		if ((hall = getHall(split[1], activeChar)) == null)
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_couldnt_find_desired_siegable_hall").replace("%i%", split[1] + ""));
			return false;
		}
		if (hall.getSiege() == null)
		{
			activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_given_hall_dont_have_any_attached_siege"));
			return false;
		}
		
		if (split[0].equals(COMMANDS[1]))
		{
			if (hall.isInSiege())
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_requested_clan_hall_alredy_siege"));
			}
			else
			{
				L2Clan owner = ClanTable.getInstance().getClan(hall.getOwnerId());
				if (owner != null)
				{
					hall.free();
					owner.setHideoutId(0);
					hall.addAttacker(owner);
				}
				hall.getSiege().startSiege();
			}
		}
		else if (split[0].equals(COMMANDS[2]))
		{
			if (!hall.isInSiege())
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_requested_clan_hall_siege"));
			}
			else
			{
				hall.getSiege().endSiege();
			}
		}
		else if (split[0].equals(COMMANDS[3]))
		{
			if (!hall.isRegistering())
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_cannot_change_siege_date_hall_siege"));
			}
			else if (split.length < 3)
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_incorrect_date_format_try_again"));
			}
			else
			{
				String[] rawDate = split[2].split(";");
				if (rawDate.length < 2)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_have_specify_format"));
				}
				else
				{
					String[] day = rawDate[0].split("-");
					String[] hour = rawDate[1].split(":");
					if ((day.length < 3) || (hour.length < 2))
					{
						activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_incomplete_day_hour_both"));
					}
					else
					{
						int d = parseInt(day[0]);
						int month = parseInt(day[1]) - 1;
						int year = parseInt(day[2]);
						int h = parseInt(hour[0]);
						int min = parseInt(hour[1]);
						if (((month == 2) && (d > 28)) || (d > 31) || (d <= 0) || (month <= 0) || (month > 12) || (year < Calendar.getInstance().get(Calendar.YEAR)))
						{
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_wrong_day_month_year_gave"));
						}
						else if ((h <= 0) || (h > 24) || (min < 0) || (min >= 60))
						{
							activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_wrong_hour_minutes_gave"));
						}
						else
						{
							Calendar c = Calendar.getInstance();
							c.set(Calendar.YEAR, year);
							c.set(Calendar.MONTH, month);
							c.set(Calendar.DAY_OF_MONTH, d);
							c.set(Calendar.HOUR_OF_DAY, h);
							c.set(Calendar.MINUTE, min);
							c.set(Calendar.SECOND, 0);
							
							if (c.getTimeInMillis() > System.currentTimeMillis())
							{
								activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_your_access_level_has_been_changed_to").replace("%c%", hall.getName() + "").replace("%i%", c.getTime().toString() + ""));
								hall.setNextSiegeDate(c.getTimeInMillis());
								hall.getSiege().updateSiege();
								hall.updateDb();
							}
							else
							{
								activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_given_time_past"));
							}
						}
					}
					
				}
			}
		}
		else if (split[0].equals(COMMANDS[4]))
		{
			if (hall.isInSiege())
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_clan_hall_siege_cannot_add_attackers_now"));
				return false;
			}
			
			L2Clan attacker = null;
			if (split.length < 3)
			{
				L2Object rawTarget = activeChar.getTarget();
				L2PcInstance target = null;
				if (rawTarget == null)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_must_target_clan_member_attacker"));
				}
				else if (!(rawTarget instanceof L2PcInstance))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_must_target_player_with_clan"));
				}
				else if ((target = (L2PcInstance) rawTarget).getClan() == null)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_target_does_not_have_any_clan"));
				}
				else if (hall.getSiege().checkIsAttacker(target.getClan()))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_target_clan_alredy_participating"));
				}
				else
				{
					attacker = target.getClan();
				}
			}
			else
			{
				L2Clan rawClan = ClanTable.getInstance().getClanByName(split[2]);
				if (rawClan == null)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_given_clan_not_exist"));
				}
				else if (hall.getSiege().checkIsAttacker(rawClan))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_given_clan_alredy_participating"));
				}
				else
				{
					attacker = rawClan;
				}
			}
			
			if (attacker != null)
			{
				hall.addAttacker(attacker);
			}
		}
		else if (split[0].equals(COMMANDS[5]))
		{
			if (hall.isInSiege())
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_clan_hall_siege_cannot_remove_attackers_now"));
				return false;
			}
			
			if (split.length < 3)
			{
				L2Object rawTarget = activeChar.getTarget();
				L2PcInstance target = null;
				if (rawTarget == null)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_must_target_clan_member_attacker"));
				}
				else if (!(rawTarget instanceof L2PcInstance))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_must_target_player_with_clan"));
				}
				else if ((target = (L2PcInstance) rawTarget).getClan() == null)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_target_does_not_have_any_clan"));
				}
				else if (!hall.getSiege().checkIsAttacker(target.getClan()))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_target_clan_not_participating"));
				}
				else
				{
					hall.removeAttacker(target.getClan());
				}
			}
			else
			{
				L2Clan rawClan = ClanTable.getInstance().getClanByName(split[2]);
				if (rawClan == null)
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_given_clan_not_exist"));
				}
				else if (!hall.getSiege().checkIsAttacker(rawClan))
				{
					activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_given_clan_not_participating"));
				}
				else
				{
					hall.removeAttacker(rawClan);
				}
			}
		}
		else if (split[0].equals(COMMANDS[6]))
		{
			if (hall.isInSiege())
			{
				activeChar.sendAdminMessage(MessagesData.getInstance().getMessage(activeChar, "admin_requested_hall_siege_right_now_cannot_clear_attacker_list"));
			}
			else
			{
				hall.getSiege().getAttackers().clear();
			}
		}
		else if (split[0].equals(COMMANDS[7]))
		{
			activeChar.sendPacket(new SiegeInfo(hall));
		}
		else if (split[0].equals(COMMANDS[8]))
		{
			ClanHallSiegeEngine siegable = hall.getSiege();
			siegable.cancelSiegeTask();
			switch (hall.getSiegeStatus())
			{
				case REGISTERING:
					siegable.prepareOwner();
					break;
				case WAITING_BATTLE:
					siegable.startSiege();
					break;
				case RUNNING:
					siegable.endSiege();
					break;
			}
		}
		
		sendSiegableHallPage(activeChar, split[1], hall);
		return false;
	}
	
	private SiegableHall getHall(String id, L2PcInstance gm)
	{
		int ch = parseInt(id);
		if (ch == 0)
		{
			gm.sendAdminMessage(MessagesData.getInstance().getMessage(gm, "admin_wrong_clan_hall_id"));
			return null;
		}
		
		SiegableHall hall = CHSiegeManager.getInstance().getSiegableHall(ch);
		
		if (hall == null)
		{
			gm.sendAdminMessage(MessagesData.getInstance().getMessage(gm, "admin_сouldnt_find_clan_hall"));
		}
		
		return hall;
	}
	
	private int parseInt(String st)
	{
		int val = 0;
		try
		{
			val = Integer.parseInt(st);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return val;
	}
	
	private void sendSiegableHallPage(L2PcInstance activeChar, String hallId, SiegableHall hall)
	{
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setFile(null, "data/html/admin/siegablehall.htm");
		msg.replace("%clanhallId%", hallId);
		msg.replace("%clanhallName%", hall.getName());
		if (hall.getOwnerId() > 0)
		{
			L2Clan owner = ClanTable.getInstance().getClan(hall.getOwnerId());
			if (owner != null)
			{
				msg.replace("%clanhallOwner%", owner.getName());
			}
			else
			{
				msg.replace("%clanhallOwner%", " " + MessagesData.getInstance().getMessage(activeChar, "admin_no_owner") + "");
			}
		}
		else
		{
			msg.replace("%clanhallOwner%", " " + MessagesData.getInstance().getMessage(activeChar, "admin_no_owner") + "");
		}
		activeChar.sendPacket(msg);
	}
}
