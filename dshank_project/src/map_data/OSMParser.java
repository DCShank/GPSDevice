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
	private HashMap<String,Relation> rels;
	private HashMap<String,Way> roadWays;
	
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
		roadWays = new HashMap<String,Way>();
		rels = new HashMap<String,Relation>();
	}
	
	/**
	 * Parses the OSM file and returns the map represented by that data.
	 * @return The Map representing the map data.
	 */
	public Map parse() throws IOException, ParserConfigurationException, SAXException {
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
		return getMap();
	}
	
	/**
	 * Returns a map with all the currently parsed data. Note that the data
	 * must be parsed vefore this is called for useful effects.
	 * @precondition Parse must be called on a parseable file before this is called.
	 * @return A Graph containing all the parsed data.
	 */
	private Map getMap() {
		Map map = new Map(minLon, minLat, maxLon, maxLat, nodes, ways, roadWays, rels);
		return map;
	}
	
	class OSMHandler extends DefaultHandler {
		
		/** Stores the nodes for a way as it's being parsed. */
		private ArrayList<Node> tempNodes;
		/** Stores the ID of an element as it's being parsed. */
		private String id = "";
		/** Stores the name of an element as it's being parsed. */
		private String name = "";
		/** Stores whether or not a way is one way. */
		private boolean oneway = false;
		/** Stores all the tags for the way currently being parsed */
		private HashMap<String, String> wayTags = new HashMap<String, String>();
//		private HashMap<String, String> relTags = new HashMap<String, String>();
		private ArrayList<Way> relWays = new ArrayList<Way>();

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
			else if(qName.equals("relation")) {
				storeWayID(atts); // Does what we want even though it says way.
			}
			else if(qName.equals("nd")) {storeWayNode(atts); }
			else if(qName.equals("member")) {storeRelWay(atts); }
			// Note that parseWayTag parses tags for relations just fine.
			else if(qName.equals("tag") && !id.isEmpty()) { parseWayTag(atts); }
			else if(qName.equals("bounds") || qName.equals("bound")) { storeBounds(atts); }
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
				Way way = new Way(id, name,  wayTags, tempNodes, oneway);
				ways.put(id, way);
				// Put the ways into specific maps depending on their properties.
				if(way.isRoad()) {
					roadWays.put(id, way);
				} 
				// Reset the values for future elements.
				id = "";
				name = "";
				oneway = false;
				wayTags = new HashMap<String,String>();
				relWays = new ArrayList<Way>();
			}
			if(qName.equals("relation")) {
				Relation rel = new Relation(id, relWays, wayTags);
				rels.put(id, rel);
				id = "";
				name = "";
				oneway = false;
				wayTags = new HashMap<String,String>();
				relWays = new ArrayList<Way>();
				
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
				if (qName.equals("minlon"))
					minLon = Double.parseDouble(value);
				if (qName.equals("maxlat"))
					maxLat = Double.parseDouble(value);
				if (qName.equals("maxlon"))
					maxLon = Double.parseDouble(value);
				if (qName.equals("box")) {
					String[] values = value.split(",");
					minLat = Double.parseDouble(values[0]);
					minLon = Double.parseDouble(values[1]);
					maxLat = Double.parseDouble(values[2]);
					maxLon = Double.parseDouble(values[3]);
				}
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
			Node node = new Node(lon, lat, id);
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
			Node node = nodes.get(ndID);
			if(node != null)
				tempNodes.add(nodes.get(ndID));
		}

		/**
		 * Adds the way to a temporary list of ways for the current relation.
		 * @param atts
		 */
		private void storeRelWay(Attributes atts) {
			String wayID = "";
			if(atts.getValue("type").equals("way")) {
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getQName(i).equals("ref"))
					wayID = atts.getValue(i);
			}
			Way way = ways.get(wayID);
			if(way != null)
				relWays.add(way);
			}
		}
		
		/**
		 * Parses a tag and stores the data in the wayTags map.
		 * If the tag makes the way one way updates that field.
		 * @param atts
		 */
		private void parseWayTag(Attributes atts) {
			String key = atts.getValue("k");
			String value = atts.getValue("v");
			wayTags.put(key, value);
			if(key.equals("name")) {
				name = value;
			} else if(key.equals("highway")) {
				if(value.equals("roundabout") || value.equals("motorway")) {
					oneway = true;
				}
			} else if(key.equals("oneway")) {
				oneway = value.equals("yes");
			}
		}
//		private void parseRelationTag(Attributes atts) {
//			String key = atts.getValue("k");
//			String value = atts.getValue("v");
//			relTags.put(key, value);
//		}
		
	}
}
