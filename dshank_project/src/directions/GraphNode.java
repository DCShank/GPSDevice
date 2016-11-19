package directions;

import java.util.Iterator;

public interface GraphNode {
	
	/**
	 * Returns an iterator over te edges of the graph.
	 * @return THe iterator for the graphs edges.
	 */
	public Iterator<GraphEdge> getEdgeIt();

}
