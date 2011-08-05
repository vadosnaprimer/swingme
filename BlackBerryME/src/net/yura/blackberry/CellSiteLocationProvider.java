package net.yura.blackberry;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.RadioInfo;
import net.yura.mobile.io.json.JSONWriter;
import net.yura.mobile.logging.Logger;

public class CellSiteLocationProvider extends LocationProvider {

	private Vector listeners;
	
	private static CellSiteLocationProvider _instance;
	
	public static LocationProvider getInstance(Criteria c){
		if (_instance==null){
			_instance = new CellSiteLocationProvider();
		}
		return _instance;			
	}
	
	private int _waitTime = 30000; // 5m
	
	private boolean hasStarted = false;
	
	public static final int STATE_NO_CELL_ID = 0;
	public static final int STATE_CONTACTING_GOOGLE = 1;
	public static final int STATE_UNKNOWN_CELL = 2;
	public static final int STATE_CONN_ERROR = 3;
	public static final int STATE_LOCATION_FOUND = 4;
	public static final int STATE_SLEEPING = 5;
	public static final int STATE_CELL_ID_FOUND = 6;
	public static final int STATE_CELL_ID_NO_CHANGE = 7;
	
	private Location _currentLocation;
	private int _lastKnownCellID;
	private int _lastKnownCellLAC;
	private int _currentState;
	private boolean _quit = false;
		
	private void setState(int state){
		_currentState = state;
		notifyStateChanged();
	}

	protected CellSiteLocationProvider(){
		_currentLocation = null;
		listeners = new Vector();
		_lastKnownCellID = -1;
		_lastKnownCellLAC = -1;
	}
	
	public Location getLocation(int timeout) throws LocationException, InterruptedException {
		return _currentLocation;
	}
	
	public int getState() {
		return _currentState;
	}
	
	private String getStateDescription(int state){
		switch (state){
		case STATE_NO_CELL_ID:
			return "STATE_NO_CELL_ID";
		case STATE_CONTACTING_GOOGLE:
			return "STATE_CONTACTING_GOOGLE";
		case STATE_UNKNOWN_CELL:
			return "STATE_UNKNOWN_CELL";
		case STATE_CONN_ERROR:
			return "STATE_CONN_ERROR";
		case STATE_LOCATION_FOUND:
			return "STATE_LOCATION_FOUND";
		case STATE_SLEEPING:
			return "STATE_SLEEPING";
		case STATE_CELL_ID_FOUND:
			return "STATE_CELL_ID_FOUND";
		case STATE_CELL_ID_NO_CHANGE:
			return "STATE_CELL_ID_NO_CHANGE";
		}
		return "";
	}
	
	public void reset() {
		_quit = true;
		listeners = null;
		hasStarted = false;
	}

	private void notifyLocationChanged() {
		 //#debug info
		Logger.info("Cell site location provider location changed: " + _currentLocation.getQualifiedCoordinates().getLatitude() + ", " + _currentLocation.getQualifiedCoordinates().getLongitude());
		
		Enumeration e = listeners.elements();
		if (e.hasMoreElements()){
			LocationListener ll = (LocationListener) e.nextElement();
			ll.locationUpdated(this, _currentLocation);
		}
	}
	
	private void notifyStateChanged(){
		 //#debug info
		Logger.info("Cell site location provider state changed: " + getStateDescription(_currentState));
				
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
					int mcc = -1;
					int mnc = -1;
					
					if (RadioInfo.getNumberOfNetworks() > 0){
						mcc = RadioInfo.getMCC(0);
						mnc = RadioInfo.getMNC(0);
					}
					
					if (cellID == 0) {
						setState(STATE_NO_CELL_ID);
						
					} else if (cellID == _lastKnownCellID && lac == _lastKnownCellLAC) {
						setState(STATE_CELL_ID_NO_CHANGE);
					} else {
						_lastKnownCellID = cellID;
						_lastKnownCellLAC = lac;
						
						setState(STATE_CELL_ID_FOUND);
						final String s = googleLocate(cellID, lac, mnc, mcc);
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
	
	private String googleLocate(int cellid, int lac, int carrier, int country) {
		try {						
			
			// request
			//{"version":"1.1.0","host":"maps.google.com","cell_towers":[{"cell_id":44049,"location_area_code":201,"mobile_country_code":234,"mobile_network_code":15}]}
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();			
			Writer writer = new OutputStreamWriter(bos);	
			
			System.out.println(Integer.toHexString(country));
			System.out.println(Integer.toHexString(carrier));
			
			new JSONWriter(writer)
		    .object()
		    	.key("version").value("1.1.0")
		        .key("host").value("maps.google.com")
		        .key("cell_towers")
			        .array()
			        	.object()
			         		.key("cell_id").value(cellid)
			         		.key("location_area_code").value(lac)
			         		.key("mobile_country_code").value(country)
			         		.key("mobile_network_code").value(carrier)
			         	.endObject()
			        .endArray()
		    .endObject();		     
			String request = new String(bos.toByteArray());
			
			HttpConnection conn = null;
			conn = (HttpConnection) Connector.open("http://www.google.com/loc/json"	+ ConnectionManager.mostRecentAppendString);
			conn.setRequestProperty("Content-Type", "application / requestJson");
			conn.setRequestMethod("POST");
			byte[] b = request.getBytes();
			conn.setRequestProperty("Content-Length", Integer.toString(b.length));
			OutputStream os = new DataOutputStream(conn.openOutputStream());
			os.write(b);
			os.close();
			int response = conn.getResponseCode();
			if (response == HttpConnection.HTTP_OK) {
			    
				String lat, lng = "";
				
				InputStream is = conn.openInputStream();
				byte[] responseBytes = new byte[is.available()];
				is.read(responseBytes);
				String responseString = new String(responseBytes);
				
				int i = responseString.indexOf("latitude");
				if (i == -1){
					return "UNKNOWN";
				} else {
					lat = responseString.substring(i + 10, responseString.indexOf(',', i));
					// ensure that lat can be parsed as a double
					try {
						Double.parseDouble(lat);
					} catch (Exception e){
						return "UNKNOWN";
					}
				}
				
				int j = responseString.indexOf("longitude");
				if (j == -1){
					return "UNKNOWN";
				} else {
					lng = responseString.substring(j + 11, responseString.indexOf(',', j));
					// ensure that lng can be parsed as a double
					try {
						Double.parseDouble(lng);
					} catch (Exception e){
						return "UNKNOWN";
					}
				}				
				// response
				// {"location":{"latitude":51.5160886,"longitude":-0.1352067,"accuracy":875.0},"access_token":"2:kyblhV7chWDTogPd:_Q_sxZ-WacfwJqM_"}
				
                return lat + "," + lng;
			}
			return "UNKNOWN";
		} catch (Exception e) {
			return "CONN_ERROR";
		}
	}
	
}
