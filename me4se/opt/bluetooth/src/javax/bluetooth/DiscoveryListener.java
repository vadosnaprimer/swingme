package javax.bluetooth;

public interface DiscoveryListener {

	public static int INQUIRY_COMPLETED = 0x00;
	public static int INQUIRY_ERROR = 0x07;
	public static int INQUIRY_TERMINATED = 0x02;
	public static int SERVICE_SEARCH_COMPLETED = 0x01;
	public static int SERVICE_SEARCH_DEVICE_NOT_REACHABLE = 0x06;
	public static int SERVICE_SEARCH_ERRO = 0x03; 
	public static int SERVICE_SEARCH_NO_RECORDS = 0x04;
	public static int SERVICE_SEARCH_TERMINATED = 0x02; 
	
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);
	
	public void inquiryCompleted(int discType);
	
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord);
	
    public void serviceSearchCompleted(int transID, int respCode); 
}