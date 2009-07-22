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
 * @see javax.swing.border.BevelBorder
 */
public class BevelBorder implements Border {

	protected int thickness,highlight,shadow;
       
        /**
         * @see javax.swing.border.BevelBorder#BevelBorder(int, int, int) BevelBorder.BevelBorder
         */
	public BevelBorder(int thickness, int highlight, int shadow) {
		
		this.thickness = thickness;
		this.highlight = highlight;
		this.shadow = shadow;
		
	}
	
	/**
         * @return The size of the bottom
         * @see javax.swing.border.BevelBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getBottom() {
		return thickness;
	}

	/**
         * @return The size of the left
         * @see javax.swing.border.BevelBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getLeft() {
		return thickness;
	}

	/**
         * @return The size of the right
         * @see javax.swing.border.BevelBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getRight() {
		return thickness;
	}

	/**
         * @return The size of the top
         * @see javax.swing.border.BevelBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getTop() {
		return thickness;
	}

        /**
         * @param comp
         * @param g
         * @param width
         * @param height
         * @see javax.swing.border.BevelBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) EmptyBorder.paintBorder
         */
	public void paintBorder(Component comp, Graphics2D g, int width,int height) {

            for (int c=0;c<thickness;c++) {

                g.setColor(highlight);
                g.drawLine(0-c, -1-c, width+c, -1-c); // top
                g.drawLine(-1-c, -1-c, -1-c, height+c); //left


                g.setColor(shadow);
                g.drawLine(-1-c, height+c, width+c, height+c); //bottom
                g.drawLine(width+c, 0-c, width+c, height+c); // right

            }

        }

        public boolean isBorderOpaque() {
            return false;
        }

}
