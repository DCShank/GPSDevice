import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

/**
 * A panel that displays a map.
 * Default values set for the 11790 area.
 * 
 * Has pan functionality on click+drag
 * Has zoom functionality on mouse wheel
 * 
 * @author David Shank
 *
 */
public class MapPanel extends JPanel {

	private static final int DEFAULT_ZOOM = 4000;
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;
	/** These values are fairly arbitrary. Selected so that the important part of the map is centered to start with. */
	private static final double DEFAULT_LAT = 40.92;
	private static final double DEFAULT_LON = -73.15;
	/** Multiplier used to determine how much to scale a degree by. @mutable */
	private double zoom;
	private Map map;
	/** 
	 * A list of "selected ways". These are ways that should be displayed differently because
	 * they have been selected in some way. For this demo selected ways are those selected in the list.
	 */
	private List<String> selectedWays;
	private MouseAdapter adapter;
	/** Latitude displayed at the center of the MapPanel. */
	private double centerLat;
	/** Longitude displayed at the center of the MapPanel. */
	private double centerLon;

	/**
	 * Initializes the MapPanel with default values and some map.
	 * @param m
	 */
	public MapPanel(Map m) {
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		
		map = m;
		selectedWays = new ArrayList<String>();
		
		zoom = DEFAULT_ZOOM;
		centerLat = DEFAULT_LAT;
		centerLon = DEFAULT_LON;
		
		initializeMouse();
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
		this.addMouseWheelListener(adapter);
	}

	/**
	 * Initializes the mouse adapter used by this map panel.
	 * I think that the better way to do this might be to have
	 * an inner class that extends mouse adapter.
	 */
	private void initializeMouse() {
		adapter = new MouseAdapter() {
			/** used for panning. */
			private int x;
			private int y;
			/**
			 * Sets initial points for the pan operation.
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				x = e.getX();
				y = e.getY();
			}
			/**
			 * Pans the map when it is dragged with a mouse.
			 */
			@Override
			public void mouseDragged(MouseEvent e) {
				centerLat -= pixToLat(e.getY() - y);
				centerLon -= pixToLon(e.getX() - x, e.getY() - y);
				y = e.getY();
				x = e.getX();
				repaint();
			}
			/**
			 * Zooms in the map when the mouse wheel is applied.
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// Only change the zoom if you're not too close or too far.
				if((zoom < 3000000 || e.getWheelRotation() > 0) && (zoom > 150 || e.getWheelRotation() < 0))
				zoom +=  e.getWheelRotation() * zoom/-15;
				repaint();
			}
		};
	}
	
	public void setSelectedWays(List<String> array) {
		selectedWays = array;
	}
	
	/**
	 * Draws every way in the map.
	 * Draws selected ways in thicker red lines.
	 * 
	 * @param g
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Iterator<Way> it = map.getWayIt();
		while (it.hasNext()) {
			drawWay(it.next(), g);
		}
		
		Color currentColor = g.getColor();
		g.setColor(Color.RED);
		
		for(Object o : selectedWays) {
			drawWay(map.getWay((String)o), g);
		}
		
		g.setColor(currentColor);
	}

	/**
	 * Returns the number of pixels that represent a number of degrees latitude.
	 * Reverses the sign on the assumption that we are in the northern
	 * hemisphere and want to display from top to bottom.
	 * 
	 * @param lat
	 *            The number of degrees latitude.
	 * @return The number of y pixels that represent the number of degrees.
	 */
	private int latToPix(double lat) {
		return (int) (zoom * lat * -1);
	}

	/**
	 * Returns the number of pixels that represent a number of degrees longitude
	 * represents.
	 * 
	 * @param lat
	 *            The number of degrees latitude.
	 * @param lon
	 *            the number of degrees longitude.
	 * @return
	 */
	private int lonToPix(double lat, double lon) {
		return (int) (lon * Math.cos(Math.toRadians(lat)) *  zoom);
	}

	/**
	 * Converts a certain number of pixels to some latitude.
	 * 
	 * @param y
	 *            The number of vertical pixels.
	 * @return The number of degrees latitude.
	 */
	private double pixToLat(int y) {
		return (double) y / zoom / -1.0;
	}

	/**
	 * Converts some number of pixels x and y to longitude.
	 * 
	 * @param x
	 *            The number of horizontal pixels.
	 * @param y
	 *            The number of vertical pixels.
	 * @return The number of degrees longitude.
	 */
	private double pixToLon(int x, int y) {
		return (double) x / zoom / Math.cos(Math.toRadians(centerLat + pixToLat(y)));
	}
	
	/**
	 * Finds the X position on screen of a node.
	 * @param n The node to find the position for
	 * @return The number of pixels away from the left side of the panel
	 */
	private int getXPos(Node n) {
		return lonToPix(n.getLat(), n.getLon() - centerLon) + getWidth() / 2;
	}
	
	/**
	 * Finds the Y position on screen of a node.
	 * @param n The node to find the position for
	 * @return The number of pixels below the top of the panel.
	 */
	private int getYPos(Node n) {
		return latToPix(n.getLat() - centerLat) + getHeight() / 2;
	}

	/**
	 * Draws a way on the screen.
	 * 
	 * @param way
	 *            The way to be drawn.
	 * @param g
	 *            Graphics object.
	 */
	private void drawWay(Way way, Graphics g) {
		Iterator<Node> it = way.getNodeIt();
		Node prevNode = it.next();
		while (it.hasNext()) {
			Node nextNode = it.next();
			g.drawLine(getXPos(prevNode), getYPos(prevNode), getXPos(nextNode), getYPos(nextNode));
			prevNode = nextNode;
		}
	}
}