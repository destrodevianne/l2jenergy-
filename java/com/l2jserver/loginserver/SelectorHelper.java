/*
 * Copyright (C) 2004-2019 L2J Server
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
package com.l2jserver.loginserver;

import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.IPv4Filter;
import com.l2jserver.loginserver.network.L2LoginClient;
import com.l2jserver.loginserver.network.serverpackets.Init;
import com.l2jserver.mmocore.IAcceptFilter;
import com.l2jserver.mmocore.IClientFactory;
import com.l2jserver.mmocore.IMMOExecutor;
import com.l2jserver.mmocore.MMOConnection;
import com.l2jserver.mmocore.ReceivablePacket;

public class SelectorHelper implements IMMOExecutor<L2LoginClient>, IClientFactory<L2LoginClient>, IAcceptFilter
{
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	private final ThreadPoolExecutor _generalPacketsThreadPool;
	private final IPv4Filter _ipv4filter;
	
	public SelectorHelper()
	{
		_generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		_ipv4filter = new IPv4Filter();
	}
	
	@Override
	public void execute(ReceivablePacket<L2LoginClient> packet)
	{
		_generalPacketsThreadPool.execute(packet);
	}
	
	@Override
	public L2LoginClient create(MMOConnection<L2LoginClient> con)
	{
		L2LoginClient client = new L2LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}
	
	@Override
	public boolean accept(SocketChannel sc)
	{
		try
		{
			return _ipv4filter.accept(sc) && !LoginController.getInstance().isBannedAddress(sc.socket().getInetAddress());
		}
		catch (UnknownHostException e)
		{
			LOG.error("{}: Invalid address: {};", SelectorHelper.class.getSimpleName(), sc.socket().getInetAddress(), e);
		}
		return false;
	}
}
