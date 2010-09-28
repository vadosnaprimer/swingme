package net.yura.android.plaf;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Component;

public class FadeIcon extends Icon {

    private int color;

    public FadeIcon(int color,int size) {
        this.color = color;
        width = size;
        height = size;
    }

    @Override
    public void paintIcon(Component c, Graphics2D g, int x, int y) {

        g.setColor(color);
        g.fillRect(x, y, width, height);

    }

}
