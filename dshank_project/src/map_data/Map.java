package map_data;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import graph_interfaces.Graph;
import graph_interfaces.GraphEdge;
import graph_interfaces.GraphNode;
import graph_interfaces.GraphSegment;

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
	/** Maps id's to road ways. */
	private HashMap<String,Way> roadWays;
	/** An ordered list of ways, from least to most important. */
	private ArrayList<Way> prioritizedWays;
	private HashMap<String, Relation> relations;
	
	private Set<RoadSegment> segments = new HashSet<RoadSegment>();
	private Set<Node> roadNodes = new HashSet<Node>();
	private double lonMin, latMin, lonMax, latMax;
	/** The strategy used for finding distances over area. */
	private final static DistanceStrategy strat = new HaversineDistance();
	
	/**
	 * Constructor for the Graph
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
				HashMap<String,Node> nodes, HashMap<String,Way> ways,
				HashMap<String,Way> roadWays, HashMap<String, Relation> rels) {
		lonMin = minLon;
		latMin = minLat;
		lonMax = maxLon;
		latMax = maxLat;
		this.nodes = nodes;
		this.ways = ways;
		this.roadWays = roadWays;
		relations = rels;
		prioritizedWays = new ArrayList<Way>(ways.values());
		prioritizedWays.sort(new Comparator<Way>() {

			@Override
			public int compare(Way o1, Way o2) {
				return wayToPri(o1).compareTo(wayToPri(o2));
			}
			
		});
		edgeInit();
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
	 * Gives an iterator over the ways of the Graph.
	 * @return An Iterator<Way> of the ways in the Graph.
	 */
	public Iterator<Way> getWayIt() {
		return ways.values().iterator();
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
	 * @param phi The angle clockwise from north of the center of the wedge.
	 * @param radius The radius of the circular segment.
	 * @param n The node that's being checked.
	 * @return True if the node is in the circular segment, false otherwise.
	 */
	public boolean inCircularWedge(double lon, double lat, double theta, double phi,
										double radius, Node node) {
		if(strat.getDistance(lon, lat, node.getLon(), node.getLat()) > radius) { return false; }
		
		double angleMin = phi - theta/2;
		while(angleMin > 180) {
			angleMin -= 360;
		}
		// Angle min is now an angle between -180 and 180
		double angleMax = phi + theta/2;
		while(angleMax > 180) {
			angleMax -= 360;
		}
		// Angle max is now between -180 and 180
		// If angle min > angle max, then you need to be careful
		double angleNode = Math.toDegrees(Math.atan2(node.getLon()-lon, node.getLat()-lat));
		if(node.getLon() - lon < 0 && node.getLat() - lat < 0) {
			angleNode *= -1;
		}
		angleNode *= -1;
		if(angleMin > angleMax) {
			// If max < min, we're on the border of the third and fourth quadrants.
			angleMax += 360;
			if(angleNode < 0) {
				// We may need to do this
				angleNode += 360;
			}
		}
		return ((angleNode < angleMax) && (angleNode > angleMin));
	}
	
	/**
	 * Determines if a node is contained by a circle.
	 * @param lon Longitude of the circles center
	 * @param lat Latitude of the circles center
	 * @param radius Radius of th ecircle
	 * @param node The node to be checked
	 * @return True if the node is contained by the circle, false otherwise.
	 */
	public boolean inCircle(double lon, double lat, double radius, Node node) {
		if(strat.getDistance(lon, lat, node.getLon(), node.getLat()) > radius) { return false; }
		return true;
	}
	
	/**
	 * Method to help initialize all the RoadEdges and assign them to their nodes.
	 */
	private void edgeInit() {
		Iterator<Way> it = roadWays.values().iterator();
		while(it.hasNext()) {
			Way w = it.next();
			Iterator<Node> nIt = w.getNodeIt();
			Node currNode = nIt.next();
			Node prevNode = null;
			while(nIt.hasNext()) {
				prevNode = currNode;
				currNode = nIt.next();
				roadNodes.add(prevNode);
				roadNodes.add(currNode);
				RoadEdge e = new RoadEdge(prevNode, currNode, w.getName(), strat);
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
	 * Segments are created only on ways. The possible cases for segments are as follows:
	 * 		S = wayStart	 E = wayEnd 	I = intersection 	D = deadEnd
	 * 		S -> E, S -> I, S -> D, 
	 * 		I -> E, I -> I, I -> D.
	 * In conclusion, segments can go from a the start of a way or an intersection to the
	 * an intersection, or the end of a way, or a dead end.
	 * 
	 * Intersections can also occur when a one way street intersects a two way street, thus
	 * we need to be careful to check both in degree and out degree.
	 */
	private void segmentInit() {
		Iterator<Way> wayIt = roadWays.values().iterator();
		while(wayIt.hasNext()) {
			Way w = wayIt.next();
			Iterator<Node> nIt = w.getNodeIt();
			// Initialize the variables for segment creation
			Node sn = nIt.next();	
			Node pn = sn;
			Node nn = null;
			double len = 0;
			ArrayList<Node>nodes = new ArrayList<Node>();
			nodes.add(sn);
			// Iterate over all the nodes in the way, creating segments
			while(nIt.hasNext()) {
				nn = nIt.next();
				len += pn.getEdgeTo((GraphNode)nn).getLength();
				nodes.add(nn);
				// If we arrive at an intersection or dead end create a segment
				if(((nn.getOutDegree() != 1 || nn.getInDegree() != 1) && w.isOneway()) 
						|| ((nn.getOutDegree() != 2 || nn.getInDegree() != 2) && !w.isOneway()) || !nIt.hasNext()) {
					RoadSegment seg = new RoadSegment(sn, nn, len, w.getName(), nodes);
					addSegment(seg);
					// If the segment is two way create the reverse segment
					if(!w.isOneway())
						addSegment(seg.getReverse());
					// Reset the variables for the next segment.
					sn = nn;
					nodes = new ArrayList<Node>();
					nodes.add(sn);
					len = 0;
				}
				// The next node becomes the previous node for the next iteration of the loop.
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
		if(seg instanceof RoadSegment) {
			RoadSegment s = (RoadSegment) seg;
			GraphNode sn = seg.getStartNode();
			GraphNode en = seg.getEndNode();
			sn.addGraphEdge(seg);
			en.addGraphEdge(seg);
			segments.add(s);
		}
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
		return getNearNodeInRadius(lon, lat, -1);
	}
	
	/**
	 * Returns the nearest node to some point within a radius for a given node iterator.
	 * @param lon The longitude of the point to find a node near.
	 * @param lat The latitude of the point to find a node near.
	 * @param radius the radius of the circle to search in. -1 for no limit.
	 * @return The node nearest the position in the radius, or null if no such node exists.
	 */
	public Node getNearNodeInRadius(double lon, double lat, double radius) {
		Iterator<Node> it = roadNodes.iterator();
		Node rtrnNode = null;
		double dist = -1;
		while(it.hasNext()) {
			Node n = it.next();
			double testDist = strat.getDistance(lon, lat, n.getLon(), n.getLat());
			if((rtrnNode == null && (radius == -1 || testDist < radius)) || (rtrnNode != null && testDist < dist)) {
				rtrnNode = n;
				dist = testDist;
			}
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
		return new HashSet<GraphSegment>(segments).iterator();
	}
	
	/**
	 * Provides an iterator for the drivable road nodes of the graph.
	 * @return An iterator for the drivable nodes.
	 */
	public Iterator<GraphNode> getNodeIterator() {
		return new HashSet<GraphNode>(roadNodes).iterator();
	}
	
	public Iterator<Way> getPrioritizedWayIt() {
		return prioritizedWays.iterator();
	}
	/**
	 * Returns an integer priority for a given road type.
	 * I don't quite follow the osm guidelines for this. They seemed to have more variation
	 * than I desired.
	 * @param s The string representing the road type
	 * @return An integer value for the priority
	 */
	public Integer wayToPri(Way w) {
		String s = w.getRoadType();
		String b = w.getTagVal("boundary");
		String n = w.getTagVal("natural");
		if(b == null) { b = ""; }	// This is so I don't have to do null checks every line.
		if(n == null) { n = ""; }
		
		if(s.equals("motorway") || s.equals("motorway_link"))
			return 4;
		if(s.equals("trunk") || s.equals("trunk_link"))
			return 3;
		if(s.equals("primary") || s.equals("primary_link"))
			return 2;
		if(s.equals("tertiary") || s.equals("secondary") || s.equals("tertiary_link")
				|| s.equals("secondary_link") || s.equals("roundabout"))
			return 1;
		if(s.equals("residential") || s.equals("unclassified") || s.equals("service"))
			return 0;
		if(b.equals("administrative"))
			return -3;
		if(b.equals("reserve") || n.equals("wood"))
			return -1;
		if(n.equals("water") || w.getTagVal("waterway") != null)
			return -2;
		return -4;
	}
	
	public Iterator<Relation> getRelationsIt() {
		return relations.values().iterator();
	}
}
