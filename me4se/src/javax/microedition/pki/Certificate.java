package javax.microedition.pki;

/**
 * @API MIDP-2.0 
 */
public interface Certificate {

	/**
	 * @API MIDP-2.0 
	 */
    public abstract String getSubject();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract String getIssuer();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract String getType();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract String getVersion();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract String getSigAlgName();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract long getNotBefore();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract long getNotAfter();

	/**
	 * @API MIDP-2.0 
	 */
    public abstract String getSerialNumber();
}
