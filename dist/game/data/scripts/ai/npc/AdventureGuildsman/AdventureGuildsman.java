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
package ai.npc.AdventureGuildsman;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.configuration.config.events.PcCafeConfig;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.PcCafeType;
import com.l2jserver.gameserver.enums.skills.CommonSkill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ServitorInstance;
import com.l2jserver.gameserver.model.variables.PlayerVariables;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExPCCafePointInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowQuestInfo;
import com.l2jserver.gameserver.network.serverpackets.ShowPCCafeCouponShowUI;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

import ai.npc.AbstractNpcAI;

/**
 * AdventureGuildsman AI test (находится в доработке)
 * @author Мо3олЬ
 */
public class AdventureGuildsman extends AbstractNpcAI
{
	private static final Map<String, int[]> PETSKILL = new HashMap<>();
	private static final Map<String, int[]> POINTSSKILL = new HashMap<>();
	private static final Map<String, int[]> TELEPORTERS = new HashMap<>();
	
	private boolean WYVERN_ACTIVE = false;
	
	// Items
	private static final int PCCAFE_LOTTERY_TICKET_30DAYS = 15358;
	private static final int PCCAFE_1ST_LOTTERY_TICKET_30DAYS = 15359; // билет на выдачу 100000 очков РС-клуба
	private static final int PCCAFE_2ND_LOTTERY_TICKET_30DAYS = 15360; // билет на выдачу 10000 очков РС-клуба
	private static final int PCCAFE_3RD_LOTTERY_TICKET_30DAYS = 15361; // билет на выдачу 2000 очков РС-клуба
	private static final int PCCAFE_4TH_LOTTERY_TICKET_30DAYS = 15362; // билет на выдачу 1000 очков РС-клуба
	private static final int PCCAFE_5TH_LOTTERY_TICKET_30DAYS = 15363; // билет на выдачу 100 очков РС-клуба
	
	// @formatter:off
	private static final int[] ADVENTURERS_GUILDSMAN =
	{
		31729, 31730, 31731, 31732, 31733, 31734, 31735, 31736, 31737, 31738, 31775, 
		31776, 31777, 31778, 31779, 31780, 31781, 31782, 31783, 31784, 31785, 31786, 
		31787, 31788, 31789, 31790, 31791, 31792, 31793, 31794, 31795, 31796, 31797, 
		31798, 31799, 31800, 31801, 31802, 31803, 31804, 31805, 31806, 31807, 31808, 
		31809, 31810, 31811, 31812, 31813, 31814, 31815, 31816, 31817, 31818, 31819, 
		31820, 31821, 31822, 31823, 31824, 31825, 31826, 31827, 31828, 31829, 31830, 
		31831, 31832, 31833, 31834, 31835, 31836, 31837, 31838, 31839, 31840, 31841, 
		31991, 31992, 31993, 31994, 31995, 32337, 32338, 32339, 32340
	};
 
	static
	{
		// баф для персонажей ниже 40 ур.    ID   LV  Points
		POINTSSKILL.put("S401", new int[] { 4397, 1, 300 }); // Berserker Spirit - 300 points
		POINTSSKILL.put("S402", new int[] { 4393, 1, 200 }); // Might - 200 points
		POINTSSKILL.put("S403", new int[] { 4392, 1, 100 }); // Shield - 100 points
		POINTSSKILL.put("S404", new int[] { 4391, 1, 200 }); // Wind Walk - 200 points
		POINTSSKILL.put("S405", new int[] { 4404, 1, 550 }); // Focus - 550 points
		POINTSSKILL.put("S406", new int[] { 4396, 1, 300 }); // Magic Barrier - 300 points
		POINTSSKILL.put("S407", new int[] { 4405, 1, 650 }); // Death Whisper - 650 points
		POINTSSKILL.put("S408", new int[] { 4403, 1, 200 }); // Guidance - 200 points
		POINTSSKILL.put("S409", new int[] { 4398, 1, 100 }); // Bless Shield - 100 points
		POINTSSKILL.put("S410", new int[] { 4394, 1, 200 }); // Blessed Body - 200 points
		POINTSSKILL.put("S411", new int[] { 4395, 1, 200 }); // Blessed Soul - 200 points
		POINTSSKILL.put("S412", new int[] { 4402, 1, 400 }); // Haste - 400 points
		POINTSSKILL.put("S413", new int[] { 4406, 1, 200 }); // Agility - 200 points
		POINTSSKILL.put("S414", new int[] { 4399, 1, 200 }); // Vampiric Rage - 200 points
		POINTSSKILL.put("S415", new int[] { 4401, 1, 200 }); // Empower - 200 points
		POINTSSKILL.put("S416", new int[] { 4400, 1, 400 }); // Acumen - 400 points

		//баф для персонажей 40-54 ур.      ID   LV  Points
		POINTSSKILL.put("S417", new int[] { 4397, 1, 300 }); // Berserker Spirit - 300 points
		POINTSSKILL.put("S418", new int[] { 4393, 2, 300 }); // Might - 300 points
		POINTSSKILL.put("S419", new int[] { 4392, 2, 150 }); // Shield - 150 points
		POINTSSKILL.put("S420", new int[] { 4391, 2, 300 }); // Wind Walk - 300 points
		POINTSSKILL.put("S421", new int[] { 4404, 2, 650 }); // Focus - 650 points
		POINTSSKILL.put("S422", new int[] { 4396, 1, 300 }); // Magic Barrier - 300 points
		POINTSSKILL.put("S423", new int[] { 4405, 2, 800 }); // Death Whisper - 800 points
		POINTSSKILL.put("S424", new int[] { 4403, 2, 300 }); // Guidance - 300 points
		POINTSSKILL.put("S425", new int[] { 4398, 2, 150 }); // Bless Shield - 150 points
		POINTSSKILL.put("S426", new int[] { 4394, 3, 300 }); // Blessed Body - 300 points
		POINTSSKILL.put("S427", new int[] { 4395, 3, 300 }); // Blessed Soul - 300 points
		POINTSSKILL.put("S428", new int[] { 4402, 1, 400 }); // Haste - 400 points
		POINTSSKILL.put("S429", new int[] { 4406, 2, 300 }); // Agility - 300 points
		POINTSSKILL.put("S430", new int[] { 4399, 2, 300 }); // Vampiric Rage - 300 points
		POINTSSKILL.put("S431", new int[] { 4401, 2, 300 }); // Empower - 300 points
		POINTSSKILL.put("S432", new int[] { 4400, 2, 600 }); // Acumen - 600 points
		
		//баф для персонажей 55 и выше ур.  ID   LV  Points
		POINTSSKILL.put("S433", new int[] { 4397, 2, 500 }); // Berserker Spirit - 500 points
		POINTSSKILL.put("S434", new int[] { 4393, 3, 400 }); // Might - 400 points
		POINTSSKILL.put("S435", new int[] { 4392, 3, 200 }); // Shield - 200 points
		POINTSSKILL.put("S436", new int[] { 4391, 2, 300 }); // Wind Walk - 300 points
		POINTSSKILL.put("S437", new int[] { 4404, 3, 800 }); // Focus - 800 points
		POINTSSKILL.put("S438", new int[] { 4396, 2, 400 }); // Magic Barrier - 400 points
		POINTSSKILL.put("S439", new int[] { 4405, 3, 950 }); // Death Whisper - 950 points
		POINTSSKILL.put("S440", new int[] { 4403, 3, 400 }); // Guidance - 400 points
		POINTSSKILL.put("S441", new int[] { 4398, 3, 200 }); // Bless Shield - 200 points
		POINTSSKILL.put("S442", new int[] { 4394, 4, 400 }); // Blessed Body - 400 points
		POINTSSKILL.put("S443", new int[] { 4395, 4, 400 }); // Blessed Soul - 400 points
		POINTSSKILL.put("S444", new int[] { 4402, 2, 950 }); // Haste - 950 points
		POINTSSKILL.put("S445", new int[] { 4406, 3, 400 }); // Agility - 400 points
		POINTSSKILL.put("S446", new int[] { 4399, 3, 400 }); // Vampiric Rage - 400 points
		POINTSSKILL.put("S447", new int[] { 4401, 3, 400 }); // Empower - 400 points
		POINTSSKILL.put("S448", new int[] { 4400, 3, 950 }); // Acumen - 950 points

		// Pet Skill                      ID   LV  QTY Points
		PETSKILL.put("P4391", new int[] { 4397, 1, 300 }); // Berserker Spirit - 300 points
		PETSKILL.put("P4392", new int[] { 4393, 2, 300 }); // Might - 300 points
		PETSKILL.put("P4393", new int[] { 4392, 2, 150 }); // Shield - 150 points
		PETSKILL.put("P4394", new int[] { 4391, 2, 300 }); // Wind Walk - 300 points
		PETSKILL.put("P4395", new int[] { 4404, 2, 650 }); // Focus - 650 points
		PETSKILL.put("P4396", new int[] { 4396, 1, 300 }); // Magic Barrier - 300 points
		PETSKILL.put("P4397", new int[] { 4405, 2, 800 }); // Death Whisper - 800 points
		PETSKILL.put("P4398", new int[] { 4403, 2, 300 }); // Guidance - 300 points
		PETSKILL.put("P4399", new int[] { 4398, 2, 150 }); // Bless Shield - 150 points
		PETSKILL.put("P4400", new int[] { 4394, 3, 300 }); // Blessed Body - 300 points
		PETSKILL.put("P4401", new int[] { 4395, 3, 300 }); // Blessed Soul - 300 points
		PETSKILL.put("P4402", new int[] { 4402, 1, 400 }); // Haste - 400 points
		PETSKILL.put("P4403", new int[] { 4406, 2, 300 }); // Agility - 300 points
		PETSKILL.put("P4404", new int[] { 4399, 2, 300 }); // Vampiric Rage - 300 points
		PETSKILL.put("P4405", new int[] { 4401, 2, 300 }); // Empower - 300 points
		PETSKILL.put("P4406", new int[] { 4400, 2, 600 }); // Acumen - 600 points
		
		// Teleporters                            x       y       z    QTY Points
		// Talking Island Teleporters
		TELEPORTERS.put("TELE_01", new int[] { -112367, 234703, -3688, 30 }); // Elven Ruins 30 points
		TELEPORTERS.put("TELE_02", new int[] { -111728, 244330, -3448, 20 }); // Singing Waterfall 20 points
		TELEPORTERS.put("TELE_03", new int[] { -106696, 214691, -3424, 30 }); // Western Territory 30 points
		TELEPORTERS.put("TELE_04", new int[] { -99586, 237637, -3568, 20 }); // Obelisk of Victory 20 points
		// Elven Village Teleporters
		TELEPORTERS.put("TELE_05", new int[] { 21362, 51122, -3688, 20 }); // Elven Forest 20 points
		TELEPORTERS.put("TELE_06", new int[] { 29294, 74968, -3776, 30 }); // Elven Fortress 30 points
		TELEPORTERS.put("TELE_07", new int[] { -10612, 75881, -3592, 50 }); // Neutral Zone 50 points
		// Dark Elf Village Teleporters
		TELEPORTERS.put("TELE_08", new int[] { -22224, 14168, -3232, 30 }); // Dark Forest 30 points
		TELEPORTERS.put("TELE_09", new int[] { -21966, 40544, -3192, 30 }); // Swampland 30 points
		TELEPORTERS.put("TELE_10", new int[] { -61095, 75104, -3352, 90 }); // Spider Nest 90 points
		TELEPORTERS.put("TELE_11", new int[] { -10612, 75881, -3592, 50 }); // Neutral Zone 50 points
		// Orc Village Teleporters
		TELEPORTERS.put("TELE_12", new int[] { -4190, -80040, -2696, 50 }); // Immortal Plateau Southern Region 50 points
		TELEPORTERS.put("TELE_13", new int[] { -10983, -117484, -2464, 30 }); // The Immortal Plateau 30 points
		TELEPORTERS.put("TELE_14", new int[] { 9340, -112509, -2536, 40 }); // Cave of Trials 40 points
		TELEPORTERS.put("TELE_15", new int[] { 8652, -139941, -1144, }); // Frozen Waterfall 40 points
		// Dwarven Village Teleporters
		TELEPORTERS.put("TELE_16", new int[] { 139714, -177456, -1536, 20 }); // Abandoned Coal Mines 20 points
		TELEPORTERS.put("TELE_17", new int[] { 169008, -208272, -3504, 60 }); // Eastern Mining Zone (Northeastern Shore) 60 points
		TELEPORTERS.put("TELE_18", new int[] { 136910, -205082, -3664, 30 }); // Western Mining Zone (Central Shore) 30 points
		TELEPORTERS.put("TELE_19", new int[] { 171946, -173352, 3440, 280 }); // Mithril Mines Western Entrance 280 points
		TELEPORTERS.put("TELE_20", new int[] { 178591, -184615, -360, 300 }); // Mithril Mines Eastern Entrance 300 points
		// Kamael Village No Teleporters
		// The Village of Gludin Teleporters
		TELEPORTERS.put("TELE_21", new int[] { -44763, 203497, -3592, 50 }); // Langk Lizardman Dwelling 50 points
		TELEPORTERS.put("TELE_22", new int[] { -63736, 101522, -3552, 40 }); // Fellmere Harvest Grounds 40 points
		TELEPORTERS.put("TELE_23", new int[] { -75437, 168800, -3632, 20 }); // Windmill Hill 20 points
		TELEPORTERS.put("TELE_24", new int[] { -53001, 191425, -3568, 50 }); // Forgotten Temple 50 points
		TELEPORTERS.put("TELE_25", new int[] { -89763, 105359, -3576, 50 }); // Orc Barracks 50 points
		TELEPORTERS.put("TELE_26", new int[] { -88539, 83389, -2864, 70 }); // Windy Hill 70 points
		TELEPORTERS.put("TELE_27", new int[] { -49853, 147089, -2784, 30 }); // Abandoned Camp 30 points
		TELEPORTERS.put("TELE_28", new int[] { -16526, 208032, -3664, 90 }); // Wastelands 90 points
		TELEPORTERS.put("TELE_29", new int[] { -42256, 198333, -2800, 100 }); // Red Rock Ridge 100 points
		// Gludio Teleporters
		TELEPORTERS.put("TELE_30", new int[] { -41248, 122848, -2904, 20 }); // Ruins of Agony 20 points
		TELEPORTERS.put("TELE_31", new int[] { -19120, 136816, -3752, 20 }); // Ruins of Despair 20 points
		TELEPORTERS.put("TELE_32", new int[] { -9959, 176184, -4160, 60 }); // Ant Cave 60 points
		TELEPORTERS.put("TELE_33", new int[] { -28327, 155125, -3496, 40 }); // Windawood Manor 40 points
		// Dion Teleporters
		TELEPORTERS.put("TELE_34", new int[] { 5106, 126916, -3664, 20 }); // Cruma Marshlands 20 points
		TELEPORTERS.put("TELE_35", new int[] { 17225, 114173, -3440, 60 }); // Cruma Tower 60 points
		TELEPORTERS.put("TELE_36", new int[] { 47382, 111278, -2104, 50 }); // Fortress of Resistance 50 points
		TELEPORTERS.put("TELE_37", new int[] { 630, 179184, -3720, 40 }); // Plains of Dion 40 points
		TELEPORTERS.put("TELE_38", new int[] { 34475, 188095, -2976, 80 }); // Bee Hive 80 points
		TELEPORTERS.put("TELE_39", new int[] { 60374, 164301, -2856, 100 }); // Tanor Canyon 100 points
		// Floran Village Teleporters
		TELEPORTERS.put("TELE_40", new int[] { 50568, 152408, -2656, 40 }); // Execution Grounds 40 points
		TELEPORTERS.put("TELE_41", new int[] { 33565, 162393, -3600, 40 }); // Tanor Canyon (West side) 40 points
		TELEPORTERS.put("TELE_42", new int[] { 26810, 172787, -3376, 20 }); // Floran Agricultural Area 20 points
		// Heine Teleporters
		TELEPORTERS.put("TELE_43", new int[] { 87691, 162835, -3563, 300 }); // Field of Silence 300 points
		TELEPORTERS.put("TELE_44", new int[] { 82192, 226128, -3664, 150 }); // Field of Whispers 150 points
		TELEPORTERS.put("TELE_45", new int[] { 115583, 192261, -3488, 60 }); // Entrance to Alligator Islands 60 points
		TELEPORTERS.put("TELE_46", new int[] { 84413, 234334, -3656, 60 }); // Garden of Eva 60 points
		TELEPORTERS.put("TELE_47", new int[] { 149518, 195280, -3736, 180 }); // Isle of Prayer 180 points
		// Giran Teleporters
		TELEPORTERS.put("TELE_48", new int[] { 73024, 118485, -3688, 50 }); // Dragon Valley 50 points
		TELEPORTERS.put("TELE_49", new int[] { 131557, 114509, -3712, 180 }); // Antharas Lair 180 points
		TELEPORTERS.put("TELE_50", new int[] { 43408, 206881, -3752, 150 }); // Devil Isle 150 points
		TELEPORTERS.put("TELE_51", new int[] { 85546, 131328, -3672, 30 }); // Brekas Stronghold 30 points
		// Oren Teleporters
		TELEPORTERS.put("TELE_52", new int[] { 76839, 63851, -3648, 20 }); // Sel Mahum Training Grounds (West Gate) 20 points
		TELEPORTERS.put("TELE_53", new int[] { 87252, 85514, -3056, 50 }); // Plains of Lizardmen 50 points
		TELEPORTERS.put("TELE_54", new int[] { 91539, -12204, -2440, 130 }); // Outlaw Forest 130 points
		TELEPORTERS.put("TELE_55", new int[] { 64328, 26803, -3768, 70 }); // Sea of Spores 70 points
		// Hunters Village Teleporters
		TELEPORTERS.put("TELE_56", new int[] { 124904, 61992, -3920, 40 }); // Southern Pathway of Enchanted Valley 40 points
		TELEPORTERS.put("TELE_57", new int[] { 104426, 33746, -3800, 90 }); // Northern Pathway of Enchanted Valley 90 points
		TELEPORTERS.put("TELE_58", new int[] { 142065, 81300, -3000, 50 }); // Entrance to the Forest of Mirrors 50 points
		// Aden Teleporters
		TELEPORTERS.put("TELE_59", new int[] { 168217, 37990, -4072, 50 }); // Forsaken Plains 50 points
		TELEPORTERS.put("TELE_60", new int[] { 184742, 19745, -3168, 80 }); // Seal of Shilen 80 points
		TELEPORTERS.put("TELE_61", new int[] { 142065, 81300, -3000, 110 }); // Forest of Mirrors 110 points
		TELEPORTERS.put("TELE_62", new int[] { 155310, -16339, -3320, 170 }); // Blazing Swamp 170 points
		TELEPORTERS.put("TELE_63", new int[] { 183543, -14974, -2776, 170 }); // Fields of Massacre 170 points
		TELEPORTERS.put("TELE_64", new int[] { 106517, -2871, -3416, 150 }); // Ancient Battleground 150 points
		TELEPORTERS.put("TELE_65", new int[] { 170838, 55776, -5280, 160 }); // Silent Valley 160 points
		TELEPORTERS.put("TELE_66", new int[] { 114649, 11115, -5120, 110 }); // ToI 110 points
		TELEPORTERS.put("TELE_67", new int[] { 174491, 50942, -4360, 190 }); // The Giant's Cave 190 points
		// Goddard Teleporters
		TELEPORTERS.put("TELE_68", new int[] { 125740, -40864, -3736, 110 }); // Varka Silenos Stronghold 110 points
		TELEPORTERS.put("TELE_69", new int[] { 146990, -67128, -3640, 50 }); // Ketra Orc Outpost 50 points
		TELEPORTERS.put("TELE_70", new int[] { 144880, -113468, -2560, 240 }); // Hot Springs 240 points
		TELEPORTERS.put("TELE_71", new int[] { 165054, -47861, -3560, 60 }); // Wall of Argos 60 points
		TELEPORTERS.put("TELE_72", new int[] { 106414, -87799, -2920, 250 }); // Monastery of silence 250 points
		TELEPORTERS.put("TELE_73", new int[] { 169018, -116303, -2432, 250 }); // Forge of the Gods 250 points
		// Rune Teleporters
		TELEPORTERS.put("TELE_74", new int[] { 53516, -82831, -2700, 120 }); // Wild Beast Pastures 120 points
		TELEPORTERS.put("TELE_75", new int[] { 65307, -71445, -3688, 100 }); // Valley of Saints 100 points
		TELEPORTERS.put("TELE_76", new int[] { 52107, -54328, -3152, 30 }); // Forest of the Dead 300 points
		TELEPORTERS.put("TELE_77", new int[] { 69340, -50203, -3288, 80 }); // Swamp of Screams 80 points
		TELEPORTERS.put("TELE_78", new int[] { 106414, -87799, -2920, 350 }); // Monastery of Silence 350 points
		TELEPORTERS.put("TELE_79", new int[] { 89513, -44800, -2136, 230 }); // Stakato 230 points
		TELEPORTERS.put("TELE_80", new int[] { 11235, -24026, -3640, 160 }); // Primeval Isle 160 points
		// Schuttgart Teleporters
		TELEPORTERS.put("TELE_81", new int[] { 47692, -115745, -3744, 240 }); // Crypt of Disgrace 240 points
		TELEPORTERS.put("TELE_82", new int[] { 111965, -154172, -1528, 40 }); // Plunderous Plains 400 points
		TELEPORTERS.put("TELE_83", new int[] { 68693, -110438, -1904, 190 }); // Den of Evil 190 points
		TELEPORTERS.put("TELE_84", new int[] { 91280, -117152, -3928, 60 }); // Pavel Ruins 60 points
		TELEPORTERS.put("TELE_85", new int[] { 113903, -108752, -856, 90 }); // Ice Merchant Cabin 90 points
		// Hardin's Private Academy Teleporters
		TELEPORTERS.put("TELE_86", new int[] { 73024, 118485, -3688, 50 }); // Dragon Valley 50 points
		TELEPORTERS.put("TELE_87", new int[] { 131557, 114509, -3712, 80 }); // Antharas Lair 80 points
		TELEPORTERS.put("TELE_88", new int[] { 113553, 134813, -3540, 40 }); // Gorgon Flower Garden 40 points
		TELEPORTERS.put("TELE_89", new int[] { 60374, 164301, -2856, 140 }); // Tanor Canyon 140 points
		// Ivory Tower Teleporters
		TELEPORTERS.put("TELE_90", new int[] { 106517, -2871, -3416, 90 }); // Ancient Battleground 90 points
		TELEPORTERS.put("TELE_91", new int[] { 93218, 16969, -3904, 20 }); // Forest of Evil 20 points
		TELEPORTERS.put("TELE_92", new int[] { 67097, 68815, -3648, 120 }); // Timak Outpost 110 points
		TELEPORTERS.put("TELE_93", new int[] { -122425, 73348, -2872, 40 }); // Stronghold 1 40 points
		TELEPORTERS.put("TELE_94", new int[] { -116934, 46616, 368, 80 }); // Stronghold 2 80 points
		TELEPORTERS.put("TELE_95", new int[] { -85800, 37066, -2053, 50 }); // Stronghold 3 50 points
		TELEPORTERS.put("TELE_96", new int[] { -74045, 51984, -3681, 70 }); // Isle of Soul Harbor 70 points
	}
	// @formatter:on
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmtext = null;
		
		switch (event)
		{
			case "quest_list":
			{
				player.sendPacket(ExShowQuestInfo.STATIC_PACKET);
				break;
			}
			case "pccafe_coupon":
			{
				htmtext = "pccoupon001.htm";
				break;
			}
			case "show_pccafe_coupon_ui":
			{
				if (player.getVariables().getLong(PlayerVariables.PCC_CODE_ATTEMPTS, 1) < PcCafeConfig.ALT_PCBANG_POINTS_MAX_CODE_ENTER_ATTEMPTS)
				{
					player.sendPacket(new ShowPCCafeCouponShowUI());
				}
				else
				{
					htmtext = "max_attempts.htm";
				}
				break;
			}
			case "pccafe_list":
			{
				htmtext = "pccafe_list001.htm";
				break;
			}
			case "life_crystal":
			{
				htmtext = npc.getId() + "-1.htm";
				break;
			}
			case "item_list":
			{
				htmtext = "pccafe_item001.htm";
				break;
			}
			case "buff_list":
			{
				if (player.getLevel() < 40) // ниже 40-го уровня
				{
					htmtext = "pccafe_buff_1001.htm";
				}
				else if ((player.getLevel() >= 40) & (player.getLevel() < 54)) // для 40-54 уровня
				{
					htmtext = "pccafe_buff_2001.htm";
				}
				else if (player.getLevel() >= 55) // выше 55-го уровня
				{
					htmtext = "pccafe_buff_3001.htm";
				}
				// htmtext = "pccafe_buff_warning.htm"; // TODO: нужена реализация
				break;
			}
			case "buff_list_pet":
			{
				htmtext = "pccafe_summon_newbuff_001.htm";
				break;
			}
			case "life_crystal_help.htm":
			case "adventure_manager_help.htm":
			case "raid_boss_position.htm":
			case "pccafe_help_inzone001.htm":
			case "pccafe_help_lottery001.htm":
			case "pccafe_help_lottery002.htm":
			case "pccafe_buff_1002.htm":
			case "pccafe_buff_2002.htm":
			case "pccafe_buff_3002.htm":
			case "pccafe_buff_warning.htm":
			case "pccafe_summon_newbuff_002.htm":
			case "pccoupon001.htm":
			case "pccoupon_error_under20.htm":
			case "pccafe_wyvern001.htm":
			{
				htmtext = event;
				break;
			}
			case "give_lottery_ticket":
			{
				if (!player.getVariables().getBoolean(PlayerVariables.USED_PC_LOTTERY_TICKET, false))
				{
					player.getVariables().set(PlayerVariables.USED_PC_LOTTERY_TICKET, true);
					giveItems(player, PCCAFE_LOTTERY_TICKET_30DAYS, 1);
				}
				else
				{
					htmtext = "pccafe_help_lottery_notoneday.htm";
				}
				break;
			}
			case "trade_100":
			{
				htmtext = tradeItem(player, PCCAFE_5TH_LOTTERY_TICKET_30DAYS, 100);
				break;
			}
			case "trade_1000":
			{
				htmtext = tradeItem(player, PCCAFE_4TH_LOTTERY_TICKET_30DAYS, 1000);
				break;
			}
			case "trade_2000":
			{
				htmtext = tradeItem(player, PCCAFE_3RD_LOTTERY_TICKET_30DAYS, 2000);
				break;
			}
			case "trade_10000":
			{
				htmtext = tradeItem(player, PCCAFE_2ND_LOTTERY_TICKET_30DAYS, 10000);
				break;
			}
			case "trade_100000":
			{
				htmtext = tradeItem(player, PCCAFE_1ST_LOTTERY_TICKET_30DAYS, 100000);
				break;
			}
		}
		
		if (event.startsWith("tele"))
		{
			htmtext = npc.getId() + "-2.htm";
		}
		else if (TELEPORTERS.containsKey(event))
		{
			if (player.getPcCafePoints() >= TELEPORTERS.get(event)[3])
			{
				player.setPcCafePoints(player.getPcCafePoints() - (TELEPORTERS.get(event)[3]));
				SystemMessage smsgpc = SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT);
				smsgpc.addInt(TELEPORTERS.get(event)[3]);
				player.sendPacket(smsgpc);
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), TELEPORTERS.get(event)[3], 1, PcCafeType.CONSUME, 12));
				player.teleToLocation(TELEPORTERS.get(event)[0], TELEPORTERS.get(event)[1], TELEPORTERS.get(event)[2]);
				return null;
			}
			htmtext = "pccafe_notpoint001.htm";
		}
		else if (event.equalsIgnoreCase("warrior40"))
		{
			if (player.getPcCafePoints() >= 3100)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 3100);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(3100));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -3100, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player);
				npc.doCast(SkillData.getInstance().getSkill(4397, 1)); // Berserker Spirit
				npc.doCast(SkillData.getInstance().getSkill(4393, 1)); // Might
				npc.doCast(SkillData.getInstance().getSkill(4392, 1)); // Shield
				npc.doCast(SkillData.getInstance().getSkill(4391, 1)); // Wind Walk
				npc.doCast(SkillData.getInstance().getSkill(4404, 1)); // Focus
				npc.doCast(SkillData.getInstance().getSkill(4405, 1)); // Death Whisper
				npc.doCast(SkillData.getInstance().getSkill(4403, 1)); // Guidance
				npc.doCast(SkillData.getInstance().getSkill(4398, 1)); // Bless Shield
				npc.doCast(SkillData.getInstance().getSkill(4394, 1)); // Blessed Body
				npc.doCast(SkillData.getInstance().getSkill(4402, 1)); // Haste
				npc.doCast(SkillData.getInstance().getSkill(4406, 1)); // Agility
				npc.doCast(SkillData.getInstance().getSkill(4399, 1)); // Vampiric Rage
				htmtext = "pccafe_buff_1001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("mage40"))
		{
			if (player.getPcCafePoints() >= 1600)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 1600);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(1600));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -1600, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player);
				npc.doCast(SkillData.getInstance().getSkill(4397, 1)); // Berserker Spirit
				npc.doCast(SkillData.getInstance().getSkill(4396, 1)); // Magic Barrier
				npc.doCast(SkillData.getInstance().getSkill(4392, 1)); // Shield
				npc.doCast(SkillData.getInstance().getSkill(4391, 1)); // Wind Walk
				npc.doCast(SkillData.getInstance().getSkill(4394, 1)); // Blessed Body
				npc.doCast(SkillData.getInstance().getSkill(4395, 1)); // Blessed Soul
				npc.doCast(SkillData.getInstance().getSkill(4401, 1)); // Empower
				npc.doCast(SkillData.getInstance().getSkill(4400, 1)); // Acumen
				htmtext = "pccafe_buff_1001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("warrior54"))
		{
			if (player.getPcCafePoints() >= 4000)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 4000);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(4000));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -4000, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player);
				npc.doCast(SkillData.getInstance().getSkill(4397, 1)); // Berserker Spirit
				npc.doCast(SkillData.getInstance().getSkill(4393, 2)); // Might
				npc.doCast(SkillData.getInstance().getSkill(4392, 2)); // Shield
				npc.doCast(SkillData.getInstance().getSkill(4391, 2)); // Wind Walk
				npc.doCast(SkillData.getInstance().getSkill(4404, 2)); // Focus
				npc.doCast(SkillData.getInstance().getSkill(4405, 2)); // Death Whisper
				npc.doCast(SkillData.getInstance().getSkill(4403, 2)); // Guidance
				npc.doCast(SkillData.getInstance().getSkill(4398, 2)); // Bless Shield
				npc.doCast(SkillData.getInstance().getSkill(4394, 3)); // Blessed Body
				npc.doCast(SkillData.getInstance().getSkill(4402, 1)); // Haste
				npc.doCast(SkillData.getInstance().getSkill(4406, 2)); // Agility
				npc.doCast(SkillData.getInstance().getSkill(4399, 2)); // Vampiric Rage
				htmtext = "pccafe_buff_2001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("mage54"))
		{
			if (player.getPcCafePoints() >= 2100)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 2100);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(2100));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -2100, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player);
				npc.doCast(SkillData.getInstance().getSkill(4397, 1)); // Berserker Spirit
				npc.doCast(SkillData.getInstance().getSkill(4396, 1)); // Magic Barrier
				npc.doCast(SkillData.getInstance().getSkill(4392, 2)); // Shield
				npc.doCast(SkillData.getInstance().getSkill(4391, 2)); // Wind Walk
				npc.doCast(SkillData.getInstance().getSkill(4394, 3)); // Blessed Body
				npc.doCast(SkillData.getInstance().getSkill(4395, 3)); // Blessed Soul
				npc.doCast(SkillData.getInstance().getSkill(4401, 2)); // Empower
				npc.doCast(SkillData.getInstance().getSkill(4400, 2)); // Acumen
				htmtext = "pccafe_buff_2001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("warrior55"))
		{
			if (player.getPcCafePoints() >= 5600)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 5600);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(5600));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -5600, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player);
				npc.doCast(SkillData.getInstance().getSkill(4397, 2)); // Berserker Spirit
				npc.doCast(SkillData.getInstance().getSkill(4393, 3)); // Might
				npc.doCast(SkillData.getInstance().getSkill(4392, 3)); // Shield
				npc.doCast(SkillData.getInstance().getSkill(4391, 2)); // Wind Walk
				npc.doCast(SkillData.getInstance().getSkill(4404, 3)); // Focus
				npc.doCast(SkillData.getInstance().getSkill(4405, 3)); // Death Whisper
				npc.doCast(SkillData.getInstance().getSkill(4403, 3)); // Guidance
				npc.doCast(SkillData.getInstance().getSkill(4398, 3)); // Bless Shield
				npc.doCast(SkillData.getInstance().getSkill(4394, 4)); // Blessed Body
				npc.doCast(SkillData.getInstance().getSkill(4402, 2)); // Haste
				npc.doCast(SkillData.getInstance().getSkill(4406, 3)); // Agility
				npc.doCast(SkillData.getInstance().getSkill(4399, 3)); // Vampiric Rage
				htmtext = "pccafe_buff_3001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("mage55"))
		{
			if (player.getPcCafePoints() >= 3000)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 3000);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(3000));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -3000, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player);
				npc.doCast(SkillData.getInstance().getSkill(4397, 2)); // Berserker Spirit
				npc.doCast(SkillData.getInstance().getSkill(4396, 2)); // Magic Barrier
				npc.doCast(SkillData.getInstance().getSkill(4392, 3)); // Shield
				npc.doCast(SkillData.getInstance().getSkill(4391, 2)); // Wind Walk
				npc.doCast(SkillData.getInstance().getSkill(4394, 4)); // Blessed Body
				npc.doCast(SkillData.getInstance().getSkill(4395, 4)); // Blessed Soul
				npc.doCast(SkillData.getInstance().getSkill(4401, 3)); // Empower
				npc.doCast(SkillData.getInstance().getSkill(4400, 3)); // Acumen
				htmtext = "pccafe_buff_3001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (POINTSSKILL.containsKey(event))
		{
			if (player.getLevel() < 40) // ниже 40-го уровня
			{
				if (player.getPcCafePoints() >= POINTSSKILL.get(event)[2])
				{
					player.setPcCafePoints(player.getPcCafePoints() - POINTSSKILL.get(event)[2]);
					SystemMessage smsgpc = SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT);
					smsgpc.addInt(POINTSSKILL.get(event)[2]);
					player.sendPacket(smsgpc);
					player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -POINTSSKILL.get(event)[2], 1, PcCafeType.CONSUME, 12));
					npc.setTarget(player);
					npc.doCast(SkillData.getInstance().getSkill(POINTSSKILL.get(event)[0], POINTSSKILL.get(event)[1]));
					return "pccafe_buff_1002.htm";
				}
				htmtext = "pccafe_notpoint001.htm";
			}
			else if ((player.getLevel() >= 40) & (player.getLevel() < 54)) // для 40-54 уровня
			{
				if (player.getPcCafePoints() >= POINTSSKILL.get(event)[2])
				{
					player.setPcCafePoints(player.getPcCafePoints() - POINTSSKILL.get(event)[2]);
					SystemMessage smsgpc = SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT);
					smsgpc.addInt(POINTSSKILL.get(event)[2]);
					player.sendPacket(smsgpc);
					player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -POINTSSKILL.get(event)[2], 1, PcCafeType.CONSUME, 12));
					npc.setTarget(player);
					npc.doCast(SkillData.getInstance().getSkill(POINTSSKILL.get(event)[0], POINTSSKILL.get(event)[1]));
					return "pccafe_buff_2002.htm";
				}
				htmtext = "pccafe_notpoint001.htm";
			}
			else if (player.getLevel() >= 55) // выше 55-го уровня
			{
				if (player.getPcCafePoints() >= POINTSSKILL.get(event)[2])
				{
					player.setPcCafePoints(player.getPcCafePoints() - POINTSSKILL.get(event)[2]);
					SystemMessage smsgpc = SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT);
					smsgpc.addInt(POINTSSKILL.get(event)[2]);
					player.sendPacket(smsgpc);
					player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -POINTSSKILL.get(event)[2], 1, PcCafeType.CONSUME, 12));
					npc.setTarget(player);
					npc.doCast(SkillData.getInstance().getSkill(POINTSSKILL.get(event)[0], POINTSSKILL.get(event)[1]));
					return "pccafe_buff_3002.htm";
				}
				htmtext = "pccafe_notpoint001.htm";
			}
			else
			{
				htmtext = "pccafe_skill_nolevel.htm";
			}
		}
		else if (event.equalsIgnoreCase("pet_warrior"))
		{
			if ((player.getSummon() == null) || !(player.getSummon() instanceof L2ServitorInstance))
			{
				htmtext = "pccafe_notsummon.htm";
			}
			else if (player.getPcCafePoints() >= 4000)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 4000);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(4000));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -4000, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player.getSummon());
				npc.doCast(SkillData.getInstance().getSkill(4397, 1));
				npc.doCast(SkillData.getInstance().getSkill(4393, 2));
				npc.doCast(SkillData.getInstance().getSkill(4392, 2));
				npc.doCast(SkillData.getInstance().getSkill(4391, 2));
				npc.doCast(SkillData.getInstance().getSkill(4404, 2));
				npc.doCast(SkillData.getInstance().getSkill(4396, 1));
				npc.doCast(SkillData.getInstance().getSkill(4405, 2));
				npc.doCast(SkillData.getInstance().getSkill(4403, 2));
				npc.doCast(SkillData.getInstance().getSkill(4398, 2));
				npc.doCast(SkillData.getInstance().getSkill(4394, 3));
				npc.doCast(SkillData.getInstance().getSkill(4402, 1));
				npc.doCast(SkillData.getInstance().getSkill(4406, 2));
				npc.doCast(SkillData.getInstance().getSkill(4399, 2));
				htmtext = "pccafe_summon_newbuff_001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("pet_mage"))
		{
			if ((player.getSummon() == null) || !(player.getSummon() instanceof L2ServitorInstance))
			{
				htmtext = "pccafe_notsummon.htm";
			}
			else if (player.getPcCafePoints() >= 2100)
			{
				player.setPcCafePoints(player.getPcCafePoints() - 2100);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(2100));
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -2100, 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player.getSummon());
				npc.doCast(SkillData.getInstance().getSkill(4397, 1));
				npc.doCast(SkillData.getInstance().getSkill(4396, 1));
				npc.doCast(SkillData.getInstance().getSkill(4392, 2));
				npc.doCast(SkillData.getInstance().getSkill(4391, 2));
				npc.doCast(SkillData.getInstance().getSkill(4395, 3));
				npc.doCast(SkillData.getInstance().getSkill(4401, 2));
				npc.doCast(SkillData.getInstance().getSkill(4400, 2));
				htmtext = "pccafe_summon_newbuff_001.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (PETSKILL.containsKey(event))
		{
			if ((player.getSummon() == null) || !(player.getSummon() instanceof L2ServitorInstance))
			{
				htmtext = "pccafe_notsummon.htm";
			}
			else if (player.getPcCafePoints() >= PETSKILL.get(event)[2])
			{
				player.setPcCafePoints(player.getPcCafePoints() - PETSKILL.get(event)[2]);
				SystemMessage smsgpc = SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT);
				smsgpc.addInt(PETSKILL.get(event)[2]);
				player.sendPacket(smsgpc);
				player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -PETSKILL.get(event)[2], 1, PcCafeType.CONSUME, 12));
				npc.setTarget(player.getSummon());
				npc.doCast(SkillData.getInstance().getSkill(PETSKILL.get(event)[0], PETSKILL.get(event)[1]));
				return "pccafe_summon_newbuff_002.htm";
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		else if (event.equalsIgnoreCase("wyvern"))
		{
			if (!(player.getSummon() == null) || (player.getSummon() instanceof L2ServitorInstance))
			{
				player.sendPacket(SystemMessageId.SUMMON_ONLY_ONE);
				return null;
			}
			else if (player.getPcCafePoints() >= PcCafeConfig.ALT_PC_BANG_WIVERN_PRICE)
			{
				if (WYVERN_ACTIVE)
				{
					htmtext = "pccafe_no_wyvern001.htm";
				}
				else
				{
					player.setPcCafePoints(player.getPcCafePoints() - PcCafeConfig.ALT_PC_BANG_WIVERN_PRICE);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_USING_S1_POINT).addLong(PcCafeConfig.ALT_PC_BANG_WIVERN_PRICE));
					player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), -PcCafeConfig.ALT_PC_BANG_WIVERN_PRICE, 1, PcCafeType.CONSUME, 12));
					player.dismount();
					player.mount(12621, 0, true);
					player.addSkill(CommonSkill.WYVERN_BREATH.getSkill());
					WYVERN_ACTIVE = true;
					return null;
				}
			}
			else
			{
				htmtext = "pccafe_notpoint001.htm";
			}
		}
		return htmtext;
	}
	
	private String tradeItem(L2PcInstance player, int itemId, int points)
	{
		if (player.getPcCafePoints() >= PcCafeConfig.MAX_PC_BANG_POINTS)
		{
			return "pccafe_help_lottery_fail2.htm";
		}
		
		if ((player.getInventory().getItemByItemId(itemId) == null))
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return "pccafe_help_lottery_fail.htm";
		}
		
		if (takeItems(player, itemId, 1))
		{
			player.setPcCafePoints(player.getPcCafePoints() + points);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ACQUIRED_S1_PC_BANG_POINT).addLong(points));
			player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), points, 1, PcCafeType.ADD, 12));
			return "pccafe_help_lottery003.htm";
		}
		return "pccafe_help_lottery_fail.htm";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".htm";
	}
	
	private AdventureGuildsman()
	{
		super(AdventureGuildsman.class.getSimpleName(), "ai/npc");
		addStartNpc(ADVENTURERS_GUILDSMAN);
		addTalkId(ADVENTURERS_GUILDSMAN);
		addFirstTalkId(ADVENTURERS_GUILDSMAN);
	}
	
	public static void main(String[] args)
	{
		new AdventureGuildsman();
	}
}