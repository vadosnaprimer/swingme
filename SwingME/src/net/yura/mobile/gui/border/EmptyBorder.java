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
 * @see javax.swing.border.EmptyBorder
 */
public class EmptyBorder implements Border {

	protected int top,bottom,left,right;
	
        /**
         * create a inverted border from an existing border
         * @param b the border
         */
        public EmptyBorder(Border b) {
            this( -b.getTop(),-b.getLeft(),-b.getBottom(),-b.getRight() );
        }
        
        /**
         * @param top the top inset of the border
         * @param left the left inset of the border
         * @param bottom the bottom inset of the border
         * @param right the right inset of the border
         * @see javax.swing.border.EmptyBorder#EmptyBorder(int, int, int, int) EmptyBorder.EmptyBorder
         */
	public EmptyBorder(int top, int left, int bottom, int right) {
		
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		
	}
	
	/**
         * @return The size of the bottom
         * @see javax.swing.border.EmptyBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getBottom() {

		return bottom;
	}

	/**
         * @return The size of the left
         * @see javax.swing.border.EmptyBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getLeft() {

		return left;
	}

	/**
         * @return The size of the right
         * @see javax.swing.border.EmptyBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getRight() {

		return right;
	}

	/**
         * @return The size of the top
         * @see javax.swing.border.EmptyBorder#getBorderInsets(java.awt.Component) EmptyBorder.getBorderInsets
         */
	public int getTop() {

		return top;
	}

        /**
         * @param c
         * @param g
         * @param width
         * @param height
         * @see javax.swing.border.EmptyBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) EmptyBorder.paintBorder
         */
	public void paintBorder(Component c, Graphics g, int width,int height) { }

}
