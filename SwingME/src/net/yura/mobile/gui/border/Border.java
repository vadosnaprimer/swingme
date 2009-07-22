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

import net.yura.mobile.gui.Graphics2D;
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
	void paintBorder(Component c, Graphics2D g,int width,int height);
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

        /**
         * @return Returns whether or not the border is opaque. If the border is opaque, it is responsible for filling in it's own background when painting. 
         * @see javax.swing.border.Border#isBorderOpaque()
         */
        public boolean isBorderOpaque();
}
