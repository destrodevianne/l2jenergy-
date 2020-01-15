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
package com.l2jserver.loginserver.mail;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.loginserver.configuration.config.EmailConfig;
import com.l2jserver.loginserver.model.data.MailContent;

/**
 * @author mrTJO
 */
public class BaseMail implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(BaseMail.class);
	
	private MimeMessage _messageMime = null;
	
	private class SmtpAuthenticator extends Authenticator
	{
		private final PasswordAuthentication _auth;
		
		public SmtpAuthenticator()
		{
			_auth = new PasswordAuthentication(EmailConfig.EMAIL_SYS_USERNAME, EmailConfig.EMAIL_SYS_PASSWORD);
		}
		
		@Override
		public PasswordAuthentication getPasswordAuthentication()
		{
			return _auth;
		}
	}
	
	public BaseMail(String account, String mailId, String... args)
	{
		String mailAddr = getUserMail(account);
		
		if (mailAddr == null)
		{
			return;
		}
		
		MailContent content = MailSystem.getInstance().getMailContent(mailId);
		if (content == null)
		{
			return;
		}
		
		String message = compileHtml(account, content.getText(), args);
		
		final Properties mailProp = new Properties();
		mailProp.put("mail.smtp.host", EmailConfig.EMAIL_SYS_HOST);
		mailProp.put("mail.smtp.auth", EmailConfig.EMAIL_SYS_SMTP_AUTH);
		mailProp.put("mail.smtp.port", EmailConfig.EMAIL_SYS_PORT);
		mailProp.put("mail.smtp.socketFactory.port", EmailConfig.EMAIL_SYS_PORT);
		mailProp.put("mail.smtp.socketFactory.class", EmailConfig.EMAIL_SYS_FACTORY);
		mailProp.put("mail.smtp.socketFactory.fallback", EmailConfig.EMAIL_SYS_FACTORY_CALLBACK);
		final SmtpAuthenticator authenticator = (EmailConfig.EMAIL_SYS_SMTP_AUTH ? new SmtpAuthenticator() : null);
		
		Session mailSession = Session.getDefaultInstance(mailProp, authenticator);
		
		try
		{
			_messageMime = new MimeMessage(mailSession);
			_messageMime.setSubject(content.getSubject());
			try
			{
				_messageMime.setFrom(new InternetAddress(EmailConfig.EMAIL_SYS_ADDRESS, EmailConfig.EMAIL_SERVERINFO_NAME));
			}
			catch (UnsupportedEncodingException e)
			{
				LOG.warn("Sender Address not Valid!");
			}
			_messageMime.setContent(message, "text/html");
			_messageMime.setRecipient(Message.RecipientType.TO, new InternetAddress(mailAddr));
		}
		catch (MessagingException e)
		{
			LOG.warn("{}: ", getClass().getSimpleName(), e);
		}
	}
	
	private String compileHtml(String account, String html, String[] args)
	{
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				html = html.replace("%var" + i + "%", args[i]);
			}
		}
		html = html.replace("%accountname%", account);
		return html;
	}
	
	private String getUserMail(String username)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(EmailConfig.EMAIL_SYS_SELECTQUERY))
		{
			statement.setString(1, username);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					String mail = rset.getString(EmailConfig.EMAIL_SYS_DBFIELD);
					return mail;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Cannot select user mail!", e);
		}
		return null;
	}
	
	@Override
	public void run()
	{
		try
		{
			if (_messageMime != null)
			{
				Transport.send(_messageMime);
			}
		}
		catch (MessagingException e)
		{
			LOG.warn("Error encounterd while sending email!", e);
		}
	}
}
