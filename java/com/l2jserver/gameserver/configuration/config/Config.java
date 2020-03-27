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
package com.l2jserver.gameserver.configuration.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.PropertiesParser;

/**
 * This class loads all the game server related configurations from files.<br>
 * The files are usually located in config folder in server root folder.<br>
 * Each configuration has a default value (that should reflect retail behavior).
 */
public final class Config
{
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	
	public static final String FORTSIEGE_CONFIGURATION_FILE = "./config/FortSiege.properties";
	public static final String L2JMOD_CONFIG_FILE = "./config/L2JMods.properties";
	public static final String SIEGE_CONFIGURATION_FILE = "./config/Siege.properties";
	
	// --------------------------------------------------
	// L2JMods Settings
	// --------------------------------------------------
	public static boolean TVT_EVENT_ENABLED;
	public static boolean TVT_EVENT_IN_INSTANCE;
	public static String TVT_EVENT_INSTANCE_FILE;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
	public static int TVT_EVENT_MAX_PARTICIPANTS_PER_IP;
	public static boolean TVT_ALLOW_VOICED_COMMAND;
	
	// --------------------------------------------------
	// No classification assigned to the following yet
	// --------------------------------------------------
	public static boolean CHECK_KNOWN;
	
	/**
	 * This class initializes all global variables for configuration.<br>
	 * If the key doesn't appear in properties file, a default value is set by this class. {@link #CONFIGURATION_FILE} (properties file) for configuring your server.
	 */
	public static void load()
	{
		// Load L2JMod L2Properties file (if exists)
		final PropertiesParser L2JModSettings = new PropertiesParser(L2JMOD_CONFIG_FILE);
		
		TVT_EVENT_ENABLED = L2JModSettings.getBoolean("TvTEventEnabled", false);
		TVT_EVENT_IN_INSTANCE = L2JModSettings.getBoolean("TvTEventInInstance", false);
		TVT_EVENT_INSTANCE_FILE = L2JModSettings.getString("TvTEventInstanceFile", "coliseum.xml");
		TVT_EVENT_INTERVAL = L2JModSettings.getString("TvTEventInterval", "20:00").split(",");
		TVT_EVENT_PARTICIPATION_TIME = L2JModSettings.getInt("TvTEventParticipationTime", 3600);
		TVT_EVENT_RUNNING_TIME = L2JModSettings.getInt("TvTEventRunningTime", 1800);
		TVT_EVENT_PARTICIPATION_NPC_ID = L2JModSettings.getInt("TvTEventParticipationNpcId", 0);
		
		if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			TVT_EVENT_ENABLED = false;
			LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
		}
		else
		{
			String[] tvtNpcCoords = L2JModSettings.getString("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (tvtNpcCoords.length < 3)
			{
				TVT_EVENT_ENABLED = false;
				LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
			}
			else
			{
				TVT_EVENT_REWARDS = new ArrayList<>();
				TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
				TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
				TVT_EVENT_TEAM_1_COORDINATES = new int[3];
				TVT_EVENT_TEAM_2_COORDINATES = new int[3];
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
				TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
				if (tvtNpcCoords.length == 4)
				{
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(tvtNpcCoords[3]);
				}
				TVT_EVENT_MIN_PLAYERS_IN_TEAMS = L2JModSettings.getInt("TvTEventMinPlayersInTeams", 1);
				TVT_EVENT_MAX_PLAYERS_IN_TEAMS = L2JModSettings.getInt("TvTEventMaxPlayersInTeams", 20);
				TVT_EVENT_MIN_LVL = L2JModSettings.getByte("TvTEventMinPlayerLevel", (byte) 1);
				TVT_EVENT_MAX_LVL = L2JModSettings.getByte("TvTEventMaxPlayerLevel", (byte) 80);
				TVT_EVENT_RESPAWN_TELEPORT_DELAY = L2JModSettings.getInt("TvTEventRespawnTeleportDelay", 20);
				TVT_EVENT_START_LEAVE_TELEPORT_DELAY = L2JModSettings.getInt("TvTEventStartLeaveTeleportDelay", 20);
				TVT_EVENT_EFFECTS_REMOVAL = L2JModSettings.getInt("TvTEventEffectsRemoval", 0);
				TVT_EVENT_MAX_PARTICIPANTS_PER_IP = L2JModSettings.getInt("TvTEventMaxParticipantsPerIP", 0);
				TVT_ALLOW_VOICED_COMMAND = L2JModSettings.getBoolean("TvTAllowVoicedInfoCommand", false);
				TVT_EVENT_TEAM_1_NAME = L2JModSettings.getString("TvTEventTeam1Name", "Team1");
				tvtNpcCoords = L2JModSettings.getString("TvTEventTeam1Coordinates", "0,0,0").split(",");
				if (tvtNpcCoords.length < 3)
				{
					TVT_EVENT_ENABLED = false;
					LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
				}
				else
				{
					TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
					TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
					TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
					TVT_EVENT_TEAM_2_NAME = L2JModSettings.getString("TvTEventTeam2Name", "Team2");
					tvtNpcCoords = L2JModSettings.getString("TvTEventTeam2Coordinates", "0,0,0").split(",");
					if (tvtNpcCoords.length < 3)
					{
						TVT_EVENT_ENABLED = false;
						LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
					}
					else
					{
						TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
						TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
						TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
						tvtNpcCoords = L2JModSettings.getString("TvTEventParticipationFee", "0,0").split(",");
						try
						{
							TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(tvtNpcCoords[0]);
							TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(tvtNpcCoords[1]);
						}
						catch (NumberFormatException nfe)
						{
							if (tvtNpcCoords.length > 0)
							{
								LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
							}
						}
						tvtNpcCoords = L2JModSettings.getString("TvTEventReward", "57,100000").split(";");
						for (String reward : tvtNpcCoords)
						{
							String[] rewardSplit = reward.split(",");
							if (rewardSplit.length != 2)
							{
								LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward {}", reward);
							}
							else
							{
								try
								{
									TVT_EVENT_REWARDS.add(new int[]
									{
										Integer.parseInt(rewardSplit[0]),
										Integer.parseInt(rewardSplit[1])
									});
								}
								catch (NumberFormatException nfe)
								{
									if (!reward.isEmpty())
									{
										LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward {}", reward);
									}
								}
							}
						}
						
						TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = L2JModSettings.getBoolean("TvTEventTargetTeamMembersAllowed", true);
						TVT_EVENT_SCROLL_ALLOWED = L2JModSettings.getBoolean("TvTEventScrollsAllowed", false);
						TVT_EVENT_POTIONS_ALLOWED = L2JModSettings.getBoolean("TvTEventPotionsAllowed", false);
						TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = L2JModSettings.getBoolean("TvTEventSummonByItemAllowed", false);
						TVT_REWARD_TEAM_TIE = L2JModSettings.getBoolean("TvTRewardTeamTie", false);
						tvtNpcCoords = L2JModSettings.getString("TvTDoorsToOpen", "").split(";");
						for (String door : tvtNpcCoords)
						{
							try
							{
								TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen {}", door);
								}
							}
						}
						
						tvtNpcCoords = L2JModSettings.getString("TvTDoorsToClose", "").split(";");
						for (String door : tvtNpcCoords)
						{
							try
							{
								TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose {}", door);
								}
							}
						}
						
						tvtNpcCoords = L2JModSettings.getString("TvTEventFighterBuffs", "").split(";");
						if (!tvtNpcCoords[0].isEmpty())
						{
							TVT_EVENT_FIGHTER_BUFFS = new HashMap<>(tvtNpcCoords.length);
							for (String skill : tvtNpcCoords)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs {}", skill);
								}
								else
								{
									try
									{
										TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs {}", skill);
										}
									}
								}
							}
						}
						
						tvtNpcCoords = L2JModSettings.getString("TvTEventMageBuffs", "").split(";");
						if (!tvtNpcCoords[0].isEmpty())
						{
							TVT_EVENT_MAGE_BUFFS = new HashMap<>(tvtNpcCoords.length);
							for (String skill : tvtNpcCoords)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs {}", skill);
								}
								else
								{
									try
									{
										TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											LOG.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs {}", skill);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
