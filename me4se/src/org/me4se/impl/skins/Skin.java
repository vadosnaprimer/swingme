package org.me4se.impl.skins;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.*;
import java.io.*;
import java.util.*;


import javax.microedition.lcdui.Canvas;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.lcdui.*;
import org.me4se.scm.*;

public class Skin extends ScmContainer {

    private Image defaultImage;
    private Image pressedButtonsImage;
    private Image highlightedImage;
    //Frame frame = new Frame ("ME4SE");
    public Panel panel;

    boolean sized = false;

    private SkinButton pressing = null;
    private SkinButton over = null;
    //Component saveFocus;

    private String skinUrl;

    private static Hashtable keyCodes = new Hashtable();

 
    private static void registerKey(String name, int value) {
        keyCodes.put(name, new Integer(value));
    }

    static {
        registerKey("VK_UP", KeyEvent.VK_UP);
        registerKey("VK_DOWN", KeyEvent.VK_DOWN);
        registerKey("VK_LEFT", KeyEvent.VK_LEFT);
        registerKey("VK_RIGHT", KeyEvent.VK_RIGHT);

        registerKey("VK_HOME", KeyEvent.VK_HOME);
        registerKey("VK_END", KeyEvent.VK_END);
        registerKey("VK_ESCAPE", KeyEvent.VK_ESCAPE);
        registerKey("VK_ENTER", KeyEvent.VK_ENTER);
        registerKey("VK_SPACE", KeyEvent.VK_SPACE);

        registerKey("VK_F1", KeyEvent.VK_F1);
        registerKey("VK_F2", KeyEvent.VK_F2);
        registerKey("VK_F3", KeyEvent.VK_F3);
        registerKey("VK_F4", KeyEvent.VK_F4);
        registerKey("VK_F5", KeyEvent.VK_F5);
        registerKey("VK_F6", KeyEvent.VK_F6);
        registerKey("VK_F7", KeyEvent.VK_F7);
        registerKey("VK_F8", KeyEvent.VK_F8);
        registerKey("VK_F9", KeyEvent.VK_F9);
        registerKey("VK_F10", KeyEvent.VK_F10);
        registerKey("VK_F11", KeyEvent.VK_F11);
        registerKey("VK_F12", KeyEvent.VK_F12);
    }

    class SkinButton extends ScmComponent {

        private int x;
        private int y;
        //private char keyChar = KeyEvent.CHAR_UNDEFINED;
        //private String keyCode = null;
        private String name;

        public SkinButton(String name, int x, int y, int w, int h) {
            this.name = name;
            setBounds(x, y, w, h);
            this.x = x;
            this.y = y;
        }

        public boolean isFocusTraversable() {
            return false;
        }

        public void paint(Graphics g) {
            if (over == this) {
                if (pressing == this)
                    g.drawImage(pressedButtonsImage, -x, -y, null);
                else
                    g.drawImage(highlightedImage, -x, -y, null);
            }
            /*  else
                g.drawImage(defaultImage, -x, -y, null); */
        }

        public boolean mousePressed(int button, int x, int y, int mask) {
            //System.out.println("pressed: " + button);
            if (button != 1)
                return false;

            over = this;
            SkinButton old = pressing;
            pressing = this;
            repaint();
            if (old != null)
                old.repaint();

            ApplicationManager.getInstance().displayContainer.keyPressed(name);
            return true;
        }

        public boolean mouseDragged(int x, int y, int mask) {
            if ((over == this)
                == (x < 0 || x > getWidth() || y < 0 || y > getHeight())) {
                over = over==this? null : this;
                repaint();
                return true;
            }
            else
                return false;
        }

        public boolean mouseReleased(int button, int x, int y, int mask) {
            if (button != 1)
                return false;

            pressing = null;
            getParent().repaint();

            ApplicationManager.getInstance().displayContainer.keyReleased(name);
            return true;
        }

        public void mouseEntered() {
            //ScmComponent oldOver = over;
            
            if (pressing == null) {
                over = this;
              /*  if(oldOver != null){
                    oldOver.repaint();
                }*/
            }
            getParent().repaint();
        }

        public void mouseExited() {
            if(over == this){
                over = null;
            }
            getParent().repaint();
        }
    }

    public int getInt(String name, int dflt) {
        return ApplicationManager.getInstance().getIntProperty(name, dflt);
    }

    public String getName(String name) {
        return ApplicationManager.concatPath(skinUrl, name);
    }

    public void paint(Graphics g) {
        g.drawImage(defaultImage, 0, 0, null);
        super.paint(g);
    }

    public Dimension getMinimumSize() {
        return new Dimension(
            defaultImage.getWidth(null),
            defaultImage.getHeight(null));
    }

    public Skin() {
        ApplicationManager manager = ApplicationManager.getInstance();
        //		setBackground(Color.white);
        skinUrl = manager.getProperty("skin");

        try {
            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(manager.openInputStream(skinUrl)));

            while (true) {
                String s = reader.readLine();
                if (s == null)
                    break;
                if (s.startsWith(":"))
                    continue;

                int cut = s.indexOf('=');
                if (cut == -1)
                    cut = s.indexOf(':');

                if (cut == -1)
                    continue;

                String key = s.substring(0, cut).trim().toLowerCase();
                String value = s.substring(cut + 1).trim();

                try {

                    if (key.equals("default_image"))
                        defaultImage = manager.getImage(getName(value));
                    else if (key.equals("pressed_buttons_image"))
                        pressedButtonsImage = manager.getImage(getName(value));
                    else if (key.equals("highlighted_image"))
                        highlightedImage = manager.getImage(getName(value));
/*
                    else if (key.startsWith("custom."))
                        manager.custom.put(key.substring(7), value);
                    else if (key.startsWith("icon.")) {
                        String name = key.substring(5);
                        if (name.indexOf('.') != -1)
                            manager.icons.put(
                                name,
                                manager.getImage(getName(value)));
                    }*/
                    else{
                        manager.properties.put(key, value);
                        if(key.startsWith("microedition.")){
                            if(manager.applet == null){
                                java.lang.System.setProperty(key, value);
                            }
                            else {
                                org.me4se.System.properties.setProperty(key, value);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.err.println(
                        "exception " + e + " in property line: " + s);
                    //e.printStackTrace();
                }
            }
            //setSize (

//            manager.waitForImage(defaultImage, "skin");
            //add (new ScmWrapper (skinContainer), BorderLayout.CENTER);

            for (Enumeration e = manager.properties.keys();
                e.hasMoreElements();
                ) {
                String key = (String) e.nextElement();
                String value = (String) manager.getProperty(key);

                try {

                    if (key.startsWith("font.")
                        && value.endsWith(".properties")) {
                        String type = key.substring(5);
                        if (FontInfo.physicalCache.get(value) == null)
                            FontInfo.physicalCache.put(
                                value,
                                new BitmapFont(this, value));

                    }
                    else if (key.startsWith("button.")) {
                        int cut0 = value.indexOf(',');
                        int cut1 = value.indexOf(',', cut0 + 1);
                        int cut2 = value.indexOf(',', cut1 + 1);
                        int cut3 = value.indexOf(',', cut2 + 1);
                        if (cut3 == -1)
                            cut3 = value.length();

                        add(
                            new SkinButton(
                                key.substring(7).toUpperCase(),
                                Integer.parseInt(
                                    value.substring(0, cut0).trim()),
                                Integer.parseInt(
                                    value.substring(cut0 + 1, cut1).trim()),
                                Integer.parseInt(
                                    value.substring(cut1 + 1, cut2).trim()),
                                Integer.parseInt(
                                    value.substring(cut2 + 1, cut3).trim())));
                    }
                    //    else if (key.startsWith ("icon.")) 
                    
                    else if(key.startsWith("key.")){
                    	String buttonName = key.substring(4).toUpperCase();
                    	String[] vCodes = ApplicationManager.split(value);
                    	
                    	for(int i = 0; i < vCodes.length; i++){
                    		Integer ib = (Integer) keyCodes.get(vCodes[i]);
                    		if(ib != null){
                    			manager.virtualKeyMap.put(ib, buttonName);
                    		}
                    	}
                    }
                    else if(key.startsWith("game.")){
                    	String f = key.substring(5).toUpperCase();
                    	int gameCode;
                    	if(f.equals("UP")){
                    		gameCode = Canvas.UP;
                    	}
                    	else if(f.equals("DOWN")){
                    		gameCode = Canvas.DOWN;
                    	}
                    	else if(f.equals("LEFT")){
                    		gameCode = Canvas.LEFT;
                    	}
                    	else if(f.equals("RIGHT")){
                    		gameCode = Canvas.RIGHT;
                    	}
                    	else if(f.equals("FIRE") || f.equals("SELECT")){
                    		gameCode = Canvas.FIRE;
                    	}
                    	else {
                    		continue;
                    	}

                    	String[] buttons = ApplicationManager.split(value);
                    	Integer gc = new Integer(gameCode);
                    	
						for(int i = 0; i < buttons.length; i++) {
							manager.gameActions.put(buttons[i], gc);
						}
                    }
                    else if(key.startsWith("keycode.")){
                    	String buttonName = key.substring(8);
                    	Integer keyCode = new Integer(value);
                    	
                    	manager.keyCodeToButtonName.put(keyCode, buttonName);
                    }
                    	
                }
                catch (Exception x) {
                    System.err.println(
                        "exception "
                            + x
                            + " evaluating property line: "
                            + key
                            + "="
                            + value);
                    //x.printStackTrace();
                }
            }

            manager.bgColor = (new Color(getInt("screenBGColor", 0x0ffffff)));
            manager.isColor =
                (manager.properties.getProperty("iscolor", "true")
                    .equals("true"));
            manager.colorCount = (getInt("colorcount", 256*256*256));
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}