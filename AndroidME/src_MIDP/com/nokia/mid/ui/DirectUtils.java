package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;

public class DirectUtils {

    public static DirectGraphics getDirectGraphics(Graphics graphics) {
        return new DirectGraphics(graphics);
    }

}
