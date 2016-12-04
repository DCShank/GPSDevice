package application;

/**
 * Interface for objects that want to process events from the map panel.
 * @author david
 *
 */
public interface MapPanelListener {
	
	/**
	 * Processes an event from the map panel.
	 * @param e The event to be processed.
	 */
	public void processEvent(MapPanelEvent e);

}
