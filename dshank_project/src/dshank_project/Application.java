package dshank_project;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import map_data.Map;
import map_data.OSMParser;

public class Application extends JFrame {
	
	private Map map;
	private MapPanel mapPanel;
	private OSMParser prsr;
	/** The default scale strategy used by map panels */
	private ScaleStrategy scale;;
	
	/**
	 * Initializes the application with an OSM file to use for data.
	 * @param file An OSM file
	 */
	public Application(File file) {
		
		
	}
	
	public void loadMap(File file) throws Exception {
		prsr = new OSMParser(file);
		try {
			prsr.parse();
		} catch (Exception x) {
			throw x;
		}
		map = prsr.getMap();
		mapPanel = new MapPanel(map);
		
	}
	
	/**
	 * Main method for initialzing the program. Takes an OSM file as the argument.
	 * @param args OSM file to be used
	 */
	public static void main(String[] args) {

	}

}
