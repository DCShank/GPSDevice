package map_data;

import java.io.File;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses an OSM file into useful data and can hand out that data.
 * @author david
 *
 */
public class OSMParser {
	
	private HashMap<String,Node> nodes;
	private HashMap<String,Way> ways;
	private HashMap<String,Way> namedWays;
	private HashMap<String,Relation> relations;
	
	/**
	 * Initializes the OSMParser on a given file.
	 * @param file The OSM file to be parsed.
	 */
	public OSMParser(File file) {
		
	}
	
	/**
	 * Parses the OSM file.
	 */
	public void parse() {
		
	}
	
	/**
	 * Returns the HashMap containiing all the nodes.
	 * @return The nodes HashMap
	 */
	public HashMap<String,Node> getNodes() {
		return null;
	}
	/**
	 * Returns the Hashmap containing all the ways.
	 * @return The ways HashMap
	 */
	public HashMap<String,Way> getWays() {
		return null;
	}
	
	class OSMHandler extends DefaultHandler {

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
		 * Method called by SAX parser when start tag for XML element is
		 * encountered.
		 * Used to check if we just encountered a useful element. May 
		 * store element data in fields.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
			
		}

		/**
		 * Method called by SAX parser when end tag for XML element is
		 * encountered. This can occur even if there is no explicit end tag
		 * present in the document.
		 * 
		 * Creates Ways and stores them in the ways HashMap.
		 */
		public void endElement(String namespaceURI, String localName, String qName) throws SAXParseException {
			
		}

		
	}

}
