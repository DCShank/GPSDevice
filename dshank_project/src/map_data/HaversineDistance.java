package map_data;

public class HaversineDistance implements DistanceStrategy {
	// This value is in meters.
	private static final double RADIUS_EARTH = 6371000;

	public HaversineDistance() {
		// Doesn't actually need to initialize anything. If this were to be
		// used for other planetr I guess that could be a parameter.
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
//		System.out.println("");;
//		System.out.println(latStartRad);
//		System.out.println(latEndRad);
//		System.out.println(latDiffRad);
//		System.out.println(lonDiffRad);
//		System.out.println(a);
//		System.out.println(c);
		return d;
		
	}

}
