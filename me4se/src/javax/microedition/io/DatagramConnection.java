package javax.microedition.io;

import java.io.*;

/**
 * @API MIDP-1.0 
 */

public interface DatagramConnection extends Connection {

	/**
	 * @API MIDP-1.0 
	 */
	public int getMaximumLength() throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public int getNominalLength() throws IOException;
	
	/**
	 * @API MIDP-1.0 
	 */
	public void send(Datagram dgram) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public void receive(Datagram dgram) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public Datagram newDatagram(int size) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public Datagram newDatagram(int size, String addr) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public Datagram newDatagram(byte[] buf, int size) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException;
}
