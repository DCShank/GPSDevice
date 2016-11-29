package map_data;

import java.util.ArrayList;
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
	private ArrayList<Object> elements;
	
	public Relation(String idString, List<Object> elements) {
		
	}
	
	@Override
	public boolean equals(Object other) {
		return (Boolean) null;
	}
	
	@Override
	public int hashCode() {
		return (Integer) null;
	}

}
