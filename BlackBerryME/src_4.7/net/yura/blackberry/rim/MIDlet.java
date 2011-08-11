package net.yura.blackberry.rim;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.yura.mobile.gui.KeyEvent;

public abstract class MIDlet extends UiApplication {

	public int checkPermission(String string) {
		return 0;
	}

    public final void notifyDestroyed() {

    }
	
    public boolean platformRequest(String url) {
    	
    	// launch native date picker?
    	
    	return false;
	}

    public static void main(String[] args) {
    	
        
        
        int keyLayout = Keypad.getHardwareLayout();
        
        
        boolean qwerty =keyLayout==Keypad.HW_LAYOUT_32 ||
        keyLayout==Keypad.HW_LAYOUT_39 ||
        keyLayout==Keypad.HW_LAYOUT_LEGACY ||
        keyLayout==Keypad.HW_LAYOUT_PHONE;

boolean qw_er_ty=keyLayout==Keypad.HW_LAYOUT_REDUCED ||
                 keyLayout==Keypad.HW_LAYOUT_REDUCED_24;

KeyEvent.BLACKBERRY_QWERTY = qwerty;
        
        
        
        
        
    	if (args.length==0) {
    		Dialog.alert("no args given, pls give args");
    	}
    	else {
    		try {
	        	MIDlet theApp = (MIDlet) Class.forName( args[0] ).newInstance();
	            theApp.enterEventDispatcher();
    		}
    		catch (Exception ex) {
    			Dialog.alert("error starting: "+ex.toString() );
    		}
    	}

    }

}
