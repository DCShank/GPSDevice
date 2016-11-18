package dshank_project;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

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
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		loadMap(file);
		pack();
		setVisible(true);
	}
	
	public void loadMap(File file) throws Exception {
		prsr = new OSMParser(file);
		prsr.parse();
		map = prsr.getMap();
		mapPanel = new MapPanel(map);
		getContentPane().add(mapPanel, BorderLayout.CENTER);
		pack();
	}
	
	/**
	 * Main method for initialzing the program. Takes an OSM file as the argument.
	 * @param args OSM file to be used
	 * @throws Exception Throws an exception if something goes wrong with the file.
	 */
	public static void main(String[] args) throws Exception {
		Application test = new Application(new File(args[0]));
	}

}
