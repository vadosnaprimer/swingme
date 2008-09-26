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

/**
 * @author Yura Mamyrin
 * @see javax.swing.JCheckBox
 */
public class CheckBox extends RadioButton {
    
    /**
     * @see javax.swing.JCheckBox#JCheckBox() JCheckBox.JCheckBox
     */
        public CheckBox() {
        }

    /**
     * @param label
     * @see javax.swing.JCheckBox#JCheckBox(java.lang.String) JCheckBox.JCheckBox
     */
	public CheckBox(String label){
            super(label);
	}
	
        /**
         * @param text
         * @param selected
         * @see javax.swing.JCheckBox#JCheckBox(java.lang.String, boolean) JCheckBox.JCheckBox
         */
        public CheckBox(String text, boolean selected) {
             super(text);
             setSelected(selected);
        }
        
        protected void toggleSelection() {
            setSelected(!isSelected());
        }

    protected void paintIcon(Graphics g, int x, int y) {

        if (icon==null) {

            g.setColor(foreground);
            
            int w = getIconWidth();
            int h = getIconHeight();

            g.drawRect(x, y, w-1, h-1);

            if (isSelected()) {

                //g.fillRect(x+3, y+3, size-6, size-6);
                
                for (int pad=3;pad<6;pad++) {
                    g.drawLine(x+pad, y+h/2, x+w/3, y+h-pad);
                    g.drawLine(x+w/3, y+h-pad,x+w-pad,y+pad);
                }

            }
            
        }
        else {
            super.paintIcon(g, x, y);
        }

    }
        

        public String getName() {
            return "CheckBox";
        }
	
}