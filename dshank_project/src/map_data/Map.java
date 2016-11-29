package map_data;

import java.io.File;

import directions.Graph;
import directions.GraphEdge;
import directions.GraphNode;
import directions.GraphSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A representation of the data in an OSM map.
 * Takes an OSM file and uses a parser to extract the data, then stores the data.
 * @author david
 *
 */
public class Map implements Graph {
	/** Maps id's to nodes */
	private HashMap<String,Node> nodes;
	/** maps id's to ways */
	private HashMap<String,Way> ways;
	/** maps names to ways */
	private HashMap<String,Way> namedWays;
	/** maps id's to relations */
	private HashMap<String,Way> roadWays;
	
	private HashMap<String,Way> nonRoadWays;
	
	private Set<GraphSegment> segments;
	
	private Set<GraphNode> roadNodes;
	
	private double lonMin, latMin, lonMax, latMax;
	/** The strategy used for finding distances over area. */
	private final static DistanceStrategy strat = new HaversineDistance();
	
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
	 * @param nonRoadWays 
	 */
	public Map(double minLon, double minLat, double maxLon, double maxLat,
				HashMap<String,Node> nodes, HashMap<String,Way> ways, HashMap<String,Way> namedWays,
				HashMap<String,Way> roadWays, HashMap<String,Way> nonRoadWays) {
		lonMin = minLon;
		latMin = minLat;
		lonMax = maxLon;
		latMax = maxLat;
		this.nodes = nodes;
		this.ways = ways;
		this.namedWays = namedWays;
		this.roadWays = roadWays;
		this.nonRoadWays = nonRoadWays;
		roadNodes = new HashSet<GraphNode>();
		edgeInit();
		segments = new HashSet();
		segmentInit();
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
	 * Gives an iterator over the ways of the Map.
	 * @return An Iterator<Way> of the ways in the Map.
	 */
	public Iterator<Way> getWayIt() {
		return ways.values().iterator();
	}
	
	/**
	 * Gives an iterator over the drivable road ways.
	 * @return An Iterator<Way> of the road ways in the map.
	 */
	public Iterator<Way> getRoadIt() {
		return roadWays.values().iterator();
	}
	
	/**
	 * Determines if a node is within a "circular wedge" with some start position,
	 * heading, angle, and radius. A circular wedge is a section of a circle from the center
	 * to the outside edge, with some angle from one side of the wedge to the other.
	 * 
	 * All angles are in degrees.
	 * 
	 * @param lon The initial longitude of the circular segment.
	 * @param lat The initial latitude of the circular segment.
	 * @param theta	 The angle Theta of the circular segment.
	 * @param phi The angle counterclockwise from east of the center of the wedge.
	 * @param radius The radius of the circular segment.
	 * @param n The node that's being checked.
	 * @return True if the node is in the circular segment, false otherwise.
	 */
	public boolean inCircularWedge(double lon, double lat, double theta, double phi,
										double radius, GraphNode node) {
		// Return false if the node is outside the possible range of the circular segment.
//		System.out.println(!(strat.getDistance(lon, lat, n.getLon(), n.getLat()) > radius));
//		System.out.println(n.getLon() + " " + n.getLat() + "\n");
		if(strat.getDistance(lon, lat, node.getLon(), node.getLat()) > radius) { return false; }
		
		double angleMin = phi - theta/2;
		while(angleMin > 180) {
			angleMin -= 360;
		}
		double angleMax = phi + theta/2;
		while(angleMax > 180) {
			angleMax -= 360;
		}
		double angleNode = Math.toDegrees(Math.atan2(node.getLat()-lat, node.getLon()-lon));
//		System.out.println(angleNode + " Angle Node");
//		System.out.println(angleMax + " Angle Max");
//		System.out.println(angleMin + " Angle Min");
		// This works somehow. Bad practice to leave in code found by trial and error but hey.
		// This case only happens at around 180 degrees.
		if(angleMin > angleMax) {
			return !((angleNode > angleMax) && (angleNode < angleMin));
		}
		return ((angleNode < angleMax) && (angleNode > angleMin));
	}
	
	public boolean inCircle(double lon, double lat, double radius, GraphNode node) {
		if(strat.getDistance(lon, lat, node.getLon(), node.getLat()) > radius) { return false; }
		return true;
	}
	
	/**
	 * Method to help initialize all the RoadEdges and assign them to their nodes.
	 */
	private void edgeInit() {
		Iterator<Way> it = getRoadIt();
		while(it.hasNext()) {
			Way w = it.next();
			Iterator<Node> nIt = w.getNodeIt();
			Node currNode = nIt.next();
			Node prevNode = null;
			while(nIt.hasNext()) {
				prevNode = currNode;
				currNode = nIt.next();
				roadNodes.add((GraphNode) prevNode);
				roadNodes.add((GraphNode) currNode);
				RoadEdge e = new RoadEdge(prevNode, currNode, strat);
//				System.out.println(e.getLength());
				prevNode.addGraphEdge((GraphEdge)e);
				if(!w.isOneway()) {
					currNode.addGraphEdge((GraphEdge)e.getReverse());
				}
			}
		}
	}
	
	/**
	 * Initializes the segments for this map.
	 */
	private void segmentInit() {
		Iterator<Way> wayIt = getRoadIt();
		while(wayIt.hasNext()) {
			Way w = wayIt.next();
			Iterator<Node> nIt = w.getNodeIt();
			Node sn = nIt.next();
			Node pn = sn;
			Node nn = null;
			double len = 0;
			ArrayList<Node>nodes = new ArrayList<Node>();
			nodes.add(sn);
			while(nIt.hasNext()) {
				nn = nIt.next();
				len += pn.getEdgeTo((GraphNode)nn).getLength();
				nodes.add(nn);
				if((nn.getDegree() != 1 && w.isOneway()) || (nn.getDegree() != 2 && !w.isOneway()) || !nIt.hasNext()) {
					RoadSegment seg = new RoadSegment(sn, nn, len, nodes);
					addSegment(seg);
					if(!w.isOneway()) {
						addSegment(seg.getReverse());
					}
					sn = nn;
					nodes = new ArrayList<Node>();
					nodes.add(sn);
					len = 0;
				}
				pn = nn;
			}
		}
	}
	
	// Methods required for the Graph interface.
	
	/**
	 * Adds a segment to the graph and the corresponding start and end nodes.
	 * @param seg The segment to be added.
	 */
	public void addSegment(GraphSegment seg) {
		GraphNode sn = seg.getStartNode();
		GraphNode en = seg.getEndNode();
		sn.addGraphEdge(seg);
		en.addGraphEdge(seg);
		segments.add(seg);
	}
	
	/**
	 * Removes a segment from the graph and from the corresponding nodes.
	 * @param seg The segment to be removed.
	 */
	public void removeSegment(GraphSegment seg) {
		GraphNode sn = seg.getStartNode();
		GraphNode en = seg.getEndNode();
		sn.removeSegment(seg);
		en.removeSegment(seg);
		segments.remove(seg);
	}
	
	/**
	 * Returns the nearest node to some point for a given node iterator.
	 * @param lon The longitude of the point to find a node near.
	 * @param lat The latitude of the point to find a node near.
	 * @return The node nearest to the point that is available in the way set.
	 */
	public GraphNode getNearNode(double lon, double lat) {
		Iterator<GraphNode> it = getNodeIterator();
		GraphNode rtrnNode = it.next();
		double dist = strat.getDistance(lon, lat, rtrnNode.getLon(), rtrnNode.getLat());
		while(it.hasNext()) {
			GraphNode n = it.next();
			double testDist = strat.getDistance(lon, lat, n.getLon(), n.getLat());
			if(testDist < dist) {
				rtrnNode = n;
				dist = testDist;
			}
		}
		return rtrnNode;
	}
	/**
	 * Returns the nearest node to some point for a given node iterator.
	 * @param lon The longitude of the point to find a node near.
	 * @param lat The latitude of the point to find a node near.
	 * @param radius the radius of the circle to search in
	 * @return The node nearest the position in the radius, or null if no such node exists.
	 */
	public GraphNode getNearNodeInRadius(double lon, double lat, double radius) {
		Iterator<GraphNode> it = getNodeIterator();
		GraphNode rtrnNode = it.next();
		double dist = strat.getDistance(lon, lat, rtrnNode.getLon(), rtrnNode.getLat());
		while(it.hasNext()) {
			GraphNode n = it.next();
			double testDist = strat.getDistance(lon, lat, n.getLon(), n.getLat());
			if(testDist < dist) {
				rtrnNode = n;
				dist = testDist;
			}
		}
		if(dist > radius) {
			return null;
		}
		return rtrnNode;
	}
	
	
	/**
	 * Returns the number of drivable nodes in the graph.
	 * @return int The number of nodes in roadNodes.
	 */
	public int getNodeSize() {
		return roadNodes.size();
	}
	
	/**
	 * Provides an iterator for the segments of the graph.
	 * @return An iterator over the segments of the graph.
	 */
	public Iterator<GraphSegment> getSegmentIterator() {
		return segments.iterator();
	}
	
	/**
	 * Provides an iterator for the drivable road nodes of the graph.
	 * @return An iterator for the drivable nodes.
	 */
	public Iterator<GraphNode> getNodeIterator() {
		return roadNodes.iterator();
	}
}
