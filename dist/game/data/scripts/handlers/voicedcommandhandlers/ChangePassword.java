/*
 * Copyright (C) 2004-2018 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.voicedcommandhandlers;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.LoginServerThread;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MessagesData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Nik
 */
public class ChangePassword implements IVoicedCommandHandler
{
	private static final Map<Integer, Long> REUSES = new ConcurrentHashMap<>();
	private static final int REUSE = 30 * 60 * 1000; // 30 Min
	
	private static final String[] _voicedCommands =
	{
		"changepassword"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (target != null)
		{
			final StringTokenizer st = new StringTokenizer(target);
			try
			{
				String curpass = null, newpass = null, repeatnewpass = null;
				if (st.hasMoreTokens())
				{
					curpass = st.nextToken();
				}
				if (st.hasMoreTokens())
				{
					newpass = st.nextToken();
				}
				if (st.hasMoreTokens())
				{
					repeatnewpass = st.nextToken();
				}
				
				if (!((curpass == null) || (newpass == null) || (repeatnewpass == null)))
				{
					if (!newpass.equals(repeatnewpass))
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "password_no_repeated"));
						return false;
					}
					if (newpass.length() < 3)
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "password_shorter"));
						return false;
					}
					if (newpass.length() > 30)
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "password_longer"));
						return false;
					}
					final Long timeStamp = REUSES.get(activeChar.getObjectId());
					if ((timeStamp != null) && ((System.currentTimeMillis() - REUSE) < timeStamp.longValue()))
					{
						activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "new_password_cannot_change_password_often"));
						return false;
					}
					REUSES.put(activeChar.getObjectId(), System.currentTimeMillis());
					LoginServerThread.getInstance().sendChangePassword(activeChar.getAccountName(), activeChar.getName(), curpass, newpass);
				}
				else
				{
					activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "password_invalid"));
					return false;
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage(MessagesData.getInstance().getMessage(activeChar, "password_problem"));
				LOG.warn("", e);
			}
		}
		else
		{
			// showHTML(activeChar);
			String html = HtmCache.getInstance().getHtm("en", "data/html/mods/ChangePassword.htm");
			if (html == null)
			{
				html = "<html><body><br><br><center><font color=LEVEL>404:</font> File Not Found</center></body></html>";
			}
			activeChar.sendPacket(new NpcHtmlMessage(html));
			return true;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
