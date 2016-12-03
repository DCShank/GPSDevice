package graph_interfaces;

import java.util.Iterator;
import java.util.Set;

/**
 * An interface that defines the behavior of an aggregator for the graph like objects in this project.
 * @author david
 *
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
