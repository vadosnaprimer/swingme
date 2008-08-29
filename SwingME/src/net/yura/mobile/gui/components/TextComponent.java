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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.border.Border;

/**
 * @author Yura Mamyrin
 * @see javax.swing.text.JTextComponent
 */
public abstract class TextComponent extends Component implements ActionListener, CommandListener {
	
        public static final int ANY=javax.microedition.lcdui.TextField.ANY;
        public static final int CONSTRAINT_MASK=javax.microedition.lcdui.TextField.CONSTRAINT_MASK;
        public static final int DECIMAL=javax.microedition.lcdui.TextField.DECIMAL;
        public static final int EMAILADDR=javax.microedition.lcdui.TextField.EMAILADDR;
        public static final int INITIAL_CAPS_SENTENCE=javax.microedition.lcdui.TextField.INITIAL_CAPS_SENTENCE;
        public static final int INITIAL_CAPS_WORD=javax.microedition.lcdui.TextField.INITIAL_CAPS_WORD;
        public static final int NON_PREDICTIVE=javax.microedition.lcdui.TextField.NON_PREDICTIVE;
        public static final int NUMERIC=javax.microedition.lcdui.TextField.NUMERIC;
        public static final int PASSWORD=javax.microedition.lcdui.TextField.PASSWORD;
        public static final int PHONENUMBER=javax.microedition.lcdui.TextField.PHONENUMBER;
        public static final int SENSITIVE=javax.microedition.lcdui.TextField.SENSITIVE;
        public static final int UNEDITABLE=javax.microedition.lcdui.TextField.UNEDITABLE;
        public static final int URL=javax.microedition.lcdui.TextField.URL;
    
        private static TextBox textbox;
    
	private static CommandButton SOFTKEY_CLEAR = new CommandButton("Clear", "clear");
	public static void setClearButtonText(String a) {
		SOFTKEY_CLEAR = new CommandButton(a,SOFTKEY_CLEAR.getActionCommand());
	}

        private static Command ok = new Command("OK",Command.OK ,1);
	public static void setOKButtonText(String a) {
		ok = new Command(a,ok.getCommandType(),ok.getPriority());
	}
        
        private static Command cancel = new Command("Cancel",Command.CANCEL,1 );
        public static void setCancelButtonText(String a) {
		cancel = new Command(a,cancel.getCommandType(),cancel.getPriority());
	}
        
        private static final int cursorBlinkWait = 500;
        private static int autoAcceptTimeout = 1000; // MUST be more then blink time
        protected static final int padding = 2;
        
        private String label;
	private int mode;

	private Border borderColor;
	private Border activeBorderColor;
        protected int activeTextColor;

        private StringBuffer text;
        protected Font font;

	private int maxSize;
	protected int caretPosition;
        
	protected boolean showCaret;
	
        private char tmpChar;
        private long lastKeyEvent;
        
        /**
         * @see javax.swing.text.JTextComponent#JTextComponent() JTextComponent.JTextComponent
         */
	public TextComponent(String title,String initialText,int max, int constraints) {

		maxSize = max;
		mode = constraints;
                label = title;

                if ((javax.microedition.lcdui.TextField.UNEDITABLE & mode) != 0) {
                    selectable = false;
                }

                setText(initialText);

	}

        private void insertNewCharacter(char ch) {
                text.insert(caretPosition, ch);
        }

        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent) DocumentListener.changedUpdate
         */
        protected void changedUpdate(int offset,int length) {
            
        }
        
        protected void autoAccept() {
            
            if (tmpChar!=0) {
                char tmp = tmpChar;
                tmpChar=0;
                insertNewCharacter(tmp);
                setCaretPosition(caretPosition+1);
            }
            
        }
	
	private void updateSoftKeys() {
		
            if (isFocused()) {
                // put this back in to hide the clear action on phones it is not needed on
		//if (!RootPane.hasclearkey) {
		
			if(caretPosition==0 && tmpChar==0){
				DesktopPane.getDesktopPane().setComponentCommand(1,null);
			}
                        else{
				DesktopPane.getDesktopPane().setComponentCommand(1,SOFTKEY_CLEAR);
			}
		//}
            }
	}
	
	public void actionPerformed(String actionCommand) {
		if ("clear".equals(actionCommand)) {

                    if (tmpChar!=0) {
                        tmpChar=0;
                        changedUpdate(caretPosition,1);
                        updateSoftKeys();
                        repaint();
                    }
                    else if (caretPosition>0) {
                        
                        text.deleteCharAt(caretPosition-1);
                        changedUpdate(caretPosition,1);
                        setCaretPosition(caretPosition-1);

                        
                    }
                    
		}
	}

	public boolean keyEvent(KeyEvent keyEvent) {
		
            int keyCode = keyEvent.getIsDownKey();
            
            lastKeyEvent = System.currentTimeMillis();
            
            // 8 is the ascii for backspace, so we dont want this key, no no
            if (keyCode==8) { keyCode=KeyEvent.KEY_CLEAR; }
            
            // if it is a letter that can be typed
            if (keyCode > Character.MIN_VALUE && keyCode <= Character.MAX_VALUE ) {
                
                keyCode = keyEvent.getKeyChar(mode,tmpChar==0);
                
                
                if (text.length() < maxSize && keyCode!=0) {
                    
                    char thechar = (char)keyCode;
                    
                    if (keyEvent.acceptOld()) {
                        autoAccept();
                    }
                    else {
                        tmpChar = 0;
                    }
                    
                    if (keyEvent.acceptNew()) {
                         insertNewCharacter(thechar);
                         changedUpdate(caretPosition,1);
                         setCaretPosition(caretPosition+1);
                    }
                    else {
                        tmpChar = thechar;
                        changedUpdate(caretPosition,1);
                        updateSoftKeys();
                        repaint();
                    }
                }
                return true;
            }
            else {
                if (keyEvent.isDownKey(KeyEvent.KEY_CLEAR) || keyCode==KeyEvent.KEY_CLEAR) {
			actionPerformed(SOFTKEY_CLEAR.getActionCommand());
			return true;
		}
                else if (keyEvent.isDownAction(Canvas.LEFT)) {
                    
                    if (caretPosition>0) {
                        autoAccept();
                        setCaretPosition(caretPosition-1);
                        return true;
                    }
                    else {
                        return !keyEvent.justPressedAction(Canvas.LEFT);
                    }
                    
                }
                else if (keyEvent.isDownAction(Canvas.RIGHT)) {
                    
                    if (tmpChar!=0) {
                       autoAccept();
                       return true;
                    }
                    else if (caretPosition<text.length() || tmpChar!=0) {

                        setCaretPosition(caretPosition+1);
                        return true;
                    }
                    else {
                        return !keyEvent.justPressedAction(Canvas.RIGHT);
                    }
                    
                }
                else if (keyEvent.justPressedAction(Canvas.FIRE)) {
                       
                    if (textbox==null) {
                        textbox = new TextBox(label, getText(), maxSize, mode);
                        textbox.addCommand(ok);
                        textbox.addCommand(cancel); 
                    }
                    else {
                       textbox.setConstraints(mode);
                       textbox.setTitle(label);
                       textbox.setString(getText());
                       textbox.setMaxSize(maxSize);
                    }

                    textbox.setCommandListener(this);
                    Display.getDisplay(DesktopPane.getDesktopPane().getMidlet()).setCurrent(textbox);
                       
                    return true;
		}
                return false;
            }
            
            
	}
        
        public void setTitle(String s) {
            label = s;
        }
        
        /**
         * @param a position in the text
         * @see javax.swing.text.JTextComponent#setCaretPosition(int) JTextComponent.setCaretPosition
         */
        public void setCaretPosition(int a) {
            caretPosition = a;
            repaint();
            updateSoftKeys();
        }
        
        /**
         * @return the Caret position
         * @see javax.swing.text.JTextComponent#getCaretPosition() JTextComponent.getCaretPosition
         */
        public int getCaretPosition() {
            return caretPosition;
        }

        public void commandAction(Command arg0, Displayable arg1) {
            if (arg0==ok) {
                setText(textbox.getString());
            }
            // go back to normal
            DesktopPane rp = DesktopPane.getDesktopPane();
            Display.getDisplay(rp.getMidlet()).setCurrent(rp);
            rp.setFullScreenMode(true);
        }
        
	public void animate() {

                int newWait = cursorBlinkWait;
            
		while (isFocused()) {

                    int oldCaret = caretPosition;
                    
			wait(newWait);

                        long timeNow = System.currentTimeMillis();
                        
                        if (tmpChar!=0 && timeNow > lastKeyEvent + autoAcceptTimeout) {

                                autoAccept();
                                newWait = cursorBlinkWait;
                        }
                        else if (tmpChar!=0) {
                            newWait = (int)Math.max(1, lastKeyEvent + autoAcceptTimeout - timeNow);
                        }
                        else {
                            newWait = cursorBlinkWait;
                        }

                        if (oldCaret==caretPosition) {
                            showCaret = !showCaret;
                        }
                        else {
                            showCaret = true;
                        }
			
			repaint();
		}
		showCaret = false;
		repaint();
	}

        protected String getDisplayString() {
            
            String s=text.toString();
            
            // there is a VERY small chance that this method is called
            // when the caret is outside the text
            // this is because it is called during a paint method, and 
            // they can happen when ever
            //
            // the reason a caret can be outside the text is because
            // when we remove a char, we need to first del it from the string
            // then inform the companent that a change has happened
            // and only after that move the carret back
            // so when the carret is moving, is has information such as
            // what is in the string and how many lines it takes up available
            int caret = caretPosition>s.length()?s.length():caretPosition;
            
            // TODO if password or entering text, add that info
            boolean password = ((javax.microedition.lcdui.TextField.PASSWORD & mode) != 0);
            
            String st1 = s.substring(0, caret);
            String st2 = s.substring(caret, s.length() );
            
            if (password) {
                StringBuffer buffer = new StringBuffer();
                for (int c=0;c<st1.length();c++) {
                    buffer.append('*');
                }
                if (tmpChar!=0) {
                     buffer.append(tmpChar);
                }
                for (int c=0;c<st2.length();c++) {
                    buffer.append('*');
                }
                
                return buffer.toString();
            }
            else {
                 return st1+((tmpChar!=0)?String.valueOf(tmpChar):"")+st2;
            }
           
        }
        
	public void focusLost() {
		super.focusLost();
		setBorder(borderColor);
		
		showCaret = false;

                autoAccept();

                DesktopPane.getDesktopPane().setComponentCommand(1,null);
		repaint();
	}

	
	public void focusGained() {
                super.focusGained();
		setBorder(activeBorderColor);
		
		showCaret = true;

		DesktopPane.getDesktopPane().animateComponent(this);
		
                updateSoftKeys();
                
                // this means that anything stored in
                // the keyEvent Object will be ignored
                
                // not needed i think
                //tmpChar = 0;
                
		repaint();

	}
	
        
	public int getActiveTextColor() {
		return activeTextColor;
	}

	public void setActiveTextColor(int color) {
		activeTextColor = color;
	}

	public int getMode() {
		return mode;
	}

	public void setText(String str) {
		text = new StringBuffer(str);
		setCaretPosition(text.length());
	}

	public void setMaxSize(int size) {
		maxSize = size;
	}

	public String getText() {
            String s=text.toString();
            if (tmpChar==0) {
		return s;
            }
            else {
                return s.substring(0, caretPosition)+tmpChar+ s.substring(caretPosition, s.length() );
            }
	}
        
        /**
         * @return the font
         * @see java.awt.Component#getFont() Component.getFont
         */
        public Font getFont() {
		return font;
	}
        
        /**
         * @param font The font to use
         * @see javax.swing.JComponent#setFont(java.awt.Font) JComponent.setFont
         */
	public void setFont(Font font){
		this.font = font;
	}
        
	public void setConstraints(int m) {
		mode = m;
	}

	public int getLength() {
		return text.length() + ((tmpChar==0)?0:1);
	}

        public void updateUI() {
                super.updateUI();
                Style theme = DesktopPane.getDefaultTheme(this);
        
                font = theme.getFont(Style.ALL);
		borderColor = theme.getBorder(Style.ALL);
		activeBorderColor = theme.getBorder(Style.FOCUSED);
		activeTextColor = theme.getForeground(Style.FOCUSED);
        
        }

}
