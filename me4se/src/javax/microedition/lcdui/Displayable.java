// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
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

package javax.microedition.lcdui;

import java.util.Vector;

import javax.microedition.midlet.ApplicationManager;

import org.me4se.scm.ScmComponent;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public abstract class Displayable {

  CommandListener commandListener;
  private java.util.Vector commands = new Vector();

  Display display;
  ScmDisplayable container;

  ScmDeviceLabel titleComponent = new ScmDeviceLabel("title", null, false);
  ScmDeviceLabel tickerComponent = new ScmDeviceLabel("ticker", null, false);

  /**
   * The title is stored separately from the titleLable because the ticker may
   * alter the titleLabel text.
   */
  String title;
  Ticker ticker;
  Vector commandMenuEntries;

  // set to true if soft buttons shall not be shown on a Canvas
  // boolean hideSoftButtons;

  /** set to true if the current form or *item* requires a select button */
  boolean selectButtonRequired;

  Displayable() {
    Display.check();

    // noSelectButton = ApplicationManager.manager.skin != null &&
    // ApplicationManager.manager.getProperty("button.select") == null;

    // noSelectButton =
    // ApplicationManager.manager.properties.get("command.menu.activate")

  }

  /**
   * notify the Displayable that it is displayed on the screen now
   */
  void _showNotify() {
  }

  /**
   * Overwritten in Form
   * 
   * @ME4SE Internal
   */
  Item getCurrentItem() {
    return null;
  }

  void insertCommand(Vector v, Command cmd, Item item) {

    int i = 0;

    while (i < v.size()
        && ((CmdInfo) v.elementAt(i)).command.getPriority() < cmd.getPriority()) {
      i++;
    }

    v.insertElementAt(new CmdInfo(cmd, item), i);
  }

  /**
   * Returns a Vector of CmdInfo objects for this Displayable and the focused
   * item, sorted by priority.
   * 
   * @ME4SE Internal
   */

  Vector getCommandInfoList() {
    Vector v = new Vector();

    Item item = getCurrentItem();
    if (item != null && item.commands != null) {
      for (int i = 0; i < item.commands.size(); i++) {
        insertCommand(v, (Command) item.commands.elementAt(i), item);
      }
    }

    for (int i = 0; i < commands.size(); i++) {
      insertCommand(v, (Command) commands.elementAt(i), null);
    }

    if (selectButtonRequired
        && ApplicationManager.getInstance().getProperty("command.menu.select",
            "").toUpperCase().startsWith("SOFT")) {
      v.insertElementAt(new CmdInfo(ScmDisplayable.ITEM_SELECT_COMMAND, null),
          0);
    }

    return v;
  }

  /**
   * @API MIDP-1.0
   */
  public synchronized void addCommand(Command cmd) {

    if (cmd == null)
      return;

    if (commands.indexOf(cmd) != -1)
      return;
    for (int i = 0; i < commands.size(); i++) {
      Command curCmd = (Command) commands.elementAt(i);
      if (cmd.getPriority() < curCmd.getPriority()) {
        commands.insertElementAt(cmd, i);
        container.updateButtons();
        return;
      }
    }
    commands.insertElementAt(cmd, commands.size());
    container.updateButtons();
  }

  /**
   * @ME4SE INTERN
   */
  void handleCommand(Command cmd, Item item) {

    try {

      if (cmd == ScmDisplayable.OPTIONS_COMMAND)
        display.setCurrent(new CommandList(this));
      else if (cmd == ScmDisplayable.ITEM_SELECT_COMMAND) {
        ScmComponent f = container.getFocusOwner();
        // f.keyPressed("SELECT");
        // f.keyReleased("SELECT");

        if (f instanceof ScmDeviceLabel) {
          ((ScmDeviceLabel) f).action();
        }
      } else if (cmd != null) {
        if (item != null) {
          if (item.listener != null) {
            item.listener.commandAction(cmd, item);
          }
        } else if (commandListener != null)
          commandListener.commandAction(cmd, this);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @API MIDP-1.0
   */
  public boolean isShown() {
    return display != null && display.current == this;
  }

  /**
   * @API MIDP-1.0
   */
  public void setCommandListener(CommandListener commandListener) {
    this.commandListener = commandListener;
  }

  /**
   * @API MIDP-1.0
   */
  public void removeCommand(Command cmd) {
    try {
      int idx = commands.indexOf(cmd);
      if (idx == -1)
        return;
      commands.removeElementAt(idx);

      container.updateButtons();
    } catch (Exception ex) {
    }
  }

  /**
   * @API MIDP-2.0
   */
  public String getTitle() {
    return title;
  }

  /**
   * @API MIDP-2.0
   */
  public void setTitle(String title) {
    this.title = title;
    titleComponent.setText(title);
    container.invalidate();
  }

  /**
   * @API MIDP-2.0
   */
  public Ticker getTicker() {
    return ticker;
  }

  /**
   * @API MIDP-2.0
   */
  public void setTicker(Ticker ticker) {
    this.ticker = ticker;
  }

  /**
   * @API MIDP-2.0
   */
  public int getWidth() {
    return container.main.getWidth();
  }

  /**
   * @API MIDP-2.0
   */
  public int getHeight() {
    return container.main.getHeight();
  }

  /**
   * @API MIDP-2.0
   */
  protected void sizeChanged(int w, int h) {
  }
}