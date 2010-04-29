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
        public static final int KEY_SOFTKEY3 = -5;	//Default Softkey center value
        public static final int KEY_CLEAR = -8;         //Default Clear value, this NEEDS to be negative

        public static final int KEY_DELETE = -9; // is the right 'clear' key in WTK3

        public static final int KEY_CALL = -10; // green key
        public static final int KEY_END  = -11; // back button on some sony ericsons, android
        public static final int KEY_MENU = -12; // 'app menu' on S60, 'inapp menu' on android
        public static final int KEY_EDIT = -50; // S60 pensil button
        //public static final int KEY_WEB = -20; // SonyE
        //public static final int KEY_PHOTO = -24 or -25 or -26; // SonyE
        //public static final int KEY_UP = -36; // SonyE
        //public static final int KEY_DOWN = -37; // SonyE
        //public static final int KEY_MUSIC = -22 or -23; // SonyE (opens music player)

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
        public char getKeyChar(int code,String cha, boolean accepted) {
//
//            // we give priority to whats JUST been pressed
//            int code = getJustPressedKey();
//            // try and get the just pressed code, as if thats valid, we should use it
//            if (code <= Character.MIN_VALUE || code > Character.MAX_VALUE ) {
//                    code = getIsDownKey();
//            }
//
//
//
//                char[] cha = getChars((char)code,constraints);

                // only accept the old key if the code is different from the current, and there is something to accept
                old = !accepted && lastGotCode != code;
                lastGotCode = code;

                // if there is no letter to use
                if (cha == null) {
                    return 0;
                }

                if (code == '0' && Midlet.getPlatform() == Midlet.PLATFORM_SONY_ERICSSON) {
                    // Hack: Jane - For SE, the "+" is entered on "0", using a long key press...
                    acc = (pos == -1);
                    old = (pos >= 0);
                    return (pos >= 0) ? '0' : (pos==-1) ? '+' : 0;
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
                    if (pos==-2 && cha.length()!=1) {
                        return 0;
                    }
                    // else we are going to repeat a letter

                    // if we accepted the last letter then reset the position
                    if (accepted) {
                        pos=0;
                    }
                    // autoaccept if the length is 1
                    acc = cha.length()==1;
                    return cha.charAt(pos%cha.length());
                //}
            //}
        }

        private static final String CHARS_PHONE = "*+pw";
        private static final String CHARS_DECIMAL = ".-";
        private static final String CHARS_35 = " ";
        private static final String CHARS_42 = "*#";
        private static final String CHARS_48;
        private static final String CHARS_49 = ".,?!1@'-_():;&/%";
        private static final String CHARS_50 = "abc2";
        private static final String CHARS_51 = "def3";
        private static final String CHARS_52 = "ghi4";
        private static final String CHARS_53 = "jkl5";
        private static final String CHARS_54 = "mno6";
        private static final String CHARS_55 = "pqrs7";
        private static final String CHARS_56 = "tuv8";
        private static final String CHARS_57 = "wxyz9";

        static {
            if (Midlet.getPlatform() == Midlet.PLATFORM_SONY_ERICSSON) {
                CHARS_48 = "0+";
            }
            else {
                CHARS_48 = " 0";
            }
        };

        public static String getChars(char keycode,int constraints) {

            if ((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.NUMERIC) {

                if (keycode>='0' && keycode<='9') {
                    return String.valueOf(keycode);
                }
                return null;
            }

            if ((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.DECIMAL) {

                if (keycode>='0' && keycode<='9' || CHARS_DECIMAL.indexOf(keycode)!=-1) {
                    return String.valueOf(keycode);
                }
                if (keycode=='*'){
                    return String.valueOf(CHARS_DECIMAL.charAt(0));
                }
                if (keycode=='#'){
                    return String.valueOf(CHARS_DECIMAL.charAt(1));
                }
                return null;
            }

            if ((constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.PHONENUMBER) {

                if ((keycode>='0' && keycode<='9') || keycode=='#') {
                    return String.valueOf(keycode);
                }
                if (keycode=='*'){
                    return CHARS_PHONE;
                }
                if (CHARS_PHONE.indexOf(keycode)!=-1) {
                    return String.valueOf(keycode);
                }
                return null;
            }

            switch(keycode) {
                case '#': return CHARS_35;
                case '*': return CHARS_42;
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
                default: return String.valueOf(keycode);
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
         * it should though return the most recent of the down keys
         */
        public int getIsDownKey() {
               int res = 0;
               for (int c=0;c<isDownKeys.length;c++) {
                    if (isDownKeys[c] != 0) {
                        res = isDownKeys[c];
                        if (isDownKeys[c] > 0)
                        {
                            // Give priority to positive values (ascii).
                            break;
                        }
                    }
               }
               return res;
        }
        public int getIsDownAction() {
               int res = 0;
               for (int c=0;c<isDownKeys.length;c++) {
                    if (isDownKeys[c] != 0) {
                        int action = getKeyAction(isDownKeys[c]);
                        if (action != 0) {
                            res = action;
                        }
                    }
               }
               return res;
        }

        // on nokia emulators can throw when the key does not exist on the phone
        public String getKeyText(int keyCode) {
            try {
                return canvas.getKeyName(keyCode);
            }
            catch(Throwable ex) {
                return ex.getMessage();
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
                case 127: return KEY_DELETE;
                case '\r': return '\n'; // 13 -> 10

                // Sony-Ericsson phones - Volume keys
		// not sure about this
                //case -36: return canvas.getKeyCode(Canvas.UP);
                //case -37: return canvas.getKeyCode(Canvas.DOWN);

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
                if (isDownKeys[c] == 0 || isDownKeys[c] == keyCode) {
                    isDownKeys[c] = keyCode;
                    done=true;
                }
            }
            if (!done) {
                int[] newKeyDown = new int[isDownKeys.length+1];

                // Copy all data
                System.arraycopy(isDownKeys, 0, newKeyDown, 1, isDownKeys.length);
                newKeyDown[0] = keyCode;

                isDownKeys = newKeyDown;
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
