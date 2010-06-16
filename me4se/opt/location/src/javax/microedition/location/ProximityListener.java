package javax.microedition.location;

public interface ProximityListener {

	public void monitoringStateChanged(boolean isMonitoringActive);
	public void proximityEvent(Coordinates coordinates, Location location);
	
}