package net.yura.mobile.logging;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.layout.FlowLayout;

/**
 *
 * @author Orens
 *
 */
public class DesktopLogger extends Logger {

  private final int level;
  private Frame debugwindow;
  private TextArea text;

  public DesktopLogger(int desktopLevel) {
    this.level = desktopLevel;
    //#debug fatal
    setLevel(FATAL);
    //#debug error
    setLevel(ERROR);
    //#debug warn
    setLevel(WARN);
    //#debug info
    setLevel(INFO);
    //#debug debug
    setLevel(DEBUG);
  }

  public void init() {
    debugwindow = new Frame("Debug");
    debugwindow.setName("Dialog");
    text = new TextArea();
    text.setFocusable(false);
    text.setLineWrap(true);
    //MenuBar menubar = new MenuBar();
    Button close = new Button("Close");
    close.setActionCommand(Frame.CMD_CLOSE);
    // hack to avoid having to make a new action listoner
    close.addActionListener(debugwindow.getTitleBar());
    close.setMnemonic(KeyEvent.KEY_SOFTKEY2);
    //menubar.add(close);
    //desktop.debugwindow.setMenuBar(menubar);
    Panel p = new Panel(new FlowLayout());
    p.add(close);
    // This is not needed, but just in case something
    // has gone wrong with the theme, we set some defaults
    text.setFont(Font.getDefaultSystemFont());
    text.setForeground(0x00000000);
    text.setBackground(0x00FFFFFF);
    //desktop.debugwindow.setBackground(0x00FFFFFF);
    debugwindow.getContentPane().add(new ScrollPane(text));
    debugwindow.getContentPane().add(p, Graphics.BOTTOM);
    debugwindow.setBounds(10, 10, DesktopPane.getDesktopPane().getWidth() - 20, DesktopPane.getDesktopPane().getHeight() / 2);
  }

  protected synchronized void log(String message, int level) {
    super.log(message, level);
    try {
        if (level >= this.level) {
          if (debugwindow == null) {
            init();
          }
          text.append(toString(level) + message + "\n");
          if (!debugwindow.isVisible()) {
            debugwindow.setVisible(true);
          }
          else {
            debugwindow.repaint();
          }
        }
    }
    catch(Throwable th) {
        Logger.error(th);
    }
  }
}


