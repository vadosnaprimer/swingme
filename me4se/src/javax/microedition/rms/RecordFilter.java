package javax.microedition.rms;

/**
 * @API MIDP-1.0
 */
public interface RecordFilter {

	/**
	 * @API MIDP-1.0
	 */
    public boolean matches (byte [] candidate);

}
