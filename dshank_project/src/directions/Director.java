package directions;

import java.util.ArrayList;
import java.util.List;

import map_data.Node;

public class Director {
	/** A list of edges to follow to reach a destination */
	private List<GraphEdge> directions;
	/** A list of the directions to follow in a human readable form. */
	private String dirString;
	
	private GraphNode startNode;
	
	private GraphNode endNode;
	
	public Director(Graph g) {
		this.g = g;
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

}
