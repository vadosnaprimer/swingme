package net.yura.blackberry;

import net.yura.abba.ui.UIServices;
import net.yura.mobile.gui.Midlet;

import com.badoo.mobile.AppServices;
import com.badoo.mobile.platform.PlatformServices;

public class BlackBerryPlatformServices extends PlatformServices {

	public void registerForNotifications(boolean arg01) {
		if (arg01) {
			AppServices.getInstance().getBadgeManager().addBadgeListener(Midlet.getMidlet());
		} else {
			Midlet.getMidlet().deregisterNotifications();
		}
	}
	
	public String getAppVersion(){
		String appVersion = Midlet.getMidlet().getAppProperty("MIDlet-Version");
        if (appVersion==null) {
        	appVersion="unknown version";
        }
        return appVersion;
	}
	
}
