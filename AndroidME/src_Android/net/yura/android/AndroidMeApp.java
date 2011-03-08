package net.yura.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import net.yura.mobile.logging.Logger;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class AndroidMeApp extends Application {

    private static AndroidMeApp instance;

    public static Context getContext() {
        return instance;
    }

    public static AndroidMeApp getIntance() {
        return instance;
    }

    public static MIDlet getMIDlet() {
        return instance.midlet;
    }

    // TODO: Async
    public static void createMIDlet(final String className) throws Exception {

        // Needs to run on the UI thread, otherwise some of the API's will not start
        instance.handler.post(new Runnable() {
            public void run() {
                try {
                    Class<?> midletClass = Class.forName(className);
                    instance.midlet = (MIDlet) midletClass.newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("unable to start MIDlet: "
                            + className, ex);
                }
            }
        });
    }

    public static void waitForMIDletCreation() {
        // Just wait for a dummy task to be handled.
        instance.invokeAndWait(new Thread());
    }

    // TODO: Async
    public static void startMIDlet() {
        // Needs to run on the UI thread, otherwise some of the API's will not start
        instance.handler.post(new Runnable() {
            public void run() {
                try {

                    Display display = Display.getDisplay(instance.midlet);

                    if (display.hiddenDisplay!=null) {
                        display.setCurrent(display.hiddenDisplay);
                    }

                    instance.midlet.doStartApp();


                } catch (Exception ex) {
                    throw new RuntimeException("unable to start MIDlet: ", ex);
                }
            }
        });
    }

    public static Vector<String[]> getJadMidlets() {
        return instance.jadMidlets;
    }

    private MIDlet midlet;
    private Vector<String[]> jadMidlets;
    private Thread eventThread;
    private final Object lock = new Object();
    private Handler handler;

    private Vector<BroadcastReceiver> broadcastReceiverList = new Vector<BroadcastReceiver>();

    public AndroidMeApp() {
        //System.out.println(">>>>>>>>>> AndroidMeApp@constructor");
        instance = this;

        handler = new Handler();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.eventThread = Thread.currentThread();

        try {
            setSystemProperties();
            loadJadFile();

            Vector<String[]> jadMidlets = getJadMidlets();
            if (jadMidlets.size() == 1) {
                String midletClassName = jadMidlets.elementAt(0)[2];
                createMIDlet(midletClassName);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

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
            ex.printStackTrace();
        }

        midlet = null;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        broadcastReceiverList.add(receiver); // Keep a copy, so we can clean up on shutdown.

        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        broadcastReceiverList.remove(receiver);

        super.unregisterReceiver(receiver);
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
                handler.post(r);
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void invokeLater(final Runnable runnable) {
        handler.post(runnable);
    }

    private void setSystemProperties() {
        System.setProperty("microedition.platform", "androidMe(" + Build.MODEL + " " + Build.VERSION.RELEASE + ")");
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
        IntentFilter fileFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        fileFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        fileFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        fileFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        fileFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        fileFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        fileFilter.addDataScheme("file");

        IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);

        BroadcastReceiver receiver = new SystemChangedBroadcastReceiver();
        registerReceiver(receiver, fileFilter);
        registerReceiver(receiver, wifiFilter);

        setFileSystemProperties();

        // BlueTooth
        System.setProperty("bluetooth.api.version", "1.1");
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

    private void setWifiProperties() {
        String prop = "";

        WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiMan != null) {
            WifiInfo myWifi = wifiMan.getConnectionInfo();
            String myMAC = "";
            if (myWifi != null) {
                myMAC = myWifi.getBSSID();
                // System.out.println(">>> My wifi MAC: " + myMAC);
            }


            List<ScanResult> scanRes = wifiMan.getScanResults();
            if (scanRes != null) {
                for (ScanResult scanResult : scanRes) {
                    if (myMAC != null && !myMAC.equals(scanResult.BSSID)) {
                        // System.out.println(">>> Neighbour wifi MAC: " + scanResult.BSSID);
                        if (prop.length() > 0) {
                            prop += ";";
                        }
                        prop += scanResult.BSSID + "=" + scanResult.level;
                    }
                }
            }
        }

        System.setProperty("wifi.state",  prop);
    }

    private void loadJadFile() throws IOException {
        Properties properties = new Properties();
        MIDlet.DEFAULT_APPLICATION_PROPERTIES = properties;

        String[] assetList = getResources().getAssets().list("");
        for (int i = 0; i < assetList.length; i++) {
            //System.out.println("> > >" + assetList[i]);
            if (assetList[i].endsWith(".jad")) {
                //System.out.println("Found a Jad File: " + assetList[i]);

                InputStream is = getAssets().open(assetList[i]);
                properties.load(is);
                break;
            }
        }

        jadMidlets = new Vector<String[]>();

        int count = 0;
        while (true) {
            count++;
            String midletProp = properties.getProperty("MIDlet-" + count);
            //System.out.println("Found MIDlet: " + midletProp);
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
    }

    class SystemChangedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //#debug debug
                System.out.println(">>> SystemChangedBroadcastReceiver: received " + intent.getAction());

                if (WifiManager.WIFI_STATE_CHANGED_ACTION.endsWith(intent.getAction())) {
                    setWifiProperties();
                }
                else {
                    setFileSystemProperties();
                }
            } catch (Throwable e) {
                //#debug warn
                Logger.warn(e);
            }
        }
    }
}