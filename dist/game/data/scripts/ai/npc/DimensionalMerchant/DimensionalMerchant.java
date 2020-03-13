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
package ai.npc.DimensionalMerchant;

import java.util.HashMap;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.variables.PlayerVariables;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExGetPremiumItemList;

import ai.npc.AbstractNpcAI;

/**
 * Dimensional Merchant AI.
 * @author St3eT, Мо3олЬ
 */
public class DimensionalMerchant extends AbstractNpcAI
{
	// NPC
	private static final int MERCHANT = 32478; // Dimensional Merchant
	// Items
	private static final int MINION_COUPON = 13273; // Minion Coupon (5-hour)
	private static final int MINION_COUPON_EV = 13383; // Minion Coupon (5-hour) (Event)
	private static final int SUP_MINION_COUPON = 14065; // Superior Minion Coupon - 5-hour
	private static final int SUP_MINION_COUPON_EV = 14074; // Superior Minion Coupon (Event) - 5-hour
	private static final int FRIEND_RECOMMENDATION_PROOF = 15279; // Friend Recommendation Proof
	
	private static final int[] GIFTS =
	{
		15213, // Friend Recommend Round 1 Reward Pack
		15215, // Friend Recommend Round 2 Reward Pack
		15217, // Friend Recommend Round 1 Reward Pack
		15219 // Friend Recommend Round 1 Reward Pack
	};
	
	// Misc
	private static final HashMap<String, Integer> MINION_EXCHANGE = new HashMap<>();
	{
		// Normal
		MINION_EXCHANGE.put("whiteWeasel", 13017); // White Weasel Minion Necklace
		MINION_EXCHANGE.put("fairyPrincess", 13018); // Fairy Princess Minion Necklace
		MINION_EXCHANGE.put("wildBeast", 13019); // Wild Beast Fighter Minion Necklace
		MINION_EXCHANGE.put("foxShaman", 13020); // Fox Shaman Minion Necklace
		// Superior
		MINION_EXCHANGE.put("toyKnight", 14061); // Toy Knight Summon Whistle
		MINION_EXCHANGE.put("spiritShaman", 14062); // Spirit Shaman Summon Whistle
		MINION_EXCHANGE.put("owlMonk", 13550); // Owl Monk Hunting Helper Necklace
		MINION_EXCHANGE.put("turtleAscetic", 14064); // Turtle Ascetic Summon Necklace
	}
	
	private DimensionalMerchant()
	{
		super(DimensionalMerchant.class.getSimpleName(), "ai/npc");
		addStartNpc(MERCHANT);
		addFirstTalkId(MERCHANT);
		addTalkId(MERCHANT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "32478.html":
			case "32478-01.html":
			case "32478-02.html":
			case "32478-03.html":
			case "32478-04.html":
			case "32478-05.html":
			case "32478-06.html":
			case "32478-07.html":
			case "32478-08.html":
			case "32478-09.html":
			case "32478-10.html":
			case "32478-11.html":
			case "32478-12.html":
			case "32478-13.html":
			case "32478-14.html":
			case "32478-15.html":
			{
				htmltext = event;
				break;
			}
			case "receivePremium":
			{
				if (player.getPremiumItemList().isEmpty())
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_MORE_DIMENSIONAL_ITEMS_TO_BE_FOUND);
				}
				else
				{
					player.sendPacket(new ExGetPremiumItemList(player));
				}
				break;
			}
			case "whiteWeasel":
			case "fairyPrincess":
			case "wildBeast":
			case "foxShaman":
			{
				htmltext = giveMinion(player, event, MINION_COUPON, MINION_COUPON_EV);
				break;
			}
			case "toyKnight":
			case "spiritShaman":
			case "owlMonk":
			case "turtleAscetic":
			{
				htmltext = giveMinion(player, event, SUP_MINION_COUPON, SUP_MINION_COUPON_EV);
				break;
			}
			case "friend_recommendation":
			{
				if (!player.getVariables().getBoolean(PlayerVariables.USED_PC_FRIEND_RECOMMENDATION_PROOF, false))
				{
					player.getVariables().set(PlayerVariables.USED_PC_FRIEND_RECOMMENDATION_PROOF, true);
					htmltext = tradeItem(player, FRIEND_RECOMMENDATION_PROOF);
				}
				else
				{
					htmltext = "32478-14.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	private String tradeItem(L2PcInstance player, int itemId)
	{
		if ((player.getInventory().getItemByItemId(itemId) == null))
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return "32478-13.html";
		}
		
		if (takeItems(player, itemId, 1))
		{
			rewardItems(player, GIFTS[getRandom(GIFTS.length)], 1);
			return "32478-07.html";
		}
		return "32478-13.html";
	}
	
	private String giveMinion(L2PcInstance player, String event, int couponId, int eventCouponId)
	{
		if (hasAtLeastOneQuestItem(player, couponId, eventCouponId))
		{
			takeItems(player, (hasQuestItems(player, eventCouponId) ? eventCouponId : couponId), 1);
			final int minionId = MINION_EXCHANGE.get(event);
			giveItems(player, minionId, 1);
			final L2ItemInstance summonItem = player.getInventory().getItemByItemId(minionId);
			final IItemHandler handler = ItemHandler.getInstance().getHandler(summonItem.getEtcItem());
			if ((handler != null) && !player.hasPet())
			{
				handler.useItem(player, summonItem, true);
			}
			return "32478-07.html";
		}
		return "32478-06.html";
	}
	
	public static void main(String[] args)
	{
		new DimensionalMerchant();
	}
}
