package javax.microedition.media;

/**
 * @API MIDP-2.0
 * @API MMAPI-1.0
 */ 
public interface Controllable {

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract Control[] getControls();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract Control getControl(String s);
}
