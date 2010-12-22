package net.yura.blackberry.midlet;

import javax.microedition.lcdui.Display;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;

public abstract class MIDlet extends javax.microedition.midlet.MIDlet implements KeyListener{

    int keyPressed;

    public MIDlet() {
        // Register RIM key listener
        Application.getApplication().addKeyListener(this);
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
        //#debug debug
        System.out.println(">>>> keyDown");

        // We only handle escape/menu button here, and wait for the key-up.
        // We can receive key up's without a key down, and we will ignore that.
        keyPressed = Keypad.key(keycode);
        return (keyPressed == Keypad.KEY_ESCAPE ||
                keyPressed == Keypad.KEY_MENU);
    }

    public boolean keyUp(int keycode, int time) {
        //#debug debug
        System.out.println(">>>> keyUp");

        int key = Keypad.key(keycode);

        // Ignore key up's without a key down.
        if (keyPressed == key) {
            keyPressed = 0;
            if (key == Keypad.KEY_ESCAPE) {
                processKey(KeyEvent.KEY_END);
                return true;
            }
            if(key == Keypad.KEY_MENU) {
                processKey(KeyEvent.KEY_MENU);
                return true;
            }
        }

        return false;
    }

    void processKey(int key) {
        try {
            //#debug debug
            System.out.println(">>>> processKey key = " + key );

            // TODO, when some thing like TextBox is open this will breaks
            Object canvas = Display.getDisplay(this).getCurrent();

            if (canvas instanceof DesktopPane) {
	            //#debug debug
	            System.out.println(">>>> Found a DesktopPane to fw event");
	
	            ((DesktopPane)canvas).keyPressed(key);
	            ((DesktopPane)canvas).keyReleased(key);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
