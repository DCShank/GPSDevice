package map_data;

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
	private HashMap<String,Way> roadWays;
	
	private double lonMin, latMin, lonMax, latMax;
	
	/**
	 * Constructor for the Map
	 * @param minLon The minimum bound for longitude
	 * @param minLat The minimum bound for latitude
	 * @param maxLon The maximum bound for longitude
	 * @param maxLat The maximum bound for latitude
	 * @param nodes The map from ids to Nodes
	 * @param ways The map from ids to Ways
	 * @param namedWays The map from names to Ways
	 * @param roadWays A map of the ways that are roads.
	 */
	public Map(double minLon, double minLat, double maxLon, double maxLat,
				HashMap<String,Node> nodes, HashMap<String,Way> ways, HashMap<String,Way> namedWays,
				HashMap<String,Way> roadWays) {
		lonMin = minLon;
		latMin = minLat;
		lonMax = maxLon;
		latMax = maxLat;
		this.nodes = nodes;
		this.ways = ways;
		this.namedWays = namedWays;
		this.roadWays = roadWays;
	}
	
	/**
	 * Gives an iterator over the ways of the Map.
	 * @return An Iterator<Way> of the ways in the Map.
	 */
	public Iterator<Way> getWayIt() {
		return ways.values().iterator();
	}
	
	public Iterator<Way> getRoadWayIt() {
		return roadWays.values().iterator();
	}
	
	/**
	 * Returns the minimum latitude value.
	 * @return The minimum latitude.
	 */
	public double getLatMin() {
		return latMin;
	}
	
	/**
	 * Returns the maximum latitude value.
	 * @return The maximum latitude.
	 */
	public double getLatMax() {
		return latMax;
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