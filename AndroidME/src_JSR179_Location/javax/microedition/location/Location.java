package javax.microedition.location;

/**
 * The <code>Location</code> class represents the standard set of basic location
 * information. This includes the timestamped coordinates, accuracy, speed,
 * course, and information about the positioning method used for the location,
 * plus an optional textual address.
 * <p>
 * The location method is indicated using a bit field. The individual bits are
 * defined using constants in this class. This bit field is a bitwise
 * combination of the location method technology bits (MTE_*), method type
 * (MTY_*) and method assistance information (MTA_*). All other bits in the 32
 * bit integer than those that have defined constants in this class are reserved
 * and MUST not be set by implementations (i.e. these bits must be 0).
 * <p>
 * A <code>Location</code> object may be either 'valid' or 'invalid'. The validity
 * can be queried using the <code>isValid</code> method. A valid <code>Location</code>
 * object represents a location with valid coordinates and the
 * <code>getQualifiedCoordinates</code> method must return their coordinates. An
 * invalid <code>Location</code> object doesn't have valid coordinates, but the extra
 * info that is obtained from the <code>getExtraInfo</code> method can provide
 * information about the reason why it was not possible to provide a valid
 * <code>Location</code>. For an invalid <code>Location</code> object, the
 * <code>getQualifiedCoordinates</code> method may return either <code>null</code> or some
 * coordinates where the information is not necessarily fully correct. The
 * periodic location updates to the <code>LocationListener</code> may return invalid
 * <code>Location</code> objects if it isn't possible to determine the location.
 * <p>
 * This class is only a container for the information. When the platform
 * implementation returns <code>Location</code> objects, it MUST ensure that it only
 * returns objects where the parameters have values set as described for their
 * semantics in this class.
 */
public interface Location
{
	/**
	 * Returns the coordinates of this location and their accuracy.
	 * 
	 * @return a <code>QualifiedCoordinates</code> object. If the coordinates are not
	 *  known, returns <code>null</code>.
	 */
	public QualifiedCoordinates getQualifiedCoordinates ();

	/**
	 * Returns the terminal's current ground speed in meters per second (m/s) at
	 * the time of measurement. The speed is always a non-negative value. Note
	 * that unlike the coordinates, speed does not have an associated accuracy
	 * because the methods used to determine the speed typically are not able to
	 * indicate the accuracy.
	 * 
	 * @return the current ground speed in m/s for the terminal or <code>Float.NaN</code>
	 *  if the speed is not known
	 */
	public float getSpeed ();
	
	/**
	 * Returns the terminal's course made good in degrees relative to true north.
	 * The value is always in the range [0.0,360.0) degrees.
	 * 
	 * @return the terminal's course made good in degrees relative to true north
	 *  or <code>Float.NaN</code> if the course is not known
	 */
	public float getCourse ();
	
	/**
	 * Returns the time stamp at which the data was collected.  This timestamp 
	 * should represent the point in time when the measurements were made.
	 * Implementations make best effort to set the timestamp as close to this 
	 * point in time as possible.  The time returned is the time of the local 
	 * clock in the terminal in milliseconds using the same clock and same time 
	 * representation as <code>System.currentTimeMillis()</code>.
	 * 
	 * @return a timestamp representing the time
	 * @see java.lang.System#currentTimeMillis()
	 */
	public long getTimestamp ();

	/**
	 * Returns whether this Location instance represents a valid location with
	 * coordinates or an invalid one where all the data, especially the latitude
	 * and longitude coordinates, may not be present.
	 *
	 * A valid <code>Location</code> object contains valid coordinates whereas an
	 * invalid <code>Location</code> object may not contain valid coordinates but
	 * may contain other information via the <code>getExtraInfo()</code> method to
	 * provide information on why it was not possible to provide a valid
	 * <code>Location</code> object.
	 * 
	 * @return a boolean value with true indicating that this <code>Location</code>
	 *  instance is valid and false indicating an invalid <code>Location</code>
	 *  instance
	 */
	public boolean isValid ();
}
