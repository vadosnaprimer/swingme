package javax.microedition.lcdui;

/**
 * @author Stefan Haustein
 *
 * @ME4SE INTERNAL
 *  */

import org.me4se.scm.*;

import javax.microedition.midlet.*;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

class ScmDisplayable extends ScmContainer {

  static String[] TYPES = { "null", "SCREEN", "BACK", "CANCEL", "OK", "HELP", "STOP", "EXIT", "ITEM" };

  static final Command OPTIONS_COMMAND = new Command(ApplicationManager.getInstance().getProperty(
      "options.command.label", "Menu"), Command.SCREEN, 0);

  /**
   * Note: Different from List.SELECT_COMMAND, this is used if a select command
   * is required; *may* trigger a list select command
   */

  static final Command ITEM_SELECT_COMMAND = new Command("Select", Command.OK, 0);
  // static final Command GAME_COMMAND1 = new Command("Action1", Command.SCREEN,
  // 0);
  // static final Command GAME_COMMAND2 = new Command("Action2", Command.SCREEN,
  // 0);

  /** The displayable represented by this container */

  Displayable displayable;

  ScmSoftButton[] softButtons;
  Hashtable buttons = new Hashtable();
  ApplicationManager manager;

  /**
   * The title component. The title is not contained in main, main contains only
   * the scrollable components.
   */

  ScmDeviceLabel title;

  /** The ticker component */

  ScmDeviceLabel ticker;

  /** The scrollable main area of the displayable object */

  ScmComponent main;

  /*
   * If this flag is set to true, the full screen area is used for the main
   * component
   */

  int best;

  /**
   * This component makes sure that the size of main is allways correct,
   * regardles of the "displayed" status.
   */

  ScmDisplayable(Displayable displayable) {

    manager = ApplicationManager.getInstance();

    this.displayable = displayable;

    setWidth(manager.screenWidth);
    setHeight(manager.screenHeight);

    init();
  }

  public void init() {

    fullScreen = (displayable instanceof Canvas) && ((Canvas) displayable)._fullScreenMode;

    for (int i = getComponentCount() - 1; i >= 0; i--) {
      remove(i);
    }

    if (!fullScreen) {
      this.title = displayable.titleComponent;
      this.ticker = displayable.tickerComponent;

      add(title);

      if (title.location == null) {
        title.setHeight(title.getMinimumSize().height);
        title.setWidth(getWidth());
      }

      if (ticker.location != null) {
        add(ticker);
      }

      if (displayable instanceof Screen) {
        Screen scr = (Screen) displayable;

        if (scr.iconUp != null)
          add(scr.iconUp);
        if (scr.iconDown != null)
          add(scr.iconDown);
      }
    }

    int cnt;
    if (manager.getProperty("skin") == null)
      cnt = 2;
    else {
      cnt = 0;
      while (manager.getProperty("softbutton." + (cnt)) != null) {
        cnt++;
      }
    }

    softButtons = new ScmSoftButton[cnt];

    for (int i = 0; i < softButtons.length; i++) {
      softButtons[i] = new ScmSoftButton(this, i);
      if (!fullScreen)
        add(softButtons[i]);
    }

  }

  public void paint(java.awt.Graphics g) {

    Vector toRun = null;

    if (displayable.display != null && displayable.display.callSerially.size() > 0) {
      toRun = displayable.display.callSerially;
      displayable.display.callSerially = new Vector();
    }

    super.paint(g);

    if (manager.timeoutImage != null) {
      g.drawImage(manager.timeoutImage, (getWidth() - manager.timeoutImage.getWidth(null)) / 2,
          (getHeight() - manager.timeoutImage.getHeight(null)) / 2, null);
    }

    if (toRun != null)
      for (int i = 0; i < toRun.size(); i++)
        ((Runnable) toRun.elementAt(i)).run();
  }

  boolean shutdownTrigger;

  public boolean keyRepeated(String key) {
    if (shutdownTrigger) {
      ApplicationManager.getInstance().active.notifyDestroyed();
      return true;
    }
    return super.keyRepeated(key);

  }

  public boolean keyPressed(String key) {

    shutdownTrigger = "POWER".equals(key);

    String timeout = manager.getProperty("me4se.timeout");
    if (timeout != null && !timeout.equals("")) {
      if (manager.firstKeyPress == 0) {
        manager.firstKeyPress = System.currentTimeMillis();
      } else if (manager.firstKeyPress + 1000L * Integer.parseInt(timeout) < System.currentTimeMillis()) {

        if (manager.timeoutImage == null) {
          try {
            manager.timeoutImage = manager.getImage(manager.getProperty("me4se.timeout-image", "/timeout.png"));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        return true;
      }
    }

    // System.out.println("Key pressed: "+ key);

    if (key.startsWith("SOFT") && key.length() == 5) {

      int nr = key.charAt(4) - '1';
      // System.out.println(
      // "Function key F"
      // + (code - KeyEvent.VK_F1 + 1)
      // + " intercepted!");

      if (nr < softButtons.length) {
        ScmSoftButton bi = softButtons[nr];

        if (bi.getParent() != null) {
          bi.action();

          // displayable.handleCommand(bi.command, bi.item);
          return true;
        }
      }
    }
    return super.keyPressed(key);
  }

  void setMain(ScmComponent main, boolean scrollable) {

    if (scrollable) {
      ScmScrollPane scrollPane = new ScmScrollPane();
      scrollPane.add(main);
      if (ApplicationManager.getInstance().getFlag("scrollbar")) {
        ScmScrollBar sb = new ScmScrollBar();
        sb.setBackground(javax.microedition.midlet.ApplicationManager.getInstance().bgColor);
        scrollPane.setVerticalBar(sb);
      }
      main = scrollPane;
    }

    if (this.main != null)
      remove(this.main);

    this.main = main;
    add(main);
    doLayout();
  }

  /*
   * void handleSoftButton(int index) {
   * 
   * Command cmd = buttons[index].command;
   */

  protected void updateButtons() {

    // System.out.println("update buttons "+this.displayable+" current item:
    // "+displayable.getCurrentItem());

    Vector commands = displayable.getCommandInfoList();

    buttons = new Hashtable();

    // Assign commands to special buttons (not to soft buttons!)
    // let them remain in the list if the label is not empty

    for (int i = 0; i < commands.size(); i++) {
      CmdInfo ci = (CmdInfo) commands.elementAt(i);
      Command cmd = ci.command;

      String desired[] = ApplicationManager.split(ApplicationManager.getInstance().getProperty(
          "command.keys." + cmd.getTypeName()));

      for (int j = 0; j < desired.length; j++) {
        String dj = desired[j].toUpperCase();

        if (buttons.get(dj) == null && !dj.startsWith("SOFT")) {
          buttons.put(dj, ci);

          if ((cmd.getLabel() == null || cmd.getLabel().equals(""))
              && (cmd.getLongLabel() == null || cmd.getLongLabel().equals(""))) {
            commands.removeElementAt(i);
            i--;
          }
        }
        break;
      }
    }

    // if too many remaining commands,
    // make sure the option menu command is placed first!

    Vector remaining = null;

    for (int pass = 0; pass < 2; pass++) {

      for (int i = 0; i < softButtons.length; i++)
        softButtons[i].setCommand(null, null);

      CmdInfo reinserted = null;
      remaining = new Vector();
      for (int i = 0; i < commands.size(); i++) {
        remaining.addElement(commands.elementAt(i));
      }

      if (pass == 1) {
        remaining.insertElementAt(new CmdInfo(OPTIONS_COMMAND, null), 0);
      }

      for (int i = 0; i < remaining.size(); i++) {
        CmdInfo ci = (CmdInfo) remaining.elementAt(i);
        Command cmd = ci.command;

        String[] desired = ApplicationManager.split(ApplicationManager.getInstance().getProperty(
            cmd == OPTIONS_COMMAND ? "command.menu.activate" : ("command.keys." + cmd.getTypeName())));

        if (cmd == OPTIONS_COMMAND && desired.length == 0) {
          desired = new String[] { "SOFT" + softButtons.length };
        }

        for (int j = 0; j < desired.length; j++) {
          String dj = desired[j].toUpperCase();

          if (dj.startsWith("SOFT")) {
            // System.out.println("trying to map " + cmd.getLabel()
            // + " prio: " + cmd.getPriority() + " to " + dj);
            buttons.put(dj, ci);
            int idx = Integer.parseInt(dj.substring(4)) - 1;

            if (softButtons[idx].command == null) {
              softButtons[idx].setCommand(cmd, ci.item);

              remaining.removeElementAt(i);
              i--;

              // re-insert command if the button is too small to
              // display the label

              if (softButtons[idx].getWidth() < ScmSoftButton.MIN_WIDTH && ci.command.type != Command.CENTERBLOCKER) {
                remaining.addElement(ci);
                reinserted = ci;
              }
            }
          }
          break;
        }
      }

      // assign remaining by priority

      for (int i = 0; i < softButtons.length && remaining.size() > 0; i++) {
        if (softButtons[i].command == null) {
          CmdInfo ci = (CmdInfo) remaining.elementAt(0);
          if (softButtons[i].getWidth() >= ScmSoftButton.MIN_WIDTH) { // duplicate
            // CK CMD
            remaining.removeElementAt(0);
          }
          softButtons[i].setCommand(ci.command, ci.item);
        }
      }

      if (remaining.size() == 0 || (remaining.size() == 1 && remaining.elementAt(0) == reinserted))
        break;
    }

    displayable.commandMenuEntries = remaining;
  }

  public void doLayout() {

    ApplicationManager manager = ApplicationManager.getInstance();

    int x = 0;
    int y = 0;
    int w = manager.screenWidth;
    int h = manager.screenHeight;

    x = manager.getIntProperty("screenPaintableRegion.x", x);
    y = manager.getIntProperty("screenPaintableRegion.y", y);
    w = manager.getIntProperty("screenPaintableRegion.width", w);
    h = manager.getIntProperty("screenPaintableRegion.height", h);

    if (fullScreen) {
      x = manager.getIntProperty("canvasPaintableRegion.x", x);
      y = manager.getIntProperty("canvasPaintableRegion.y", y);
      w = manager.getIntProperty("canvasPaintableRegion.width", w);
      h = manager.getIntProperty("canvasPaintableRegion.height", h);
    }

    if (manager.getProperty("skin") == null && h == manager.screenHeight && softButtons!=null && softButtons.length > 0
        && softButtons[0] != null) {
      h -= softButtons[0].getHeight();
    }

    if (title != null && title.location == null) {
      title.setBounds(x, y, w, title.getHeight());
      y += title.getHeight() + 1;
      h -= y;
    }

    // YURA we want to resize the child to the full available size
    if (main!=null) { // here the main is the ScmCanvas
        x = 0;
        y = 0;
        w = getWidth();
        h = getHeight();

        boolean change = w != main.getWidth() || h != main.getHeight();
        main.setBounds(x, y, w, h);
        main.doLayout();

        if (change && displayable != null) {
          try {
            displayable.sizeChanged(w, h);
          } catch (Exception e) {

          }
        }
    }
    super.doLayout();
  }

}
