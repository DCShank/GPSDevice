package dshank_project;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;

/**
 * Class that displays map data to a swing panel.
 * @author david
 *
 */
public class MapPanel extends JPanel {
	/** The map data to be represented */
	private Map map;
	/** The latitude displayed in the center of the display */
	private double cenlat;
	/** the longitude displayed in the center of the display */
	private double cenLon;
	/** MouseAdapter that handles all mouse events */
	private MouseAdapter mouse;
	/** Strategy for converting to pixels from lat/lon and to lat/lon from pixels. */
	private ScaleStrategy scale;
	
	/**
	 * Constructor for this object that takes a map to be displayed as the parameter
	 * @param map The map to be displayed
	 */
	public MapPanel(Map map) {
		
	}
	
	/**
	 * Moves the center of the screen by the specified amounts lat and lon.
	 * @param lat Amount to move by in the lat direction
	 * @param lon Amount to move by in the lon direction
	 */
	public void pan(double lat, double lon) {
		
	}
	
	/**
	 * Sets the center of the panel to some specified latitude and longitude;
	 * @param lat The latitude to set center at.
	 * @param lon The longitude to set center at.
	 */
	public void setCenter(double lat, double lon) {
		
	}
	
	/**
	 * Draws the map.
	 */
	@Override
	public void paintComponent(Graphics g) {
		
	}
	
	/**
	 * Draws a given way. Primarily a helper method for paintComponent.
	 * @param way The way to be drawn.
	 */
	public void drawWay(Way way) {
		
	}
	
	/**
	 * Takes a given lat value and finds its relative position on the screen.
	 * @param lat The lat value to find the position of.
	 * @return The number of pixels below the top of the screen.
	 */
	public int latToScreen(double lat) {
		return (Integer) null;
	}
	
	/**
	 * Takes a given lon value and finds its relative position on the screen.
	 * @param lon The lon value to find the position of.
	 * @param lat The lat of that lon value.
	 * @return A value representing x pixels from the left side of the screen.
	 */
	public int lonToScreen(double lon, double lat) {
		return (Integer) null;
	}

}
