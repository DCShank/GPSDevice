import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class that sets up the demo gui for the map project.
 * 
 * @author David Shank
 */

public class MapDemo {

	private JFrame frame;
	private Map map;
	private OSMParser parser;
	
	public MapDemo(File file) throws Exception {
		// Initialize the Map.
		parser = new OSMParser(file);
		parser.parse();
		map = parser.getMap();

		// Create the frame.
		frame = new JFrame("Map Demo");
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout());

		// Create and add the MapPanel
		MapPanel mapPanel = new MapPanel(map);
		content.add(mapPanel, BorderLayout.CENTER);

		// Create a list of named ways and add that to the frame.
		// The list also lets you highlight ways on the map.
		JList<String> wayList = new JList<String>(map.getNamedWayKeys());
		wayList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		wayList.setLayoutOrientation(JList.VERTICAL);
		wayList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				mapPanel.setSelectedWays(wayList.getSelectedValuesList());
				mapPanel.repaint();
			}
		});
		JScrollPane scroller = new JScrollPane(wayList);
		content.add(scroller, BorderLayout.WEST);

		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Test driver. Takes filenames to be parsed as command-line arguments.
	 */
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			OSMParser prsr = new OSMParser(new File(args[i]));
			prsr.parse();
			Map myMap = prsr.getMap();
		}
		MapDemo demo = new MapDemo(new File(args[0]));
	}

}
