package net.yura.blackberry.rim;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.Device;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;

import net.yura.blackberry.BlackBerryOptionPane;
import net.yura.blackberry.ConnectionManager;

import net.yura.mobile.gui.Animation;
import net.yura.mobile.util.BlackBerryThumbLoader;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.ImageUtil;

public abstract class MIDlet extends UiApplication {

	protected ConnectionManager conManager;
    
	 public MIDlet() {            
        BlackBerryOptionPane.init();
        
        conManager = ConnectionManager.getInstance();
        UiApplication.getUiApplication().addGlobalEventListener(conManager); // Listen to service books changes
        WLANInfo.addListener(conManager.getConnWIFIListener()); // Listen to WIFI changes
        CoverageInfo.addListener(conManager.getConnRadioListener()); // Listen to radio coverage changes
        
        Animation.FPS = 2;
        ImageUtil.thumbLoader = new BlackBerryThumbLoader();
        //setPermissions();
        
    }
	
	
	 
	 private void setPermissions() {		
		final ApplicationPermissionsManager apm = ApplicationPermissionsManager.getInstance();	
		final ApplicationPermissions desiredPermissions = new ApplicationPermissions();
		
		// INPUT_SIMULATION is required to dismiss the camera after a photo has been taken
		desiredPermissions.addPermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION);
		desiredPermissions.addPermission(ApplicationPermissions.PERMISSION_WIFI);
		// LOCATION_DATA is required for GPS
		desiredPermissions.addPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);
		desiredPermissions.addPermission(ApplicationPermissions.PERMISSION_FILE_API);
		ApplicationPermissions currentPermissions = apm.getApplicationPermissions();
		    	
		if (currentPermissions.getPermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION) == ApplicationPermissions.VALUE_ALLOW
				&& currentPermissions.getPermission(ApplicationPermissions.PERMISSION_WIFI) == ApplicationPermissions.VALUE_ALLOW
				&& currentPermissions.getPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA) == ApplicationPermissions.VALUE_ALLOW
				&& currentPermissions.getPermission(ApplicationPermissions.PERMISSION_FILE_API) == ApplicationPermissions.VALUE_ALLOW) {
			// permissions are fine
		} else {			
			UiApplication.getUiApplication().invokeLater(new Runnable() {				
				public void run() {
					apm.invokePermissionsRequest(desiredPermissions);					
				}
			});	
		}      
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
    			System.out.println(property);
    			return property;
    		}
    	//#mdebug info
    	}  
    	//#enddebug
    	return null;
    }
    
    public boolean platformRequest(String url) throws ConnectionNotFoundException {
    	if (url.startsWith("http://")) {
    		Browser.getDefaultSession().displayPage(url);
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
		int keyLayout = Keypad.getHardwareLayout();
		boolean qwerty = keyLayout == Keypad.HW_LAYOUT_32
				|| keyLayout == Keypad.HW_LAYOUT_39
				|| keyLayout == Keypad.HW_LAYOUT_LEGACY
				|| keyLayout == Keypad.HW_LAYOUT_PHONE;

		boolean qw_er_ty = keyLayout == Keypad.HW_LAYOUT_REDUCED
				|| keyLayout == Keypad.HW_LAYOUT_REDUCED_24;

		KeyEvent.BLACKBERRY_QWERTY = qwerty;
		if (args.length == 0) {
			Dialog.alert("no args given, pls give args");
		}
		else {
			try {
				MIDlet theApp = (MIDlet) Class.forName(args[0]).newInstance();
				theApp.enterEventDispatcher();
			}
			catch (Exception ex) {
				Dialog.alert("error starting: " + ex.toString());
			}
		}
	}

}
