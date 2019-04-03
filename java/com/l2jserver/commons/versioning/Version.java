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
package com.l2jserver.commons.versioning;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version
{
	private static final Logger LOG = LoggerFactory.getLogger(Version.class);
	
	private String _revisionNumber = "exported";
	private String _versionNumber = "-1";
	private String _buildDate = "";
	private String _buildJdk = "";
	
	public Version(final Class<?> c)
	{
		final File jarName = Locator.getClassSource(c);
		try (JarFile jarFile = new JarFile(jarName))
		{
			final Attributes attrs = jarFile.getManifest().getMainAttributes();
			
			setBuildJdk(attrs);
			
			setBuildDate(attrs);
			
			setRevisionNumber(attrs);
			
			setVersionNumber(attrs);
		}
		catch (IOException e)
		{
			LOG.error("Unable to get soft information\nFile name '{}' isn't a valid jar", (jarName == null ? "null" : jarName.getAbsolutePath()));
		}
	}
	
	public String getRevisionNumber()
	{
		return _revisionNumber;
	}
	
	/**
	 * @param attrs
	 */
	private void setRevisionNumber(final Attributes attrs)
	{
		final String revisionNumber = attrs.getValue("Implementation-Build");
		if (revisionNumber != null)
		{
			_revisionNumber = revisionNumber;
		}
		else
		{
			_revisionNumber = "-1";
		}
	}
	
	public String getVersionNumber()
	{
		return _versionNumber;
	}
	
	/**
	 * @param attrs
	 */
	private void setVersionNumber(final Attributes attrs)
	{
		final String versionNumber = attrs.getValue("Implementation-Version");
		if (versionNumber != null)
		{
			_versionNumber = versionNumber;
		}
		else
		{
			_versionNumber = "-1";
		}
	}
	
	public String getBuildDate()
	{
		return _buildDate;
	}
	
	/**
	 * @param attrs
	 */
	private void setBuildDate(final Attributes attrs)
	{
		final String buildDate = attrs.getValue("Build-Date");
		if (buildDate != null)
		{
			_buildDate = buildDate;
		}
		else
		{
			_buildDate = "-1";
		}
	}
	
	public String getBuildJdk()
	{
		return _buildJdk;
	}
	
	/**
	 * @param attrs
	 */
	private void setBuildJdk(final Attributes attrs)
	{
		String buildJdk = attrs.getValue("Build-Jdk");
		if (buildJdk != null)
		{
			_buildJdk = buildJdk;
		}
		else
		{
			buildJdk = attrs.getValue("Created-By");
			if (buildJdk != null)
			{
				_buildJdk = buildJdk;
			}
			else
			{
				_buildJdk = "-1";
			}
		}
	}
}
