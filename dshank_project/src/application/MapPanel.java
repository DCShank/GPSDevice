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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import graph_interfaces.GraphEdge;
import graph_interfaces.GraphNode;
import graph_interfaces.GraphSegment;
import map_data.DistanceStrategy;
import map_data.HaversineDistance;
import map_data.Map;
import map_data.Node;
import map_data.Way;

/**
 * Class that displays map data to a swing panel.
 * @author david
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
	
	public static final int DEFAULT_WIDTH = 1420;
	public static final int DEFAULT_HEIGHT = 800;
	
	private static final int PIX_TO_CUR = 35;
	private double currentRad;
	
	public static final DistanceStrategy strat = new HaversineDistance();
	
	private Node selectedNode = null;
	private Node hoveredNode = null;
	private Node start = null;
	private Node end = null;
	
	private List<GraphEdge> directions;
	
	private HashSet<Node> highlightedNodes;
	
	private HashSet<Node> tempNodes;
	
	private double testHeading = 0;
	
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
		
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		initMouse();
		this.setBackground(Color.DARK_GRAY);
		this.addMouseListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseMotionListener(mouse);
		
		highlightedNodes = new HashSet<Node>();
		tempNodes = new HashSet<Node>();
	}	
	
	/**
	 * Initializes all the mouse functions.
	 */
	private void initMouse() {
		mouse = new MouseAdapter() {
			/** used for panning. */
			private int x;
			private int y;
			
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
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!trackPos) {
					zoomToPosition(e.getX(), e.getY(), -e.getWheelRotation());
				} else {
					scale.zoom(-e.getWheelRotation());
				}
				setCenter(cenLon, cenLat);
				currentRad = strat.getDistance(0, 0, scale.pixelsToLon(PIX_TO_CUR,PIX_TO_CUR),
						scale.pixelsToLat(PIX_TO_CUR));
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				double lat = screenToLat(e.getY());
				double lon = screenToLon(e.getX(), e.getY());
				Node n = map.getNearNodeInRadius(lon, lat, currentRad);
				if(e.getButton() == e.BUTTON1)
					start = n;
				if(e.getButton() == e.BUTTON3)
					end = n;
				updateListeners(false);
				
//				selectedNode = n;
				
				// Testing code
//				System.out.println(n.toString());
//				Iterator<GraphNode> it = map.getNodeIterator();
//				tempNodes = new HashSet<Node>();
//				while(it.hasNext()) {
//					GraphNode next = it.next();
//					if(map.inCircularWedge(screenToLon(e.getX(),e.getY()), screenToLat(e.getY()),
//							120, testHeading, 500, next)) {
//						tempNodes.add((Node) next);
//					}
//				}
//				testHeading += 10;
				
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
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		Iterator<Way> wayIt = map.getWayIt();
		while(wayIt.hasNext()) {
			Way w = wayIt.next();
			if(!w.isRoad()) {
				drawWay(w, g);
			} else {
				highlightWay(w, g);
			}
		}
		g.setColor(c);
		for(Node n : highlightedNodes) {
			drawNode(n, Color.BLUE, g);
		}
		for(Node n : tempNodes) {
			drawNode(n, Color.GREEN, g);
		}
		if(hoveredNode != null)
			drawNode(hoveredNode, Color.ORANGE, g);
		if(selectedNode != null)
			drawNode(selectedNode, Color.MAGENTA, g);
		if(directions != null)
			drawEdges(directions, Color.RED, g);
		if(start != null)
			drawNode(start, Color.PINK, g);
		if(end != null)
			drawNode(end, Color.RED, g);
		if(driverLon != null && driverLat != null) {
			g.setColor(Color.WHITE);
			g.fillOval(lonToScreen(driverLon, driverLat)-4, latToScreen(driverLat)-4, 9, 9);
			
		}
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
	
	private void drawEdges(List<GraphEdge> edges, Color c, Graphics g) {
		Color currColor = g.getColor();
		g.setColor(c);
		for(GraphEdge e : edges) {
			drawEdge(e, g);
		}
		g.setColor(currColor);
	}
	
	private void drawNode(Node n, Color c, Graphics g) {
		Color c2 = g.getColor();
		g.setColor(c);
		g.fillOval(lonToScreen(n.getLon(), n.getLat())-3, latToScreen(n.getLat())-3, 7, 7);
		g.setColor(c2);
	}
	
	public void setDirections(List<GraphEdge> edges) {
		directions = edges;
		repaint();
	}
	
	/**
	 * Highlights a given way.
	 * @param way The Way to be highlighted.
	 */
	public void highlightWay(Way way, Graphics g) {
		Color currentColor = g.getColor();
		g.setColor(Color.cyan);
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
	 * Pops the currently selected node and sets selectedNode to null.
	 * @return selectedNode.
	 */
	public GraphNode getSelectedNode() {
		GraphNode rtrnNode = selectedNode;
		return rtrnNode;
	}
	
	/**
	 * Adds a node to the set of highlighted nodes.
	 * @param n THe node to be highlighted.
	 */
	public void addHighlightedNode(Node n) {
		highlightedNodes.add(n);
	}
	
	/**
	 * Removes a node from the set of highlighted nodes.
	 * @param n The node to be unhighlighted.
	 */
	public void removeHighlightedNode(Node n) {
		highlightedNodes.remove(n);
	}
	
	public void setDriver(double lon, double lat) {
		driverLon = lon;
		driverLat = lat;
	}
	
	public void setTrackPos(boolean track) {
		trackPos = track;
	}
	
	public void setStart(Node s) {
		start = s;
	}
	public void setEnd(Node e) {
		end = e;
	}
	
	public void addListener(MapPanelListener l) {
		listeners.add(l);
	}
	
	private void updateListeners(boolean movedMap) {
		UpdateEvent e = new UpdateEvent();
		e.hasMoved = movedMap;
		for(MapPanelListener l : listeners) {
			l.processEvent(e);
		}
	}
	
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
}
