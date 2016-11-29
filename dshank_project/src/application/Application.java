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
import javax.swing.JToggleButton;
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
	
	private boolean followGPS = false;
	private boolean inDriveMode = false;
	
	private GPSEvent prevEvent = null;
	
	private JButton getDir;
	private JToggleButton driveThere;

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
//		JButton getDir;
		JButton clearDir;
		JToggleButton trackPos;
//		JToggleButton driveThere;
		

		public ButtonPanel() {
			ActionListener buttonPanelListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GraphNode n = mapPanel.getSelectedNode();
					if ((e.getActionCommand().equals("start") || e.getActionCommand().equals("end"))) {
						SwingWorker<Object, Object> task = new NodeSetter(n, e.getActionCommand());
						task.execute();
					}
					if (e.getActionCommand().equals("directions")) {
						SwingWorker<List<GraphEdge>, Object> task = new DirectionFinder();
						task.execute();
					}
					if (e.getActionCommand().equals("track")) {
						followGPS = trackPos.isSelected();
					}
					if (e.getActionCommand().equals("clear")) {
						selStart.setEnabled(true);
						getDir.setEnabled(false);
						driveThere.setEnabled(true);
						driveThere.setSelected(false);
						inDriveMode = false;
						dir.clearDirections();
						mapPanel.setDirections(null);
						mapPanel.setStart(null);
						mapPanel.setEnd(null);
						mapPanel.repaint();
						messageDisplay.setText("All selections have been cleared.");
					}
					if (e.getActionCommand().equals("drive")) {
						if(driveThere.isSelected()) {
							if(prevEvent != null) {
								inDriveMode = true;
								dir.setStartNode(map.getNearNode(prevEvent.getLongitude(), prevEvent.getLatitude()));
								mapPanel.setStart(null);
								selStart.setEnabled(false);
								DirectionFinder task = new DirectionFinder();
								task.execute();
								messageDisplay.setText("Drive there mode enabled.");
							} else {
								messageDisplay.setText("No GPS coordinates to drive from!");
								driveThere.setSelected(false);
							}
						} else {
							inDriveMode = false;
							selStart.setEnabled(true);
							messageDisplay.setText("Drive there mode disabled.");
						}
					}
				}
			};
			// Init the sel start node button
			selStart = new JButton("Select start");
			selStart.setActionCommand("start");
			selStart.addActionListener(buttonPanelListener);
			// Init the sel end node button
			selEnd = new JButton("Select end");
			selEnd.setActionCommand("end");
			selEnd.addActionListener(buttonPanelListener);
			// Init the get directions button
			getDir = new JButton("Get directions");
			getDir.setActionCommand("directions");
			getDir.setEnabled(false);
			getDir.setToolTipText("Must have selected a start and end node to get directions");
			getDir.addActionListener(buttonPanelListener);
			// Init the clear directions button
			clearDir = new JButton("Clear directions");
			clearDir.setActionCommand("clear");
			clearDir.addActionListener(buttonPanelListener);
			// Init the track position button.
			trackPos = new JToggleButton("Track position", false);
			trackPos.setActionCommand("track");
			trackPos.addActionListener(buttonPanelListener);
			// Init the drive there button
			driveThere = new JToggleButton("Drive there", false);
			driveThere.setActionCommand("drive");
			driveThere.setEnabled(false);
			driveThere.setToolTipText("Must have selected an end node to get driving directions");
			driveThere.addActionListener(buttonPanelListener);
			// Set up the button panel.
			this.setLayout(new GridLayout(0, 1));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this.add(selStart);
			this.add(selEnd);
			this.add(getDir);
			this.add(clearDir);
			this.add(trackPos);
			this.add(driveThere);
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
			if(n != null) {
				if(startOrEnd.equals("start")) {
					dir.setStartNode(n);
					mapPanel.setStart(n);
					messageDisplay.setText("Start node set.");
				}
				if(startOrEnd.equals("end")) {
					dir.setEndNode(n);
					mapPanel.setEnd(n);
					driveThere.setEnabled(true);
					messageDisplay.setText("End node set.");
				}
				if(dir.getStartNode() != null && dir.getEndNode() != null) {
					getDir.setEnabled(true);
				}
				return n;
			} else {
				messageDisplay.setText("No node selected!");
				return n;
			}
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
			double newHead = event.getHeading() + 90;
			if(newHead < 0) {
				newHead = 360 + newHead;
			}
			return dir.updateDirections(event.getLatitude(), event.getLongitude(), newHead);
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
			gps = new GPSDevice(file.getAbsolutePath());
			gps.addGPSListener(Application.this);
			return map;
		}
		
		@Override
		protected void done() {
			if (mapPanel != null) {
				remove(mapPanel);
			}
			try {
				mapPanel = new MapPanel(get());
			} catch (Exception e) {
			}
			dir = new Director((Graph)map);
			getContentPane().add(mapPanel, BorderLayout.CENTER);
			pack();
		}
	}

	@Override
	public void processEvent(GPSEvent e) {
		prevEvent = e;
		if(followGPS)
			mapPanel.setCenter(e.getLongitude(), e.getLatitude());
		mapPanel.setDriving(true, e.getLongitude(), e.getLatitude());
		if(inDriveMode) {
			RouteChecker task = new RouteChecker(e);
			task.execute();
		}
	}
}
