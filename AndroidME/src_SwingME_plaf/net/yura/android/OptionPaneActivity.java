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
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class OptionPaneActivity extends Activity implements OnCancelListener, OnClickListener, OnGlobalLayoutListener {


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

            if (msg!=null) {
                CharSequence alertText = (msg.startsWith("<html>")) ? Html.fromHtml(msg) : msg;
                alertDialog.setMessage(alertText);
            }

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

            fixButtonsHeight();
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
                fireActionPerformed(buttons[i]);
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

                Button[] buttons = optionPaneWrapper.getOptions();
                fireActionPerformed(buttons[i]);

                break;
            }
        }

        finish();
    }

    private void fireActionPerformed(Button b) {
        try {
            ActionListener al = optionPaneWrapper.getActionListener();
            if (al!=null) {
                al.actionPerformed(b.getActionCommand());
            }
        }
        catch (Throwable e) {
            //#debug debug
            e.printStackTrace();
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

    // ------------------- START HACK: dialog buttons height ----------------
    // See bug http://code.google.com/p/android/issues/detail?id=15246
    // The layout manager of Dialog does not insure all buttons
    // have the same height. To work we need to run on the UI thread,
    // so the buttons height is already calculated.

    private ViewTreeObserver treeObserver;
    private void fixButtonsHeight() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (treeObserver == null) {
                    for (int i = 0; i < BUTTON_TYPE.length; i++) {
                        android.widget.Button btn = alertDialog.getButton(BUTTON_TYPE[i]);
                        resetButtonSize(btn);

                        if (btn != null && treeObserver == null) {
                            treeObserver = btn.getRootView().getViewTreeObserver();
                            treeObserver.addOnGlobalLayoutListener(OptionPaneActivity.this);
                        }
                    }
                }
            }
        });
    }

    private void resetButtonSize(android.widget.Button btn) {
        if (btn != null) {
            btn.setMinLines(1);
            btn.setMaxLines(3);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        for (int i = 0; i < BUTTON_TYPE.length; i++) {
            resetButtonSize(alertDialog.getButton(BUTTON_TYPE[i]));
        }
    }

    @Override
    public void onGlobalLayout() {

        //#debug info
        System.out.println(">>>>> OptionPaneActivity: onGlobalLayout");

        int maxH = 0;
        for (int i = 0; i < BUTTON_TYPE.length; i++) {
            android.widget.Button btn = alertDialog.getButton(BUTTON_TYPE[i]);
            if (btn != null) {
                maxH = Math.max(btn.getHeight(), maxH);

                // Even the height of a multi-line is wrong in some phones, so
                // we make sure we have at least enough to show the text.
                int h = (btn.getLineCount() + 1) * btn.getLineHeight();
                maxH = Math.max(h, maxH);
            }
        }

        for (int i = 0; i < BUTTON_TYPE.length; i++) {
            android.widget.Button btn = alertDialog.getButton(BUTTON_TYPE[i]);
            if (btn != null && btn.getHeight() > 0 && btn.getHeight() != maxH) {
                btn.setHeight(maxH);
            }
        }
    }
    // ------------------- END HACK: dialog buttons height --------------------
}
