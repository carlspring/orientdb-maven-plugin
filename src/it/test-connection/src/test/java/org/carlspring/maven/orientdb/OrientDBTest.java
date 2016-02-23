package org.carlspring.maven.orientdb;

import java.io.IOException;
import java.util.Properties;


import org.junit.Assert;
import org.junit.Test;
import com.orientechnologies.orient.client.remote.OServerAdmin;

public class OrientDBTest
{

	@Test
	public void connectionTest() throws IOException
	{
		OServerAdmin serverAdmin = new OServerAdmin("remote:localhost:3015").connect("root", "hello");
		System.out.println("Succesfully established a test connection to the database!");
	}

}
