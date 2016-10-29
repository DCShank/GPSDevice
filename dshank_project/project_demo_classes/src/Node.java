/**
 * Represents a node (location) on a map.
 * @author David Shank
 *
 */
public class Node{
    
    private String name;
    private String id;
    private double lat;
    private double lon;
    /** This seemed like it might be useful later...*/
    private boolean visible;
    
    public Node(String id, double latitude, double longitude) {
	this.id = id;;
	lat = latitude;
	lon = longitude;
    }
    
    /**
     * Returns the latitude for this node.
     * @return The latitude for this node as a double.
     */
    public double getLat() {
    	return lat;
    }
    /**
     * Returns the longitude for this node.
     * @return The longitude of the node as a double.
     */
    public double getLon() {
    	return lon;
    }
    
    @Override
    public String toString() {
    	String rtrnString = "[Latitude: " + lat + "] [Longitude: " + lon +"]";
    	return rtrnString;
    }
    
    /**
     * Override equals. Two nodes are equal if thier IDs match.
     * @param Node other The other node to compare to.
     * @return True if the IDs match, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
	if(this == other)
	    return true;
	if(other == null)
	    return false;
	if(getClass() != other.getClass())
	    return false;
	return getID().equals(((Node)other).getID());
    }
    
    @Override
    public int hashCode() {
	return id.hashCode();
    }
    
    public String getID() {
	return id;
    }

}
