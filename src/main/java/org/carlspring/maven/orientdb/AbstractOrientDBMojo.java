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
import org.apache.maven.project.MavenProject;

import java.net.InetAddress;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 */
public abstract class AbstractOrientDBMojo
        extends AbstractMojo
{

    @Parameter(readonly = true, property = "project", required = true)
    public MavenProject project;

    /**
     * The port to start OrientDB on.
     */
    @Parameter(property = "orientdb.port")
    private int port;

    /**
     * The username to use when authenticating.
     */
    @Parameter(property = "orientdb.username", defaultValue = "admin")
    private String username;

    /**
     * The password to use when authenticating.
     */
    @Parameter(property = "orientdb.password", defaultValue = "password")
    private String password;

    /**
     * The absolute class name of the driver.
     */
    @Parameter(property = "orientdb.driver")
    private String driver;

    /**
     * The URL to use when connecting.
     */
    @Parameter(property = "orientdb.url")
    private String connectionURL;

    /**
     * Whether to bypass running orientdb.
     */
    @Parameter(property = "orientdb.skip")
    private boolean skip;


    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            getLog().info("Skipping OrienDB execution.");
            return;
        }

        setup();

        doExecute();
    }

    protected void setup()
            throws MojoExecutionException
    {

        try
        {
            final InetAddress localHost = InetAddress.getByAddress("localhost", new byte[]{ 127, 0, 0, 1 });

            getLog().info("Initializing OrientDB for " + localHost);

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

    public MavenProject getProject()
    {
        return project;
    }

    public void setProject(MavenProject project)
    {
        this.project = project;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
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

    public String getConnectionURL()
    {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL)
    {
        this.connectionURL = connectionURL;
    }

    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public boolean isSkip()
    {
        return skip;
    }

    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

}
