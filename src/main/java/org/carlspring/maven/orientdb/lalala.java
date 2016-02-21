package org.carlspring.maven.orientdb;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class lalala
{
	public void a() {
		OrientGraph graph = new OrientGraph("plocal:C:/temp/graph/db");
		try {
			if (graph != null) {
				System.out.println("conectado");
			}
		} finally {
		  graph.shutdown();
		}
	}
}
