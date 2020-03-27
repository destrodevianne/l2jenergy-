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
package com.l2jserver.gameserver.enums.actors;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.interfaces.IIdentifiable;

public enum ClassId implements IIdentifiable
{
	HUMAN_FIGHTER(0x00, false, Race.HUMAN, null),
	WARRIOR(0x01, false, Race.HUMAN, HUMAN_FIGHTER),
	GLADIATOR(0x02, false, Race.HUMAN, WARRIOR),
	WARLORD(0x03, false, Race.HUMAN, WARRIOR),
	KNIGHT(0x04, false, Race.HUMAN, HUMAN_FIGHTER),
	PALADIN(0x05, false, Race.HUMAN, KNIGHT),
	DARK_AVENGER(0x06, false, Race.HUMAN, KNIGHT),
	ROGUE(0x07, false, Race.HUMAN, HUMAN_FIGHTER),
	TREASURE_HUNTER(0x08, false, Race.HUMAN, ROGUE),
	HAWKEYE(0x09, false, Race.HUMAN, ROGUE),
	
	HUMAN_MYSTIC(0x0a, true, Race.HUMAN, null),
	HUMAN_WIZARD(0x0b, true, Race.HUMAN, HUMAN_MYSTIC),
	SORCERER(0x0c, true, Race.HUMAN, HUMAN_WIZARD),
	NECROMANCER(0x0d, true, Race.HUMAN, HUMAN_WIZARD),
	WARLOCK(0x0e, true, true, Race.HUMAN, HUMAN_WIZARD),
	CLERIC(0x0f, true, Race.HUMAN, HUMAN_MYSTIC),
	BISHOP(0x10, true, Race.HUMAN, CLERIC),
	PROPHET(0x11, true, Race.HUMAN, CLERIC),
	
	ELVEN_FIGHTER(0x12, false, Race.ELF, null),
	ELVEN_KNIGHT(0x13, false, Race.ELF, ELVEN_FIGHTER),
	TEMPLE_KNIGHT(0x14, false, Race.ELF, ELVEN_KNIGHT),
	SWORD_SINGER(0x15, false, Race.ELF, ELVEN_KNIGHT),
	ELVEN_SCOUT(0x16, false, Race.ELF, ELVEN_FIGHTER),
	PLAINS_WALKER(0x17, false, Race.ELF, ELVEN_SCOUT),
	SILVER_RANGER(0x18, false, Race.ELF, ELVEN_SCOUT),
	
	ELVEN_MYSTIC(0x19, true, Race.ELF, null),
	ELVEN_WIZARD(0x1a, true, Race.ELF, ELVEN_MYSTIC),
	SPELLSINGER(0x1b, true, Race.ELF, ELVEN_WIZARD),
	ELEMENTAL_SUMMONER(0x1c, true, true, Race.ELF, ELVEN_WIZARD),
	ELVEN_ORACLE(0x1d, true, Race.ELF, ELVEN_MYSTIC),
	ELVEN_ELDER(0x1e, true, Race.ELF, ELVEN_ORACLE),
	
	DARK_FIGHTER(0x1f, false, Race.DARK_ELF, null),
	PALUS_KNIGHT(0x20, false, Race.DARK_ELF, DARK_FIGHTER),
	SHILLIEN_KNIGHT(0x21, false, Race.DARK_ELF, PALUS_KNIGHT),
	BLADEDANCER(0x22, false, Race.DARK_ELF, PALUS_KNIGHT),
	ASSASSIN(0x23, false, Race.DARK_ELF, DARK_FIGHTER),
	ABYSS_WALKER(0x24, false, Race.DARK_ELF, ASSASSIN),
	PHANTOM_RANGER(0x25, false, Race.DARK_ELF, ASSASSIN),
	
	DARK_MYSTIC(0x26, true, Race.DARK_ELF, null),
	DARK_WIZARD(0x27, true, Race.DARK_ELF, DARK_MYSTIC),
	SPELLHOWLER(0x28, true, Race.DARK_ELF, DARK_WIZARD),
	PHANTOM_SUMMONER(0x29, true, true, Race.DARK_ELF, DARK_WIZARD),
	SHILLIEN_ORACLE(0x2a, true, Race.DARK_ELF, DARK_MYSTIC),
	SHILLIEN_ELDER(0x2b, true, Race.DARK_ELF, SHILLIEN_ORACLE),
	
	ORC_FIGHTER(0x2c, false, Race.ORC, null),
	ORC_RAIDER(0x2d, false, Race.ORC, ORC_FIGHTER),
	DESTROYER(0x2e, false, Race.ORC, ORC_RAIDER),
	MONK(0x2f, false, Race.ORC, ORC_FIGHTER),
	TYRANT(0x30, false, Race.ORC, MONK),
	
	ORC_MYSTIC(0x31, true, Race.ORC, null),
	ORC_SHAMAN(0x32, true, Race.ORC, ORC_MYSTIC),
	OVERLORD(0x33, true, Race.ORC, ORC_SHAMAN),
	WARCRYER(0x34, true, Race.ORC, ORC_SHAMAN),
	
	DWARVEN_FIGHTER(0x35, false, Race.DWARF, null),
	SCAVENGER(0x36, false, Race.DWARF, DWARVEN_FIGHTER),
	BOUNTY_HUNTER(0x37, false, Race.DWARF, SCAVENGER),
	ARTISAN(0x38, false, Race.DWARF, DWARVEN_FIGHTER),
	WARSMITH(0x39, false, Race.DWARF, ARTISAN),
	
	/*
	 * Dummy Entries (id's already in decimal format) btw FU NCSoft for the amount of work you put me through to do this!! <START>
	 */
	DUMMY_1(58, false, null, null),
	DUMMY_2(59, false, null, null),
	DUMMY_3(60, false, null, null),
	DUMMY_4(61, false, null, null),
	DUMMY_5(62, false, null, null),
	DUMMY_6(63, false, null, null),
	DUMMY_7(64, false, null, null),
	DUMMY_8(65, false, null, null),
	DUMMY_9(66, false, null, null),
	DUMMY_10(67, false, null, null),
	DUMMY_11(68, false, null, null),
	DUMMY_12(69, false, null, null),
	DUMMY_13(70, false, null, null),
	DUMMY_14(71, false, null, null),
	DUMMY_15(72, false, null, null),
	DUMMY_16(73, false, null, null),
	DUMMY_17(74, false, null, null),
	DUMMY_18(75, false, null, null),
	DUMMY_19(76, false, null, null),
	DUMMY_20(77, false, null, null),
	DUMMY_21(78, false, null, null),
	DUMMY_22(79, false, null, null),
	DUMMY_23(80, false, null, null),
	DUMMY_24(81, false, null, null),
	DUMMY_25(82, false, null, null),
	DUMMY_26(83, false, null, null),
	DUMMY_27(84, false, null, null),
	DUMMY_28(85, false, null, null),
	DUMMY_29(86, false, null, null),
	DUMMY_30(87, false, null, null),
	
	DUELIST(0x58, false, Race.HUMAN, GLADIATOR),
	DREADNOUGHT(0x59, false, Race.HUMAN, WARLORD),
	PHOENIX_KNIGHT(0x5a, false, Race.HUMAN, PALADIN),
	HELL_KNIGHT(0x5b, false, Race.HUMAN, DARK_AVENGER),
	SAGGITARIUS(0x5c, false, Race.HUMAN, HAWKEYE),
	ADVENTURER(0x5d, false, Race.HUMAN, TREASURE_HUNTER),
	ARCHMAGE(0x5e, true, Race.HUMAN, SORCERER),
	SOULTAKER(0x5f, true, Race.HUMAN, NECROMANCER),
	ARCANA_LORD(0x60, true, true, Race.HUMAN, WARLOCK),
	CARDINAL(0x61, true, Race.HUMAN, BISHOP),
	HIEROPHANT(0x62, true, Race.HUMAN, PROPHET),
	
	EVAS_TEMPLAR(0x63, false, Race.ELF, TEMPLE_KNIGHT),
	SWORD_MUSE(0x64, false, Race.ELF, SWORD_SINGER),
	WIND_RIDER(0x65, false, Race.ELF, PLAINS_WALKER),
	MOONLIGHT_SENTINEL(0x66, false, Race.ELF, SILVER_RANGER),
	MYSTIC_MUSE(0x67, true, Race.ELF, SPELLSINGER),
	ELEMENTAL_MASTER(0x68, true, true, Race.ELF, ELEMENTAL_SUMMONER),
	EVAS_SAINT(0x69, true, Race.ELF, ELVEN_ELDER),
	
	SHILLIEN_TEMPLAR(0x6a, false, Race.DARK_ELF, SHILLIEN_KNIGHT),
	SPECTRAL_DANCER(0x6b, false, Race.DARK_ELF, BLADEDANCER),
	GHOST_HUNTER(0x6c, false, Race.DARK_ELF, ABYSS_WALKER),
	GHOST_SENTINEL(0x6d, false, Race.DARK_ELF, PHANTOM_RANGER),
	STORM_SCREAMER(0x6e, true, Race.DARK_ELF, SPELLHOWLER),
	SPECTRAL_MASTER(0x6f, true, true, Race.DARK_ELF, PHANTOM_SUMMONER),
	SHILLIEN_SAINT(0x70, true, Race.DARK_ELF, SHILLIEN_ELDER),
	
	TITAN(0x71, false, Race.ORC, DESTROYER),
	GRAND_KHAVATARI(0x72, false, Race.ORC, TYRANT),
	DOMINATOR(0x73, true, Race.ORC, OVERLORD),
	DOOMCRYER(0x74, true, Race.ORC, WARCRYER),
	
	FORTUNE_SEEKER(0x75, false, Race.DWARF, BOUNTY_HUNTER),
	MAESTRO(0x76, false, Race.DWARF, WARSMITH),
	
	DUMMY_31(0x77, false, null, null),
	DUMMY_32(0x78, false, null, null),
	DUMMY_33(0x79, false, null, null),
	DUMMY_34(0x7a, false, null, null),
	
	MALE_SOLDIER(0x7b, false, Race.KAMAEL, null),
	TROOPER(0x7D, false, Race.KAMAEL, MALE_SOLDIER),
	BERSERKER(0x7F, false, Race.KAMAEL, TROOPER),
	MALE_SOULBREAKER(0x80, false, Race.KAMAEL, TROOPER),
	
	FEMALE_SOLDIER(0x7C, false, Race.KAMAEL, null),
	WARDER(0x7E, false, Race.KAMAEL, FEMALE_SOLDIER),
	FEMALE_SOULBREAKER(0x81, false, Race.KAMAEL, WARDER),
	ARBALESTER(0x82, false, Race.KAMAEL, WARDER),
	
	DOOMBRINGER(0x83, false, Race.KAMAEL, BERSERKER),
	MALE_SOULHOUND(0x84, false, Race.KAMAEL, MALE_SOULBREAKER),
	FEMALE_SOULHOUND(0x85, false, Race.KAMAEL, FEMALE_SOULBREAKER),
	TRICKSTER(0x86, false, Race.KAMAEL, ARBALESTER),
	INSPECTOR(0x87, false, Race.KAMAEL, WARDER), // DS: yes, both male/female inspectors use skills from warder
	JUDICATOR(0x88, false, Race.KAMAEL, INSPECTOR);
	
	/** The Identifier of the Class */
	private final int _id;
	
	/** True if the class is a mage class */
	private final boolean _isMage;
	
	/** True if the class is a summoner class */
	private final boolean _isSummoner;
	
	/** The Race object of the class */
	private final Race _race;
	
	/** The parent ClassId or null if this class is a root */
	private final ClassId _parent;
	
	/**
	 * Class constructor.
	 * @param pId the class Id.
	 * @param pIsMage {code true} if the class is mage class.
	 * @param race the race related to the class.
	 * @param pParent the parent class Id.
	 */
	private ClassId(int pId, boolean pIsMage, Race race, ClassId pParent)
	{
		_id = pId;
		_isMage = pIsMage;
		_isSummoner = false;
		_race = race;
		_parent = pParent;
	}
	
	/**
	 * Class constructor.
	 * @param pId the class Id.
	 * @param pIsMage {code true} if the class is mage class.
	 * @param pIsSummoner {code true} if the class is summoner class.
	 * @param race the race related to the class.
	 * @param pParent the parent class Id.
	 */
	private ClassId(int pId, boolean pIsMage, boolean pIsSummoner, Race race, ClassId pParent)
	{
		_id = pId;
		_isMage = pIsMage;
		_isSummoner = pIsSummoner;
		_race = race;
		_parent = pParent;
	}
	
	/**
	 * Gets the ID of the class.
	 * @return the ID of the class
	 */
	@Override
	public final int getId()
	{
		return _id;
	}
	
	/**
	 * @return {code true} if the class is a mage class.
	 */
	public final boolean isMage()
	{
		return _isMage;
	}
	
	/**
	 * @return {code true} if the class is a summoner class.
	 */
	public final boolean isSummoner()
	{
		return _isSummoner;
	}
	
	/**
	 * @return the Race object of the class.
	 */
	public final Race getRace()
	{
		return _race;
	}
	
	/**
	 * @param cid the parent ClassId to check.
	 * @return {code true} if this Class is a child of the selected ClassId.
	 */
	public final boolean childOf(ClassId cid)
	{
		if (_parent == null)
		{
			return false;
		}
		
		if (_parent == cid)
		{
			return true;
		}
		
		return _parent.childOf(cid);
		
	}
	
	/**
	 * @param cid the parent ClassId to check.
	 * @return {code true} if this Class is equal to the selected ClassId or a child of the selected ClassId.
	 */
	public final boolean equalsOrChildOf(ClassId cid)
	{
		return (this == cid) || childOf(cid);
	}
	
	/**
	 * @return the child level of this Class (0=root, 1=child leve 1...)
	 */
	public final int level()
	{
		if (_parent == null)
		{
			return 0;
		}
		
		return 1 + _parent.level();
	}
	
	/**
	 * @return its parent Class Id
	 */
	public final ClassId getParent()
	{
		return _parent;
	}
	
	public static ClassId getClassId(int cId)
	{
		try
		{
			return ClassId.values()[cId];
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
