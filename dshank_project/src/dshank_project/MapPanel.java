package dshank_project;

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
	/** MouseAdapter that handles all mouse events */
	private MouseAdapter mouse;
	/** Strategy for converting to pixels from lat/lon and to lat/lon from pixels. */
	private final ScaleStrategy scale = new MapScale();
	
	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 600;
	
	private HashSet<Way> highlightedWays;
	
	/**
	 * Constructor for this object that takes a map to be displayed as the parameter
	 * @param map The map to be displayed
	 */
	public MapPanel(Map map) {
		this.map = map;
		scale.initZoom(map.getLatMin(), map.getLatMax(), DEFAULT_HEIGHT);
		cenLat = (map.getLatMax()+map.getLatMin())/2.0;
		cenLon = (map.getLonMax()+map.getLonMin())/2.0;
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		initMouse();
		this.addMouseListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseMotionListener(mouse);
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
				pan(scale.pixelsToLon(x-e.getX(),
						scale.latToPixels(cenLat)-getHeight()/2), scale.pixelsToLat(y-e.getY()));
				y = e.getY();
				x = e.getX();
				repaint();
			}
			/**
			 * Zooms in the map when the mouse wheel is applied.
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scale.zoom(-e.getWheelRotation());
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
		cenLon += lon;
		cenLat += lat;
	}
	
	/**
	 * Sets the center of the panel to some specified latitude and longitude;
	 * @param lat The latitude to set center at.
	 * @param lon The longitude to set center at.
	 */
	public void setCenter(double lon, double lat) {
		
	}
	
	public void setHighlightedWays(Set<Way> highlightedWays) {
		
	}
	
	/**
	 * Draws the map.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Iterator<Way> wayIt = map.getWayIt();
		Iterator<Way> roadWayIt = map.getRoadWayIt();
		while(wayIt.hasNext()) {
			drawWay(wayIt.next(), g);
		}
		highlightWays(roadWayIt, g);
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
	public void highlightWays(Iterator<Way> it, Graphics g) {
		Color currentColor = g.getColor();
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.BLUE);
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

}
