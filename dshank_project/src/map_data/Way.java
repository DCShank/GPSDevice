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
 * Ways have a map of their tags.
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
	 * Convenience method for getting the highway tag.
	 * @return The highway type of this way.
	 */
	public String getRoadType() {
		String rtrn = tags.get("highway");
		if(rtrn == null) {rtrn = ""; };
		return rtrn;
	}
	
	/**
	 * Returns the value for a given tag for this way.
	 * @param tag The tage to get the value of
	 * @return The value of the tag
	 */
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
