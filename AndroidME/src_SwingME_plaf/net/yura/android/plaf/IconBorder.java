package net.yura.android.plaf;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;

public class IconBorder implements Border {

    private Icon icon1,icon2;

    public IconBorder(Icon a1, Icon a2) {
        icon1 = a1;
        icon2 = a2;
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {

        icon1.paintIcon(c, g, width, (height - icon1.getIconHeight()) / 2);

        if (icon2!=null) {
            icon2.paintIcon(c, g, width +(icon1.getIconWidth() - icon2.getIconWidth()) / 2 , (height - icon2.getIconHeight()) / 2);
        }
    }

    public int getTop() {
        return 0;
    }

    public int getBottom() {
        return 0;
    }

    public int getRight() {
        return icon1.getIconWidth();
    }

    public int getLeft() {
        return 0;
    }

    public boolean isBorderOpaque() {
        return false;
    }

}
