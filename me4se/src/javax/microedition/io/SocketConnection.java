package javax.microedition.io;

import java.io.IOException;

/**
 * @API MIDP-2.0 
 */
public interface SocketConnection extends StreamConnection {

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte DELAY = 0;

	/**
	 * @API MIDP-2.0 
	 */	
	public static final byte LINGER = 1;
	
	/**
	 * @API MIDP-2.0 
	 */	
	public static final byte KEEPALIVE = 2;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte RCVBUF = 3;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte SNDBUF = 4;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract int getSocketOption(byte option) throws IllegalArgumentException, IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract String getLocalAddress() throws IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract int getLocalPort() throws IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract String getAddress() throws IOException;

	/**
	 * @API MIDP-2.0 
	 */
	public abstract int getPort() throws IOException;
}
