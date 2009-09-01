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

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;

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
            preferredWidth = 0.5;
            
            workoutSize();
        }
    
    	public void paintComponent(Graphics2D g) {
            
		String textString = getDisplayString();

		int[] oldClip = g.getClip();
		g.clipRect(0, 0, width, height);
                
                g.setColor( getCurrentForeground() );
                g.setFont(font);
		g.drawString( textString, offset, (height-font.getHeight())/2 );

                if (showCaret) {
                    int x = font.getWidth(textString.substring(0, caretPosition));
                    g.drawLine(offset+x, padding, offset+x, height-padding-1);
                }
		g.setClip(oldClip);
	}

        public void pointerEvent(int type, int x, int y, KeyEvent keys) {
            super.pointerEvent(type, x, y, keys);

            if (type==DesktopPane.RELEASED) {

                String txt = getDisplayString()+" ";

                // TODO take into account centre and right aligh
                int mid = getStringCharOffset(txt,font,x -offset);

                setCaretPosition(mid);
            }
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

        public void workoutMinimumSize() {

            if (preferredWidth!=-1) {
                width = (int)(DesktopPane.getDesktopPane().getWidth()*preferredWidth);
            }
            else {
                width = 0;
            }
            
            height = font.getHeight() + padding*2; // put some padding in
        }

    public String getDefaultName() {
        return "TextField";
    }
        public boolean allowNewLine() {
            return false;
        }
        
}
