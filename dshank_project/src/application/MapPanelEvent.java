package application;

import map_data.Node;

/**
 * Interface for processing events from the mapPanel.
 * @author david
 *
 */
public interface MapPanelEvent {
	public Node getStartNode();
	
	public Node getEndNode();
	
	public boolean movedMap();

}
