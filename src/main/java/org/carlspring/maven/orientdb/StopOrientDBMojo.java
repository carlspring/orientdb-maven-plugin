package org.carlspring.maven.orientdb;

/**
 * Copyright 2016 Carlspring Consulting & Development Ltd.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

/**
 * @author Martin Todorov (carlspring@gmail.com)
 * @author Juan Ignacio Bais (bais.juan@gmail.com)
 */
@Mojo(name = "stop", requiresProject = false)
public class StopOrientDBMojo extends AbstractOrientDBMojo
{

    /**
     * Whether to fail, if OrientDB is not running.
     */
    @Parameter(property = "orientdb.fail.if.already.running", defaultValue = "true")
    boolean failIfNotRunning;

    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException
    {
        if (!server.isActive())
        {
            if (failIfNotRunning)
            {
                throw new MojoExecutionException("Failed to stop the OrientDB server, no server running!");
            }

            getLog().error("OrientDB server was already stopped.");
            return;
        }

        server.shutdown();

        while (true)
        {
            if (!server.isActive())
            {
                getLog().info("OrientDB has stopped!");
                break;
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

    public boolean isFailIfNotRunning()
    {
        return failIfNotRunning;
    }

    public void setFailIfNotRunning(boolean failIfNotRunning)
    {
        this.failIfNotRunning = failIfNotRunning;
    }

}
