package javax.microedition.location;


public class LocationImpl implements Location {

	private final android.location.Location location;

	public LocationImpl(android.location.Location loc) {
		this.location = loc;
	}

	public float getCourse() {
		return location.getBearing();
	}

	public QualifiedCoordinates getQualifiedCoordinates() {
		QualifiedCoordinates coords = new QualifiedCoordinates(location.getLatitude(), location.getLongitude(), (float) location.getAltitude(), location.getAccuracy(), location.getAccuracy());
		return coords;
	}

	public float getSpeed() {
		return location.getSpeed();
	}

	public long getTimestamp() {
		return location.getTime();
	}

	public boolean isValid() {
		return true;
	}

}
