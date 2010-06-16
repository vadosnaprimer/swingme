package javax.microedition.location;

public interface LocationListener {

	public void locationUpdated(LocationProvider provider, Location location);
	
	public void providerStateChanged(LocationProvider provider, int newState); 
	
}
