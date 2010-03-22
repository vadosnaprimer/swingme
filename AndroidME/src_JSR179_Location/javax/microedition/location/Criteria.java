package javax.microedition.location;

/**
 * The criteria used for the selection of the location provider is defined by
 * the values in this class. It is up to the implementation to provide a
 * <code>LocationProvider</code> that can obtain locations constrained by these
 * values.
 * <p>
 * Instances of <code>Criteria</code> are used by the application to indicate
 * criteria for choosing the location provider in the
 * <code>LocationProvider.getInstance</code> method call. The implementation
 * considers the different criteria fields to choose the location provider that
 * best fits the defined criteria. The different criteria fields do not have any
 * defined priority order but the implementation uses some implementation
 * specific logic to choose the location provider that can typically best meet
 * the defined criteria.
 * <p>
 * However, the cost criteria field is treated differently from others. If the
 * application has set the cost field to indicate that the returned location
 * provider is not allowed to incur financial cost to the end user, the
 * implementation MUST guarantee that the returned location provider does not
 * incur cost.
 * <p>
 * If there is no available location provider that is able to meet all the
 * specified criteria, the implementation is allowed to make its own best effort
 * selection of a location provider that is closest to the defined criteria
 * (provided that the cost criteria is met). However, an implementation is not
 * required to return a location provider if it does not have any available
 * provider that is able to meet these criteria or be sufficiently close to
 * meeting them, where the judgement of sufficiently close is an implementation
 * dependent best effort choice. It is left up to the implementation to consider
 * what is close enough to the specified requirements that it is worth providing
 * the location provider to the application.
 * <p>
 * The default values for the criteria fields are specified below in the table.
 * The default values are always the least restrictive option that will match
 * all location providers. Default values:
 * <p>
 * <table border="1">
 *  <tr>
 *   <th align="left">Criteria field</th>
 *   <th align="left">Default value</th>
 *  </tr>
 *  <tr>
 *   <td>Horizontal accuracy</td>
 *   <td>NO_REQUIREMENT</td>
 *  </tr>
 *  <tr>
 *   <td>Vertical accuracy</td>
 *   <td>NO_REQUIREMENT</td>
 *  </tr>
 *  <tr>
 *   <td>Preferred response time</td>
 *   <td>NO_REQUIREMENT</td>
 *  </tr>
 *  <tr>
 *   <td>Power consumption</td>
 *   <td>NO_REQUIREMENT</td>
 *  </tr>
 *  <tr>
 *   <td>Cost allowed</td>
 *   <td>true (allowed to cost)</td>
 *  </tr>
 *  <tr>
 *   <td>Speed and course required</td>
 *   <td>false (not required)</td>
 *  </tr>
 *  <tr>
 *   <td>Altitude required</td>
 *   <td>false (not required)</td>
 *  </tr>
 *  <tr>
 *   <td>Address info required</td>
 *   <td>false (not required)</td>
 *  </tr>
 *  <tr>
 *   <td>Allow local LBS</td>
 *   <td>true (use the LBS on the device running this MIDlet if it meets the other criteria)</td>
 *  </tr>
 *  <tr>
 *   <td>Remote LBS address</td>
 *   <td>null (only use the local LBS on the device running this MIDlet)</td>
 *  </tr>
 * </table>
 * <p>
 * The implementation of this class only retains the values that are passed in
 * using the <code>set*</code> methods. It does not try to validate the values of the
 * parameters in any way. Applications may set any values it likes, even
 * negative values, but the consequence may be that no matching
 * <code>LocationProvider</code> can be created.
 */
public class Criteria
{
	/**
	 * Constant indicating no requirements for the parameter.
	 */
	public static final int NO_REQUIREMENT = 0;

	/**
	 * Level indicating only low power consumption allowed.
	 */
	public static final int POWER_USAGE_LOW = 1;

	/**
	 * Level indicating average power consumption allowed.
	 */
	public static final int POWER_USAGE_MEDIUM = 2;

	/**
	 * Level indicating high power consumption allowed.
	 */
	public static final int POWER_USAGE_HIGH = 3;

	/**
	 * The horizontal accuracy preference measured in meters. The preference
	 * indicates maximum allowed typical 1-sigma standard deviation for the
	 * location method.
	 */
	private int horizontalAccuracy = NO_REQUIREMENT;

	/**
	 * The vertical accuracy preference measured in meters. The preference
	 * indicates maximum allowed typical 1-sigma standard deviation for the
	 * location method.
	 */
	private int verticalAccuracy = NO_REQUIREMENT;

	/**
	 * The desired maximum response time preference in milliseconds.
	 */
	private int maxResponseTime = NO_REQUIREMENT;

	/**
	 * The preferred maximum level of power consumption. Should be one of
	 * <code>NO_REQUIREMENT</code>, <code>POWER_USAGE_LOW</code>, <code>POWER_USAGE_MEDIUM</code>,
	 * <code>POWER_USAGE_HIGH</code>.
	 */
	private int powerConsumption = NO_REQUIREMENT;

	/**
	 * Whether getting the location is allowed to cost the user.
	 */
	private boolean costAllowed = true;

	/**
	 * Whether the speed and course are required.
	 */
	private boolean speedAndCourseRequired = false;

	/**
	 * Whether the altitude is required.
	 */
	private boolean altitudeRequired = false;

	/**
	 * Whether the location provider should be able to determine textual address
	 * information. Setting this criteria to <code>true</code> implies that a
	 * location provider should be selected that is capable of providing the
	 * textual address information. This does not mean that every returned
	 * location instance necessarily will have all the address information
	 * filled in, though.
	 */
	private boolean addressInfoRequired = false;
	
	/**
	 * When <code>true</code> the LBS on this device should be used if it meets the
	 * other criteria.  Local LBS gets preference over any remote LBS specified
	 * by in <code>remoteDeviceAddress</code>.
	 * <p>
	 * To force remote LBS to be used set this value to <code>false</code> and provide
	 * a remote address in <code>remoteDeviceAddress</code>.
	 */
	private boolean allowLocalLBS = true;
	
	/**
	 * The Bluetooth address of the remote GPS device to use if no local LBS
	 * matches the other criteria.  If this is <code>null</code> than only local LBS
	 * can be used.
	 */
	private String remoteDeviceAddress = null;

	/**
	 * Constructs a <code>Criteria</code> object. All the fields are set to the
	 * default values that are specified below in the specification of the
	 * <code>set*</code> methods for the parameters.
	 */
	public Criteria ()
	{
	}

	/**
	 * Returns the preferred power consumption.
	 * 
	 * @return the power consumption level, should be one of <code>NO_REQUIREMENT</code>,
	 *         <code>POWER_USAGE_LOW</code>, <code>POWER_USAGE_MEDIUM</code>,
	 *         <code>POWER_USAGE_HIGH</code>.
	 * @see #setPreferredPowerConsumption(int)
	 */
	public int getPreferredPowerConsumption ()
	{
		return powerConsumption;
	}

	/**
	 * Returns the preferred cost setting.
	 * 
	 * @return the preferred cost setting. <code>true</code> if allowed to cost,
	 *         <code>false</code> if it must be free of charge.
	 * @see #setCostAllowed(boolean)
	 */
	public boolean isAllowedToCost ()
	{
		return costAllowed;
	}

	/**
	 * Returns the vertical accuracy value set in this <code>Criteria</code>.
	 * 
	 * @return the accuracy in meters.
	 * @see #setVerticalAccuracy(int)
	 */
	public int getVerticalAccuracy ()
	{
		return verticalAccuracy;
	}

	/**
	 * Returns the horizontal accuracy value set in this <code>Criteria</code>.
	 * 
	 * @return the horizontal accuracy in meters.
	 * @see #setHorizontalAccuracy(int)
	 */
	public int getHorizontalAccuracy ()
	{
		return horizontalAccuracy;
	}

	/**
	 * Returns the preferred maximum response time.
	 * 
	 * @return the maximum response time in milliseconds.
	 * @see #setPreferredResponseTime(int)
	 */
	public int getPreferredResponseTime ()
	{
		return maxResponseTime;
	}

	/**
	 * Returns whether the location provider should be able to determine speed
	 * and course.
	 * 
	 * @return whether the location provider should be able to determine speed
	 *         and course. <code>true</code> means that it should be able, <code>false</code>
	 *         means that this is not required.
	 * @see #setSpeedAndCourseRequired(boolean)
	 */
	public boolean isSpeedAndCourseRequired ()
	{
		return speedAndCourseRequired;
	}

	/**
	 * Returns whether the location provider should be able to determine
	 * altitude.
	 * 
	 * @return whether the location provider should be able to determine
	 *         altitude. <code>true</code> means that it should be able, <code>false</code>
	 *         means that this is not required.
	 * @see #setAltitudeRequired(boolean)
	 */
	public boolean isAltitudeRequired ()
	{
		return altitudeRequired;
	}

	/**
	 * Returns whether the location provider should be able to determine textual
	 * address information.
	 * 
	 * @return whether the location provider should be able to normally provide
	 *         textual address information. <code>true</code> means that it should be
	 *         able, <code>false</code> means that this is not required.
	 * @see #setAddressInfoRequired(boolean)
	 */
	public boolean isAddressInfoRequired ()
	{
		return addressInfoRequired;
	}
	
	/**
	 * Returns if the LBS on this device should be used if it meets the
	 * other criteria.  Local LBS gets preference over any remote LBS specified
	 * by in <code>remoteDeviceAddress</code>.
	 * <p>
	 * If <code>false</code> only the remote LBS at <code>setRemoteDeviceAddress</code> can
	 * be used.
	 * 
	 * @return <code>true</code> if local LBS can be used; <code>false</code> if remote LBS
	 *  must be used.
	 */
	public boolean isLocalLBSAllowed ()
	{
		return allowLocalLBS;
	}
	
	/**
	 * Returns the Bluetooth address of the remote GPS device to use if no
	 * local LBS matches the other criteria.  If this is <code>null</code> than only
	 * local LBS can be used.
	 * 
	 * @return the URL of a remote LBS device.  Typically this is a Bluetooth
	 *  URL obtained from the <code>LocationProvider.discoverBluetoothDevices</code>
	 *  method.  If this is <code>null</code> than only local LBS will be used.
	 * @see #setRemoteDeviceAddress(String)
	 */
	public String getRemoteDeviceAddress ()
	{
		return remoteDeviceAddress;
	}

	/**
	 * Sets the desired horizontal accuracy preference. Accuracy is measured in
	 * meters. The preference indicates maximum allowed typical 1-sigma standard
	 * deviation for the location method. Default is <code>NO_REQUIREMENT</code>,
	 * meaning no preference on horizontal accuracy.
	 * 
	 * @param accuracy - the preferred horizontal accuracy in meters
	 * @see #getHorizontalAccuracy()
	 */
	public void setHorizontalAccuracy (int accuracy)
	{
		this.horizontalAccuracy = accuracy;
	}

	/**
	 * Sets the desired vertical accuracy preference. Accuracy is measured in
	 * meters. The preference indicates maximum allowed typical 1-sigma standard
	 * deviation for the location method. Default is NO_REQUIREMENT, meaning no
	 * preference on vertical accuracy.
	 * 
	 * @param accuracy - the preferred vertical accuracy in meters
	 * @see #getVerticalAccuracy()
	 */
	public void setVerticalAccuracy (int accuracy)
	{
		this.verticalAccuracy = accuracy;
	}

	/**
	 * Sets the desired maximum response time preference. This value is
	 * typically used by the implementation to determine a location method that
	 * typically is able to produce the location information within the defined
	 * time. Default is <code>NO_REQUIREMENT</code>, meaning no response time
	 * constraint.
	 * 
	 * @param time - the preferred time constraint and timeout value in
	 *        milliseconds
	 * @see #getPreferredResponseTime()
	 */
	public void setPreferredResponseTime (int time)
	{
		this.maxResponseTime = time;
	}

	/**
	 * Sets the preferred maximum level of power consumption.
	 * <p>
	 * These levels are inherently indeterminable and depend on many factors. It
	 * is the judgement of the implementation that defines a positioning method
	 * as consuming low power or high power. Default is <code>NO_REQUIREMENT</code>,
	 * meaning power consumption is not a quality parameter.
	 * 
	 * @param level - the preferred maximum level of power consumption. Should
	 *        be one of <code>NO_REQUIREMENT</code>, <code>POWER_USAGE_LOW</code>,
	 *        <code>POWER_USAGE_MEDIUM</code>, <code>POWER_USAGE_HIGH</code>.
	 * @see #getPreferredPowerConsumption()
	 */
	public void setPreferredPowerConsumption (int level)
	{
		this.powerConsumption = level;
	}

	/**
	 * Sets the preferred cost setting.
	 * <p>
	 * Sets whether the requests for location determination is allowed to incur
	 * any financial cost to the user of the terminal.
	 * <p>
	 * The default is true, i.e. the method is allowed to cost.
	 * <p>
	 * Note that the platform implementation may not always be able to know if a
	 * location method implies cost to the end user or not. If the
	 * implementation doesn't know, it MUST assume that it may cost. When this
	 * criteria is set to false, the implementation may only return a
	 * <code>LocationProvider</code> of which it is certain that using it for
	 * determining the location does not cause a per usage cost to the end user.
	 * 
	 * @param costAllowed - <code>false</code> if location determination is not
	 *        allowed to cost, <code>true</code> if it is allowed to cost
	 * @see #isAllowedToCost()
	 */
	public void setCostAllowed (boolean costAllowed)
	{
		this.costAllowed = costAllowed;
	}

	/**
	 * Sets whether the location provider should be able to determine speed and
	 * course. Default is <code>false</code>.
	 * 
	 * @param speedAndCourseRequired - if set to <code>true</code>, the
	 *        <code>LocationProvider</code> is required to be able to normally
	 *        determine the speed and course. if set the <code>false</code>, the
	 *        speed and course are not required.
	 * @see #isSpeedAndCourseRequired()
	 */
	public void setSpeedAndCourseRequired (boolean speedAndCourseRequired)
	{
		this.speedAndCourseRequired = speedAndCourseRequired;
	}

	/**
	 * Sets whether the location provider should be able to determine altitude.
	 * <p>
	 * Default is <code>false</code>.
	 * 
	 * @param altitudeRequired - if set to <code>true</code>, the <code>LocationProvider</code>
	 *        is required to be able to normally determine the altitude if set
	 *        the <code>false</code>, the altitude is not required.
	 * @see #isAltitudeRequired()
	 */
	public void setAltitudeRequired (boolean altitudeRequired)
	{
		this.altitudeRequired = altitudeRequired;
	}

	/**
	 * Sets whether the location provider should be able to determine textual
	 * address information. Setting this criteria to <code>true</code> implies that a
	 * location provider should be selected that is capable of providing the
	 * textual address information. This does not mean that every returned
	 * location instance necessarily will have all the address information
	 * filled in, though.
	 * <p>
	 * Default is <code>false</code>.
	 * 
	 * @param addressInfoRequired - if set to <code>true</code>, the
	 *        <code>LocationProvider</code> is required to be able to normally
	 *        determine the textual address information. If set the <code>false</code>,
	 *        the textual address information is not required.
	 * @see #isAddressInfoRequired()
	 */
	public void setAddressInfoRequired (boolean addressInfoRequired)
	{
		this.addressInfoRequired = addressInfoRequired;
	}
	
	/**
	 * Sets if the LBS on this device should be used if it meets the
	 * other criteria.  Local LBS gets preference over any remote LBS specified
	 * by in <code>remoteDeviceAddress</code>.
	 * <p>
	 * To force remote LBS to be used set this value to <code>false</code> and provide
	 * a remote address to <code>setRemoteDeviceAddress</code>.
	 * <p>
	 * Default is <code>true</code>.
	 * 
	 * @param allowLocalLBS when <code>true</code> uses LBS on the current device
	 *  if it meets the other criteria; when <code>false</code> forces remote LBS
	 *  to be used.
	 */
	public void setAllowLocalLBS (boolean allowLocalLBS)
	{
		this.allowLocalLBS = allowLocalLBS;
	}
	
	/**
	 * Sets the Bluetooth address of the remote GPS device.  This address
	 * can be obtained from the <code>LocationProvider.discoverBluetoothDevices</code>
	 * method.  Once the address is known, it should be stored using the
	 * Record Management System (RMS) for the next time the MIDlet is run.
	 * If this value is set to <code>null</code>, the default, than only LBS providers
	 * built into the device running the MIDlet will be used. 
	 * 
	 * @param address - the Bluetooth address of the remote GPS device or
	 *  <code>null</code> if only local LBS can be used.
	 * @see #getRemoteDeviceAddress
	 */
	public void setRemoteDeviceAddress (String address)
	{
		this.remoteDeviceAddress = address;
	}
}
