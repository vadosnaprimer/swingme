package javax.microedition.location;

import net.yura.android.AndroidMeActivity;
import net.yura.android.AndroidMeApp;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationProvider implements android.location.LocationListener {

	public static final int AVAILABLE = android.location.LocationProvider.AVAILABLE;
	public static final int OUT_OF_SERVICE = android.location.LocationProvider.OUT_OF_SERVICE;
	public static final int TEMPORARILY_UNAVAILABLE = android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

	private static final float MINIMUM_MOVEMENT_IN_METRES = 10;
	private static String locationProvider;
	private static Location lastKnownLocation;

	private final LocationManager lm;
	private LocationListener locationListener;
	private int status;

	/*
	 * Implementation of JSR-179
	 */
	static void addProximityListener(ProximityListener listener,
			Coordinates coordinates, float proximityRadius) {
		throw new UnsupportedOperationException();
	}

	public static LocationProvider getInstance(Criteria cr) throws LocationException {
		return new LocationProvider(cr);

	}

	public static Location getLastKnownLocation() {
		return lastKnownLocation;
	}

	public Location getLocation(int timeout) {
		android.location.Location loc = lm
				.getLastKnownLocation(locationProvider);
		Location location = new LocationImpl(loc);
		return location;
	}

	public int getState() {
		return status;
	}

	public static void removeProximityListener(ProximityListener listener) {
		throw new UnsupportedOperationException();
	}

	public void reset() {
		lm.removeUpdates(this);
	}

	public void setLocationListener(final LocationListener listener, final int interval,
			int timeout, int maxAge) {
		this.locationListener = listener;
		final LocationProvider provider = this;
		if (locationListener != null) {
			AndroidMeApp.getIntance().invokeLater( new Runnable() {
				public void run() {
					lm.requestLocationUpdates(locationProvider, interval * 1000, MINIMUM_MOVEMENT_IN_METRES, provider);
				}
			});
			// lm.requestLocationUpdates(locationProvider, interval * 1000, MINIMUM_MOVEMENT_IN_METRES, this);
		} else {
			lm.removeUpdates(this);
		}
	}

	/*
	 * Private methods to support the implementation
	 */
	private LocationProvider(Criteria cr) throws LocationException {
		lm = getLocationManager();
		locationProvider = lm.getBestProvider(cr.getAndroidCriteria(), false);
		android.location.Location loc = lm.getLastKnownLocation(locationProvider);
		setLastKnownLocation(loc);
	}

	private void setLastKnownLocation(android.location.Location loc) {
		if(loc != null) {
			lastKnownLocation = new LocationImpl(loc);
		}
		else {
			lastKnownLocation = null;
		}
	}

	private static LocationManager getLocationManager() {
		return (LocationManager) AndroidMeApp.getContext().getSystemService(Context.LOCATION_SERVICE);
	}

	public void onLocationChanged(android.location.Location location) {
		setLastKnownLocation(location);
		if (locationListener != null) {
			locationListener.locationUpdated(this, lastKnownLocation);
		}
	}

	public void onProviderDisabled(String provider) {
		status = TEMPORARILY_UNAVAILABLE;
	}

	public void onProviderEnabled(String provider) {
		status = AVAILABLE;
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		this.status = status;
		if (locationListener != null) {
			locationListener.providerStateChanged(this, status);
		}
	}

}
