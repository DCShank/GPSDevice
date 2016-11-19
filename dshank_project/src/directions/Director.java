package directions;

import java.util.ArrayList;
import java.util.List;

import map_data.Node;

public class Director {
	private final Graph g;
	/** A list of edges to follow to reach a destination */
	private List directions;
	/** A list of the directions to follow in a human readable form. */
	private String dirString;
	
	private Node startNode;
	
	private Node endNode;
	
	public Director(Graph g) {
		this.g = g;
	}
	
	/**
	 * Returns an ordered list of the directions from start to end.
	 * @param start The start node.
	 * @param end The end node.
	 * @return An ordered list of GraphEdges from start to end.
	 */
	public List getDirections(Node start, Node end) {
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
	 * Calculates a set of directions based on two input nodes.
	 * Also writes the directions to the instance variable.
	 * @param s The start node.
	 * @param e The end node.
	 * @return The list of directions for immediate use.
	 */
	private List calcDir(Node s, Node e) {
		return null;
	}

}
