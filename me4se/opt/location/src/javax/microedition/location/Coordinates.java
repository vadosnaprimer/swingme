package javax.microedition.location;

public class Coordinates {

	public static final int DD_MM = 2;
	public static final int DD_MM_SS = 1;
	
	private double longitude = -1;
	private double latitude = -1;
	private float altitude = -1;
	
	public Coordinates(double latitude, double longitude, float altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	  System.out.println("ME4SE: Coordinates(double='"+latitude+"', double='"+longitude+"', float='" + altitude + "') called.");
	}
	
	public float azimuthTo(Coordinates to) {
		System.out.println("ME4SE: Coordinates.azimuthTo(Coordinates) NYI!");
		return Float.MIN_VALUE;
	}

	public static String convert(double coordinate, int outputType) {
		System.out.println("ME4SE: Coordinates.convert(double, int) NYI!");
		return "ME4SE: Coordinates.convert() NYI!";
	}
	
	public static double convert(String coordinate) {
		System.out.println("ME4SE: Coordinates.convert(String) NYI!");
		return Double.MIN_VALUE;
	}

	public float distance(Coordinates to) {
		System.out.println("ME4SE: Coordinates.distance(Coordinates) NYI!");
		return Float.MIN_VALUE;
	}
	
	public float getAltitude() {
		System.out.println("ME4SE: Coordinates.getAltitude() called.");
		return altitude;
	}
	
	public double getLatitude() {
		System.out.println("ME4SE: Coordinates.getLatitude() called.");
		return latitude;
	}
	
	public double getLongitude() {
		System.out.println("ME4SE: Coordinates.getLongitude() called.");
		return longitude;
	}

	public void setAltitude(float altitude) {
		System.out.println("ME4SE: Coordinates.setAltitude(float="+altitude+") called.");
		this.altitude = altitude;
	}

	public void setLatitude(double latitude) {
		System.out.println("ME4SE: Coordinates.setLatitude(double="+latitude+") called.");
		this.latitude = latitude;
	}

	public void	setLongitude(double longitude) {
		System.out.println("ME4SE: Coordinates.setLongitude(double longitude="+longitude+") called.");
		this.longitude = longitude;
	}
}