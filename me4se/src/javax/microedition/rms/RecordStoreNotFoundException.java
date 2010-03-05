package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */
public class RecordStoreNotFoundException extends RecordStoreException {

	/**
	 * @API MIDP-1.0
	 */
	public RecordStoreNotFoundException() {
	}

	/**
	 * @API MIDP-1.0
     */
	public RecordStoreNotFoundException(String message) {
		super(message);
	}
}
