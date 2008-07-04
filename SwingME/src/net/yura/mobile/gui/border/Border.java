package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.components.Component;
/**
 * @author Yura Mamyrin
 * @see javax.swing.border.Border
 */
public interface Border {

    /**
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param width the width of the painted border
     * @param height the height of the painted border
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) Border.paintBorder
     */
	void paintBorder(Component c, Graphics g,int width,int height);
        /**
         * @see javax.swing.border.Border#getBorderInsets(java.awt.Component) Border.getBorderInsets
         */
	int getTop();
        /**
         * @see javax.swing.border.Border#getBorderInsets(java.awt.Component) Border.getBorderInsets
         */
	int getBottom();
        /**
         * @see javax.swing.border.Border#getBorderInsets(java.awt.Component) Border.getBorderInsets
         */
	int getRight();
        /**
         * @see javax.swing.border.Border#getBorderInsets(java.awt.Component) Border.getBorderInsets
         */
	int getLeft();
        
        //TODO
        // maybe add http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/border/Border.html#isBorderOpaque()
}
