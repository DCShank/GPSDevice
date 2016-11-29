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
 * A super edge that extends from one intersection to another, or to a dead end.
 * A segment extends from one node with more than one outgoing edge to another
 * node with more that one outgoing edge or no outgoing edges.
 * 
 * For two way streets, a segment extends from a node with more than two outgoing
 * edges to a node with more than two outgoing edges or one or fewer outgoing edges.
 * 
 * @author david
 */
public class RoadSegment implements GraphSegment {
	
	private LinkedList<GraphEdge> edges;
	private final HashSet<Node> nodes;
	private final String id;
	private final Node startNode;
	private final Node endNode;
	private double length;

	/**
	 * Initializes a RoadSegment with a start node, end node, length, and a list of nodes
	 * that are part of the segment.
	 * @param sn The start node.
	 * @param en The end node.
	 * @param len The length of the segment from start to finish.
	 * @param nodes The nodes that are in the segment.
	 */
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
	
	/**
	 * Alternative constructor that takes a list of edges rather than nodes and length.
	 * Calculates the length of the segment based on the lengths of the component edges.
	 * @param sn The start node.
	 * @param en The end node.
	 * @param edges The component edges that make up the segment.
	 */
	public RoadSegment(Node sn, Node en, List<GraphEdge> edges) {
		startNode = sn;
		endNode = en;
		length = 0;
		nodes = new HashSet<Node>();
		this.edges = new LinkedList(edges);
		for(GraphEdge e : edges) {
			length += e.getLength();
			nodes.add((Node) e.getStartNode());
			nodes.add((Node) e.getEndNode());
		}
		// This doesn't actually work because you can have two segments that go from a node to itself.
		id = startNode.getID() + endNode.getID();
	}
	
	/**
	 * Returns whether or not a node is in the segment.
	 * @return True if the segment contains the node, false otherwise.
	 * 
	 * CONSIDER RENAMING TO contains()
	 */
	@Override
	public boolean hasNode(GraphNode n) {
		return nodes.contains(n);
	}

	/**
	 * Returns the length of the segment from start to finish.
	 * @return The total double length of the segment.
	 */
	@Override
	public double getLength() {
		return length;
	}

	/**
	 * Returns the start node of the segment.
	 * @return The start node of the segment.
	 */
	@Override
	public GraphNode getStartNode() {
		return startNode;
	}

	/**
	 * Returns the end node of the segment.
	 * @return The end node of the segment.
	 */
	@Override
	public GraphNode getEndNode() {
		return endNode;
	}

	/**
	 * Returns the ID of the segment.
	 * 
	 * WARNING: Segment ID's are NOT unique. Because the graph is directed, two segments can have
	 * the same start and end node but go in opposite directions.
	 * 
	 * This can probably be removed.
	 */
	@Override
	public String getID() {
		return id;
	}
	
	/**
	 * Provides the reverse segment of this segment. The reversed segment has all its edges directions
	 * reversed and has its start and end nodes opposite.
	 * @return The reverse road segment to this road segment.
	 */
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
	
	/**
	 * Returns a sub segment that extends from the beginning of this segment to the specified end node.
	 * @param en The end of the new subsegment.
	 * @return A segment that goes from this segments start node to the specified end node.
	 */
	public GraphSegment getPreSubsegment(GraphNode en) {
		Iterator<GraphEdge> edgeIt = edges.iterator();
		GraphEdge currEdge = edgeIt.next();
		LinkedList<GraphEdge> newList = new LinkedList<GraphEdge>();
		newList.add(currEdge);
		while(currEdge.getEndNode() != null && currEdge.getEndNode() != en) {
			currEdge = edgeIt.next();
			newList.add(currEdge);
		}
		return new RoadSegment(startNode, (Node) en, newList);
	}
	
	/**
	 * Returns a sub segment that extends from the specified start node to the end of this segment.
	 * @param sn The start node of the new sub segment.
	 * @return A segment that goes from the specified start node to this segments end node.
	 */
	public GraphSegment getPostSubsegment(GraphNode sn) {
		Iterator<GraphEdge> edgeIt = edges.iterator();
		GraphEdge currEdge = edgeIt.next();
		LinkedList<GraphEdge> newList = new LinkedList<GraphEdge>();
		while(currEdge.getEndNode() != sn) { 
			currEdge = edgeIt.next(); 
			}
		while(currEdge.getEndNode() != endNode) {
			currEdge = edgeIt.next();
			newList.add(currEdge);
		}
		return new RoadSegment((Node) sn, endNode, newList);
	}

	@Override
	public Iterator<GraphEdge> getEdgeIt() {
		return edges.iterator();
	}
	
	/**
	 * While I dislike passing around these lists, it seems like the easiest way
	 * to do some things.
	 * 
	 * It essentially lets me skip the step where I reconstruct the list from an iterator.
	 */
	@Override
	public List<GraphEdge> getEdgeList() {
		return new LinkedList<GraphEdge>(edges);
	}

	@Override
	public int hashCode() {
		return edges.hashCode();
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

		return id.equals(o.id) && edges.equals(o.edges);
	}
	
	@Override
	public String toString() {
		String rtrnString = "";
		rtrnString += "Start Node: " + startNode.toString();
		rtrnString += "  End Node: " + endNode.toString();
		return rtrnString;
	}
}
