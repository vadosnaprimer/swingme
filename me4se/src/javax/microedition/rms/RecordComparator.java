package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */
public interface RecordComparator {
    
	/**
	 * @API MIDP-1.0
	 */ 
    public static final int EQUIVALENT = 0;
    
	/**
	 * @API MIDP-1.0
	 */ 
    public static final int FOLLOWS = 1;
    
	/**
	 * @API MIDP-1.0
	 */ 
    public static final int PRECEDES = -1;
   
	/**
	 * @API MIDP-1.0
	 */ 
    public int compare (byte[] rec1, byte[] rec2);
}



