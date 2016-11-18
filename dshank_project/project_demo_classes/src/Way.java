import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a way on a map.
 * Way's seem to represent buildings, roads, and other geographic stuff.
 * @author David Shank
 *
 */
public class Way {
	
    private String id;
    private String name;
    /** A list of nodes */
    private ArrayList<Node> nodes;
    
    public Way(String id, ArrayList<Node> nodes, String name) {
    	this.id = id;
    	this.nodes = nodes;
    	this.name = name;
    }
    
    /**
     * Returns the string for this way.
     * @return The string for the name.
     */
    public String getName() {
    	return name;
    }
    
    /**
     * Returns whether or not the way is named.
     * @return True if the way has a a name, false otherwise.
     */
    public boolean isNamed() {
    	return !name.isEmpty();
    }
    
    /**
     * Gives an iterator for the nodes of this way.
     * @return Iterator<Node>
     */
    public Iterator<Node> getNodeIt() {
    	return nodes.iterator();
    }
    
    /**
     * Returns the id for this way.
     * @return the string for the ID.
     */
    public String getID() {
    	return id;
    }
    
    
    @Override
    public boolean equals(Object other) {
    	if(this == other)
    		return true;
    	if(other == null)
    		return false;
    	if(getClass() != other.getClass())
    		return false;
    	return id.equals(((Way)other).getID());
    }
    
    @Override
    public int hashCode() {
    	return id.hashCode();
    }

}
