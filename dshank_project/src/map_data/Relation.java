package map_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Relations are currently not being used in my map.
 * This is a placeholder in case I decide to add them later.
 * @author david
 *
 */
public class Relation {
	/** The string representing the ID for this Relation. */
	private String id;
	/** The elements that are included in this Relation. */
	private ArrayList<Way> ways;
	private HashMap<String, String> tags;
	
	public Relation(String idString, List<Way> elements, HashMap<String, String> tags) {
		ways = new ArrayList<Way>(elements);
		this.tags = tags;
	}
	
	/**
	 * Returns the value for some tag key in Relations tags.
	 */
	public String getTagVal(String tag) {
		return tags.get(tag);
	}
	
	public List<Way> getWays() {
		return ways;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null) { return false; }
		if(other == this) { return true;  }
		if(other.getClass() != this.getClass()) { return false; }
		Relation o = (Relation) other;
		return id.equals(o.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
