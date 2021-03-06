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
package com.l2jserver.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.EnumIntBitmask;
import com.l2jserver.gameserver.bbs.ForumsBBSManager;
import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.configuration.config.FeatureConfig;
import com.l2jserver.gameserver.configuration.config.SiegeConfig;
import com.l2jserver.gameserver.configuration.config.community.CBasicConfig;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.data.sql.impl.CrestTable;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.ClanPrivilege;
import com.l2jserver.gameserver.enums.ForumType;
import com.l2jserver.gameserver.enums.ForumVisibility;
import com.l2jserver.gameserver.enums.ZoneId;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.instancemanager.SiegeManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager.Territory;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.bbs.Forum;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanJoin;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanLeaderChange;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanLeft;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanLvlUp;
import com.l2jserver.gameserver.model.friend.BlockList;
import com.l2jserver.gameserver.model.interfaces.IIdentifiable;
import com.l2jserver.gameserver.model.interfaces.INamable;
import com.l2jserver.gameserver.model.itemcontainer.ClanWarehouse;
import com.l2jserver.gameserver.model.itemcontainer.ItemContainer;
import com.l2jserver.gameserver.model.pledge.RankPrivs;
import com.l2jserver.gameserver.model.pledge.SubPledge;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.ExSubPledgeSkillAdd;
import com.l2jserver.gameserver.network.serverpackets.ItemList;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.PledgeReceiveSubPledgeCreated;
import com.l2jserver.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.l2jserver.gameserver.network.serverpackets.PledgeShowMemberListAll;
import com.l2jserver.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import com.l2jserver.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.l2jserver.gameserver.network.serverpackets.PledgeSkillList;
import com.l2jserver.gameserver.network.serverpackets.PledgeSkillList.SubPledgeSkill;
import com.l2jserver.gameserver.network.serverpackets.PledgeSkillListAdd;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;
import com.l2jserver.gameserver.util.Util;

public class L2Clan implements IIdentifiable, INamable
{
	private static final Logger LOG = LoggerFactory.getLogger(L2Clan.class);
	
	private static final String SELECT_CLAN_SUBPLEDGES = "SELECT `sub_pledge_id`, `name`, `leader_id` FROM `clan_subpledges` WHERE `clan_id` = ?";
	private static final String SELECT_CLAN_SKILLS = "SELECT `skill_id`, `skill_level`, `sub_pledge_id` FROM `clan_skills` WHERE `clan_id` = ?";
	
	private static final String UPDATE_CLAN_ALLY_CREST_CLAN = "UPDATE `clan_data` SET `ally_crest_id` = ? WHERE `clan_id` = ?";
	private static final String UPDATE_CLAN_ALLY_CREST_ALLY = "UPDATE `clan_data` SET `ally_crest_id` = ? WHERE `ally_id` = ?";
	
	// Ally Penalty Types
	public static final int PENALTY_TYPE_CLAN_LEAVED = 1; // Clan leaved ally
	public static final int PENALTY_TYPE_CLAN_DISMISSED = 2; // Clan was dismissed from ally
	public static final int PENALTY_TYPE_DISMISS_CLAN = 3; // Leader clan dismiss clan from ally
	public static final int PENALTY_TYPE_DISSOLVE_ALLY = 4; // Leader clan dissolve ally
	
	// Sub-unit types
	public static final int SUBUNIT_NONE = Byte.MIN_VALUE;
	public static final int SUBUNIT_ACADEMY = -1; // Clan subunit type of Academy
	public static final int SUBUNIT_MAIN_CLAN = 0;
	public static final int SUBUNIT_ROYAL1 = 100; // Clan subunit type of Royal Guard A
	public static final int SUBUNIT_ROYAL2 = 200; // Clan subunit type of Royal Guard B
	public static final int SUBUNIT_KNIGHT1 = 1001; // Clan subunit type of Order of Knights A-1
	public static final int SUBUNIT_KNIGHT2 = 1002; // Clan subunit type of Order of Knights A-2
	public static final int SUBUNIT_KNIGHT3 = 2001; // Clan subunit type of Order of Knights B-1
	public static final int SUBUNIT_KNIGHT4 = 2002; // Clan subunit type of Order of Knights B-2
	
	private String _name;
	private int _clanId;
	private L2ClanMember _leader;
	private String _allyName;
	private int _allyId = 0;
	private int _level;
	private int _castleId;
	private int _fortId;
	private int _hideoutId;
	private int _hiredGuards;
	private int _crestId;
	private int _crestLargeId;
	private int _allyCrestId;
	private int _auctionBiddedAt = 0;
	private long _allyPenaltyExpiryTime;
	private int _allyPenaltyType;
	private long _charPenaltyExpiryTime;
	private long _dissolvingExpiryTime;
	private int _bloodAllianceCount;
	private int _bloodOathCount;
	private Forum _forum;
	
	private final ItemContainer _warehouse = new ClanWarehouse(this);
	private final Set<Integer> _atWarWith = ConcurrentHashMap.newKeySet();
	private final Set<Integer> _atWarAttackers = ConcurrentHashMap.newKeySet();
	private final Map<Integer, L2ClanMember> _members = new ConcurrentHashMap<>();
	
	/** Map(Integer, L2Skill) containing all skills of the L2Clan */
	private final Map<Integer, Skill> _skills = new ConcurrentHashMap<>();
	private final Map<Integer, RankPrivs> _privs = new ConcurrentHashMap<>();
	private final Map<Integer, SubPledge> _subPledges = new ConcurrentHashMap<>();
	private final Map<Integer, Skill> _subPledgeSkills = new ConcurrentHashMap<>();
	
	private int _reputationScore = 0;
	
	private String _notice;
	private boolean _noticeEnabled = false;
	private static final int MAX_NOTICE_LENGTH = 8192;
	private int _newLeaderId;
	
	private final AtomicInteger _siegeKills = new AtomicInteger();
	private final AtomicInteger _siegeDeaths = new AtomicInteger();
	
	public static final RankPrivs[] EMPTY_RANK_PRIVSES_ARRAY = new RankPrivs[0];
	public static final int RANK_FIRST = 1;
	public static final int RANK_LAST = 10;
	private static final int REPUTATION_PLACES = 100;
	private static final ClanReputationComparator REPUTATION_COMPARATOR = new ClanReputationComparator();
	
	/**
	 * Called if a clan is referenced only by id. In this case all other data needs to be fetched from db
	 * @param clanId A valid clan Id to create and restore
	 */
	public L2Clan(int clanId)
	{
		_clanId = clanId;
		initializePrivs();
		DAOFactory.getInstance().getClanDAO().restoreClan(this);
		getWarehouse().restore();
	}
	
	/**
	 * Called only if a new clan is created
	 * @param clanId A valid clan Id to create
	 * @param clanName A valid clan name
	 */
	public L2Clan(int clanId, String clanName)
	{
		_clanId = clanId;
		_name = clanName;
		initializePrivs();
	}
	
	/**
	 * @return Returns the clanId.
	 */
	@Override
	public int getId()
	{
		return _clanId;
	}
	
	/**
	 * @param clanId The clanId to set.
	 */
	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}
	
	/**
	 * @return Returns the leaderId.
	 */
	public int getLeaderId()
	{
		return (_leader != null ? _leader.getObjectId() : 0);
	}
	
	/**
	 * @return L2ClanMember of clan leader.
	 */
	public L2ClanMember getLeader()
	{
		return _leader;
	}
	
	/**
	 * @param leader the leader to set.
	 */
	public void setLeader(L2ClanMember leader)
	{
		_leader = leader;
		_members.put(leader.getObjectId(), leader);
	}
	
	public void setNewLeader(L2ClanMember member)
	{
		final L2PcInstance newLeader = member.getPlayerInstance();
		final L2ClanMember exMember = getLeader();
		final L2PcInstance exLeader = exMember.getPlayerInstance();
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLeaderChange(exMember, member, this));
		
		if (exLeader != null)
		{
			if (exLeader.isFlying())
			{
				exLeader.dismount();
			}
			
			if (getLevel() >= SiegeConfig.SIEGE_CLAN_MIN_LEVEL)
			{
				SiegeManager.getInstance().removeSiegeSkills(exLeader);
			}
			exLeader.getClanPrivileges().clear();
			exLeader.broadcastUserInfo();
		}
		else
		{
			DAOFactory.getInstance().getClanDAO().updateClanPrivsOld(getLeaderId());
		}
		
		setLeader(member);
		
		if (getNewLeaderId() != 0)
		{
			setNewLeaderId(0, true);
		}
		
		DAOFactory.getInstance().getClanDAO().updateClan(this);
		
		if (exLeader != null)
		{
			exLeader.setPledgeClass(L2ClanMember.calculatePledgeClass(exLeader));
			exLeader.broadcastUserInfo();
			exLeader.checkItemRestriction();
		}
		
		if (newLeader != null)
		{
			newLeader.setPledgeClass(L2ClanMember.calculatePledgeClass(newLeader));
			newLeader.getClanPrivileges().setAll();
			
			if (getLevel() >= SiegeConfig.SIEGE_CLAN_MIN_LEVEL)
			{
				SiegeManager.getInstance().addSiegeSkills(newLeader);
			}
			newLeader.broadcastUserInfo();
		}
		else
		{
			DAOFactory.getInstance().getClanDAO().updateClanPrivsNew(getLeaderId());
		}
		
		broadcastClanStatus();
		broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_C1).addString(member.getName()));
		
		LOG.info("Leader of Clan: {} changed to: {} ex leader: {}", getName(), member.getName(), exMember.getName());
	}
	
	/**
	 * @return the clan leader's name.
	 */
	public String getLeaderName()
	{
		if (_leader == null)
		{
			LOG.warn("{}: Clan {} without clan leader!", L2Clan.class.getName(), getName());
			return "";
		}
		return _leader.getName();
	}
	
	/**
	 * @return the clan name.
	 */
	@Override
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		_name = name;
	}
	
	/**
	 * Adds a clan member to the clan.
	 * @param member the clan member.
	 */
	public void addClanMember(L2ClanMember member)
	{
		_members.put(member.getObjectId(), member);
	}
	
	/**
	 * Adds a clan member to the clan.<br>
	 * Using a different constructor, to make it easier to read.
	 * @param player the clan member
	 */
	public void addClanMember(L2PcInstance player)
	{
		final L2ClanMember member = new L2ClanMember(this, player);
		member.setPlayerInstance(player);
		addClanMember(member);
		player.setClan(this);
		player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
		player.sendPacket(new PledgeShowMemberListUpdate(player));
		player.sendPacket(new PledgeSkillList(this));
		addSkillEffects(player);
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanJoin(member, this));
	}
	
	/**
	 * Updates player status in clan.
	 * @param player the player to be updated.
	 */
	public void updateClanMember(L2PcInstance player)
	{
		final L2ClanMember member = new L2ClanMember(player.getClan(), player);
		if (player.isClanLeader())
		{
			setLeader(member);
		}
		addClanMember(member);
	}
	
	/**
	 * @param name the name of the required clan member.
	 * @return the clan member for a given name.
	 */
	public L2ClanMember getClanMember(String name)
	{
		for (L2ClanMember temp : _members.values())
		{
			if (temp.getName().equals(name))
			{
				return temp;
			}
		}
		return null;
	}
	
	/**
	 * @param objectId the required clan member object Id.
	 * @return the clan member for a given {@code objectId}.
	 */
	public L2ClanMember getClanMember(int objectId)
	{
		return _members.get(objectId);
	}
	
	/**
	 * @param objectId the object Id of the member that will be removed.
	 * @param clanJoinExpiryTime time penalty to join a clan.
	 */
	public void removeClanMember(int objectId, long clanJoinExpiryTime)
	{
		final L2ClanMember exMember = _members.remove(objectId);
		if (exMember == null)
		{
			LOG.warn("Member Object ID: {} not found in clan while trying to remove", objectId);
			return;
		}
		
		final int leadssubpledge = getLeaderSubPledge(objectId);
		if (leadssubpledge != 0)
		{
			// Sub-unit leader withdraws, position becomes vacant and leader
			// should appoint new via NPC
			getSubPledge(leadssubpledge).setLeaderId(0);
			DAOFactory.getInstance().getClanDAO().updateSubPledge(this, leadssubpledge);
		}
		
		if (exMember.getApprentice() != 0)
		{
			final L2ClanMember apprentice = getClanMember(exMember.getApprentice());
			if (apprentice != null)
			{
				if (apprentice.getPlayerInstance() != null)
				{
					apprentice.getPlayerInstance().setSponsor(0);
				}
				else
				{
					apprentice.setApprenticeAndSponsor(0, 0);
				}
				apprentice.saveApprenticeAndSponsor(0, 0);
			}
		}
		if (exMember.getSponsor() != 0)
		{
			final L2ClanMember sponsor = getClanMember(exMember.getSponsor());
			if (sponsor != null)
			{
				if (sponsor.getPlayerInstance() != null)
				{
					sponsor.getPlayerInstance().setApprentice(0);
				}
				else
				{
					sponsor.setApprenticeAndSponsor(0, 0);
				}
				
				sponsor.saveApprenticeAndSponsor(0, 0);
			}
		}
		exMember.saveApprenticeAndSponsor(0, 0);
		
		if (CharacterConfig.REMOVE_CASTLE_CIRCLETS)
		{
			CastleManager.getInstance().removeCirclet(exMember, getCastleId());
		}
		
		final L2PcInstance player = exMember.getPlayerInstance();
		if (player != null)
		{
			if (!player.isNoble())
			{
				player.setTitle("");
			}
			player.setApprentice(0);
			player.setSponsor(0);
			
			if (player.isClanLeader())
			{
				SiegeManager.getInstance().removeSiegeSkills(player);
				player.setClanCreateExpiryTime(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(CharacterConfig.ALT_CLAN_CREATE_DAYS));
			}
			// remove Clan skills from Player
			removeSkillEffects(player);
			
			// remove Residential skills
			if (player.getClan().getCastleId() > 0)
			{
				CastleManager.getInstance().getCastleByOwner(player.getClan()).removeResidentialSkills(player);
			}
			
			if (player.getClan().getFortId() > 0)
			{
				FortManager.getInstance().getFortByOwner(player.getClan()).removeResidentialSkills(player);
			}
			
			player.sendSkillList();
			player.setClan(null);
			
			// players leaving from clan academy have no penalty
			if (exMember.getPledgeType() != -1)
			{
				player.setClanJoinExpiryTime(clanJoinExpiryTime);
			}
			
			player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
			player.broadcastUserInfo();
			// disable clan tab
			player.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
		}
		else
		{
			DAOFactory.getInstance().getClanDAO().removeMember(exMember.getObjectId(), clanJoinExpiryTime, getLeaderId() == objectId ? System.currentTimeMillis() + TimeUnit.DAYS.toMillis(CharacterConfig.ALT_CLAN_CREATE_DAYS) : 0);
		}
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLeft(exMember, this));
	}
	
	public L2ClanMember[] getMembers()
	{
		return _members.values().toArray(new L2ClanMember[_members.size()]);
	}
	
	public int getMembersCount()
	{
		return _members.size();
	}
	
	public int getSubPledgeMembersCount(int subpl)
	{
		int result = 0;
		for (L2ClanMember temp : _members.values())
		{
			if (temp.getPledgeType() == subpl)
			{
				result++;
			}
		}
		return result;
	}
	
	/**
	 * @param pledgeType the Id of the pledge type.
	 * @return the maximum number of members allowed for a given {@code pledgeType}.
	 */
	public int getMaxNrOfMembers(int pledgeType)
	{
		int limit = 0;
		
		switch (pledgeType)
		{
			case 0:
			{
				switch (getLevel())
				{
					case 3:
					{
						limit = 30;
						break;
					}
					case 2:
					{
						limit = 20;
						break;
					}
					case 1:
					{
						limit = 15;
						break;
					}
					case 0:
					{
						limit = 10;
						break;
					}
					default:
					{
						limit = 40;
						break;
					}
				}
				break;
			}
			case -1:
			{
				limit = 20;
				break;
			}
			case 100:
			case 200:
			{
				switch (getLevel())
				{
					case 11:
					{
						limit = 30;
						break;
					}
					default:
					{
						limit = 20;
						break;
					}
				}
				break;
			}
			case 1001:
			case 1002:
			case 2001:
			case 2002:
			{
				switch (getLevel())
				{
					case 9:
					case 10:
					case 11:
					{
						limit = 25;
						break;
					}
					default:
					{
						limit = 10;
						break;
					}
				}
				break;
			}
			default:
			{
				break;
			}
		}
		return limit;
	}
	
	/**
	 * @param exclude the object Id to exclude from list.
	 * @return all online members excluding the one with object id {code exclude}.
	 */
	public List<L2PcInstance> getOnlineMembers(int exclude)
	{
		final List<L2PcInstance> onlineMembers = new ArrayList<>();
		for (L2ClanMember temp : _members.values())
		{
			if ((temp != null) && temp.isOnline() && (temp.getObjectId() != exclude))
			{
				onlineMembers.add(temp.getPlayerInstance());
			}
		}
		return onlineMembers;
	}
	
	/**
	 * @return the online clan member count.
	 */
	public int getOnlineMembersCount()
	{
		int count = 0;
		for (L2ClanMember temp : _members.values())
		{
			if ((temp == null) || !temp.isOnline())
			{
				continue;
			}
			count++;
		}
		return count;
	}
	
	/**
	 * @return the alliance Id.
	 */
	public int getAllyId()
	{
		return _allyId;
	}
	
	/**
	 * @return the alliance name.
	 */
	public String getAllyName()
	{
		return _allyName;
	}
	
	/**
	 * @param allyCrestId the alliance crest Id to be set.
	 */
	public void setAllyCrestId(int allyCrestId)
	{
		_allyCrestId = allyCrestId;
	}
	
	/**
	 * @return the alliance crest Id.
	 */
	public int getAllyCrestId()
	{
		return _allyCrestId;
	}
	
	/**
	 * @return the clan level.
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * Sets the clan level and updates the clan forum if it's needed.
	 * @param level the clan level to be set.
	 */
	public void setLevel(int level)
	{
		_level = level;
		if ((_level >= 2) && (_forum == null) && CBasicConfig.ENABLE_COMMUNITY_BOARD)
		{
			final Forum clanRootForum = ForumsBBSManager.getInstance().getForumByName("ClanRoot");
			if (clanRootForum != null)
			{
				_forum = clanRootForum.getChildByName(_name);
				if (_forum == null)
				{
					_forum = ForumsBBSManager.getInstance().createNewForum(_name, clanRootForum, ForumType.CLAN, ForumVisibility.CLAN_MEMBER_ONLY, getId());
				}
			}
		}
	}
	
	/**
	 * @return the castle Id for this clan if owns a castle, zero otherwise.
	 */
	public int getCastleId()
	{
		return _castleId;
	}
	
	/**
	 * @return the fort Id for this clan if owns a fort, zero otherwise.
	 */
	public int getFortId()
	{
		return _fortId;
	}
	
	/**
	 * @return the hideout Id for this clan if owns a hideout, zero otherwise.
	 */
	public int getHideoutId()
	{
		return _hideoutId;
	}
	
	/**
	 * @param crestId the Id of the clan crest to be set.
	 */
	public void setCrestId(int crestId)
	{
		_crestId = crestId;
	}
	
	/**
	 * @return Returns the clanCrestId.
	 */
	public int getCrestId()
	{
		return _crestId;
	}
	
	/**
	 * @param crestLargeId The id of pledge LargeCrest.
	 */
	public void setCrestLargeId(int crestLargeId)
	{
		_crestLargeId = crestLargeId;
	}
	
	/**
	 * @return Returns the clan CrestLargeId
	 */
	public int getCrestLargeId()
	{
		return _crestLargeId;
	}
	
	/**
	 * @param allyId The allyId to set.
	 */
	public void setAllyId(int allyId)
	{
		_allyId = allyId;
	}
	
	/**
	 * @param allyName The allyName to set.
	 */
	public void setAllyName(String allyName)
	{
		_allyName = allyName;
	}
	
	/**
	 * @param castleId the castle Id to set.
	 */
	public void setCastleId(int castleId)
	{
		_castleId = castleId;
	}
	
	/**
	 * @param fortId the fort Id to set.
	 */
	public void setFortId(int fortId)
	{
		_fortId = fortId;
	}
	
	/**
	 * @param hideoutId the hideout Id to set.
	 */
	public void setHideoutId(int hideoutId)
	{
		_hideoutId = hideoutId;
	}
	
	/**
	 * @param id the Id of the player to be verified.
	 * @return {code true} if the player belongs to the clan.
	 */
	public boolean isMember(int id)
	{
		return (id == 0 ? false : _members.containsKey(id));
	}
	
	/**
	 * @return the Blood Alliance count for this clan
	 */
	public int getBloodAllianceCount()
	{
		return _bloodAllianceCount;
	}
	
	public void setBloodAllianceCount(int bloodAllianceCount)
	{
		_bloodAllianceCount = bloodAllianceCount;
	}
	
	/**
	 * Increase Blood Alliance count by config predefined count and updates the database.
	 */
	public void increaseBloodAllianceCount()
	{
		_bloodAllianceCount += SiegeConfig.BLOOD_ALLIANCE_REWARD;
		DAOFactory.getInstance().getClanDAO().updateBloodAllianceCount(getBloodAllianceCount(), getId());
	}
	
	/**
	 * Reset the Blood Alliance count to zero and updates the database.
	 */
	public void resetBloodAllianceCount()
	{
		_bloodAllianceCount = 0;
		DAOFactory.getInstance().getClanDAO().updateBloodAllianceCount(getBloodAllianceCount(), getId());
	}
	
	/**
	 * @return the Blood Oath count for this clan
	 */
	public int getBloodOathCount()
	{
		return _bloodOathCount;
	}
	
	public void setBloodOathCount(int bloodOathCount)
	{
		_bloodOathCount = bloodOathCount;
	}
	
	/**
	 * Increase Blood Oath count by config predefined count and updates the database.
	 */
	public void increaseBloodOathCount()
	{
		_bloodOathCount += FeatureConfig.FS_BLOOD_OATH_COUNT;
		DAOFactory.getInstance().getClanDAO().updateBloodOathCount(getBloodOathCount(), getId());
	}
	
	/**
	 * Reset the Blood Oath count to zero and updates the database.
	 */
	public void resetBloodOathCount()
	{
		_bloodOathCount = 0;
		DAOFactory.getInstance().getClanDAO().updateBloodOathCount(getBloodOathCount(), getId());
	}
	
	public void updateClanScoreInDB()
	{
		DAOFactory.getInstance().getClanDAO().updateClanScore(getReputationScore(), getId());
	}
	
	private void storeNotice(String notice, boolean enabled)
	{
		if (notice == null)
		{
			notice = "";
		}
		
		if (notice.length() > MAX_NOTICE_LENGTH)
		{
			notice = notice.substring(0, MAX_NOTICE_LENGTH - 1);
		}
		
		DAOFactory.getInstance().getClanDAO().setNotice(getId(), getNotice(), isNoticeEnabled());
		_notice = notice;
		_noticeEnabled = enabled;
	}
	
	public void setNoticeEnabled(boolean enabled)
	{
		storeNotice(_notice, enabled);
	}
	
	public void setNotice(String notice)
	{
		storeNotice(notice, _noticeEnabled);
	}
	
	public boolean isNoticeEnabled()
	{
		return _noticeEnabled;
	}
	
	public String getNotice()
	{
		if (_notice == null)
		{
			return "";
		}
		return _notice;
	}
	
	/**
	 * @return all the clan skills.
	 */
	public final Skill[] getAllSkills()
	{
		if (_skills == null)
		{
			return new Skill[0];
		}
		return _skills.values().toArray(new Skill[_skills.values().size()]);
	}
	
	/**
	 * @return the map containing this clan skills.
	 */
	public Map<Integer, Skill> getSkills()
	{
		return _skills;
	}
	
	/**
	 * Used to add a skill to skill list of this L2Clan
	 * @param newSkill
	 * @return
	 */
	public Skill addSkill(Skill newSkill)
	{
		Skill oldSkill = null;
		if (newSkill != null)
		{
			// Replace oldSkill by newSkill or Add the newSkill
			oldSkill = _skills.put(newSkill.getId(), newSkill);
		}
		return oldSkill;
	}
	
	public Skill addNewSkill(Skill newSkill)
	{
		return addNewSkill(newSkill, -2);
	}
	
	/**
	 * Used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
	 * @param newSkill
	 * @param subType
	 * @return
	 */
	public Skill addNewSkill(Skill newSkill, int subType)
	{
		Skill oldSkill = null;
		if (newSkill != null)
		{
			
			if (subType == -2)
			{
				oldSkill = _skills.put(newSkill.getId(), newSkill);
			}
			else if (subType == 0)
			{
				oldSkill = _subPledgeSkills.put(newSkill.getId(), newSkill);
			}
			else
			{
				final SubPledge subunit = getSubPledge(subType);
				if (subunit != null)
				{
					oldSkill = subunit.addNewSkill(newSkill);
				}
				else
				{
					LOG.warn("Subpledge {} does not exist for clan {}", subType, getId());
					return oldSkill;
				}
			}
			
			try (Connection con = ConnectionFactory.getInstance().getConnection())
			{
				if (oldSkill != null)
				{
					try (PreparedStatement ps = con.prepareStatement("UPDATE `clan_skills` SET `skill_level` = ? WHERE `skill_id` = ? AND `clan_id` = ?"))
					{
						ps.setInt(1, newSkill.getLevel());
						ps.setInt(2, oldSkill.getId());
						ps.setInt(3, getId());
						ps.execute();
						ps.close();
					}
					
				}
				else
				{
					try (PreparedStatement ps = con.prepareStatement("INSERT INTO `clan_skills` (`clan_id`, `skill_id`, `skill_level`, `skill_name`, `sub_pledge_id`) VALUES (?, ?, ?, ?, ?)"))
					{
						ps.setInt(1, getId());
						ps.setInt(2, newSkill.getId());
						ps.setInt(3, newSkill.getLevel());
						ps.setString(4, newSkill.getName());
						ps.setInt(5, subType);
						ps.execute();
						ps.close();
					}
				}
			}
			catch (Exception e)
			{
				LOG.warn("Error could not store clan skills!", e);
			}
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_SKILL_S1_ADDED);
			sm.addSkillName(newSkill.getId());
			
			for (L2ClanMember temp : _members.values())
			{
				if ((temp != null) && (temp.getPlayerInstance() != null) && temp.isOnline())
				{
					if (subType == -2)
					{
						if (newSkill.getMinPledgeClass() <= temp.getPlayerInstance().getPledgeClass())
						{
							temp.getPlayerInstance().addSkill(newSkill, false); // Skill is not saved to player DB
							temp.getPlayerInstance().sendPacket(new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel()));
							temp.getPlayerInstance().sendPacket(sm);
							temp.getPlayerInstance().sendSkillList();
						}
					}
					else
					{
						if (temp.getPledgeType() == subType)
						{
							temp.getPlayerInstance().addSkill(newSkill, false); // Skill is not saved to player DB
							temp.getPlayerInstance().sendPacket(new ExSubPledgeSkillAdd(subType, newSkill.getId(), newSkill.getLevel()));
							temp.getPlayerInstance().sendPacket(sm);
							temp.getPlayerInstance().sendSkillList();
						}
					}
				}
			}
		}
		return oldSkill;
	}
	
	public void addSkillEffects()
	{
		for (Skill skill : _skills.values())
		{
			for (L2ClanMember temp : _members.values())
			{
				try
				{
					if ((temp != null) && temp.isOnline())
					{
						if (skill.getMinPledgeClass() <= temp.getPlayerInstance().getPledgeClass())
						{
							temp.getPlayerInstance().addSkill(skill, false); // Skill is not saved to player DB
						}
					}
				}
				catch (NullPointerException e)
				{
					LOG.warn("", e);
				}
			}
		}
	}
	
	public void addSkillEffects(L2PcInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		for (Skill skill : _skills.values())
		{
			if (skill.getMinPledgeClass() <= player.getPledgeClass())
			{
				player.addSkill(skill, false); // Skill is not saved to player DB
			}
		}
		
		if (player.getPledgeType() == 0)
		{
			for (Skill skill : _subPledgeSkills.values())
			{
				player.addSkill(skill, false); // Skill is not saved to player DB
			}
		}
		else
		{
			final SubPledge subunit = getSubPledge(player.getPledgeType());
			if (subunit == null)
			{
				return;
			}
			for (Skill skill : subunit.getSkills())
			{
				player.addSkill(skill, false); // Skill is not saved to player DB
			}
		}
		
		if (getReputationScore() < 0)
		{
			skillsStatus(player, true);
		}
	}
	
	public void removeSkillEffects(L2PcInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		for (Skill skill : _skills.values())
		{
			player.removeSkill(skill, false); // Skill is not saved to player DB
		}
		
		if (player.getPledgeType() == 0)
		{
			for (Skill skill : _subPledgeSkills.values())
			{
				player.removeSkill(skill, false); // Skill is not saved to player DB
			}
		}
		else
		{
			final SubPledge subunit = getSubPledge(player.getPledgeType());
			if (subunit == null)
			{
				return;
			}
			for (Skill skill : subunit.getSkills())
			{
				player.removeSkill(skill, false); // Skill is not saved to player DB
			}
		}
	}
	
	public void skillsStatus(L2PcInstance player, boolean disable)
	{
		if (player == null)
		{
			return;
		}
		
		for (Skill skill : _skills.values())
		{
			if (disable)
			{
				player.disableSkill(skill, -1);
			}
			else
			{
				player.enableSkill(skill);
			}
		}
		
		if (player.getPledgeType() == 0)
		{
			for (Skill skill : _subPledgeSkills.values())
			{
				if (disable)
				{
					player.disableSkill(skill, -1);
				}
				else
				{
					player.enableSkill(skill);
				}
			}
		}
		else
		{
			final SubPledge subunit = getSubPledge(player.getPledgeType());
			if (subunit != null)
			{
				for (Skill skill : subunit.getSkills())
				{
					if (disable)
					{
						player.disableSkill(skill, -1);
					}
					else
					{
						player.enableSkill(skill);
					}
				}
			}
		}
	}
	
	public void broadcastToOnlineAllyMembers(L2GameServerPacket packet)
	{
		for (L2Clan clan : ClanTable.getInstance().getClanAllies(getAllyId()))
		{
			clan.broadcastToOnlineMembers(packet);
		}
	}
	
	public void broadcastToOnlineMembers(L2GameServerPacket packet)
	{
		for (L2ClanMember member : _members.values())
		{
			if ((member != null) && member.isOnline())
			{
				member.getPlayerInstance().sendPacket(packet);
			}
		}
	}
	
	public void broadcastCSToOnlineMembers(CreatureSay packet, L2PcInstance broadcaster)
	{
		for (L2ClanMember member : _members.values())
		{
			if ((member != null) && member.isOnline() && !BlockList.isBlocked(member.getPlayerInstance(), broadcaster))
			{
				member.getPlayerInstance().sendPacket(packet);
			}
		}
	}
	
	public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, L2PcInstance player)
	{
		for (L2ClanMember member : _members.values())
		{
			if ((member != null) && member.isOnline() && (member.getPlayerInstance() != player))
			{
				member.getPlayerInstance().sendPacket(packet);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return getName() + "[" + getId() + "]";
	}
	
	public ItemContainer getWarehouse()
	{
		return _warehouse;
	}
	
	public boolean isAtWarWith(int id)
	{
		return _atWarWith.contains(id);
	}
	
	public boolean isAtWarWith(L2Clan clan)
	{
		return _atWarWith.contains(clan.getId());
	}
	
	public boolean isAtWarAttacker(int id)
	{
		return _atWarAttackers.contains(id);
	}
	
	public void setEnemyClan(L2Clan clan)
	{
		_atWarWith.add(clan.getId());
	}
	
	public void setEnemyClan(int id)
	{
		_atWarWith.add(id);
	}
	
	public void setAttackerClan(L2Clan clan)
	{
		_atWarAttackers.add(clan.getId());
	}
	
	public void setAttackerClan(int clan)
	{
		_atWarAttackers.add(clan);
	}
	
	public void deleteEnemyClan(L2Clan clan)
	{
		_atWarWith.remove(clan.getId());
	}
	
	public void deleteAttackerClan(L2Clan clan)
	{
		_atWarAttackers.remove(clan.getId());
	}
	
	public int getHiredGuards()
	{
		return _hiredGuards;
	}
	
	public void incrementHiredGuards()
	{
		_hiredGuards++;
	}
	
	public boolean isAtWar()
	{
		return (_atWarWith != null) && !_atWarWith.isEmpty();
	}
	
	public Set<Integer> getWarList()
	{
		return _atWarWith;
	}
	
	public Set<Integer> getAttackerList()
	{
		return _atWarAttackers;
	}
	
	public void broadcastClanStatus()
	{
		for (L2PcInstance member : getOnlineMembers(0))
		{
			member.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
			member.sendPacket(new PledgeShowMemberListAll(this, member));
		}
	}
	
	/**
	 * used to retrieve subPledge by type
	 * @param pledgeType
	 * @return
	 */
	public final SubPledge getSubPledge(int pledgeType)
	{
		if (_subPledges == null)
		{
			return null;
		}
		return _subPledges.get(pledgeType);
	}
	
	/**
	 * Used to retrieve subPledge by type
	 * @param pledgeName
	 * @return
	 */
	public final SubPledge getSubPledge(String pledgeName)
	{
		if (_subPledges == null)
		{
			return null;
		}
		
		for (SubPledge sp : _subPledges.values())
		{
			if (sp.getName().equalsIgnoreCase(pledgeName))
			{
				return sp;
			}
		}
		return null;
	}
	
	/**
	 * Used to retrieve all subPledges
	 * @return
	 */
	public final SubPledge[] getAllSubPledges()
	{
		return _subPledges.values().toArray(new SubPledge[_subPledges.values().size()]);
	}
	
	public SubPledge createSubPledge(L2PcInstance player, int pledgeType, int leaderId, String subPledgeName)
	{
		SubPledge subPledge = null;
		pledgeType = getAvailablePledgeTypes(pledgeType);
		if (pledgeType == 0)
		{
			if (pledgeType == L2Clan.SUBUNIT_ACADEMY)
			{
				player.sendPacket(SystemMessageId.CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
			}
			else
			{
				player.sendMessage(MessagesData.getInstance().getMessage(player, "clan_create_units_fail"));
			}
			return null;
		}
		
		if (_leader.getObjectId() == leaderId)
		{
			player.sendMessage(MessagesData.getInstance().getMessage(player, "clan_leader_fail"));
			return null;
		}
		
		// Royal Guard 5000 points per each
		// Order of Knights 10000 points per each
		if ((pledgeType != -1) && (((getReputationScore() < FeatureConfig.ROYAL_GUARD_COST) && (pledgeType < L2Clan.SUBUNIT_KNIGHT1)) || ((getReputationScore() < FeatureConfig.KNIGHT_UNIT_COST) && (pledgeType > L2Clan.SUBUNIT_ROYAL2))))
		{
			player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return null;
		}
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO `clan_subpledges` (`clan_id`, `sub_pledge_id`, `name`, `leader_id`) values (?, ?, ?, ?)"))
		{
			ps.setInt(1, getId());
			ps.setInt(2, pledgeType);
			ps.setString(3, subPledgeName);
			ps.setInt(4, pledgeType != -1 ? leaderId : 0);
			ps.execute();
			
			subPledge = new SubPledge(pledgeType, subPledgeName, leaderId);
			_subPledges.put(pledgeType, subPledge);
			
			if (pledgeType != -1)
			{
				// Royal Guard 5000 points per each
				// Order of Knights 10000 points per each
				if (pledgeType < L2Clan.SUBUNIT_KNIGHT1)
				{
					setReputationScore(getReputationScore() - FeatureConfig.ROYAL_GUARD_COST, true);
				}
				else
				{
					setReputationScore(getReputationScore() - FeatureConfig.KNIGHT_UNIT_COST, true);
					// TODO: clan lvl9 or more can reinforce knights cheaper if first knight unit already created, use Config.KNIGHT_REINFORCE_COST
				}
			}
		}
		catch (Exception e)
		{
			LOG.error("Error saving sub clan data!", e);
		}
		
		broadcastToOnlineMembers(new PledgeShowInfoUpdate(_leader.getClan()));
		broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(subPledge, _leader.getClan()));
		return subPledge;
	}
	
	public int getAvailablePledgeTypes(int pledgeType)
	{
		if (pledgeType == SUBUNIT_MAIN_CLAN)
		{
			return SUBUNIT_NONE;
		}
		
		if (_subPledges.get(pledgeType) != null)
		{
			switch (pledgeType)
			{
				case SUBUNIT_ACADEMY:
				{
					return SUBUNIT_NONE;
				}
				case SUBUNIT_ROYAL1:
				{
					pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
					break;
				}
				case SUBUNIT_ROYAL2:
				{
					return SUBUNIT_NONE;
				}
				case SUBUNIT_KNIGHT1:
				{
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
					break;
				}
				case SUBUNIT_KNIGHT2:
				{
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
					break;
				}
				case SUBUNIT_KNIGHT3:
				{
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
					break;
				}
				case SUBUNIT_KNIGHT4:
				{
					return SUBUNIT_NONE;
				}
			}
		}
		return pledgeType;
	}
	
	public void restoreRankPrivs()
	{
		DAOFactory.getInstance().getClanDAO().getPrivileges(getId()).forEach((rank, privileges) -> _privs.get(rank).setPrivs(privileges));
	}
	
	public void initializePrivs()
	{
		for (int i = RANK_FIRST; i <= RANK_LAST; i++)
		{
			_privs.put(i, new RankPrivs(i, 0, new EnumIntBitmask<>(ClanPrivilege.class, false)));
		}
	}
	
	public EnumIntBitmask<ClanPrivilege> getRankPrivs(int rank)
	{
		if (_privs.get(rank) != null)
		{
			return _privs.get(rank).getPrivs();
		}
		return new EnumIntBitmask<>(ClanPrivilege.class, false);
	}
	
	public void setRankPrivs(int rank, int privs)
	{
		final RankPrivs rankPrivileges = _privs.get(rank);
		if (rankPrivileges != null)
		{
			rankPrivileges.setPrivs(privs);
			
			DAOFactory.getInstance().getClanDAO().storePrivileges(getId(), rank, privs);
			
			for (L2ClanMember cm : getMembers())
			{
				if (cm.isOnline() && (cm.getPlayerInstance() != null) && (cm.getPowerGrade() == rank))
				{
					cm.getPlayerInstance().getClanPrivileges().setBitmask(privs);
					cm.getPlayerInstance().sendPacket(new UserInfo(cm.getPlayerInstance()));
					cm.getPlayerInstance().sendPacket(new ExBrExtraUserInfo(cm.getPlayerInstance()));
				}
			}
			broadcastClanStatus();
		}
		else
		{
			_privs.put(rank, new RankPrivs(rank, 0, privs));
			
			DAOFactory.getInstance().getClanDAO().storePrivileges(getId(), rank, privs);
		}
	}
	
	public void updatePrivsForRank(final int rank)
	{
		for (L2ClanMember member : getMembers())
		{
			if (member.isOnline() && (member.getPlayerInstance() != null) && (member.getPowerGrade() == rank))
			{
				if (member.getPlayerInstance().isClanLeader())
				{
					continue;
				}
				member.getPlayerInstance().sendPacket(new UserInfo(member.getPlayerInstance()));
				member.getPlayerInstance().sendPacket(new ExBrExtraUserInfo(member.getPlayerInstance()));
			}
		}
	}
	
	/**
	 * @return all RankPrivs.
	 */
	public final RankPrivs[] getAllRankPrivs()
	{
		if (_privs == null)
		{
			return EMPTY_RANK_PRIVSES_ARRAY;
		}
		return _privs.values().toArray(new RankPrivs[_privs.values().size()]);
	}
	
	public int getLeaderSubPledge(int leaderId)
	{
		int id = 0;
		for (SubPledge sp : _subPledges.values())
		{
			if (sp.getLeaderId() == 0)
			{
				continue;
			}
			if (sp.getLeaderId() == leaderId)
			{
				id = sp.getId();
			}
		}
		return id;
	}
	
	public synchronized void addReputationScore(int value, boolean save)
	{
		setReputationScore(getReputationScore() + value, save);
	}
	
	public synchronized void takeReputationScore(int value, boolean save)
	{
		setReputationScore(getReputationScore() - value, save);
	}
	
	public void setReputationScore(int value, boolean save)
	{
		if ((getReputationScore() >= 0) && (value < 0))
		{
			broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.REPUTATION_POINTS_0_OR_LOWER_CLAN_SKILLS_DEACTIVATED));
			for (L2ClanMember member : _members.values())
			{
				if (member.isOnline() && (member.getPlayerInstance() != null))
				{
					skillsStatus(member.getPlayerInstance(), true);
				}
			}
		}
		else if ((getReputationScore() < 0) && (value >= 0))
		{
			broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_SKILLS_WILL_BE_ACTIVATED_SINCE_REPUTATION_IS_0_OR_HIGHER));
			for (L2ClanMember member : _members.values())
			{
				if (member.isOnline() && (member.getPlayerInstance() != null))
				{
					skillsStatus(member.getPlayerInstance(), false);
				}
			}
		}
		
		setReputationScore(value);
		if (getReputationScore() > 100000000)
		{
			setReputationScore(100000000);
		}
		
		if (getReputationScore() < -100000000)
		{
			setReputationScore(-100000000);
		}
		
		broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
		if (save)
		{
			updateClanScoreInDB();
		}
	}
	
	public int getReputationScore()
	{
		return _reputationScore;
	}
	
	public void setReputationScore(int reputationScore)
	{
		_reputationScore = reputationScore;
	}
	
	public int getRank()
	{
		final L2Clan[] clans = ClanTable.getInstance().getClans2(); // TODO: переписать для final Collection<L2Clan> clans = ClanTable.getInstance().getClans();
		Arrays.parallelSort(clans, REPUTATION_COMPARATOR);
		
		final int place = 1;
		for (int i = 0; i < clans.length; i++) // getValue(),
		{
			if (i == REPUTATION_PLACES)
			{
				return 0;
			}
			
			final L2Clan clan = clans[i];
			if (clan == this)
			{
				return place + i;
			}
		}
		return 0;
	}
	
	public int getAuctionBiddedAt()
	{
		return _auctionBiddedAt;
	}
	
	public void setAuctionBiddedAt(int id, boolean storeInDb)
	{
		_auctionBiddedAt = id;
		
		if (storeInDb)
		{
			try (Connection con = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("UPDATE `clan_data` SET `auction_bid_at` = ? WHERE `clan_id` = ?"))
			{
				ps.setInt(1, id);
				ps.setInt(2, getId());
				ps.execute();
				ps.close();
			}
			catch (Exception e)
			{
				LOG.warn("Could not store auction for clan!", e);
			}
		}
	}
	
	/**
	 * @param activeChar the clan inviting player.
	 * @param target the invited player.
	 * @param pledgeType the pledge type to join.
	 * @return {core true} if activeChar and target meet various conditions to join a clan.
	 */
	public boolean checkClanJoinCondition(L2PcInstance activeChar, L2PcInstance target, int pledgeType)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_JOIN_CLAN))
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}
		
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return false;
		}
		
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_INVITE_YOURSELF);
			return false;
		}
		
		if (getCharPenaltyExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(SystemMessageId.YOU_MUST_WAIT_BEFORE_ACCEPTING_A_NEW_MEMBER);
			return false;
		}
		
		if (target.getClanId() != 0)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_WORKING_WITH_ANOTHER_CLAN);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (((target.getLevel() > 40) || (target.getClassId().level() >= 2)) && (pledgeType == -1))
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DOESNOT_MEET_REQUIREMENTS_TO_JOIN_ACADEMY);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			activeChar.sendPacket(SystemMessageId.ACADEMY_REQUIREMENTS);
			return false;
		}
		
		if (getSubPledgeMembersCount(pledgeType) >= getMaxNrOfMembers(pledgeType))
		{
			if (pledgeType == 0)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_IS_FULL);
				sm.addString(getName());
				activeChar.sendPacket(sm);
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.SUBCLAN_IS_FULL);
			}
			return false;
		}
		return true;
	}
	
	public void changeLargeCrest(int crestId)
	{
		if (getCrestLargeId() != 0)
		{
			CrestTable.getInstance().removeCrest(getCrestLargeId());
		}
		
		setCrestLargeId(crestId);
		
		DAOFactory.getInstance().getClanDAO().changeLargeCrest(crestId, getId());
		
		for (L2PcInstance member : getOnlineMembers(0))
		{
			member.broadcastUserInfo();
		}
	}
	
	public void changeAllyCrest(int crestId, boolean onlyThisClan)
	{
		String sqlStatement = UPDATE_CLAN_ALLY_CREST_CLAN;
		int allyId = getId();
		if (!onlyThisClan)
		{
			if (getAllyCrestId() != 0)
			{
				CrestTable.getInstance().removeCrest(getAllyCrestId());
			}
			sqlStatement = UPDATE_CLAN_ALLY_CREST_ALLY;
			allyId = getAllyId();
		}
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sqlStatement))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, allyId);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warn("Could not update ally crest for ally/clan id {}!", allyId, e);
		}
		
		if (onlyThisClan)
		{
			setAllyCrestId(crestId);
			for (L2PcInstance member : getOnlineMembers(0))
			{
				member.broadcastUserInfo();
			}
		}
		else
		{
			for (L2Clan clan : ClanTable.getInstance().getClanAllies(getAllyId()))
			{
				clan.setAllyCrestId(crestId);
				for (L2PcInstance member : clan.getOnlineMembers(0))
				{
					member.broadcastUserInfo();
				}
			}
		}
	}
	
	public void changeClanCrest(int crestId)
	{
		if (getCrestId() != 0)
		{
			CrestTable.getInstance().removeCrest(getCrestId());
		}
		
		setCrestId(crestId);
		
		DAOFactory.getInstance().getClanDAO().changeClanCrest(crestId, getId());
		
		for (L2PcInstance member : getOnlineMembers(0))
		{
			member.broadcastUserInfo();
		}
	}
	
	/**
	 * @param activeChar the clan inviting player.
	 * @param target the invited player.
	 * @return {core true} if activeChar and target meet various conditions to join a clan.
	 */
	public boolean checkAllyJoinCondition(L2PcInstance activeChar, L2PcInstance target)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if ((activeChar.getAllyId() == 0) || !activeChar.isClanLeader() || (activeChar.getClanId() != activeChar.getAllyId()))
		{
			activeChar.sendPacket(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return false;
		}
		
		final L2Clan leaderClan = activeChar.getClan();
		if (leaderClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (leaderClan.getAllyPenaltyType() == PENALTY_TYPE_DISMISS_CLAN)
			{
				activeChar.sendPacket(SystemMessageId.CANT_INVITE_CLAN_WITHIN_1_DAY);
				return false;
			}
		}
		
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return false;
		}
		
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_INVITE_YOURSELF);
			return false;
		}
		
		if (target.getClan() == null)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_MUST_BE_IN_CLAN);
			return false;
		}
		
		if (!target.isClanLeader())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			return false;
		}
		
		final L2Clan targetClan = target.getClan();
		if (target.getAllyId() != 0)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_ALREADY_MEMBER_OF_S2_ALLIANCE);
			sm.addString(targetClan.getName());
			sm.addString(targetClan.getAllyName());
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (targetClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_LEAVED)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANT_ENTER_ALLIANCE_WITHIN_1_DAY);
				sm.addString(target.getClan().getName());
				sm.addString(target.getClan().getAllyName());
				activeChar.sendPacket(sm);
				return false;
			}
			
			if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_DISMISSED)
			{
				activeChar.sendPacket(SystemMessageId.CANT_ENTER_ALLIANCE_WITHIN_1_DAY);
				return false;
			}
		}
		
		if (activeChar.isInsideZone(ZoneId.SIEGE) && target.isInsideZone(ZoneId.SIEGE))
		{
			activeChar.sendPacket(SystemMessageId.OPPOSING_CLAN_IS_PARTICIPATING_IN_SIEGE);
			return false;
		}
		
		if (leaderClan.isAtWarWith(targetClan.getId()))
		{
			activeChar.sendPacket(SystemMessageId.MAY_NOT_ALLY_CLAN_BATTLE);
			return false;
		}
		
		if (ClanTable.getInstance().getClanAllies(activeChar.getAllyId()).size() >= CharacterConfig.ALT_MAX_NUM_OF_CLANS_IN_ALLY)
		{
			activeChar.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_LIMIT);
			return false;
		}
		return true;
	}
	
	public long getAllyPenaltyExpiryTime()
	{
		return _allyPenaltyExpiryTime;
	}
	
	public int getAllyPenaltyType()
	{
		return _allyPenaltyType;
	}
	
	public void setAllyPenaltyExpiryTime(long expiryTime, int penaltyType)
	{
		_allyPenaltyExpiryTime = expiryTime;
		_allyPenaltyType = penaltyType;
	}
	
	public long getCharPenaltyExpiryTime()
	{
		return _charPenaltyExpiryTime;
	}
	
	public void setCharPenaltyExpiryTime(long time)
	{
		_charPenaltyExpiryTime = time;
	}
	
	public long getDissolvingExpiryTime()
	{
		return _dissolvingExpiryTime;
	}
	
	public void setDissolvingExpiryTime(long time)
	{
		_dissolvingExpiryTime = time;
	}
	
	public void createAlly(L2PcInstance player, String allyName)
	{
		if (null == player)
		{
			return;
		}
		
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE);
			return;
		}
		
		if (getAllyId() != 0)
		{
			player.sendPacket(SystemMessageId.ALREADY_JOINED_ALLIANCE);
			return;
		}
		
		if (getLevel() < 5)
		{
			player.sendPacket(SystemMessageId.TO_CREATE_AN_ALLY_YOU_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
			return;
		}
		
		if (getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (getAllyPenaltyType() == L2Clan.PENALTY_TYPE_DISSOLVE_ALLY)
			{
				player.sendPacket(SystemMessageId.CANT_CREATE_ALLIANCE_10_DAYS_DISOLUTION);
				return;
			}
		}
		
		if (getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_CREATE_ALLY_WHILE_DISSOLVING);
			return;
		}
		
		if (!Util.isAlphaNumeric(allyName))
		{
			player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME);
			return;
		}
		
		if ((allyName.length() > 16) || (allyName.length() < 2))
		{
			player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME_LENGTH);
			return;
		}
		
		if (ClanTable.getInstance().isAllyExists(allyName))
		{
			player.sendPacket(SystemMessageId.ALLIANCE_ALREADY_EXISTS);
			return;
		}
		setAllyId(getId());
		setAllyName(allyName.trim());
		setAllyPenaltyExpiryTime(0, 0);
		DAOFactory.getInstance().getClanDAO().updateClan(this);
		player.sendPacket(new UserInfo(player));
		player.sendPacket(new ExBrExtraUserInfo(player));
		// TODO: Need correct message id
		player.sendMessage(MessagesData.getInstance().getMessage(player, "clan_alliance_done").replace("%s%", allyName + ""));
	}
	
	public void dissolveAlly(L2PcInstance player)
	{
		if (getAllyId() == 0)
		{
			player.sendPacket(SystemMessageId.NO_CURRENT_ALLIANCES);
			return;
		}
		
		if (!player.isClanLeader() || (getId() != getAllyId()))
		{
			player.sendPacket(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return;
		}
		
		if (player.isInsideZone(ZoneId.SIEGE))
		{
			player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_ALLY_WHILE_IN_SIEGE);
			return;
		}
		broadcastToOnlineAllyMembers(SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_DISOLVED));
		
		final long currentTime = System.currentTimeMillis();
		for (L2Clan clan : ClanTable.getInstance().getClanAllies(getAllyId()))
		{
			if (clan.getId() != getId())
			{
				clan.setAllyId(0);
				clan.setAllyName(null);
				clan.setAllyPenaltyExpiryTime(0, 0);
				DAOFactory.getInstance().getClanDAO().updateClan(clan);
			}
		}
		setAllyId(0);
		setAllyName(null);
		changeAllyCrest(0, false);
		setAllyPenaltyExpiryTime(currentTime + (CharacterConfig.ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED * 86400000L), L2Clan.PENALTY_TYPE_DISSOLVE_ALLY); // 24*60*60*1000 = 86400000
		DAOFactory.getInstance().getClanDAO().updateClan(this);
	}
	
	public boolean levelUpClan(L2PcInstance player)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}
		if (System.currentTimeMillis() < getDissolvingExpiryTime())
		{
			player.sendPacket(SystemMessageId.CANNOT_RISE_LEVEL_WHILE_DISSOLUTION_IN_PROGRESS);
			return false;
		}
		
		boolean increaseClanLevel = false;
		
		switch (getLevel())
		{
			case 0:
			{
				// Upgrade to 1
				if ((player.getSp() >= 20000) && (player.getAdena() >= 650000))
				{
					if (player.reduceAdena("ClanLvl", 650000, player.getTarget(), true))
					{
						player.setSp(player.getSp() - 20000);
						final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1);
						sp.addInt(20000);
						player.sendPacket(sp);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 1:
			{
				// Upgrade to 2
				if ((player.getSp() >= 100000) && (player.getAdena() >= 2500000))
				{
					if (player.reduceAdena("ClanLvl", 2500000, player.getTarget(), true))
					{
						player.setSp(player.getSp() - 100000);
						final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1);
						sp.addInt(100000);
						player.sendPacket(sp);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 2:
			{
				// Upgrade to 3
				if ((player.getSp() >= 350000) && (player.getInventory().getItemByItemId(1419) != null))
				{
					// TODO unhardcode these item IDs
					// itemId 1419 == Blood Mark
					if (player.destroyItemByItemId("ClanLvl", 1419, 1, player.getTarget(), false))
					{
						player.setSp(player.getSp() - 350000);
						final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1);
						sp.addInt(350000);
						player.sendPacket(sp);
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
						sm.addItemName(1419);
						player.sendPacket(sm);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 3:
			{
				// Upgrade to 4
				if ((player.getSp() >= 1000000) && (player.getInventory().getItemByItemId(3874) != null))
				{
					// itemId 3874 == Alliance Manifesto
					if (player.destroyItemByItemId("ClanLvl", 3874, 1, player.getTarget(), false))
					{
						player.setSp(player.getSp() - 1000000);
						final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1);
						sp.addInt(1000000);
						player.sendPacket(sp);
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
						sm.addItemName(3874);
						player.sendPacket(sm);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 4:
			{
				// Upgrade to 5
				if ((player.getSp() >= 2500000) && (player.getInventory().getItemByItemId(3870) != null))
				{
					// itemId 3870 == Seal of Aspiration
					if (player.destroyItemByItemId("ClanLvl", 3870, 1, player.getTarget(), false))
					{
						player.setSp(player.getSp() - 2500000);
						final SystemMessage sp = SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1);
						sp.addInt(2500000);
						player.sendPacket(sp);
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
						sm.addItemName(3870);
						player.sendPacket(sm);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 5:
			{
				// Upgrade to 6
				if ((getReputationScore() >= FeatureConfig.CLAN_LEVEL_6_COST) && (getMembersCount() >= FeatureConfig.CLAN_LEVEL_6_REQUIREMENT))
				{
					setReputationScore(getReputationScore() - FeatureConfig.CLAN_LEVEL_6_COST, true);
					final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
					cr.addInt(FeatureConfig.CLAN_LEVEL_6_COST);
					player.sendPacket(cr);
					increaseClanLevel = true;
				}
				break;
			}
			case 6:
			{
				// Upgrade to 7
				if ((getReputationScore() >= FeatureConfig.CLAN_LEVEL_7_COST) && (getMembersCount() >= FeatureConfig.CLAN_LEVEL_7_REQUIREMENT))
				{
					setReputationScore(getReputationScore() - FeatureConfig.CLAN_LEVEL_7_COST, true);
					final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
					cr.addInt(FeatureConfig.CLAN_LEVEL_7_COST);
					player.sendPacket(cr);
					increaseClanLevel = true;
				}
				break;
			}
			case 7:
			{
				// Upgrade to 8
				if ((getReputationScore() >= FeatureConfig.CLAN_LEVEL_8_COST) && (getMembersCount() >= FeatureConfig.CLAN_LEVEL_8_REQUIREMENT))
				{
					setReputationScore(getReputationScore() - FeatureConfig.CLAN_LEVEL_8_COST, true);
					final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
					cr.addInt(FeatureConfig.CLAN_LEVEL_8_COST);
					player.sendPacket(cr);
					increaseClanLevel = true;
				}
				break;
			}
			case 8:
			{
				// Upgrade to 9
				if ((getReputationScore() >= FeatureConfig.CLAN_LEVEL_9_COST) && (player.getInventory().getItemByItemId(9910) != null) && (getMembersCount() >= FeatureConfig.CLAN_LEVEL_9_REQUIREMENT))
				{
					// itemId 9910 == Blood Oath
					if (player.destroyItemByItemId("ClanLvl", 9910, 150, player.getTarget(), false))
					{
						setReputationScore(getReputationScore() - FeatureConfig.CLAN_LEVEL_9_COST, true);
						final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
						cr.addInt(FeatureConfig.CLAN_LEVEL_9_COST);
						player.sendPacket(cr);
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED);
						sm.addItemName(9910);
						sm.addLong(150);
						player.sendPacket(sm);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 9:
			{
				// Upgrade to 10
				if ((getReputationScore() >= FeatureConfig.CLAN_LEVEL_10_COST) && (player.getInventory().getItemByItemId(9911) != null) && (getMembersCount() >= FeatureConfig.CLAN_LEVEL_10_REQUIREMENT))
				{
					// itemId 9911 == Blood Alliance
					if (player.destroyItemByItemId("ClanLvl", 9911, 5, player.getTarget(), false))
					{
						setReputationScore(getReputationScore() - FeatureConfig.CLAN_LEVEL_10_COST, true);
						final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
						cr.addInt(FeatureConfig.CLAN_LEVEL_10_COST);
						player.sendPacket(cr);
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED);
						sm.addItemName(9911);
						sm.addLong(5);
						player.sendPacket(sm);
						increaseClanLevel = true;
					}
				}
				break;
			}
			case 10:
			{
				// Upgrade to 11
				boolean hasTerritory = false;
				for (Territory terr : TerritoryWarManager.getInstance().getAllTerritories())
				{
					if (terr.getOwnerClan().getId() == getId())
					{
						hasTerritory = true;
						break;
					}
				}
				
				if (hasTerritory && (getReputationScore() >= FeatureConfig.CLAN_LEVEL_11_COST) && (getMembersCount() >= FeatureConfig.CLAN_LEVEL_11_REQUIREMENT))
				{
					setReputationScore(getReputationScore() - FeatureConfig.CLAN_LEVEL_11_COST, true);
					final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
					cr.addInt(FeatureConfig.CLAN_LEVEL_11_COST);
					player.sendPacket(cr);
					increaseClanLevel = true;
				}
				break;
			}
			default:
			{
				return false;
			}
		}
		
		if (!increaseClanLevel)
		{
			player.sendPacket(SystemMessageId.FAILED_TO_INCREASE_CLAN_LEVEL);
			return false;
		}
		
		// the player should know that he has less sp now :p
		final StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.SP, player.getSp());
		player.sendPacket(su);
		player.sendPacket(new ItemList(player, false));
		changeLevel(getLevel() + 1);
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanLvlUp(player, this));
		return true;
	}
	
	public void changeLevel(int level)
	{
		DAOFactory.getInstance().getClanDAO().changeLevel(level, getId());
		
		setLevel(level);
		
		if (getLeader().isOnline())
		{
			L2PcInstance leader = getLeader().getPlayerInstance();
			if (level > 4)
			{
				SiegeManager.getInstance().addSiegeSkills(leader);
				leader.sendPacket(SystemMessageId.CLAN_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
			}
			else if (level < 5)
			{
				SiegeManager.getInstance().removeSiegeSkills(leader);
			}
		}
		
		// notify all the members about it
		broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEVEL_INCREASED));
		broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
	}
	
	public void restoreSubPledges()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_CLAN_SUBPLEDGES))
		{
			// Retrieve all subpledges of this clan from the database
			ps.setInt(1, getId());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					final int id = rset.getInt("sub_pledge_id");
					final String name = rset.getString("name");
					final int leaderId = rset.getInt("leader_id");
					// Create a SubPledge object for each record
					final SubPledge pledge = new SubPledge(id, name, leaderId);
					_subPledges.put(id, pledge);
				}
				rset.close();
			}
			ps.close();
		}
		catch (Exception e)
		{
			LOG.warn("Couldn't restore clan sub-units!", e);
		}
	}
	
	public void restoreSkills()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_CLAN_SKILLS))
		{
			// Retrieve all skills of this L2PcInstance from the database
			ps.setInt(1, getId());
			try (ResultSet rset = ps.executeQuery())
			{
				// Go though the recordset of this SQL query
				while (rset.next())
				{
					final int id = rset.getInt("skill_id");
					final int level = rset.getInt("skill_level");
					// Create a L2Skill object for each record
					final Skill skill = SkillData.getInstance().getSkill(id, level);
					// Add the L2Skill object to the L2Clan _skills
					final int subType = rset.getInt("sub_pledge_id");
					
					if (subType == -2)
					{
						_skills.put(skill.getId(), skill);
					}
					else if (subType == 0)
					{
						_subPledgeSkills.put(skill.getId(), skill);
					}
					else
					{
						final SubPledge subunit = _subPledges.get(subType);
						if (subunit != null)
						{
							subunit.addNewSkill(skill);
						}
						else
						{
							LOG.info("Missing subpledge {} for clan {}, skill skipped.", subType, getId());
						}
					}
				}
				rset.close();
			}
			ps.close();
		}
		catch (Exception e)
		{
			LOG.error("Error restoring clan skills!", e);
		}
	}
	
	/**
	 * Check if this clan can learn the skill for the given skill ID, level.
	 * @param skillId
	 * @param skillLevel
	 * @return {@code true} if skill can be learned.
	 */
	public boolean isLearnableSubSkill(int skillId, int skillLevel)
	{
		Skill current = _subPledgeSkills.get(skillId);
		// is next level?
		if ((current != null) && ((current.getLevel() + 1) == skillLevel))
		{
			return true;
		}
		// is first level?
		if ((current == null) && (skillLevel == 1))
		{
			return true;
		}
		// other sub-pledges
		for (SubPledge subunit : _subPledges.values())
		{
			// disable academy
			if (subunit.getId() == -1)
			{
				continue;
			}
			current = subunit.getSkill(skillId);
			// is next level?
			if ((current != null) && ((current.getLevel() + 1) == skillLevel))
			{
				return true;
			}
			// is first level?
			if ((current == null) && (skillLevel == 1))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isLearnableSubPledgeSkill(Skill skill, int subType)
	{
		// academy
		if (subType == -1)
		{
			return false;
		}
		
		final int id = skill.getId();
		Skill current;
		if (subType == 0)
		{
			current = _subPledgeSkills.get(id);
		}
		else
		{
			current = _subPledges.get(subType).getSkill(id);
		}
		// is next level?
		if ((current != null) && ((current.getLevel() + 1) == skill.getLevel()))
		{
			return true;
		}
		// is first level?
		if ((current == null) && (skill.getLevel() == 1))
		{
			return true;
		}
		
		return false;
	}
	
	public List<SubPledgeSkill> getAllSubSkills()
	{
		final List<SubPledgeSkill> list = new LinkedList<>();
		for (Skill skill : _subPledgeSkills.values())
		{
			list.add(new SubPledgeSkill(0, skill.getId(), skill.getLevel()));
		}
		for (SubPledge subunit : _subPledges.values())
		{
			for (Skill skill : subunit.getSkills())
			{
				list.add(new SubPledgeSkill(subunit.getId(), skill.getId(), skill.getLevel()));
			}
		}
		return list;
	}
	
	public void setNewLeaderId(int objectId, boolean storeInDb)
	{
		_newLeaderId = objectId;
		if (storeInDb)
		{
			DAOFactory.getInstance().getClanDAO().updateClan(this);
		}
	}
	
	public int getNewLeaderId()
	{
		return _newLeaderId;
	}
	
	public L2PcInstance getNewLeader()
	{
		return L2World.getInstance().getPlayer(_newLeaderId);
	}
	
	public String getNewLeaderName()
	{
		return CharNameTable.getInstance().getNameById(_newLeaderId);
	}
	
	public int getSiegeKills()
	{
		return _siegeKills.get();
	}
	
	public int getSiegeDeaths()
	{
		return _siegeDeaths.get();
	}
	
	public int addSiegeKill()
	{
		return _siegeKills.incrementAndGet();
	}
	
	public int addSiegeDeath()
	{
		return _siegeDeaths.incrementAndGet();
	}
	
	public void clearSiegeKills()
	{
		_siegeKills.set(0);
	}
	
	public void clearSiegeDeaths()
	{
		_siegeDeaths.set(0);
	}
	
	private static class ClanReputationComparator implements Comparator<L2Clan>
	{
		public ClanReputationComparator()
		{
			
		}
		
		@Override
		public int compare(final L2Clan o1, final L2Clan o2)
		{
			if ((o1 == null) || (o2 == null))
			{
				return 0;
			}
			return o2.getReputationScore() - o1.getReputationScore();
		}
	}
}
