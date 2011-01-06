package net.yura.mobile.gui.plaf;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class MetalScrollBar implements Border {

    Icon left,right;

    public MetalScrollBar(Icon left,Icon right) {
        this.left = left;
        this.right = right;
    }

    public void paintBorder(Component c, Graphics2D g, int w, int h) {
        left.paintIcon(c, g, -getLeft(),(h-left.getIconHeight())/2);
        right.paintIcon(c, g, w, (h-right.getIconHeight())/2);
    }

    public int getTop() {
        return 0;
    }

    public int getBottom() {
        return 0;
    }

    public int getRight() {
        return right.getIconWidth();
    }

    public int getLeft() {
        return left.getIconWidth();
    }

    public boolean isBorderOpaque() {
        return true;
    }

}
