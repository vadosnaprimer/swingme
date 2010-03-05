package javax.microedition.io;

import java.io.*;

/**
 * @API MIDP-1.0 
 */
public interface Datagram extends DataInput, DataOutput {

	/**
	 * @API MIDP-1.0 
	 */
	public String getAddress();

	/**
	 * @API MIDP-1.0 
	 */
	public byte[] getData();

	/**
	 * @API MIDP-1.0 
	 */
	public int getLength();
	
	/**
	 * @API MIDP-1.0 
	 */
	public int getOffset();

	/**
	 * @API MIDP-1.0 
	 */
	public void setAddress(String addr) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public void setAddress(Datagram reference);

	/**
	 * @API MIDP-1.0 
	 */
	public void setLength(int len);

	/**
	 * @API MIDP-1.0 
	 */
	public void setData(byte[] buffer, int offset, int len);

	/**
	 * @API MIDP-1.0 
	 */
	public void reset();
}
