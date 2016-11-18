package map_data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses an OSM file into useful data and can hand out that data.
 * @author david
 *
 */
public class OSMParser {
	/** The file to be parsed*/
	private File file;
	
	/** Collections of the various elements extracted from the file. */
	private HashMap<String,Node> nodes;
	private HashMap<String,Way> ways;
	private HashMap<String,Way> namedWays;
	private HashMap<String,Way> roadWays;
	private HashMap<String,Relation> relations;
	
	/** Bounds for the map being parsed. */
	private double minLon, maxLon, minLat, maxLat;
	
	/**
	 * Initializes the OSMParser on a given file.
	 * @param file The OSM file to be parsed.
	 */
	public OSMParser(File f) {
		file = f;
		nodes = new HashMap<String,Node>();
		ways = new HashMap<String,Way>();
		namedWays = new HashMap<String,Way>();
		roadWays = new HashMap<String,Way>();
	}
	
	/**
	 * Parses the OSM file.
	 * I'm going to be completely honest when I say that this is mostly
	 * copied from the original OSMParser provided.
	 */
	public void parse() throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		OSMHandler handler = new OSMHandler();
		xmlReader.setContentHandler(handler);
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			InputSource source = new InputSource(stream);
			xmlReader.parse(source);
		} catch (IOException x) {
			throw x;
		} finally {
			if (stream != null)
				stream.close();
		}
	}
	
	/**
	 * Returns the HashMap containiing all the nodes.
	 * @return The nodes HashMap
	 */
	public HashMap<String,Node> getNodes() {
		return nodes;
	}
	/**
	 * Returns the Hashmap containing all the ways.
	 * @return The ways HashMap
	 */
	public HashMap<String,Way> getWays() {
		return ways;
	}
	
	class OSMHandler extends DefaultHandler {
		
		/** Stores the nodes for a way as it's being parsed. */
		private ArrayList<Node> tempNodes;
		/** Stores the ID of an element as it's being parsed. */
		private String id;
		/** Stores the name of an element as it's being parsed. */
		private String name;
		/** Stores the type of road of a way as it's being parsed. */
		private String roadType;

		/**
		 * Method called by SAX parser when start of document is encountered.
		 */
		public void startDocument() {}

		/**
		 * Method called by SAX parser when end of document is encountered.
		 */
		public void endDocument() {}

		/**
		 * Method called by SAX parser when start tag for XML element is
		 * encountered.
		 * Used to check if we just encountered a useful element. May 
		 * store element data in fields.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
			if(qName.equals("node")) {storeNode(atts);}
			else if(qName.equals("way")) {
				storeWayID(atts);
				tempNodes = new ArrayList<Node>();
			}
			else if(qName.equals("nd")) {storeWayNode(atts); }
			else if(qName.equals("tag") && !id.isEmpty()) { parseWayTag(atts); }
			else if(qName.equals("bounds")) { storeBounds(atts); }
		}

		/**
		 * Method called by SAX parser when end tag for XML element is
		 * encountered. This can occur even if there is no explicit end tag
		 * present in the document.
		 * 
		 * Creates Ways and stores them in the ways HashMap.
		 */
		public void endElement(String namespaceURI, String localName, String qName) throws SAXParseException {
			if(qName.equals("way")) {
				Way way = new Way(id, tempNodes, name);
				ways.put(id, way);
				// Put the ways into specific maps depending on their properties.
				if(way.isNamed()) {
					namedWays.put(name, way);
				}
				if(way.isRoad()) {
					roadWays.put(id, way);
				}
				// Reset the values for future elements.
				id = "";
				name = "";
				roadType = "";
			}
		}
		
		/**
		 * Gets the bounds of a map file and stores them to instance vars.
		 * @param atts
		 */
		private void storeBounds(Attributes atts) {
			for (int i = 0; i < atts.getLength(); i++) {
				String qName = atts.getQName(i);
				String value = atts.getValue(i);
				if (qName.equals("minlat"))
					minLat = Double.parseDouble(value);
				if (qName.equals("minLon"))
					minLon = Double.parseDouble(value);
				if (qName.equals("maxLat"))
					maxLat = Double.parseDouble(value);
				if (qName.equals("maxLon"))
					maxLon = Double.parseDouble(value);
			}
		}
		
		/**
		 * Given that an element is a node, adds it to the nodes hashMap
		 * @param atts
		 */
		private void storeNode(Attributes atts) {
			String id = "";
			double lat = 0;
			double lon = 0;
			for (int i = 0; i < atts.getLength(); i++) {
				String qName = atts.getQName(i);
				String value = atts.getValue(i);
				if (qName.equals("id"))
					id = value;
				if (qName.equals("lat"))
					lat = Double.parseDouble(value);
				if (qName.equals("lon"))
					lon = Double.parseDouble(value);
			}
			Node node = new Node(lat, lon, id);
			nodes.put(node.getID(), node);
		}
		/**
		 * Iterates over the atts and stores the id to an instance variable
		 * @param atts
		 */
		private void storeWayID(Attributes atts) {
			id = "";
			for (int i = 0; i < atts.getLength(); i++) {
				String qName = atts.getQName(i);
				String value = atts.getValue(i);
				if (qName.equals("id"))
					id = value;
			}
		}

		/**
		 * Adds the node to a temporary list of nodes for the current way.
		 * @param atts
		 */
		private void storeWayNode(Attributes atts) {
			String ndID = "";
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getQName(i).equals("ref"))
					ndID = atts.getValue(i);
			}
			tempNodes.add(nodes.get(ndID));

		}
		
		/**
		 * Parses a tag for a way and if it's a useful tag extracts its data.
		 * Tags that are useful in this case are highways and names.
		 * @param atts
		 */
		private void parseWayTag(Attributes atts) {
			String key = atts.getValue("k");
			String value = atts.getValue("v");
			if(key.equals("name")) {
				name = value;
			} else if(key.equals("highway")) {
				roadType = value;
			}
		}

		
	}

}
