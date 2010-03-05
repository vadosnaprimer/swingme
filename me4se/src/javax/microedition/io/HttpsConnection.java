package javax.microedition.io;

import java.io.IOException;

/**
 * @API MIDP-2.0 
 */
public interface HttpsConnection extends HttpConnection {

	/**
	 * @API MIDP-2.0 
	 */
	public abstract SecurityInfo getSecurityInfo() throws IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract int getPort();
}
