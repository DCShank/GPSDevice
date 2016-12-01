package directions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import map_data.Node;
import map_data.RoadSegment;

/**
 * SKELETON CLASS
 * 
 * A class that will produce directions from a start point to an end point.
 * Much easier to make mutable than otherwise because 
 * 
 * This class is starting to seem really bloated. This is one place I might
 * look to split off a smaller class. Originally I had planned to have a separate
 * GPSListener but this seemed convenient as I was working.
 * @author david
 *
 */
public class Director {
	
	public static final double DEFAULT_ANGLE = 120;
	public static final double DEFAULT_RADIUS = 40;
	private final Graph graph;
	/** A list of edges to follow to reach a destination */
	private List<GraphEdge> directions;
	/** A list of the nodes traversed by this graph. */
	private List<GraphNode> dirNodes;
	/** A set of nodes. Useful for checking if a node is in the directions. */
	private Set<GraphNode> nodeSet;
	/** A list of the directions to follow in a human readable form. */
	private String dirString;
	
	private GraphNode startNode;
	
	private GraphNode endNode;
	
	private Set<GraphSegment> tempSegments;
	
	public Director(Graph g) {
		graph = g;
		tempSegments = new HashSet<GraphSegment>();
		startNode = null;
		endNode = null;
	}
	
	/**
	 * Sets the start node to a specified node
	 * @param n the node to set start to
	 */
	public synchronized void setStartNode(GraphNode n) {
		startNode = n;
	}
	
	/**
	 * Sets the end node to a specified node
	 * @param n the node to set end to
	 */
	public synchronized void setEndNode(GraphNode n) {
		endNode = n;
	}
	
	/**
	 * Returns the start node.
	 * @return the start node.
	 */
	public synchronized GraphNode getStartNode() {
		return startNode;
	}
	
	/**
	 * Returns the end node.
	 * @return the end node.
	 */
	public synchronized GraphNode getEndNode() {
		return endNode;
	}
	
	/**
	 * Returns whether or not a certain node is in the route.
	 * @param n
	 * @return
	 */
	public boolean hasNode(GraphNode n) {
		return false;
	}
	
	/**
	 * Returns an ordered list of the directions from start to end.
	 * @return An ordered list of GraphEdges from start to end.
	 */
	public List<GraphEdge> getDirections() {
		if(startNode != null && endNode != null) {
			return calcDir();
		}
		return null;
	}
	
	/**
	 * Returns the human-readable, line separated, direction string.
	 * @return The string containing the directions.
	 */
	public String getDirString() {
		return null;
	}
	
	/**
	 * Calculates a set of directions based on the Directors start and end nodes.
	 * Also writes the directions to the instance variable.
	 * @return The list of directions for immediate use.
	 */
	private synchronized List<GraphEdge> calcDir() {
		HashMap<GraphNode, GraphSegment> predSegs = new HashMap<GraphNode, GraphSegment>(8192);
		HashSet<GraphNode> visited = new HashSet<GraphNode>(8192);
		HashMap<GraphNode, Double> distances = new HashMap<GraphNode, Double>(8192);
		Comparator<GraphNode> distComp = new Comparator<GraphNode>() {
			@Override
			public int compare(GraphNode o1, GraphNode o2) {
				if(distances.get(o1) < distances.get(o2)) { return -1; }
				if(distances.get(o1) > distances.get(o2)) { return 1; }
				return 0;
			}
		};
		PriorityQueue<GraphNode> distQueue = new PriorityQueue<GraphNode>(distComp);
		distances.put(startNode, 0.0);
		distQueue.add(startNode);
		
		if(!startNode.getSegmentIt().hasNext()) { splitStartSegment(); }
		if(!endNode.getSegmentIt().hasNext()) { splitEndSegment(); }
		
		while(!visited.contains(endNode)) {
			// The index of the next node to visit
			GraphNode visitNext = leastDistIndex(visited, distQueue);
			if(visitNext == null) {
				return null;
			}
			// Iterator over the segments of the next node to visit.
			Iterator<GraphSegment> segIt = visitNext.getSegmentIt();
			// Iterate for each segment
			while(segIt.hasNext()) {
				GraphSegment s = segIt.next();
				GraphNode nextNode = s.getEndNode();
				// If a node is visited, don't bother.
				if(!visited.contains(nextNode)) {
					
					double newDist =  distances.get(visitNext) + s.getLength();
					if(distances.get(nextNode) == null || distances.get(nextNode) > newDist) {
						distances.put(nextNode, newDist);
						predSegs.put(nextNode, s);
						distQueue.add(nextNode);
					}
				}
				
			}
			visited.add(visitNext);
		}
		
		directions = new LinkedList<GraphEdge>(extractDirections(predSegs));
		clearTempSegments();
		return directions;
		
	}
	
	private GraphNode leastDistIndex(Set<GraphNode> visited, PriorityQueue<GraphNode> distQueue) {
		GraphNode nextNode = distQueue.poll();
		while(visited.contains(nextNode) && nextNode != null) {
			nextNode = distQueue.poll();
		}
		return nextNode;
	}
	
	/**
	 * Creates one or two new segments that go from the start node to the nearby
	 * nodes with segments.
	 * @precondition The start node must not have outgoing segments.
	 */
	private void splitStartSegment() {
		Iterator<GraphSegment> sIt = graph.getSegmentIterator();
		Set<GraphSegment> tempSegs = new HashSet<GraphSegment>();
//		System.out.println("Split Start");
		while(sIt.hasNext()) {
			GraphSegment s = sIt.next();
			if(s.hasNode(startNode)) {
				GraphSegment tempSeg = s.getPostSubsegment(startNode);
				tempSegs.add(tempSeg);
//				System.out.println(tempSeg.toString());
			}
		}
		tempSegments.addAll(tempSegs);
		for(GraphSegment seg : tempSegs) {
			graph.addSegment(seg);
		}
	}
	
	private void splitEndSegment() {
		Iterator<GraphSegment> sIt = graph.getSegmentIterator();
		Set<GraphSegment> tempSegs = new HashSet<GraphSegment>();
//		System.out.println("Split End");
		while(sIt.hasNext()) {
			GraphSegment s = sIt.next();
			if(s.hasNode(endNode)) {
//				System.out.println(s.toString());
				GraphSegment tempSeg = s.getPreSubsegment(endNode);
				tempSegs.add(tempSeg);
//				System.out.println(tempSeg.toString());
			}
		}
		// This is necessary because you can't add things to graph while iterating.
		tempSegments.addAll(tempSegs);
		for(GraphSegment seg : tempSegs) {
			graph.addSegment(seg);
		}
	}
	
	private void clearTempSegments() {
		for(GraphSegment s : tempSegments) {
			graph.removeSegment(s);
		}
		tempSegments = new HashSet<GraphSegment>();
	}
	
	private List<GraphEdge> extractDirections(HashMap<GraphNode, GraphSegment> predSegs) {
		LinkedList<GraphSegment> dirSegList = new LinkedList<GraphSegment>();
		GraphNode currNode = endNode;
		while(currNode != startNode) {
			GraphSegment predSeg = predSegs.get(currNode);
			dirSegList.addLast(predSeg);
			currNode = predSeg.getStartNode();
		}
		LinkedList<GraphEdge> dirList = new LinkedList<GraphEdge>();
		for(GraphSegment s : dirSegList) {
			dirList.addAll(s.getEdgeList());
		}
		return dirList;
	}
	
	/**
	 * Determines whether someone is off course based on their lon, lat, and heading.
	 * @param lon The longitude of the position.
	 * @param lat The latitdue of the position
	 * @param heading The heading.
	 * @return
	 */
	private boolean onCourse(double lon, double lat, double heading) {
		Iterator<GraphEdge> eIt = directions.iterator();
		while(eIt.hasNext()) {
			GraphEdge e = eIt.next();
			GraphNode n = e.getEndNode();
			double len = e.getLength();
			if( graph.inCircularWedge(lon, lat, DEFAULT_ANGLE, heading, len * 1.2, n)
					|| (graph.inCircle(lon, lat, DEFAULT_RADIUS, n))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finds and returns the first node ahead of some position with heading.
	 * Returns null if there is no such node.
	 * @param lon The longitude position.
	 * @param lat The latitude position.
	 * @param heading The heading.
	 * @return The next node if such a node exists, null otherwise.
	 */
	private GraphNode nearNodeOnCourse(double lon, double lat, double heading) {
		return null;
	}
	
	/**
	 * Removes the first element of the list. essentially representing following
	 * the directions forward one edge.
	 */
	private void moveForward() {
		
	}
	
	/**
	 * Removes elements from the directions until you reach the selected node.
	 * @param n The node to progress to.
	 */
	private void moveForwardTo(GraphNode n) {
		
	}
	
	/**
	 * Updates and returns the directions based on some current position and heading.
	 * If the GPS indicates it is off course we recalculate new directions based
	 * on the nearest node.
	 * Otherwise we move forward along the route.
	 * @param lat The latitude of the position.
	 * @param lon the longitude of the position.
	 * @param heading The heading.
	 * @return The new directions.
	 */
	public List<GraphEdge> updateDirections(double lat, double lon, double heading) {
		Iterator<GraphEdge> eIt = directions.iterator();
		while(eIt.hasNext()) {
			GraphEdge e = eIt.next();
			if(onCourse(lon, lat, heading)) {
				return directions;
			}
		}
		startNode = graph.getNearNode(lon, lat);
		return calcDir();
	}
	
	public void clearDirections() {
		directions = null;
	}

}
