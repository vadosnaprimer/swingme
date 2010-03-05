package javax.microedition.io.file;

/**
 * @author Michael Kroll, michael.kroll@trantor.de
 * @API PDAP-1.0
 */

public interface FileSystemListener {
	/**
	 * @API PDAP-1.0
	 */
	public static int ROOT_ADDED = 0;
	/**
	 * @API PDAP-1.0
	 */
	public static int ROOT_REMOVED = 1;
 
	/**
	 * @API PDAP-1.0
	 */
	public void rootChanged(int state, java.lang.String rootName);
}

