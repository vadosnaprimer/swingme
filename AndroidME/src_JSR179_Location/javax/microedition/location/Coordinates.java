package javax.microedition.location;


/**
 * The <code>Coordinates</code> class represents coordinates as
 * latitude-longitude-altitude values. The latitude and longitude values are
 * expressed in degrees using floating point values. The degrees are in decimal
 * values (rather than minutes/seconds). The coordinates are given using the
 * WGS84 datum.
 * <p>
 * This class also provides convenience methods for converting between a string
 * coordinate representation and the <code>double</code> representation used in this
 * class.
 */
public class Coordinates
{
	/**
	 * This is the earth's mean radius in meters.  Using the mean gives the most
	 * accurate results for distances measured with any bearing.
	 * <p>
	 * In truth the earth is not a perfect sphere.  The radius of the equator
	 * is 6,378,137 and the polar radius is 6,356,752.3142.  The FAI's definition
	 * of 6,371,000 lies between them.
	 */
	private static final double METERS_PER_RADIAN = 6371000;

	/**
	 * Identifier for string coordinate representation Degrees, Minutes, Seconds
	 * and decimal fractions of a second.
	 */
	public static final int DD_MM_SS = 1;

	/**
	 * Identifier for string coordinate representation Degrees, Minutes, decimal
	 * fractions of a minute.
	 */
	public static final int DD_MM = 2;

	/**
	 * The altitude of the location in meters, defined as height above WGS84
	 * ellipsoid. <code>Float.Nan</code> can be used to indicate that altitude is not
	 * known.
	 */
	private float altitude;

	/**
	 * The latitude of the location. Valid range: [-90.0, 90.0]. Positive values
	 * indicate northern latitude and negative values southern latitude.
	 */
	private double latitude;

	/**
	 * The longitude of the location. Valid range: [-180.0, 180.0). Positive
	 * values indicate eastern longitude and negative values western longitude.
	 */
	private double longitude;

	/**
	 * Constructs a new <code>Coordinates</code> object with the values specified.
	 * The latitude and longitude parameters are expressed in degrees using
	 * floating point values. The degrees are in decimal values (rather than
	 * minutes/seconds).
	 * <p>
	 * The coordinate values always apply to the WGS84 datum.
	 * <p>
	 * The <code>Float.NaN</code> value can be used for altitude to indicate that
	 * altitude is not known.
	 * 
	 * @param latitude - the latitude of the location. Valid range: [-90.0,
	 *        90.0]. Positive values indicate northern latitude and negative
	 *        values southern latitude.
	 * @param longitude - the longitude of the location. Valid range: [-180.0,
	 *        180.0). Positive values indicate eastern longitude and negative
	 *        values western longitude.
	 * @param altitude - the altitude of the location in meters, defined as
	 *        height above WGS84 ellipsoid. <code>Float.Nan</code> can be used to
	 *        indicate that altitude is not known.
	 * @throws java.lang.IllegalArgumentException - if an input parameter is out
	 *         of the valid range.
	 */
	public Coordinates (double latitude, double longitude, float altitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}

	/**
	 * Returns the latitude component of this coordinate. Positive values
	 * indicate northern latitude and negative values southern latitude.
	 * <p>
	 * The latitude is given in WGS84 datum.
	 * 
	 * @return the latitude in degrees
	 * @see #setLatitude(double)
	 */
	public double getLatitude ()
	{
		return latitude;
	}

	/**
	 * Returns the longitude component of this coordinate. Positive values
	 * indicate eastern longitude and negative values western longitude.
	 * <p>
	 * The longitude is given in WGS84 datum.
	 * 
	 * @return the longitude in degrees
	 * @see #setLongitude(double)
	 */
	public double getLongitude ()
	{
		return longitude;
	}

	/**
	 * Returns the altitude component of this coordinate. Altitude is defined to
	 * mean height above the WGS84 reference ellipsoid. 0.0 means a location at
	 * the ellipsoid surface, negative values mean the location is below the
	 * ellipsoid surface, <code>Float.Nan</code> that no altitude is not available.
	 * 
	 * @return the altitude in meters above the reference ellipsoid
	 * @see #setAltitude(float)
	 */
	public float getAltitude ()
	{
		return altitude;
	}

	/**
	 * Sets the geodetic altitude for this point.
	 * 
	 * @param altitude - the altitude of the location in meters, defined as
	 *        height above the WGS84 ellipsoid. 0.0 means a location at the
	 *        ellipsoid surface, negative values mean the location is below the
	 *        ellipsoid surface, <code>Float.Nan</code> that no altitude is not
	 *        available
	 * @see #getAltitude()
	 */
	public void setAltitude (float altitude)
	{
		this.altitude = altitude;
	}

	/**
	 * Sets the geodetic latitude for this point. Latitude is given as a double
	 * expressing the latitude in degrees in the WGS84 datum.
	 * 
	 * @param latitude - the latitude component of this location in degrees.
	 *        Valid range: [-90.0, 90.0].
	 * @throws java.lang.IllegalArgumentException - if latitude is out of the
	 *         valid range
	 * @see #getLatitude()
	 */
	public void setLatitude (double latitude)
	{
		if ( Double.isNaN(latitude) || (latitude < -90.0 || latitude >= 90.0) )
		{
			throw new IllegalArgumentException("Latitude (" + latitude + ") is invalid.");
		}
		else
		{
			this.latitude = latitude;
		}
	}

	/**
	 * Sets the geodetic longitude for this point. Longitude is given as a
	 * double expressing the longitude in degrees in the WGS84 datum.
	 * 
	 * @param longitude - the longitude of the location in degrees. Valid range:
	 *        [-180.0, 180.0)
	 * @throws java.lang.IllegalArgumentException - if longitude is out of the
	 *         valid range
	 * @see #getLongitude()
	 */
	public void setLongitude (double longitude)
	{
		if ( Double.isNaN(longitude) || (longitude < -180.0 || longitude >= 180.0) )
		{
			throw new IllegalArgumentException("Longitude (" + longitude + ") is invalid.");
		}
		else
		{
			this.longitude = longitude;
		}
	}

	/**
	 * Calculates the azimuth between the two points according to the ellipsoid
	 * model of WGS84. The azimuth is relative to true north. The Coordinates
	 * object on which this method is called is considered the origin for the
	 * calculation and the Coordinates object passed as a parameter is the
	 * destination which the azimuth is calculated to. When the origin is the
	 * North pole and the destination is not the North pole, this method returns
	 * 180.0. When the origin is the South pole and the destination is not the
	 * South pole, this method returns 0.0. If the origin is equal to the
	 * destination, this method returns 0.0. The implementation shall calculate
	 * the result as exactly as it can. However, it is required that the result
	 * is within 1 degree of the correct result.
	 * 
	 * @param to - the <code>Coordinates</code> of the destination
	 * @return the azimuth to the destination in degrees. Result is within the
	 *         range [0.0 ,360.0).
	 * @throws java.lang.NullPointerException - if the parameter is <code>null</code>
	 */
	public float azimuthTo (Coordinates to)
	{
		throw new RuntimeException("Not implemted");
//		if ( to == null )
//		{
//			throw new IllegalArgumentException( "azimuthTo does not accept a null parameter." );
//		}
//		
//		// Convert from degrees to radians.
//		double lat1 = Math.toRadians( latitude );
//		double lon1 = Math.toRadians( longitude );
//		double lat2 = Math.toRadians( to.latitude );
//		double lon2 = Math.toRadians( to.longitude );
//		
//		// Formula for computing the course between two points.
//		// It is explained in detail here:
//		//   http://williams.best.vwh.net/avform.htm
//		//   http://www.movable-type.co.uk/scripts/LatLong.html
//		// course = atan2(
//		//            sin(lon2-lon1)*cos(lat2),                                 // c1
//		//            cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(lon2-lon1))   // c2
//		
//		double deltaLon = lon2 - lon1;
//		double cosLat2 = Math.cos( lat2 );
//		double c1 = Math.sin(deltaLon) * cosLat2;
//		double c2 = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * cosLat2 * Math.cos(deltaLon);
//		double courseInRadians = MathFunc.atan2( c1, c2 );
//		
//		double course = Math.toDegrees( courseInRadians );
//		course = (360.0 + course) % 360.0;  // Normalize to [0,360)
//		return (float)course;
	}
	
	/**
	 * Calculates the geodetic distance between the two points according to the
	 * ellipsoid model of WGS84. Altitude is neglected from calculations.
	 * <p>
	 * The implementation shall calculate this as exactly as it can. However, it
	 * is required that the result is within 0.35% of the correct result.
	 * 
	 * @param to - the <code>Coordinates</code> of the destination
	 * @return the distance to the destination in meters
	 * @throws java.lang.NullPointerException - if the parameter is <code>null</code>
	 */
	public float distance (Coordinates to)
	{
		throw new RuntimeException("Not implemted");
//		if ( to == null )
//		{
//			throw new IllegalArgumentException( "distance does not accept a null parameter." );
//		}
//		
//		// Convert from degrees to radians.
//		double lat1 = Math.toRadians( latitude );
//		double lon1 = Math.toRadians( longitude );
//		double lat2 = Math.toRadians( to.latitude );
//		double lon2 = Math.toRadians( to.longitude );
//
//		// Use the Haversine formula for greater accuracy when measuring
//		// short distances.  It is explained in detail here:
//		//   http://williams.best.vwh.net/avform.htm
//		//   http://www.movable-type.co.uk/scripts/LatLong.html
//		// d = 2*asin(sqrt(
//		//          (sin((lat1-lat2)/2))^2 +    // d2
//		//          cos(lat1)*cos(lat2) *       // d3
//		//          (sin((lon1-lon2)/2))^2) )   // d5
//		
//		double d1 = Math.sin( (lat1 - lat2) / 2.0 );
//		double d2 = d1 * d1;
//		double d3 = Math.cos( lat1 ) * Math.cos( lat2 );
//		double d4 = Math.sin( (lon1 - lon2) / 2.0 );
//		double d5 = d4 * d4;
//		double d6 = d2 + d3 * d5;
//		double distanceInRadians = 2.0 * MathFunc.asin( Math.sqrt(d6) );
//		
//		double distance = METERS_PER_RADIAN * distanceInRadians; 
//		return (float)distance;
	}
	
	/**
	 * Compares if two <code>Coordinates</code> object reference the same location.
	 * 
	 * @param other is another <code>Coordinates</code> object.
	 * @return <code>true</code> if the two reference the same location;
	 *  <code>false</code> otherwise.
	 */
	public boolean equals (Object other)
	{
		// This is an allowable difference to account for floating point imprecisions.
		final double tolerance = 0.000001;
		
		if ( other == null )
		{
			return false;
		}

		if ( (other instanceof Coordinates) == false )
		{
			return false;
		}
		
		// Otherwise it is a Coordinates object.
		Coordinates c = (Coordinates)other;
		
		if ( (latitude < c.latitude - tolerance) || (latitude > c.latitude + tolerance) )
		{
			return false;
		}
		
		if ( (longitude < c.longitude - tolerance) || (longitude > c.longitude + tolerance) )
		{
			return false;
		}
		
		if ( (Float.isNaN(altitude) == true) && (Float.isNaN(c.altitude) == false) )
		{
			return false;
		}
		if ( (Float.isNaN(altitude) == false) && (Float.isNaN(c.altitude) == true) )
		{
			return false;
		}
		if ( Float.isNaN(altitude) && Float.isNaN(c.altitude) )
		{
			return true;
		}
		if ( (altitude < c.altitude - tolerance) || (altitude > c.altitude + tolerance) )
		{
			return false;
		}
		
		// If we got here the two coordinates are equal.
		return true;
	}
	
	/**
	 * Provides a string representation of the coordinates.
	 *
	 * @return A string such as "79.32°N 169.8°W 25.7m" where the words are the
	 *  latitude, longitude, and altitude (in meters).
	 */
	public String toString()
	{
		String s;
		
		// Add the latitude.
		if ( latitude >= 0.0 )
		{
			s = String.valueOf( latitude );
			s += "°N ";
		}
		else
		{
			s = String.valueOf( -1 * latitude );
			s += "°S ";
		}
		
		// Add the longitude.
		if ( longitude >= 0.0 )
		{
			s += String.valueOf( longitude );
			s += "°E";
		}
		else
		{
			s += String.valueOf( -1 * longitude );
			s += "°W";
		}
		
		// Add the altitude.
		if ( Float.isNaN( altitude ) == false )
		{
			s += (" " + altitude + "m");
		}
		
		return s;
	}
}
