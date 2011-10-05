package javax.microedition.location;

import net.yura.android.AndroidMeApp;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationProvider implements android.location.LocationListener {

    public static final int AVAILABLE = android.location.LocationProvider.AVAILABLE;
    public static final int OUT_OF_SERVICE = android.location.LocationProvider.OUT_OF_SERVICE;
    public static final int TEMPORARILY_UNAVAILABLE = android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

    private static final float MINIMUM_MOVEMENT_IN_METRES = 10;
    private int interval;
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
        return new LocationImpl(getLocationImpl());
    }

    private android.location.Location getLocationImpl() {
        android.location.Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return loc;
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

        // Clean up any older listener
        if (this.locationListener != null) {
            lm.removeUpdates(this);
        }

        this.locationListener = listener;
        this.interval = interval;

        // To save battery we always start to use network. If it turns out to
        // be unavailable, we switch to GPS
        registerForLocationUpdates(LocationManager.NETWORK_PROVIDER, interval);
    }

    private void registerForLocationUpdates(String provider, int interval) {
        if (locationListener == null) {
            lm.removeUpdates(this);
        } else {
        	
        	try {
        		// this can throw "java.lang.IllegalArgumentException: provider=gps" on the google TV
        		lm.requestLocationUpdates(provider, interval * 1000, MINIMUM_MOVEMENT_IN_METRES, this, AndroidMeApp.getIntance().getLooper());
        	}
        	catch (Throwable th) {
        		th.printStackTrace();
        	}
        }
    }

    /*
     * Private methods to support the implementation
     */
    private LocationProvider(Criteria cr) throws LocationException {
        lm = (LocationManager) AndroidMeApp.getContext().getSystemService(Context.LOCATION_SERVICE);
        setLastKnownLocation(getLocationImpl());
    }

    private void setLastKnownLocation(android.location.Location loc) {
        if (loc == null) {
            lastKnownLocation = null;
        }
        else {
            lastKnownLocation = new LocationImpl(loc);
        }
    }

    public void onLocationChanged(android.location.Location location) {
        setLastKnownLocation(location);
        if (locationListener != null) {
            locationListener.locationUpdated(this, lastKnownLocation);
        }
    }

    public void onProviderDisabled(String provider) {
        status = TEMPORARILY_UNAVAILABLE;

         if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
             // Network not available, fall back to GPS! Increase pooling time.
             registerForLocationUpdates(LocationManager.GPS_PROVIDER, 5*interval);
         }
    }

    public void onProviderEnabled(String provider) {
        status = AVAILABLE;

        if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            // Network is available again. Unregister from GPS.
            lm.removeUpdates(this);
            registerForLocationUpdates(LocationManager.NETWORK_PROVIDER, interval);
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        this.status = status;
        if (locationListener != null) {
            locationListener.providerStateChanged(this, status);
        }
    }
}
