// STATUS: API complete

package javax.microedition.io;

import java.io.*;

/**
 * @API MIDP-1.0 
 */
public interface OutputConnection extends Connection {

	/**
	 * @API MIDP-1.0 
	 */
    public DataOutputStream openDataOutputStream () throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
    public OutputStream openOutputStream () throws IOException;
}
