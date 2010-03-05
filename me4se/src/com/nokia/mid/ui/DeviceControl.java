package com.nokia.mid.ui;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.ApplicationManager;



/**
 * @API NOKIAUI
 */
public class DeviceControl {

	/**
	 * @API NOKIAUI
	 * @ME4SE UNIMPLEMENTED
	 */
    public static void setLights(int num, int level) {
		//System.out.println("ME4SE: DeviceControl.setLights(int num='" + num + "', int level='" + level + "') called with no effect!");
    }

	/**
	 * @API NOKIAUI
	 * @ME4SE UNIMPLEMENTED
	 */
    public static void flashLights(long duration) {
		//System.out.println("ME4SE: DeviceControl.flashLights(long duration='" + duration + "') called with no effect!");
    }

	/**
	 * @API NOKIAUI
	 */
    public static void startVibra(int freq, long duration) {
    	Display.getDisplay(ApplicationManager.getInstance().active).vibrate((int) duration);
    }

	/**
	 * @API NOKIAUI
	 */
    public static void stopVibra() {
    	Display.getDisplay(ApplicationManager.getInstance().active).vibrate(0);
    }
}
