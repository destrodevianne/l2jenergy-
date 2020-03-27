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

import static com.l2jserver.gameserver.enums.actors.ClassLevel.FIRST;
import static com.l2jserver.gameserver.enums.actors.ClassLevel.FOURTH;
import static com.l2jserver.gameserver.enums.actors.ClassLevel.SECOND;
import static com.l2jserver.gameserver.enums.actors.ClassLevel.THIRD;
import static com.l2jserver.gameserver.enums.actors.ClassType.FIGHTER;
import static com.l2jserver.gameserver.enums.actors.ClassType.MYSTIC;
import static com.l2jserver.gameserver.enums.actors.ClassType.PRIEST;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author luisantonioa
 */
public enum PlayerClass
{
	HumanFighter(Race.HUMAN, FIGHTER, FIRST),
	Warrior(Race.HUMAN, FIGHTER, SECOND),
	Gladiator(Race.HUMAN, FIGHTER, THIRD),
	Warlord(Race.HUMAN, FIGHTER, THIRD),
	HumanKnight(Race.HUMAN, FIGHTER, SECOND),
	Paladin(Race.HUMAN, FIGHTER, THIRD),
	DarkAvenger(Race.HUMAN, FIGHTER, THIRD),
	Rogue(Race.HUMAN, FIGHTER, SECOND),
	TreasureHunter(Race.HUMAN, FIGHTER, THIRD),
	Hawkeye(Race.HUMAN, FIGHTER, THIRD),
	HumanMystic(Race.HUMAN, MYSTIC, FIRST),
	HumanWizard(Race.HUMAN, MYSTIC, SECOND),
	Sorceror(Race.HUMAN, MYSTIC, THIRD),
	Necromancer(Race.HUMAN, MYSTIC, THIRD),
	Warlock(Race.HUMAN, MYSTIC, THIRD),
	Cleric(Race.HUMAN, PRIEST, SECOND),
	Bishop(Race.HUMAN, PRIEST, THIRD),
	Prophet(Race.HUMAN, PRIEST, THIRD),
	
	ElvenFighter(Race.ELF, FIGHTER, FIRST),
	ElvenKnight(Race.ELF, FIGHTER, SECOND),
	TempleKnight(Race.ELF, FIGHTER, THIRD),
	Swordsinger(Race.ELF, FIGHTER, THIRD),
	ElvenScout(Race.ELF, FIGHTER, SECOND),
	Plainswalker(Race.ELF, FIGHTER, THIRD),
	SilverRanger(Race.ELF, FIGHTER, THIRD),
	ElvenMystic(Race.ELF, MYSTIC, FIRST),
	ElvenWizard(Race.ELF, MYSTIC, SECOND),
	Spellsinger(Race.ELF, MYSTIC, THIRD),
	ElementalSummoner(Race.ELF, MYSTIC, THIRD),
	ElvenOracle(Race.ELF, PRIEST, SECOND),
	ElvenElder(Race.ELF, PRIEST, THIRD),
	
	DarkElvenFighter(Race.DARK_ELF, FIGHTER, FIRST),
	PalusKnight(Race.DARK_ELF, FIGHTER, SECOND),
	ShillienKnight(Race.DARK_ELF, FIGHTER, THIRD),
	Bladedancer(Race.DARK_ELF, FIGHTER, THIRD),
	Assassin(Race.DARK_ELF, FIGHTER, SECOND),
	AbyssWalker(Race.DARK_ELF, FIGHTER, THIRD),
	PhantomRanger(Race.DARK_ELF, FIGHTER, THIRD),
	DarkElvenMystic(Race.DARK_ELF, MYSTIC, FIRST),
	DarkElvenWizard(Race.DARK_ELF, MYSTIC, SECOND),
	Spellhowler(Race.DARK_ELF, MYSTIC, THIRD),
	PhantomSummoner(Race.DARK_ELF, MYSTIC, THIRD),
	ShillienOracle(Race.DARK_ELF, PRIEST, SECOND),
	ShillienElder(Race.DARK_ELF, PRIEST, THIRD),
	
	OrcFighter(Race.ORC, FIGHTER, FIRST),
	OrcRaider(Race.ORC, FIGHTER, SECOND),
	Destroyer(Race.ORC, FIGHTER, THIRD),
	OrcMonk(Race.ORC, FIGHTER, SECOND),
	Tyrant(Race.ORC, FIGHTER, THIRD),
	OrcMystic(Race.ORC, MYSTIC, FIRST),
	OrcShaman(Race.ORC, MYSTIC, SECOND),
	Overlord(Race.ORC, MYSTIC, THIRD),
	Warcryer(Race.ORC, MYSTIC, THIRD),
	
	DwarvenFighter(Race.DWARF, FIGHTER, FIRST),
	DwarvenScavenger(Race.DWARF, FIGHTER, SECOND),
	BountyHunter(Race.DWARF, FIGHTER, THIRD),
	DwarvenArtisan(Race.DWARF, FIGHTER, SECOND),
	Warsmith(Race.DWARF, FIGHTER, THIRD),
	
	dummyEntry1(null, null, null),
	dummyEntry2(null, null, null),
	dummyEntry3(null, null, null),
	dummyEntry4(null, null, null),
	dummyEntry5(null, null, null),
	dummyEntry6(null, null, null),
	dummyEntry7(null, null, null),
	dummyEntry8(null, null, null),
	dummyEntry9(null, null, null),
	dummyEntry10(null, null, null),
	dummyEntry11(null, null, null),
	dummyEntry12(null, null, null),
	dummyEntry13(null, null, null),
	dummyEntry14(null, null, null),
	dummyEntry15(null, null, null),
	dummyEntry16(null, null, null),
	dummyEntry17(null, null, null),
	dummyEntry18(null, null, null),
	dummyEntry19(null, null, null),
	dummyEntry20(null, null, null),
	dummyEntry21(null, null, null),
	dummyEntry22(null, null, null),
	dummyEntry23(null, null, null),
	dummyEntry24(null, null, null),
	dummyEntry25(null, null, null),
	dummyEntry26(null, null, null),
	dummyEntry27(null, null, null),
	dummyEntry28(null, null, null),
	dummyEntry29(null, null, null),
	dummyEntry30(null, null, null),
	/*
	 * (3rd classes)
	 */
	duelist(Race.HUMAN, FIGHTER, FOURTH),
	dreadnought(Race.HUMAN, FIGHTER, FOURTH),
	phoenixKnight(Race.HUMAN, FIGHTER, FOURTH),
	hellKnight(Race.HUMAN, FIGHTER, FOURTH),
	sagittarius(Race.HUMAN, FIGHTER, FOURTH),
	adventurer(Race.HUMAN, FIGHTER, FOURTH),
	archmage(Race.HUMAN, MYSTIC, FOURTH),
	soultaker(Race.HUMAN, MYSTIC, FOURTH),
	arcanaLord(Race.HUMAN, MYSTIC, FOURTH),
	cardinal(Race.HUMAN, PRIEST, FOURTH),
	hierophant(Race.HUMAN, PRIEST, FOURTH),
	
	evaTemplar(Race.ELF, FIGHTER, FOURTH),
	swordMuse(Race.ELF, FIGHTER, FOURTH),
	windRider(Race.ELF, FIGHTER, FOURTH),
	moonlightSentinel(Race.ELF, FIGHTER, FOURTH),
	mysticMuse(Race.ELF, MYSTIC, FOURTH),
	elementalMaster(Race.ELF, MYSTIC, FOURTH),
	evaSaint(Race.ELF, PRIEST, FOURTH),
	
	shillienTemplar(Race.DARK_ELF, FIGHTER, FOURTH),
	spectralDancer(Race.DARK_ELF, FIGHTER, FOURTH),
	ghostHunter(Race.DARK_ELF, FIGHTER, FOURTH),
	ghostSentinel(Race.DARK_ELF, FIGHTER, FOURTH),
	stormScreamer(Race.DARK_ELF, MYSTIC, FOURTH),
	spectralMaster(Race.DARK_ELF, MYSTIC, FOURTH),
	shillienSaint(Race.DARK_ELF, PRIEST, FOURTH),
	
	titan(Race.ORC, FIGHTER, FOURTH),
	grandKhavatari(Race.ORC, FIGHTER, FOURTH),
	dominator(Race.ORC, MYSTIC, FOURTH),
	doomcryer(Race.ORC, MYSTIC, FOURTH),
	
	fortuneSeeker(Race.DWARF, FIGHTER, FOURTH),
	maestro(Race.DWARF, FIGHTER, FOURTH),
	
	dummyEntry31(null, null, null),
	dummyEntry32(null, null, null),
	dummyEntry33(null, null, null),
	dummyEntry34(null, null, null),
	
	maleSoldier(Race.KAMAEL, FIGHTER, FIRST),
	femaleSoldier(Race.KAMAEL, FIGHTER, FIRST),
	trooper(Race.KAMAEL, FIGHTER, SECOND),
	warder(Race.KAMAEL, FIGHTER, SECOND),
	berserker(Race.KAMAEL, FIGHTER, THIRD),
	maleSoulbreaker(Race.KAMAEL, FIGHTER, THIRD),
	femaleSoulbreaker(Race.KAMAEL, FIGHTER, THIRD),
	arbalester(Race.KAMAEL, FIGHTER, THIRD),
	doombringer(Race.KAMAEL, FIGHTER, FOURTH),
	maleSoulhound(Race.KAMAEL, FIGHTER, FOURTH),
	femaleSoulhound(Race.KAMAEL, FIGHTER, FOURTH),
	trickster(Race.KAMAEL, FIGHTER, FOURTH),
	inspector(Race.KAMAEL, FIGHTER, THIRD),
	judicator(Race.KAMAEL, FIGHTER, FOURTH);
	
	private Race _race;
	private ClassLevel _level;
	private ClassType _type;
	
	private static final Set<PlayerClass> mainSubclassSet;
	private static final Set<PlayerClass> neverSubclassed = EnumSet.of(Overlord, Warsmith);
	
	private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
	private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
	private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
	private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
	private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(Sorceror, Spellsinger, Spellhowler);
	
	private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap<>(PlayerClass.class);
	
	static
	{
		Set<PlayerClass> subclasses = getSet(null, THIRD);
		subclasses.removeAll(neverSubclassed);
		
		mainSubclassSet = subclasses;
		
		subclassSetMap.put(DarkAvenger, subclasseSet1);
		subclassSetMap.put(Paladin, subclasseSet1);
		subclassSetMap.put(TempleKnight, subclasseSet1);
		subclassSetMap.put(ShillienKnight, subclasseSet1);
		
		subclassSetMap.put(TreasureHunter, subclasseSet2);
		subclassSetMap.put(AbyssWalker, subclasseSet2);
		subclassSetMap.put(Plainswalker, subclasseSet2);
		
		subclassSetMap.put(Hawkeye, subclasseSet3);
		subclassSetMap.put(SilverRanger, subclasseSet3);
		subclassSetMap.put(PhantomRanger, subclasseSet3);
		
		subclassSetMap.put(Warlock, subclasseSet4);
		subclassSetMap.put(ElementalSummoner, subclasseSet4);
		subclassSetMap.put(PhantomSummoner, subclasseSet4);
		
		subclassSetMap.put(Sorceror, subclasseSet5);
		subclassSetMap.put(Spellsinger, subclasseSet5);
		subclassSetMap.put(Spellhowler, subclasseSet5);
	}
	
	PlayerClass(Race race, ClassType pType, ClassLevel pLevel)
	{
		_race = race;
		_level = pLevel;
		_type = pType;
	}
	
	public final Set<PlayerClass> getAvailableSubclasses(L2PcInstance player)
	{
		Set<PlayerClass> subclasses = null;
		
		if (_level == THIRD)
		{
			if (player.getRace() != Race.KAMAEL)
			{
				subclasses = EnumSet.copyOf(mainSubclassSet);
				
				subclasses.remove(this);
				
				switch (player.getRace())
				{
					case ELF:
						subclasses.removeAll(getSet(Race.DARK_ELF, THIRD));
						break;
					case DARK_ELF:
						subclasses.removeAll(getSet(Race.ELF, THIRD));
						break;
				}
				
				subclasses.removeAll(getSet(Race.KAMAEL, THIRD));
				
				Set<PlayerClass> unavailableClasses = subclassSetMap.get(this);
				
				if (unavailableClasses != null)
				{
					subclasses.removeAll(unavailableClasses);
				}
				
			}
			else
			{
				subclasses = getSet(Race.KAMAEL, THIRD);
				subclasses.remove(this);
				// Check sex, male subclasses female and vice versa
				// If server owner set MaxSubclass > 3 some kamael's cannot take 4 sub
				// So, in that situation we must skip sex check
				if (CharacterConfig.MAX_SUBCLASS <= 3)
				{
					if (player.getAppearance().getSex() != null)
					{
						subclasses.removeAll(EnumSet.of(femaleSoulbreaker));
					}
					else
					{
						subclasses.removeAll(EnumSet.of(maleSoulbreaker));
					}
				}
				if (!player.getSubClasses().containsKey(2) || (player.getSubClasses().get(2).getLevel() < 75))
				{
					subclasses.removeAll(EnumSet.of(inspector));
				}
			}
		}
		return subclasses;
	}
	
	public static final EnumSet<PlayerClass> getSet(Race race, ClassLevel level)
	{
		EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);
		
		for (PlayerClass playerClass : EnumSet.allOf(PlayerClass.class))
		{
			if ((race == null) || playerClass.isOfRace(race))
			{
				if ((level == null) || playerClass.isOfLevel(level))
				{
					allOf.add(playerClass);
				}
			}
		}
		return allOf;
	}
	
	public final boolean isOfRace(Race pRace)
	{
		return _race == pRace;
	}
	
	public final boolean isOfType(ClassType pType)
	{
		return _type == pType;
	}
	
	public final boolean isOfLevel(ClassLevel pLevel)
	{
		return _level == pLevel;
	}
	
	public final ClassLevel getLevel()
	{
		return _level;
	}
}
