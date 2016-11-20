package directions;

import java.util.Iterator;

public interface GraphNode {
	
	/**
	 * Returns the unique id of this node
	 * @return The string id of this node.
	 */
	public String getID();
	
	/**
	 * Returns an iterator over te edges moving away from the graph.
	 * @return THe iterator for the graphs edges.
	 */
	public Iterator<GraphEdge> getEdgeIt();

}
