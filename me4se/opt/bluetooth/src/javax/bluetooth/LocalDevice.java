package javax.bluetooth;

import javax.microedition.io.Connection;

public class LocalDevice {

	private LocalDevice() {
		
	}
	
	public String getBluetoothAddress() {
		return null;
	}

	public DeviceClass getDeviceClass() {
		return null;
	}

	public int getDiscoverable() {
		return Integer.MIN_VALUE;
	}
	
	public DiscoveryAgent getDiscoveryAgent() {
		return null;
	}
	
	public String getFriendlyName() {
		return null;
	}
	
	public static LocalDevice getLocalDevice() {
		return null;
	}
	
	public static String getProperty(java.lang.String property) {
		return null;
	}
	
	public ServiceRecord getRecord(Connection notifier) {
		return null;
	}
	
	public static boolean isPowerOn() {
		return false;
	}

	public boolean setDiscoverable(int mode) {
		return false;
	}
	
	public void updateRecord(ServiceRecord srvRecord) {
	}	
}