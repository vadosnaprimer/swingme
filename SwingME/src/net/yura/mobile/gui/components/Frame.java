package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;

/**
 * @author Yura Mamyrin
 */
public class Frame extends Window {

    private TitleBar title;

    public Frame() {
        title = new TitleBar("", null, true, true, false, true, true);
        add(title,Graphics.TOP);
    }
    public Frame(String name) {
        this();
        title.setTitle(name);
    }

}
