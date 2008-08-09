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
import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTextField
 */
public class TextField extends TextComponent {

        private int offset;
    
        public TextField() {
            this(TextComponent.ANY);
        }
        
        public TextField(int a) {
            super("TextField", "", 100, a);
            
            offset = padding;
            
            workoutSize();
        }
    
    	public void paintComponent(Graphics g) {
            
		String textString = getDisplayString();

		int oldClipX = g.getClipX();
		int oldClipY = g.getClipY();
		int oldClipW = g.getClipWidth();
		int oldClipH = g.getClipHeight();
		g.clipRect(0, 0, width, height);
                
                g.setColor(isFocused()?activeTextColor:foreground);
		font.drawString(g, textString, offset, (height-font.getHeight())/2, Graphics.TOP | Graphics.LEFT);

                if (showCaret) {
                    int x = font.getWidth(textString.substring(0, caretPosition));
                    g.drawLine(offset+x, padding, offset+x, height-padding-1);
                }
		g.setClip(oldClipX, oldClipY, oldClipW, oldClipH);
	}
    
        public void setCaretPosition(int a) {
            
            super.setCaretPosition(a);
            
            int extraSpace=10;
            String s = getDisplayString();
            int x = font.getWidth(s.substring(0, caretPosition));
            int fw = font.getWidth(s)+extraSpace;
            
            if (x > -offset+width-padding-extraSpace) {
                offset = -x+(width*2)/3;
            }
            else if (x<-offset+padding) {
                offset = -x+width/3;
            }
            
            if (-offset+width-padding > fw) { offset=width-padding-fw; }
            if (offset>padding) { offset=padding; }

            repaint();
        }

        public void workoutSize() {
                    // TODO, add preferred width option
                    width = DesktopPane.getDesktopPane().getWidth() /2;
                    height = font.getHeight() + padding*2; // put some padding in
        }

    public String getName() {
        return "TextField";
    }
        
}
