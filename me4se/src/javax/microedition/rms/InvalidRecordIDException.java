/**
 * @API MIDP-1.0
 */
package javax.microedition.rms;

public class InvalidRecordIDException extends RecordStoreException {

	/**
	 * @API MIDP-1.0
	 */
	public InvalidRecordIDException() {

	}

	/**
	 * @API MIDP-1.0
	 */
	public InvalidRecordIDException(String message) {
		super(message);
	}

}
