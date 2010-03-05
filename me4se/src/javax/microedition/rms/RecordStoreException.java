package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */ 
public class RecordStoreException extends Exception {

	/**
	 * Constructs a new RecordStoreException with no detail message.
	 * @API MIDP-1.0
	 */
	public RecordStoreException() {
	}

	/**
	 * Constructs a new RecordStoreException with the specified detail message.
	 * @API MIDP-1.0
	 */
	public RecordStoreException(String message) {
		super(message);
	}

}
