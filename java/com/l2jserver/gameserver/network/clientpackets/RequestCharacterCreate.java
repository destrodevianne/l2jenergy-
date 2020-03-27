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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.configuration.config.ServerConfig;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.xml.impl.InitialEquipmentData;
import com.l2jserver.gameserver.data.xml.impl.InitialShortcutData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.PlayerCreationPointData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.gameserver.enums.actors.ClassId;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.stat.PcStat;
import com.l2jserver.gameserver.model.actor.templates.L2PcTemplate;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerCreate;
import com.l2jserver.gameserver.model.items.PcItemTemplate;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.CharacterCreateFail;
import com.l2jserver.gameserver.network.serverpackets.CharacterCreateSuccess;
import com.l2jserver.gameserver.network.serverpackets.CharacterSelectionInfo;

@SuppressWarnings("unused")
public final class RequestCharacterCreate extends L2GameClientPacket
{
	private static final String _C__0C_REQUESTCHARACTERCREATE = "[C] 0C RequestCharacterCreate";
	private static final int PLAYER_NAME_MAX_LENGHT = 16;
	
	private static final Logger LOG = LoggerFactory.getLogger("accounting");
	
	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_race = readD();
		_sex = (byte) readD();
		_classId = readD();
		_int = readD();
		_str = readD();
		_con = readD();
		_men = readD();
		_dex = readD();
		_wit = readD();
		_hairStyle = (byte) readD();
		_hairColor = (byte) readD();
		_face = (byte) readD();
	}
	
	@Override
	protected void runImpl()
	{
		if ((_name.length() < 1) || (_name.length() > 16))
		{
			sendPacket(CharacterCreateFail.REASON_16_ENG_CHARS);
			return;
		}
		
		if (CharacterConfig.FORBIDDEN_NAMES.contains(_name.toLowerCase()))
		{
			sendPacket(CharacterCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if (!isValidName(_name))
		{
			sendPacket(CharacterCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if (_name.isEmpty() || (_name.length() > PLAYER_NAME_MAX_LENGHT))
		{
			sendPacket(CharacterCreateFail.REASON_16_ENG_CHARS);
			return;
		}
		
		// Your name is already taken by a NPC.
		if (NpcData.getInstance().getTemplateByName(_name) != null)
		{
			sendPacket(CharacterCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if ((_face > 2) || (_face < 0))
		{
			LOG.warn("Character Creation Failure: Character face {} is invalid. Possible client hack. {}", _face, getClient());
			sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if ((_hairStyle < 0) || ((_sex == 0) && (_hairStyle > 4)) || ((_sex != 0) && (_hairStyle > 6)))
		{
			LOG.warn("Character Creation Failure: Character hair style {} is invalid. Possible client hack. {}", _hairStyle, getClient());
			sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if ((_hairColor > 3) || (_hairColor < 0))
		{
			LOG.warn("Character Creation Failure: Character hair color {} is invalid. Possible client hack. {}", _hairColor, getClient());
			sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		// You already have the maximum amount of characters for this account.
		if ((CharNameTable.getInstance().getAccountCharacterCount(getClient().getAccountName()) >= ServerConfig.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) && (ServerConfig.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0))
		{
			sendPacket(CharacterCreateFail.REASON_TOO_MANY_CHARACTERS);
			return;
		}
		
		// The name already exists.
		if (CharNameTable.getInstance().doesCharNameExist(_name))
		{
			sendPacket(CharacterCreateFail.REASON_NAME_ALREADY_EXISTS);
			return;
		}
		
		// The class id related to this template is post-newbie.
		if (ClassId.getClassId(_classId).level() > 0)
		{
			LOG.warn("Character Creation Failure: {} classId: {}", _name, _classId);
			
			sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		// Create the player Object.
		final L2PcInstance player = L2PcInstance.create(_classId, getClient().getAccountName(), _name, new PcAppearance(_face, _hairColor, _hairStyle, Sex.values()[_sex]));
		if (player == null)
		{
			sendPacket(CharacterCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		// HP and MP are at maximum and CP is zero by default.
		player.setCurrentCp(0);
		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
		
		sendPacket(CharacterCreateSuccess.STATIC_PACKET);
		
		initNewChar(getClient(), player);
		
		LOG.info("Created new character {} {}.", player, getClient());
	}
	
	private boolean isValidName(String text)
	{
		return ServerConfig.PLAYER_NAME_TEMPLATE.matcher(text).matches();
	}
	
	private void initNewChar(L2GameClient client, L2PcInstance player)
	{
		L2World.getInstance().storeObject(player);
		
		if (CharacterConfig.STARTING_ADENA > 0)
		{
			player.addAdena("Init", CharacterConfig.STARTING_ADENA, null, false);
		}
		
		final L2PcTemplate template = player.getTemplate();
		Location createLoc = PlayerCreationPointData.getInstance().getCreationPoint(template.getClassId());
		player.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
		player.setTitle("");
		
		if (CharacterConfig.ENABLE_VITALITY)
		{
			player.setVitalityPoints(Math.min(CharacterConfig.STARTING_VITALITY_POINTS, PcStat.MAX_VITALITY_POINTS), true);
		}
		if (CharacterConfig.STARTING_LEVEL > 1)
		{
			player.addLevel(CharacterConfig.STARTING_LEVEL - 1);
		}
		if (CharacterConfig.STARTING_SP > 0)
		{
			player.addSp(CharacterConfig.STARTING_SP);
		}
		
		final List<PcItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(player.getClassId());
		if (initialItems != null)
		{
			for (PcItemTemplate ie : initialItems)
			{
				final L2ItemInstance item = player.getInventory().addItem("Init", ie.getId(), ie.getCount(), player, null);
				if (item == null)
				{
					LOG.warn("Could not create item during char creation: itemId {}, amount {}.", ie.getId(), ie.getCount());
					continue;
				}
				
				if (item.isEquipable() && ie.isEquipped())
				{
					player.getInventory().equipItem(item);
				}
			}
		}
		
		for (L2SkillLearn skill : SkillTreesData.getInstance().getAvailableSkills(player, player.getClassId(), false, true))
		{
			player.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
		}
		
		// Register all shortcuts for actions, skills and items for this new character.
		InitialShortcutData.getInstance().registerAllShortcuts(player);
		
		EventDispatcher.getInstance().notifyEvent(new OnPlayerCreate(player, player.getObjectId(), player.getName(), client), Containers.Players());
		
		player.setOnlineStatus(true, false);
		player.deleteMe();
		
		final CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1);
		sendPacket(csi);
		client.setCharSelection(csi.getCharInfo());
	}
	
	@Override
	public String getType()
	{
		return _C__0C_REQUESTCHARACTERCREATE;
	}
}
