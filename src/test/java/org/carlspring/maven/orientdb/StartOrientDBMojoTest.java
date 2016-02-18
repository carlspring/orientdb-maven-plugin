package org.carlspring.maven.orientdb;

import java.sql.SQLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;

public class StartOrientDBMojoTest extends AbstractOrientDBMojoTest {

	StartOrientDBMojo startMojo;
	StopOrientDBMojo stopMojo;

	protected void setUp() throws Exception {
		super.setUp();

		startMojo = (StartOrientDBMojo) lookupConfiguredMojo("start", POM_PLUGIN);
		stopMojo = (StopOrientDBMojo) lookupConfiguredMojo("stop", POM_PLUGIN);
	}

	public void testMojo() throws MojoExecutionException, MojoFailureException, InterruptedException, SQLException {
		startMojo.execute();
		Assert.assertFalse(startMojo.isSkip());

		Thread.sleep(5000);

		stopMojo.execute();
	}
}
