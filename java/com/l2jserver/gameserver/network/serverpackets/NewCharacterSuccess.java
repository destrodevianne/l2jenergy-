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
import com.l2jserver.gameserver.model.actor.templates.L2PcTemplate;
import com.l2jserver.gameserver.model.base.ClassId;

public final class NewCharacterSuccess extends L2GameServerPacket
{
	private final List<L2PcTemplate> _templates = new ArrayList<>();
	
	public static final NewCharacterSuccess STATIC_PACKET = new NewCharacterSuccess();
	
	private NewCharacterSuccess()
	{
		_templates.add(PlayerTemplateData.getInstance().getTemplate(0));
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.fighter)); // Human Figther
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.mage)); // Human Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.elvenFighter)); // Elven Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.elvenMage)); // Elven Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.darkFighter)); // Dark Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.darkMage)); // Dark Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.orcFighter)); // Orc Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.orcMage)); // Orc Mystic
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.dwarvenFighter)); // Dwarf Fighter
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.maleSoldier)); // Male Kamael Soldier
		_templates.add(PlayerTemplateData.getInstance().getTemplate(ClassId.femaleSoldier)); // Female Kamael Soldier
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
