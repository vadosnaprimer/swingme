package net.yura.android;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
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

    public static AndroidMeActivity DEFAULT_ACTIVITY;
    public static MenuSystem menuSystem;

    public MIDlet getMIDlet() {
        return AndroidMeApp.getMIDlet();
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (DEFAULT_ACTIVITY != null) {
            super.finish(); // This can only run as single instance.
            return;
        }

        DEFAULT_ACTIVITY = this;

        showWaitingView();

        Vector<String[]> jadMidlets = AndroidMeApp.getJadMidlets();

        if (jadMidlets.size() > 1) {
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
            // Start MIDlet
            new Thread() {
                public void run() {
//                    waitForDefaultView(); TODO:!!!!
                    AndroidMeApp.startMIDlet();
                }
            }.start();
        }

//        PrintStream log = new PrintStream(new LogOutputStream("AndroidMe"));
//        System.setErr(log);
//        System.setOut(log);
    }

    @Override
    protected void onResume() {
        super.onResume();

        { // Eliminates colour banding
            Window window = getWindow();
            window.setFormat(PixelFormat.RGBA_8888);
        }

        // When we resume, we want to have a nice pool of memory. At the moment
        // we ask for 1/2 of the max memory. If the max is 25Mb, this is 12.5Mb.
        Runtime rt = Runtime.getRuntime();
        long allocMem = rt.maxMemory() / 2;

        // NOTE: This code is not doing the trick... We need to use brute force.
        // VMRuntime.getRuntime().setMinimumHeapSize(allocMem);

        Vector v = new Vector();
        try {
            while (rt.totalMemory() < allocMem) {
                v.add(new byte[100 * 1024]);
            }
        } catch (Throwable e) {
            //#debug debug
            Logger.warn(e);
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

    private void showWaitingView() {
        if (waitingView == null) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            textView.setDrawingCacheBackgroundColor(0xFF000000);
            textView.setBackgroundColor(0xFF000000);
            textView.setText("Please Wait...");

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

            DEFAULT_ACTIVITY = null;
        }
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        onPrepareOptionsMenu();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    // HACK: Work around for issue:
    // http://code.google.com/p/android/issues/detail?id=11833
    // We now call this method ourself, instead of relying on the call of
    // onPrepareOptionsMenu(Menu menu)
    public boolean onPrepareOptionsMenu() {
        if (menu == null) {
            return false;
        }

        if (menuSystem==null) {

            MIDlet midlet = AndroidMeApp.getMIDlet();
            if (midlet == null) {
                return super.onPrepareOptionsMenu(menu);
            }

	        boolean result = false;
	    	Display display = Display.getDisplay( midlet );
	        Displayable current = display.getCurrent();
	        if (current != null) {
	            // load the menu items
	            menu.close();
	            menu.clear();
	            Vector<Command> commands = current.getCommands();
	            for (int i = 0; i < commands.size(); i++) {
	                result = true;
	                Command cmd = commands.get(i);
	                if (cmd.getCommandType() != Command.BACK && cmd.getCommandType() != Command.EXIT) {
	                	// TODO YURA: why is this addSubMenu????
	                    menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getLabel());
	                }
	            }
	        }
	        return result;

        }

        return menuSystem.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	if (menuSystem==null) {

    		MIDlet midlet = AndroidMeApp.getMIDlet();
	        Display display = Display.getDisplay( midlet );
	        Displayable current = display.getCurrent();

	        if (current != null) {
	            Vector<Command> commands = current.getCommands();

	            int commandIndex = item.getItemId() - Menu.FIRST;
	            Command c = commands.get(commandIndex);
	            CommandListener l = current.getCommandListener();

	            if (c != null && l != null) {
	                l.commandAction(c, current);
	                return true;
	            }
	        }

	        return false;

    	}

    	return menuSystem.onOptionsItemSelected(item);

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

            MIDlet midlet = getMIDlet();
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
