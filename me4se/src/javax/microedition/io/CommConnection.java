/*
 * $Id: CommConnection.java,v 1.1 2007/07/29 19:12:33 haustein Exp $
 */

package javax.microedition.io;

/**
 * @author Michael Kroll, michael.kroll@trantor.de
 * @API MIDP-2.0
 */
public interface CommConnection extends StreamConnection {

	/**
	 * @API MIDP-2.0 
	 */
	public int getBaudRate();

	/**
	 * @API MIDP-2.0 
	 */
	public int setBaudRate(int baudrate);
}

/*
 * $Log: CommConnection.java,v $
 * Revision 1.1  2007/07/29 19:12:33  haustein
 * Initial checkin of contents moved from the kobjects.org me4se module...
 *
 * Revision 1.2  2003/11/08 16:01:01  mkroll
 * added api tags
 *
 * Revision 1.1  2003/11/06 21:27:31  mkroll
 * added empty classes and methods.
 *
 */

