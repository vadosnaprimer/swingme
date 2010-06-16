package javax.microedition.location;

public class Orientation {
	
	public Orientation(float azimuth, boolean isMagnetic, float pitch, float roll) {
		System.out.println("ME4SE: Orientation(float, boolean, float, float) NYI!");
	}
	
	public float getCompassAzimuth() {
		System.out.println("ME4SE: Orientation.getCompassAzimuth() NYI!");
		return Float.MIN_VALUE;
	}
	
	public static Orientation getOrientation() {
		System.out.println("ME4SE: Orientation.getOrientation() NYI!");
		return null;
	}

	public float getPitch() {
		System.out.println("ME4SE: Orientation.getPitch() NYI!");
		return Float.MIN_VALUE;
	}
	
	public float getRoll() {
		System.out.println("ME4SE: Orientation.getRoll() NYI!");
		return Float.MIN_VALUE;
	}
	
	public boolean isOrientationMagnetic() {
		System.out.println("ME4SE: Orientation.isOrientationMagnetic() NYI!");
		return false;
	}
}