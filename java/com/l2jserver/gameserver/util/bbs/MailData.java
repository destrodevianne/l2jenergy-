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
package com.l2jserver.gameserver.util.bbs;

import java.util.Date;

/**
 * @author Мо3олЬ
 */
public class MailData
{
	private final String _author;
	private final String _title;
	private final String _postDate;
	private final int _messageId;
	
	public MailData(String author, String title, int postDate, int messageId)
	{
		_author = author;
		_title = title;
		_postDate = String.format(String.format("%1$te-%1$tm-%1$tY", new Date(postDate * 1000L)));
		_messageId = messageId;
	}
	
	public String getAuthor()
	{
		return _author;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public String getPostDate()
	{
		return _postDate;
	}
	
	public int getMessageId()
	{
		return _messageId;
	}
}
