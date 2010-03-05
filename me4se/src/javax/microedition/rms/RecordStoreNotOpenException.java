package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */
public class RecordStoreNotOpenException extends RecordStoreException {

	/**
	 * @API MIDP-1.0
	 */
	public RecordStoreNotOpenException() {
	}

	/**
	 * @API MIDP-1.0
	 */
	public RecordStoreNotOpenException(String message) {
		super(message);
	}
}
