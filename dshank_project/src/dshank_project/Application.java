package dshank_project;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import map_data.Map;
import map_data.OSMParser;

public class Application extends JFrame {
	
	private Map map;
	private MapPanel mapPanel;
	private OSMParser prsr;
	
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
		mapPanel = new MapPanel(map);
		createMenuBar();
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

}
