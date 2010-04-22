package net.yura.mobile.gui.plaf.nimbus;

import net.yura.mobile.gui.plaf.Style;

/**
 * @author Nathan
 */
public class NimbusBorderSetting {

    public static final int TOP = 0;
    public static final int LEFT = 1;
    public static final int BOTTOM = 2;
    public static final int RIGHT = 3;

    public int color1;
    public int color2;
    public int[] thickness;
    public int[] corner;
    public double reflection;
    public int gradientOrientation;

    public NimbusBorderSetting(int color1, int color2, int thickness, int[] corner, double reflection) {
        int[] thicknesses = {thickness,thickness,thickness,thickness};
        this.color1 = color1;
        this.color2 = color2;
        this.thickness = thicknesses;
        this.corner = corner;
        this.reflection = reflection;
    }

    public NimbusBorderSetting(int color1, int color2, int thickness, int corner, double reflection) {        
        int[] corners = {corner,corner,corner,corner};
        int[] thicknesses = {thickness,thickness,thickness,thickness};
        this.color1 = color1;
        this.color2 = color2;
        this.thickness = thicknesses;
        this.corner = corners;
        this.reflection = reflection;
    }

    public NimbusBorderSetting(int color1, int color2, int thickness, int corner, double reflection, int orientation) {
        this (color1,color2,thickness,corner,reflection);
        gradientOrientation = orientation;
    }

    public NimbusBorderSetting(int color1, int color2, int[] thickness, int[] corner, double reflection) {
        this.color1 = color1;
        this.color2 = color2;
        this.thickness = thickness;
        this.corner = corner;
        this.reflection = reflection;
    }

    public NimbusBorderSetting(int color1, int color2, int[] thickness, int[] corner, double reflection, int orientation) {
        this(color1,color2,thickness,corner,reflection);
        gradientOrientation = orientation;
    }

    public NimbusBorderSetting(int color1, int color2, int[] thickness, int corner, double reflection) {
        int[] corners = {corner,corner,corner,corner};
        this.color1 = color1;
        this.color2 = color2;
        this.thickness = thickness;
        this.corner = corners;
        this.reflection = reflection;
    }

    NimbusBorderSetting() {
        color1 = Style.NO_COLOR;
        color2 = Style.NO_COLOR;
        int[] thicknesses = {0,0,0,0};
        int[] corners = {0,0,0,0};
        thickness = thicknesses;
        corner = corners;
    }

}
