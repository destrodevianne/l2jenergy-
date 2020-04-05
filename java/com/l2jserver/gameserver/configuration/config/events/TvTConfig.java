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
package com.l2jserver.gameserver.configuration.config.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.gameeventengine.GameEventTeam;
import com.l2jserver.gameserver.model.gameeventengine.IGameEventConfig;
import com.l2jserver.gameserver.model.gameeventengine.TeamGameEventConfig;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.util.IXmlReader;

public class TvTConfig extends TeamGameEventConfig implements IGameEventConfig, IXmlReader
{
	protected TvTConfig()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("configuration/events/tvt.xml");
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node cd = n.getFirstChild(); cd != null; cd = cd.getNextSibling())
				{
					if ("EventInInstance".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_IN_INSTANCE = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventInstanceFile".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_INSTANCE_FILE = attrs.getNamedItem("val").getNodeValue();
					}
					else if ("EventTimeInterval".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_TIME_INTERVAL = attrs.getNamedItem("val").getNodeValue().split(",");
					}
					else if ("EventRunningTime".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_RUNNING_TIME = Byte.parseByte(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventPlayersInTeams".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_MIN_PLAYERS_IN_TEAMS = Short.parseShort(attrs.getNamedItem("min").getNodeValue());
						EVENT_MAX_PLAYERS_IN_TEAMS = Short.parseShort(attrs.getNamedItem("max").getNodeValue());
					}
					else if ("EventPlayerLevel".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_MIN_LVL = Byte.parseByte(attrs.getNamedItem("min").getNodeValue());
						EVENT_MAX_LVL = Byte.parseByte(attrs.getNamedItem("max").getNodeValue());
					}
					else if ("EventParticipationTime".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_PARTICIPATION_TIME = Byte.parseByte(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventRespawnTeleportDelay".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_RESPAWN_TELEPORT_DELAY = Byte.parseByte(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventStartLeaveTeleportDelay".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_START_LEAVE_TELEPORT_DELAY = Byte.parseByte(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventParticipation".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_PARTICIPATION_FEE = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventTakeItem".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_TAKE_ITEM_ID = Short.parseShort(attrs.getNamedItem("id").getNodeValue());
						EVENT_TAKE_COUNT = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());
					}
					else if ("EventEnabledKillBonus".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_ENABLED_KILL_BONUS = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventKillBonusReward".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_KILL_BONUS_ID = Short.parseShort(attrs.getNamedItem("id").getNodeValue());
						EVENT_KILL_BONUS_COUNT = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());
					}
					else if ("EventEffectsRemoval".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_EFFECTS_REMOVAL = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("Team".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						byte teamId = Byte.parseByte(attrs.getNamedItem("id").getNodeValue());
						String name = attrs.getNamedItem("name").getNodeValue();
						String[] coord = attrs.getNamedItem("coordinates").getNodeValue().split(",");
						int[] coordinates = new int[4];
						for (int i = 0; i < coord.length; i++)
						{
							coordinates[i] = Integer.parseInt(coord[i]);
						}
						EVENT_TEAM_ID_NAME_COORDINATES.add(new GameEventTeam(teamId, name, coordinates));
					}
					else if ("EventReward".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						String[] itemIds = attrs.getNamedItem("id").getNodeValue().split(",");
						String[] counts = attrs.getNamedItem("count").getNodeValue().split(",");
						EVENT_REWARDS = new ArrayList<>();
						for (int i = 0; i < itemIds.length; i++)
						{
							EVENT_REWARDS.add(new int[]
							{
								Integer.parseInt(itemIds[i]),
								Integer.parseInt(counts[i])
							});
						}
					}
					else if ("EventRewardTeamTie".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_REWARD_TEAM_TIE = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventMageBuffs".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						String[] skills = attrs.getNamedItem("val").getNodeValue().split(";");
						if (!skills[0].isEmpty())
						{
							EVENT_MAGE_BUFFS = new HashMap<>(skills.length);
							for (String skill : skills)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									LOG.warn("TvTConfig: invalid config property -> EventMageBuffs {}", skill);
								}
								else
								{
									try
									{
										EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											LOG.warn("TvTConfig: invalid config property -> EventMageBuffs {}", skill);
										}
									}
								}
							}
						}
					}
					else if ("EventFighterBuffs".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						String[] skills = attrs.getNamedItem("val").getNodeValue().split(";");
						
						if (!skills[0].isEmpty())
						{
							EVENT_FIGHTER_BUFFS = new HashMap<>(skills.length);
							for (String skill : skills)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									LOG.warn("TvTConfig: invalid config property -> EventFighterBuffs {}", skill);
								}
								else
								{
									try
									{
										EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											LOG.warn("TvTConfig: invalid config property -> EventFighterBuffs {}", skill);
										}
									}
								}
							}
						}
					}
					else if ("EventRestrictItems".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						String[] items = attrs.getNamedItem("val").getNodeValue().split(",");
						EVENT_RESTRICT_ITEMS = new int[items.length];
						for (int i = 0; i < items.length; i++)
						{
							EVENT_RESTRICT_ITEMS[i] = Integer.parseInt(items[i]);
						}
					}
					else if ("EventRestrictSkills".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						String[] skills = attrs.getNamedItem("val").getNodeValue().split(",");
						EVENT_RESTRICT_SKILLS = new ArrayList<>(skills.length);
						for (String skill : skills)
						{
							EVENT_RESTRICT_SKILLS.add(new SkillHolder(Integer.parseInt(skill), 1));
						}
					}
					else if ("EventPotionsAllowed".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventScrollsAllowed".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_SCROLL_ALLOWED = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventSummonByItemAllowed".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventTargetTeamMembersAllowed".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventRequireMinFragsToReward".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_REQUIRE_MIN_FRAGS_TO_REWARD = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
					}
					else if ("EventMinFragsRequired".equalsIgnoreCase(cd.getNodeName()))
					{
						attrs = cd.getAttributes();
						EVENT_MIN_FRAGS_TO_REWARD = Byte.parseByte(attrs.getNamedItem("val").getNodeValue());
					}
				}
			}
		}
	}
	
	public static TvTConfig getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TvTConfig INSTANCE = new TvTConfig();
	}
}
