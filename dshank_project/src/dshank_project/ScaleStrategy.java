package dshank_project;

public interface ScaleStrategy {

	/**
	 * Converts some latitude to a number of pixels
	 * @param lat The value to convert
	 * @return The number of pixels
	 */
	int latToPixels(double lat);

	/**
	 * Converts a latitude and longitude to a number of pixels in the longitudinal direction.
	 * @param lon The value to convert
	 * @param lat The position in lat
	 * @return The number of pixels
	 */
	int lonToPixels(double lon, double lat);

	/**
	 * Converts some number of pixels into a latitude
	 * @param pixels The number of pixels
	 * @return The value in latitude
	 */
	double pixelsToLat(int pixels);

	/**
	 * Converts some pixel position to a longitude
	 * @param lonPix The number of pixels in the longitude axis
	 * @param latPix The number of pixels in the latitude axis
	 * @return The longitude value
	 */
	double pixelsToLon(int lonPix, int latPix);
	
	/**
	 * Alternative method for if you already have a latitude value calculated.
	 * @param lonPix The number of pixels in the longitude direction
	 * @param lat The latitude of that position
	 * @return The longitude at the position.
	 */
	double pixelsToLon(int lonPix, double lat);

	/**
	 * Zooms in or out, depending on the value of direction.
	 * Scales the zoom by zoomChangeFactor
	 * @param direction 1 to increase zoom, -1 to decrease zoom.
	 */
	void zoom(int direction);

	/**
	  * Sets the zoom to some specific value.
	  * @param newZoom The value of the new zoom.
	  */
	void setZoom(double newZoom);
	
	/**
	 * initializes the zoom so that the entire vertical portion of the map is visible.
	 * @param latMin The minimum latitude
	 * @param latMax The maximum latitude
	 * @param height The height of the window to be initialized for
	 */
	public void initZoom(double latMin, double latMax, int height);

}