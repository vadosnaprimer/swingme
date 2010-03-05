package javax.microedition.io;

import java.io.*;

/**
 * @API MIDP-1.0 
 */
public class ConnectionNotFoundException extends IOException {

	/**
	 * @API MIDP-1.0 
	 */
	public ConnectionNotFoundException() {
		super();
	}
 
	/**
	 * @API MIDP-1.0 
	 */
	public ConnectionNotFoundException(String s) {
		super(s);
	}
}
