package net.yura.android.plaf;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;

public class BorderWithDivider implements Border {

    Border border,divider;
    boolean showDiv;

    public BorderWithDivider(Border border,Border divider,boolean showD) {
        this.border = border;
        this.divider = divider;
        showDiv = showD;
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {

        border.paintBorder(c, g, width, height);

        int x = divider.getLeft() - getLeft();
        int y = height + border.getBottom() + divider.getTop();
        g.translate(x, y);
        divider.paintBorder(c, g, width
                +getLeft()-divider.getLeft()
                +getRight()-divider.getRight()
                , 0);
        g.translate(-x, -y);
    }

    public int getTop() {
        return border.getTop();
    }

    public int getBottom() {
        return border.getBottom()+ (showDiv?divider.getTop()+divider.getBottom():0);
    }

    public int getRight() {
        return border.getRight();
    }

    public int getLeft() {
        return border.getLeft();
    }

    public boolean isBorderOpaque() {
        return border.isBorderOpaque();
    }

}
