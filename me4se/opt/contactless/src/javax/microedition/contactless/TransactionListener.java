package javax.microedition.contactless;

public interface TransactionListener {

	public static byte UNKNOWN_SLOT = -1;

	public void externalReaderDetected(byte slot); 
	
}
