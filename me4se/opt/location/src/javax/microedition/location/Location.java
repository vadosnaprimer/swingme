package javax.microedition.location;

public class Location {

	public static final int MTA_ASSISTED = 262144;
	public static final int MTA_UNASSISTED = 524288;
	public static final int MTE_ANGLEOFARRIVAL = 32;
	public static final int MTE_CELLID = 8;
	public static final int MTE_SATELLITE = 1;
	public static final int MTE_SHORTRANGE = 16;
	public static final int MTE_TIMEDIFFERENCE = 2;
	public static final int	MTE_TIMEOFARRIVAL = 4;
	public static final int	MTY_NETWORKBASED = 131072;
	public static final int	MTY_TERMINALBASED = 65536;
	
	private QualifiedCoordinates coords;
	
	protected Location() {
		System.out.println("ME4SE: Location() NYI!");
	}

	public AddressInfo getAddressInfo() {
		System.out.println("ME4SE: Location.getAddressInfo() NYI!");
		return null;
	}
	
	public float getCourse() {
		System.out.println("ME4SE: Location.getCourse() NYI!");
		return Float.MIN_VALUE;
	}

	public String getExtraInfo(String mimetype) {
		System.out.println("ME4SE: Location.getExtraInfo(String) NYI!");
		return "ME4SE NYI!";
	}

	public int getLocationMethod() {
		System.out.println("ME4SE: Location.getLocationMethod() NYI!");
		return Integer.MIN_VALUE;
	}

	public QualifiedCoordinates getQualifiedCoordinates() {
		System.out.println("ME4SE: Location.getQualifiedCoordinates() called.");
		//$GPRMC,162902.000,V,4722.1440,N,00831.3255,E,1.68,167.01,140508,,,N*77
		coords = new QualifiedCoordinates(47.37454, 8.522465, 0, 0, 0);
		return coords;
	}

	public float getSpeed() {
		System.out.println("ME4SE: Location.getSpeed() NYI!");
		return Float.MIN_VALUE;
	}

	public long getTimestamp() {
		System.out.println("ME4SE: Location.getTimeStamp() NYI!");
		return Long.MIN_VALUE;
	}

	public boolean isValid() {
		System.out.println("ME4SE: Location.isValid() called. Returning 'true'.");
		return true;
	}
}