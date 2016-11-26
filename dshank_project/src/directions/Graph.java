package directions;

import java.util.Set;

public interface Graph {
	/**
	 * Returns whether or not a GraphNode is within some circular segment at some
	 * position on the graph.
	 * @param x The x coordinate of the base of the circular segment.
	 * @param y The y coordinate of the base of the circular segment.
	 * @param theta The angle, theta, of the circular segment.
	 * @param phi The angle from (north? x axis?) to the center of the circular segment.
	 * @param radius The radius of the circle.
	 * @param n The node being checked.
	 * @return True if the node is in the segment, false otherwise.
	 */
	public boolean inCircularSegment(double x, double y, double theta, double phi,
										double radius, GraphNode n);
	
	public void addSegment(GraphSegment seg);
	
	public void removeSegment(GraphSegment seg);
	
//	/**
//	 * Returns a set of GraphNodes.
//	 * @return The set of GraphNodes
//	 */
//	public Set getGraphNodes();

}
