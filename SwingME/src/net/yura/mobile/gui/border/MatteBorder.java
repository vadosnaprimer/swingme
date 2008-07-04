package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 * @see javax.swing.border.MatteBorder
 */
public class MatteBorder extends EmptyBorder {

    private int color;
    
    /**
     * @param top the top inset of the border
     * @param left the left inset of the border
     * @param bottom the bottom inset of the border
     * @param right the right inset of the border
     * @param color the color rendered for the border
     * @see javax.swing.border.MatteBorder#MatteBorder(int, int, int, int, java.awt.Color) MatteBorder.MatteBorder
     */
    	public MatteBorder(int top, int left, int bottom, int right,int color) {
            super(top,left,bottom,right);
            
            this.color = color;
        }
        /**
         * @param c
         * @param g
         * @param width
         * @param height
         * @see javax.swing.border.MatteBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) MatteBorder.paintBorder
         */
        public void paintBorder(Component c, Graphics g, int width,int height) {
            
                g.setColor(color);
            
            	g.fillRect(-left, -top, width+left+right, top); // top
                
		g.fillRect(-left, height, width+left+right, bottom); // bottom
			
		g.fillRect(-left, 0, left, height ); // left
                
		g.fillRect(width, 0, right, height ); // right
            
        }

    
}


