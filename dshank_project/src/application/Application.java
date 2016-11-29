package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.starkeffect.highway.GPSDevice;
import com.starkeffect.highway.GPSEvent;
import com.starkeffect.highway.GPSListener;

import directions.Director;
import directions.Graph;
import directions.GraphEdge;
import directions.GraphNode;
import map_data.Map;
import map_data.Node;
import map_data.OSMParser;

public class Application extends JFrame implements GPSListener{

	/** Parses OSM data into a usable state. Used for making the Map */
	private OSMParser prsr;
	/** Map that contains all the map data for the current map */
	private Map map;
	/** Panel that displays map data for the current map. */
	private MapPanel mapPanel;
	/** Used for finding directions from one position to another on a graph. */
	private Director dir;
	/** Private list of the directions that are given to the map panel. */
	private List<GraphEdge> directions;
	/** Starks implementation of the GPS. Also provides real time position updates. */
	private GPSDevice gps;
	/** Label for displaying system relevant messages, such as found a route, without popups, */
	private JLabel messageDisplay;

	/**
	 * Constructor for the application which takes no argument.
	 */
	public Application() throws Exception {
		setTitle("Map Application");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Irritating
		setPreferredSize(new Dimension(MapPanel.DEFAULT_WIDTH, MapPanel.DEFAULT_HEIGHT));
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		createMenuBar();
		ButtonPanel buttons = new ButtonPanel();
		messageDisplay = new JLabel();
		messageDisplay.setHorizontalAlignment(JLabel.CENTER);
		messageDisplay.setText("Message display.");
		messageDisplay.setToolTipText("Displays system updates.");
		content.add(buttons, BorderLayout.WEST);
		content.add(messageDisplay, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}

	/**
	 * Initializes the application with an OSM file to use for data.
	 * 
	 * @param file
	 *            An OSM file
	 */
	public Application(File file) throws Exception {
		this();
		loadMap(file);
		;
	}

	/**
	 * Loads a given map file and displays it on the application frame.
	 * 
	 * @param file
	 *            The file to be loaded.
	 * @throws Exception
	 *             Throws an exception if the file can't be loaded. This causes
	 *             the file to stop being loaded and leaves the previous map
	 *             being displayed.
	 */
	public void loadMap(File file) throws Exception {
		prsr = new OSMParser(file);
		prsr.parse();
		map = prsr.getMap();
		// Clear away the old stark gps, dereference it as much as possible, and delete the old frame.
		if (gps != null) {
			gps.removeGPSListener(this);
			gps = null;
			Frame[] frames = getFrames();
			for(Frame f : frames) {
				if(!f.getTitle().equals("Map Application")) {
					f.dispose();
				}
			}
		}
		// Remove the old map panel if one exists.
		if (mapPanel != null) {
			remove(mapPanel);
		}
		mapPanel = new MapPanel(map);
		dir = new Director((Graph)map);
		gps = new GPSDevice(file.getAbsolutePath());
		gps.addGPSListener(this);
		getContentPane().add(mapPanel, BorderLayout.CENTER);
		pack();
	}

	/**
	 * Creates a menu bar for the application. Currently the only function of
	 * the menu bar is to load new OSM files.
	 */
	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		final JFileChooser fc = new JFileChooser();
		JMenuItem loadMap = new JMenuItem("Load Map");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OSM Map files", "osm");
		fc.setFileFilter(filter);
		// The action listener displays a file chooser dialog and lets display a
		// new file.
		loadMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int chooseVal = fc.showOpenDialog(Application.this);
				if (chooseVal == fc.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
//						loadMap(file);
						MapLoader task = new MapLoader(file);
						task.execute();
					} catch (Exception x) {
						JOptionPane.showMessageDialog(null, "Failed to load map.");
					}
				}
			}
		});
		fileMenu.add(loadMap);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * A panel that holds all the buttons used by the application. Currently
	 * holds select start and end nodes
	 * 
	 * In the future will have buttons for getting directions and activating the
	 * gps.
	 * 
	 * @author david
	 *
	 */
	class ButtonPanel extends JPanel {
		JButton selStart;
		JButton selEnd;
		JButton getDir;

		public ButtonPanel() {
			ActionListener buttonPanelListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GraphNode n = mapPanel.getSelectedNode();
					if (n != null && (e.getActionCommand().equals("start") || e.getActionCommand().equals("end"))) {
						SwingWorker<Object, Object> task = new NodeSetter(n, e.getActionCommand());
						task.execute();
					}
					if (n != null && e.getActionCommand().equals("directions")) {
						SwingWorker<List<GraphEdge>, Object> task = new DirectionFinder();
						task.execute();
					}
				}
			};

			selStart = new JButton("Select start");
			selStart.setActionCommand("start");
			selStart.addActionListener(buttonPanelListener);
			selEnd = new JButton("Select end");
			selEnd.setActionCommand("end");
			selEnd.addActionListener(buttonPanelListener);
			getDir = new JButton("Get directions");
			getDir.setActionCommand("directions");
			getDir.addActionListener(buttonPanelListener);
			this.setLayout(new GridLayout(0, 1));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this.add(selStart);
			this.add(selEnd);
			this.add(getDir);
		}
	}

	/**
	 * Main method for initialzing the program. Takes an OSM file as the
	 * argument.
	 * 
	 * @param args
	 *            OSM file to be displayed first.
	 * @throws Exception
	 *             Throws an exception if something goes wrong with the file.
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			new Application(new File(args[0]));
		} else {
			new Application();
		}
	}
	
	class NodeSetter extends SwingWorker<Object, Object> {
		GraphNode n;
		String startOrEnd;
		public NodeSetter(GraphNode n, String whichNode) {
			this.n = n;
			startOrEnd = whichNode;
			
		}
		@Override
		protected Object doInBackground() throws Exception {
			if(startOrEnd.equals("start")) {
				GraphNode oldStart = dir.getStartNode();
				if (oldStart != null) {
				mapPanel.removeHighlightedNode((Node) oldStart);
				}
				dir.setStartNode(n);
				mapPanel.addHighlightedNode((Node) n);
//				JOptionPane.showMessageDialog(null, "Start node selected");
				messageDisplay.setText("Start node selected.");
			}
			if(startOrEnd.equalsIgnoreCase("end")) {
				GraphNode oldEnd = dir.getEndNode();
				if (oldEnd != null) {
				mapPanel.removeHighlightedNode((Node) oldEnd);
				}
				dir.setEndNode(n);
				mapPanel.addHighlightedNode((Node) n);
//				JOptionPane.showMessageDialog(null, "End node selected");
				messageDisplay.setText("End node selected.");
			}
			return n;
		}
	}

	class DirectionFinder extends SwingWorker<List<GraphEdge>, Object> {
		
		public DirectionFinder() {
			messageDisplay.setText("Searching for route...");
		}

		@Override
		protected List<GraphEdge> doInBackground() throws Exception {
			return dir.getDirections();
		}
		
		@Override
		protected void done() {
			try {
				directions = get();
				mapPanel.setDirections(directions);
				if(directions == null) {
					messageDisplay.setText("No route exists.");;
				} else {
					messageDisplay.setText("Route found!");
				}
			} catch (Exception e) {
				messageDisplay.setText("An ERROR has occurred!");
			}
		}
	}
	
	class RouteChecker extends SwingWorker<List<GraphEdge>, Object> {
		GPSEvent event;
		List<GraphEdge> oldDir = directions;
		public RouteChecker(GPSEvent e) {
			event = e;
		}
		@Override
		protected List<GraphEdge> doInBackground() throws Exception {
			return dir.updateDirections(event.getLatitude(), event.getLongitude(), event.getHeading());
		}
		@Override
		protected void done() {
			try {
				directions = get();
				mapPanel.setDirections(directions);
				if(directions == null) {
					messageDisplay.setText("No route exists.");;
				} else if (!oldDir.equals(directions)){
					messageDisplay.setText("Route updated.");
				} else {
					messageDisplay.setText("On route to destination");
				}
			} catch (Exception e) {
				
			}
		}
		
	}
	
	class MapLoader extends SwingWorker<Map, Object> {

		private File file;
		public MapLoader(File f) {
			file = f;
			messageDisplay.setText("Loading map...");
		}
		@Override
		protected Map doInBackground() throws Exception {
			
			prsr = new OSMParser(file);
			prsr.parse();
			map = prsr.getMap();
			return map;
		}
		
		@Override
		protected void done() {
			if (gps != null) {
				gps.removeGPSListener(Application.this);
				gps = null;
				Frame[] frames = getFrames();
				for(Frame f : frames) {
					if(!f.getTitle().equals("Map Application")) {
						f.dispose();
					}
				}
			}
			if (mapPanel != null) {
				remove(mapPanel);
			}
			try {
				mapPanel = new MapPanel(get());
			} catch (Exception e) {
			}
			dir = new Director((Graph)map);
			gps = new GPSDevice(file.getAbsolutePath());
			gps.addGPSListener(Application.this);
			getContentPane().add(mapPanel, BorderLayout.CENTER);
			pack();
		}
	}

	@Override
	public void processEvent(GPSEvent e) {
		double lon = e.getLongitude();
		double lat = e.getLatitude();
		double newHead = e.getHeading() + 90;
		if(newHead < 0) {
			newHead = 360 + newHead;
		}
		mapPanel.setCenter(lon, lat);
		mapPanel.setIsDriving(true);
		RouteChecker task = new RouteChecker(e);
		task.execute();
	}
}
