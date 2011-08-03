package net.yura.blackberry.midlet;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDletStateChangeException;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.yura.blackberry.BlackBerryOptionPane;
import net.yura.blackberry.BlackBerryThumbLoader;
import net.yura.blackberry.ConnectionManager;
import net.yura.mobile.gui.Animation;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.ImageUtil;

public abstract class MIDlet extends javax.microedition.midlet.MIDlet implements KeyListener {

    int keyPressed;

    protected ConnectionManager conManager;
    
    public MIDlet() {
        // Register RIM key listener
        Application.getApplication().addKeyListener(this);
                
        BlackBerryOptionPane.init();
        
        conManager = ConnectionManager.getInstance();
        UiApplication.getUiApplication().addGlobalEventListener(conManager); // Listen to service books changes
        WLANInfo.addListener(conManager.getConnWIFIListener()); // Listen to WIFI changes
        CoverageInfo.addListener(conManager.getConnRadioListener()); // Listen to radio coverage changes
        
        Animation.FPS = 2;       
        
        int keyLayout = Keypad.getHardwareLayout();
        
        boolean qwerty =keyLayout==Keypad.HW_LAYOUT_32 ||
        				keyLayout==Keypad.HW_LAYOUT_39 ||
        				keyLayout==Keypad.HW_LAYOUT_LEGACY ||
        				keyLayout==Keypad.HW_LAYOUT_PHONE;
        
        boolean qw_er_ty=keyLayout==Keypad.HW_LAYOUT_REDUCED ||
						 keyLayout==Keypad.HW_LAYOUT_REDUCED_24;
        
        KeyEvent.BLACKBERRY_QWERTY = qwerty;
        
        ImageUtil.thumbLoader = new BlackBerryThumbLoader();
        setPermissions();
    }
        
    /* This method asserts the permissions that Badoo requires to run */
    private void setPermissions(){
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

    public boolean keyChar(char key, int status, int time) {
        return false;
    }

    public boolean keyRepeat(int keycode, int time) {
        return false;
    }

    public boolean keyStatus(int keycode, int time) {
        return false;
    }

    
    
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    	   			
    }
    
    public boolean keyDown(int keycode, int time) {
    	
    	Class screen = UiApplication.getUiApplication().getActiveScreen().getClass();
    	if (net.rim.device.api.ui.component.Dialog.class==screen) return false;
    	if ("net.rim.device.api.ui.menu.DefaultMenuScreen".equals(screen.getName())) return false;

    	
    	Object canvas = Display.getDisplay(this).getCurrent();
    	if (!(canvas instanceof DesktopPane)) return false;
    	
        //#debug debug
        System.out.println(">>>> keyDown");

        // We only handle escape/menu button here, and wait for the key-up.
        // We can receive key up's without a key down, and we will ignore that.
        keyPressed = Keypad.key(keycode);
        return (keyPressed == Keypad.KEY_ESCAPE ||
                keyPressed == Keypad.KEY_MENU);
    }

    public boolean keyUp(int keycode, int time) {
    	Object canvas = Display.getDisplay(this).getCurrent();
    	if (!(canvas instanceof DesktopPane)) return false;
    	
        //#debug debug
        System.out.println(">>>> keyUp");

        int key = Keypad.key(keycode);

        // Ignore key up's without a key down.
        if (keyPressed == key) {
            keyPressed = 0;
            if (key == Keypad.KEY_ESCAPE) {
                processKey( (DesktopPane)canvas, KeyEvent.KEY_END);
                return true;
            }
            if(key == Keypad.KEY_MENU) {
                processKey( (DesktopPane)canvas, KeyEvent.KEY_MENU);
                return true;
            }
        }

        return false;
    }

    void processKey(DesktopPane canvas, int key) {
        try {
            //#debug debug
            System.out.println(">>>> Found a DesktopPane to fw event");

            ((DesktopPane)canvas).keyPressed(key);
            ((DesktopPane)canvas).keyReleased(key);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    
  
	/**
	 * Shows the softkeyboard if the device supports it. This method is only supported on the Android platform at the moment.
	 * @see #hideSoftKeyboard()
	 * @see #isSoftKeyboardShown()
	 * /
	public static void showSoftKeyboard() {
		//#if polish.android1.5
		//#	MidletBridge.instance.showSoftKeyboard();
		//#elif polish.blackberry && polish.hasPointerEvents && polish.usePolishGui
			Display disp = Display.getDisplay(this);
			if (disp != null) {
				VirtualKeyboard keyboard = ((BaseScreen)(Object)disp).getVirtualKeyboard();
				if (keyboard != null) {
					keyboard.setVisibility( VirtualKeyboard.SHOW );					
				}
			}
		//#endif
	}
	
	/**
	 * Hides the softkeyboard if the device supports it. This method is only supported on the Android platform at the moment.
	 * @see #showSoftKeyboard()
	 * @see #isSoftKeyboardShown()
	 * /
	public static void hideSoftKeyboard() {
		//#if polish.android1.5
		//#	MidletBridge.instance.hideSoftKeyboard();
		//#elif polish.blackberry && polish.hasPointerEvents && polish.usePolishGui
			Display disp = Display.getInstance();
			if (disp != null) {
				VirtualKeyboard keyboard = ((BaseScreen)(Object)disp).getVirtualKeyboard();
				if (keyboard != null) {
					keyboard.setVisibility( VirtualKeyboard.HIDE );					
				}
			}
		//#endif
	}
    
    */
    
    
}
