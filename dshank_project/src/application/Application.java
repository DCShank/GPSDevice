package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import directions.Director;
import directions.GPSEvent;
import directions.GPSListener;
import directions.Graph;
import directions.GraphNode;
import map_data.Map;
import map_data.Node;
import map_data.OSMParser;

public class Application extends JFrame implements GPSListener {

	private Map map;
	private MapPanel mapPanel;
	private OSMParser prsr;
	private Director dir;

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
		content.add(buttons, BorderLayout.WEST);
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
		// Remove the old map panel if one exists.
		if (mapPanel != null) {
			remove(mapPanel);
		}
		mapPanel = new MapPanel(map);
		dir = new Director((Graph)map);
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
						loadMap(file);
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
	 * Processes a GPSEvent and updates everything important, including the
	 * Director and MapPanel.
	 * @param e The GPSEvent with information.
	 */
	public void processEvent(GPSEvent e) {
		
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

		public ButtonPanel() {
			ActionListener selListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GraphNode n = mapPanel.popSelectedNode();
					if (n != null && e.getActionCommand().equals("start")) {
						GraphNode oldStart = dir.getStartNode();
						if (oldStart != null) {
							mapPanel.removeHighlightedNode((Node) oldStart);
						}
						dir.setStartNode(n);
						mapPanel.addHighlightedNode((Node) n);
						JOptionPane.showMessageDialog(null, "Start node selected");
					}
					if (n != null && e.getActionCommand().equals("end")) {
						GraphNode oldEnd = dir.getEndNode();
						if (oldEnd != null) {
							mapPanel.removeHighlightedNode((Node) oldEnd);
						}
						dir.setEndNode(n);
						mapPanel.addHighlightedNode((Node) n);
						JOptionPane.showMessageDialog(null, "End node selected");
					}
				}
			};

			selStart = new JButton("Select start");
			selStart.setActionCommand("start");
			selStart.addActionListener(selListener);
			selEnd = new JButton("Select end");
			selEnd.setActionCommand("end");
			selEnd.addActionListener(selListener);
			this.setLayout(new GridLayout(0, 1));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this.add(selStart);
			this.add(selEnd);
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

}
