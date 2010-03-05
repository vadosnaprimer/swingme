package com.nokia.mid.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

/**
 * @API NOKIAUI
 */
public abstract class FullCanvas extends Canvas {

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_SOFTKEY1 = -6;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_SOFTKEY2 = -7;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_SEND = -10;
	
	/**
	 * @API NOKIAUI
	 */	
	public static final int KEY_END = -11;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_SOFTKEY3 = -5;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_UP_ARROW = -1;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_DOWN_ARROW = -2;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_LEFT_ARROW = -3;

	/**
	 * @API NOKIAUI
	 */
	public static final int KEY_RIGHT_ARROW = -4;

	/**
	 * @API NOKIAUI
	 */
	protected FullCanvas() {
        setFullScreenMode(true);
	}

	/**
	 * @API NOKIAUI
	 */
	public void addCommand(Command cmd) {
		throw new IllegalStateException("addCommand(Command cmd) not supported in FullCanvas.");
	}

	/**
	 * @API NOKIAUI
	 */
	public void setCommandListener(CommandListener l) {
		throw new IllegalStateException("setCommandListener(CommandListener l) not supported in FullCanvas.");
	}
	
	
}
