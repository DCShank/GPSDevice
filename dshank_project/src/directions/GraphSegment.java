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

}
