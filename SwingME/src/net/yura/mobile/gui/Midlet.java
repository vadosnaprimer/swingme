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

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

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

    private static int platform = detectPlatform();

	private DesktopPane rootpane;

	public Midlet() {

		rootpane = makeNewRootPane();

	}
	/**
	 * THIS METHOD WILL MAKE A NEW ROOTPANE
	 * but nothing can be set up here as things like getHeight wont work
	 */
	protected abstract DesktopPane makeNewRootPane();

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
		//System.out.println("pauseApp");
	}

	protected void startApp() {
		// TODO come out of pause
		//System.out.println("startApp");
	}

	public static int getPlatform() {
	    return platform;
	}

	private static int detectPlatform() {

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

        // detecting ME4SE
        if (hasClass("org.me4se.MIDletRunner")) {
            return PLATFORM_ME4SE;
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
        } catch (Throwable ex) {
            return false;
        }
	}

}
