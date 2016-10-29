import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Class representing the data for a map.
 * 
 * @author David Shank
 *
 */
public class Map {
	
	private HashMap<String, Node> nodes;
	private HashMap<String, Way> ways;
	private HashMap<String, Way> namedWays;
	private HashMap<String, Relation> relations;
	
	public Map(HashMap<String, Node> nds, HashMap<String, Way> wys,
				HashMap<String, Way> namedWys, HashMap<String, Relation> rltns) {
		nodes = nds;
		ways = wys;
		namedWays = namedWys;
		relations = rltns;
	}
	
	/**
	 * Gets an array of the named ways in the map.
	 * @return String[] for the named ways.
	 */
	public String[] getNamedWayKeys() {
		String[] wayArray = new String[namedWays.size()];
		namedWays.keySet().toArray(wayArray);
		Arrays.sort(wayArray);
		return wayArray;
	}
	
	public Iterator<Way> getWayIt() {
		return ways.values().iterator();
	}
	
	/**
	 * Returns whether a key corresponds to a way.
	 * @param key The key to check.
	 * @return Whether a Way exists that corresponds to the key.
	 */
	public boolean existsWay(String key) {
		return (ways.containsKey(key) || namedWays.containsKey(key));
	}
	
	/**
	 * Returns a Way for a specified key. Accepts IDs and names.
	 * @param key The key of the desired way. Can be either an id or name.
	 * @return The Way object.
	 * @precondition A way with the specified key must exist in either ways or namedWays
	 */
	public Way getWay(String key) {
		if(ways.containsKey(key))
			return ways.get(key);
		return namedWays.get(key);
	}
	
	/**
	 * Prints each node in a way, along with a number enumerating
	 * that nodes position in the way.
	 * @param key the id or name of the way to be printed.
	 */
	public void printWay(String key) {
		if(!existsWay(key)) {
			System.out.println("No such Way");
			return;
		}
		Way way = getWay(key);
		Iterator<Node> it = way.getNodeIt();
		int counter = 0;
		while(it.hasNext()) {
			counter += 1;
			Node node = it.next();
			System.out.println(counter + " " + node.toString());
		}
	}

}
