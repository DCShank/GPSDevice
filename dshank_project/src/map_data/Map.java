package map_data;

import java.io.File;

import directions.Graph;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A representation of the data in an OSM map.
 * Takes an OSM file and uses a parser to extract the data, then stores the data.
 * @author david
 *
 */
public class Map implements Graph{
	/** Maps id's to nodes */
	private HashMap<String,Node> nodes;
	/** maps id's to ways */
	private HashMap<String,Way> ways;
	/** maps names to ways */
	private HashMap<String,Way> namedWays;
	/** maps id's to relations */
	private HashMap<String,Way> roadWays;
	
	private double lonMin, latMin, lonMax, latMax;
	
	private final DistanceStrategy strat = new HaversineDistance();
	
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
	
	public Iterator<Way> getRoadIt() {
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
	 * Returns the minimum longitude value.
	 * @return The minimum longitude.
	 */
	public double getLonMin() {
		return lonMin;
	}
	
	/**
	 * Returns the maximum longitude value.
	 * @return The maximum longitude
	 */
	public double getLonMax() {
		return lonMax;
	}
	
	/**
	 * Determines if a node is within a circular segment with some start position,
	 * heading, angle, and radius.
	 * 
	 * I don't know if this works. Will need to be tested.
	 * 
	 * Also I don't know what direction and from what starting point the heading
	 * is going to be given so this is all essentially a sample of what the code
	 * should be like.
	 * 
	 * @param lon The initial longitude of the circular segment.
	 * @param lat The initial latitude of the circular segment.
	 * @param theta	 The angle Theta of the circular segment.
	 * @param phi The angle from north the the center of theta.
	 * @param radius The radius of the circular segment.
	 * @param n The node that's being checked.
	 * @return True if the node is in the circular segment, false otherwise.
	 */
	public boolean inCircularSegment(double lon, double lat, double theta, double phi,
										double radius, Node n) {
		// Return false if the node is outside the possible range of the circular segment.
		if(strat.getDistance(lon, lat, n.getLon(), n.getLat()) > radius) { return false; }
		double angleMin = phi - theta/2;
		double angleMax = phi + theta/2;
		// Should find the angle from North to the node...
		double angleNode = Math.toDegrees(Math.atan2(n.getLat()-lat, n.getLon()-lon));
		return ((angleNode < angleMax) || (angleNode > angleMin));
	}
	
	/**
	 * Returns the nearest node on a given set of ways.
	 * In this case the set of ways is parameterized as an iterator because map only
	 * really gives out iterators.
	 * @param lon The longitude of the point to find a node near.
	 * @param lat The latitude of the point to find a node near.
	 * @param wayIt The iterator for the set of ways that you want to search through.
	 * @return The node nearest to the point that is available in the way set.
	 */
	public Node getNearNode(double lon, double lat, Iterator<Way> wayIt) {
		Node rtrnNode = wayIt.next().getNearestNode(lon, lat, strat);
		double dist = strat.getDistance(lon, lat, rtrnNode.getLon(), rtrnNode.getLat());
		while(wayIt.hasNext()) {
			Way way = wayIt.next();
			Node n = way.getNearestNode(lon, lat, strat);
			double testDist = strat.getDistance(lon, lat, n.getLon(), n.getLat());
			if(testDist < dist) {
				rtrnNode = n;
				dist = testDist;
			}
		}
		return rtrnNode;
	}
}
