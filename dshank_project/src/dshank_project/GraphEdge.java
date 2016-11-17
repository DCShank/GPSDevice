package dshank_project;

/**
 * Represents an Edge on a graph with a length, Nodes, and adjacent edges to each Node.
 * @author david
 *
 */
public class GraphEdge {
	private final Node startNode;
	private final Node endNode;
	private final double length;
	
	/**
	 * Constructor for a graph edge.
	 * @param sn the startNode
	 * @param en the endNode
	 */
	GraphEdge(Node sn, Node en, DistanceStrategy strat) {
		startNode = sn;
		endNode = en;
		length = strat.getDistance(sn.getLon(), sn.getLat(), en.getLon(), en.getLat());
	}

}
