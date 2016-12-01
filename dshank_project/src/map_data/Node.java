package map_data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import directions.GraphEdge;
import directions.GraphNode;
import directions.GraphSegment;

/**
 * Represents a node in an OSM map. Also implements GraphNode features.
 * Has a latitude, longitude, and ID.
 * Also has lists of it's incoming and outgoing edges and segments.
 * Has a degree. The degree measures the number of outgoing edges.
 * @author david
 *
 */
public class Node implements GraphNode {
	private final double lon;
	private final double lat;
	private final String id;
	private int degree = 0;
	private int outDegree = 0;
	/** 
	 * These edges are the edges with this node as the start node. Edges are
	 * directed.
	 */
	private Set<GraphEdge> edges;
	
	private Set<GraphEdge> incomingEdges;
	/**
	 * A set of segments.
	 */
	private Set<GraphSegment> segments;
	
	private Set<GraphSegment> incomingSegments;
	
	private HashMap<GraphNode, GraphEdge> toNodeEdges;
	
	/**
	 * Constructs a node with the given position and id.
	 * 
	 * @param latitude The latitude of the node
	 * @param longitude The longitude of the node
	 * @param idString The string representing the nodes id
	 */
	public Node(double longitude, double latitude, String idString) {
		lon = longitude;
		lat = latitude;
		id = idString;
		edges = new HashSet<GraphEdge>();
		incomingEdges = new HashSet<GraphEdge>();
		toNodeEdges = new HashMap<GraphNode, GraphEdge>();
		segments = new HashSet<GraphSegment>();
		incomingSegments = new HashSet<GraphSegment>();
	}
	
	/**
	 * Returns the Nodes longitude
	 * @return longitude
	 */
	public double getLon() {
		return lon;
	}
	
	/**
	 * Returns the Nodes latitude.
	 * @return latitude
	 */
	public double getLat() {
		return lat;
	}
	
	/**
	 * Returns the nodes ID
	 * @return string id
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Adds a graph edge to the node.
	 * Determines the nature of the edge adds it appropriately to the node.
	 * @param edge The edge to be added.
	 */
	public void addGraphEdge(GraphEdge edge) {
		if(edge instanceof RoadEdge && edge.getStartNode().equals(this)) {
			edges.add(edge);
			toNodeEdges.put(edge.getEndNode(), edge);
			degree += 1;
		}
		if(edge instanceof RoadSegment && edge.getStartNode().equals(this)) {
			segments.add((RoadSegment) edge);
		}
		if(edge instanceof RoadEdge && edge.getEndNode().equals(this)) {
			incomingEdges.add(edge);
		}
		if(edge instanceof RoadSegment && edge.getEndNode().equals(this)) {
			incomingSegments.add((RoadSegment)edge);
		}
	}
	
	/**
	 * Removes a segment from this nodes lists of attached segments.
	 * 
	 * THIS SHOULD PROBABLY BE REPLACED BY A removeEdge METHOD
	 */
	public void removeSegment(GraphSegment seg) {
		if(segments.contains(seg)) {
			segments.remove(seg);
		}
		if(incomingSegments.contains(seg)) {
			incomingSegments.remove(seg);
		}
	}
	
	// Iterators for all the edge and segment lists of the node.
	
	public Iterator<GraphEdge> getEdgeIt() {
		return edges.iterator();
	}
	
	public Iterator<GraphEdge> getIncomingEdgeIt() {
		return incomingEdges.iterator();
	}
	
	public Iterator<GraphSegment> getSegmentIt() {
		return segments.iterator();
	}
	
	public Iterator<GraphSegment> getIncomingSegmentIt() {
		return incomingSegments.iterator();
	}

	/**
	 * Returns the 'degree' of the node.
	 * This actually returns the 'Out degree' of the node. That is to say, the number of
	 * outgoing edges from the node.
	 * @return The degree (number of outgoing edges) of the node.
	 */
	public int getDegree() {
		return degree;
	}
	
	/**
	 * Returns the edge that leads to the specified node.
	 * @precondition The edge must exist
	 * @return The edge that takes this node to the specified node.
	 */
	public GraphEdge getEdgeTo(GraphNode n) {
		return toNodeEdges.get(n);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null) { return false; }
		if(other == this) { return true;  }
		if(other.getClass() != this.getClass()) { return false; }
		Node o = (Node) other;
		return o.getID().equals(this.getID());
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	/**
	 * Provides a human-readable representation of the data stored by the node. Useful for debugging.
	 * @return Returns a string representing the node.
	 */
	@Override
	public String toString() {
		String rtrn = "";
		rtrn += "[ID: " + id + ", Lat: " + lat + ", Lon: " + lon + ", Degree: " + degree + "]";
		return rtrn;
	}

	@Override
	public int getOutDegree() {
		return outDegree;
	}

}
