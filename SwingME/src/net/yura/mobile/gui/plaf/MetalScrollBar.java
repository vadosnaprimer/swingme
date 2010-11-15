package net.yura.mobile.gui.plaf;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class MetalScrollBar implements Border {

    boolean thumb;
    int background,foreground;
    int thinkness;
    int size;

    public MetalScrollBar(boolean t,int foreground,int background, int size) {
        thumb = t;
        this.background =background;
        this.foreground = foreground;
        this.size = (size/2)*2; // round
    }

    public void paintBorder(Component c, Graphics2D g, int w, int h) {

        int fcolor = foreground==Style.NO_COLOR?c.getCurrentForeground():foreground;
        g.setColor( fcolor );

        int x = -getLeft();
        int y = -getTop();
        int width = w+getLeft()+getRight();
        int height = h+getTop()+getBottom();

        if (thumb) {

            g.setColor( fcolor );
            g.fillRect(x, y+getTop(), width , height-getTop()-getBottom());

        }
        else {

            g.setColor( background );
            g.fillRect(x, y, width, height);
            g.setColor(fcolor);

            int arrowHeight = getTop();

            g.drawRect(x, y, width-1, arrowHeight-1);
            g.drawRect(x, y+height-arrowHeight, width-1, arrowHeight-1);

            int gp = 2; // gap between arrow and sides
            int top = y+((arrowHeight/2)-2);
            int iconWidth = width-(gp*2);
            g.fillTriangle(x+gp+(iconWidth/2)+1, top,
                           x+gp+iconWidth, top+5,
                           x+gp, top+5);

            top = y+height-arrowHeight+((arrowHeight/2)-2);
            g.fillTriangle(x+gp+(iconWidth/2)+1, top+5,
                           x+gp+iconWidth, top,
                           x+gp, top);

            // draw the lines either side
            g.drawLine( x , y+arrowHeight, x, y+height-arrowHeight );
            g.drawLine(x+width-1, y+arrowHeight, x+width-1, y+height-arrowHeight);
        }
    }

    public int getTop() {
        return thumb?1:size;
    }

    public int getBottom() {
        return getTop();
    }

    public int getRight() {
        return size/2 - (thumb?2:0);
    }

    public int getLeft() {
        return getRight();
    }

    public boolean isBorderOpaque() {
        return true;
    }

}
