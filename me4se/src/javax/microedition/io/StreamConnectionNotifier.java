package javax.microedition.io;


/**
 * @API MIDP-1.0 
 */
public interface StreamConnectionNotifier extends Connection {
    
	/**
	 * @API MIDP-1.0 
	 */
    public StreamConnection acceptAndOpen () throws java.io.IOException;
}
