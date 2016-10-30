package dshank_project;

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
	
	/**
	 * Constructs a node with the given position and id.
	 * 
	 * @param latitude The latitude of the node
	 * @param longitude The longitude of the node
	 * @param idString The string representing the nodes id
	 */
	public Node(double latitude, double longitude, String idString) {
	}
	
	/**
	 * Returns the Nodes latitude.
	 * @return latitude
	 */
	public double getLat() {
		return (Double) null;
	}
	
	/**
	 * Returns the Nodes longitude
	 * @return longitude
	 */
	public double getLon() {
		return (Double) null;
	}
	
	/**
	 * Returns the nodes ID
	 * @return string id
	 */
	public String getID() {
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		return (Boolean) null;
	}
	
	@Override
	public int hashCode() {
		return (Integer) null;
	}

}
