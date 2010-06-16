package javax.bluetooth;

public class DiscoveryAgent {

	public static int CACHED = 0x00;
	public static int GIAC = 0x9E8B33;
	public static int LIAC = 0x9E8B00;
	public static int NOT_DISCOVERABLE = 0x00;
	public static int PREKNOWN = 0x01;

	private DiscoveryAgent() {
		
	}

	public boolean cancelInquiry(DiscoveryListener listener) {
		return false;
	}
	
	public boolean cancelServiceSearch(int transID) {
		return false;
	}
	
	public RemoteDevice[] retrieveDevices(int option) {
		return null;
	}
	
	public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener discListener) throws BluetoothStateException {
		return Integer.MIN_VALUE;
	}

	public String selectService(UUID uuid, int security, boolean master) throws BluetoothStateException {
		return null;
	}
	
	public boolean startInquiry(int accessCode, DiscoveryListener listener) throws BluetoothStateException {
		return false;
	}
}