package net.yura.blackberry;

import java.util.Vector;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.CoverageStatusListener;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.yura.mobile.io.SocketClient;

public class ConnectionManager implements GlobalEventListener {
	
	public final static int WIFI = 0;
	public final static int BIS_B = 1;
	public final static int DIRECT_TCP = 2;
	public final static int WAP = 3;
	public final static int MDS = 4;
	public final static int NO_CONNECTION = 5;
	 //#mdebug info
	private Vector observers = new Vector();
	
	public interface ConnectionStateObserver {
	     public void update(String status);	  
	}	  
	
    public void addObserver(ConnectionStateObserver oi){ 
        observers.addElement(oi);
        notifyObservers();
    }  
 
    public void removeObserver(ConnectionStateObserver oi){ 
        observers.removeElement(oi);  
    }
 
    public void notifyObservers(){ 
    	String message = getConnectionDescription(currentConnectionMethod);
    	
        for(int i=0;i<observers.size();i++){            
        	((ConnectionStateObserver)observers.elementAt(i)).update(message); 
        }
    }
    //#enddebug
    
    private String getConnectionDescription(int connectionMethod){
		switch (connectionMethod) {
		case WIFI:
			return "WIFI";
		case BIS_B:
			return "BIS";
		case DIRECT_TCP:
			return "TCP";
		case WAP:
			return "WAP";
		case MDS:
			return "MDS";
		case NO_CONNECTION:
			return "NONE";
		}
		return "XXX";
	}
	
	private int currentConnectionMethod = -1;
	
	private WIFIListener connWIFIListener;
	private RadioListener connRadioListener;
	
	public static String mostRecentAppendString = null;
	
	public String getCurrentConnectionMethod(){
		return getConnectionDescription(currentConnectionMethod);
	}
	
	public WIFIListener getConnWIFIListener(){
		return connWIFIListener ;
	}
	
	public RadioListener getConnRadioListener(){
		return connRadioListener ;
	}
	
	class WIFIListener implements WLANConnectionListener {
		public void networkConnected() {
			setCoverage();
			updateConnection();
		}
		public void networkDisconnected(int reason) {
			setCoverage();
			updateConnection();
		}
	}
	
	class RadioListener implements CoverageStatusListener {
		public void coverageStatusChanged(int newCoverage) {
			setCoverage();
			updateConnection();
		}		
	}
	
	private static final long ID = 0x1431cf6271d3b1edL;

	private static String IPPP = "IPPP";

	private static ConnectionManager _manager;
	private boolean _mdsSupport;
	private boolean _bisSupport;
	private boolean _wapSupport;
	private boolean _wifiSupport;
	private boolean _tcpSupport;
	
	private ConnectionManager() {
		setCoverage();
		updateConnection();
		connRadioListener = new RadioListener();
		connWIFIListener = new WIFIListener();
	}

	public static ConnectionManager getInstance() {
		if (_manager == null) {
			_manager = new ConnectionManager();
		}
		return _manager;
	}    
	
	public String getInternetConnectionString() {
		String connStr = null;
		if (_wifiSupport){
			connStr = ";interface=wifi";
			currentConnectionMethod = WIFI;
		} else if (_mdsSupport) {
			connStr = ";deviceside=false";
			currentConnectionMethod = MDS;
		} else if (_bisSupport) {
			connStr = ";deviceside=false;ConnectionType=mds-public";
			currentConnectionMethod = BIS_B;
		} else if (_tcpSupport ) {
			 String carrierUid = getCarrierBIBSUid();
	            if (carrierUid == null) {
	                connStr = ";deviceside=true";
	            } else {	                
	                connStr = ";deviceside=false;connectionUID="+carrierUid + ";ConnectionType=mds-public";
	            }
	            currentConnectionMethod = DIRECT_TCP;
		} else if (_wapSupport) {
			currentConnectionMethod = WAP;
			// TODO: WAP support
		} else {
			currentConnectionMethod = NO_CONNECTION;
			// TODO: No internet connection
		}
		notifyObservers();
		return connStr;
	}

	private void setCoverage() {
		_mdsSupport = CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS);
		_bisSupport = (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B));
		_wifiSupport = (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED);
		_tcpSupport = ((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT);
	}

	private void updateConnection(){
		mostRecentAppendString = getInternetConnectionString();
		SocketClient.connectAppend = mostRecentAppendString;
	}
	
	private void parseServiceBooks() {
		ServiceBook sb = ServiceBook.getSB();
		ServiceRecord[] records = sb.findRecordsByCid(IPPP);
		if (records == null) {
			return;
		}

		int numRecords = records.length;
		for (int i = 0; i < numRecords; i++) {
			ServiceRecord myRecord = records[i];
			String name = myRecord.getName();
			String uid = myRecord.getUid();
			if (myRecord.isValid() && !myRecord.isDisabled()) {
				int encryptionMode = myRecord.getEncryptionMode();
				if (encryptionMode == ServiceRecord.ENCRYPT_RIM) {
					_mdsSupport = true;
				} else {
					_bisSupport = true;
				}
			}
		}
	}

    /**
     * Looks through the phone's service book for a carrier provided BIBS network
     * @return The uid used to connect to that network.
     */
    private static String getCarrierBIBSUid() {   	
        ServiceRecord[] records = ServiceBook.getSB().getRecords();
        for(int currentRecord = 0; currentRecord < records.length; currentRecord++) {
            if(records[currentRecord].getCid().toLowerCase().equals("ippp")) {                
            	if(records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0) {
                    return records[currentRecord].getUid();
                }
            }
        }
        return null;
    } 
	
	public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
		if (guid == ServiceBook.GUID_SB_ADDED || guid == ServiceBook.GUID_SB_CHANGED || guid == ServiceBook.GUID_SB_OTA_SWITCH || guid == ServiceBook.GUID_SB_OTA_UPDATE || guid == ServiceBook.GUID_SB_REMOVED) {
			parseServiceBooks();
			updateConnection();
		}
	}
	
}
