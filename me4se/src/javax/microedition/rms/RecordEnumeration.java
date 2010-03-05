package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */
public interface RecordEnumeration {

	/**
	 * @API MIDP-1.0
	 */
	public void destroy();

	/**
	 * @API MIDP-1.0
	 */
	public boolean hasNextElement();

	/**
	 * @API MIDP-1.0
	 */
	public boolean hasPreviousElement();

	/**
	 * @API MIDP-1.0
	 */
	public boolean isKeptUpdated();

	/**
	 * @API MIDP-1.0
	 */
	public void keepUpdated(boolean keepUpdated);

	/**
	 * @API MIDP-1.0
	 */
	public byte[] nextRecord() throws RecordStoreException, RecordStoreNotOpenException, InvalidRecordIDException;

	/**
	 * @API MIDP-1.0
	 */
	public int nextRecordId() throws InvalidRecordIDException;
	
	/**
	 * @API MIDP-1.0
	 */
	public int numRecords();

	/**
	 * @API MIDP-1.0
	 */
	public byte[] previousRecord() throws RecordStoreException, RecordStoreNotOpenException, InvalidRecordIDException;

	/**
	 * @API MIDP-1.0
	 */
	public int previousRecordId() throws InvalidRecordIDException;

	/**
	 * @API MIDP-1.0
	 */
	public void rebuild();

	/**
	 * @API MIDP-1.0
	 */
	public void reset();

}
