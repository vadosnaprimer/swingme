package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Component;

/**
 *
 * @author ymamyrin
 */
public class CompoundBorder implements Border {

    private Border outsideBorder;
    private Border insideBorder;
    
    public CompoundBorder(Border outside, Border inside) {
        outsideBorder = outside;
        insideBorder = inside;
    }

    public Border getInsideBorder() {
        return insideBorder;
    }

    public void setInsideBorder(Border insideBorder) {
        this.insideBorder = insideBorder;
    }

    public void setOutsideBorder(Border outsideBorder) {
        this.outsideBorder = outsideBorder;
    }

    public Border getOutsideBorder() {
        return outsideBorder;
    }
    
    public void paintBorder(Component c, Graphics g, int width, int height) {
        
        g.translate(-insideBorder.getLeft(), -insideBorder.getTop());
        outsideBorder.paintBorder(c,g,width+insideBorder.getLeft()+insideBorder.getRight(),height+insideBorder.getTop()+insideBorder.getBottom());
        g.translate(insideBorder.getLeft(), insideBorder.getTop());
        
        insideBorder.paintBorder(c, g, width, height);
    }

    public int getTop() {
        return outsideBorder.getTop() + insideBorder.getTop();
    }

    public int getBottom() {
        return outsideBorder.getBottom() + insideBorder.getBottom();
    }

    public int getRight() {
        return outsideBorder.getRight() + insideBorder.getRight();
    }

    public int getLeft() {
        return outsideBorder.getLeft() + insideBorder.getLeft();
    }

}
