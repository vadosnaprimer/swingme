package net.yura.android;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import net.yura.mobile.logging.Logger;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidMeActivity extends Activity implements OnItemClickListener {

    private View defaultView;
    private View waitingView;
    private long backgroundTime;

    public static Activity DEFAULT_ACTIVITY;



    @Override
    final protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (DEFAULT_ACTIVITY != null) {
            // This can only run as single instance!!
            // Forward the intent that started us, and then die.
            ((AndroidMeActivity)DEFAULT_ACTIVITY).onNewIntent(getIntent());
            super.finish();
            return;
        }

        DEFAULT_ACTIVITY = this;

        onSingleCreate();
    }

    protected void onSingleCreate() {
        showWaitingView();

        Vector<String[]> jadMidlets = AndroidMeApp.getJadMidlets();

        if (jadMidlets.size() == 1) {
            // Start MIDlet
            new Thread() {
                public void run() {
//                    waitForDefaultView(); TODO:!!!!
                    AndroidMeApp.startMIDlet();
                }
            }.start();
        }
        else if (jadMidlets.size() > 1) {
            ListView listView = new ListView(AndroidMeActivity.this);
            listView.setOnItemClickListener(AndroidMeActivity.this);

            String[] listValues = new String[jadMidlets.size()];
            for (int i = 0; i < jadMidlets.size(); i++) {
                listValues[i] = jadMidlets.elementAt(i)[0];
            }

            listView.setAdapter(new ArrayAdapter<String>(AndroidMeActivity.this, android.R.layout.simple_list_item_1, listValues));

            showContentView(listView);
        }
        else {
            throw new RuntimeException("No jad file found! please provide a jad file with a Midlet in the android assets folder");
        }

//        PrintStream log = new PrintStream(new LogOutputStream("AndroidMe"));
//        System.setErr(log);
//        System.setOut(log);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Eliminates colour banding
        Window window = getWindow();

        if (Build.VERSION.SDK_INT > 4) {
            window.setFormat(PixelFormat.RGBX_8888);
        }
        else {
            // on the 1.6 emulator if you use RGBX_8888 it gives a white blank screen when starting the app
            window.setFormat(PixelFormat.RGBA_8888);
        }

    }

    //Override
    protected void onStop() {
        super.onStop();
        backgroundTime = System.currentTimeMillis();
    }

    //Override
    protected void onStart() {
        super.onStart();
        backgroundTime = 0L;

        hardKeyboardHidden = getResources().getConfiguration().hardKeyboardHidden;

    }

    public long getInBackgroundTime() {
        return (backgroundTime <= 0) ? 0 : (System.currentTimeMillis() - backgroundTime);
    }

    private void showContentView(final View view) {
    	runOnUiThread(new Runnable() {
            public void run() {
                if (defaultView != view) {
                    setContentView(view);
                }
            }
        });
        this.defaultView = view;
    }

    private long start = System.currentTimeMillis();
    @Override
    public void setContentView(View view) {

    	//#debug debug
    	Logger.debug("[AndroidMeActivity] setContentView view: "+AndroidRemoteTest.toString(view) +" "+ (System.currentTimeMillis() - start) );

    	super.setContentView(view);

        //#debug debug
    	Logger.debug("[AndroidMeActivity] setContentView Decor: "+AndroidRemoteTest.toString( getWindow().getDecorView() ) );
    }

    private void showWaitingView() {
        if (waitingView == null) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            textView.setDrawingCacheBackgroundColor(0xFF000000);
            textView.setBackgroundColor(0xFF000000);
            textView.setText("SwingME loading...");

            waitingView = textView;
        }

        showContentView(waitingView);
    }

    private void waitForDefaultView() {
        while (defaultView.getWidth() == 0) {
            try {
                System.out.println("Waiting for view...");
                Thread.sleep(100);
            } catch (Exception ex) {
                Logger.warn(ex);
            }
        }
    }

    private void startMIDlet(final String midletClassName) {
        showWaitingView();

        new Thread() {
            public void run() {
                try {
                    waitForDefaultView();

                    AndroidMeApp.createMIDlet(midletClassName);
                    AndroidMeApp.startMIDlet();
                } catch (Throwable ex) {
                    Logger.warn(ex);
                }
            }
        }.start();
    }


//    private void closeMIDlet() {
//        try {
//            if (midlet != null) {
//                midlet.doDestroyApp(true);
//            }
//        }
//        catch (Throwable ex) {
//        }
//
//        midlet = null;
//    }
//
//    //Override
//    public void finish() {
//        // Ignore multiple calls to finish()
//        if (!closed) {
//            closed = true;
//            closeMIDlet();
//            super.finish();
//        }
//    }


    //Override
    protected void onDestroy() {
        super.onDestroy();

        if (DEFAULT_ACTIVITY == this) {
//            closeMIDlet();

            MIDlet midlet = AndroidMeApp.getMIDlet();
            if (midlet!=null) {
                Display.getDisplay( midlet ).killUI();
            }

            DEFAULT_ACTIVITY = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MIDlet midlet = AndroidMeApp.getMIDlet();
        if (midlet!=null) {
            Displayable current = Display.getDisplay( midlet ).getCurrent();
            if (current != null) {
                return ((Canvas)current).onCreateOptionsMenu(menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MIDlet midlet = AndroidMeApp.getMIDlet();
        if (midlet!=null) {
            Displayable current = Display.getDisplay( midlet ).getCurrent();
            if (current != null) {
                return ((Canvas)current).onPrepareOptionsMenu(menu);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MIDlet midlet = AndroidMeApp.getMIDlet();
        if (midlet!=null) {
            Displayable current = Display.getDisplay( midlet ).getCurrent();
            if (current != null) {
                return ((Canvas)current).onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public int getScreenHeight() {
        return this.defaultView.getHeight();
    }

    public int getScreenWidth() {
        return this.defaultView.getWidth();
    }

    //Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (position >= 0) {
            Vector<String[]> jadMidlets = AndroidMeApp.getJadMidlets();
            String midletClassName = jadMidlets.elementAt(position)[2];

            startMIDlet(midletClassName);
        }
    }

    //Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Object result = null;
            if(data != null && data.getExtras() != null) {
                result = data.getExtras().get("data");
            }

            MIDlet midlet = AndroidMeApp.getMIDlet();
            midlet.onResult(requestCode, resultCode, result);
        } catch (Throwable e) {
            //#debug info
            Logger.warn(e);
        }
    }

    private int hardKeyboardHidden;

    /**
     * @see javax.microedition.lcdui.Canvas.CanvasView#onCreateInputConnection(android.view.inputmethod.EditorInfo)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {

	        // Checks the orientation of the screen
	        //if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        //}
	        //else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        //}

	        // Checks whether a hardware keyboard is available
	        // this is part of the HTC Hack

	        if ("HTC".equals(Build.MANUFACTURER)) {
		        if ( hardKeyboardHidden != newConfig.hardKeyboardHidden ) {
		        	hardKeyboardHidden = newConfig.hardKeyboardHidden;
		        	((Canvas)Display.getDisplay(AndroidMeApp.getMIDlet()).getCurrent()).restartInput();
		        }
	        }
        }
        catch(Throwable th) {
        	//#debug debug
        	Logger.warn(th);
        }
    }
}
