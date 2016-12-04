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
import java.util.List;

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
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.starkeffect.highway.GPSDevice;
import com.starkeffect.highway.GPSEvent;
import com.starkeffect.highway.GPSListener;

import graph_interfaces.GraphEdge;
import graph_interfaces.GraphNode;
import map_data.Map;
import map_data.Node;
import map_data.OSMParser;

public class Application extends JFrame implements GPSListener, MapPanelListener{

	/** Parses OSM data into a usable state. Used for making the Map */
	private OSMParser prsr;
	/** Map that contains all the map data for the current map */
	private Map map;
	/** Panel that displays map data for the current map. */
	private MapPanel mapPanel;
	/** Used for finding directions from one position to another on a map. */
	private Director dir;
	/** Private list of the directions that are given to the map panel. */
	private List<GraphEdge> directions;
	/** Starks implementation of the GPS. Also provides real time position updates. */
	private GPSDevice gps;
	/** Label for displaying system relevant messages, such as found a route, without popups, */
	private JLabel messageDisplay;
	private JTextArea dirDisplay;
	private GPSEvent prevEvent = null;
	
	private JButton getDir;
	private JToggleButton driveThere;
	private JToggleButton trackPos;

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
		dirDisplay = new JTextArea(20,25);
		
		messageDisplay = new JLabel();
		messageDisplay.setHorizontalAlignment(JLabel.CENTER);
		messageDisplay.setFont(messageDisplay.getFont().deriveFont(20f));
		messageDisplay.setText("Lest click to select the start node, right click to select the end node.");
		messageDisplay.setToolTipText("Displays system updates.");
		
		content.add(buttons, BorderLayout.NORTH);
		content.add(messageDisplay, BorderLayout.SOUTH);
		content.add(dirDisplay, BorderLayout.EAST);
		
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
		MapLoader load = new MapLoader(file);
		load.execute();
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
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent arg0) {
				int chooseVal = fc.showOpenDialog(Application.this);
				if (chooseVal == fc.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
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
	 * Main method for initializing the program. Takes an OSM file as the
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

	/**
	 * A panel that holds all the buttons used by the application.
	 * Some of the buttons are referenced outside of the scope of the panel, and
	 * are thus made instance variables of the Application instead.
	 * @author david
	 *
	 */
	class ButtonPanel extends JPanel {
//		JButton getDir;
		JButton clearDir;
//		JToggleButton trackPos;
//		JToggleButton driveThere;
		
		/**
		 * Initializes the button panel.
		 */
		public ButtonPanel() {
			ActionListener buttonPanelListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Gets directions from start to end.
					if (e.getActionCommand().equals("directions")) {
						SwingWorker<List<GraphEdge>, Object> task = new DirectionFinder();
						task.execute();
					}
					// Clears all the current selections.
					if (e.getActionCommand().equals("clear")) {
						getDir.setEnabled(false);
						driveThere.setEnabled(true);
						driveThere.setSelected(false);
						dir.clearDirections();
						mapPanel.setDirections(null);
						mapPanel.setStart(null);
						mapPanel.setEnd(null);
						mapPanel.repaint();
						updateAppState();
						messageDisplay.setText("All selections have been cleared.");
					}
					// If we select drive there, set the state of everything to driving.
					// Unless we can't enter driver there mode.
					if (e.getActionCommand().equals("drive")) {
						if(driveThere.isSelected()) {
							if(prevEvent != null) {
								dir.setStartNode(map.getNearNode(prevEvent.getLongitude(), prevEvent.getLatitude()));
								mapPanel.setStart(null);
								DirectionFinder task = new DirectionFinder();
								task.execute();
								updateAppState();
								messageDisplay.setText("Drive there mode enabled.");
							} else {
								messageDisplay.setText("No GPS coordinates to drive from!");
								driveThere.setSelected(false);
							}
						} else {
							messageDisplay.setText("Drive there mode disabled.");
						}
					}
					// If we select the track button, set the state of the map panel to tracking.
					if (e.getActionCommand().equals("track")) {
						mapPanel.setTrackPos(trackPos.isSelected());
						if(trackPos.isSelected()) {
							mapPanel.setCenter(prevEvent.getLongitude(), prevEvent.getLatitude());
							mapPanel.repaint();
						}
					}
				}
			};
			// Init the get directions button
			getDir = new JButton("Get directions");
			getDir.setActionCommand("directions");
			getDir.setEnabled(false);
			getDir.setToolTipText("Must have selected a start and end node to get directions");
			getDir.addActionListener(buttonPanelListener);
			// Init the clear directions button
			clearDir = new JButton("Clear selections");
			clearDir.setActionCommand("clear");
			clearDir.addActionListener(buttonPanelListener);
			// Init the track position button.
			trackPos = new JToggleButton("Track position", false);
			trackPos.setActionCommand("track");
			trackPos.setEnabled(false);
			trackPos.addActionListener(buttonPanelListener);
			// Init the drive there button
			driveThere = new JToggleButton("Drive there", false);
			driveThere.setActionCommand("drive");
			driveThere.setEnabled(false);
			driveThere.setToolTipText("Must have selected an end node to get driving directions");
			driveThere.addActionListener(buttonPanelListener);
			// Set up the button panel.
			this.setLayout(new GridLayout(1, 0));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this.add(getDir);
			this.add(clearDir);
			this.add(trackPos);
			this.add(driveThere);
		}
	}

	/**
	 * Finds directions in the background.
	 * @author david
	 *
	 */
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
					dirDisplay.setText(dir.getDirString());
				}
			} catch (Exception e) {
				messageDisplay.setText("An ERROR has occurred!");
			}
		}
	}

	/**
	 * Checks your route in the background, and updates it if you're off course.
	 * @author david
	 *
	 */
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
					dirDisplay.setText(dir.getDirString());
				} else {
					dirDisplay.setText(dir.getDirString());
					messageDisplay.setText("On route to destination");
				}
			} catch (Exception e) {
			}
		}

	}
	
	/**
	 * Loads the map in the background.
	 * @author david
	 *
	 */
	class MapLoader extends SwingWorker<Map, Object> {

		private File file;
		public MapLoader(File f) {
			file = f;
			messageDisplay.setText("Loading map...");
		}
		@Override
		protected Map doInBackground() throws Exception {
			prsr = new OSMParser(file);
			map = prsr.parse();
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
			prevEvent = null;
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
				mapPanel.addListener(Application.this);
			} catch (Exception e) {
			}
			messageDisplay.setText("Map loaded. Left click to select start, right click to select end.");
			dir = new Director(map);
			updateAppState();
			getContentPane().add(mapPanel, BorderLayout.CENTER);
			pack();
		}
	}	

	/**
	 * Updates the state of the application based on an event from the gps device.
	 */
	@Override
	public void processEvent(GPSEvent e) {
		prevEvent = e;
		updateAppState();
		if(trackPos.isSelected())
			mapPanel.setCenter(e.getLongitude(), e.getLatitude());
		mapPanel.setDriver(e.getLongitude(), e.getLatitude());
		if(driveThere.isSelected()) {
			RouteChecker task = new RouteChecker(e);
			task.execute();
		}
	}
	
	/**
	 * Updates the state of the application based on an event from the map panel.
	 */
	@Override
	public void processEvent(MapPanelEvent e) {
		if(dir.getStartNode() != e.getStartNode())
			messageDisplay.setText("Start node selected.");
		if(dir.getEndNode() != e.getEndNode())
			messageDisplay.setText("End node selected.");
		dir.setEndNode(e.getEndNode());
		if(driveThere.isSelected()) {
			mapPanel.setStart(null);
			messageDisplay.setText("Cannot select start nodes while in drive there mode.");
		} else {
			dir.setStartNode(e.getStartNode());
		}
		if(e.movedMap()) {
			if(trackPos.isSelected()) {
				messageDisplay.setText("Moved map, exiting tracking.");
				trackPos.setSelected(false);
			}
			mapPanel.setTrackPos(false);
		}
		updateAppState();
	}
	
	/**
	 * Updates the general state of the application to be consistent. Generally useful for buttons.
	 */
	public void updateAppState() {
		if(dir.getEndNode() == null) {
			driveThere.setSelected(false);
			mapPanel.setTrackPos(false);
		}
		trackPos.setEnabled(prevEvent != null);
		driveThere.setEnabled(dir.getEndNode() != null && prevEvent != null);
		getDir.setEnabled(dir.getStartNode() != null && dir.getEndNode() != null && !driveThere.isSelected());

		mapPanel.repaint();
	}
}
