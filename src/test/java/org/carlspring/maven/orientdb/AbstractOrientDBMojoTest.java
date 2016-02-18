package org.carlspring.maven.orientdb;

import java.io.File;
import java.sql.SQLException;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;

public class AbstractOrientDBMojoTest extends AbstractMojoTestCase
{

	protected static final String TARGET_TEST_CLASSES = "target/test-classes";
	protected static final String POM_PLUGIN = TARGET_TEST_CLASSES + "/poms/pom-start.xml";

	protected Mojo lookupConfiguredMojo(String goal, String basedir) throws Exception
	{
		MavenProject project = readMavenProject(new File(basedir));
		Mojo mojo = lookupConfiguredMojo(project, goal);
		return mojo;
	}

	private MavenProject readMavenProject(File pom) throws Exception
	{
		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
		request.setBaseDirectory(pom.getParentFile());
		ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
		MavenProject project = lookup(ProjectBuilder.class).build(pom, configuration).getProject();
		return project;
	}

	protected boolean isOrientUp(AbstractOrientDBMojo mojo) throws SQLException
	{
		// TODO: figure out how to check this
//		return DriverManager.getConnection("jdbc:orient://localhost:2480/db;user=root;password=hello;create=true")
//				.isReadOnly();
		return false;
	}
}
