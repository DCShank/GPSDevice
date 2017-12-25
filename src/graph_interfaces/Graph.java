package graph_interfaces;

import java.util.Iterator;
import java.util.Set;

/**
 * An interface that defines the behavior of a graph.
 * A graph should be expected to have nodes, edges, and segments.
 * 
 * I'm not happy with the way this turned out. I should have made the graph class more important
 * and had the various graph object be immutable.
 * 
 * @author david
 */
public interface Graph {
	/**
	 * Adds a segment to the graph.
	 * @param seg The segment to be added.
	 */
	public void addSegment(GraphSegment seg);
	
	/**
	 * Removes a segment from the graph.
	 * @param seg The segment to be removed.
	 */
	public void removeSegment(GraphSegment seg);
	
	/**
	 * Provides an iterator for the segments of the graph.
	 * @return Returns an Iterator<GraphSegment> over the segments of the graph.
	 */
	public Iterator<GraphSegment> getSegmentIterator();
	
	/**
	 * Provides an iterator over the nodes of the graph.
	 * @return Returns an Iterator<GraphNode> over the nodes of the graph.
	 */
	public Iterator<GraphNode> getNodeIterator();
	
	/**
	 * Returns the number of nodes in the graph.
	 * @return The integer number of nodes in the graph.
	 */
	public int getNodeSize();

}
