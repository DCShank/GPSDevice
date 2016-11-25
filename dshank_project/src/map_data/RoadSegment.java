package map_data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import directions.GraphEdge;
import directions.GraphNode;

/**
 * A super edge that extends from one intersection to another.
 * An intersection extends from one a node with multiple outgoing edges to either
 * a node with more than one outgoing edge or no outgoing edges.
 * @author david
 *
 */
public class RoadSegment implements GraphEdge {
	
	private final ArrayList<Node> nodeList;
	private final HashSet<Node> nodes;
	private final String id;
	private final Node startNode;
	private final Node endNode;
	private final double length;

	public RoadSegment(Node sn, Node en, double len, List<Node> nList) {
		startNode = sn;
		endNode = en;
		length = len;
		nodeList = new ArrayList<Node>(nList);
		nodes = new HashSet<Node>(nList);
		id = sn.getID() + en.getID();
	}
	
	@Override
	public boolean hasNode(GraphNode n) {
		return nodes.contains(n);
	}

	@Override
	public double getLength() {
		return length;
	}

	@Override
	public GraphNode getStartNode() {
		return startNode;
	}

	@Override
	public GraphNode getEndNode() {
		return endNode;
	}

	@Override
	public String getID() {
		return id;
	}
	
	public RoadSegment getReverse() {
		return new RoadSegment(endNode, startNode, length, nodeList);
	}

	@Override
	public int hashCode() {
		String concatID = startNode.getID() + endNode.getID();
		return concatID.hashCode();
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
		RoadSegment o = (RoadSegment) other;
		return id.equals(o.id);
	}
}
