package directions;

import java.util.Iterator;

/**
 * A Node on a graph. Aggregated by a Graph. Can have edges.
 * 
 * A node on a graph. I think at some point I lost track of the point of the graph interfaces
 * and found that I was using them where it wasn't necessary, which lead to it being helpful
 * to have getLat and getLon.
 * 
 * I'll have to reconsider those. Maybe rename them to getY and getX
 * @author david
 *
 */
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
	
	/**
	 * Should be replaced with remove edge.
	 * @param seg
	 */
	public void removeSegment(GraphSegment seg);

	public double getLat();
	
	public double getLon();

}
