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
package com.l2jserver.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.EnumIntBitmask;
import com.l2jserver.gameserver.configuration.config.Config;
import com.l2jserver.gameserver.dao.ClanDAO;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2ClanMember;

/**
 * MySQL DAO Factory implementation.
 * @author Мо3олЬ, Zoey76
 */
public class ClanDAOMySQLImpl implements ClanDAO
{
	private static final Logger LOG = LoggerFactory.getLogger(ClanDAOMySQLImpl.class);
	
	private static final String INSERT_CLAN_DATA = "INSERT INTO `clan_data` (`clan_id`, `clan_name`, `clan_level`, `hasCastle`, `blood_alliance_count`, `blood_oath_count`, `ally_id`, `ally_name`, `leader_id`, `crest_id`, `crest_large_id`, `ally_crest_id`, `new_leader_id`) values (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_CLAN_PRIVILEGES = "INSERT INTO `clan_privs` (`clan_id`, `rank`, `party`, `privs`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `privs`=?";
	private static final String UPDATE_CLAN_DATA = "UPDATE `clan_data` SET `leader_id` = ?, `ally_id` = ?, `ally_name` = ?, `reputation_score` = ?, `ally_penalty_expiry_time` = ?,`ally_penalty_type` = ?, `char_penalty_expiry_time` = ?, `dissolving_expiry_time` = ?, `new_leader_id` = ? WHERE `clan_id` = ?";
	private static final String SELECT_CLAN_DATA = "SELECT * FROM `clan_data` where `clan_id` = ?";
	private static final String SELECT_CLAN_NOTICE = "SELECT `enabled`, `notice` FROM `clan_notices` WHERE `clan_id` = ?";
	private static final String SELECT_CLAN_PRIVILEGES = "SELECT `privs`, `rank`, `party` FROM `clan_privs` WHERE clan_id=?";
	private static final String UPDATE_CLAN_LEVEL = "UPDATE `clan_data` SET `clan_level` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_CLAN_CREST = "UPDATE `clan_data` SET `crest_id` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_CLAN_CREST_LARGE = "UPDATE `clan_data` SET `crest_large_id` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_CLAN_SUBPLEDGES = "UPDATE `clan_subpledges` SET `leader_id` = ?, `name` = ? WHERE `clan_id` = ? AND `sub_pledge_id` = ?";
	private static final String UPDATE_CLAN_SCORE = "UPDATE `clan_data` SET `reputation_score` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_BLOOD_OATH = "UPDATE `clan_data` SET `blood_oath_count` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_BLOOD_ALLIANCE = "UPDATE `clan_data` SET `blood_alliance_count` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_CLAN_PRIVS = "UPDATE `characters` SET `clan_privs` = ? WHERE `charId` = ?";
	private static final String UPDATE_CLAN_CHAR = "UPDATE `characters` SET `clanid` = 0, `title` = ?, `clan_join_expiry_time` = ?, `clan_create_expiry_time` = ?, `clan_privs` = 0, `wantspeace` = 0, `subpledge` = 0, `lvl_joined_academy` = 0, `apprentice` = 0, `sponsor` = 0 WHERE `charId` = ?";
	private static final String UPDATE_CLAN_CHAR_APPRENTICE = "UPDATE `characters` SET `apprentice` = 0 WHERE `apprentice` = ?";
	private static final String UPDATE_CLAN_CHAR_SPONSOR = "UPDATE `characters` SET `sponsor` = 0 WHERE `sponsor` = ?";
	
	@Override
	public void storeClan(L2Clan clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_CLAN_DATA))
		{
			ps.setInt(1, clan.getId());
			ps.setString(2, clan.getName());
			ps.setInt(3, clan.getLevel());
			ps.setInt(4, clan.getCastleId());
			ps.setInt(5, clan.getBloodAllianceCount());
			ps.setInt(6, clan.getBloodOathCount());
			ps.setInt(7, clan.getAllyId());
			ps.setString(8, clan.getAllyName());
			ps.setInt(9, clan.getLeaderId());
			ps.setInt(10, clan.getCrestId());
			ps.setInt(11, clan.getCrestLargeId());
			ps.setInt(12, clan.getAllyCrestId());
			ps.setInt(13, clan.getNewLeaderId());
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.error("Error saving new clan!", e);
		}
	}
	
	@Override
	public void updateClan(L2Clan clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_DATA))
		{
			ps.setInt(1, clan.getLeaderId());
			ps.setInt(2, clan.getAllyId());
			ps.setString(3, clan.getAllyName());
			ps.setInt(4, clan.getReputationScore());
			ps.setLong(5, clan.getAllyPenaltyExpiryTime());
			ps.setInt(6, clan.getAllyPenaltyType());
			ps.setLong(7, clan.getCharPenaltyExpiryTime());
			ps.setLong(8, clan.getDissolvingExpiryTime());
			ps.setInt(9, clan.getNewLeaderId());
			ps.setInt(10, clan.getId());
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.error("Couldn't saving clan!", e);
		}
	}
	
	@Override
	public void restoreClan(L2Clan clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_CLAN_DATA))
		{
			ps.setInt(1, clan.getId());
			try (ResultSet clanData = ps.executeQuery())
			{
				if (clanData.next())
				{
					clan.setName(clanData.getString("clan_name"));
					clan.setLevel(clanData.getInt("clan_level"));
					clan.setCastleId(clanData.getInt("hasCastle"));
					clan.setBloodAllianceCount(clanData.getInt("blood_alliance_count"));
					clan.setBloodOathCount(clanData.getInt("blood_oath_count"));
					clan.setAllyId(clanData.getInt("ally_id"));
					clan.setAllyName(clanData.getString("ally_name"));
					clan.setAllyPenaltyExpiryTime(clanData.getLong("ally_penalty_expiry_time"), clanData.getInt("ally_penalty_type"));
					
					if (clan.getAllyPenaltyExpiryTime() < System.currentTimeMillis())
					{
						clan.setAllyPenaltyExpiryTime(0, 0);
					}
					clan.setCharPenaltyExpiryTime(clanData.getLong("char_penalty_expiry_time"));
					
					if ((clan.getCharPenaltyExpiryTime() + (Config.ALT_CLAN_JOIN_DAYS * 86400000L)) < System.currentTimeMillis()) // 24*60*60*1000 = 86400000
					{
						clan.setCharPenaltyExpiryTime(0);
					}
					clan.setDissolvingExpiryTime(clanData.getLong("dissolving_expiry_time"));
					
					clan.setCrestId(clanData.getInt("crest_id"));
					clan.setCrestLargeId(clanData.getInt("crest_large_id"));
					clan.setAllyCrestId(clanData.getInt("ally_crest_id"));
					
					clan.setReputationScore(clanData.getInt("reputation_score"), false);
					clan.setAuctionBiddedAt(clanData.getInt("auction_bid_at"), false);
					clan.setNewLeaderId(clanData.getInt("new_leader_id"), false);
					
					final int leaderId = (clanData.getInt("leader_id"));
					
					ps.clearParameters();
					
					try (PreparedStatement select = con.prepareStatement("SELECT `char_name`, `level`, `classid`, `charId`, `title`, `power_grade`, `subpledge`, `apprentice`, `sponsor`, `sex`, `race` FROM `characters` WHERE `clanid` = ?"))
					{
						select.setInt(1, clan.getId());
						try (ResultSet clanMember = select.executeQuery())
						{
							L2ClanMember member = null;
							while (clanMember.next())
							{
								member = new L2ClanMember(clan, clanMember);
								if (member.getObjectId() == leaderId)
								{
									clan.setLeader(member);
								}
								else
								{
									clan.addClanMember(member);
								}
							}
						}
					}
				}
			}
			clan.restoreSubPledges();
			clan.restoreRankPrivs();
			clan.restoreSkills();
			restoreNotice(clan);
		}
		catch (Exception e)
		{
			LOG.error("Couldn'trestoring clan data!", e);
		}
	}
	
	public void restoreNotice(L2Clan clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_CLAN_NOTICE))
		{
			ps.setInt(1, clan.getId());
			try (ResultSet noticeData = ps.executeQuery())
			{
				while (noticeData.next())
				{
					clan.setNoticeEnabled(noticeData.getBoolean("enabled"));
					clan.setNotice(noticeData.getString("notice"));
				}
				noticeData.close();
			}
			ps.close();
		}
		catch (Exception e)
		{
			LOG.error("Error restoring clan notice!", e);
		}
	}
	
	@Override
	public void changeLevel(int level, int clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_LEVEL))
		{
			ps.setInt(1, level);
			ps.setInt(2, clan);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Could not increase clan level!", e);
		}
	}
	
	@Override
	public void changeClanCrest(int crestId, int clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_CREST))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, clan);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warn("Could not update crest for clan {}!", clan, e);
		}
	}
	
	@Override
	public void changeLargeCrest(int crestId, int clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_CREST_LARGE))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, clan);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.warn("Could not update large crest for clan {}!", clan, e);
		}
	}
	
	@Override
	public void removeMember(int playerId, long clanJoinExpiryTime, long clanCreateExpiryTime)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps1 = con.prepareStatement(UPDATE_CLAN_CHAR);
			PreparedStatement ps2 = con.prepareStatement(UPDATE_CLAN_CHAR_APPRENTICE);
			PreparedStatement ps3 = con.prepareStatement(UPDATE_CLAN_CHAR_SPONSOR))
		{
			// Remove clan member.
			ps1.setString(1, "");
			ps1.setLong(2, clanJoinExpiryTime);
			ps1.setLong(3, clanCreateExpiryTime);
			ps1.setInt(4, playerId);
			ps1.execute();
			ps1.close();
			// Remove apprentice.
			ps2.setInt(1, playerId);
			ps2.execute();
			ps2.close();
			// Remove sponsor.
			ps3.setInt(1, playerId);
			ps3.execute();
			ps3.close();
		}
		catch (Exception e)
		{
			LOG.error("Couldn't removing clan member!", e);
		}
	}
	
	@Override
	public void updateSubPledge(L2Clan clan, int pledgeType)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_SUBPLEDGES))
		{
			ps.setInt(1, clan.getSubPledge(pledgeType).getLeaderId());
			ps.setString(2, clan.getSubPledge(pledgeType).getName());
			ps.setInt(3, clan.getId());
			ps.setInt(4, pledgeType);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.error("Couldn't updating subpledge!", e);
		}
	}
	
	@Override
	public void updateClanScore(int reputationScore, int clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_SCORE))
		{
			ps.setInt(1, reputationScore);
			ps.setInt(2, clan);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Couldn't update clan score!", e);
		}
	}
	
	@Override
	public void updateBloodOathCount(int bloodOath, int clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_BLOOD_OATH))
		{
			ps.setInt(1, bloodOath);
			ps.setInt(2, clan);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Couldn't update blood oath count!", e);
		}
	}
	
	@Override
	public void updateBloodAllianceCount(int bloodAlliance, int clan)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_BLOOD_ALLIANCE))
		{
			ps.setInt(1, bloodAlliance);
			ps.setInt(2, clan);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Couldn't update blood alliance count!", e);
		}
	}
	
	@Override
	public void updateClanPrivsOld(int leader)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_PRIVS))
		{
			ps.setInt(1, 0);
			ps.setInt(2, leader);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Couldn't update clan privs for old clan leader!", e);
		}
	}
	
	@Override
	public void updateClanPrivsNew(int leader)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_PRIVS))
		{
			ps.setInt(1, EnumIntBitmask.getAllBitmask(ClanPrivilege.class));
			ps.setInt(2, leader);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Couldn't update clan privs for new clan leader", e);
		}
	}
	
	@Override
	public void setNotice(int clan, String notice, boolean enabled)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO `clan_notices` (`clan_id`, `notice`, `enabled`) values (?, ?, ?) ON DUPLICATE KEY UPDATE `notice` = ?, `enabled` = ?"))
		{
			ps.setInt(1, clan);
			ps.setString(2, notice);
			
			if (enabled)
			{
				ps.setString(3, "true");
			}
			else
			{
				ps.setString(3, "false");
			}
			
			ps.setString(4, notice);
			
			if (enabled)
			{
				ps.setString(5, "true");
			}
			else
			{
				ps.setString(5, "false");
			}
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Error could not store clan notice!", e);
		}
	}
	
	@Override
	public Map<Integer, Integer> getPrivileges(int clanId)
	{
		final Map<Integer, Integer> result = new HashMap<>();
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_CLAN_PRIVILEGES))
		{
			ps.setInt(1, clanId);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int rank = rs.getInt("rank");
					if (rank == -1)
					{
						continue;
					}
					result.put(rank, rs.getInt("privs"));
				}
			}
		}
		catch (Exception ex)
		{
			LOG.error("Unable to restore clan privileges for clan Id {}!", clanId, ex);
		}
		return result;
	}
	
	@Override
	public void storePrivileges(int clanId, int rank, int privileges)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_CLAN_PRIVILEGES))
		{
			ps.setInt(1, clanId);
			ps.setInt(2, rank);
			ps.setInt(3, 0);
			ps.setInt(4, privileges);
			ps.setInt(5, privileges);
			ps.execute();
		}
		catch (Exception ex)
		{
			LOG.error("Unable to store clan privileges for clan Id {}!", clanId, ex);
		}
	}
}
