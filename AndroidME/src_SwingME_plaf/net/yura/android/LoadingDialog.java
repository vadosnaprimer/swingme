package net.yura.android;

import net.yura.android.AndroidMeActivity;
import net.yura.android.AndroidMeApp;
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

    private static ProgressDialog dialog;

    private String action;
    private String loadingMessage;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Url url = new Url( getIntent().getData().toString() );
        this.loadingMessage = url.getQueryParameter("message");
        String command = url.getQueryParameter("command");
        runAction(("".equals(command)||command==null)?"setup":command,0);
        
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
                    dialog.setCancelable(false);
                }

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
