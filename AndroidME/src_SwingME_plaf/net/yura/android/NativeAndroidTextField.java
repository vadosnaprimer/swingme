package net.yura.android;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.lcdui.TextBox;

import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import javax.microedition.lcdui.Canvas.InputHelper;

public class NativeAndroidTextField implements InputHelper,ChangeListener {

    private Component textField; // this is the SwingME component
    private EditText editText; // this is the J2ME component
    private TextBox textBox; // this is the Android Component
    
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
        
        editText = new EditText( AndroidMeActivity.DEFAULT_ACTIVITY ) {

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
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
            public boolean onKeyLongPress(int keyCode, KeyEvent event) {
                boolean use = super.onKeyLongPress(keyCode, event);
                if (!use) return view.onKeyLongPress(keyCode, event);
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
            
        };
        
        editText.setText(textBox.getString());
        

        
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
    
    
    int tx=-100,ty=-100; // some random none valid numbers
    public void onDraw() {

System.out.println("[NativeAndroidTextField] ##################### layout");
        
        // this is the only swing me specific method

        
        int x = textField.getXOnScreen();
        int y = textField.getYOnScreen();
        
        // if the location has changed since last paint we need to move the component
        if (x!=tx || y!=ty) {
        
            Border insets = textField.getInsets();
            int sx = x-insets.getLeft();
            int sy = y-insets.getTop();

            editText.layout(sx, sy, sx+textField.getWidthWithBorder(), sy+textField.getHeightWithBorder() );
        
            tx=x;
            ty=y;
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
        
        // this is some fucked up crack induced crazy shit that android makes you do to simply remove a view
        group.post(new Runnable() {
            @Override
            public void run() {
                for (View child:remove) {
                    group.removeView(child);
                }
            }
        });

        
        
        
        // set text back
        
        textBox.setString(editText.getText().toString());
        
        // fire ok button
        // this will set the text back into SwingME, and then close the native input
        // we do NOT want to do this as this closes the keyboard, and we do not want to do that
        //textBox.fireCommand(javax.microedition.lcdui.Command.OK);
        
        // just set the text back on the text component
        ((TextComponent)textField).setText( textBox.getString() );

    }

}
