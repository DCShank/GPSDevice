package directions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	}
	
	/**
	 * Sets the start node to a specified node
	 * @param n the node to set start to
	 */
	public void setStartNode(GraphNode n) {
		startNode = n;
	}
	
	/**
	 * Sets the end node to a specified node
	 * @param n the node to set end to
	 */
	public void setEndNode(GraphNode n) {
		endNode = n;
	}
	
	/**
	 * Returns the start node.
	 * @return the start node.
	 */
	public GraphNode getStartNode() {
		return startNode;
	}
	
	/**
	 * Returns the end node.
	 * @return the end node.
	 */
	public GraphNode getEndNode() {
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
		return new ArrayList();
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
	private List<GraphEdge> calcDir() {
		ArrayList<GraphNode> nodeIndices = new ArrayList<GraphNode>();
		ArrayList<GraphEdge> predEdges = new ArrayList<GraphEdge>();
		ArrayList<GraphNode> visited = new ArrayList<GraphNode>();
		ArrayList<Double> distances = new ArrayList<Double>();
		
		boolean startOnSegment = false;
		boolean endOnSegment = false;
		if(startNode.getSegmentIt().hasNext()) { startOnSegment = true; }
		if(endNode.getSegmentIt().hasNext()) { endOnSegment = true; }
		
	}
	
	/**
	 * Creates one or two new segments that go from the start node to the nearby
	 * nodes with segments.
	 * @precondition The start node must not have outgoing segments.
	 */
	private void splitStartSeg() {
		Iterator<GraphSegment> sIt = graph.getSegmentIterator();
		while(sIt.hasNext()) {
			GraphSegment s = sIt.next();
			if(s.hasNode(startNode)) {
				GraphSegment tempSeg = s.getPostSubsegment(startNode);
				tempSegments.add(tempSeg);
				graph.addSegment(tempSeg);
			}
		}
	}
	
	private void splitEndSegment() {
		Iterator<GraphSegment> sIt = graph.getSegmentIterator();
		while(sIt.hasNext()) {
			GraphSegment s = sIt.next();
			if(s.hasNode(endNode)) {
				GraphSegment tempSeg = s.getPreSubsegment(endNode);
				tempSegments.add(tempSeg);
				graph.addSegment(tempSeg);
			}
		}
	}
	
	private void clearTempSegments() {
		for(GraphSegment s : tempSegments) {
			graph.removeSegment(s);
		}
	}
	
	private List<GraphEdge> extractDirections(List<GraphNode> nodeIndices, List<GraphEdge> predEdges) {
		LinkedList<GraphEdge> dirList = new LinkedList<GraphEdge>();
		GraphNode currNode = endNode;
		while(currNode != startNode) {
			int currIndex = nodeIndices.indexOf(currNode);
			GraphEdge predEdge = predEdges.get(currIndex);
			dirList.addLast(predEdge);
			currNode = predEdge.getStartNode();
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
		return null;
	}

}
