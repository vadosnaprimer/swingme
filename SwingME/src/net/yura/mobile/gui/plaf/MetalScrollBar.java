package net.yura.mobile.gui.plaf;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class MetalScrollBar implements Border {

    Icon top,bottom;

    public MetalScrollBar(Icon top,Icon bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public void paintBorder(Component c, Graphics2D g, int w, int h) {
        top.paintIcon(c, g, (w-top.getIconWidth())/2, -getTop());
        bottom.paintIcon(c, g, (w-bottom.getIconWidth())/2, h);
    }

    public int getTop() {
        return top.getIconHeight();
    }

    public int getBottom() {
        return bottom.getIconHeight();
    }

    public int getRight() {
        return 0;
    }

    public int getLeft() {
        return 0;
    }

    public boolean isBorderOpaque() {
        return true;
    }

}
