package map_data;

/**
 * Represents an Edge on a graph with a length, Nodes, and adjacent edges to each Node.
 * @author david
 *
 */
public class GraphEdge {
	private final Node startNode;
	private final Node endNode;
	private final double length;
	
	/**
	 * Constructor for a graph edge.
	 * @param sn the startNode
	 * @param en the endNode
	 */
	GraphEdge(Node sn, Node en, DistanceStrategy strat) {
		startNode = sn;
		endNode = en;
		length = strat.getDistance(sn.getLon(), sn.getLat(), en.getLon(), en.getLat());
	}
	
	/**
	 * returns whether or not a specified node is one of the nodes connected to this edge.
	 * @param n The specified node
	 * @return Whether this edge connects to the specified node.
	 */
	public boolean hasNode(Node n) {
		return startNode.equals(n) || endNode.equals(n);
	}
	
	/**
	 * returns the length of this edge
	 * @return the double value of the length between the two nodes.
	 */
	public double getLength() {
		return length;
	}
	
	/**
	 * returns the start node
	 * @return The start node
	 */
	public Node getStartNode() {
		return startNode;
	}
	
	/**
	 * returns the end node
	 * @return the end node
	 */
	public Node getEndNode() {
		return endNode;
	}
	
	/**
	 * I don't know if this works. I believe that because every node has a unique id that
	 * each for every possible pair of nodes, concatID should be unique.
	 */
	@Override
	public int hashCode() {
		String concatID = startNode.getID() + endNode.getID();
		return concatID.hashCode();
	}
	
	/**
	 * Checks if this GraphEdge equals another graph edge. Two GraphEdges are equals if
	 * they have the same pair of nodes, regardless of direction.
	 */
	@Override
	public boolean equals(Object other) {
		if(other == null) { return false; }
		if(other == this) { return true; }
		if(other.getClass() != this.getClass()) { return false; }
		GraphEdge o = (GraphEdge) other;
		return o.hasNode(startNode) && o.hasNode(endNode);
	}

}
