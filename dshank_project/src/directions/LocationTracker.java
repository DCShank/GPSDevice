package directions;

public class LocationTracker implements GPSListener {
	
	private Director dir;
	
	public LocationTracker(Director dir) {
		
	}
	
	/**
	 * Processes an event from the GPSDevice, and determines how to proceed.
	 */
	public void processEvent(GPSEvent e) {
		
	}
	
	public boolean onCourse(double lon, double lat, double heading) {
		return true;
	}

}
