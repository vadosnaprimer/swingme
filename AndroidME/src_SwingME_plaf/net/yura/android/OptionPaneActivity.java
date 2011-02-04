package net.yura.android;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.logging.Logger;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;

public class OptionPaneActivity extends Activity implements OnCancelListener, OnClickListener {


    private static final int[] BUTTON_TYPE = {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEUTRAL, DialogInterface.BUTTON_NEGATIVE};

    private AlertDialog alertDialog;
    private OptionPane optionPaneWrapper;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Midlet midlet = Midlet.getMidlet();
            String url = getIntent().getData().toString();
            optionPaneWrapper = (OptionPane) midlet.retrievePlatformRequestParam(url);

            if (alertDialog == null) {
                alertDialog = new AlertDialog.Builder(this).create();
            }

            alertDialog.setTitle(optionPaneWrapper.getTitle());

            alertDialog.setCancelable(isCancelable());

            String msg = (String) optionPaneWrapper.getMessage();

            CharSequence alertText = (msg.startsWith("<html>")) ? Html.fromHtml(msg) : msg;
            alertDialog.setMessage(alertText);

            int msgType = optionPaneWrapper.getMessageType();
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

            String[] buttonsText = getButtonsText();

            int buttonsSize = Math.min(buttonsText.length, BUTTON_TYPE.length);

            for (int i = 0; i < buttonsSize; i++) {
                if (buttonsText[i] != null) {
                    alertDialog.setButton(BUTTON_TYPE[i], buttonsText[i], this);
                }
            }

            alertDialog.setOnCancelListener(this);
            alertDialog.show();
        } catch (Throwable ex) {
            //#debug warn
            Logger.warn(ex);

            finish();
        }
    }

    // Implements OnCancelListener
    public void onCancel(DialogInterface arg0) {

        Button[] buttons = optionPaneWrapper.getOptions();
        for (int i = 0; i < buttons.length; i++) {
            int mnemonic = buttons[i].getMnemonic();
            if (mnemonic == KeyEvent.KEY_END || mnemonic == KeyEvent.KEY_SOFTKEY2) {
                buttons[i].fireActionPerformed();
                break;
            }
        }

        finish();
    }

    // Override
    protected void onDestroy() {
        super.onDestroy();

        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    // ImplementsOnClickListener
    public void onClick(DialogInterface dialog, int which) {

        for (int i = 0; i < BUTTON_TYPE.length; i++) {
            if (BUTTON_TYPE[i] == which) {
                alertDialog.dismiss();

                try {
                    Button[] buttons = optionPaneWrapper.getOptions();
                    ActionListener al = optionPaneWrapper.getActionListener();
                    if (al!=null) {
                        al.actionPerformed(buttons[i].getActionCommand());
                    }
                } catch (Throwable e) {
                }

                finish();
                break;
            }
        }
    }

    private boolean isCancelable() {
        Button[] buttons = optionPaneWrapper.getOptions();
        for (int i = 0; i < buttons.length; i++) {
            int mnemonic = buttons[i].getMnemonic();
            if (mnemonic == KeyEvent.KEY_END || mnemonic == KeyEvent.KEY_SOFTKEY2) {
                return true;
            }
        }

        return false;
    }

    private String[] getButtonsText() {
        Button[] buttons = optionPaneWrapper.getOptions();
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
