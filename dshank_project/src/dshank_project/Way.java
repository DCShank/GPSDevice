package dshank_project;

import java.util.ArrayList;
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
	/** The empty string if the Way has no road type */
	private String roadType;
	
	/**
	 * Initializes the Way with the parameters given.
	 * @param idString The id for the Way
	 * @param nodeList The list of nodes for the Way
	 * @param wayName The Ways name if it has one
	 */
	public Way(String idString, List<Node> nodeList, String wayName) {
		
	}
	
	/**
	 * Initializes the Way with the parameters given. No name
	 * @param idString The id for the Way
	 * @param nodeList The list of nodes for the Way
	 */
	public Way(String idString, List<Node> nodeList) {
		
	}
	
	/**
	 * Gives an iterator for the nodes in the Way.
	 * @return An Iterator<Node> of the Nodes in the Way.
	 */
	public Iterator<Node> getNodeIt() {
		return null;
	}
	
	/**
	 * Returns the string id of the Way.
	 * @return String id
	 */
	public String getID() {
		return null;
	}
	
	/**
	 * Returns the name of the Way.
	 * All ways are set to the empty string by default, so even unnamed ways
	 * have a return value.
	 * @return The name for named ways, empty string otherwise
	 */
	public String getName() {
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
