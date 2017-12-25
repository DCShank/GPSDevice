package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.StrokeBorder;

import graph_interfaces.GraphEdge;
import graph_interfaces.GraphNode;
import graph_interfaces.GraphSegment;
import map_data.DistanceStrategy;
import map_data.HaversineDistance;
import map_data.Map;
import map_data.Node;
import map_data.Relation;
import map_data.Way;

/**
 * Class that displays map data to a swing panel.
 * @author david
 */
public class MapPanel extends JPanel {

	/** Strategy for converting to pixels from lat/lon and to lat/lon from pixels. */
	private static final ScaleStrategy scale = new MapScale();
	/** Strategy for finding the distance between two points. */
	public static final DistanceStrategy strat = new HaversineDistance();
	
	// New colors!
	public static final Color LEAST_COLOR = new Color(220,220,120);
	public static final Color BOUND_COLOR = new Color(150,110,220);
	public static final Color BACKGROUND_COLOR = new Color(7,40,74);
	
	public static final int DEFAULT_WIDTH = 1420;
	public static final int DEFAULT_HEIGHT = 800;
	
	/** Strokes for drawing map elements with varying priority. */
	private BasicStroke least,low,med,high,most;
	/** Constant for scaling drawn items on the map. Should probably be moved to the scale object. */
	private static final double PRI_CONST = .000075;
	
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
	/** Acceptable radius in pixels to the cursor before we stof finding nearby nodes. */
	private static final int PIX_TO_CUR = 35;
	private double currentRad;
	
	private Node hoveredNode = null;
	private Node start = null;
	private Node end = null;
	
	private List<GraphEdge> directions;
	private Double driverLon = null;
	private Double driverLat = null;
	private boolean trackPos = false;
	
	private ArrayList<MapPanelListener> listeners = new ArrayList<MapPanelListener>();
	
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
		directions = null;
		currentRad = strat.getDistance(0, 0, scale.pixelsToLon(PIX_TO_CUR,PIX_TO_CUR),
				scale.pixelsToLat(PIX_TO_CUR));
		
		updateStrokes();
		
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		initMouse();
		this.setBackground(BACKGROUND_COLOR);
		this.addMouseListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseMotionListener(mouse);
	}	
	
	/**
	 * Initializes all the mouse functions.
	 */
	private void initMouse() {
		mouse = new MouseAdapter() {
			/** used for panning. */
			private int x;
			private int y;
			
			/**
			 * The nearest node is kept highlighted.
			 */
			@Override
			public void mouseMoved(MouseEvent e) {
				double lat = screenToLat(e.getY());
				double lon = screenToLon(e.getX(), e.getY());
				Node n = map.getNearNodeInRadius(lon, lat, currentRad);
//				GraphNode n = map.getNearNode(lon, lat);
				hoveredNode = n;
				repaint();
			}
			
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
			 * Exits tracking mode.
			 */
			@Override
			public void mouseDragged(MouseEvent e) {
				trackPos = false;	// If you dragged the mouse, take us out of tracking mode.
				updateListeners(true);
				pan(scale.pixelsToLon(x-e.getX(),screenToLat(y)), scale.pixelsToLat(y-e.getY()));
				y = e.getY();
				x = e.getX();
				repaint();
			}
			/**
			 * Zooms in the map when the mouse wheel is applied.
			 * If you're in tracking mode it zooms in towards the driver.
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!trackPos) {
					zoomToPosition(e.getX(), e.getY(), -e.getWheelRotation());
				} else {
					scale.zoom(-e.getWheelRotation());
				}
				updateStrokes();
				setCenter(cenLon, cenLat);
				currentRad = strat.getDistance(0, 0, scale.pixelsToLon(PIX_TO_CUR,PIX_TO_CUR),
						scale.pixelsToLat(PIX_TO_CUR));
				repaint();
			}
			
			/**
			 * Handles mouse clicks. If you left click it should set the nearest node to the
			 * start node and if you right click it should set the nearest node to the end node.
			 */
			@SuppressWarnings("static-access")
			@Override
			public void mouseClicked(MouseEvent e) {
//				double lat = screenToLat(e.getY());
//				double lon = screenToLon(e.getX(), e.getY());
//				System.out.println(lon);
//				System.out.println(lat);
//				Node n = map.getNearNodeInRadius(lon, lat, currentRad);
				Node n = hoveredNode; // One fewer calculation.
				if(e.getButton() == e.BUTTON1)
					start = n;
				if(e.getButton() == e.BUTTON3)
					end = n;
				updateListeners(false);
				repaint();
			}
		};
	}
	
	/**
	 * Moves the center of the screen by the specified amounts lat and lon.
	 * @param lat Amount to move by in the lat direction
	 * @param lon Amount to move by in the lon direction
	 */
	private void pan(double lon, double lat) {
		setCenter(cenLon + lon, cenLat + lat);
	}
	
	/**
	 * Zooms the screen and pans so that the mouse is positioned on the same point,
	 * @param lonPix The pixel position on screen of the longitude
	 * @param latPix The pixel position on screen of the latitude
	 */
	private void zoomToPosition(int lonPix, int latPix, int direction) {
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
	
	/**
	 * Draws the map. each way is drawn with a color and stroke based
	 * on its priority. Point objets are mostly drawn with a minimum size
	 * and that scales past a certain point.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		Color c = g.getColor();
		
		// Draws all the different ways based on their priority.
		Iterator<Way> wayIt = map.getPrioritizedWayIt();
		while(wayIt.hasNext()) {
			Way w = wayIt.next();
			int priority = map.wayToPri(w);
			switch (priority) {
			case -3: g2.setStroke(low);
			g.setColor(Color.PINK);
			break;
			case -2: g2.setStroke(low);
			g.setColor(Color.BLUE);
			break;
			case -1: g2.setStroke(low);
			g.setColor(Color.GREEN);
			break;
			case 0: g2.setStroke(least);
			g.setColor(LEAST_COLOR);
			break;
			case 1: g2.setStroke(low);
			g.setColor(Color.YELLOW);
			break;
			case 2: g2.setStroke(med);
			g.setColor(Color.YELLOW);
			break;
			case 3: g2.setStroke(high);
			g.setColor(Color.ORANGE);
			break;
			case 4: g2.setStroke(most);
			g.setColor(Color.RED);
			break;
			case 5: g2.setStroke(least);
			g.setColor(BOUND_COLOR);
			break;
			default: g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.LIGHT_GRAY);
			break;
			}
			drawWay(w, g);
		}
		g.setColor(BOUND_COLOR);
		g2.setStroke(low);
		Iterator<Relation> relIt = map.getRelationsIt();
		while(relIt.hasNext()) {
			Relation r = relIt.next();
			if(r.getTagVal("boundary") != null) {
				List<Way> relWays = r.getWays();
				for(Way way : relWays) {
					drawWay(way, g);
				}
			}
		}
		
		if(directions != null) {
			g2.setStroke(most);
			drawEdges(directions, Color.MAGENTA, g);
			g2.setStroke(new BasicStroke(1));
		}
		
		// For many point objects I wanted to draw them with a minimum size.
		if(start != null) {
			drawNodeFixedSize(start, Color.MAGENTA, 3, g);	// Minimum size if we're zoomed out
			drawNode(start, Color.MAGENTA, 3, g);			// Size if we're zoomed in.
		} if(end != null) {
			drawNodeFixedSize(end, Color.RED, 3, g);
			drawNode(end, Color.RED, 3, g);
		} if(hoveredNode != null) {
			drawNodeFixedSize(hoveredNode, Color.CYAN, 3, g);
			drawNode(hoveredNode, Color.CYAN, 3, g);
		} if(driverLon != null && driverLat != null) {
			drawPoint(driverLon, driverLat, Color.GREEN, 4, g);
			drawPosition(driverLon, driverLat, Color.GREEN, 5, g);
		}
		// Reset the color.
		g.setColor(c);
			
	}
	
	/**
	 * Draws a given way. Primarily a helper method for paintComponent.
	 * @param way The way to be drawn.
	 */
	private void drawWay(Way way, Graphics g) {
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
	 * Draws an edge from one point to another.
	 * @param e The edge to draw
	 * @param g Graphics object
	 */
	private void drawEdge(GraphEdge e, Graphics g) {
		Node prevNode = (Node) e.getStartNode();
		Node curNode = (Node) e.getEndNode();
		
		int prevY, prevX, curY, curX;
		prevY = latToScreen(prevNode.getLat());
		prevX = lonToScreen(prevNode.getLon(), prevNode.getLat());
		curY = latToScreen(curNode.getLat());
		curX = lonToScreen(curNode.getLon(), curNode.getLat());
		
		g.drawLine(prevX, prevY, curX, curY);
	}
	
	/**
	 * Draws a collection of edges.
	 * @param edges
	 * @param c
	 * @param g
	 */
	private void drawEdges(Collection<GraphEdge> edges, Color c, Graphics g) {
		Color currColor = g.getColor();
		g.setColor(c);
		for(GraphEdge e : edges) {
			drawEdge(e, g);
		}
		g.setColor(currColor);
	}
	
	/**
	 * Draws a node that scales with the zoom level.
	 * @param n The node to be drawn
	 * @param c The color of the node
	 * @param radius An arbitrary radius value to scale by
	 * @param g The graphics object
	 */
	private void drawNode(Node n, Color c, int radius, Graphics g) {
		Color c2 = g.getColor();
		g.setColor(c);
		int r = (int)(radius * PRI_CONST * scale.getZoom());
		g.fillOval(lonToScreen(n.getLon(), n.getLat())-r, latToScreen(n.getLat())-r, 2*r+1, 2*r+1);
		g.setColor(c2);
	}
	
	/**
	 * Draws a fixed size node
	 * @param n The node to be drawn
	 * @param c The color of the node
	 * @param r The radius in pixels of the circle to be drawn
	 * @param g The graphics object
	 */
	private void drawNodeFixedSize(Node n, Color c, int r, Graphics g) {
		Color c2 = g.getColor();
		g.setColor(c);
		g.fillOval(lonToScreen(n.getLon(), n.getLat())-r, latToScreen(n.getLat())-r, 2*r+1, 2*r+1);
		g.setColor(c2);
	}
	
	/**
	 * Draws a position that scales with zoom.
	 * @param lon longitude of the position
	 * @param lat latitude of the position
	 * @param c Color to draw the position
	 * @param radius Radius of the circle to draw
	 * @param g The giraffics object
	 */
	private void drawPosition(double lon, double lat, Color c, int radius, Graphics g) {
		Color co = g.getColor();
		g.setColor(c);
		int r = (int) (radius * PRI_CONST * scale.getZoom());
		g.fillOval(lonToScreen(lon, lat)-r, latToScreen(lat)-r, 2*r+1, 2*r+1);
		g.setColor(co);
	}
	
	/**
	 * Draws a fixed size circle at some point.
	 * @param lon The longitude of the point
	 * @param lat The latitude of the point
	 * @param c Color to draw the point
	 * @param r Radius of the fixed size circle to draw at the point
	 * @param g The graphics object
	 */
	private void drawPoint(double lon, double lat, Color c, int r, Graphics g) {
		Color oc = g.getColor();	// The original color
		g.setColor(c);
		g.fillOval(lonToScreen(lon-r, lat), latToScreen(lat-r), 2*r+1, 2*r+1);
		g.setColor(oc);
	}
	
	public void setDirections(List<GraphEdge> edges) {
		directions = edges;
		repaint();
	}
	
	/**
	 * Takes a given lat value and finds its relative position on the screen.
	 * @param lat The lat value to find the position of.
	 * @return The number of pixels below the top of the screen.
	 */
	private int latToScreen(double lat) {
		return scale.latToPixels(lat-cenLat) + getHeight() / 2;
	}
	
	/**
	 * Takes a given lon value and finds its relative position on the screen.
	 * @param lon The lon value to find the position of.
	 * @param lat The lat of that lon value.
	 * @return A value representing x pixels from the left side of the screen.
	 */
	private int lonToScreen(double lon, double lat) {
		return scale.lonToPixels(lon-cenLon, lat) + getWidth() / 2;
	}
	
	/**
	 * Returns the approximate latitude value of a point on the screen..
	 * @param y The number of pixels below the top of the screen.
	 * @return The latitude of that y position on the screen.
	 */
	private double screenToLat(int y) {
		return cenLat-scale.pixelsToLat((getHeight()/2)-y);
	}
	
	/**
	 * Returns the longitude of an x-y position on the screen.
	 * @param x the position to the right of the screen
	 * @param y the position below the top of the screen
	 * @return the longitude at that position
	 */
	private double screenToLon(int x, int y) {
		return cenLon+scale.pixelsToLon(x-getWidth()/2, screenToLat(y));
	}
	
	/**
	 * Sets the position of the driver in latitude and longitude
	 * @param lon Longitude of the driver
	 * @param lat Latitude of hte driver
	 */
	public void setDriver(double lon, double lat) {
		driverLon = lon;
		driverLat = lat;
	}
	
	/**
	 * Sets whether we are tracking position
	 * @param track True if we are tracking, false otherwise
	 */
	public void setTrackPos(boolean track) {
		trackPos = track;
	}
	
	/**
	 * Sets the start node
	 * @param s The start node
	 */
	public void setStart(Node s) {
		start = s;
	}
	/**
	 * Sets the end node
	 * @param e The end node
	 */
	public void setEnd(Node e) {
		end = e;
	}
	/**
	 * Adds an event listener for events from the map panel
	 * @param l The listener to be added
	 */
	public void addListener(MapPanelListener l) {
		listeners.add(l);
	}
	/**
	 * Updates the listeners with a new event
	 * @param movedMap True if the map has been panned, false otherwise
	 */
	private void updateListeners(boolean movedMap) {
		UpdateEvent e = new UpdateEvent();
		e.hasMoved = movedMap;
		for(MapPanelListener l : listeners) {
			l.processEvent(e);
		}
	}
	/**
	 * Class describing map panel events.
	 * @author david
	 *
	 */
	class UpdateEvent implements MapPanelEvent {
		
		private boolean hasMoved = false;

		@Override
		public Node getStartNode() {
			return start;
		}
		@Override
		public Node getEndNode() {
			return end;
		}
		@Override
		public boolean movedMap() {
			return hasMoved;
		}
		
	}
	
	/**
	 * Updates all the strokes for the different priority ways.
	 * 
	 * I do this because I'm unsure if java would handle making a new
	 * stroke every time I go through a case statement smartly.
	 * This way it only has to update them when I zoom in.
	 */
	private void updateStrokes() {
		float zoomVal = (float) (PRI_CONST * scale.getZoom());repaint();
		int cr = BasicStroke.CAP_ROUND;
		int jr = BasicStroke.JOIN_ROUND;
		most = new BasicStroke(3 * zoomVal, cr, jr);
		high = new BasicStroke((float)(2.25 * zoomVal), cr, jr);
		med = new BasicStroke((float)(1.75 * zoomVal), cr, jr);
		low = new BasicStroke((float)(1.25 * zoomVal), cr, jr);
		least = new BasicStroke(zoomVal, cr, jr);
	}
}
