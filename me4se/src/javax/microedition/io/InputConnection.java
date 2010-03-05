// STATUS: API complete

package javax.microedition.io;

import java.io.*;

/**
 * @API MIDP-1.0 
 */
public interface InputConnection extends Connection {

	/**
	 * @API MIDP-1.0 
	 */
    public DataInputStream openDataInputStream () throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
    public InputStream openInputStream () throws IOException;
}
