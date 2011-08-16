package net.yura.blackberry.rim;

import java.util.Hashtable;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;


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
		    Screen src = (Screen)screen;
		    
		    // if this screen is already the current one, we do not want to push it a second time
		    if (src != midlet.getActiveScreen()) {
		        midlet.pushScreen(src);
		    }
		}
		if (screen == null) {
			UiApplication.getUiApplication().requestBackground();
		}
	}

	public void callSerially(Runnable runner) {
		midlet.invokeLater(runner);
	}

	public void vibrate(int duration) {
		// TODO 
	}

}
