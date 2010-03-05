package javax.microedition.io;

import java.io.IOException;

/**
 * @API MIDP-2.0 
 */
public interface SecureConnection extends SocketConnection {

	/**
	 * @API MIDP-2.0 
	 */
	public abstract SecurityInfo getSecurityInfo() throws IOException;
}
