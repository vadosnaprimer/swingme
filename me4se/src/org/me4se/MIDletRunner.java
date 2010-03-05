// ME4SE - A MicroEdition Emulation for J2SE
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: Sebastian Vastag
//
// STATUS:
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

package org.me4se;

import java.applet.Applet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.microedition.midlet.ApplicationManager;

public class MIDletRunner extends Applet {

  private static int startCount;

  /**
   * Implementation of the applet start() method, invoked automatically when the
   * emulator is started as an Applet.
   */

  public void start() {
    System.out.println("Applet start was called.");

    startCount++;

    if (!ApplicationManager.isInitialized()) {
      startMIDlet(null, -1, this, null);
    } else {
      boolean restartable = ApplicationManager.getInstance()
          .getBooleanProperty("me4se.restartable", true);

      System.out
          .println("Calling ApplicationManager.manager.start(); restartable: "
              + restartable + " start count: " + startCount);

      if (restartable || startCount > 2) { // avoid death spiral
        startMIDlet(null, -1, this, null);
      } else {
        String containerUrl = getDocumentBase().toString();
        int cut = containerUrl.indexOf("frld=");
        if (cut == -1) {
          containerUrl += containerUrl.indexOf('?') == -1 ? '?' : '&';
        } else {
          containerUrl = containerUrl.substring(0, cut);
        }

        try {
          getAppletContext().showDocument(
              new URL(containerUrl + "frld="
                  + Long.toString(System.currentTimeMillis(), 36)));
        } catch (MalformedURLException e) {
          e.printStackTrace();
          throw new RuntimeException("" + e);
        }
      }
    }
  }

  /**
   * Implementation of the applet stop() method, invoked automatically when the
   * emulator Applet is stopped.
   */

  public void stop() {

    boolean restartable = ApplicationManager.getInstance().getBooleanProperty(
        "me4se.restartable", true);

    System.out.println("Applet stop() was called. Restartable: " + restartable);

    if (restartable) {
      ApplicationManager.getInstance().pause();
    }
  }

  /**
   * Implementation of the applet destroy() method, invoked automatically when
   * the emulator Applet is destroyed.
   */

  public void destroy() {
    System.out.println("Applet destroy() was called.");
    ApplicationManager.getInstance().destroy(true, true);
  }

  public boolean isFocusTraversable() {
    return false;
  }

  /**
   * Starts the MIDlet with the given class name.
   * 
   * @param className
   *          Class name of the MIDlet to be launched.
   * 
   * @param container
   *          UI-Component where the Emulator shall be embedded. If null, an AWT
   *          Frame will be created automatically.
   * 
   * @param properties
   *          Additional emulator properties, may be null.
   */

  public static void startMIDlet(String className, java.awt.Panel container,
      Properties emulatorProperties) {
    JadFile jad = new JadFile();
    jad.setValue("MIDlet-1", ",," + className);

    startMIDlet(jad, 0, container, emulatorProperties);
  }

  /**
   * Starts the MIDlet or MIDlet suite specified by the given JAD file. Please
   * note that it is not possible to start two or more MIDlets in the same VM.
   * 
   * @param jadFile
   *          MIDlet descriptor file.
   * 
   * @param index
   *          The index (1..number of MIDlets) of the MIDlet to be started. If
   *          the index is <= 0, a MIDlet selector will be shown if necessary.
   * 
   * @param container
   *          UI-Component where the Emulator shall be embedded. If null, an AWT
   *          Frame will be created automatically. To enable headless operation,
   *          the frame will remain invisible until the MIDlet accesses the
   *          display.
   * 
   * @param properties
   *          Additional emulator properties, may be null.
   */

  public static void startMIDlet(JadFile jadFile, int index,
      java.awt.Panel container, Properties emulatorProperies) {
    if (ApplicationManager.isInitialized()) {
      ApplicationManager.getInstance().destroy(true, true);
    }

    ApplicationManager.createInstance(container, emulatorProperies).launch(
        jadFile, index);
  }

  /**
   * This method permits to start the emulator from the command line.
   */
  public static void main(String[] argv) {

    Properties param = new Properties();
    JadFile jadFile = null;

    for (int i = 0; i < argv.length; i++) {

      if (argv[i].startsWith("-")) {
        param.put(argv[i].substring(1), argv[i + 1]);
        i++;
      } else {
        int p = argv[i].indexOf(".jad");
        if (p != -1 && p == argv[i].length() - 4) {
          param.put("jad", argv[i]);
        } else {
          jadFile = new JadFile();
          jadFile.setValue("MIDlet-1", ",," + argv[i]);
        }
      }
    }

    Enumeration keys = param.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      if (key.startsWith("JAD:")) {
        jadFile.setValue(key.substring(4), (String) param.get(key));
      }

    }

    startMIDlet(jadFile, 0, null, param);
  }
}