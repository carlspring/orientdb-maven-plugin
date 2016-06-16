package org.carlspring.maven.orientdb;

import java.io.File;
import java.io.IOException;
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

    // private static final String CONFIGURATION_TOKEN = "${configurationFile}";

    private String serverConfigTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                          "<orient-server>\n" +
                                          "    <network>\n" +
                                          "        <protocols>\n" +
                                          "            <protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>\n" +
                                          "            <protocol name=\"http\" implementation=\"com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb\"/>\n" +
                                          "        </protocols>\n" +
                                          "        <listeners>\n" +
                                          "            <listener ip-address=\"0.0.0.0\" port-range=\"${binary.port}\" protocol=\"binary\"/>\n" +
                                          "            <listener ip-address=\"0.0.0.0\" port-range=\"${http.port}\" protocol=\"http\"/>\n" +
                                          "        </listeners>\n" +
                                          "    </network>\n" +
                                          "    <users>\n" +
                                          "        <user name=\"${username}\" password=\"${password}\" resources=\"*\"/>\n" +
                                          "    </users>\n" +
                                          "    <properties>\n" +
                                          "        <entry name=\"server.database.path\" value=\"target/orientdb/db\"/>\n" +
                                          "        <entry name=\"orientdb.www.path\" value=\"target/orientdb/www/\"/>\n" +
                                          // "        <entry name=\"orientdb.config.file\" value=\"${configurationFile}\"/>\n" +
                                          "        <entry name=\"server.cache.staticResources\" value=\"false\"/>\n" +
                                          "        <entry name=\"log.console.level\" value=\"info\"/>\n" +
                                          "        <entry name=\"log.file.level\" value=\"fine\"/>\n" +
                                          "        <entry name=\"plugin.dynamic\" value=\"false\"/>\n" +
                                          "    </properties>\n" +
                                          "</orient-server>\n";

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
                }
                else
                {
                    OServerConfiguration configuration = updateConfiguration();
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
                    }
                    else
                    {
                        getLog().info("OrientDB is already running.");
                    }
                }
                else
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
                }
                else
                {
                    getLog().info("OrientDB ping-pong: [FAILED]");
                    throw new MojoFailureException("Failed to start the OServer. " +
                                                   "The server did not respond with a pong withing 60 seconds.");
                }
            }
            else
            {
                throw new MojoExecutionException("Failed to start the OrientDB server!");
            }
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

    private OServerConfiguration updateConfiguration()
            throws IOException
    {
        OServerConfigurationManager cfgManager = new OServerConfigurationManager(new File(configurationFile));
        OServerConfiguration configuration = cfgManager.getConfiguration();

        for (OServerNetworkListenerConfiguration listener : configuration.network.listeners)
        {
            if ("binary".equals(listener.protocol) && binaryPort != 0)
            {
                listener.portRange = binaryPort + "-" + binaryPort + 6;
            }
            else if ("http".equals(listener.protocol) && httpPort != 0)
            {
                listener.portRange = httpPort + "-" + httpPort + 10;
            }
        }

        if (username != null && password != null)
        {
            configuration.users = new OServerUserConfiguration[] { new OServerUserConfiguration(username, password, "*") };
        }

        return configuration;
    }

    private String doReplacements()
    {
        /*
        serverConfigTemplate = serverConfigTemplate.replace(CONFIGURATION_TOKEN, configurationFile != null ?
                                                                                 configurationFile : "target/orientdb/db");
        */
        serverConfigTemplate = serverConfigTemplate.replace(BINARY_PORT_TOKEN, binaryPort + "-" +
                                                                               binaryPort + 6);
        serverConfigTemplate = serverConfigTemplate.replace(HTTP_PORT_TOKEN, binaryPort + "-" +
                                                                             binaryPort + 10);
        serverConfigTemplate = serverConfigTemplate.replace(USERNAME_TOKEN, username);

        return serverConfigTemplate.replace(PASSWORD_TOKEN, password);
    }

}
