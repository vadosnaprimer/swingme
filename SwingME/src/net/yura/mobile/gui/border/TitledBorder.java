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

import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 * @see javax.swing.border.TitledBorder
 */
public class TitledBorder implements Border {

    private static final int LEFT_OFFSET=10;

    protected String title;
    protected Border border;
    protected Font   titleFont;
    protected int  titleColor = 0xFF000000; // default black

    /**
     * @see javax.swing.border.TitledBorder#TitledBorder(javax.swing.border.Border, java.lang.String) TitledBorder.TitledBorder
     */
    public TitledBorder(Border border, String title,Font f) {
        this.border = border;
        if (this.border==null) { this.border = Component.empty; }
        titleFont = f;
        this.title = title;
    }

    /**
     * @see javax.swing.border.TitledBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) TitledBorder.paintBorder
     */
    public void paintBorder(Component c, Graphics2D g, int width, int height) {
        int topOffset = (getTop() - border.getTop()) /2;
        int leftOffset = getLeft()+LEFT_OFFSET;
        g.translate(0, -topOffset);

        int[] clip = g.getClip();
        // paint strip left of text
        int fullHeight = height+topOffset+border.getTop()+border.getBottom();
        g.clipRect(-getLeft(), -border.getTop(), leftOffset, fullHeight);
        border.paintBorder(c, g, width, height+topOffset);
        g.setClip(clip);

        // paint strip right of text
        int textWidth = titleFont.getWidth(title)+2;
        g.clipRect(textWidth+LEFT_OFFSET, -border.getTop(), width-LEFT_OFFSET-textWidth+border.getRight(), fullHeight);
        border.paintBorder(c, g, width, height+topOffset);
        g.setClip(clip);

        // paint strip below text
        g.clipRect(LEFT_OFFSET, topOffset, textWidth, height+border.getBottom());
        border.paintBorder(c, g, width, height+topOffset);
        g.setClip(clip);
        g.translate(0, topOffset);

        g.setFont(titleFont);
        g.setColor(titleColor);
        g.drawString(title, LEFT_OFFSET+1, -getTop() + (getTop() - titleFont.getHeight())/2 );

    }

    public int getTop() {
        return Math.max(border.getTop(), titleFont.getHeight());
    }

    public int getBottom() {
        return border.getBottom();
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
