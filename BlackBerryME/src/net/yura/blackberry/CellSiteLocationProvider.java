package net.yura.blackberry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.system.GPRSInfo;

public class CellSiteLocationProvider extends LocationProvider {

	private Vector listeners;
	
	private int _waitTime = 300000; // 5m
	
	private boolean hasStarted = false;
	
	public static final int STATE_NO_CELL_ID = 0;
	public static final int STATE_CONTACTING_GOOGLE = 1;
	public static final int STATE_UNKNOWN_CELL = 2;
	public static final int STATE_CONN_ERROR = 2;
	public static final int STATE_LOCATION_FOUND = 3;
	public static final int STATE_SLEEPING = 4;
	public static final int STATE_CELL_ID_FOUND = 5;
	public static final int STATE_CELL_ID_NO_CHANGE = 6;
	
	//private static CellSiteLocationProvider _instance;
	
	private Location _currentLocation;
	private int _lastKnownCellID;
	private int _currentState;
	private boolean _quit = false;
	
	/**
	 * @param cr This parameter is ignored
	 * @return An instance of the location provider
	 */
	/*public static LocationProvider getInstance(Criteria cr){
		if (_instance == null)
			_instance = new CellSiteLocationProvider();
		return _instance;
	}*/
	
	private void setState(int state){
		_currentState = state;
		notifyStateChanged();
	}

	public CellSiteLocationProvider(){
		_currentLocation = null;
		listeners = new Vector();		
	}
	
	public Location getLocation(int timeout) throws LocationException, InterruptedException {
		return _currentLocation;
	}

	public int getState() {
		return _currentState;
	}
	
	public void reset() {
		_quit = true;
		listeners = null;
		hasStarted = false;
	}

	private void notifyLocationChanged(){
		Enumeration e = listeners.elements();
		if (e.hasMoreElements()){
			LocationListener ll = (LocationListener) e.nextElement();
			ll.locationUpdated(this, _currentLocation);
		}
	}
	
	private void notifyStateChanged(){
		Enumeration e = listeners.elements();
		if (e.hasMoreElements()){
			LocationListener ll = (LocationListener) e.nextElement();
			ll.providerStateChanged(this, _currentState);
		}
	}
	
	public void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge) {
		if (!listeners.contains(listener)){
			if (interval > 5000) {
				_waitTime = interval;
			}
			listeners.addElement(listener);

			if (listeners.size() == 1 && hasStarted == false) {
				startLookingForLocation();
			}
			
		}		
	}
	
	private void startLookingForLocation(){
		hasStarted = true;
		Thread t = new Thread("CellSiteLocationProviderThread") {
			public void run(){
				while (!_quit){
					GPRSInfo.GPRSCellInfo ci = GPRSInfo.getCellInfo();
					int cellID = ci.getCellId();
					int lac = ci.getLAC();
					
					/*					
					Testing CellID and LAC  
					int cellID = 47871;
					int lac = 50022;						
					*/
					
					if (cellID == 0) {
						setState(STATE_NO_CELL_ID);
						
					} else if (cellID == _lastKnownCellID && _currentLocation != null) {
						setState(STATE_CELL_ID_NO_CHANGE);
					} else {
						_lastKnownCellID = cellID;
						setState(STATE_CELL_ID_FOUND);
						final String s = tryToLocate(cellID, lac);
						if (s.equalsIgnoreCase("UNKNOWN")){
							setState(STATE_UNKNOWN_CELL);
						} else if (s.equalsIgnoreCase("CONN_ERROR")){
							setState(STATE_CONN_ERROR);
						} else {
							setState(STATE_LOCATION_FOUND);
							_currentLocation = new Location() {								
								public QualifiedCoordinates getQualifiedCoordinates() {
									String s1 = s.substring(0,	s.indexOf(","));
									String s2 = s.substring(s.indexOf(",") + 1);
									return new QualifiedCoordinates(Double.parseDouble(s1), Double.parseDouble(s2), 0, 0, 0);
								}
								public boolean isValid() {
									return true;
								}								
							};							
							notifyLocationChanged();							
						}						
					}
					setState(STATE_SLEEPING);
					try {
						sleep(_waitTime);
					} catch (InterruptedException e) {
						_quit = true;
					}
				}
			}
		};
		t.start();		
	}
	
	/**
	 * @param aCellID The cell ID of the cell tower
	 * @param aLAC The LAC for the cell tower
	 * @return The GPS location of the cell tower.
	 */
	private String tryToLocate(int aCellID, int aLAC) {
		try {
			HttpConnection conn = null;
			conn = (HttpConnection) Connector.open("http://www.google.com/glm/mmap"	+ ConnectionManager.mostRecentAppendString);

			conn.setRequestMethod("POST");
			DataOutputStream os = new DataOutputStream(conn.openOutputStream());
			os.writeShort(21);
			os.writeLong(0);
			os.writeUTF("fr");
			os.writeUTF("Sony_Ericsson-K750");
			os.writeUTF("1.3.1");
			os.writeUTF("Web");
			os.writeByte(27);

			os.writeInt(0);
			os.writeInt(0);
			os.writeInt(3);
			os.writeUTF("");
			os.writeInt(aCellID);
			os.writeInt(aLAC);
			os.writeInt(0);
			os.writeInt(0);
			os.writeInt(0);
			os.writeInt(0);
			os.flush();

			int response = conn.getResponseCode();
			if (response == HttpConnection.HTTP_OK) {
				InputStream in = conn.openInputStream();
				DataInputStream dis = new DataInputStream(in);

				// Read some prior data
				dis.readShort();
				dis.readByte();
				// Read the error-code
				int errorCode = dis.readInt();
				if (errorCode == 0) {
					double lat = dis.readInt() / 1000000D;
					double lng = dis.readInt() / 1000000D;
					// Read the rest of the data
					dis.readInt();
					dis.readInt();
					dis.readUTF();					
					return lat + "," + lng;
				}
			}
			return "UNKNOWN";
		} catch (Exception e) {
			return "CONN_ERROR";
		}
	}
	
}
