package net.yura.blackberry.rim;

import java.util.Hashtable;

import net.rim.device.api.ui.Screen;


public class Display {

	static Hashtable displays = new Hashtable();
	
	public static Display getDisplay(MIDlet midlet) {
		Display display = (Display)displays.get(midlet);
		if (display==null) {
			display = new Display(midlet);
			displays.put(midlet, display);
		}
		return display;
	}
	
	private MIDlet midlet;
	
	private Display(MIDlet mid) {
		midlet = mid;
	}

	public void setCurrent(Object screen) {
		if (screen instanceof Screen) {
			midlet.pushScreen((Screen)screen);
		}
	}

	public void callSerially(Runnable runner) {
		midlet.invokeLater(runner);
	}

	public void vibrate(int duration) {
		// TODO 
	}

}
