package directions;

import java.util.Iterator;

/**
 * I'm considering reworking the the entire graph system to using these as the
 * highest level of element, rather than just having edges and nodes being the
 * only available elements. For now I've added the interface on an experimental
 * basis to see what I would need to add.
 * 
 * Represents a Segment of a Graph.
 * A segment in this case means a section from a start node to an end node
 * without any intersections in between.
 * 
 * Segments are directed.
 * @author david
 *
 */
public interface GraphSegment {
	
	/**
	 * Returns the distance. 
	 * Should equal the entire distance of all the edges that make up the segment.
	 * @return The distance of the GraphSegment.
	 */
	public double getDistance();
	
	/**
	 * Returns whether or not a specified Node exists in the GraphSegment.
	 * @return True if the node is part of the GraphSegment, false otherwise.
	 */
	public boolean hasNode(GraphNode n);
	
	/**
	 * Returns the start node for the segment.
	 * @return Returns the start node for the segment.
	 */
	public GraphNode getStartNode();
	
	/**
	 * Returns the end node for the segment.
	 * @return Returns the end node for the segment.
	 */
	public GraphNode getEndNode();
	
	/**
	 * Returns an iterator over the GraphEdges of the GraphSegment.
	 * @return The iterator for the edges.
	 */
	public Iterator<GraphEdge> getEdgeIt();

}
