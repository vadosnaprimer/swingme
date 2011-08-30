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
	    
	    // if it is a canvas and this means it takes up the full screen
		if (screen instanceof Screen) {
		    Screen src = (Screen)screen;
		    
		    Screen current = midlet.getActiveScreen();
		    
		    // remove all screens from the stack untill we either end up with none, or the one we want 
		    while ( current != null && current != src ) {
		        midlet.popScreen(current);
		        current = midlet.getActiveScreen();
		    }

                    // if this screen is already the current one, we do not want to push it a second time		    
		    if (current != src) {
		        midlet.pushScreen(src);
		    }
		    else {
		        ((Canvas)screen).setInputHelper( (TextBox.InputHelper)null );
		    }
		    
		    hereAndNow = null;
		}
		
		if (screen instanceof TextBox) {
		    
		    if (hereAndNow != screen) {
		        hereAndNow = ((TextBox)screen);
		        
                        Canvas current = (Canvas)midlet.getActiveScreen();
		        
		        TextBox.InputHelper helper = new TextBox.TextBoxDialog();
		        //TextBox.InputHelper helper = new TextBox.TextBoxKeyboard();
		        //TextBox.InputHelper helper = new TextBox.TextBoxNative();
		        helper.start(hereAndNow,midlet);

		        current.setInputHelper(helper);
		    }

		}
		
		if (screen == null) {
			UiApplication.getUiApplication().requestBackground();
		}
	}
	
	TextBox hereAndNow;
	
	public Object getCurrent() {
	    return midlet.getActiveScreen();
	}

	public void callSerially(Runnable runner) {
		midlet.invokeLater(runner);
	}

	public void vibrate(int duration) {
		// TODO 
	}

}
