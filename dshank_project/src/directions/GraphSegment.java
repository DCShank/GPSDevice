package directions;

import java.util.Iterator;

/**
 * A segment is essentially a super edge that extends from one intersection to another.
 * 
 * Segments are directed.
 * @author david
 *
 */
public interface GraphSegment extends GraphEdge {
	
	/**
	 * Returns an iterator over the GraphEdges of the GraphSegment.
	 * @return The iterator for the edges.
	 */
	public Iterator<GraphEdge> getEdgeIt();
	
	/**
	 * Returns the subsegment that goes from the start of this segment to the selected end node.
	 * @precondition The GraphSegment must contain the desired end node.
	 * @param en The node to end the subsegment on.
	 * @return	A Segment representing the edges from the start to the end node.
	 */
	public GraphSegment getPreSubsegment(GraphNode en);

	/**
	 * Returns the subsegment that goes from the selected start node to the end of the segmenet.
	 * @precondition The GraphSegment must contain the desired start node.
	 * @param en The node to start the subsegment on.
	 * @return	A Segment representing the edges from the start to the end node.
	 */
	public GraphSegment getPostSubsegment(GraphNode sn);

}
