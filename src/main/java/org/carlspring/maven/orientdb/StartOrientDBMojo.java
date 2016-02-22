package org.carlspring.maven.orientdb;

import java.io.File;
import java.net.BindException;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerConfigurationManager;
import com.orientechnologies.orient.server.config.OServerNetworkListenerConfiguration;
import com.orientechnologies.orient.server.config.OServerUserConfiguration;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 * @author Juan Ignacio Bais (bais.juan@gmail.com)
 */
@Mojo(name = "start", requiresProject = false)
public class StartOrientDBMojo extends AbstractOrientDBMojo
{
	private static final String BINARY_PORT_TOKEN = "${binary.port}";
	private static final String HTTP_PORT_TOKEN = "${http.port}";
	private static final String USERNAME_TOKEN = "${username}";
	private static final String PASSWORD_TOKEN = "${password}";

	/**
	 * Whether to fail, if there's already something running on the port.
	 */
	@Parameter(property = "orientdb.fail.if.already.running", defaultValue = "true")
	private boolean failIfAlreadyRunning;

	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			try
			{
				getLog().info("Starting the OrientDB server ...");

				if (configurationFile == null)
				{
					String config = doReplacements();
					server.startup(config);
				} else
				{
					OServerConfigurationManager cfgManager = new OServerConfigurationManager(
							new File(configurationFile));
					OServerConfiguration configuration = cfgManager.getConfiguration();

					for (OServerNetworkListenerConfiguration listener : configuration.network.listeners)
					{
						if ("binary".equals(listener.protocol) && binaryPort != 0)
						{
							listener.portRange = String.valueOf(binaryPort) + "-" + String.valueOf(binaryPort) + 6;
						} else if ("http".equals(listener.protocol) && httpPort != 0)
						{
							listener.portRange = String.valueOf(httpPort) + "-" + String.valueOf(httpPort) + 10;
						}
					}

					if (username != null && password != null)
					{
						configuration.users = new OServerUserConfiguration[]
						{ new OServerUserConfiguration(username, password, "*") };
					}

					server.startup(configuration);
				}
				server.activate();
			}
			catch (Exception e)
			{
				if (e instanceof BindException)
				{
					if (failIfAlreadyRunning)
					{
						throw new MojoExecutionException("Failed to start the OrientDB server, port already open!", e);
					} else
					{
						getLog().info("OrientDB is already running.");
					}
				} else
				{
					throw new MojoExecutionException(e.getMessage(), e);
				}
			}

			if (server != null)
			{
				long maxSleepTime = 60000;
				long sleepTime = 0;
				boolean pong = false;

				while (!pong && sleepTime < maxSleepTime)
				{
					pong = server.isActive();
					sleepTime += 1000;
					Thread.sleep(1000);
				}

				if (pong)
				{
					getLog().info("OrientDB ping-pong: [OK]");
				} else
				{
					getLog().info("OrientDB ping-pong: [FAILED]");
					throw new MojoFailureException("Failed to start the OServer."
							+ " The server did not respond with a pong withing 60 seconds.");
				}
			} else
			{
				throw new MojoExecutionException("Failed to start the OrientDB server!");
			}
		}
		catch (Exception e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private String doReplacements()
	{
		serverConfigTemplate = serverConfigTemplate.replace(BINARY_PORT_TOKEN,
				String.valueOf(binaryPort) + "-" + String.valueOf(binaryPort) + 6);
		serverConfigTemplate = serverConfigTemplate.replace(HTTP_PORT_TOKEN,
				String.valueOf(httpPort) + "-" + String.valueOf(binaryPort) + 10);
		serverConfigTemplate = serverConfigTemplate.replace(USERNAME_TOKEN, username);
		return serverConfigTemplate.replace(PASSWORD_TOKEN, password);
	}

	public boolean isFailIfAlreadyRunning()
	{
		return failIfAlreadyRunning;
	}

	public void setFailIfAlreadyRunning(boolean failIfAlreadyRunning)
	{
		this.failIfAlreadyRunning = failIfAlreadyRunning;
	}

	private static String serverConfigTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<orient-server>" + "<network>" + "<protocols>"
			+ "<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>"
			+ "<protocol name=\"http\" implementation=\"com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb\"/>"
			+ "</protocols>" + "<listeners>"
			+ "<listener ip-address=\"0.0.0.0\" port-range=\"${binary.port}\" protocol=\"binary\"/>"
			+ "<listener ip-address=\"0.0.0.0\" port-range=\"${http.port}\" protocol=\"http\"/>" + "</listeners>"
			+ "</network>" + "<users>" + "<user name=\"${username}\" password=\"${password}\" resources=\"*\"/>"
			+ "</users>" + "<properties>"
			+ "<entry name=\"orientdb.www.path\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/www/\"/>"
			+ "<entry name=\"orientdb.config.file\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/config/orientdb-server-config.xml\"/>"
			+ "<entry name=\"server.cache.staticResources\" value=\"false\"/>"
			+ "<entry name=\"log.console.level\" value=\"info\"/>" + "<entry name=\"log.file.level\" value=\"fine\"/>"
			+ "<entry name=\"plugin.dynamic\" value=\"false\"/>" + "</properties>" + "</orient-server>";
}
