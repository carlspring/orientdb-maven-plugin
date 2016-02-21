import com.tinkerpop.blueprints.impls.orient.OrientGraph

def graph = new OrientGraph("plocal:C:/temp/graph/db");
try {
	if (graph != null) {
				System.out.println("Connected");
	}
} finally {
  graph.shutdown();
}

return true;
