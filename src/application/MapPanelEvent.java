package application;

import map_data.Node;

/**
 * Interface for events from the map panel.
 * Describes what information is provided to the listener.
 * @author david
 *
 */
public interface MapPanelEvent {
	public Node getStartNode();
	
	public Node getEndNode();
	
	public boolean movedMap();

}
