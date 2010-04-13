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
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

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

	private Button SOFTKEY_CLEAR;
        private boolean showingClearKey;
//        static {
//            SOFTKEY_CLEAR = new Button("Clear");
//            SOFTKEY_CLEAR.setActionCommand("clear");
//            SOFTKEY_CLEAR.setMnemonic(KeyEvent.KEY_SOFTKEY2);
//        }
//	public static void setClearButtonText(String a) {
//		SOFTKEY_CLEAR.setText(a);
//	}
//
//        private static Command ok = new Command("OK",Command.OK ,1);
//	public static void setOKButtonText(String a) {
//		ok = new Command(a,ok.getCommandType(),ok.getPriority());
//	}
//
//        private static Command cancel = new Command("Cancel",Command.CANCEL,1 );
//        public static void setCancelButtonText(String a) {
//		cancel = new Command(a,cancel.getCommandType(),cancel.getPriority());
//	}

        private static final int cursorBlinkWait = 500;
        private static final int changeModeChar = (Midlet.getPlatform() == Midlet.PLATFORM_SONY_ERICSSON) ? '*' : '#';
        private static int autoAcceptTimeout = 1000; // MUST be more then blink time
        protected int padding = 2;

        public static final int MODE_abc = 0;
        public static final int MODE_Abc = 1;
        public static final int MODE_ABC = 2;
        public static final int MODE_123 = 3;

        private String label;
	private int constraints;
        private int mode;

//	private Border activeBorder;
//        private Border disabledBorder;
//
//        protected int activeTextColor;

        protected StringBuffer text;
        protected Font font;

	private int maxSize;
	protected int caretPosition;

	protected boolean showCaret;

        protected char tmpChar;
        private long lastKeyEvent;
        //protected double preferredPercentWidth=-1;

        /**
         * @see javax.swing.text.JTextComponent#JTextComponent() JTextComponent.JTextComponent
         */
	public TextComponent(String title,String initialText,int max, int constraints) {

		maxSize = max;
                label = title;

                setConstraints(constraints);
                setText(initialText);
        }

        /**
         * @param d the Preferred Width, can be -1 for no Preferred Width
         * /
        public void setPreferredWidth(double d) {
            preferredPercentWidth = d;
        }*/

        public boolean allowNewLine() {
            return true;
        }

        private void insertNewCharacter(char ch) {
                text.insert(caretPosition, ch);

                if (mode==MODE_Abc && !initialCapsConstraint()) {
                    setMode(MODE_abc);
                }

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
            if (isFocusOwner()) {

                // put this back in to hide the clear action on phones it is not needed on
		if (getDesktopPane().USE_SOFT_KEY_CLEAR) {
                    if(caretPosition==0 && tmpChar==0){
                        if (showingClearKey) {
                            getWindow().removeCommand(SOFTKEY_CLEAR);
                            showingClearKey = false;
                        }
                    }
                    else {
                        if (!showingClearKey) {
                            getWindow().addCommand(SOFTKEY_CLEAR);
                            showingClearKey = true;
                        }
                    }
		}
            }
	}

	public void actionPerformed(String actionCommand) {
            if (SOFTKEY_CLEAR.getActionCommand().equals(actionCommand)) {
                clear(true);
            }
            //#mdebug warn
            else {
                Logger.warn("something not right here?!?!?! "+actionCommand);
            }
            //#enddebug
	}

        private void clear(boolean back) {
           if (tmpChar!=0) {
                tmpChar=0;
                changedUpdate(caretPosition,1);
                updateSoftKeys();
                repaint();
            }
            else if (back && caretPosition>0) {
                text.deleteCharAt(caretPosition-1);
                changedUpdate(caretPosition-1,1);
                setCaretPosition(caretPosition-1);
            }
            else if (!back && caretPosition<text.length()) {
                text.deleteCharAt(caretPosition);
                changedUpdate(caretPosition,1);
                repaint();
            }
        }

        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            boolean focusOwner = isFocusOwner();
            super.processMouseEvent(type, x, y, keys);
            if (focusOwner && type==DesktopPane.PRESSED &&
                    Midlet.getPlatform() != Midlet.PLATFORM_ME4SE) {
                // TODO check if we have a qwerty keyboard
                openNativeEditor();
            }
        }


	public boolean processKeyEvent(KeyEvent keyEvent) {

            // if we are getting this event because of a release of a key
            // we will ignore it
            if (keyEvent.getJustReleasedKey()!=0) {
                return false;
            }

            // most recent down key
            int keyCode = keyEvent.getIsDownKey();

            int justPressed = keyEvent.getJustPressedKey();
            if (justPressed!=0) {
                keyCode = justPressed;
            }

            lastKeyEvent = System.currentTimeMillis();

            // 8 is the ascii for backspace, so we dont want this key, no no
            // this is done in the key event
            //if (keyCode==8) { keyCode=KeyEvent.KEY_CLEAR; }

            // if it is a letter that can be typed
            // we dont want to allow Character.MAX_VALUE as its not a real input char

            if (Midlet.getPlatform() == Midlet.PLATFORM_ME4SE && keyCode==-5) {
                keyCode='\n';
            }

            if ( keyCode > Character.MIN_VALUE && keyCode < Character.MAX_VALUE && (keyCode!=changeModeChar || getDesktopPane().QWERTY_KAYPAD || allowOnlyNumberConstraint())) {

                if (keyCode=='\n' && !allowNewLine()) {
                    return false;
                }

                String chars;

                if (mode==MODE_123 && !allowOnlyNumberConstraint()) {
                    chars = String.valueOf( (char)keyCode );
                }
                else {
                    chars = KeyEvent.getChars( (char)keyCode,constraints );
                }

                // this gets up the acceptOld and acceptNew
                keyCode = keyEvent.getKeyChar(keyCode,chars,tmpChar==0);

                if (((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.DECIMAL)) {
                    if (keyCode=='.' && text.toString().indexOf('.')!=-1) {
                        keyCode=0;
                    }
                    else if (keyCode=='-' && (caretPosition!=0 || text.length()==0 || text.charAt(0)=='-' )) {
                        keyCode=0;
                    }
                }

                if (text.length() < maxSize && keyCode!=0) {

                    char thechar = (char)keyCode;

                    if (keyEvent.acceptOld()) {
                        autoAccept();
                    }
                    else {
                        tmpChar = 0;
                    }

                    if (mode==MODE_ABC || (mode==MODE_Abc && shouldUseUppercase() )) {
                        thechar = Character.toUpperCase(thechar);
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
                if (keyCode==changeModeChar) {
                    autoAccept();
                    if (mode!=3) {
                        setMode(mode+1);
                    }
                    else {
                        setMode(0);
                    }
                    return true;
                }
                else if (keyCode==KeyEvent.KEY_CLEAR) {
			clear(true);
			return true;
		}
                else if (keyCode==KeyEvent.KEY_DELETE) {
			clear(false);
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

                    openNativeEditor();

                    return true;
		}
                return false;
            }
	}

        public void openNativeEditor() {

                // can not reuse this because of problems on S60
                textbox = new TextBox(label, getText(), maxSize, constraints);

                Command ok = new Command( (String)DesktopPane.get("okText") , Command.OK, 1);
                Command cancel = new Command( (String)DesktopPane.get("cancelText") , Command.CANCEL, 1);

                textbox.addCommand(ok);
                textbox.addCommand(cancel);

                textbox.setCommandListener(this);
                Display.getDisplay(Midlet.getMidlet()).setCurrent(textbox);

        }

        public void commandAction(Command arg0, Displayable arg1) {
            if (arg0.getCommandType()==Command.OK) {
                setText(textbox.getString());
            }
            // go back to normal
            DesktopPane rp = getDesktopPane();
            Display.getDisplay(Midlet.getMidlet()).setCurrent(rp);
            rp.setFullScreenMode(true);

            textbox = null;
        }

        private boolean shouldUseUppercase() {

            if (initialCapsConstraint()) {

                for (int c=0;c<caretPosition;c++) {
                    char ch = text.charAt(caretPosition-c-1);
                    if (ch==' ') {
                        if ((javax.microedition.lcdui.TextField.INITIAL_CAPS_WORD & constraints) != 0) {
                            return true;
                        }
                        continue;
                    }
                    if (ch=='\n' || ch=='.' || ch=='!' || ch=='?') return true;

                    // return false if we hit a letter!
                    return false;
                }

            }

            return true;
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

	public void animate() throws InterruptedException {

            try {
                int newWait = cursorBlinkWait;

		while (isFocusOwner()) {

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
            }
            finally {
		showCaret = false;
		repaint();
            }
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
            boolean password = ((javax.microedition.lcdui.TextField.PASSWORD & constraints) != 0);

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

		showCaret = false;

                autoAccept();

                DesktopPane dp = getDesktopPane();

                if (dp.USE_SOFT_KEY_CLEAR) {
                    if(showingClearKey){
                        SOFTKEY_CLEAR.getWindow().removeCommand(SOFTKEY_CLEAR);
                        showingClearKey = false;
                    }
                    SOFTKEY_CLEAR = null;
                }

                dp.setIndicatorText(null);
		repaint();
	}


	public void focusGained() {
                super.focusGained();
		showCaret = true;

                Window w = getWindow();

		w.getDesktopPane().animateComponent(this);
                setMode(mode);

                if (w.getDesktopPane().USE_SOFT_KEY_CLEAR) {
                    SOFTKEY_CLEAR = new Button( (String)DesktopPane.get("clearText") );
                    SOFTKEY_CLEAR.addActionListener(this);
                    SOFTKEY_CLEAR.setActionCommand("clear");
                    SOFTKEY_CLEAR.setMnemonic(KeyEvent.KEY_SOFTKEY2);

                    updateSoftKeys();
                }

                // this means that anything stored in
                // the keyEvent Object will be ignored

                // not needed i think
                //tmpChar = 0;

                if (textbox!=null) {
                    openNativeEditor();
                }

		repaint();

	}

//        protected void paintBorder(Graphics2D g) {
//            	if (!focusable && disabledBorder!=null) {
//			disabledBorder.paintBorder(this, g,width,height);
//		}
//                else if (isFocusOwner() && activeBorder!=null) {
//                    activeBorder.paintBorder(this, g,width,height);
//                }
//                else {
//                    super.paintBorder(g);
//                }
//        }

        public void setMode(int m) {

            if (m!=MODE_123 && allowOnlyNumberConstraint() ) {

                // cant change mode under these constraints
                return;
            }

            String i;
            switch(m) {
                case MODE_abc: i="abc"; break;
                case MODE_Abc: i="Abc"; break;
                case MODE_ABC: i="ABC"; break;
                case MODE_123: i="123"; break;
                default: throw new IllegalArgumentException();
            }

            mode = m;

            if (isFocusOwner()) {

                getDesktopPane().setIndicatorText(i);

            }
        }

        private boolean allowOnlyNumberConstraint() {
            return  ((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.PHONENUMBER) ||
                    ((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.NUMERIC) ||
                    ((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.DECIMAL);
        }

        private boolean initialCapsConstraint() {
            return  ((javax.microedition.lcdui.TextField.INITIAL_CAPS_WORD & constraints) != 0) ||
                    ((javax.microedition.lcdui.TextField.INITIAL_CAPS_SENTENCE & constraints) != 0);
        }
//
//	public int getActiveTextColor() {
//		return activeTextColor;
//	}
//
//	public void setActiveTextColor(int color) {
//		activeTextColor = color;
//	}

        public void setConstraints(int m) {
		constraints = m;

                focusable = (javax.microedition.lcdui.TextField.UNEDITABLE & constraints) == 0;

                // this crap again
                Window w = getWindow();
                DesktopPane dp=null;
                if (w!=null) {
                    dp = w.getDesktopPane();
                }
                if (dp==null) {
                    dp = DesktopPane.getDesktopPane();
                }
                // end crap

                if ( dp.QWERTY_KAYPAD ) { // in desktop mode numbers are always numbers
                    setMode(MODE_123);
                }
                else if ( allowOnlyNumberConstraint() ) {
                    setMode(MODE_123);
                }
                else if ( initialCapsConstraint() ) {
                    setMode(MODE_Abc);
                }
                else {
                    setMode(MODE_abc);
                }

	}

	public int getConstraints() {
		return constraints;
	}

        /**
         * @param str
         * @see javax.swing.text.JTextComponent#setText(java.lang.String) JTextComponent.setText
         */
	public void setText(String str) {

		String old = text==null?"":getText();

		text = new StringBuffer(str);
                tmpChar = 0;

		if (!str.equals(old)) {
			changedUpdate(0,text.length());
		}

                setCaretPosition(text.length());
	}

	public void setMaxSize(int size) {
		maxSize = size;
	}

        public int getMaxSize() {
            return maxSize;
        }

        /**
         * @return the text
         * @see javax.swing.text.JTextComponent#getText() JTextComponent.getText
         */
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

	public int getLength() {
		return text.length() + ((tmpChar==0)?0:1);
	}

        public void updateUI() {
                super.updateUI();

                font = theme.getFont(Style.ALL);

                if (SOFTKEY_CLEAR!=null) {
                    SOFTKEY_CLEAR.updateUI();
                }
        }


        // Crazy binary search!
        public static int searchStringCharOffset(String text, Font font,int xPixelOffset) {

                int first = 0;
                int upto  = text.length();
                int mid=0;
                while (first < upto) {
                    mid = (first + upto) / 2;

                    int charPos1 = font.getWidth(text.substring(0,mid));
                    int charPos2 = charPos1 + font.getWidth(text.substring(mid,mid+1));

                    if (xPixelOffset<charPos1) {
                        upto = mid;
                    }
                    else if (xPixelOffset>charPos2) {
                        first = mid + 1;
                    }
                    else {
                        break;
                    }
                }
                return mid;

        }

        public void setValue(Object obj) {
            setText( String.valueOf(obj) );
        }
        public Object getValue() {
            return getText();
        }

}
