package javax.obex;

public interface HeaderSet {

	public static int APPLICATION_PARAMETER = 0x4C;
	public static int COUNT = 0xC0;
	public static int DESCRIPTION = 0x05;
	public static int HTTP = 0x47;
	public static int LENGTH = 0xC3;
	public static int NAME = 0x01;
	public static int OBJECT_CLASS = 0x4F;
	public static int TARGET = 0x46;
	public static int TIME_4_BYTE = 0xC4;
	public static int TIME_ISO_8601 = 0x44;
	public static int TYPE = 0x42;
	public static int WHO = 0x4A;

	public void createAuthenticationChallenge(String realm, boolean userID, boolean access);
	
	public Object getHeader(int headerID);
	
	public int[] getHeaderList();
	
	public int getResponseCode();
	
	public void setHeader(int headerID, Object headerValue);	
}