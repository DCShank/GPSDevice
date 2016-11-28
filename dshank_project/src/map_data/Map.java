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
public class Map implements Graph{
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
		edgeInit();
		segments = new HashSet();
		segmentInit();
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
	
	public Iterator<Way> getNonRoadIt() {
		return nonRoadWays.values().iterator();
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
		// This works somehow. Bad practice to leave in code found by trial and error but hey.
		// This case only happens at around 180 degrees.
		if(angleMin > angleMax) {
			return !((angleNode > angleMax) && (angleNode < angleMin));
		}
		return ((angleNode < angleMax) && (angleNode > angleMin));
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
				if((nn.getDegree() != 1 && w.isOneway()) || (nn.getDegree() != 2 && !w.isOneway())) {
					RoadSegment seg = new RoadSegment(sn, nn, len, nodes);
					sn.addGraphEdge(seg);
					nn.addGraphEdge(seg);
					segments.add(seg);
					if(!w.isOneway()) {
						nn.addGraphEdge(seg.getReverse());
						sn.addGraphEdge(seg.getReverse());
						segments.add(seg.getReverse());
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
	
	/**
	 * Adds a segment to the graph and the corresponding start and end nodes.
	 * @param seg The segment to be added.
	 */
	public void addSegment(GraphSegment seg) {
		GraphNode sn = seg.getStartNode();
		GraphNode en = seg.getEndNode();
		sn.addGraphEdge(seg);
		en.addGraphEdge(seg);
	}
	
	/**
	 * Removes a segment from the graph and from the corresponding nodes.
	 */
	public void removeSegment(GraphSegment seg) {
		if(segments.contains(seg)) {
			segments.remove(seg);
			seg.getStartNode().removeSegment(seg);
			seg.getEndNode().removeSegment(seg);
		}
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
	
	public Iterator<GraphSegment> getSegmentIterator() {
		return segments.iterator();
	}
	
	public int getNodeSize() {
		return nodes.size();
	}
	
	public Iterator<Node> getNodeIt() {
		return nodes.values().iterator();
	}
}
