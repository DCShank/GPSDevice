package dshank_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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
import directions.GraphNode;
import map_data.Map;
import map_data.Node;
import map_data.OSMParser;

public class Application extends JFrame {
	
	private Map map;
	private MapPanel mapPanel;
	private OSMParser prsr;
	private Director dir;
	
	/**
	 * Initializes the application with an OSM file to use for data.
	 * @param file An OSM file
	 */
	public Application(File file) throws Exception {
		this.setTitle("Map Application");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Irritating
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		loadMap(file);
		createMenuBar();
		ButtonPanel buttons = new ButtonPanel();
		content.add(buttons, BorderLayout.WEST);
		pack();
		setVisible(true);
	}
	
	/**
	 * Loads a given map file and displays it on the application frame.
	 * @param file The file to be loaded.
	 * @throws Exception Throws an exception if the file can't be loaded. This
	 * 			causes the file to stop being loaded and leaves the previous map
	 * 			being displayed.
	 */
	public void loadMap(File file) throws Exception {
		prsr = new OSMParser(file);
		prsr.parse();
		map = prsr.getMap();
		// Remove the old map panel if one exists.
		if(mapPanel != null) {remove(mapPanel);}
		mapPanel = new MapPanel(map);
		dir = new Director();
		getContentPane().add(mapPanel, BorderLayout.CENTER);
		pack();
	}
	
	/**
	 * Creates a menu bar for the application. Currently the only function of the
	 * menu bar is to load new OSM files.
	 */
	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		final JFileChooser fc = new JFileChooser();
		JMenuItem loadMap = new JMenuItem("Load Map");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OSM Map files", "osm");
		fc.setFileFilter(filter);
		// The action listener displays a file chooser dialog and lets display a new file.
		loadMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int chooseVal = fc.showOpenDialog(Application.this);
				if(chooseVal == fc.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						loadMap(file);
					} catch(Exception x) {
						System.out.println("Failed to load the map");
					}
				}
			}
		});
		fileMenu.add(loadMap);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}
	
	/**
	 * Main method for initialzing the program. Takes an OSM file as the argument.
	 * @param args OSM file to be displayed first.
	 * @throws Exception Throws an exception if something goes wrong with the file.
	 */
	public static void main(String[] args) throws Exception {
		new Application(new File(args[0]));
	}
	
	class ButtonPanel extends JPanel {
		JButton selStart;
		JButton selEnd;
		
		public ButtonPanel() {
			ActionListener selListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GraphNode n = mapPanel.popSelectedNode();
					if(n != null && e.getActionCommand().equals("start")) {
						GraphNode oldStart = dir.getStartNode();
						if(oldStart != null) {
							mapPanel.removeHighlightedNode((Node) oldStart);
						}
						dir.setStartNode(n);
						mapPanel.addHilightedNode((Node)n);
						JOptionPane.showMessageDialog(null, "Start node selected");
					}
					if(n != null && e.getActionCommand().equals("end")) {
						GraphNode oldEnd = dir.getEndNode();
						if(oldEnd != null) {
							mapPanel.removeHighlightedNode((Node) oldEnd);
						}
						dir.setEndNode(n);
						mapPanel.addHilightedNode((Node)n);
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

}
