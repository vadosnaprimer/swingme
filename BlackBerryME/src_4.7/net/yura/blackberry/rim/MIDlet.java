package net.yura.blackberry.rim;

import javax.microedition.io.ConnectionNotFoundException;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.maps.MapView;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.yura.blackberry.BlackBerryOptionPane;
import net.yura.blackberry.ConnectionManager;
import net.yura.mobile.gui.Animation;
import net.yura.mobile.util.BlackBerryThumbLoader;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.ImageUtil;

public abstract class MIDlet extends UiApplication {

    protected ConnectionManager conManager;

    public static final int HW_LAYOUT_ITUT = 1230263636; // BB 9105, came with OS-5, but constant only came in OS-6

    public MIDlet() {            
        BlackBerryOptionPane.init();
        
        conManager = ConnectionManager.getInstance();
        UiApplication.getUiApplication().addGlobalEventListener(conManager); // Listen to service books changes
        WLANInfo.addListener(conManager.getConnWIFIListener()); // Listen to WIFI changes
        CoverageInfo.addListener(conManager.getConnRadioListener()); // Listen to radio coverage changes
        
        Animation.FPS = 2;
        
        
        int keyLayout = Keypad.getHardwareLayout();

// this method causes too many problems and cant really be used as returns different results depending on if the hardware keyboard is opened or not
// would have to use DeviceCapability.isPhysicalKeyboardAvaible too, but then we do not know what type of keyboard it is
// http://supportforums.blackberry.com/t5/Java-Development/Keypad-getHardwareLayout/td-p/743935
//                   boolean qwerty = keyLayout == Keypad.HW_LAYOUT_32
//                                   || keyLayout == Keypad.HW_LAYOUT_39
//                                   || keyLayout == Keypad.HW_LAYOUT_LEGACY
//                                   || keyLayout == Keypad.HW_LAYOUT_PHONE;
//
//                   boolean qw_er_ty = keyLayout == Keypad.HW_LAYOUT_REDUCED
//                                   || keyLayout == Keypad.HW_LAYOUT_REDUCED_24;

        KeyEvent.BLACKBERRY_ITUT = keyLayout == HW_LAYOUT_ITUT;
        
        ImageUtil.thumbLoader = new BlackBerryThumbLoader();
        
    }
		 
	public int checkPermission(String string) {
		return 0;
	}
    public final void notifyDestroyed() {
    	System.exit(0);	
    }
	
     //user_agent=null, app_version=1.6.2, device_id=0000000000000000000000000357841030097477, language=0, app_build=Android, app_name=BMA/Android, screen_height=800}
     public String getAppProperty(String s) { 	
    	//#mdebug info
    	// These app properties are added to the JAD at compile time in the ANT build file, this will thus not be available when debugging on the simulator, also: 
    	// http://supportforums.blackberry.com/t5/Java-Development/Read-Jad-properties-from-emulator-OS-4-5/td-p/85069
    	if (DeviceInfo.isSimulator()) {
    		if (s.equals("App-Build")) {
        		return "blackberry";
        	} else if (s.equalsIgnoreCase("App-Name")) {
        		return "BMA/BlackBerry";
        	} else if (s.equalsIgnoreCase("Help-Url")) {
        		return "http://badoo.com/help/";
        	} else if (s.equalsIgnoreCase("Terms-Url")) {
        		return "http://badoo.com/terms/";
        	} else if (s.equalsIgnoreCase("Server-Url")) {
        		return "bma.badoo.com:2121";
        	} else {
        		return null;
        	}
    	}
    	else {
    	//#enddebug
    		CodeModuleGroup[] allModuleGroups = CodeModuleGroupManager.loadAll();
    		CodeModuleGroup codeModuleGroup = null;
    		String moduleName = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
    		for (int i = 0; i < allModuleGroups.length; i++) {
    		   if (allModuleGroups[i].containsModule(moduleName)) {
    			   codeModuleGroup = allModuleGroups[i];
    			   break;
    		    }
    		}
    		if (codeModuleGroup!=null) {
    			String property = codeModuleGroup.getProperty(s);
    			//#debug info
    			System.out.println(property);
    			return property;
    		}
    	//#mdebug info
    	}  
    	//#enddebug
    	return null;
    }
    
    public boolean platformRequest(String url) throws ConnectionNotFoundException {

    	if (url.startsWith("http://") || url.startsWith("https://")) {
    		Browser.getDefaultSession().displayPage(url);
    	}
    	else if (url.equals("clipboard://get")) {
            
            // Retrieve the Clipboard object.
            net.rim.device.api.system.Clipboard  cp = net.rim.device.api.system.Clipboard.getClipboard();
            
            Object obj = cp.get();
            if (obj==null) {
                    // TODO System.clearProperty("clipboard.text");
            }
            else {
                    // TODO System.setProperty("clipboard.text", obj.toString()); // so far we only support Strings
            }
            
        }
        else if (url.startsWith("clipboard://put/")) {

            // Retrieve the Clipboard object.
            net.rim.device.api.system.Clipboard  cp = net.rim.device.api.system.Clipboard.getClipboard();
                        
            String text = url.substring( "clipboard://put/".length() );
            if (!"".equals(text)) {
                cp.put( text );
            }
            
        }
        else if (url.startsWith("geo:")) {
        	//geo:51.47342,-0.172655?q=51.47342%2c-0.172655%28Candy+was+here%29
        	String lat = url.substring(4,url.indexOf(","));
        	String longi = url.substring(url.indexOf(",")+1, url.indexOf("?"));
        	MapView mapView = new MapView();
        	int ilat = (int) (Double.parseDouble(lat)*100000);
        	int ilong = (int) (Double.parseDouble(longi)*100000);
            mapView.setLatitude(ilat);
            mapView.setLongitude(ilong);
            mapView.setZoom(10);
            MapsArguments mapsArgs = new MapsArguments(mapView);
            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, mapsArgs);
        }
        else {
            throw new ConnectionNotFoundException();
        }
    	
    	
    	// launch native date picker?
    	
    	/*
    	 * Getting the default date time picker

        final DateTimePicker datePicker = DateTimePicker.createInstance();
        datePicker.doModal(); 

		Specifying a date format		
		        final DateTimePicker datePicker = DateTimePicker.createInstance( Calendar.getInstance(), "yyyy-MM-dd", null);
		        datePicker.doModal();
		 
		
		Specifying a time format		
		        final DateTimePicker datePicker = DateTimePicker.createInstance( Calendar.getInstance(), null, "hh:mm:ss aa");
		        datePicker.doModal();
		 
		
		Specifying a preset date format		
		        final DateTimePicker datePicker = DateTimePicker.createInstance( Calendar.getInstance(), DateFormat.DATE_FULL, -1);
		        datePicker.doModal();
 
    	 * */
    	
    	return false;
	}

	public static void main(String[] args) {

		if (args.length == 0) {
		    
		    System.out.println("no args given, pls give args");
		    
		    // TODO THIS WILL NOT WORK AS WE ARE NOT THE EVENT THREAD!!
			Dialog.alert("no args given, pls give args");
		}
		else {
			try {
				MIDlet theApp = (MIDlet) Class.forName(args[0]).newInstance();
				theApp.enterEventDispatcher();
			}
			catch (Exception ex) {
			    
			    System.out.println("error starting: " + ex.toString());
			    
	                    // TODO THIS WILL NOT WORK AS WE ARE NOT THE EVENT THREAD!!
				Dialog.alert("error starting: " + ex.toString());
			}
		}
	}

}
