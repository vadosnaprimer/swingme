package org.me4se.impl.lcdui;

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public abstract class PhysicalFont {

    public int height;
    public int ascent;
    public int descent;
    public int leading;

    public int stringWidth (String s) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            sum += charWidth (s.charAt (i));
        }
        return sum;
    }

    public abstract int charWidth (char c);

    public int charsWidth (char []c, int start, int len) {
        int sum = 0;
        for (int i = start; i < start+len; i++) {
            sum += charWidth (c[i]);
        }
        return sum;
    }

    protected abstract void drawString(java.awt.Graphics g, String s, int x, int y);
}
