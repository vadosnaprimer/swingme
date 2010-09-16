// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
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

// should we remove the dependency on the application manager?

package javax.microedition.lcdui;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.midlet.ApplicationManager;
import javax.microedition.midlet.MIDlet;

import org.me4se.scm.ScmComponent;
import org.me4se.scm.ScmContainer;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public class Display {

  class Vibrator implements Runnable {

    int remaining;

    public Vibrator(int duration) {
      this.remaining = duration;
    }

    public void run() {
      ScmContainer shake = ApplicationManager.getInstance().displayContainer;
      java.awt.Rectangle r = shake.getBounds();

      try {
        while (remaining > 0) {
          Thread.sleep(100);
          shake.setBounds(r.x + 3, r.y, r.width - 3, r.height);
          Thread.sleep(100);
          shake.setBounds(r.x, r.y, r.width, r.height);
          remaining -= 200;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @API MIDP-2.0
   */
  public static final int LIST_ELEMENT = 1;

  /**
   * @API MIDP-2.0
   */
  public static final int CHOICE_GROUP_ELEMENT = 2;

  /**
   * @API MIDP-2.0
   */
  public static final int ALERT = 3;

  /**
   * @API MIDP-2.0
   */
  public static final int COLOR_BACKGROUND = 0;

  /**
   * @API MIDP-2.0
   */
  public static final int COLOR_FOREGROUND = 1;

  /**
   * @API MIDP-2.0
   */
  public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;

  /**
   * @API MIDP-2.0
   */
  public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;

  /**
   * @API MIDP-2.0
   */
  public static final int COLOR_BORDER = 4;

  /**
   * @API MIDP-2.0
   */
  public static final int COLOR_HIGHLIGHTED_BORDER = 5;

  private static Hashtable midlets = new Hashtable();

  private MIDlet midlet;
  protected Displayable current;
  private static ScmDisplayable currentContainer;
  protected Vector callSerially = new Vector();
  protected TickerThread tickerThread = new TickerThread(this);
  Vibrator vibrator;

  /**
   * @ME4SE INTERNAL
   */
  protected Display(MIDlet midlet) {
    this.midlet = midlet;
    tickerThread.start();
  }

  /**
   * @API MIDP-1.0
   */
  public void callSerially(Runnable r) {
    callSerially.addElement(r);
    if (current != null)
      current.container.repaint();
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNSUPPORTED
   */
  public boolean flashBacklight(int duration) {
    return false;
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNIMPLEMENTED
   */
  public boolean vibrate(int duration) {
    if (vibrator != null) {
      vibrator.remaining = 0;
    }

    vibrator = new Vibrator(duration);
    new Thread(vibrator).start();
    return true;
  }

  /**
   * @API MIDP-1.0
   */
  public static Display getDisplay(MIDlet midlet) {

    Display display = (Display) midlets.get(midlet);
    if (display == null) {
      display = new Display(midlet);
      midlets.put(midlet, display);
    }

    return display;
  }

  /**
   * @API MIDP-1.0
   */
  public synchronized void setCurrent(Displayable d) {

      // hack for yura
      if (d instanceof TextBox)
          return;

    if (d == null)
      return;

    current = d;

    final ApplicationManager manager = ApplicationManager.getInstance();

    if (manager.currentlyShown == d
        || ApplicationManager.getInstance().active != midlet)
      return;

    if (currentContainer != null)
      manager.displayContainer.remove(currentContainer);

    if (manager.currentlyShown instanceof Canvas)
      ((Canvas) manager.currentlyShown).hideNotify();

    d.container.setX(manager.getIntProperty("screen.x", 0));
    d.container.setY(manager.getIntProperty("screen.y", 0));

    if (d instanceof Alert) {
      Alert alert = (Alert) d;
      if (alert.next == null)
        alert.next = current;
    }

    manager.displayContainer.add(d.container);
    d.display = this;
    manager.currentlyShown = d;
    currentContainer = d.container;
    current._showNotify();
    //manager.wrapper.requestFocus(); // yura, removed
    currentContainer.repaint();

    // yura for SwingME to get focus from the start this needs to be in another thread
    new Thread() {
        public void run() {
            try {
                Thread.sleep(20 * 50);
            }
            catch (InterruptedException ex) { }

            manager.wrapper.requestFocusInWindow();
        }
    }.start();

  }

  /**
   * @API MIDP-1.0
   */
  public void setCurrent(Alert alert, Displayable next) {
    alert.next = next;
    setCurrent(alert);
  }

  /**
   * @API MIDP-2.0
   */
  public void setCurrentItem(Item item) {
    setCurrent(item.form);
    ((ScmComponent) item.lines.elementAt(item.lines.size() == 0 ? 0 : 1))
        .requestFocus();
  }

  /**
   * @API MIDP-1.0
   */
  public boolean isColor() {
    return true;
  }

  /**
   * @API MIDP-2.0 TODO: Fix this: read property!
   */
  public int numAlphaLevels() {
    return 256;
  }

  /**
   * @API MIDP-1.0
   */
  public int numColors() {
    return ApplicationManager.getInstance().colorCount;
  }

  /**
   * @API MIDP-1.0
   */
  public Displayable getCurrent() {
    return current;
  }

  /**
   * @API MIDP-2.0
   */
  public int getColor(int type) {
    switch (type) {
    case COLOR_BACKGROUND:
      return ApplicationManager.getInstance().getIntProperty("item.background",
          0x0ffffff);
    case COLOR_FOREGROUND:
      return ApplicationManager.getInstance().getIntProperty("item.foreground",
          0);
    case COLOR_HIGHLIGHTED_BACKGROUND:
      return ApplicationManager.getInstance().getIntProperty(
          "item.focus.background", 0x00000ff);
    case COLOR_HIGHLIGHTED_FOREGROUND:
      return ApplicationManager.getInstance().getIntProperty(
          "item.focus.foreground", 0);
    case COLOR_BORDER:
      return ApplicationManager.getInstance().getIntProperty("item.border",
          0x0ffffff);
    case COLOR_HIGHLIGHTED_BORDER:
      return ApplicationManager.getInstance().getIntProperty(
          "item.focus.border", 0x08888ff);
    }
    return 0x08888ff;
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNSUPPORTED
   */
  public int getBestImageWidth(int imageType) {
    return 0;
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNSUPPORTED
   */
  public int getBestImageHeight(int imageType) {
    System.out.println("Display.getBestImageHeight() with no effect!");
    return 0;
  }

  /**
   * @ME4SE INTERNAL
   */
  protected static void check() {
    if (ApplicationManager.getInstance().frame != null
        && !ApplicationManager.getInstance().frame.isVisible()) {
      java.awt.Frame frame = ApplicationManager.getInstance().frame;
      frame.pack();
      frame.setVisible(true);
      // YURA FIX
      //frame.setResizable(false);
    }
  }
}