package dshank_project;

/**
 * Strategy for converting latitude and longitude values into pixels and vice versa
 * 
 * @author david
 *
 */
public class MapScale implements ScaleStrategy{
	
	private static final double DEFAULT_ZOOM = 4000;
	private static final double DEFAULT_ZOOM_CHANGE = 0.15;
	
	/** The amount to multiply zoom by when zooming in or out */
	private double zoomChangeFactor;
	/** The multiplier for longitude and latitude */
	private double zoom;
	
	/**
	 * Constructs a MapScale with an initial zoom and zoomChangeFactor.
	 * @param zoom The amount to scale a latitude or longitude value by.
	 * @param zoomChangeFactor Multiplier applied to zoom when changing zoom.
	 */
	public MapScale(double zoom, double zoomChangeFactor) {
		this.zoom = zoom;
		this.zoomChangeFactor = zoomChangeFactor;
	}	
	
	/**
	 * Alternative constructor with a default value for zoom.
	 * @param zoomChangeFactor Multiplier applied to zoom when changing zoom.
	 */
	public MapScale() {
		this.zoom = DEFAULT_ZOOM;
		this.zoomChangeFactor = DEFAULT_ZOOM_CHANGE;
	}
	
	/**
	 * Returns the number of pixels below the equator.
	 * @param lat The latitude value to convert to pixels.
	 * @return Number of pixels south of the equator (negative for values above equator).
	 */
	@Override
	public int latToPixels(double lat) {
		// The negative one flips the sign so that it's top to bottom
		// instead of bottom to top.
		return (int) (lat * -1.0 * zoom);
	}
	
	/**
	 * Returns the number of pixels east of the prime meridian for some point.
	 * @param lat The latitude of the point to be converted.
	 * @param lon The longitude of the point to be converted.
	 * @return The number of pixels east of the prime meridian.
	 */
	@Override
	public int lonToPixels(double lon, double lat) {
		return (int) (lon * zoom * Math.cos(Math.toRadians(lat)));
	}
	
	/**
	 * Returns the latitude of some number of pixels.
	 * @param pixels Number of pixels south of the equator. Negative for lats north of equator.
	 * @return The latitude of the number of pixels.
	 */
	@Override
	public double pixelsToLat(int pixels) {
		return (double) (((double)pixels) / zoom * -1.0);
	}
	
	/**
	 * Returns the longitude value of some map-pixel position.
	 * @param latPix The number of pixels south of the equator.
	 * @param lonPix The number of pixels east of the equator at the point.
	 */
	@Override
	public double pixelsToLon(int lonPix, int latPix) {
		return (double) (((double)latPix) / zoom / Math.cos(Math.toRadians(pixelsToLat(latPix))));
	}
	
	/**
	 * Zooms in or out depending on the direction.
	 * @param direction Zooms in for 1 and out for -1.
	 * @precondition Direction must be 1 or -1.
	 */
	@Override
	public void zoom(int direction) {
		zoom = zoom * zoomChangeFactor * direction;
	}
	
	/**
	 * Sets the zoom to some value.
	 * @param newZoom The new double value of the zoom.
	 */
	@Override
	public void setZoom(double newZoom) {
		zoom = newZoom;
	}
	
	/**
	 * initializes the zoom so that the entire vertical portion of the map is visible.
	 * @param latMin The minimum latitude
	 * @param latMax The maximum latitude
	 * @param height The height of the window to be initialized for
	 */
	public void initZoom(double latMin, double latMax, int height) {
		double dl = Math.abs(latMax-latMin);
		zoom = ((double)height)/-1.0/dl;
	}

}