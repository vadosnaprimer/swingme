package net.yura.android;


import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import net.yura.android.lcdui.Toolkit;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
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

public class AndroidMeActivity extends Activity implements Toolkit, OnItemClickListener {

    private MIDlet midlet;
    private Vector<String[]> jadMidlets;
    private View defaultView;
    private View waitingView;
    private Thread eventThread;
    private final Object lock = new Object();
    private boolean closed;
    private Vector<BroadcastReceiver> broadcastReceiverList = new Vector<BroadcastReceiver>();

    public static AndroidMeActivity DEFAULT_ACTIVITY;

    public MIDlet getMIDlet() {
        return this.midlet;
    }

    public void invokeAndWait(final Runnable runnable) {
        if (Thread.currentThread() == eventThread) {
            runnable.run();
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    synchronized (lock) {
                        runnable.run();
                        lock.notify();
                    }
                }
            };
            synchronized (lock) {
                runOnUiThread(r);
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (DEFAULT_ACTIVITY != null) {
            super.finish(); // This can only run as single instance.
        }

        DEFAULT_ACTIVITY = this;
        this.eventThread = Thread.currentThread();

        showWaitingView(false);

        try {
            if (midlet == null) {
                startMIDlet(null);
            }
            else {
                midlet.doStartApp();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
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
        invokeAndWait(new Runnable() {
            public void run() {
                if (defaultView != view) {
                    setContentView(view);
                }
            }
        });
        this.defaultView = view;
    }

    private void showWaitingView(boolean wait) {
        if (waitingView == null) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            textView.setDrawingCacheBackgroundColor(0xFF000000);
            textView.setBackgroundColor(0xFF000000);
            textView.setText("Please Wait...");

            waitingView = textView;
        }

        showContentView(waitingView);

        if (wait) {
            while (defaultView.getWidth() == 0) {
                try {
                    System.out.println("Waiting for view...");
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void startMIDlet(final String midletClassName) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    MIDlet midlet = createMIDlet(midletClassName);
                    AndroidMeActivity.this.midlet = midlet;

                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void setFileSystemProperties() {

        final String URL_FILE_ROOT = "file:///";
        String extDir = URL_FILE_ROOT + "/";
        String privDir = extDir;
        String photosDir = extDir;

        try {
            privDir = URL_FILE_ROOT + getFilesDir().getCanonicalPath() + "/";
            String storageDir = Environment.getExternalStorageDirectory().getCanonicalPath() + "/";
            if (new File(storageDir + "DCIM").exists()) {
                extDir = URL_FILE_ROOT + storageDir;
                photosDir = extDir + "DCIM/";
            }
        }
        catch (Exception e) {
            // Do nothing. Use defaults.
        }

        //#mdebug debug
        System.out.println("extDir = " + extDir);
        System.out.println("privDir = " + privDir);
        System.out.println("photosDir = " + photosDir);
        //#enddebug

        System.setProperty("fileconn.dir.photos", photosDir);
        System.setProperty("fileconn.dir.videos",  extDir);
        System.setProperty("fileconn.dir.graphics",  extDir);
        System.setProperty("fileconn.dir.tones",  extDir);
        System.setProperty("fileconn.dir.music",  extDir);
        System.setProperty("fileconn.dir.recordings",  extDir);
        System.setProperty("fileconn.dir.private",  privDir);
    }

    private void setSystemProperties() {
        System.setProperty("microedition.platform", "androidMe");
        System.setProperty("microedition.locale", Locale.getDefault().toString());
        System.setProperty("microedition.configuration", "CLDC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");

        // Screen Resolution Properties (Ad hoc, not on J2ME)
        DisplayMetrics dm = getResources().getDisplayMetrics();

        String dpi =
            (dm.densityDpi == DisplayMetrics.DENSITY_LOW) ? "ldpi" :
            (dm.densityDpi == DisplayMetrics.DENSITY_HIGH) ? "hdpi" : "mdpi";

        System.setProperty("resdir", "/res_" + dpi);
        System.setProperty("display.dpi", dpi);
        System.setProperty("display.density", String.valueOf(dm.density));
        System.setProperty("display.scaledDensity", String.valueOf(dm.scaledDensity));

        // Multimedia Properties
        System.setProperty("microedition.media.version", "1.2");
        System.setProperty("supports.mixing", "false");
        System.setProperty("supports.audio.capture", "false");
        System.setProperty("supports.video.capture", "false ");
        System.setProperty("supports.recording", "false");
        System.setProperty("audio.encodings", "");
        System.setProperty("video.encodings", "");
        System.setProperty("video.snapshot.encodings", "encoding=image/jpeg encoding=image/jpg");
        System.setProperty("streamable.contents", "");

        // PIM and File (Note: "file.separator" already setup by Android OS)
        System.setProperty("microedition.pim.version", "1.0");
        System.setProperty("microedition.io.file.FileConnection.version", "1.0");

        // Hardware properties.
        // Returns the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA phones
        String imei = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        // fails on emulator
        if(imei != null){
        	System.setProperty("phone.imei", imei);
        }

        // Listen for External Storage events
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");

        registerReceiver(new FileBroadcastReceiver(), intentFilter);

        setFileSystemProperties();

        // BlueTooth
        System.setProperty("bluetooth.api.version", "1.1");
    }

    private MIDlet createMIDlet(String midletClassName) {
        showWaitingView(false);

        // midletClassName = "net.yura.mobile.test.MyMidlet";
        // midletClassName = "com.badoo.locator.BadooMidlet";
        // midletClassName = "net.java.dev.lwuit.speed.SpeedMIDlet";

        jadMidlets = new Vector<String[]>();
        if (midletClassName == null) {

            Properties properties = new Properties();
            MIDlet.DEFAULT_APPLICATION_PROPERTIES = properties;

            try {
                String[] assetList = getResources().getAssets().list("");
                for (int i = 0; i < assetList.length; i++) {
                    System.out.println("> > >" + assetList[i]);
                    if (assetList[i].endsWith(".jad")) {
                        System.out.println("Found a Jad File: " + assetList[i]);

                        InputStream is = getAssets().open(assetList[i]);
                        properties.load(is);
                        break;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            int count = 0;
            while (true) {
                count++;
                String midletProp = properties.getProperty("MIDlet-" + count);
                System.out.println("Found MIDlet: " + midletProp);
                if (midletProp == null) {
                    break;
                }

                try {
                    int firstComma = midletProp.indexOf(',');
                    int lastComma = midletProp.lastIndexOf(',');

                    String midletName = midletProp.substring(0, firstComma).trim();
                    String iconName   = midletProp.substring(firstComma + 1, lastComma).trim();
                    String className  = midletProp.substring(lastComma + 1).trim();

                    String[] midletEntry = {midletName, iconName, className};
                    jadMidlets.add(midletEntry);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if (jadMidlets.size() == 1) {
                midletClassName = jadMidlets.elementAt(0)[2];
            }
            else if (jadMidlets.size() > 1) {
                ListView listView = new ListView(this);
                listView.setOnItemClickListener(this);

                String[] listValues = new String[jadMidlets.size()];
                for (int i = 0; i < jadMidlets.size(); i++) {
                    listValues[i] = jadMidlets.elementAt(i)[0];
                }

                listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listValues));

                showContentView(listView);
            }
        }

        if (midletClassName != null) {
            showWaitingView(true);

            setSystemProperties();

            // create a new class loader that correctly handles getResourceAsStream!
            final ClassLoader classLoader = this.getClassLoader();

            MIDlet.DEFAULT_ACTIVITY = this;
            MIDlet.DEFAULT_TOOLKIT = this;

            final String className = midletClassName;
            invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        Class<?> midletClass = Class.forName(className, true, classLoader);
                        midlet = (MIDlet) midletClass.newInstance();

                        if (midlet != null) {
                            midlet.doStartApp();
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("unable to load class "
                                + className, ex);
                    }
                }
            });
        }

        return midlet;
    }

    private void closeMIDlet() {
        try {
            for (BroadcastReceiver receiver : broadcastReceiverList) {
                try {
                    super.unregisterReceiver(receiver);
                } catch (Throwable e) {
                    // Don't care
                    //#debug debug
                    e.printStackTrace();
                }
            }
            broadcastReceiverList.removeAllElements();

            if (midlet != null) {
                midlet.doDestroyApp(true);
            }
        }
        catch (Throwable ex) {
        }

        midlet = null;
    }

    @Override
    public void finish() {
        // Ignore multiple calls to finish()
        if (!closed) {
            closed = true;
            closeMIDlet();
            super.finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (DEFAULT_ACTIVITY == this) {
            closeMIDlet();
            MIDlet.DEFAULT_ACTIVITY = null;
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

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
        midlet.onResult(resultCode, result);
    }

    //Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        broadcastReceiverList.add(receiver); // Keep a copy, so we can clean up on shutdown.

        return super.registerReceiver(receiver, filter);
    }

    //Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        broadcastReceiverList.remove(receiver);

        super.unregisterReceiver(receiver);
    }

    class FileBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //#debug debug
            System.out.println(">>> FileBroadcastReceiver: received " + intent.getAction());
            setFileSystemProperties();
        }
    }
}
