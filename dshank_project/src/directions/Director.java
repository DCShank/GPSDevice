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
		startNode = null;
		endNode = null;
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
		if(startNode != null && endNode != null) {
			System.out.println("Found somethin");
			return calcDir();
		}
		System.out.println("Found nothin");
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
	private List<GraphEdge> calcDir() {
		ArrayList<GraphNode> indexedNodes = new ArrayList<GraphNode>(graph.getNodeSize());
		ArrayList<GraphSegment> predSegs = new ArrayList<GraphSegment>(graph.getNodeSize());
		ArrayList<GraphNode> visited = new ArrayList<GraphNode>(graph.getNodeSize());
		ArrayList<Double> distances = new ArrayList<Double>(graph.getNodeSize());
		for(int i = 0; i < graph.getNodeSize(); i++) {
			predSegs.add(null);
			visited.add(null);
			distances.add(null);
		}
		indexedNodes.add(startNode);
		distances.set(0, 0.0);
		
		if(!startNode.getSegmentIt().hasNext()) { splitStartSegment(); }
		if(!endNode.getSegmentIt().hasNext()) { splitEndSegment(); }
		
		while(!visited.contains(endNode)) {
			// The index of the next node to visit
			int visitNext = leastDistIndex(visited, distances);
			if(visitNext == -1) {
				break;
			}
			// Iterator over the segments of the next node to visit.
			Iterator<GraphSegment> segIt = indexedNodes.get(visitNext).getSegmentIt();
			// Iterate for each segment
			while(segIt.hasNext()) {
				GraphSegment s = segIt.next();
				GraphNode nextNode = s.getEndNode();
				// If a node is visited, don't bother.
				if(!visited.contains(nextNode)) {
					// If a node hasn't been indexed add it to indexed and fill in the important data.
					if(!indexedNodes.contains(nextNode)) {
						// Index the new end node.
						indexedNodes.add(s.getEndNode());
						// Set the distance of the newly indexed node to the distance to it's predessor + the segment
						distances.set(indexedNodes.indexOf(nextNode), distances.get(visitNext) + s.getLength());
						// Set the predecessor segment.
						predSegs.set(indexedNodes.indexOf(nextNode), s);
					}
					int nextIndex = indexedNodes.indexOf(nextNode);
					double newDist =  distances.get(visitNext) + s.getLength();
					// If the new distance is less than the previously recorded data, record the new distance and
					// set the new predecessor edge.
					if(newDist < distances.get(nextIndex)) {
						distances.set(nextIndex, newDist);
						predSegs.set(nextIndex, s);
					}
				}
				
			}
			visited.set(visitNext, indexedNodes.get(visitNext));
		}
		
		directions = new LinkedList<GraphEdge>(extractDirections(indexedNodes, predSegs));
		clearTempSegments();
		return directions;
		
	}
	
	private int leastDistIndex(List<GraphNode> visited, List<Double> distances) {
		int rtrnIndex = -1;
		for(int i = 0; i < distances.size(); i++) {
			// While the following two conditionals could be compacted into one, this is more legible.
			if(visited.get(i) == null && distances.get(i) != null) {
				if((rtrnIndex == -1) || (distances.get(i) < distances.get(rtrnIndex))) {
					rtrnIndex = i;
				}
			}
		}
		return rtrnIndex;
	}
	
	/**
	 * Creates one or two new segments that go from the start node to the nearby
	 * nodes with segments.
	 * @precondition The start node must not have outgoing segments.
	 */
	private void splitStartSegment() {
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
	
	private List<GraphEdge> extractDirections(List<GraphNode> nodeIndices, List<GraphSegment> predSegs) {
		LinkedList<GraphSegment> dirSegList = new LinkedList<GraphSegment>();
		GraphNode currNode = endNode;
		while(currNode != startNode) {
			int currIndex = nodeIndices.indexOf(currNode);
			GraphSegment predSeg = predSegs.get(currIndex);
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
