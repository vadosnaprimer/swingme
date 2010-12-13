package javax.microedition.location;

public interface ProximityListener {
	 void	monitoringStateChanged(boolean isMonitoringActive);
	 void	proximityEvent(Coordinates coordinates, Location location);
}
