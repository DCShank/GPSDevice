package map_data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import directions.GraphEdge;
import directions.GraphNode;
import directions.GraphSegment;

/**
 * A super edge that extends from one intersection to another.
 * An intersection extends from one a node with multiple outgoing edges to either
 * a node with more than one outgoing edge or no outgoing edges.
 * @author david
 *
 */
public class RoadSegment implements GraphSegment {
	
	private LinkedList<GraphEdge> edges;
	private final HashSet<Node> nodes;
	private final String id;
	private final Node startNode;
	private final Node endNode;
	private final double length;

	public RoadSegment(Node sn, Node en, double len, List<Node> nodes) {
		startNode = sn;
		endNode = en;
		length = len;
		this.nodes = new HashSet<Node>(nodes);
		edges = new LinkedList();
		Iterator<Node> nIt = nodes.iterator();
		Node prevNode = nIt.next();
		Node currNode = null;
		while(nIt.hasNext()) {
			currNode = nIt.next();
			edges.add( prevNode.getEdgeTo(currNode));
			prevNode = currNode;
		}
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
		LinkedList<Node> revList = new LinkedList<Node>();
		Iterator<GraphEdge> it = edges.iterator();
		GraphEdge initialEdge = it.next();
		revList.addFirst((Node) initialEdge.getStartNode());
		revList.addFirst((Node) initialEdge.getEndNode());
		while(it.hasNext()) {
			revList.addFirst((Node) it.next().getEndNode());
		}
		return new RoadSegment(endNode, startNode, length, revList);
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

	@Override
	public Iterator<GraphEdge> getEdgeIt() {
		return edges.iterator();
	}
}
