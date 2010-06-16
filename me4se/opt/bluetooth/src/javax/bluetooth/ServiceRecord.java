package javax.bluetooth;

public interface ServiceRecord {

	public static int AUTHENTICATE_ENCRYPT = 0x00;
	public static int AUTHENTICATE_NOENCRYPT = 0x01;
	public static int NOAUTHENTICATE_NOENCRYPT = 0x02;

	public int[] getAttributeIDs();
	
	public DataElement getAttributeValue(int attrID);
	
	public String getConnectionURL(int requiredSecurity, boolean mustBeMaster);
	
	public RemoteDevice getHostDevice();
	
	public boolean populateRecord(int[] attrIDs);
	
	public boolean setAttributeValue(int attrID, DataElement attrValue);
	
	public void setDeviceServiceClasses(int classes);
}