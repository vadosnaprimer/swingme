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

import javax.microedition.lcdui.Canvas;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTextField
 */
public class TextField extends TextComponent {

        private int offset;
        private ActionListener al;
        private String action;

        /**
         * @see javax.swing.JTextField#JTextField() JTextField.JTextField
         */
        public TextField() {
            this(TextComponent.ANY);
        }

        /**
         * @param constraints the type of text, {@link #ANY } is default
         */
        public TextField(int constraints) {
            super("", 100, constraints);

            offset = padding;

            workoutPreferredSize();
        }

        /**
         * @see TextArea#paintComponent(Graphics2D) 
         */
    	public void paintComponent(Graphics2D g) {

                int f = getForeground();
                
                if (!Graphics2D.isTransparent(f)) { // we need to support transparent foreground as Swing does and we need it for android
            
                    String textString = getDisplayString();

                    int[] oldClip = g.getClip();
                    g.clipRect(0, 0, width, height);

                    g.setColor( f );
                    g.setFont(font);
                    g.drawString( textString, offset, (height-font.getHeight())/2 );

                    if (showCaret) {
                        int x = font.getWidth(textString.substring(0, caretPosition));
                        g.drawLine(offset+x, padding, offset+x, height-padding-1);
                    }
                    g.setClip(oldClip);
                }
	}

        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            super.processMouseEvent(type, x, y, keys);
            if (focusable) {
                String txt = getDisplayString()+" ";
                int mid = searchStringCharOffset(txt,font,x -offset);
                setCaretPosition(mid);
            }
        }

        public void focusGained() {
            super.focusGained();
            // this is called here to update any inner scroll that needs to be done
            setCaretPosition( getCaretPosition() );
        }

        public void setCaretPosition(int a) {

            super.setCaretPosition(a);

            if (isFocusOwner()) {

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

        }

        protected void workoutMinimumSize() {
            width = 10;
            height = font.getHeight() + padding*2; // put some padding in
        }

        public String getDefaultName() {
            return "TextField";
        }

        public boolean allowChar(char keyCode) {
            if (keyCode=='\t') {
                return false;
            }
            if (keyCode=='\n') {
                if (al!=null) {
                    al.actionPerformed(action);
                }
                else {
                    transferFocus(Canvas.DOWN);
                }
                return false;
            }
            return super.allowChar(keyCode);
        }

        /**
         * @see Button#addActionListener(ActionListener)
         * @see javax.swing.JTextField#addActionListener(java.awt.event.ActionListener) JTextField.addActionListener
         */
        public void addActionListener(ActionListener al) {
            //#mdebug warn
            if (this.al!=null) {
                Logger.warn("trying to add a ActionListener when there is already one registered "+this);
                Logger.dumpStack();
            }
            if (al==null) {
                Logger.warn("trying to add a null ActionListener "+this);
                Logger.dumpStack();
            }
            //#enddebug
            this.al = al;
        }

        /**
         * @see Button#setActionCommand(String)
         * @see javax.swing.JTextField#setActionCommand(java.lang.String) JTextField.setActionCommand
         */
        public void setActionCommand(String action) {
            this.action = action;
        }

        /**
         * @see Button#getActionCommand()
         * @see javax.swing.JTextField#getActionCommand() JTextField.getActionCommand
         */
        public String getActionCommand() {
            return action;
        }

        /**
         * @see Button#getActionListeners()
         * @see javax.swing.JTextField#getActionListeners() JTextField.getActionListeners
         */
        public ActionListener[] getActionListeners() {
            return al==null?new ActionListener[0]:new ActionListener[] { al };
        }
}
