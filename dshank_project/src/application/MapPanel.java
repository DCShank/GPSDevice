package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

import map_data.Map;
import map_data.Node;
import map_data.Way;

/**
 * Class that displays map data to a swing panel.
 * @author david
 *
 */
public class MapPanel extends JPanel {
	/** The map data to be represented */
	private Map map;
	/** The latitude displayed in the center of the display */
	private double cenLat;
	/** the longitude displayed in the center of the display */
	private double cenLon;
	
	/** The number of pixels to the center latitdue of the display, from the equator. */
	private int cenLatPix;
	/** The number of pixels to the center longitude of the display, from the prime meridian. */
	private int cenLonPix;
	/** MouseAdapter that handles all mouse events */
	private MouseAdapter mouse;
	/** Strategy for converting to pixels from lat/lon and to lat/lon from pixels. */
	private final ScaleStrategy scale = new MapScale();
	
	public static final int DEFAULT_WIDTH = 1200;
	public static final int DEFAULT_HEIGHT = 800;
	
	private Node selectedNode = null;
	
	private HashSet<Node> highlightedNodes;
	private HashSet<Way> highlightedWays;
	
	/**
	 * Constructor for this object that takes a map to be displayed as the parameter
	 * @param map The map to be displayed
	 */
	public MapPanel(Map map) {
		this.map = map;
		scale.initZoom(map.getLatMin(), map.getLatMax(), DEFAULT_HEIGHT);
		cenLat = (map.getLatMax()+map.getLatMin())/2.0;
		cenLatPix = scale.latToPixels(cenLat);
		cenLon = (map.getLonMax()+map.getLonMin())/2.0;
		cenLonPix = scale.lonToPixels(cenLon, cenLat);
		
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		initMouse();
		this.addMouseListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseMotionListener(mouse);
		
		highlightedNodes = new HashSet();
	}	
	
	/**
	 * 
	 */
	private void initMouse() {
		mouse = new MouseAdapter() {
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
				pan(scale.pixelsToLon(x-e.getX(),screenToLat(y)), scale.pixelsToLat(y-e.getY()));
				y = e.getY();
				x = e.getX();
				repaint();
			}
			/**
			 * Zooms in the map when the mouse wheel is applied.
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoomToPosition(e.getX(), e.getY(), -e.getWheelRotation());
				setCenter(cenLon, cenLat);
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				double lat = screenToLat(e.getY());
				double lon = screenToLon(e.getX(), e.getY());
				Node n = map.getNearNode(lon, lat, map.getRoadIt());
				selectedNode = n;
				repaint();
			}
		};
	}
	
	/**
	 * Moves the center of the screen by the specified amounts lat and lon.
	 * @param lat Amount to move by in the lat direction
	 * @param lon Amount to move by in the lon direction
	 */
	public void pan(double lon, double lat) {
		setCenter(cenLon + lon, cenLat + lat);
	}
	
	/**
	 * Zooms the screen and pans so that the mouse is positioned on the same point,
	 * @param lonPix The pixel position on screen of the longitude
	 * @param latPix The pixel position on screen of the latitude
	 */
	public void zoomToPosition(int lonPix, int latPix, int direction) {
		double oldLat = screenToLat(latPix);
		double oldLon = screenToLon(lonPix, latPix);
		scale.zoom(direction);
		double newLat = screenToLat(latPix);
		double newLon = screenToLon(lonPix, latPix);
		double latChange = newLat-oldLat;
		double lonChange = newLon-oldLon;
		setCenter(cenLon - lonChange, cenLat - latChange);
		
	}
	
	/**
	 * Sets the center of the panel to some specified latitude and longitude;
	 * @param lat The latitude to set center at.
	 * @param lon The longitude to set center at.
	 */
	public void setCenter(double lon, double lat) {
		cenLon = lon;
		cenLat = lat;
		cenLonPix = scale.lonToPixels(lon, lat);
		cenLatPix = scale.latToPixels(lat);
	}
	
	public void setCenterPixels(int lonPix, int latPix) {
		cenLonPix = lonPix;
		cenLatPix = latPix;
		cenLat = scale.pixelsToLat(cenLatPix);
		cenLon = scale.pixelsToLon(cenLonPix, cenLatPix);
	}
	
	public void setHighlightedWays(Set<Way> highlightedWays) {
		
	}
	
	/**
	 * Draws the map.
	 * This includes drawing all ways, and highlighting ways of importance.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Iterator<Way> wayIt = map.getWayIt();
		Iterator<Way> roadWayIt = map.getRoadIt();
		while(wayIt.hasNext()) {
			drawWay(wayIt.next(), g);
		}
		highlightWays(roadWayIt,Color.MAGENTA, g);
		if(selectedNode != null) {
			Color c = g.getColor();
			Node n = selectedNode;
			g.setColor(Color.RED);
			g.fillOval(lonToScreen(n.getLon(), n.getLat())-3, latToScreen(n.getLat())-3, 7, 7);
			g.setColor(c);
		}
		for(Node n : highlightedNodes) {
			Color c = g.getColor();
			g.setColor(Color.BLUE);
			g.fillOval(lonToScreen(n.getLon(), n.getLat())-3, latToScreen(n.getLat())-3, 7, 7);
			g.setColor(c);
		}
	}
	
	/**
	 * Draws a given way. Primarily a helper method for paintComponent.
	 * @param way The way to be drawn.
	 */
	public void drawWay(Way way, Graphics g) {
		Iterator<Node> it = way.getNodeIt();
		Node curNode = it.next();
		Node prevNode = null;
		while(it.hasNext()) {
			prevNode = curNode;
			curNode = it.next();
			// I used much more verbose method names so this had to be split up here.
			int prevY, prevX, curY, curX;
			prevY = latToScreen(prevNode.getLat());
			prevX = lonToScreen(prevNode.getLon(), prevNode.getLat());
			curY = latToScreen(curNode.getLat());
			curX = lonToScreen(curNode.getLon(), curNode.getLat());
			
			g.drawLine(prevX, prevY, curX, curY);
		}
	}
	
	/**
	 * Highlights a given way.
	 * @param way The Way to be highlighted.
	 */
	public void highlightWay(Way way, Graphics g) {
		Color currentColor = g.getColor();
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.BLUE);
		drawWay(way, g);
		g.setColor(currentColor);
	}
	
	/**
	 * Highlights all ways for a given iterator.
	 * I implemented this becauaes it seemed like a waste to constantly
	 * get and reset the color and stroke for a larg set.
	 * @param it
	 * @param g
	 */
	public void highlightWays(Iterator<Way> it, Color c, Graphics g) {
		Color currentColor = g.getColor();
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(c);
		while(it.hasNext()) {
			drawWay(it.next(), g);
		}
		g.setColor(currentColor);
	}
	
	/**
	 * Takes a given lat value and finds its relative position on the screen.
	 * @param lat The lat value to find the position of.
	 * @return The number of pixels below the top of the screen.
	 */
	public int latToScreen(double lat) {
		return scale.latToPixels(lat-cenLat) + getHeight() / 2;
	}
	
	/**
	 * Takes a given lon value and finds its relative position on the screen.
	 * @param lon The lon value to find the position of.
	 * @param lat The lat of that lon value.
	 * @return A value representing x pixels from the left side of the screen.
	 */
	public int lonToScreen(double lon, double lat) {
		return scale.lonToPixels(lon-cenLon, lat) + getWidth() / 2;
	}
	
	/**
	 * Returns the approximate latitude value of a point on the screen..
	 * @param y The number of pixels below the top of the screen.
	 * @return The latitude of that y position on the screen.
	 */
	public double screenToLat(int y) {
		return cenLat-scale.pixelsToLat((getHeight()/2)-y);
	}
	
	/**
	 * Returns the longitude of an x-y position on the screen.
	 * @param x the position to the right of the screen
	 * @param y the position below the top of the screen
	 * @return the longitude at that position
	 */
	public double screenToLon(int x, int y) {
		return cenLon+scale.pixelsToLon(x-getWidth()/2, screenToLat(y));
	}
	
	/**
	 * Pops the currently selected node and sets selectedNode to null.
	 * @return selectedNode.
	 */
	public Node popSelectedNode() {
		Node rtrnNode = selectedNode;
		selectedNode = null;
		return rtrnNode;
	}
	
	/**
	 * Adds a node to the set of highlighted nodes.
	 * @param n THe node to be highlighted.
	 */
	public void addHilightedNode(Node n) {
		highlightedNodes.add(n);
	}
	
	/**
	 * Removes a node from the set of highlighted nodes.
	 * @param n The node to be unhighlighted.
	 */
	public void removeHighlightedNode(Node n) {
		highlightedNodes.remove(n);
	}

}
