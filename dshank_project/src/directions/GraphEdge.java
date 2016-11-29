package directions;

import map_data.Node;

/**
 * An edge on a graph that extends from one segment to another. Edges are expected to be
 * directed.
 * @author david
 *
 */
public interface GraphEdge {

	/**
	 * returns whether or not a specified node is one of the nodes connected to this edge.
	 * @param n The specified node
	 * @return Whether this edge connects to the specified node.
	 */
	boolean hasNode(GraphNode n);

	/**
	 * returns the length of this edge
	 * @return the double value of the length between the two nodes.
	 */
	double getLength();

	/**
	 * returns the start node
	 * @return The start node
	 */
	GraphNode getStartNode();

	/**
	 * returns the end node
	 * @return the end node
	 */
	GraphNode getEndNode();
	
	/**
	 * Returns the unique ID for this edge.
	 * @return The edge's unique id.
	 */
	String getID();

}