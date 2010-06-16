package javax.microedition.location;


public abstract class LocationProvider {

	public static final int AVAILABLE = 1;
	public static final int OUT_OF_SERVICE = 3;
	public static final int TEMPORARILY_UNAVAILABLE	= 2;
	
	protected LocationProvider() {
		System.out.println("ME4SE: LocationProvider() NYI!");
	}

	public static void addProximityListener(ProximityListener listener, Coordinates coordinates, float proximityRadius) {
		System.out.println("ME4SE: LocationProvider.addProximityListener() NYI!");
	}

	public static LocationProvider getInstance(Criteria criteria) {
		System.out.println("ME4SE: LocationProvider.getInstance() called!");
		return new LocationProviderImpl(criteria);
	}
	
	public static Location getLastKnownLocation() {
		System.out.println("ME4SE: LocationProvider.getLastKnownLocation() NYI!");
		return null;
	}
	
	public abstract Location getLocation(int timeout);
	
	public abstract int getState();
	
	public static void removeProximityListener(ProximityListener listener) {
		System.out.println("ME4SE: LocationProvider.removeProximityListener() NYI!");
	}
	
	public abstract void reset();

	public abstract void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge);
}