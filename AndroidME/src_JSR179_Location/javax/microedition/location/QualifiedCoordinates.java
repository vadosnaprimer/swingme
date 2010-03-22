package javax.microedition.location;

/**
 * The <code>QualifiedCoordinates</code> class represents coordinates as
 * latitude-longitude-altitude values that are associated with an accuracy
 * value.
 */
public class QualifiedCoordinates
	extends Coordinates
{
	/**
	 * The horizontal accuracy of this location result in meters. <code>Float.Nan</code>
	 * can be used to indicate that the accuracy is not known. Must be greater
	 * or equal to 0.
	 */
	private float horizontalAccuracy;

	/**
	 * The vertical accuracy of this location result in meters. <code>Float.Nan</code>
	 * can be used to indicate that the accuracy is not known. Must be greater
	 * or equal to 0
	 */
	private float verticalAccuracy;

	/**
	 * Constructs a new <code>QualifiedCoordinates</code> object with the values
	 * specified. The latitude and longitude parameters are expressed in degrees
	 * using floating point values. The degrees are in decimal values (rather
	 * than minutes/seconds).
	 * <p>
	 * The coordinate values always apply to the WGS84 datum.
	 * <p>
	 * The <code>Float.Nan</code> value can be used for altitude to indicate that
	 * altitude is not known.
	 * 
	 * @param latitude - the latitude of the location. Valid range: [-90.0,
	 *        90.0]. Positive values indicate northern latitude and negative
	 *        values southern latitude.
	 * @param longitude - the longitude of the location. Valid range: [-180.0,
	 *        180.0). Positive values indicate eastern longitude and negative
	 *        values western longitude
	 * @param altitude - the altitude of the location in meters, defined as
	 *        height above WGS84 ellipsoid. <code>Float.Nan</code> can be used to
	 *        indicate that altitude is not known.
	 * @param horizontalAccuracy - the horizontal accuracy of this location
	 *        result in meters. <code>Float.Nan</code> can be used to indicate that
	 *        the accuracy is not known. Must be greater or equal to 0.
	 * @param verticalAccuracy - the vertical accuracy of this location result
	 *        in meters. <code>Float.Nan</code> can be used to indicate that the
	 *        accuracy is not known. Must be greater or equal to 0.
	 * @throws java.lang.IllegalArgumentException - if an input parameter is out
	 *         of the valid range
	 */
	public QualifiedCoordinates (double latitude, double longitude,
			float altitude, float horizontalAccuracy, float verticalAccuracy)
	{
		super( latitude, longitude, altitude );
		setHorizontalAccuracy( horizontalAccuracy );
		setVerticalAccuracy( verticalAccuracy );
	}

	/**
	 * Returns the horizontal accuracy of the location in meters (1-sigma
	 * standard deviation). A value of <code>Float.Nan</code> means the horizontal
	 * accuracy could not be determined.
	 * <p>
	 * The horizontal accuracy is the RMS (root mean square) of east accuracy
	 * (latitudinal error in meters, 1-sigma standard deviation), north accuracy
	 * (longitudinal error in meters, 1-sigma).
	 * 
	 * @return the horizontal accuracy in meters. <code>Float.Nan</code> if this is
	 *         not known
	 */
	public float getHorizontalAccuracy ()
	{
		return horizontalAccuracy;
	}

	/**
	 * Sets the horizontal accuracy of the location in meters (1-sigma standard
	 * deviation). A value of <code>Float.Nan</code> means the horizontal accuracy
	 * could be determined.
	 * <p>
	 * The horizontal accuracy is the RMS (root mean square) of east accuracy
	 * (latitudinal error in meters, 1-sigma standard deviation), north accuracy
	 * (longitudinal error in meters, 1-sigma).
	 * 
	 * @param horizontalAccuracy - the horizontal accuracy of this location
	 *        result in meters. <code>Float.Nan</code> means the horizontal accuracy
	 *        could not be determined. Must be greater or equal to 0.
	 * @throws java.lang.IllegalArgumentException - if an input parameter is out
	 *         of the valid range
	 */
	public void setHorizontalAccuracy (float horizontalAccuracy)
	{
		if ( Float.isNaN(horizontalAccuracy) || (horizontalAccuracy >= 0.0F) )
		{
			this.horizontalAccuracy = horizontalAccuracy;
		}
		else
		{
			throw new IllegalArgumentException("Horizontal accuracy (" + horizontalAccuracy + ") is invalid.");
		}
	}

	/**
	 * Returns the accuracy of the location in meters in vertical direction
	 * (orthogonal to ellipsoid surface, 1-sigma standard deviation). A value of
	 * <code>Float.Nan</code> means the vertical accuracy could not be determined.
	 * 
	 * @return the vertical accuracy in meters. <code>Float.Nan</code> if this is not
	 *         known.
	 */
	public float getVerticalAccuracy ()
	{
		return verticalAccuracy;
	}

	/**
	 * Sets the accuracy of the location in meters in vertical direction
	 * (orthogonal to ellipsoid surface, 1-sigma standard deviation). A value of
	 * <code>Float.Nan</code> means the vertical accuracy could be determined.
	 * 
	 * @param verticalAccuracy - the vertical accuracy of this location result
	 *        in meters. <code>Float.Nan</code> means the horizontal accuracy could
	 *        not be determined. Must be greater or equal to 0.
	 * @throws java.lang.IllegalArgumentException - if an input parameter is out
	 *         of the valid range
	 */
	public void setVerticalAccuracy (float verticalAccuracy)
	{
		if ( Float.isNaN(verticalAccuracy) || (verticalAccuracy >= 0.0F) )
		{
			this.verticalAccuracy = verticalAccuracy;
		}
		else
		{
			throw new IllegalArgumentException("Vertical accuracy (" + verticalAccuracy + ") is invalid.");
		}
	}

	/**
	 * Provides a string representation of the qualified coordinates.
	 * 
	 * @return A string such as "79.32°N 169.8°W 25.7m ±1.8mH ±0.7mV" where the
	 *         words are the latitude, longitude, altitude (in meters),
	 *         horizontal accuracy (in meters), and vertical accuracy (in
	 *         meters).
	 */
	public String toString ()
	{
		// Get the regular coordinates as a string.
		String s = super.toString();
		
		// Append the horizontal accuracy.
		if ( Float.isNaN(horizontalAccuracy) == false )
		{
			s += " ±" + horizontalAccuracy + "mH";
		}
		
		// Append the vertical accuracy.
		if ( Float.isNaN(verticalAccuracy) == false )
		{
			s += " ±" + verticalAccuracy + "mV";
		}
		
		return s;
	}
}
