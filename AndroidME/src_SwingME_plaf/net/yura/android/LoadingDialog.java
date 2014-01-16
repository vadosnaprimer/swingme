package net.yura.android;

import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Url;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * really simple Activity to show and hide a loading screen
 */
public class LoadingDialog extends Activity implements Runnable {

    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_CANCELLABLE = "cancellable";
    public static final String PARAM_COMMAND = "command";

    private static ProgressDialog dialog;

    private String action;
    private String loadingMessage;
    private boolean cancellable;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String command;
        if (getIntent().getData() != null) {
            Url url = new Url( getIntent().getData().toString() );
            loadingMessage = url.getQueryParameter(PARAM_MESSAGE);
            cancellable = Boolean.parseBoolean(url.getQueryParameter(PARAM_CANCELLABLE));
            command = url.getQueryParameter(PARAM_COMMAND);
        } else {
            loadingMessage = getIntent().getStringExtra(PARAM_MESSAGE);
            cancellable = getIntent().getBooleanExtra(PARAM_CANCELLABLE, false);
            command = getIntent().getStringExtra(PARAM_COMMAND);
        }
        if (command == null || "".equals(command)) {
            command = "setup";
        }
        runAction(command,0);
        
        finish();
    }
    

//    public void show(String loadingMessage) {
//        this.loadingMessage = loadingMessage;
//        runAction("setup", 0);
//    }

//    public void hide() {
//        runAction("hide", 250);
//    }

    public void run() {
        // NOTE: Because this runs with a delay, by the time we run, it is
        // possible that we no longer have a valid activity. If this happens,
        // we may get a "BadTokenException: Unable to add window"
        // see http://code.google.com/p/android/issues/detail?id=3953
        // We just ignore this, by wrapping on a try/catch

        try {
            if ("setup".equals(action)) {
                setWindowNotTouchable(true);
                
                if (dialog == null) {
                    dialog = new ProgressDialog(AndroidMeActivity.DEFAULT_ACTIVITY);
                    dialog.setIndeterminate(true);
                }

                dialog.setCancelable(cancellable);
                dialog.setMessage(loadingMessage);
                
                runAction("show", 250);
            }
            else if ("show".equals(action)) {

                if (dialog!=null) {
                    dialog.show();
                }

            }
            else {
                setWindowNotTouchable(false);
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        }
        catch (Throwable e) {
            //#debug debug
            Logger.warn(e);
        }
    }

    private void runAction(String action, int delay) {
        this.action = action;
        AndroidMeApp.getIntance().invokeLater(this, delay);
    }

    private static void setWindowNotTouchable(boolean isNotTouchable) {
        try {
            int mask = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            int flag = isNotTouchable ? mask : 0;
            AndroidMeActivity.DEFAULT_ACTIVITY.getWindow().setFlags(flag, mask);
        } catch (Throwable e) {
           //#debug debug
            Logger.warn(e);
        }
    }
}
