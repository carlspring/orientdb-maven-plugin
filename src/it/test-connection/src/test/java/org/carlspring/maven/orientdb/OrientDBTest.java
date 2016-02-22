package org.carlspring.maven.orientdb;
import org.junit.Assert;
import org.junit.Test;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientDBTest
{

	@Test
	public void connectionTest() {
		OrientGraph graph = new OrientGraph("plocal:C:/temp/graph/db");
		
		Assert.assertTrue(graph != null);
		graph.shutdown();
	}

}
