package directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import map_data.Node;

/**
 * SKELETON CLASS
 * 
 * A class that will produce directions from a start point to an end point.
 * Much easier to make mutable than otherwise because 
 * @author david
 *
 */
public class Director {
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
	
	public Director() {
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
		return null;
	}
	
	/**
	 * Removes the first element of the list. essentially representing following the directions.
	 */
	public void moveForward() {
		
	}
	
	/**
	 * Removes elements from the directions until you reach the selected node.
	 * @param n The node to progress to.
	 */
	public void moveForwardTo(GraphNode n) {
		
	}
	
	/**
	 * Produces a test string with the start and end nodes.
	 */
	@Override
	public String toString() {
		String rtrnString = "";
		if(startNode != null) {
			rtrnString += "[Start: " + startNode.getID() + "] ";
		}
		if(endNode != null) {
			rtrnString += "[End: " + endNode.getID() + "] ";
		}
		return rtrnString;
	}

}
