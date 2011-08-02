package net.yura.blackberry;

import java.util.Vector;

import javax.microedition.location.Criteria;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import net.yura.abba.ui.UIServices;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.logging.Logger;

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
		return "1.6.1"; //TODO This is hard coded to version 1.6.1 at the moment, for the server does not support a blackberry specific version yet. Using android version 1.6.1. 
		/*String appVersion = Midlet.getMidlet().getAppProperty("MIDlet-Version");
        if (appVersion==null) {
        	appVersion="unknown version";
        }
        return appVersion;*/
	}
	
	public LocationProvider getLocationProvider() {
		 try {			 
			 Criteria myCriteria = new Criteria();
			 myCriteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
			 LocationProvider lp = LocationProvider.getInstance(myCriteria);
			 if (lp == null){
				 //#debug info
	             Logger.info(">> Default location provider not found, cell site location provider used");
				 return CellSiteLocationProvider.getInstance(null);
			 } else {
				 return lp;
			 }
		 } catch (LocationException e) {
             //#debug warn
             Logger.warn("Uncaught exception in CommsManager.initLocationUpdates");
             //#debug error
             Logger.error(e);
             throw new RuntimeException(e.getMessage()); // should never happen - android does not throw LocationException
         }		 
	 }
	
}
