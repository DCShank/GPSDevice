package dshank_project;

import java.util.ArrayList;
import java.util.List;

/**
 * I have seen very little use for relations. I don't predict using them anywhere
 * in this project. I have included it for completeness but I do not believe it
 * will be used in this application.
 * 
 * In order to properly implement this it would probably be necessary to have some
 * kind of 'Element' interface for Nodes Ways and Relations so that they could be
 * included in a list that was narrower than Object
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
