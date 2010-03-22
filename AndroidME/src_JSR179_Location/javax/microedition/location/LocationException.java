package javax.microedition.location;

/**
 * The <code>LocationException</code> is thrown when a location API specific
 * error has occurred.  The detailed conditions when this exception is
 * thrown are documented in the methods that throw this exception.
 */
public class LocationException
	extends Exception
{
	/**
	 * Constructs a <code>LocationException</code> with the specified detail message.
	 * 
	 * @param message - the detailed exception message.
	 */
	public LocationException (String message)
	{
		super(message);
	}
}
