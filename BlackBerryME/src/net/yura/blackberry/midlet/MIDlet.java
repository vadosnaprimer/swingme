package net.yura.blackberry.midlet;

import javax.microedition.lcdui.Display;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.FullScreen;
// import net.rim.device.api.ui.VirtualKeyboard; // API - 4.7.0 does not work on 'BlackBerry Bold (4.6)'
import net.yura.blackberry.BlackBerryOptionPane;
import net.yura.mobile.gui.Animation;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.io.SocketClient;
import net.yura.mobile.logging.Logger;

public abstract class MIDlet extends javax.microedition.midlet.MIDlet implements KeyListener{

    int keyPressed;

    public MIDlet() {
        // Register RIM key listener
        Application.getApplication().addKeyListener(this);
        
        BlackBerryOptionPane.init();
        
        Animation.FPS = 2;
        
        int keyLayout = Keypad.getHardwareLayout();
        
        boolean qwerty =keyLayout==Keypad.HW_LAYOUT_32 ||
        				keyLayout==Keypad.HW_LAYOUT_39 ||
        				keyLayout==Keypad.HW_LAYOUT_LEGACY ||
        				keyLayout==Keypad.HW_LAYOUT_PHONE;
        
        boolean qw_er_ty=keyLayout==Keypad.HW_LAYOUT_REDUCED ||
						 keyLayout==Keypad.HW_LAYOUT_REDUCED_24;
        
        KeyEvent.BLACKBERRY_QWERTY = qwerty;
        
        
        setInternetConnectionString();
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
     * Determines what connection type to use. 
     * See http://www.localytics.com/blog/post/how-to-reliably-establish-a-network-connection-on-any-blackberry-device/
     */
    private void setInternetConnectionString() {
    	
        // This code is based on the connection code developed by Mike Nelson of AccelGolf.
        // http://blog.accelgolf.com/2009/05/22/blackberry-cross-carrier-and-cross-network-http-connection        
        String connStr;                
                        
        // Simulator behaviour is controlled by the USE_MDS_IN_SIMULATOR variable.
        if(DeviceInfo.isSimulator()) {
        	//#debug debug
        	Logger.debug("Setup Internet: Simulator.");
        	connStr = ";deviceside=true";
        }                                        
        else if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED) {
        	// Wifi is the preferred transmission method
        	//#debug debug
            Logger.debug("Setup Internet: Wifi.");
            connStr = ";interface=wifi";
        }
        else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT) {
        	// Is the carrier network the only way to connect?
        	//#debug debug
            Logger.debug("Setup Internet: Carrier coverage.");
                        
            String carrierUid = getCarrierBIBSUid();
            if(carrierUid == null) {
                // Has carrier coverage, but not BIBS.  So use the carrier's TCP network
            	//#debug debug
                Logger.debug("Setup Internet: No Uid");
                connStr = ";deviceside=true";
            }
            else {
                // otherwise, use the Uid to construct a valid carrier BIBS request
            	//#debug debug
                Logger.debug("Setup Internet: uid is: " + carrierUid);
                connStr = ";deviceside=false;connectionUID="+carrierUid + ";ConnectionType=mds-public";
            }
        }                
        else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_MDS) == CoverageInfo.COVERAGE_MDS) {
        	// Check for an MDS connection instead (BlackBerry Enterprise Server)
        	//#debug debug
            Logger.debug("Setup Internet: MDS coverage found");
            connStr = ";deviceside=false";
        }
        else if(CoverageInfo.getCoverageStatus() == CoverageInfo.COVERAGE_NONE) {
        	// If there is no connection available abort to avoid bugging the user unnecessarily.
        	//#debug debug
            Logger.debug("Setup Internet: There is no available connection.");
            connStr = "";
        }
        else {
        	// In theory, all bases are covered so this shouldn't be reachable.
        	//#debug debug
            Logger.debug("Setup Internet: no option found.");
            connStr = ";deviceside=true";
        }        
        
        SocketClient.connectAppend = connStr;
    }
    
    /**
     * Looks through the phone's service book for a carrier provided BIBS network
     * @return The uid used to connect to that network.
     */
    private static String getCarrierBIBSUid() {
    	
    	// TODO: API needs signing!!!
    	
//        ServiceRecord[] records = ServiceBook.getSB().getRecords();
//        for(int currentRecord = 0; currentRecord < records.length; currentRecord++) {
//            if(records[currentRecord].getCid().toLowerCase().equals("ippp")) {
//                if(records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0) {
//                    return records[currentRecord].getUid();
//                }
//            }
//        }
        
        return null;
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
