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
import org.apache.maven.plugins.annotations.Parameter;

import com.orientechnologies.orient.server.OServerMain;

/**
 * @author Martin Todorov
 */
@Mojo(name = "start", requiresProject = false)
public class StartOrientDBMojo
        extends AbstractOrientDBMojo
{
	
    /**
     * Whether to fail, if there's already something running on the port.
     */
    @Parameter(property = "orientdb.fail.if.already.running", defaultValue = "true")
    private boolean failIfAlreadyRunning;


    @Override
    public void doExecute()
            throws MojoExecutionException, MojoFailureException
    {
        try
        {
            getLog().info("Starting the OrientDB server ...");

            // TODO: Implement
            
            server = OServerMain.create();
            server.startup();
            server.activate();
            
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean isFailIfAlreadyRunning()
    {
        return failIfAlreadyRunning;
    }

    public void setFailIfAlreadyRunning(boolean failIfAlreadyRunning)
    {
        this.failIfAlreadyRunning = failIfAlreadyRunning;
    }

}
