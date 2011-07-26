package net.yura.android;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.lcdui.TextBox;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Canvas.InputHelper;

/**
 * TODO:
 *      expand on multi-line text does not work
 *      when the theme is anything but the standard does not use it
 *      addCaretListener does not work
 *      addFocusListener does not work if there is already one set
 *      {@link TextComponent#changedUpdate(int, int) } is not called when text is entered
 * @author Yura Mamyrin
 */
public class NativeAndroidTextField implements InputHelper,ChangeListener {

    private EditText editText; // this is the Android Component
    private TextBox textBox; // this is the J2ME component
    private Component textField; // this is the SwingME component

    public NativeAndroidTextField() {

System.out.println("[NativeAndroidTextField] ##################### construct");

    }


    public boolean onCheckIsTextEditor() {
        return false;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return null;
    }

    public void start(TextBox textBox) {

        final View view = textBox.getCanvasView();

System.out.println("[NativeAndroidTextField] ##################### start");

        Window window = DesktopPane.getDesktopPane().getSelectedFrame();
        textField = window.getFocusOwner();

        this.textBox = textBox;

        editText = new NativeEditText(view);

        swing2android();

        boolean singleLine = (textField instanceof TextField);
        editText.setSingleLine( singleLine );
        int inputType = TextBox.getInputType( textBox.getConstraints() );
        if (!singleLine) {
            inputType = inputType | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;
        }
        editText.setInputType( inputType ); // this has to be done AFTER setSingleLine or passwords will break

        if (singleLine) { // actions only supported on single friend
            final TextField tf = (TextField)textField;
            if (tf.getActionListeners().length>0) {

                editText.setImeOptions( EditorInfo.IME_ACTION_DONE ); // DONE is returned by default (IME_ACTION_UNSPECIFIED), but we STILL have to set it, or getImeOptions will return 0

                editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == editText.getImeOptions() ) { // actionId is 6(IME_ACTION_DONE) is none is set. getImeOptions returns 0(IME_NULL) if none is set
                            fireActionPerformed(tf);
                        }
                        return true;
                    }
                });
            }
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                Object val = textField.getValue();
                if (val == null || val instanceof String) {
                    String text = (String)val;
                    if (!cs.equals(text)) {
                        textField.setValue(cs);
                    }
                }
                //#mdebug debug
                else {
                    System.err.println("[NativeAndroidTextField] ER1 UNKNOWN VALUE IN textField: "+val);
                    Thread.dumpStack();
                }
                //#enddebug
            }
            @Override
            public void afterTextChanged(Editable edtbl) {

            }
        });



        // this updates the position of the component
        onDraw();






        // add it to the parent
        ViewGroup group = (ViewGroup)view.getParent();
        int count = group.getChildCount();
        List<View> remove = new ArrayList<View>(1);
        for (int c=0;c<count;c++) {
            View child = group.getChildAt(c);
            if (child != view) {
                remove.add(child);
            }
        }
        if (remove.size()!=1 || remove.get(0)!=editText ) { // do not do a add remove if you are simply adding then removing the same textbox
            for (View child:remove) {
                group.removeView(child);
            }
            group.addView(editText);
        }


        // get focus
        editText.requestFocus();


        textField.addFocusListener(this);

    }


    int tx=-100,ty=-100,tw=-100,th=-100; // some random none valid numbers
    public void onDraw() {

        // this is the only swing me specific method

        int x = textField.getXOnScreen();
        int y = textField.getYOnScreen();
        int w = textField.getWidthWithBorder();
        int h = textField.getHeightWithBorder();

        // if the location has changed since last paint we need to move the component
        if (x!=tx || y!=ty || w!=tw || h!=th) {

System.out.println("[NativeAndroidTextField] ##################### layout");

            Border insets = textField.getInsets();
            int sx = x-insets.getLeft();
            int sy = y-insets.getTop();

            editText.layout(sx, sy, sx+w, sy+h );

            tx=x;
            ty=y;
            tw=w;
            th=h;
        }
    }

    @Override
    public void changeEvent(Component source, int num) {
        if (num==Component.FOCUS_LOST) {
            textField.removeFocusListener(this);
            close();
        }
    }

    public void close() {

System.out.println("[NativeAndroidTextField] ##################### close");

        View view = textBox.getCanvasView();

        view.requestFocus();

        // remove it

        final ViewGroup group = (ViewGroup)view.getParent();
        int count = group.getChildCount();
        final List<View> remove = new ArrayList<View>(1);
        for (int c=0;c<count;c++) {
            View child = group.getChildAt(c);
            if (child != view) {
                remove.add(child);
            }
        }


        // hide it from everything first, or it will be visible for the next paint
        for (View child:remove) {
           child.setVisibility( View.GONE );
        }


        // this is some fucked up crack induced crazy shit that android makes you do to simply remove a view
        group.post(new Runnable() {
            @Override
            public void run() {
                for (View child:remove) {
                    group.removeView(child);
                }
            }
        });


        android2swing();



    }

    void swing2android() {


        editText.setText(textBox.getString());


        int caret = ((TextComponent)textField).getCaretPosition();

        // HACK to put the caret at the start of the line if your text is longer then 0
        // http://groups.google.com/group/android-developers/browse_thread/thread/d1c64f4c23b3c83b
        if (caret==0 && editText.getText().length() > 0) {
            editText.setSelection(1);
            editText.extendSelection(0);
        }
        // END HACK

        editText.setSelection( caret );
    }

    void android2swing() {
        // set text back

        textBox.setString(editText.getText().toString());

        // fire ok button
        // this will set the text back into SwingME, and then close the native input
        // we do NOT want to do this as this closes the keyboard, and we do not want to do that
        //textBox.fireCommand(javax.microedition.lcdui.Command.OK);

        // just set the text back on the text component
        textField.setValue( textBox.getString() );

        ((TextComponent)textField).setCaretPosition( editText.getSelectionEnd() ); // in android no direct way to get the caret
    }



    private static void fireActionPerformed(TextField tf) {
        final ActionListener[] als = tf.getActionListeners();
        for (ActionListener al:als) {
            al.actionPerformed( tf.getActionCommand() );
        }
    }



    class NativeEditText extends EditText {

        View view;

        /*
        // THIS DOES NOT WORK ON HTC DESIRE, does lots of crazy jumping around as soon as you start to type
        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                InputConnection in = super.onCreateInputConnection(outAttrs);

                // do not expand out the UI
                outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;

                return in;
        }
        */

        public NativeEditText(View view) {
            super( AndroidMeActivity.DEFAULT_ACTIVITY );
            this.view = view;
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {

        	//YURA: THIS CREATED A COMPILATION ERROR
            int swingMeCode = 0; //Canvas.getKeyCode(event.getKeyCode());

            Window win = textField.getWindow();
            Button b = win.findMnemonicButton(swingMeCode);
            if (b!=null) {
                android2swing(); // send text to swingME
                b.fireActionPerformed();

                // get the text from swingME to J2ME
                Object val = textField.getValue();
                if (val instanceof String) {
                    textBox.setString( (String)val );
                }
                //#mdebug debug
                else {
                    System.err.println("[NativeAndroidTextField] ER2 UNKNOWN VALUE IN textField: "+val);
                    Thread.dumpStack();
                }
                //#enddebug

                swing2android(); // set the text back into the android editText
                return true;
            }

            boolean use = super.onKeyDown(keyCode, event);
            if (!use) return view.onKeyDown(keyCode, event);
            return true;
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            boolean use = super.onKeyUp(keyCode, event);
            if (!use) return view.onKeyUp(keyCode, event);
            return true;
        }

        @Override
        public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
            boolean use = super.onKeyMultiple(keyCode, repeatCount, event);
            if (!use) return view.onKeyMultiple(keyCode, repeatCount, event);
            return true;
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            boolean use = super.onKeyPreIme(keyCode, event);
            if (!use) return view.onKeyPreIme(keyCode, event);
            return true;
        }

        @Override
        public boolean onKeyShortcut(int keyCode, KeyEvent event) {
            boolean use = super.onKeyShortcut(keyCode, event);
            if (!use) return view.onKeyShortcut(keyCode, event);
            return true;
        }

        /* need new version of sdk for this
        @Override
        public boolean onKeyLongPress(int keyCode, KeyEvent event) {
            boolean use = super.onKeyLongPress(keyCode, event);
            if (!use) return view.onKeyLongPress(keyCode, event);
            return true;
        }
        */

    }




}
