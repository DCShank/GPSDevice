package map_data;

/**
 * Calculates the distance between two points over the surface of the earth.
 * 
 * @author david
 *
 */
public class HaversineDistance implements DistanceStrategy {
	/** Radius of the earth in meters */
	private static final double RADIUS_EARTH = 6371000;

	public HaversineDistance() {
		// Doesn't actually need to initialize anything. If this were to be
		// used for other planets or spheres I guess that the radius could be a
		// parameter.
	}
	/**
	 * Uses the Haversine formula to calculate the distance over a portion of the globe.
	 * 
	 * @param lonStart The longitude of the start point.
	 * @param latStart The latitude of the start point.
	 * @param lonEnd The longitude of the end point.
	 * @param latEnd The latitude of the end point.
	 * @return The distance from start point to end point over the globe.
	 */
	@Override
	public double getDistance(double lonStart, double latStart, double lonEnd, double latEnd) {
		double latStartRad = Math.toRadians(latStart);
		double latEndRad = Math.toRadians(latEnd);
		double latDiffRad = Math.toRadians(latEnd-latStart);
		double lonDiffRad = Math.toRadians(lonEnd - lonStart);
		
		double a = (Math.sin(latDiffRad/2.0) * Math.sin(latDiffRad/2.0)) + Math.cos(latStartRad)
				* Math.cos(latEndRad) * (Math.sin(lonDiffRad/2.0) * Math.sin(lonDiffRad/2.0));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = RADIUS_EARTH * c;

		return d;
		
	}

}
