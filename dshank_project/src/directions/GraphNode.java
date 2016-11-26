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
	
	/**
	 * Returns an iterator over the incoming edges for a node.
	 * @return An iterator over the incoming edges.
	 */
	public Iterator<GraphEdge> getIncomingEdgeIt();
	
	/**
	 * Returns the edge going from this node to paramter end node.
	 * @param endNode The node to find the edge to
	 * @return The node if there is a direct edge to it, walse otherwise.
	 */
	public GraphEdge getEdgeTo(GraphNode endNode);
	
	/**
	 * Returns the degree, or number of outgoing edges, of the node.
	 * @return The degree of the node. 0 if the node has no outgoing edges.
	 */
	public int getDegree();
	
	public void addGraphEdge(GraphEdge e);
	
	public Iterator<GraphSegment> getSegmentIt();
	
	public Iterator<GraphSegment> getIncomingSegmentIt();

}
