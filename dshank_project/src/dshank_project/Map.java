package dshank_project;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A representation of the data in an OSM map.
 * Takes an OSM file and uses a parser to extract the data, then stores the data.
 * @author david
 *
 */
public class Map {
	/** Maps id's to nodes */
	private HashMap<String,Node> nodes;
	/** maps id's to ways */
	private HashMap<String,Way> ways;
	/** maps names to ways */
	private HashMap<String,Way> namedWays;
	/** maps id's to relations */
	private HashMap<String,Relation> relations;
	
	/**
	 * Takes a filename and gives it to the parser to extract data from the file.
	 * Fills the fields with extracted data.
	 * @param fileName
	 */
	public Map(File fileName) {
		
	}
	
	/**
	 * Alternative constructor that takes the HashMaps for its fields as parameters.
	 * @param nodes The map from ids to Nodes
	 * @param ways The map from ids to Ways
	 * @param namedWays The map from names to Ways
	 * @param relations The map from ids to Relations
	 */
	public Map(HashMap<String,Node> nodes, HashMap<String,Way> ways, HashMap<String,Way> namedWays,
				HashMap<String,Relation> relations) {
		
	}
	
	/**
	 * Gives an iterator over the ways of the Map.
	 * @return An Iterator<Way> of the ways in the Map.
	 */
	public Iterator<Way> getWayIt() {
		return null;
	}
	
	/**
	 * Returns whether or not the specified position is "near to" a way.
	 * what near to means will be decided later I suppose.
	 * @param lat The lat position to check against.
	 * @param lon The lon position to check against.
	 * @return True if the position is near to a way, false otherwise.
	 */
	public boolean nearWay(double lat, double lon) {
		return (Boolean) null;
	}

}
