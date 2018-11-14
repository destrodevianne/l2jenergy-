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
package com.l2jserver.loginserver.mail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jserver.Config;
import com.l2jserver.loginserver.model.data.MailContent;
import com.l2jserver.util.data.xml.IXmlReader;

/**
 * @author mrTJO
 */
public class MailSystem implements IXmlReader
{
	private final Map<String, MailContent> _mailData = new HashMap<>();
	
	public MailSystem()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_mailData.clear();
		parseDatapackFile("data/mail/MailList.xml");
		LOG.info("{}: eMail System Loaded.", getClass().getSimpleName());
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node d = doc.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if (d.getNodeName().equals("mail"))
			{
				String mailId = d.getAttributes().getNamedItem("id").getNodeValue();
				String subject = d.getAttributes().getNamedItem("subject").getNodeValue();
				String maFile = d.getAttributes().getNamedItem("file").getNodeValue();
				
				try (FileInputStream fis = new FileInputStream(new File(Config.DATAPACK_ROOT, "data/mail/" + maFile));
					BufferedInputStream bis = new BufferedInputStream(fis))
				{
					int bytes = bis.available();
					byte[] raw = new byte[bytes];
					
					bis.read(raw);
					String html = new String(raw, "UTF-8");
					html = html.replaceAll(Config.EOL, "\n");
					html = html.replace("%servermail%", Config.EMAIL_SERVERINFO_ADDRESS);
					html = html.replace("%servername%", Config.EMAIL_SERVERINFO_NAME);
					
					_mailData.put(mailId, new MailContent(subject, html));
				}
				catch (IOException e)
				{
					LOG.warn("IOException while reading {}", maFile);
				}
			}
		}
	}
	
	public void sendMail(String account, String messageId, String... args)
	{
		BaseMail mail = new BaseMail(account, messageId, args);
		mail.run();
	}
	
	public MailContent getMailContent(String mailId)
	{
		return _mailData.get(mailId);
	}
	
	public static MailSystem getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final MailSystem _instance = new MailSystem();
	}
}
