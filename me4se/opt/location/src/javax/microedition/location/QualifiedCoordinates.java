package javax.microedition.location;

public class QualifiedCoordinates extends Coordinates {

	public QualifiedCoordinates(double latitude, double longitude, float altitude, float horizontalAccuracy, float verticalAccuracy) {
		super(latitude, longitude, altitude);
		System.out.println("ME4SE: QualifiedCoordinates(double, double, float, float, float) NYI!");
	}

	public float getHorizontalAccuracy() {
		System.out.println("ME4SE: QualifiedCoordinates.getHorizintalAccurancy() NYI!");
		return Float.MIN_VALUE;
	}

	public float getVerticalAccuracy() {
		System.out.println("ME4SE: QualifiedCoordinates.getVerticalAccurancy() NYI!");
		return Float.MIN_VALUE;
	}

	public void setHorizontalAccuracy(float horizontalAccuracy) {
		System.out.println("ME4SE: QualifiedCoordinates.setHorizintalAccurancy(float) NYI!");
	}
	
	public void setVerticalAccuracy(float verticalAccuracy) {
		System.out.println("ME4SE: QualifiedCoordinates.getVerticalAccurancy() NYI!");

	}	
}