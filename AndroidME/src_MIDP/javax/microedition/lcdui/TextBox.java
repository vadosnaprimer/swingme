package javax.microedition.lcdui;

import java.util.ArrayList;

import javax.microedition.lcdui.Canvas.InputHelper;
import javax.microedition.midlet.MIDlet;

import net.yura.android.AndroidMeActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class TextBox extends Screen {

    public static Class<? extends InputHelper> inputHelperClass = TextBoxView.class;

    //#debug debug
    private final static boolean debug = false;

    private String text;
    private int maxSize;
    private int constraints;
    private Canvas.CanvasView currentCanvasView;
    private InputHelper textBoxView;

    public TextBox(String title, String text, int maxSize, int constraints) {
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;

        // Hack: Current view could change...
        MIDlet midlet = AndroidMeActivity.DEFAULT_ACTIVITY.getMIDlet();

        View oldView = Display.getDisplay(midlet).getCurrent().getView();
        this.currentCanvasView = getCanvasView(oldView);

        setTitle(title);

        try {
            textBoxView = inputHelperClass.newInstance();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Canvas.CanvasView getCanvasView() {
        return currentCanvasView;
    }

    private Canvas.CanvasView getCanvasView(View view) {

        if (view == null || view instanceof Canvas.CanvasView) {
            return (Canvas.CanvasView) view;
        }

        ArrayList<View> viewList = view.getFocusables(View.FOCUSABLES_ALL);
        for (View childView : viewList) {
            if (childView != view) {
                Canvas.CanvasView childCanvasView = getCanvasView(childView);
                if (childCanvasView != null) {
                    return childCanvasView;
                }
            }
        }

        return null;
    }

    @Override
    public View getView() {
        return null;
    }

    /**
     * this is called so start the text entry by AndroidME
     */
    @Override
    public void initDisplayable(MIDlet midlet) {

        // we need to call this so the keyboard is opened
        if (currentCanvasView != null) {
            currentCanvasView.setTextInputView(textBoxView);
        }

        // need to call this after we bring the keyboard up, or that does not work
        textBoxView.start(this);

    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getString() {
        return this.text;
    }

    public void setString(String text) {
        this.text = text;
    }

    public int getConstraints() {
        return this.constraints;
    }

    public void setConstraints(int constraints) {
        this.constraints = constraints;
    }



    public void fireCommand(int type) {
        java.util.List<Command> commands = getCommands();
        for (Command command:commands) {
            if (command.getCommandType()==type) {
                CommandListener cl = getCommandListener();
                if (cl!=null) {
                    cl.commandAction(command, this);
                }
                // we have found a command we can fire, now we can exit
                return;
            }
        }
        //#debug debug
        System.out.println("[TextBox] NO BUTTON FOUND! "+type);
    }

    public static int getInputType(int constraints) {

            int inputType;

            switch (constraints & TextField.CONSTRAINT_MASK) {
				case TextField.NUMERIC:
				case TextField.DECIMAL:
					inputType = EditorInfo.TYPE_CLASS_NUMBER;
					break;
				case TextField.PHONENUMBER:
					inputType = EditorInfo.TYPE_CLASS_PHONE;
					break;
				case TextField.EMAILADDR:
					inputType = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
					break;
				case TextField.URL:
					inputType = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_URI;
					break;
				default: // TextField.ANY:
					inputType = EditorInfo.TYPE_CLASS_TEXT;
					break;
			}

            if ((constraints & TextField.PASSWORD) > 0) {
                inputType = inputType | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
            }
            if ((constraints & TextField.INITIAL_CAPS_SENTENCE) > 0) {
                inputType = inputType | EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
            if ((constraints & TextField.INITIAL_CAPS_WORD) > 0) {
                inputType = inputType | EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS;
            }

            return inputType;
    }

    private static CharSequence composingText = "";

    public static class TextBoxView implements InputHelper,InputConnection {
        private CharSequence textBeforeCursor = " ";

        private TextBox textBox;

        public TextBoxView() {
        }

        public void start(TextBox tb) {

            textBox = tb;

            // open the keyboard
            textBox.currentCanvasView.showNativeTextInput();

        }
        public void onDraw() { }
        public void onLayout() { }

        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {


            // HACK/WORK-AROUND for restartInput(): Sony Ericsson
            // InputMethodManager.restartInput() method is buggy and does not clean the composing text.
            // Changing the focus to another view, and then back to the
            // canvas fixes the problem on SE Phones, but breaks Samsung's (Galaxy S, etc).
            // Making composingText member static will keep the composing text from older
            // TextBox's, in case restartInput() doesn't clean it. but this does not work
            // if you move the caret to the middle of a line on a motorola after typing text in another box
            if (!"Sony Ericsson".equals(Build.MANUFACTURER)) {
            	composingText = "";
            }
            // END HACK

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] onCreateInputConnection " + this+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            EditText editText = new EditText( AndroidMeActivity.DEFAULT_ACTIVITY );
            editText.onCreateInputConnection(outAttrs);

            outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;

            int inputType= getInputType(textBox.constraints);

            outAttrs.inputType = inputType;

            outAttrs.initialCapsMode = getCursorCapsMode(inputType);

            return this;
        }

        // this does not change anything on any phone tested so far
        @Override
        public boolean onCheckIsTextEditor() {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] onCheckIsTextEditor " + this+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

        	return true;
        }

        //Override
        public boolean beginBatchEdit() {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] beginBatchEdit composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return true;
        }

        //Override
        public boolean endBatchEdit() {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] endBatchEdit composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return true;
        }

        //Override
        public boolean clearMetaKeyStates(int states) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] clearMetaKeyStates "+states+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

        	return true;
        }

        //Override
        public boolean commitCompletion(CompletionInfo text) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] commitCompletion >"+text+"< composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return true;
        }

        //Override
        public boolean commitText(CharSequence text, int newCursorPosition) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] commitText >"+text+"< "+newCursorPosition+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            sendComposingText(text, true);
            return true;
        }



        //Override
        public boolean deleteSurroundingText(int leftLength, int rightLength) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] deleteSurroundingText: leftLength = " + leftLength + " rightLength = " + rightLength+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            // Remove left by sending backspaces
            for (int i = 0; i < leftLength; i++) {
                sendText("\b");
            }
            // TODO: Do we need to remove the right (send delete chars)?

            return true;
        }


        //Override
        public boolean finishComposingText() {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] finishComposingText composingText: >"+composingText+"<");
        	}
        	//#enddebug

            sendComposingText(composingText, true);
            return true;
        }

        //Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] setComposingText: >" + text + "< "+newCursorPosition+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            sendComposingText(text, false);
            return true;
        }

        //Override
        public int getCursorCapsMode(int reqModes) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] getCursorCapsMode "+reqModes+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            int capitalize;
            if ((textBox.constraints & TextField.INITIAL_CAPS_SENTENCE) > 0) {

                capitalize = EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES;
            } else if ((textBox.constraints & TextField.INITIAL_CAPS_WORD) > 0) {

                capitalize = EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS;
            } else {
                capitalize = 0;
            }
            return capitalize;
        }

        //Override
        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] getExtractedText "+request+" "+flags+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            // WE CAN NOT CLEAR HERE, as next time setComposingText is called it will still have the full String
            //sendComposingText(composingText, true <- this can NOT be true );
            return null;
        }

        //Override
        public CharSequence getTextAfterCursor(int n, int flags) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] getTextAfterCursor "+n+" "+flags+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return " ";
        }

        //Override
        public CharSequence getTextBeforeCursor(int n, int flags) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] getTextBeforeCursor: " + textBeforeCursor+" "+n+" "+flags+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return textBeforeCursor;
        }

        //Override
        public boolean performEditorAction(int actionCode) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] performEditorAction "+actionCode+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            if(actionCode == EditorInfo.IME_ACTION_UNSPECIFIED) {
                // The "return" key has been pressed on the IME.
                sendText("\n");
            }
            return true;
        }

        //Override
        public boolean performContextMenuAction(int id) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] performContextMenuAction "+id+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return true;
        }

        //Override
        public boolean performPrivateCommand(String action, Bundle data) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] performPrivateCommand "+action+" "+data+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return true;
        }

        //Override
        public boolean sendKeyEvent(KeyEvent event) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] sendKeyEvent " + event+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            if (textBox.currentCanvasView != null) {
                int keyCode = event.getKeyCode();
                int action = event.getAction();

                if (action == KeyEvent.ACTION_DOWN) {
                    textBox.currentCanvasView.onKeyDown(keyCode, event);
                }
                else if (action == KeyEvent.ACTION_UP) {
                    textBox.currentCanvasView.onKeyUp(keyCode, event);
                }
            }
            return true;
        }

        //Override
        public boolean setSelection(int start, int end) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] setSelection "+start+" "+end+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            return true;
        }

        //Override
        public boolean reportFullscreenMode(boolean enabled) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView] reportFullscreenMode " +enabled+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            sendComposingText(composingText, true);
            return true;
        }

        private void sendText(CharSequence text) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView private] sendText: >" + text + "< composingText: >"+composingText+"<");
        	}
        	//#enddebug

            if (textBox.currentCanvasView != null) {
                textBox.currentCanvasView.sendText(text);
            }

            textBeforeCursor = text;
        }

        private void sendComposingText(CharSequence text, boolean done) {

        	//#mdebug debug
        	if (debug) {
        		System.out.println("[TextBoxView private] sendComposingText >"+text+"< "+done+" composingText: >"+composingText+"<");
        	}
        	//#enddebug

            // How many chars are the same?
            int nEqual = 0;
            for (int i = 0; i < text.length() && i < composingText.length(); i++) {
                if (text.charAt(i) != composingText.charAt(i)) {
                    break;
                }
                nEqual++;
            }

            // Send as many backspaces as the different chars
            for (int i = nEqual; i < composingText.length(); i++) {
                sendText("\b");
            }

            // Send the new chars
            for (int i = nEqual; i < text.length(); i++) {
                sendText(String.valueOf(text.charAt(i)));
            }

            composingText = done ? "" : text;
        }

        // Override API 11
        public boolean commitCorrection(android.view.inputmethod.CorrectionInfo arg0) {
            return true;
        }

        // Override API 11
        public CharSequence getSelectedText(int arg0) {
            return "";
        }

        // Override API 11
        public boolean setComposingRegion(int arg0, int arg1) {
            return false;
        }
    }
}
