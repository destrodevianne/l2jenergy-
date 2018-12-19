/*
 * Copyright (C) 2004-2018 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers.instant;

import com.l2jserver.gameserver.enums.PcCafeType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExPCCafePointInfo;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Мо3олЬ
 */
public class PcBang extends AbstractEffect
{
	private final int _amount;
	
	public PcBang(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		_amount = params.getInt("amount", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		final L2PcInstance target = info.getEffected().getActingPlayer();
		
		if (target == null)
		{
			return;
		}
		
		final L2PcInstance player = info.getEffector().getActingPlayer();
		
		target.setPcCafePoints(player.getPcCafePoints() + _amount);
		target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ACQUIRED_S1_PC_BANG_POINT).addLong(_amount));
		target.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), _amount, 1, PcCafeType.ADD, 12));
	}
}