package javax.microedition.io;

import javax.microedition.pki.Certificate;

/**
 * @API MIDP-2.0 
 */
public interface SecurityInfo {

	/**
	 * @API MIDP-2.0 
	 */
	public abstract Certificate getServerCertificate();

	/**
	 * @API MIDP-2.0 
	 */
	public abstract String getProtocolVersion();

	/**
	 * @API MIDP-2.0 
	 */
	public abstract String getProtocolName();

	/**
	 * @API MIDP-2.0 
	 */
	public abstract String getCipherSuite();
}
