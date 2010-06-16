package javax.microedition.contactless.ndef;

public class NDEFRecordType {

	public static final int EMPTY =	0;
	public static final int EXTERNAL_RTD = 4;
	public static final int MIME = 2;
	public static final int NFC_FORUM_RTD = 1;
	public static final int UNKNOWN = 5;
	public static final int URI = 3;

	public NDEFRecordType(int format, String name) {
		
	}

	public boolean equals(Object recordType) {
		return false;
	}
	
	public int getFormat() {
		return -1;
	}
	
	public String getName() {
		return null;
	}
	
	public byte[] getNameAsBytes() {
		return null;
	}

	public int hashCode() {
		return -1;
	}	
}