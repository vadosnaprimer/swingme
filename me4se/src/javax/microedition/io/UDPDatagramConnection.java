package javax.microedition.io;

import java.io.IOException;

/**
 * @API MIDP-2.0 
 */
public interface UDPDatagramConnection extends DatagramConnection {

	/**
	 * @API MIDP-2.0 
	 */
	public abstract String getLocalAddress() throws IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract int getLocalPort() throws IOException;
}
