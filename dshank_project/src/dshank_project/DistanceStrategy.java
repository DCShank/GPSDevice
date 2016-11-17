package dshank_project;

/**
 * A strategy for finding distance between two points.
 * @author david
 *
 */
public interface DistanceStrategy {
	
	public double getDistance(double x1, double y1, double x2, double y2);
	
}
