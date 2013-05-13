package net.yura.android;

import java.util.Vector;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.text.Html;
import android.widget.EditText;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.logging.Logger;

public class AlertOptionPane extends OptionPane implements OnCancelListener, OnClickListener {

    private static final int[] BUTTON_TYPE = {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEUTRAL, DialogInterface.BUTTON_NEGATIVE};

    private AlertDialog alertDialog;
    private EditText input;

    @Override
    public void setVisible(boolean b) {

        if (isNativeSupported()) {
            if (b) {
                try {
                    openNativeAlert();
                }
                catch(Exception ex) {
                    //#debug debug
                    Logger.warn(ex);

                    //#debug warn
                    Logger.warn("failed to start OptionPaneActivity, falling back to SwingME OptionPane");
                    super.setVisible(b);
                }
            }
            else {
                //#debug warn
                Logger.warn("this should never happen!!!! setVisible(false) in AndroidOptionPane");
                super.setVisible(b); // in case something went wrong with opening the native OptionPane, we want to be able to close it
            }
        }
        else {
            super.setVisible(b);
        }
    }

    private Object[] getMessageAsArray() {
        Object newMessage = getMessage();
        Object[] messages;
        if (newMessage instanceof Object[]) {
            messages = (Object[])newMessage;
        }
        else if (newMessage instanceof Vector) {
            messages = ((Vector)newMessage).toArray();
        }
        else if (newMessage==null) {
            messages = new Object[0];
        }
        else {
            messages = new Object[] {newMessage};
        }
        return messages;
    }

    private boolean isNativeSupported() {
        Object[] messages = getMessageAsArray();
        boolean string=false;
        boolean textField=false;
        for (int c=0;c<messages.length;c++) {
            if (messages[c] instanceof String) {
                if (string) return false;
                string=true;
            }
            else if (messages[c] instanceof TextField) {
                if (textField) return false;
                textField=true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    private void openNativeAlert() {

        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder( AndroidMeActivity.DEFAULT_ACTIVITY ).create();
        }

        alertDialog.setTitle(getTitle());
        alertDialog.setCancelable(isCancelable());

        Object[] messages = getMessageAsArray();
        for (int c=0;c<messages.length;c++) {
            if (messages[c] instanceof String) {
                String msg = (String)messages[c];
                CharSequence alertText = (msg.startsWith("<html>")) ? Html.fromHtml(msg) : msg;
                alertDialog.setMessage(alertText);
            }
            else if (messages[c] instanceof TextField) {
                TextField tf = (TextField)messages[c];
                input = new EditText( alertDialog.getContext() );
                input.setText( tf.getText() );
                NativeAndroidTextField.setNativeSettings(input, tf, tf.getConstraints(), tf.initialInputMode );
                alertDialog.setView(input);
            }
        }

        int msgType = getMessageType();
        int iconId;
        switch (msgType) {
            case OptionPane.INFORMATION_MESSAGE:
                iconId = android.R.drawable.ic_dialog_info;
                break;
            case OptionPane.ERROR_MESSAGE:
            case OptionPane.WARNING_MESSAGE:
                iconId = android.R.drawable.ic_dialog_alert;
                break;
            // TODO:
            case OptionPane.QUESTION_MESSAGE:
            case OptionPane.PLAIN_MESSAGE:
            default:
                iconId = -1;
                break;
        }

        if (iconId > 0) {
            alertDialog.setIcon(iconId);
        }
        
        // this makes the icon the correct color, BUT only works on honeycomb+ and ONLY for alert ("!") icon
        //alertDialog.setIconAttribute(android.R.attr.alertDialogIcon);

        String[] buttonsText = getButtonsText();

        int buttonsSize = Math.min(buttonsText.length, BUTTON_TYPE.length);

        for (int i = 0; i < buttonsSize; i++) {
            if (buttonsText[i] != null) {
                alertDialog.setButton(BUTTON_TYPE[i], buttonsText[i], this);
            }
        }

        alertDialog.setOnCancelListener(this);
        alertDialog.show();

    }

    private void finish() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
            input = null;
        }
    }

    // Implements OnCancelListener
    public void onCancel(DialogInterface arg0) {

        Button[] buttons = getOptions();
        for (int i = 0; i < buttons.length; i++) {
            int mnemonic = buttons[i].getMnemonic();
            if (mnemonic == KeyEvent.KEY_END || mnemonic == KeyEvent.KEY_SOFTKEY2) {
                fireActionPerformed(buttons[i]);
                break;
            }
        }

        finish();
    }

    // ImplementsOnClickListener
    public void onClick(DialogInterface dialog, int which) {

        for (int i = 0; i < BUTTON_TYPE.length; i++) {
            if (BUTTON_TYPE[i] == which) {
                //alertDialog.dismiss();

                Button[] buttons = getOptions();
                fireActionPerformed(buttons[i]);

                break;
            }
        }

        finish();
    }

    private void fireActionPerformed(Button b) {

        if (input != null) {
            Object[] messages = getMessageAsArray();
            for (int c=0;c<messages.length;c++) {
                if (messages[c] instanceof TextField) {
                    TextField tf = (TextField)messages[c];
                    tf.setText( input.getText().toString() );
                }
            }
        }

        try {
            ActionListener al = getActionListener();
            if (al!=null) {
                al.actionPerformed(b.getActionCommand());
            }
        }
        catch (Throwable e) {
            //#debug debug
            Logger.warn(e);
        }
    }

    private boolean isCancelable() {
        Button[] buttons = getOptions();
        for (int i = 0; i < buttons.length; i++) {
            int mnemonic = buttons[i].getMnemonic();
            if (mnemonic == KeyEvent.KEY_END || mnemonic == KeyEvent.KEY_SOFTKEY2) {
                return true;
            }
        }

        return false;
    }

    private String[] getButtonsText() {
        Button[] buttons = getOptions();
// The index of the button will be used later to call onButtonPressed(), so we need to return the same size...
//                 Vector v = new Vector();
//
//                 for (int i = 0; i < buttons.length; i++) {
//                     // Don't display the "cancel"/"Back" button
//                     if (buttons[i].getMnemonic() != KeyEvent.KEY_END) {
//                         v.addElement(buttons[i].getText());
//                     }
//                 }
//
//                 String[] buttonsText = new String[v.size()];
//                 v.copyInto(buttonsText);
//
        String[] buttonsText = new String[buttons.length];

        for (int i = 0; i < buttonsText.length; i++) {
            // Don't display the "cancel"/"Back" button
            if (buttons[i].getMnemonic() != KeyEvent.KEY_END) {
                buttonsText[i] = buttons[i] .getText();
            }
        }

        return buttonsText;
    }
}
