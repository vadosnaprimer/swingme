// ME4SE - A MicroEdition Emulation for J2SE
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: Sebastian Vastag
//
// STATUS: API complete
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.midlet;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import javax.microedition.io.ConnectionNotFoundException;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public abstract class MIDlet {

  boolean inDestruction;

  private static final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon",
      "kazehakase", "mozilla", "netscape" };

  /**
   * @API MIDP-1.0
   */
  protected MIDlet() {
    ApplicationManager.getInstance().activeClass = this.getClass();

    /*
     * if (ApplicationManager.manager == null) throw new
     * RuntimeException("Midlet must be started via MIDletRunner");
     */
  }

  /**
   * @API MIDP-1.0
   */
  protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

  /**
   * First the property is read from the JAD-File, if the key is not available
   * in the JAD File, the property is read as system property. Example:
   * -DMIDlet-Version=1.0. Unfortunately, manifest entries cannot be read with
   * Applet safe code.
   * 
   * @API MIDP-1.0
   */
  public String getAppProperty(String key) {

    String result = ApplicationManager.getInstance().jadFile.getValue(key);

    if (result == null) {
      result = ApplicationManager.getInstance().getProperty(key);
    }

    // System.out.println("getAppProperty("+key+")='"+result+"'");
    return result;
  }

  /**
   * @API MIDP-1.0
   */
  public void notifyDestroyed() {
    /**
     * If the MIDlet currently being destroyed is one of several MIDlets in a
     * MIDlet suite, start the MIDletChooser again to give the user a chance to
     * run another MIDlet of the suite.
     */

    if (!inDestruction && this == ApplicationManager.getInstance().active)
      ApplicationManager.getInstance().destroy(false, false);

  }

  /**
   * @API MIDP-1.0
   */
  public void notifyPaused() {
  }

  /**
   * @API MIDP-1.0
   */
  protected abstract void pauseApp();

  /**
   * @API MIDP-1.0
   * @ME4SE UNSUPPORTED
   */
  public void resumeRequest() {
  }

  /**
   * @API MIDP-1.0
   */
  protected abstract void startApp() throws MIDletStateChangeException;

  /**
   * @API MIDP-2.0
   * @ME4SE UNSUPPORTED
   */
  public final boolean platformRequest(String url) throws ConnectionNotFoundException {

// YURA YURA YURA

	if (url.startsWith("tel")) {
		System.out.println("ME4SE: MIDlet.platformRequest('" + url + "') called in order to initiate a phone call.");
		return false;
	}
        else if (url.startsWith("grasshopper")) {
            try {
                String params = url.substring(url.indexOf('?')+1);
                String[] s1 = params.split("\\&");
                String appName="Unknown me4se app",appVersion="Unknown version",locale="Unknown locale";
                for (int c=0;c<s1.length;c++) {
                    String[] s2 = s1[c].split("\\=");
                    if ("name".equals(s2[0])) {
                        appName = s2[1];
                    }
                    else if ("version".equals(s2[0])) {
                        appVersion = s2[1];
                    }
                    else if ("locale".equals(s2[0])) {
                        locale = s2[1];
                    }
                    else {
                        System.out.println("unknown grasshopper param: "+s1[c]);
                    }
                }
                try {
                    net.yura.grasshopper.SimpleBug.initSimple(appName, appVersion, locale);
                    System.out.println("Grasshopper loaded");
                }
                catch(Throwable th) {
                    System.out.println("Grasshopper not loaded");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
	else {
	    if (ApplicationManager.getInstance().applet != null) {
		try {
			ApplicationManager.getInstance().applet.getAppletContext().showDocument(new URL(url));
		} catch (Exception e) {
                    ConnectionNotFoundException ex = new ConnectionNotFoundException(e.toString());
                    ex.initCause(e);
                    throw ex;
		}
		return false; // dont let the midlet exit in order to avoid the corresp.
			      // page reload
	    }
	    else {
		try {
                    openURL(url);
			//edu.stanford.ejalbert.BrowserLauncher launcher = new edu.stanford.ejalbert.BrowserLauncher();
			//launcher.openURLinBrowser( url );
		} catch (Exception e) {
                    ConnectionNotFoundException ex = new ConnectionNotFoundException(e.toString());
                    ex.initCause(e);
                    throw ex;
		}
		return false;
	    }

	}

/*
    if (ApplicationManager.getInstance().applet != null) {
      try {
        ApplicationManager.getInstance().applet.getAppletContext().showDocument(new URL(url));
      } catch (MalformedURLException e) {
        throw new ConnectionNotFoundException(e.toString());
      }
      return false; // dont let the midlet exit in order to avoid the corresp.
      // page reload
    } else {
      if (url.startsWith("tel")) {
        System.out.println("ME4SE: MIDlet.platformRequest('" + url + "') called in order to initiate a phone call.");
        return false;
      } else if (url.startsWith("http")) {
        openURL(url);
        return false;
      } else {
        throw new ConnectionNotFoundException(url);
      }
    }
*/
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNSUPPORTED
   */
  public final int checkPermission(String permission) {
    System.out.println("MIDlet.checkPermission() called with no effect!");
    return -4711;
  }

  /** 
   * Internal message to open a webbrowser if platformRequest with an http url is called.
   * @param url The url to be opened in the browser.
   */
  private void openURL(String url) {
    String osName = System.getProperty("os.name");
    try {
      if (osName.startsWith("Mac OS")) {
        Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
        openURL.invoke(null, new Object[] { url });
      } else if (osName.startsWith("Windows"))
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      else { // assume Unix or Linux
        boolean found = false;
        for (String browser : browsers)
          if (!found) {
            found = Runtime.getRuntime().exec(new String[] { "which", browser }).waitFor() == 0;
            if (found)
              Runtime.getRuntime().exec(new String[] { browser, url });
          }
        if (!found)
          throw new Exception(Arrays.toString(browsers));
      }
    } catch (Exception e) {
      System.out.println("Error attempting to launch web browser\n" + e.toString());
    }
  }
}