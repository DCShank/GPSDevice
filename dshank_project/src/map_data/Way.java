package map_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing a way in an OSM map.
 * 
 * Ways tend to be roads, geographic features, buildings, and pretty much anything
 * made up of lines.
 * 
 * @author david
 *
 */
public class Way {
	/** The Ways ID string */
	private String id;
	/** The list of nodes that the Way contains. */
	private ArrayList<Node> nodes;
	/** The empty string if the Way has no name */
	private String name;
	/** Boolean that declares if the road is one way. */
	private boolean oneway;
	private boolean isRoad;
	
	private HashMap<String, String> tags;
	
	/**
	 * Initializes the Way with the parameters given.
	 * @param idString The id for the Way
	 * @param nodeList The list of nodes for the Way
	 * @param wayName The Ways name if it has one
	 */
	public Way(String idString, String wayName, HashMap<String, String> tagMap, List<Node> nodeList, boolean oneway) {
		name = wayName;
		id = idString;
		nodes = new ArrayList<Node>(nodeList);
		this.oneway = oneway;
		tags = tagMap;
		String roadType = getRoadType();
		isRoad = !(roadType.isEmpty() || roadType.equals("footway") || roadType.equals("path"));
	}
	
	/**
	 * Initializes the Way with the parameters given. No name
	 * @param idString The id for the Way
	 * @param nodeList The list of nodes for the Way
	 */
	public Way(String idString, List<Node> nodeList) {
		name = "";
		id = idString;
		nodes = new ArrayList<Node>(nodeList);
	}
	
	/**
	 * Gives an iterator for the nodes in the Way.
	 * @return An Iterator<Node> of the Nodes in the Way.
	 */
	public Iterator<Node> getNodeIt() {
		return nodes.iterator();
	}
	
	/**
	 * Returns the string id of the Way.
	 * @return String id
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Returns the name of the Way.
	 * All ways are set to the empty string by default, so even unnamed ways
	 * have a return value.
	 * @return The name for named ways, empty string otherwise
	 */
	public String getName() {
		return name;
	}
    
    /**
     * Returns whether or not the way is named.
     * @return True if the way has a a name, false otherwise.
     */
    public boolean isNamed() {
    	return !name.isEmpty();
    }
	
	/**
	 * Returns whether or not the way is a road.
	 * There are a number of types of 'highway's that we don't want to count,
	 * such as paths and foot paths. I have isolated the roads types that should
	 * be included.
	 * @return True if the way is a road, false otherwise.
	 */
	public boolean isRoad() {
		return isRoad;
	}
	
	/**
	 * Returns whether or not the way is oneway.
	 * @return True if the way is oneway, false otherwise.
	 */
	public boolean isOneway() {
		return oneway;
	}
	
	/**
	 * Returns the node nearest to the specified coordinates on this way.
	 * @param lon The longitude of the point to search for a node near to
	 * @param lat The latitude of the point to search for a node near to
	 * @return
	 */
	public Node getNearestNode(double lon, double lat, DistanceStrategy strat) {
		Iterator<Node> it = nodes.iterator();
		Node rtrnNode = it.next();
		double dist = strat.getDistance(lon, lat, rtrnNode.getLon(), rtrnNode.getLat());
		while(it.hasNext()) {
			Node n = it.next();
			double testDist = strat.getDistance(lon, lat, n.getLon(), n.getLat());
			if(testDist < dist) {
				rtrnNode = n;
				dist = testDist;
			}
		}
		return rtrnNode;
	}
	
	/**
	 * Convenience method for getting the highway tag.
	 * @return The highway type of this way.
	 */
	public String getRoadType() {
		String rtrn = tags.get("highway");
		if(rtrn == null) {rtrn = ""; };
		return rtrn;
	}
	
	public String getTagVal(String tag) {
		return tags.get(tag);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null) { return false; }
		if(other == this) { return true;  }
		if(other.getClass() != this.getClass()) { return false; }
		Way o = (Way) other;
		return o.getID().equals(this.getID());
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
