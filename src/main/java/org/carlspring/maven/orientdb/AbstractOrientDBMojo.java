package org.carlspring.maven.orientdb;

/**
 * Copyright 2016 Carlspring Consulting & Development Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 * @author Juan Ignacio Bais (bais.juan@gmail.com)
 */
public abstract class AbstractOrientDBMojo extends AbstractMojo
{

	@Parameter(property = "orientdb.cfg")
	protected String configurationFile;

	/**
	 * The port to start OrientDB on.
	 */
	@Parameter(property = "orientdb.binary.port")
	protected int binaryPort;

	/**
	 * The port to start OrientDB on.
	 */
	@Parameter(property = "orientdb.http.port")
	protected int httpPort;

	/**
	 * The username to use when authenticating.
	 */
	@Parameter(property = "orientdb.username")
	protected String username;

	/**
	 * The password to use when authenticating.
	 */
	@Parameter(property = "orientdb.password")
	protected String password;

	/**
	 * Whether to bypass running orientdb.
	 */
	@Parameter(property = "orientdb.skip")
	private boolean skip;

	/**
	 * Shared {@link OServer} instance for all mojos.
	 */
	protected OServer server;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if (skip)
		{
			getLog().info("Skipping OrienDB execution.");
			return;
		}

		setup();

		doExecute();
	}

	protected void setup() throws MojoExecutionException
	{

		try
		{
			server = OServerMain.server();
			if (server == null)
			{
				server = OServerMain.create();
			}
		}
		catch (Exception e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	/**
	 * Implement mojo logic here.
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

	public int getBinaryPort()
	{
		return binaryPort;
	}

	public void setBinaryPort(int port)
	{
		this.binaryPort = port;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isSkip()
	{
		return skip;
	}

	public void setSkip(boolean skip)
	{
		this.skip = skip;
	}

	public int getHttpPort()
	{
		return httpPort;
	}

	public void setHttpPort(int httpPort)
	{
		this.httpPort = httpPort;
	}

	public String getConfigurationPath()
	{
		return configurationFile;
	}

	public void setConfigurationPath(String configurationPath)
	{
		this.configurationFile = configurationPath;
	}

}
