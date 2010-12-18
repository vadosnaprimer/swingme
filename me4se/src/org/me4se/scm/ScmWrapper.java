/* Copyright (c) 2002,2003,2004 Stefan Haustein, Oberhausen, Rhld., Germany
 */

package org.me4se.scm;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.System;
import org.me4se.impl.Log;

/**
 * A buffered wrapper for including scm components in an AWT container
 */

public class ScmWrapper extends Canvas implements MouseMotionListener,
    MouseListener, ComponentListener, KeyListener,MouseWheelListener, FocusListener {

  public ScmComponent component;

  boolean invalid;

  // @MH
  int pressedX = -1;

  int pressedY = -1;

  float scale = 1;

  /** Currently pressed key, used for keyRepeated events */
  String pressing;

  public int paintCount;

  // static only for test!!!
  public BufferedImage offScreenCache;

  // Graphics offScreenGraphics;

  class Helper extends ScmContainer {
    public void repaint(int x, int y, int w, int h) {
      Log.log(Log.DRAW_EVENTS, "sending repaint request to AWT: " + x + "," + y
          + "," + w + "," + h);
      ScmWrapper.this.repaint((int) (x * scale), (int) (y * scale),
          (int) (w * scale), (int) (h * scale));
      Log.log(Log.DRAW_EVENTS, "repaint request sent to AWT");

    }

    public Graphics getGraphics() {
      Graphics2D g = (Graphics2D) getOffScreen().getGraphics();
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
          RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_OFF);
      g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_SPEED);
      g.setRenderingHint(RenderingHints.KEY_DITHERING,
          RenderingHints.VALUE_DITHER_DISABLE);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_SPEED);
      return g;
    }

    public void invalidate() {
      if (!invalid) {
        // System.out.println("repaint scheduled!");
        invalid = true;
        // coordinates neccessary, otherwise macos assumes old site :-(
        ScmWrapper.this.repaint(0, 0, ScmWrapper.this.getSize().width,
            ScmWrapper.this.getSize().height);
      }
    }
  }

  Helper helper = new Helper();

  // Image offScreen;

  ApplicationManager manager;

  public ScmWrapper(ApplicationManager manager) {
    this(manager, 1);
  }

  public ScmWrapper(ApplicationManager manager, float scale) {
    this.scale = scale;
    this.manager = manager;
    addMouseListener(this);
    addMouseMotionListener(this);
    addComponentListener(this);
    addKeyListener(this);
    addMouseWheelListener(this);

    // YURA FIX
    setFocusTraversalKeysEnabled(false);
    addFocusListener(this);
  }

    public void focusGained(FocusEvent e) {
        pressing=null;
        Displayable dis = Display.getDisplay(manager.active).getCurrent();
        if (dis instanceof javax.microedition.lcdui.Canvas) {
            ((javax.microedition.lcdui.Canvas)dis)._showNotify();
        }
    }

    /**
     * YURA to stop keys sticking, and maybe to unfocus components in case of many Canvases
     */
    public void focusLost(FocusEvent e) {
        pressing=null;
        // this is not really correct use of this method
        // but is needed to reset any keys that may have been pressed at the time
        // when the panel loses focus, maybe should use pauseApp() in MIDlet
        Displayable dis = Display.getDisplay(manager.active).getCurrent();
        if (dis instanceof javax.microedition.lcdui.Canvas) {
            ((javax.microedition.lcdui.Canvas)dis)._hideNotify();
        }
    }

  public void componentResized(ComponentEvent ev) {
    invalid = false;
    Dimension d = getSize();
    component.setBounds(0, 0, (int) (d.width / scale), (int) (d.height / scale));
    
    //System.out.println( "resized: " + component.getWidth() + ", " + component.getHeight() + " invalid: " + invalid);

  }

  public void componentMoved(ComponentEvent ev) {
  }

  public void componentHidden(ComponentEvent ev) {
  }

  public void componentShown(ComponentEvent ev) {
  }

  public void setComponent(ScmComponent component) {
    if (this.component != null)
      helper.remove(this.component);

    if (component.parent != null)
      throw new RuntimeException("component already assigned");

    this.component = component;
    helper.add(component);
    repaint();
  }

  public void update(Graphics g) {
    paint(g);
  }

  public BufferedImage getOffScreen() {
    Dimension size = getSize();
    if (size.width <= 0 || size.height <= 0 || size.width > 1000000000
        || size.height > 100000000) {
      size = new Dimension(320, 640);
    }

    // System.out.println("Size: "+size);

    if (offScreenCache == null || offScreenCache.getWidth(this) != size.width
        || offScreenCache.getHeight(this) != size.height) {

      offScreenCache = new BufferedImage(size.width, size.height,
          BufferedImage.TYPE_INT_RGB);
      /*
       * createImage( (int) (size.width / scale), (int) (size.height / scale));
       */
    }
    return offScreenCache;
  }

  public void paint(java.awt.Graphics g) {
    Log.log(Log.DRAW_EVENTS, "AWT paint entered: " + g.getClipRect());
    if (invalid) {
      component.doLayout();
      invalid = false;
    }

    Dimension size = getSize();
    if (size.width <= 0 || size.height <= 0)
      return;

    BufferedImage offScreen = getOffScreen();

    Graphics offScreenGraphics = offScreen.getGraphics();

    offScreenGraphics.setClip(g.getClip());



    offScreenGraphics.setColor(Color.black);

    helper.paint(offScreenGraphics);

    paintCount++;

    /*
     * offScreenGraphics.setColor(Color.BLUE);
     * 
     * for(int i = 15; i < size.width; i += 30){
     * offScreenGraphics.drawLine((paintCount+i) % size.width, 0, (paintCount+i) %
     * size.width, size.height); }
     * /

    int orig = offScreen.getRGB(0, 0);
    offScreen.setRGB(0, 0, orig ^ 0x0ffffff);
    if (offScreen.getRGB(0, 0) == orig) {
      System.out
          .println("***** Cannot draw to offscreen (Press F12 to reallocate) ****");
      // reallocate here!
    }
     */

    if (!g.drawImage(offScreen, 0, 0, size.width, size.height, 0, 0,
        (int) (size.width / scale), (int) (size.height / scale), this)) {
      System.out.println("DRAWIMAGE WAS RETURNING FALSE!!!");
    }
    // g.drawImage(offScreen, 0, 0, this);

    /*
     * g.setColor(Color.RED);
     * 
     * for(int i = 0; i < size.width; i += 30){ g.drawLine((paintCount+i) %
     * size.width, 0, (paintCount+i) % size.width, size.height); }
     */

    Log.log(Log.DRAW_EVENTS, "AWT paint left");
  }

  public void mouseDragged(MouseEvent ev) {
    component.mouseDragged((int) (ev.getX() / scale),
        (int) (ev.getY() / scale), ev.getModifiers());
  }

  int getMouseButton(InputEvent ev) {

    int modifiers = ev.getModifiers();
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
      return 1;
    if ((modifiers & InputEvent.BUTTON2_MASK) != 0)
      return 2;
    if ((modifiers & InputEvent.BUTTON3_MASK) != 0)
      return 3;

    return 0;
  }

  public void mouseMoved(MouseEvent ev) {
    component.mouseMoved((int) (ev.getX() / scale), (int) (ev.getY() / scale),
        ev.getModifiers());
  }

  // public void mousePressed(MouseEvent ev) {
  // component.mousePressed(getMouseButton(ev), (int)(ev.getX()/scale),
  // (int)(ev.getY()/scale), ev.getModifiers());
  // }

  public void mousePressed(MouseEvent ev) {
    component.mousePressed(getMouseButton(ev), (int) (ev.getX() / scale),
        (int) (ev.getY() / scale), ev.getModifiers());
    pressedX = ev.getX();
    pressedY = ev.getY();

    menu = false;
  }

  public void mouseReleased(MouseEvent ev) {
    component.mouseReleased(getMouseButton(ev), (int) (ev.getX() / scale),
        (int) (ev.getY() / scale), ev.getModifiers());
    // @MH
    int maxdelta = 6;
    if ((pressedX != -1) && (pressedY != -1)) {
      if (((Math.abs(pressedX - ev.getX())) < maxdelta)
          && (((Math.abs(pressedX - ev.getX())) > 0) || (((Math.abs(pressedY
              - ev.getY())) > 0)))
          && ((Math.abs(pressedY - ev.getY())) < maxdelta)) {
        mouseClicked(ev);
      }
    }
    pressedX = -1;
    pressedY = -1;
    // @MH

  }

  public void mouseClicked(MouseEvent ev) {
    component.mouseClicked(getMouseButton(ev), (int) (ev.getX() / scale),
        (int) (ev.getY() / scale), ev.getModifiers(), ev.getClickCount());
  }

  public java.awt.Dimension getPreferredSize() {
    return getMinimumSize();
  }

  public void mouseExited(MouseEvent ev) {
    // System.out.println ("mouse exited!");
    mouseMoved(ev);
  }

  /** Map key code (VK_xxx) to a named button event */

  public void keyPressed(KeyEvent ev) {
    String name = manager.getButtonName(ev);
    //System.out.println("Pressed Button Name: " + name + " event: " + ev);
    if (name != null) {
//      System.out.println("Button Name1: " + name);

      if (name.equals(pressing)) {
        component.keyRepeated(name);
      } else {
        pressing = name;
        component.keyPressed(name);
      }
    }

    if (!keyDown) {
        keyDown = true;
        if (ev.getKeyCode() == KeyEvent.VK_ALT) {
            menu = true;
        }
    }
    if (ev.getKeyCode() != KeyEvent.VK_ALT) {
        menu = false;
    }
  }
  boolean keyDown;
  boolean menu;
  /**
   * Dont do anything here...
   */
  public void keyTyped(KeyEvent ev) {
  }

  public void keyReleased(KeyEvent ev) {
    String name = manager.getButtonName(ev);
    //System.out.println("Released Button Name: " + name + " event: " + ev);
    if (name == null) {
      name = pressing;
    }
    pressing = null;

    if (name != null) {
      component.keyReleased(name);
    }

    // Alt+F4 should still work to close app
    if (menu && ev.getKeyCode() == KeyEvent.VK_ALT) {
        component.keyPressed("MENU");
        component.keyReleased("MENU");
        ev.consume();
    }
    menu = false;
    keyDown = false;
  }

  public void mouseEntered(MouseEvent ev) {
  }

  public java.awt.Dimension getMinimumSize() {
    Dimension d = component.getMinimumSize();
    d.width = (int) (d.width * scale);
    d.height = (int) (d.height * scale);
    return d;
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
	int a = e.getWheelRotation();
	int c = a;
//System.out.println("mouseWheelMoved "+a);
	if (a<0) {
		c++;
		component.keyPressed("UP");
	}
	else {
		c--;
		component.keyPressed("DOWN");
	}
	while (c!=0) {
		if (a<0) {
			c++;
			component.keyRepeated("UP");
		}
		else {
			c--;
			component.keyRepeated("DOWN");
		}
	}
	if (a<0) {
		component.keyReleased("UP");
	}
	else {
		component.keyReleased("DOWN");
	}
  }
}

