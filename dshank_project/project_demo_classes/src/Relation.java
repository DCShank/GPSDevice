import java.util.ArrayList;

public class Relation {
	
	private String id;
	private ArrayList<Way> ways;
	
	public Relation(String id, ArrayList<Way> ways) {
		this.id = id;
		this.ways = ways;
	}
	
	public String getID() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other == null)
			return false;
		if(this.getClass() != other.getClass())
			return false;
		return getID().equals(((Relation)other).getID());
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
