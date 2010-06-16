package javax.microedition.location;


public class LocationProviderImpl extends LocationProvider {

  private Criteria criteria;
  public static int state = LocationProvider.OUT_OF_SERVICE;
  
  public LocationProviderImpl(Criteria crit) {
    System.out.println("ME4SE: LocationProviderImpl(Criteria criteria=" + criteria + ") called.");
    this.criteria = crit;
  }
  
  public Location getLocation(int timeout) {
    System.out.println("ME4SE: LocationProviderImpl.getLocation(int timeout=" + timeout +") called!");
    Location loc = new Location();
    return loc;
  }

  public int getState() {
    System.out.println("ME4SE: LocationProviderImpl.getState() called.");
    switch (state) {
    case LocationProvider.AVAILABLE:
      System.out.println("\tState=" + LocationProvider.AVAILABLE);
      break;
    case LocationProvider.OUT_OF_SERVICE:
      System.out.println("\tState=" + LocationProvider.OUT_OF_SERVICE);
      break;
    case LocationProvider.TEMPORARILY_UNAVAILABLE:
      System.out.println("\tState=" + LocationProvider.TEMPORARILY_UNAVAILABLE);
      break;
    }
    return state;
  }

  public void reset() {
    System.out.println("ME4SE: LocationProviderImpl.reset() called.");
  }

  public void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge) {
    System.out.println("ME4SE: LocationProviderImpl.setLocationListener() called.");
  }
}