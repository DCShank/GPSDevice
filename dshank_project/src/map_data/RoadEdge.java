package map_data;

import directions.GraphEdge;
import directions.GraphNode;

/**
 * Represents an Edge on a graph with a length, Nodes, and adjacent edges to each Node.
 * RoadEdges can be assumed to be directional; For a two way street each node would have
 * a different RoadEdge, although both RoadEdges would have both of the nodes, with start
 * and end reversed.
 * @author david
 *
 */
public class RoadEdge implements GraphEdge {
	/** 
	 * The String ID for this edge.
	 * The ID is made up of the id of the start node + the id of the end node.
	 */
	private final String id;
	private final Node startNode;
	private final Node endNode;
	private final double length;
	private final DistanceStrategy strat;
	
	/**
	 * Constructor for a graph edge.
	 * @param sn the startNode
	 * @param en the endNode
	 * @param strat The strategy for finding the distance.
	 */
	public RoadEdge(Node sn, Node en, DistanceStrategy strat) {
		startNode = sn;
		endNode = en;
		id = startNode.getID() + endNode.getID();
		length = strat.getDistance(sn.getLon(), sn.getLat(), en.getLon(), en.getLat());
		this.strat = strat;
	}
	
	/**
	 * returns the length of this edge
	 * @return the double value of the length between the two nodes.
	 */
	@Override
	public double getLength() {
		return length;
	}
	
	/**
	 * returns the start node
	 * @return The start node
	 */
	@Override
	public Node getStartNode() {
		return startNode;
	}
	
	/**
	 * returns the end node
	 * @return the end node
	 */
	@Override
	public Node getEndNode() {
		return endNode;
	}
	
	/**
	 * Returns the unique ID for this edge.
	 * The IDs for RoadEdges is made up of the id of the start node + the id of the end node.
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Returns the "reversed" edge. A reversed edge opposite start and end nodes.
	 * @return A new RoadEdge with opposite start and end nodes.
	 */
	public RoadEdge getReverse() {
		RoadEdge reverse = new RoadEdge(endNode, startNode, strat);
		return reverse;
	}
	
	/**
	 * returns whether or not a specified node is one of the nodes connected to this edge.
	 * @param n The specified node
	 * @return Whether this edge connects to the specified node.
	 */
	@Override
	public boolean hasNode(GraphNode n) {
		return startNode.equals(n) || endNode.equals(n);
	}
	
	/**
	 * I don't know if this works. I believe that because every node has a unique id that
	 * for every possible pair of nodes, concatID should be unique.
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	/**
	 * Checks if both two edges are equals.
	 * @return True if the objects share the same ID, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if(other == null) { return false; }
		if(other == this) { return true; }
		if(other.getClass() != this.getClass()) { return false; }
		RoadEdge o = (RoadEdge) other;
		return id.equals(o.id);
	}

}
