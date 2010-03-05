package javax.microedition.io;

import java.io.IOException;

/**
 * @API MIDP-2.0 
 */
public class PushRegistry {

	private PushRegistry() {
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	 */
	public static void registerConnection(String connection, String midlet, String filter) throws ClassNotFoundException, IOException {
		System.out.println("ME4SE: PushRegistry.registerConnection(String connection='" +  connection 
		    + "', String midlet='" + midlet + "', String filter='" + filter + "') called with no effect!");
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	 */
	public static boolean unregisterConnection(String connection) {
		System.out.println("ME4SE: PushRegistry.unregisterConnection(String connection='"+connection+"') called with no effect!");
		return false;
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	 */
	public static String[] listConnections(boolean available) {
		System.out.println("ME4SE: PushRegistry.listConnections(boolean available='" + available + "') called with no effect!");
		return null;
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	 */
	public static String getMIDlet(String connection) {
		System.out.println("ME4SE: PushRegistry.getMIDlet(String connection='"+connection+"') called with no effect!");
		return null;
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	 */
	public static String getFilter(String connection) {
		System.out.println("ME4SE: PushRegistry.getFilter(String connection='"+connection+"') called with no effect!");
		return null;
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	 */
	public static long registerAlarm(String midlet, long time) throws ClassNotFoundException, ConnectionNotFoundException {
		System.out.println("ME4SE: PushRegistry.registerAlarm(String midlet='"+midlet+"', long time='"+time+"') called with no effect!");
		return 0L;
	}
}
