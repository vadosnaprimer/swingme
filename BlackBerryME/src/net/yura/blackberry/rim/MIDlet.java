package net.yura.blackberry.rim;

import net.rim.device.api.ui.UiApplication;

public abstract class MIDlet extends UiApplication {

	public int checkPermission(String string) {
		return 0;
	}

    public final void notifyDestroyed() {
    	
    	
    }
	
    public boolean platformRequest(String url) {
    	return false;
	}

}
