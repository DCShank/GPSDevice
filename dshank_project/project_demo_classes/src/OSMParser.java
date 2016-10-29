import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sample parser for reading Open Street Map XML format files. Illustrates the
 * use of SAXParser to parse XML.
 *
 * @author E. Stark
 * @date September 20, 2009
 */
class OSMParser {

	/** OSM file from which the input is being taken. */
	private File file;

	/** Key = ID num */
	private HashMap<String, Node> nodes;

	private HashMap<String, Way> ways;

	private HashMap<String, Relation> relations;

	/** Maps names to their appropriate ways. */
	private HashMap<String, Way> waysNamed;

	/**
	 * Initialize an OSMParser that takes data from a specified file.
	 *
	 * @param s
	 *            The file to read.
	 * @throws IOException
	 */
	public OSMParser(File f) {
		file = f;
		nodes = new HashMap<String, Node>();
		ways = new HashMap<String, Way>();
		waysNamed = new HashMap<String, Way>();
		relations = new HashMap<String, Relation>();
	}

	/**
	 * Parse the OSM file underlying this OSMParser.
	 */
	public void parse() throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(false);
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

	public String getElements() {
		return "nodes:" + nodes.size() + "\nways: " + ways.size() + "\nrelations: " + relations.size()
				+ "\nnamed ways: " + waysNamed.size();
	}

	/**
	 * Produces a Map object based on the current contents of this parser
	 * object.
	 * 
	 * This seems like a very hacky way to do it. A better solution might be to
	 * have the parse method return a map.
	 * 
	 * @return A Map containing this objects Node, Way, and Relation HashMaps.
	 */
	public Map getMap() {
		return new Map(nodes, ways, waysNamed, relations);
	}

	/**
	 * Handler class used by the SAX XML parser. The methods of this class are
	 * called back by the parser when XML elements are encountered.
	 */
	class OSMHandler extends DefaultHandler {

		/** Current character data. */
		private String cdata;

		/** Attributes of the current element. */
		private Attributes attributes;

		/** List of the most recent nd's for a way */
		private ArrayList<Node> nds = new ArrayList<Node>();

		/** The id of the most recent way */
		private String wayID = "";
		private String wayName = "";

		private String relID = "";

		private ArrayList<Way> relWays = new ArrayList<Way>();

		/**
		 * Get the most recently encountered CDATA.
		 */
		public String getCdata() {
			return cdata;
		}

		/**
		 * Get the attributes of the most recently encountered XML element.
		 */
		public Attributes getAttributes() {
			return attributes;
		}

		/**
		 * Method called by SAX parser when start of document is encountered.
		 */
		public void startDocument() {
		}

		/**
		 * Method called by SAX parser when end of document is encountered.
		 */
		public void endDocument() {
		}

		/**
		 * Given that an element is a node, adds it to the nodes hashMap
		 * 
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
			Node node = new Node(id, lat, lon);
			nodes.put(node.getID(), node);
		}

		/**
		 * Iterates over the atts and stores the id to an instance variable
		 * 
		 * @param atts
		 */
		private void storeWayID(Attributes atts) {
			wayID = "";
			for (int i = 0; i < atts.getLength(); i++) {
				String qName = atts.getQName(i);
				String value = atts.getValue(i);
				if (qName.equals("id"))
					wayID = value;
			}
		}

		/**
		 * Adds the node to a list of nodes.
		 * 
		 * @param atts
		 */
		private void storeWayNd(Attributes atts) {
			String ndID = "";
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getQName(i).equals("ref"))
					ndID = atts.getValue(i);
			}
			nds.add(nodes.get(ndID));

		}

		private void storeRelID(Attributes atts) {
			relID = "";
			for (int i = 0; i < atts.getLength(); i++) {
				String qName = atts.getQName(i);
				String value = atts.getValue(i);
				if (qName.equals("id"))
					relID = value;

			}
		}

		private void storeRelElement(Attributes atts) {
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getQName(i).equals("way")) {
					String value = atts.getValue(i);
					relWays.add(ways.get(value));
				}
			}
		}

		/**
		 * Method called by SAX parser when start tag for XML element is
		 * encountered.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
			attributes = atts; // This doesn't do anything?
			if (qName.equals("node"))
				storeNode(atts);
			if (qName.equals("way")) {
				storeWayID(atts);
				nds = new ArrayList<Node>();
			}
			if (qName.equals("nd")) {
				storeWayNd(atts);
			}
			if (qName.equals("relation")) {
				storeRelID(atts);
				relWays = new ArrayList<Way>();
			}
			if (qName.equals("member")) {
				storeRelElement(atts);
			}
			if (qName.equals("tag") && !(wayID.equals(""))) {
				for (int i = 0; i < atts.getLength(); i++) {
					if (atts.getQName(i).equals("k")) {
						if (atts.getValue(i).equals("name")) {
							wayName = atts.getValue(i + 1);
						}
					}
				}
			}
		}

		/**
		 * Method called by SAX parser when end tag for XML element is
		 * encountered. This can occur even if there is no explicit end tag
		 * present in the document.
		 */
		public void endElement(String namespaceURI, String localName, String qName) throws SAXParseException {
			if (qName.equals("way")) {
				Way way = new Way(wayID, nds, wayName);
				ways.put(wayID, way);
				if (!way.getName().equals("")) {
					waysNamed.put(wayName, way);
				}
				wayID = "";
				wayName = "";
			}
			if (qName.equals("relation")) {
				Relation rel = new Relation(relID, relWays);
				relations.put(relID, rel);
			}
		}

		/**
		 * Method called by SAX parser when character data is encountered.
		 */
		public void characters(char[] ch, int start, int length) throws SAXParseException {
			// OSM files apparently do not have interesting CDATA.
			// System.out.println("cdata(" + length + "): '"
			// + new String(ch, start, length) + "'");
			cdata = (new String(ch, start, length)).trim();
		}

		/**
		 * Auxiliary method to display the most recently encountered attributes.
		 */
		private void showAttrs(Attributes atts) {
			for (int i = 0; i < atts.getLength(); i++) {
				String qName = atts.getQName(i);
				String type = atts.getType(i);
				String value = atts.getValue(i);
				System.out.println("\t" + qName + "=" + value + "[" + type + "]");
			}
		}
	}
}
