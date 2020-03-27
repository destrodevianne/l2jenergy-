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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.data.xml.impl.PlayerTemplateData;
import com.l2jserver.gameserver.enums.actors.ClassId;
import com.l2jserver.gameserver.model.actor.templates.L2PcTemplate;

public final class NewCharacterSuccess extends L2GameServerPacket
{
	private final List<L2PcTemplate> _templates = new ArrayList<>();
	
	public static final NewCharacterSuccess STATIC_PACKET = new NewCharacterSuccess();
	
	private NewCharacterSuccess()
	{
		_templates.add(PlayerTemplateData.getInstance().getTemplate(0));
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.HUMAN_FIGHTER)); // Human Figther
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.HUMAN_MYSTIC)); // Human Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.ELVEN_FIGHTER)); // Elven Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.ELVEN_MYSTIC)); // Elven Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.DARK_FIGHTER)); // Dark Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.DARK_MYSTIC)); // Dark Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.ORC_FIGHTER)); // Orc Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.ORC_MYSTIC)); // Orc Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.DWARVEN_FIGHTER)); // Dwarf Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.MALE_SOLDIER)); // Male Kamael Soldier
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.FEMALE_SOLDIER)); // Female Kamael Soldier
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x0D);
		writeD(_templates.size());
		
		for (L2PcTemplate template : _templates)
		{
			if (template == null)
			{
				continue;
			}
			
			// TODO: Unhardcode these
			writeD(template.getRace().ordinal());
			writeD(template.getClassId().getId());
			writeD(0x46);
			writeD(template.getBaseSTR());
			writeD(0x0A);
			writeD(0x46);
			writeD(template.getBaseDEX());
			writeD(0x0A);
			writeD(0x46);
			writeD(template.getBaseCON());
			writeD(0x0A);
			writeD(0x46);
			writeD(template.getBaseINT());
			writeD(0x0A);
			writeD(0x46);
			writeD(template.getBaseWIT());
			writeD(0x0A);
			writeD(0x46);
			writeD(template.getBaseMEN());
			writeD(0x0A);
		}
	}
}
