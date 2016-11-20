package map_data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import directions.GraphEdge;
import directions.GraphNode;

/**
 * Represents a node in an OSM map.
 * 
 * Has a latitude, longitude, and ID.
 * @author david
 *
 */
public class Node implements GraphNode {
	private double lon;
	private double lat;
	private String id;
	/** 
	 * These edges are the edges with this node as the start node. Edges are
	 * directed.
	 */
	private Set<GraphEdge> edges;
	
	/**
	 * Constructs a node with the given position and id.
	 * 
	 * @param latitude The latitude of the node
	 * @param longitude The longitude of the node
	 * @param idString The string representing the nodes id
	 */
	public Node(double longitude, double latitude, String idString) {
		lat = latitude;
		lon = longitude;
		id = idString;
		edges = new HashSet<GraphEdge>();
	}
	
	/**
	 * Returns the Nodes latitude.
	 * @return latitude
	 */
	public double getLat() {
		return lat;
	}
	
	/**
	 * Returns the Nodes longitude
	 * @return longitude
	 */
	public double getLon() {
		return lon;
	}
	
	/**
	 * Returns the nodes ID
	 * @return string id
	 */
	public String getID() {
		return id;
	}
	
	public Iterator<GraphEdge> getEdgeIt() {
		return edges.iterator();
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

}
