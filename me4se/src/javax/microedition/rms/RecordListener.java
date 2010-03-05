package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */
public interface RecordListener {
   
	/**
	 * @API MIDP-1.0
	 */ 
    public void recordAdded (RecordStore recordStore, int recordId);
    
	/**
	 * @API MIDP-1.0
	 */
    public void recordChanged (RecordStore recordStore, int recordId);
    
	/**
	 * @API MIDP-1.0
	 */
    public void recordDeleted (RecordStore recordStore, int recordId);
    
}
