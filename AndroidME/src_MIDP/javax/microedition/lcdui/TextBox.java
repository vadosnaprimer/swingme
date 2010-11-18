package javax.microedition.lcdui;

import java.util.ArrayList;

import javax.microedition.midlet.MIDlet;
import net.yura.android.AndroidMeActivity;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.EditText;


public class TextBox extends Screen {
    private String text;
    private int maxSize;
    private int constraints;
    private Canvas.CanvasView currentCanvasView;
    private TextBoxView textBoxView;

    public TextBox(String title, String text, int maxSize, int constraints) {
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;

        // Hack: Current view could change...
        MIDlet midlet = AndroidMeActivity.DEFAULT_ACTIVITY.getMIDlet();

        View oldView = Display.getDisplay(midlet).getCurrent().getView();
        this.currentCanvasView = getCanvasView(oldView);

        textBoxView = new TextBoxView(AndroidMeActivity.DEFAULT_ACTIVITY);
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

    @Override
    public void initDisplayable(MIDlet midlet) {
        if (currentCanvasView != null) {
            currentCanvasView.setInputConnectionView(textBoxView);
        }
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

    private class TextBoxView extends View implements InputConnection {

        CharSequence composingText = "";

        public TextBoxView(Context context) {
            super(context);
        }

        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {

            // System.out.println("TextBoxView: onCreateInputConnection " + this);

            EditText editText = new EditText(getContext());
            editText.onCreateInputConnection(outAttrs);

            outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;

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

            outAttrs.inputType = inputType;

            outAttrs.initialCapsMode = getCursorCapsMode(inputType);

            return this;
        }


        //Override
        public boolean beginBatchEdit() {
            // System.out.println("beginBatchEdit");
            return true;
        }

        //Override
        public boolean endBatchEdit() {
            // System.out.println("endBatchEdit");
            return true;
        }

        //Override
        public boolean clearMetaKeyStates(int states) {
            // System.out.println("clearMetaKeyStates");
            return true;
        }

        //Override
        public boolean commitCompletion(CompletionInfo text) {
            // System.out.println("commitCompletion");
            return true;
        }

        //Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            // System.out.println("commitText");
            sendComposingText(text, true);
            return true;
        }



        //Override
        public boolean deleteSurroundingText(int leftLength, int rightLength) {
            // System.out.println("deleteSurroundingText");
            return true;
        }


        //Override
        public boolean finishComposingText() {
            // System.out.println("finishComposingText");
            sendComposingText(composingText, true);
            return true;
        }

        //Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            // System.out.println("setComposingText: " + text);
            sendComposingText(text, false);
            return true;
        }

        //Override
        public int getCursorCapsMode(int reqModes) {
            // System.out.println("getCursorCapsMode");
            int capitalize;
            if ((constraints & TextField.INITIAL_CAPS_SENTENCE) > 0) {

                capitalize = EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES;
            } else if ((constraints & TextField.INITIAL_CAPS_WORD) > 0) {

                capitalize = EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS;
            } else {
                capitalize = 0;
            }
            return capitalize;
        }

        //Override
        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
            // System.out.println("getExtractedText");
            sendComposingText(composingText, true);
            return null;
        }

        //Override
        public CharSequence getTextAfterCursor(int n, int flags) {
            // System.out.println("getTextAfterCursor");
            return " ";
        }

        //Override
        public CharSequence getTextBeforeCursor(int n, int flags) {
            // System.out.println("getTextBeforeCursor");
            return " ";
        }

        //Override
        public boolean performEditorAction(int actionCode) {
            // System.out.println("performEditorAction");
            if(actionCode == EditorInfo.IME_ACTION_UNSPECIFIED) {
                // The "return" key has been pressed on the IME.
                sendText("\n");
            }
            return true;
        }

        //Override
        public boolean performContextMenuAction(int id) {
            // System.out.println("performContextMenuAction");
            return true;
        }

        //Override
        public boolean performPrivateCommand(String action, Bundle data) {
            // System.out.println("performPrivateCommand");
            return true;
        }

        //Override
        public boolean sendKeyEvent(KeyEvent event) {
            // System.out.println("sendKeyEvent");
            if (currentCanvasView != null) {
                int keyCode = event.getKeyCode();
                int action = event.getAction();

                if (action == KeyEvent.ACTION_DOWN) {
                    currentCanvasView.onKeyDown(keyCode, event);
                } else if (action == KeyEvent.ACTION_UP) {
                    currentCanvasView.onKeyUp(keyCode, event);
                }
            }
            return true;
        }

        //Override
        public boolean setSelection(int start, int end) {
            // System.out.println("setSelection");
            return true;
        }

        //Override
        public boolean reportFullscreenMode(boolean enabled) {
            // System.out.println("reportFullscreenMode");
            sendComposingText(composingText, true);
            return true;
        }

        private void sendText(CharSequence text) {
            // System.out.println("sendText: >" + text + "<");
            if (currentCanvasView != null) {
                currentCanvasView.sendText(text);
            }
        }

        private void sendComposingText(CharSequence text, boolean done) {

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
    }
}
