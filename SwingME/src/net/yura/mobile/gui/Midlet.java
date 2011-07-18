/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui;

import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import net.yura.mobile.logging.DesktopLogger;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public abstract class Midlet extends MIDlet {

    public static final int PLATFORM_NOT_DEFINED = 0;
    public static final int PLATFORM_NOKIA_S40 = 1;
    public static final int PLATFORM_NOKIA_S60 = 2;
    public static final int PLATFORM_SONY_ERICSSON = 3;
    public static final int PLATFORM_SAMSUNG = 4;
    public static final int PLATFORM_MOTOROLA = 5;
    public static final int PLATFORM_SIEMENS = 6;
    public static final int PLATFORM_LG = 7;
    public static final int PLATFORM_ME4SE = 8;
    public static final int PLATFORM_WTK = 9;
    public static final int PLATFORM_ANDROID = 10;
    public static final int PLATFORM_BLACKBERRY = 11;

    private static int platform = detectPlatform();

    static {
        //#debug warn
        Logger.setLogger( new DesktopLogger(Logger.WARN) );
    }

    private DesktopPane rootpane;
    private Hashtable platformReqParams;

    public Midlet() {

            // Changing thread priority should only be done in platforms
            // were actually improve performance.
            QueueProcessorThread.CHANGE_PRIORITY = !((Midlet.getPlatform() == Midlet.PLATFORM_ANDROID || Midlet.getPlatform() == Midlet.PLATFORM_BLACKBERRY));

            rootpane = makeNewRootPane();

    }
    /**
     * THIS METHOD WILL MAKE A NEW ROOTPANE
     * but nothing can be set up here as things like getHeight wont work
     */
    protected DesktopPane makeNewRootPane() {
        return new DesktopPane(this, 0xFF000000, null);
    }

    /**
     * this will set up everything needed to start the
     * app like the size and stuff
     */
    protected abstract void initialize(DesktopPane rootpane);

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {

            rootpane.kill();

            Display.getDisplay(this).setCurrent((Displayable)null);

            notifyDestroyed();

    }


    protected void pauseApp() {
            // TODO
            //Logger.debug("pauseApp");
    }

    protected void startApp() {
            // TODO come out of pause
            //Logger.debug("startApp");
    }

    public static int getPlatform() {
        return platform;
    }

    private static int detectPlatform() {

        // detecting BLACKBERRY
        if (hasClass("net.rim.device.api.ui.UiApplication")) {
            return PLATFORM_BLACKBERRY;
        }

        // detecting ME4SE
        if (hasClass("org.me4se.MIDletRunner")) {
            return PLATFORM_ME4SE;
        }

        String currentPlatform = System.getProperty("microedition.platform");
        if (currentPlatform == null) {
            currentPlatform = ""; // Avoid null pointer exceptions
        }

        // detecting NOKIA
        if (currentPlatform.indexOf("Nokia") >= 0) {
            // detecting S40 vs S60
            if (hasClass("com.nokia.mid.impl.isa.ui.gdi.Pixmap")) {
                return PLATFORM_NOKIA_S40;
            }
            return PLATFORM_NOKIA_S60;
        }

        // detecting SONY ERICSSON
        if (currentPlatform.indexOf("SonyEricsson") >= 0 ||
            System.getProperty("com.sonyericsson.java.platform") != null) {
            return PLATFORM_SONY_ERICSSON;
        }

        // detecting SAMSUNG
        if (hasClass("com.samsung.util.Vibration")) {
            return PLATFORM_SAMSUNG;
        }

        // detecting MOTOROLA
        if (hasClass("com.motorola.multimedia.Vibrator") ||
            hasClass("com.motorola.graphics.j3d.Effect3D") ||
            hasClass("com.motorola.multimedia.Lighting") ||
            hasClass("com.motorola.multimedia.FunLight") ||
            hasClass("com.motorola.phonebook.PhoneBookRecord")) {
            return PLATFORM_MOTOROLA;
        }

        // detecting SIEMENS
        if (hasClass("com.siemens.mp.io.File")) {
            return PLATFORM_SIEMENS;
        }

        // detecting LG
        if (hasClass("mmpp.media.MediaPlayer") ||
            hasClass("mmpp.phone.Phone") ||
            hasClass("mmpp.lang.MathFP") ||
            hasClass("mmpp.media.BackLight")) {
                return PLATFORM_LG;
        }

        // detecting WTK
        if (currentPlatform.indexOf("wtk") >= 0) {
            return PLATFORM_WTK;
        }

        // detecting Android
        if (hasClass("android.app.Activity")) {
            return PLATFORM_ANDROID;
        }

        return PLATFORM_NOT_DEFINED;
    }

    // Utility method. Finds if a class is available to the class Loader
    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }


    //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,
    //==== platform Requests ===================================================
    //�``����,��,����``����,��,����``����,��,����``����,��,����``����,��,����``�

    public static Midlet getMidlet() {
        return DesktopPane.getDesktopPane().midlet;
    }

    // This will only be called on Android.
    public void platformRequest(String url, Object arg) {
        try {
            if (platformReqParams == null) {
                platformReqParams = new Hashtable();
            }

            if (arg != null) {
                platformReqParams.put(url, arg);
            }

            platformRequest(url);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    // This will only be called on Android. By the "Android" side
    public Object retrievePlatformRequestParam(String url) {
        Object res = platformReqParams.remove(url);
        return res;
    }

    public static void call(String number) {
        try {
            // TODO remove spaces from number
            getMidlet().platformRequest("tel:" + number);
        }
        catch (Exception e) {
            //#mdebug warn
            Logger.warn("can not call: " + number + " " + e.toString());
            Logger.warn(e);
            //#enddebug
        }

    }

    public static void openURL(String url) {
        try {
            getMidlet().platformRequest(url);
        }
        catch (Exception e) {
            //#mdebug warn
            Logger.warn("can not open url: " + url + " " + e.toString());
            Logger.warn(e);
            //#enddebug
        }

    }

    public static void vibration(int duration) {
        try {
            Display.getDisplay(getMidlet()).vibrate(duration);
        }
        catch (Exception e) {
            //#mdebug warn
            Logger.warn("can not vibration " + e.toString());
            Logger.warn(e);
            //#enddebug
        }
    }

    /**
     * @see java.lang.System#exit(int) System.exit
     */
    public static void exit() {
        try {
            getMidlet().destroyApp(true);
        }
        catch (Exception ex) {
            // as you called this yourself, you should not be throwing here
            Logger.warn(ex);
            throw new RuntimeException();
        }
    }

    public static void hide() {
        Display.getDisplay(getMidlet()).setCurrent(null);
    }

    /**
     * name MUST start with a "/"
     * @see java.lang.Class#getResourceAsStream(java.lang.String) Class.getResourceAsStream
     */
    public static InputStream getResourceAsStream(String name) {

        try {
            String resdir;
            try {
                // when running as a me4se applet, this can throw a SecurityException
                resdir = System.getProperty("resdir");
            }
            catch (Throwable th) {
                resdir = null;
            }
            if (resdir!=null) { // (getPlatform()==PLATFORM_ANDROID) {
                InputStream is = Midlet.class.getResourceAsStream(resdir+name);
                if (is!=null) {
                    return is;
                }
            }
        }
        catch(Throwable th) {
            th.printStackTrace();
        }

        return Midlet.class.getResourceAsStream(name);
    }
    
    public void onResult(int code, Object obj) {
    	
    }

}
