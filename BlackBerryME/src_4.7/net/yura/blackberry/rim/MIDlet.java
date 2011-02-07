package net.yura.blackberry.rim;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

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
