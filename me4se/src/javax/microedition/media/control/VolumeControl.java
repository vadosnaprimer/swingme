package javax.microedition.media.control;

import javax.microedition.media.Control;

/**
 * @API MIDP-2.0
 * @API MMAPI-1.0
 */ 
public interface VolumeControl extends Control {

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void setMute(boolean mute);

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract boolean isMuted();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract int setLevel(int level);
	
	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract int getLevel();
}
