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

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;

import javax.microedition.lcdui.Image;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JCheckBox
 */
public class CheckBox extends RadioButton {
    
        public CheckBox(String label,Image a,Image b) {
            super(label,a,b);
        }
    
	public CheckBox(String label){
            super(label);
	}
	
        protected void toggleSelection() {
            selected=!selected;
        }

	public void paintExtra(Graphics g) {

            int size = getFont().getHeight();
            
            int x=padding;
            int y = (height-size)/2;

            g.drawRect(x, y, size-1, size-1);

            if (isSelected()) {

                //g.fillRect(x+3, y+3, size-6, size-6);
                
                for (int pad=3;pad<6;pad++) {
                    g.drawLine(x+pad, y+size/2, x+size/3, y+size-pad);
                    g.drawLine(x+size/3, y+size-pad,x+size-pad,y+pad);
                }

            }
	}

	
}