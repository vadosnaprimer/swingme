package org.me4se.impl.lcdui;

import java.awt.image.RGBImageFilter;

import javax.microedition.midlet.ApplicationManager;

public class Color2GrayFilter extends RGBImageFilter {

    public static Color2GrayFilter instance = new Color2GrayFilter();

    public int filterRGB(int x, int y, int rgb) {
        //return rgb & 0x0ffffff;
        return ApplicationManager.getInstance().getDeviceColor(rgb);
    }
}
