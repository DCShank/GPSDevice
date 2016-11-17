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
	 * @param lat The position in lat
	 * @param lon The value to convert
	 * @return The number of pixels
	 */
	int lonToPixels(double lat, double lon);

	/**
	 * Converts some number of pixels into a latitude
	 * @param pixels The number of pixels
	 * @return The value in latitude
	 */
	double pixelsToLat(int pixels);

	/**
	 * Converts some pixel position to a longitude
	 * @param latPix The number of pixels in the latitude axis
	 * @param lonPix The number of pixels in the longitude axis
	 * @return The longitude value
	 */
	double pixelsToLon(int latPix, int lonPix);

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

}