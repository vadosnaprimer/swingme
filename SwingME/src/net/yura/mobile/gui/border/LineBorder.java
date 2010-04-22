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
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.Style;
/**
 * @author Yura Mamyrin
 * @see javax.swing.border.LineBorder
 */
public class LineBorder implements Border {

	private int color;
	private int thickness;
	private int stroke;
	private int insideColor;
        private boolean roundedCorners;
	
        /**
         * creates a black border
         */
	public LineBorder() {
		this(0);
	}
	/**
         * @param c the color for the border
         * @see javax.swing.border.LineBorder#LineBorder(java.awt.Color) LineBorder.LineBorder
         */
	public LineBorder(int c) {
		
		this(c,1);
	}
	/**
         * @param c the color of the border
         * @param t the thickness of the border
         * @see javax.swing.border.LineBorder#LineBorder(java.awt.Color, int) LineBorder.LineBorder
         */
	public LineBorder(int c,int t) {
		
		this(c,Style.NO_COLOR,t,false);
	}
        /**
         * @param c the color of the border
         * @param c2 the inside color
         * @param t the thickness of the border
         * @param r whether or not border corners should be round
         * @see javax.swing.border.LineBorder#LineBorder(java.awt.Color, int, boolean) LineBorder.LineBorder
         */
        public LineBorder(int c,int c2,int t,boolean r) {
            this(c,c2,t,r,Graphics.SOLID);
        }
        
	public LineBorder(int c,int c2,int t,boolean r,int s) {
		
		color=c;
		thickness = t;
		stroke = s;
                insideColor = c2;
                roundedCorners = r;
	}
	
        /**
         * @param c
         * @param g
         * @param width
         * @param height
         * @see javax.swing.border.LineBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int) LineBorder.paintBorder
         */
	public void paintBorder(Component c, Graphics2D g, int width, int height) {
	
            if (!roundedCorners) {
		g.setColor(color);
		
		if (thickness==1) {
			
                        int oldStroke = g.getStrokeStyle();
                    
			g.setStrokeStyle(stroke);
			
			g.drawRect(-1, -1, width+1, height+1);
			
			g.setStrokeStyle(oldStroke);
		}
		else {
			g.fillRect(-thickness, -thickness, width+(thickness*2), thickness);
			g.fillRect(-thickness, height, width+(thickness*2), thickness);
			
			g.fillRect(-thickness, 0, thickness, height );
			g.fillRect(width, 0, thickness, height );
		}
            }
            else {
                
                int a = thickness*4;
		
                if (insideColor!=Style.NO_COLOR) {
                    g.setColor(insideColor);
                    g.fillRoundRect(-thickness,-thickness,width+(2*thickness)-1,height+(2*thickness)-1,a,a);
                }
                
		g.setColor(color);
		g.drawRoundRect(-thickness,-thickness,width+(2*thickness)-1,height+(2*thickness)-1,a,a);
            }
	}

    /**
     * @return the thickness
     * @see javax.swing.border.LineBorder#getBorderInsets(java.awt.Component) LineBorder.getBorderInsets
     */
	public int getBottom() {
		return thickness;
	}

    /**
     * @return the thickness
     * @see javax.swing.border.LineBorder#getBorderInsets(java.awt.Component) LineBorder.getBorderInsets
     */
	public int getLeft() {
		return thickness;
	}

    /**
     * @return the thickness
     * @see javax.swing.border.LineBorder#getBorderInsets(java.awt.Component) LineBorder.getBorderInsets
     */
	public int getRight() {
		return thickness;
	}

    /**
     * @return the thickness
     * @see javax.swing.border.LineBorder#getBorderInsets(java.awt.Component) LineBorder.getBorderInsets
     */
	public int getTop() {
		return thickness;
	}

	/**
         * @return the color of the border
         * @see javax.swing.border.LineBorder#getLineColor() LineBorder.getLineColor
         */
	public int getLineColor() {
		return color;
	}

	public void setLineColor(int color) {
		this.color = color;
	}

        /**
         * @return the thickness of the border
         * @see javax.swing.border.LineBorder#getThickness() LineBorder.getThickness
         */
	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	
        public boolean isBorderOpaque() {
            return false;
        }

}
