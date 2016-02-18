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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Runs the OrientDB server, blocking until the server no longer responds to
 * pings.
 *
 * @author Martin Todorov (carlspring@gmail.com)
 * @author Juan Ignacio Bais (bais.juan@gmail.com)
 */
@Mojo(name = "run", requiresProject = false)
public class RunOrientDBMojo extends StartOrientDBMojo
{

	// @Override
	// public void doExecute()
	// throws MojoExecutionException, MojoFailureException
	// {
	// try
	// {
	// super.doExecute();
	//
	// getLog().info("Blocking to wait for connections, use the orientdb:stop
	// goal to kill.");
	//
	// // TODO: Implement.
	// }
	// catch (Exception e)
	// {
	// throw new MojoExecutionException(e.getMessage(), e);
	// }
	// }

	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			super.doExecute();
			getLog().info("Blocking to wait for connections, use the orientdb:stop goal to kill.");

			while (true)
			{
				if (!server.isActive())
				{
					getLog().info("OrientDB server is not responding to pings, exiting...");
					return;
				}
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
