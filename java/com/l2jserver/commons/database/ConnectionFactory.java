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
package com.l2jserver.commons.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.pool.PooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.C3P0PooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.HikariCPPooledConnectionFactory;

/**
 * Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class ConnectionFactory
{
	public static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);
	
	private final PooledConnectionFactory pooledConnectionFactory;
	
	private final String connectionPool;
	
	private final String url;
	
	private final String user;
	
	private final String password;
	
	private final String driver;
	
	private final int maxPoolSize;
	
	private final int maxIdleTime;
	
	ConnectionFactory(Builder builder)
	{
		connectionPool = builder._connectionPool;
		url = builder._url;
		user = builder._user;
		password = builder._password;
		driver = builder._driver;
		maxPoolSize = builder._maxPoolSize;
		maxIdleTime = builder._maxIdleTime;
		
		switch (connectionPool)
		{
			default:
			case "HikariCP":
			{
				pooledConnectionFactory = new HikariCPPooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
			case "C3P0":
			{
				pooledConnectionFactory = new C3P0PooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
		}
		LOG.info("Using {} connection pool.", pooledConnectionFactory.getClass().getSimpleName().replace("PooledConnectionFactory", ""));
	}
	
	public static PooledConnectionFactory getInstance()
	{
		return Builder.INSTANCE.pooledConnectionFactory;
	}
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public static final class Builder
	{
		protected static volatile ConnectionFactory INSTANCE;
		
		public String _connectionPool;
		public String _url;
		public String _user;
		public String _password;
		public String _driver;
		public int _maxPoolSize;
		public int _maxIdleTime;
		
		Builder()
		{
		}
		
		public Builder withConnectionPool(String connectionPool)
		{
			_connectionPool = connectionPool;
			return this;
		}
		
		public Builder withUrl(String url)
		{
			_url = url;
			return this;
		}
		
		public Builder withUser(String user)
		{
			_user = user;
			return this;
		}
		
		public Builder withPassword(String password)
		{
			_password = password;
			return this;
		}
		
		public Builder withDriver(String driver)
		{
			_driver = driver;
			return this;
		}
		
		public Builder withMaxPoolSize(int maxPoolSize)
		{
			_maxPoolSize = maxPoolSize;
			return this;
		}
		
		public Builder withMaxIdleTime(int maxIdleTime)
		{
			_maxIdleTime = maxIdleTime;
			return this;
		}
		
		public void build()
		{
			if (INSTANCE == null)
			{
				synchronized (this)
				{
					if (INSTANCE == null)
					{
						INSTANCE = new ConnectionFactory(this);
					}
					else
					{
						LOG.warn("Trying to build another Connection Factory!");
					}
				}
			}
		}
	}
}
