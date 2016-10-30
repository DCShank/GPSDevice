package dshank_project;

/**
 * Strategy for converting latitude and longitude values into pixels and vice versa
 * @author david
 *
 */
public class ScaleStrategy {
	
	/** The amount to multiply zoom by when zooming in or out */
	private double zoomChangeFactor;
	/** The multiplier for longitude and latitude */
	private double zoom;
	
	/**
	 * Constructs a ScaleStrategy with an initial zoom and zoomChangeFactor.
	 * @param zoom The amount to scale a latitude or longitude value by.
	 * @param zoomChangeFactor Multiplier applied to zoom when changing zoom.
	 */
	public ScaleStrategy(double zoom, double zoomChangeFactor) {
		
	}
	
	/**
	 * Converts some latitude to a number of pixels
	 * @param lat The value to convert
	 * @return The number of pixels
	 */
	public int latToPixels(double lat) {
		return (Integer) null;
	}
	
	/**
	 * Converts a latitude and longitude to a number of pixels in the longitudinal direction.
	 * @param lat The position in lat
	 * @param lon The value to convert
	 * @return The number of pixels
	 */
	public int lonToPixels(double lat, double lon) {
		return (Integer) null;
	}
	
	/**
	 * Converts some number of pixels into a latitude
	 * @param pixels The number of pixels
	 * @return The value in latitude
	 */
	public double pixelsToLat(int pixels) {
		return (Double) null;
	}
	
	/**
	 * Converts some pixel position to a longitude
	 * @param latPix The number of pixels in the latitude axis
	 * @param lonPix The number of pixels in the longitude axis
	 * @return The longitude value
	 */
	public double pixelsToLon(int latPix, int lonPix) {
		return (Double) null;
	}
	
	/**
	 * Zooms in or out, depending on the value of direction.
	 * Scales the zoom by zoomChangeFactor
	 * @param direction 1 to increase zoom, -1 to decrease zoom.
	 */
	public void zoom(int direction) {
		
	}
	
	 /**
	  * Sets the zoom to some specific value.
	  * @param newZoom The value of the new zoom.
	  */
	public void setZoom(double newZoom) {
		
	}

}
