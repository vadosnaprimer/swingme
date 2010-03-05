package javax.microedition.media.control;

import javax.microedition.media.Control;

/**
 * @API MIDP-2.0
 * @API MMAPI-1.0
 */ 
public interface ToneControl extends Control {

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte VERSION = -2;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte TEMPO = -3;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte RESOLUTION = -4;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte BLOCK_START = -5;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte BLOCK_END = -6;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte PLAY_BLOCK = -7;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte SET_VOLUME = -8;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte REPEAT = -9;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte C4 = 60;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final byte SILENCE = -1;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void setSequence(byte abyte0[]);

}
