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
package handlers.usercommandhandlers;

import static com.l2jserver.gameserver.GameTimeController.MILLIS_IN_TICK;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static com.l2jserver.gameserver.enums.TeleportWhereType.TOWN;
import static com.l2jserver.gameserver.network.SystemMessageId.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT;
import static com.l2jserver.gameserver.network.serverpackets.ActionFailed.STATIC_PACKET;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.configuration.config.CharacterConfig;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.gameeventengine.GameEventManager;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SetupGauge;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Unstuck user command.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class Unstuck implements IUserCommandHandler
{
	private static final long FIVE_MINUTES = MINUTES.toSeconds(5);
	
	private static final SkillHolder ESCAPE_5_MINUTES = new SkillHolder(2099);
	
	private static final SkillHolder ESCAPE_1_SECOND = new SkillHolder(2100);
	
	private static final int RETURN = 1050;
	
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		
		if (!GameEventManager.getInstance().getEvent().onEscapeUse(activeChar.getObjectId()))
		{
			activeChar.sendPacket(STATIC_PACKET);
			return false;
		}
		
		if (activeChar.isJailed())
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "no_unstuck_in_jail"));
			return false;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return false;
		}
		
		if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || //
			activeChar.isAlikeDead() || activeChar.inObserverMode() || activeChar.isCombatFlagEquipped())
		{
			return false;
		}
		
		final int unstuckTimer = (activeChar.isGM() ? 1000 : CharacterConfig.UNSTUCK_INTERVAL * 1000);
		activeChar.forceIsCasting(GameTimeController.getInstance().getGameTicks() + (unstuckTimer / MILLIS_IN_TICK));
		
		if (activeChar.isGM())
		{
			activeChar.doCast(ESCAPE_1_SECOND);
			return true;
		}
		
		if (CharacterConfig.UNSTUCK_INTERVAL == FIVE_MINUTES)
		{
			activeChar.doCast(ESCAPE_5_MINUTES);
			return true;
		}
		
		if (CharacterConfig.UNSTUCK_INTERVAL > 100)
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "unstuck_time_minutes").replace("%s%", SECONDS.toMinutes(CharacterConfig.UNSTUCK_INTERVAL) + ""));
		}
		else
		{
			activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "unstuck_time_seconds").replace("%s%", CharacterConfig.UNSTUCK_INTERVAL + ""));
		}
		
		activeChar.getAI().setIntention(AI_INTENTION_IDLE);
		// SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, RETURN, 1, unstuckTimer, 0), 900);
		activeChar.sendPacket(new SetupGauge(0, unstuckTimer));
		// End SoE Animation section
		
		// Continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (!activeChar.isDead())
			{
				activeChar.setIsIn7sDungeon(false);
				activeChar.enableAllSkills();
				activeChar.setIsCastingNow(false);
				activeChar.setInstanceId(0);
				activeChar.teleToLocation(TOWN);
			}
		}, unstuckTimer));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}