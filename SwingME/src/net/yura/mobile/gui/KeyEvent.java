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

package net.yura.mobile.gui;

import javax.microedition.lcdui.Canvas;

/**
 * @author Yura Mamyrin
 * @see java.awt.event.KeyEvent
 */
public class KeyEvent {

     // ALL KEYS THAT ARE NOT LETTERS MUST BE NAGATIVE!
    
	public static final int KEY_SOFTKEY1 = -6;	//Default Softkey left value
	public static final int KEY_SOFTKEY2 = -7;	//Default Softkey right value
        public static final int KEY_CLEAR = -8;         //Default Clear value, this NEEDS to be negative

        public static final int KEY_CALL = -10;
        public static final int KEY_END  = -11;
        public static final int KEY_MENU = -12;
        public static final int KEY_EDIT = -50;
        
	private Canvas canvas;
	
	private int justPressedKey;
        private int justReleasedKey;
        private int[] isDownKeys=new int[1];

        
        private int pos;
        private boolean acc;
        private boolean old;
        private int lastGotCode;
        
        public boolean acceptNew() {
            return acc;
        }
        public boolean acceptOld() {
            return old;
        }
        public char getKeyChar(int mode,boolean accepted) {
            
            // we give priority to whats JUST been pressed
            int code = getJustPressedKey();
            // try and get the just pressed code, as if thats valid, we should use it
            if (code <= Character.MIN_VALUE || code > Character.MAX_VALUE ) {
                    code = getIsDownKey();
            }


                
                char[] cha = getChars((char)code,mode);

                // only accept the old key if the code is different from the current, and there is something to accept
                old = !accepted && lastGotCode != code;
                lastGotCode = code;

                // if there is no letter to use
                if (cha.length==0) {
                    return 0;
                }
                
                // this means we are here coz the user is holding down the button
                // (and the last letter was NOT accepted, as pos can NOT be -1 if it was)
                if (pos==-1) {
                                // we would never get a case where normally hitting a key does nothing,
                                // (or only puts 1 char on) but holding it down gives something different
                                // as those keys will never end in pos==-1 as they would be accpeted right away
                
                    acc = true;
                    return (char)code;
                }
                //else {

                    // here if they are holding it down for a long time
                    // and there is more then 1 letter to choose from
                    // this means we wanted the number and now dont want anything
                    if (pos==-2 && cha.length!=1) {
                        return 0;
                    }
                    // else we are going to repeat a letter

                    // if we accepted the last letter then reset the position
                    if (accepted) {
                        pos=0;
                    }
                    // autoaccept if the length is 1
                    acc = cha.length==1;
                    return cha[pos%cha.length];
                //}
            //}
        }
        
        private static final char[] CHARS_PHONE = new char[] {'*','+','p','w'};
        
        private static final char[] CHARS_42 = new char[] {'*'};
        private static final char[] CHARS_35 = new char[] {'#'};
        private static final char[] CHARS_48 = new char[] {' ','0'};
        private static final char[] CHARS_49 = new char[] {'.',',','?','!','1','@','\'','-','_'};
        private static final char[] CHARS_50 = new char[] {'a','b','c','2'};
        private static final char[] CHARS_51 = new char[] {'d','e','f','3'};
        private static final char[] CHARS_52 = new char[] {'g','h','i','4'};
        private static final char[] CHARS_53 = new char[] {'j','k','l','5'};
        private static final char[] CHARS_54 = new char[] {'m','n','o','6'};
        private static final char[] CHARS_55 = new char[] {'p','q','r','s','7'};
        private static final char[] CHARS_56 = new char[] {'t','u','v','8'};
        private static final char[] CHARS_57 = new char[] {'w','x','y','z','9'};
        
        public static char[] getChars(char keycode,int mode) {
            
            if (((mode & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.NUMERIC)) {
                
                if (keycode>='0' && keycode<='9') {
                    return new char[] {keycode};
                }
                return new char[] {};
                
            }
            
            if (((mode & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.PHONENUMBER)) {
                
                if ((keycode>='0' && keycode<='9') || keycode=='#') {
                    return new char[] {keycode};
                }
                if (keycode=='*'){
                    return CHARS_PHONE;
                }
                
                for (int c=0;c<CHARS_PHONE.length;c++) {
                    if (keycode==CHARS_PHONE[c]) {
                        return new char[] {keycode};
                    }
                }
                
                return new char[] {};
                
            }
            
            switch(keycode) {
                case '*': return CHARS_42;
                case '#': return CHARS_35;
                case '0': return CHARS_48;
                case '1': return CHARS_49;
                case '2': return CHARS_50;
                case '3': return CHARS_51;
                case '4': return CHARS_52;
                case '5': return CHARS_53;
                case '6': return CHARS_54;
                case '7': return CHARS_55;
                case '8': return CHARS_56;
                case '9': return CHARS_57;
                default: return new char[] {keycode};
            }
            
        }
        
	public KeyEvent(Canvas c) {

		canvas = c;
		
	}

    public int getJustPressedKey() {
        return justPressedKey;
    }

    public int getJustReleasedKey() {
        return justReleasedKey;
    }
    /*
     * This is a bad method as it only returns the FIRST key that is down! 
     */
    public int getIsDownKey() {
           for (int c=0;c<isDownKeys.length;c++) {
                if (isDownKeys[c] != 0) {
                    return isDownKeys[c];
                }
           }
           return 0;
    }
    
    // on nokia emulators can throw when the key does not exist on the phone
    public String getKeyText(int keyCode) {
        try {
            return canvas.getKeyName(keyCode);
        }
        catch(Throwable ex) {
            return ex.toString();
        }
    }
    public int getKeyAction(int code) {
        try {
            return canvas.getGameAction(code);
        }
        catch(Throwable ex) {
            return 0;
        }
    }

        public boolean isDownAction(int action) {
            for (int c=0;c<isDownKeys.length;c++) {
                if (isDownKeys[c]!=0 && getKeyAction(isDownKeys[c]) == action) {
                    return true;
                }
            }
            return false;
        }

        public boolean justPressedAction(int action) {
            return justPressedKey!=0 && getKeyAction(justPressedKey) == action;
        }

        public boolean justReleasedAction(int action) {
            return justReleasedKey!=0 && getKeyAction(justReleasedKey) == action;
        }

        
        
        
        public boolean isDownKey(int key) {
            for (int c=0;c<isDownKeys.length;c++) {
                if (isDownKeys[c] == key) {
                    return true;
                }
            }
            return false;
        }

        public boolean justPressedKey(int key) {
            return justPressedKey == key;
        }

        public boolean justReleasedKey(int key) {
            return justReleasedKey == key;
        }
        
	public void keyPressed(int keyCode) {

            keyCode = check(keyCode);
            
                // count up if the same key is being pressed
                if (justReleasedKey==keyCode) {
                     pos++;
                }
                else {
                    pos=0;
                }
            
		justReleasedKey = 0;
		justPressedKey = keyCode;
                addKeyDown(keyCode);
	}

	public void keyReleased(int keyCode) {
            
            keyCode = check(keyCode);
            
                // reset to 0 if we were holding the key down
                if (pos<0) {
                    pos=0;
                }
            
            	justPressedKey = 0;
		justReleasedKey = keyCode;
                removeKeyDown(keyCode);
	}

        private int check(int keyCode) {
            
            switch (keyCode) {
                case 8: return KEY_CLEAR;
                case 13: return '\n'; // 10
                default: return keyCode;
            }

        }
        
	public void keyRepeated(int keyCode) {

                // if its held down for a long time to to -2
                if (pos==-1 || pos==-2) {
                    pos=-2;
                }
                // otherwise -1
                else {
                    pos=-1;
                }
                
		justReleasedKey=0;
                justPressedKey=0;
	}

        private void addKeyDown(int keyCode) {
            
            boolean done=false;
            for (int c=0;c<isDownKeys.length;c++) {
                if (isDownKeys[c]==0) {
                    isDownKeys[c] = keyCode;
                    done=true;
                }
            }
            if (!done) {
                int[] newKeyDown = new int[isDownKeys.length+1];
                for (int c=0;c<newKeyDown.length;c++) {
                    if (c!=isDownKeys.length) {
                        newKeyDown[c] = isDownKeys[c];
                    }
                    else {
                        newKeyDown[c] = keyCode;
                    }
                }
            }
            
        }
        private void removeKeyDown(int keyCode) {
            
            for (int c=0;c<isDownKeys.length;c++) {
                if (isDownKeys[c]==keyCode) {
                    isDownKeys[c] = 0;
                }
            }
        }
	public void clear() {
            
            justPressedKey=0;
            justReleasedKey=0;
            for (int c=0;c<isDownKeys.length;c++) {
                    isDownKeys[c] = 0;
            }
            
        }
}
