package map_data;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in an OSM map.
 * 
 * Has a latitude, longitude, and ID.
 * @author david
 *
 */
public class Node {
	private double lat;
	private double lon;
	private String id;
	private Set<GraphEdge> edges;
	
	/**
	 * Constructs a node with the given position and id.
	 * 
	 * @param latitude The latitude of the node
	 * @param longitude The longitude of the node
	 * @param idString The string representing the nodes id
	 */
	public Node(double latitude, double longitude, String idString) {
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
