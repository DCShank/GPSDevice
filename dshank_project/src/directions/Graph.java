package directions;

import java.util.Iterator;
import java.util.Set;

public interface Graph {
	/**
	 * Returns whether or not a GraphNode is within some circular wedge at some
	 * position on the graph.
	 * @param x The x coordinate of the base of the circular segment.
	 * @param y The y coordinate of the base of the circular segment.
	 * @param theta The angle, theta, of the circular segment.
	 * @param phi The angle from (north? x axis?) to the center of the circular segment.
	 * @param radius The radius of the circle.
	 * @param n The node being checked.
	 * @return True if the node is in the segment, false otherwise.
	 */
	public boolean inCircularWedge(double x, double y, double theta, double phi,
										double radius, GraphNode n);
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
	
	/**
	 * Returns the nearest node to a certain position.
	 * @param x The x coordinate to find a node near.
	 * @param y The y coordinate to find a node near.
	 * @return The nearest node to the position in the set of nodes provided.
	 */
	public GraphNode getNearNode(double x, double y);
	
}
