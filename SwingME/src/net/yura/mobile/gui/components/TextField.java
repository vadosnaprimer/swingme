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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTextField
 */
public class TextField extends Component implements ActionListener, CommandListener {
	
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
        private static final int padding = 2;
        
        private int offset;
        private String label;
	private int mode;

	private Border borderColor;
	private Border activeBorderColor;
        private int activeTextColor;

        private StringBuffer text;
        private Font font;

	private int maxSize;
	private int caretPosition;
        
        private boolean active;
	private boolean showCaret;
	
        private char tmpChar;
        private long lastKeyEvent;
        
        public TextField(int constraints) {
            this("Text","",100,constraints);
        }
        
	public TextField(String title, String text, int maxSize, int constraints) {
		this(title,text,maxSize,constraints,DesktopPane.getDefaultTheme().font);
	}

	public TextField(String title,String initialText,int max, int mod,Font f) {

		font = f;
		maxSize = max;
		mode = mod;
                label = title;

		height = font.getHeight() + padding*2; // put some padding in
		// give it some random width
		width = DesktopPane.getDefaultTheme().defaultWidth /2; // TODO BAD!!

		setBackground(DesktopPane.getDefaultTheme().background);
		setForeground(DesktopPane.getDefaultTheme().foreground);

		borderColor = DesktopPane.getDefaultTheme().normalBorder;
		activeBorderColor = DesktopPane.getDefaultTheme().activeBorder;
		
		setBorder(borderColor);
		
		if (font.getColors().length >1) {
			activeTextColor = font.getColors()[1];
		}
		else {
			activeTextColor = foreground;
		}
                
                transparent = false;
                
                offset = padding;
                
                setText(initialText);
	}

        private void insertNewCharacter(char ch) {
            
                text.insert(caretPosition, ch);
                setCaretPosition(caretPosition+1);
        }
        private void autoAccept() {
            
            if (tmpChar!=0) {
                insertNewCharacter(tmpChar);
                tmpChar=0;
            }
            
        }
	
	private void updateSoftKeys() {
		
		// put this back in to hide the clear action on phones it is not needed on
		//if (!RootPane.hasclearkey) {
		
			if(caretPosition==0){
				DesktopPane.getDesktopPane().setComponentCommand(1,null);
			}
                        else{
				DesktopPane.getDesktopPane().setComponentCommand(1,SOFTKEY_CLEAR);
			}
		//}
	}
	
	public void actionPerformed(String actionCommand) {
		if ("clear".equals(actionCommand)) {

                    if (tmpChar!=0) {
                        tmpChar=0;
                        repaint();
                    }
                    else if (caretPosition>0) {
                        text.deleteCharAt(caretPosition-1);
                        setCaretPosition(caretPosition-1);
                    }
                    
		}
	}

	public boolean keyEvent(KeyEvent keyEvent) {
		
            int keyCode = keyEvent.getIsDownKey();
            
            lastKeyEvent = System.currentTimeMillis();
            
            // 8 is the ascii for backspace, sowe dont want this key, no no
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
                    }
                    else {
                        tmpChar = thechar;
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
        
        public void setCaretPosition(int a) {
            
            int extraSpace=10;
            caretPosition = a;
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
            
            updateSoftKeys();
            repaint();
            
        }
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
            
		while (active) {
                    
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

			showCaret = !showCaret;
			
			repaint();
		}
		showCaret = false;
		repaint();
	}

	public void paintComponent(Graphics g) {
            
		String textString = getDisplayString();

		int oldClipX = g.getClipX();
		int oldClipY = g.getClipY();
		int oldClipW = g.getClipWidth();
		int oldClipH = g.getClipHeight();
		g.clipRect(0, 0, width, height);
                
                g.setColor(active?activeTextColor:foreground);
		font.drawString(g, textString, offset, (height-font.getHeight())/2, Graphics.TOP | Graphics.LEFT);

                if (showCaret) {
                    int x = font.getWidth(textString.substring(0, caretPosition));
                    g.drawLine(offset+x, padding, offset+x, height-padding-1);
                }
		g.setClip(oldClipX, oldClipY, oldClipW, oldClipH);
	}

        private String getDisplayString() {
            
            // TODO if password or entering text, add that info
            
            boolean password = ((javax.microedition.lcdui.TextField.PASSWORD & mode) != 0);
            String s=text.toString();
            
            String st1 = s.substring(0, caretPosition);
            String st2 = s.substring(caretPosition, s.length() );
            
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
		
		active = false;
		showCaret = false;
		
		DesktopPane.getDesktopPane().setComponentCommand(1,null);
		
                autoAccept();
                
		repaint();
	}

	
	public void focusGained() {
                super.focusGained();
		setBorder(activeBorderColor);
		
		active = true;
		showCaret = true;

		DesktopPane.getDesktopPane().animateComponent(this);
		
                updateSoftKeys();
                
                // this means that anything stored in
                // the keyEvent Object will be ignored
                tmpChar = 0;
                
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
		return text.toString();
	}

        public Font getFont() {
		return font;
	}
        
	public void setMode(int m) {
		mode = m;
	}

	public int getLength() {
		return text.length();
	}

}
