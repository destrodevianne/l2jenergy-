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
package com.l2jserver.gameserver.instancemanager.vote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.custom.TopsConfig;
import com.l2jserver.gameserver.enums.MailMessageType;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.itemcontainer.Mail;
import com.l2jserver.gameserver.util.Broadcast;

public abstract class VoteSystem implements Runnable
{
	protected static final Logger LOG = LoggerFactory.getLogger(VoteSystem.class);
	
	private static List<VoteSystem> _voteSystems = new ArrayList<>();
	
	private final int _votesDiff;
	private final boolean _allowReport;
	private final int _boxes;
	private final Map<Integer, Integer> _rewards;
	private final int _checkMins;
	private int _lastVotes = 0;
	private final Map<String, Integer> _playerIps = new HashMap<>();
	
	public static void initialize()
	{
		if (TopsConfig.ENABLE_VOTE_SYSTEM)
		{
			if (TopsConfig.ALLOW_NETWORK_VOTE_REWARD)
			{
				_voteSystems.add(new Network(TopsConfig.NETWORK_VOTES_DIFFERENCE, TopsConfig.ALLOW_NETWORK_GAME_SERVER_REPORT, TopsConfig.NETWORK_DUALBOXES_ALLOWED, TopsConfig.NETWORK_REWARD, TopsConfig.NETWORK_REWARD_CHECK_TIME));
			}
			if (TopsConfig.ALLOW_TOPZONE_VOTE_REWARD)
			{
				_voteSystems.add(new Topzone(TopsConfig.TOPZONE_VOTES_DIFFERENCE, TopsConfig.ALLOW_TOPZONE_GAME_SERVER_REPORT, TopsConfig.TOPZONE_DUALBOXES_ALLOWED, TopsConfig.TOPZONE_REWARD, TopsConfig.TOPZONE_REWARD_CHECK_TIME));
			}
			if (TopsConfig.ALLOW_HOPZONE_VOTE_REWARD)
			{
				_voteSystems.add(new Hopzone(TopsConfig.HOPZONE_VOTES_DIFFERENCE, TopsConfig.ALLOW_HOPZONE_GAME_SERVER_REPORT, TopsConfig.HOPZONE_DUALBOXES_ALLOWED, TopsConfig.HOPZONE_REWARD, TopsConfig.HOPZONE_REWARD_CHECK_TIME));
			}
			LOG.info("Vote System: Started.");
		}
		else
		{
			LOG.info("Vote System: Engine is disabled.");
		}
	}
	
	public static VoteSystem getVoteSystem(String name)
	{
		for (VoteSystem vs : _voteSystems)
		{
			if (vs.getSiteName().equals(name))
			{
				return vs;
			}
		}
		return null;
	}
	
	public VoteSystem(int votesDiff, boolean allowReport, int boxes, Map<Integer, Integer> rewards, int checkMins)
	{
		_votesDiff = votesDiff;
		_allowReport = allowReport;
		_boxes = boxes;
		_rewards = rewards;
		_checkMins = checkMins;
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this, checkMins * 1000 * 60, checkMins * 1000 * 60);
	}
	
	protected void reward()
	{
		int currentVotes = getVotes();
		
		if (currentVotes == -1)
		{
			LOG.warn("There was a problem on getting server votes.");
			return;
		}
		
		if (_lastVotes == 0)
		{
			_lastVotes = currentVotes;
			Broadcast.toAllOnlinePlayers(getSiteName() + ": Current vote count is " + currentVotes + ".");
			Broadcast.toAllOnlinePlayers(getSiteName() + ": We need " + ((_lastVotes + getVoteDiff()) - currentVotes) + " vote(s) for reward.");
			if (_allowReport)
			{
				LOG.info("Server votes on {}: {}", getSiteName(), currentVotes);
				LOG.info("Votes needed for reward: {}", ((_lastVotes + getVoteDiff()) - currentVotes));
			}
			return;
		}
		
		if (currentVotes >= (_lastVotes + getVoteDiff()))
		{
			Collection<L2PcInstance> pls = L2World.getInstance().getPlayers();
			
			int charId = ((L2Object) pls).getObjectId();
			
			if (_allowReport)
			{
				LOG.info("Server votes on {}: {}", getSiteName(), currentVotes);
				LOG.info("Votes needed for next reward: {}", ((currentVotes + getVoteDiff()) - currentVotes));
			}
			Broadcast.toAllOnlinePlayers(getSiteName() + ": Everyone has been rewarded.");
			Broadcast.toAllOnlinePlayers(getSiteName() + ": Current vote count is " + currentVotes + ".");
			Broadcast.toAllOnlinePlayers(getSiteName() + ": We need " + getVoteDiff() + " vote(s) for next reward.");
			
			for (L2PcInstance p : pls)
			{
				if ((p.getClient() == null) || p.getClient().isDetached())
				{
					continue;
				}
				
				boolean canReward = false;
				String pIp = p.getClient().getConnection().getInetAddress().getHostAddress();
				if (_playerIps.containsKey(pIp))
				{
					int count = _playerIps.get(pIp);
					if (count < getBoxes())
					{
						_playerIps.remove(pIp);
						_playerIps.put(pIp, count + 1);
						canReward = true;
					}
				}
				else
				{
					canReward = true;
					_playerIps.put(pIp, 1);
				}
				
				if (canReward)
				{
					L2PcInstance pl = (L2PcInstance) L2World.getInstance().findObject(charId);
					
					String text1 = "Reward vote bonus raiting!"; // TODO: перенести в xml
					String text2 = "Thank you for your vote in Hopzone/topzone/Network raiting. Best regards " + TopsConfig.TOP_SERVER_ADDRESS; // TODO: перенести в xml
					Message msg = new Message(pl.getObjectId(), text1, text2, MailMessageType.NEWS);
					Mail attachments = msg.createAttachments();
					
					for (int i : _rewards.keySet())
					{
						attachments.addItem("Vote reward.", i, _rewards.get(i), null, null);
					}
					MailManager.getInstance().sendMessage(msg);
				}
				else
				{
					L2PcInstance pl = (L2PcInstance) L2World.getInstance().findObject(charId);
					String text1 = "[ERROR] Reward vote bonus raiting!"; // TODO: перенести в xml
					String text2 = "Already " + getBoxes() + " character(s) of your ip have been rewarded, so this character won't be rewarded."; // TODO: перенести в xml
					Message msg = new Message(pl.getObjectId(), text1, text2, MailMessageType.NEWS);
					MailManager.getInstance().sendMessage(msg);
				}
			}
			_playerIps.clear();
			_lastVotes = currentVotes;
		}
		else
		{
			if (_allowReport)
			{
				LOG.info("Server votes on {}: {}", getSiteName(), currentVotes);
				LOG.info("Votes needed for next reward: {}", ((_lastVotes + getVoteDiff()) - currentVotes));
			}
			Broadcast.toAllOnlinePlayers(getSiteName() + ": Current vote count is " + currentVotes + ".");
			Broadcast.toAllOnlinePlayers(getSiteName() + ": We need " + ((_lastVotes + getVoteDiff()) - currentVotes) + " vote(s) for reward.");
		}
	}
	
	public int getBoxes()
	{
		return _boxes;
	}
	
	public int getVoteDiff()
	{
		return _votesDiff;
	}
	
	public int getCheckMins()
	{
		return _checkMins;
	}
	
	public abstract int getVotes();
	
	public abstract String getSiteName();
}
