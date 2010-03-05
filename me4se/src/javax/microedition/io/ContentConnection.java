package javax.microedition.io;

/**
 * @API MIDP-1.0 
 */
public interface ContentConnection extends StreamConnection {

	/**
	 * @API MIDP-1.0 
	 */
    public String getType ();
    
	/**
	 * @API MIDP-1.0 
	 */
    public long getLength ();

	/**
	 * @API MIDP-1.0 
	 */
    public String getEncoding ();
}
