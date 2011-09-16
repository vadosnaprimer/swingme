package net.yura.blackberry.rim;

import java.util.Calendar;

import javax.microedition.io.ConnectionNotFoundException;

import net.yura.blackberry.WebPayment;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.picker.DateTimePicker;
import net.yura.blackberry.BlackBerryOptionPane;
import net.yura.blackberry.ConnectionManager;
import net.yura.mobile.gui.Animation;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.util.BlackBerryThumbLoader;
import net.yura.mobile.util.Url;
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
        	if ((CodeModuleManager.getModuleHandle("net_rim_bb_lbs") > 0) || (CodeModuleManager.getModuleHandle("net_rim_bb_maps") > 0)) {
        		//geo:51.47342,-0.172655?q=51.47342%2c-0.172655%28Candy+was+here%29
            	String lat = url.substring(4,url.indexOf(","));
            	String longi = url.substring(url.indexOf(",")+1, url.indexOf("?"));            	
            	int ilat = (int) (Double.parseDouble(lat) * 100000);
            	int ilong = (int) (Double.parseDouble(longi) * 100000);
                String description = Url.decode(url.substring(url.indexOf("%28")+3,url.indexOf("%29")));
                String document = "<lbs clear='ALL' id='yura'><location x='" + Integer.toString(ilong) + "' y='" + Integer.toString(ilat) + "' label='" + description + "' description='" + description + "' zoom='0' /></lbs>";
                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments(MapsArguments.ARG_LOCATION_DOCUMENT, document));
        	} 
        	else {
                Browser.getDefaultSession().displayPage("http://maps.google.com/" + url.substring(url.indexOf("?q=")));
        	}          
        }
        else if (url.startsWith("native://net.yura.android.datepicker.CalendarPickerActivity/")) {
        	Url u = new Url(url);
        	String date = u.getPathSegment(0);        	
        	Calendar initialDate = Calendar.getInstance();
        	initialDate.set(Calendar.DATE, Integer.parseInt(date.substring(8,10)));
        	initialDate.set(Calendar.MONTH, Integer.parseInt(date.substring(5,7))-1);
        	initialDate.set(Calendar.YEAR, Integer.parseInt(date.substring(0,4)));        	
        	final DateTimePicker datePicker = DateTimePicker.createInstance( initialDate,  DateFormat.DATE_SHORT, -1);
	        datePicker.doModal();	        
	        Midlet.getMidlet().onResult(0, -1, datePicker.getDateTime());	        	        
        }
        else if (url.startsWith("nativeNoResult://com.badoo.mobile.android.view.WebPaymentAVC")) {
        	Url u = new Url(url);        	
        	String startURL = u.getPathSegment(0);
        	String endURL = u.getPathSegment(1);
        	String browserTitle = u.getPathSegment(2);        	
        	WebPayment wp = new WebPayment(startURL, endURL, browserTitle);        	
        }
        else {
            throw new ConnectionNotFoundException();
        }
    	    	
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
