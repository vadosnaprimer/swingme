package net.yura.android;


import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
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

    public static AndroidMeActivity DEFAULT_ACTIVITY;

    public MIDlet getMIDlet() {
        return AndroidMeApp.getMIDlet();
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (DEFAULT_ACTIVITY != null) {
            super.finish(); // This can only run as single instance.
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
        // we ask for 3/5 of the max memory. If the max is 25Mb, this is 15Mb.
        Runtime rt = Runtime.getRuntime();
        long allocMem = (3 * rt.maxMemory()) / 5;

        // NOTE: This code is not doing the trick... We need to use brute force.
        // VMRuntime.getRuntime().setMinimumHeapSize(allocMem);

        Vector v = new Vector();
        try {
            while (rt.totalMemory() < allocMem) {
                v.add(new byte[100 * 1024]);
            }
        } catch (Throwable e) {
            //#debug debug
            e.printStackTrace();
        }
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
                ex.printStackTrace();
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
                    ex.printStackTrace();
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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MIDlet midlet = AndroidMeApp.getMIDlet();
        if (midlet == null) {
            return super.onPrepareOptionsMenu(menu);
        }

        boolean result = false;
        Display display = Display.getDisplay(midlet);
        Displayable current = display.getCurrent();
        if (current != null) {
            // load the menu items
            menu.clear();
            Vector<Command> commands = current.getCommands();
            for (int i = 0; i < commands.size(); i++) {
                result = true;
                Command cmd = commands.get(i);
                if (cmd.getCommandType() != Command.BACK && cmd.getCommandType() != Command.EXIT) {
                    menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getLabel());
                }
            }
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MIDlet midlet = AndroidMeApp.getMIDlet();
        Display display = Display.getDisplay(midlet);
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
        super.onActivityResult(requestCode, resultCode, data);
        Object result = null;
        if(data != null && data.getExtras() != null) {
        	result = data.getExtras().get("data");
        }

        MIDlet midlet = getMIDlet();
        midlet.onResult(resultCode, result);
    }
}
