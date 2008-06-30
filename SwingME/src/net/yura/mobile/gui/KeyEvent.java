package net.yura.mobile.gui;

import javax.microedition.lcdui.Canvas;

public class KeyEvent {

    	//#ifdef polish.key.LeftSoftKey:defined
	//#= public static final int KEY_SOFTKEY1 = ${polish.key.LeftSoftKey};
	//#else
		public static final int KEY_SOFTKEY1 = -6;	//Default Softkey left value
	//#endif

	//#ifdef polish.key.RightSoftKey:defined
	//#= public static final int KEY_SOFTKEY2 = ${polish.key.RightSoftKey};
	//#else
		public static final int KEY_SOFTKEY2 = -7;	//Default Softkey right value
	//#endif

	//#ifdef polish.key.ClearKey:defined
	//#= public static final int KEY_CLEAR = ${polish.key.ClearKey};
//# 	public static final boolean hasclearkey=true;
	//#else
                public static final int KEY_CLEAR = -8;     //Default Clear value
	//#= public static final boolean hasclearkey=false;
	//#endif

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

            // if we are in number mode we dont want ANY of the other chars! so we either return a number or nothing
            if (((mode & javax.microedition.lcdui.TextField.CONSTRAINT_MASK) == javax.microedition.lcdui.TextField.NUMERIC)) {
                
                old = false;
                // in this mode there will never be a old char
                
                if (code>=48 && code<=57) {
                    acc = true;
                    return (char)code;
                }
                return 0;
            }
            else {
                
                char[] cha = getChars(code,mode);

                // only accept the old key if the code is different from the current, and there is something to accept
                old = !accepted && lastGotCode != code;
                lastGotCode = code;
                
                // this means we are here coz the user is holding down the button
                if (pos==-1) {
                
                    acc = true;
                    return (char)code;
                }
                else {

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
                }
            }
        }
        
        private static final char[] CHARS_42 = new char[] {'*'};
        private static final char[] CHARS_35 = new char[] {'#'};
        private static final char[] CHARS_48 = new char[] {' ','0'};
        private static final char[] CHARS_49 = new char[] {'.',',','?','!','1'};
        private static final char[] CHARS_50 = new char[] {'a','b','c'};
        private static final char[] CHARS_51 = new char[] {'d','e','f'};
        private static final char[] CHARS_52 = new char[] {'g','h','i'};
        private static final char[] CHARS_53 = new char[] {'j','k','l'};
        private static final char[] CHARS_54 = new char[] {'m','n','o'};
        private static final char[] CHARS_55 = new char[] {'p','q','r','s'};
        private static final char[] CHARS_56 = new char[] {'t','u','v'};
        private static final char[] CHARS_57 = new char[] {'w','x','y','z'};
        
        public static char[] getChars(int keycode,int mode) {
            
            switch(keycode) {
                case 42: return CHARS_42;
                case 35: return CHARS_35;
                case 48: return CHARS_48;
                case 49: return CHARS_49;
                case 50: return CHARS_50;
                case 51: return CHARS_51;
                case 52: return CHARS_52;
                case 53: return CHARS_53;
                case 54: return CHARS_54;
                case 55: return CHARS_55;
                case 56: return CHARS_56;
                case 57: return CHARS_57;
                default: return new char[] {(char)keycode};
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
    

    public String getKeyText(int keyCode) {
        return canvas.getKeyName(keyCode);
    }
    public int getKeyAction(int code) {
        return canvas.getGameAction(code);
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
            return justPressedKey!=0 && canvas.getGameAction(justPressedKey) == action;
        }

        public boolean justReleasedAction(int action) {
            return justReleasedKey!=0 && canvas.getGameAction(justReleasedKey) == action;
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
            
                // reset to 0 if we were holding the key down
                if (pos<0) {
                    pos=0;
                }
            
            	justPressedKey = 0;
		justReleasedKey = keyCode;
                removeKeyDown(keyCode);
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
