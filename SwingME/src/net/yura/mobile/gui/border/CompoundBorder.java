/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 * @see javax.swing.border.CompoundBorder
 */
public class CompoundBorder implements Border {

    private Border outsideBorder;
    private Border insideBorder;
    
    /**
     * @param outside the outside border
     * @param inside the inside border to be nested
     * @see javax.swing.border.CompoundBorder#CompoundBorder(javax.swing.border.Border, javax.swing.border.Border) CompoundBorder.CompoundBorder
     */
    public CompoundBorder(Border outside, Border inside) {
        outsideBorder = outside;
        insideBorder = inside;
    }

    /**
     * @return the inside border object
     * @see javax.swing.border.CompoundBorder#getInsideBorder() CompoundBorder.getInsideBorder
     */
    public Border getInsideBorder() {
        return insideBorder;
    }

    /**
     * @return the outside border object
     * @see javax.swing.border.CompoundBorder#getOutsideBorder() CompoundBorder.getOutsideBorder
     */
    public Border getOutsideBorder() {
        return outsideBorder;
    }
    
    public void setInsideBorder(Border insideBorder) {
        this.insideBorder = insideBorder;
    }

    public void setOutsideBorder(Border outsideBorder) {
        this.outsideBorder = outsideBorder;
    }

    /**
     * @param c
     * @param g
     * @param width
     * @param height
     * @see javax.swing.border.CompoundBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) CompoundBorder.paintBorder
     */
    public void paintBorder(Component c, Graphics g, int width, int height) {
        
        g.translate(-insideBorder.getLeft(), -insideBorder.getTop());
        outsideBorder.paintBorder(c,g,width+insideBorder.getLeft()+insideBorder.getRight(),height+insideBorder.getTop()+insideBorder.getBottom());
        g.translate(insideBorder.getLeft(), insideBorder.getTop());
        
        insideBorder.paintBorder(c, g, width, height);
    }

    /**
     * @return the top of the composite border by adding the top of the outside border to the top of the inside border
     * @see javax.swing.border.CompoundBorder#getBorderInsets(java.awt.Component) CompoundBorder.getBorderInsets
     */
    public int getTop() {
        return outsideBorder.getTop() + insideBorder.getTop();
    }
    /**
     * @return the bottom of the composite border by adding the bottom of the outside border to the bottom of the inside border
     * @see javax.swing.border.CompoundBorder#getBorderInsets(java.awt.Component) CompoundBorder.getBorderInsets
     */
    public int getBottom() {
        return outsideBorder.getBottom() + insideBorder.getBottom();
    }
    /**
     * @return the right of the composite border by adding the right of the outside border to the right of the inside border
     * @see javax.swing.border.CompoundBorder#getBorderInsets(java.awt.Component) CompoundBorder.getBorderInsets
     */
    public int getRight() {
        return outsideBorder.getRight() + insideBorder.getRight();
    }
    /**
     * @return the left of the composite border by adding the left of the outside border to the left of the inside border
     * @see javax.swing.border.CompoundBorder#getBorderInsets(java.awt.Component) CompoundBorder.getBorderInsets
     */
    public int getLeft() {
        return outsideBorder.getLeft() + insideBorder.getLeft();
    }
    
    public boolean isBorderOpaque() {
        return outsideBorder.isBorderOpaque() || insideBorder.isBorderOpaque();
    }

}
