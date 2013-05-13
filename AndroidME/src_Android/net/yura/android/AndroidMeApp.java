package net.yura.android;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.webkit.WebView;
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

        //#debug debug
        Logger.debug("£EP AndroidMeApp onCreate");
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
            Logger.warn(e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
      //#debug debug
        Logger.debug("£EP AndroidMeApp onTerminate");

        try {
            for (BroadcastReceiver receiver : broadcastReceiverList) {
                try {
                    super.unregisterReceiver(receiver);
                } catch (Throwable e) {
                    // Don't care
                    //#debug debug
                    Logger.warn(e);
                }
            }
            broadcastReceiverList.removeAllElements();

            if (midlet != null) {
                midlet.doDestroyApp(true);
            }
        }
        catch (Throwable ex) {
            Logger.warn(ex);
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
                    Logger.warn(ex);
                }
            }
        }
    }

    public void removeCallbacks(final Runnable runnable) {
    	handler.removeCallbacks(runnable);
    }

    public void invokeLater(final Runnable runnable) {
        handler.post(runnable);
    }

    public void invokeLater(final Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }

    public void invokeLater(final Runnable runnable, long delayMillis, boolean removeAnyExistingInstances) {
    	if (removeAnyExistingInstances) {
    		removeCallbacks(runnable);
    	}
    	invokeLater(runnable, delayMillis);
    }

    public Looper getLooper() {
        return handler.getLooper();
    }

    private String getUserAgent() {
        try {
            WebView webView = new WebView(this);
            return webView.getSettings().getUserAgentString();
        } catch (Throwable e) {
            //#debug info
            e.printStackTrace();
        }

        return System.getProperty("microedition.platform") + ";" + System.getProperty("microedition.profiles") + ";" + System.getProperty("microedition.configuration") + ";" + System.getProperty("microedition.encoding");
    }

    private void setSystemProperties() {
        System.setProperty("microedition.platform", "androidMe(" + Build.MODEL + " " + Build.VERSION.RELEASE + ")");
        System.setProperty("microedition.locale", Locale.getDefault().toString());
        System.setProperty("microedition.configuration", "CLDC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");
        System.setProperty("userAgent", getUserAgent());


        // Screen Resolution Properties (Ad hoc, not on J2ME)
        DisplayMetrics dm = getResources().getDisplayMetrics();

        String dpi =
            (dm.densityDpi == DisplayMetrics.DENSITY_LOW) ? "ldpi" :
            (dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM) ? "mdpi" :
            (dm.densityDpi == DisplayMetrics.DENSITY_HIGH) ? "hdpi" :
            (dm.densityDpi == DisplayMetrics.DENSITY_TV) ? "tvdpi" :
                "xhdpi"; // API-11 DisplayMetrics.DENSITY_XHIGH

        Configuration config = getResources().getConfiguration();

        int screenLayout = Configuration.SCREENLAYOUT_SIZE_MASK & config.screenLayout;

        String size =
            (screenLayout == Configuration.SCREENLAYOUT_SIZE_SMALL ) ? "small" :
            (screenLayout == Configuration.SCREENLAYOUT_SIZE_NORMAL) ? "normal" :
            (screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) ? "large" :
                "xlarge"; // API-11 Configuration.SCREENLAYOUT_SIZE_XLARGE


        // deprecated
        System.setProperty("resdir", "/res_" + (( "mdpi".equals(dpi) && "xlarge".equals(size) )?size+"-":"") + ("xhdpi".equals(dpi)?"hdpi":dpi) );


        System.setProperty("display.dpi", dpi);
        System.setProperty("display.size", size);
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


        System.setProperty("java.io.tmpdir", getCacheDir().toString() ); // OLD: java.io.tmpdir=/sdcard
        System.setProperty("user.home", getFilesDir().toString() ); // OLD: user.home=

        
        // Hardware properties.
        // Returns the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA phones
        String imei = getUniqueHardwareId();
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

        BroadcastReceiver receiver = new SystemChangedBroadcastReceiver();
        registerReceiver(receiver, fileFilter);
        
        if ( hasPermission("ACCESS_WIFI_STATE") ) {
            
            IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
            wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            
            registerReceiver(receiver, wifiFilter);
        }

        setFileSystemProperties();

        // BlueTooth
        System.setProperty("bluetooth.api.version", "1.1");
        
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0 );
            System.setProperty("versionName", pinfo.versionName );
            System.setProperty("versionCode", String.valueOf(pinfo.versionCode) );
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        System.setProperty("debug", String.valueOf(net.yura.mobile.BuildConfig.DEBUG) );
        

        // HTTP connection reuse which was buggy pre-froyo
        // http://android-developers.blogspot.dk/2011/09/androids-http-clients.html
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
            System.setProperty("http.keepAlive", "false");
        }

    }
    
    private boolean hasPermission(String permission) {
        int res = getContext().checkCallingOrSelfPermission( "android.permission."+ permission );
        return res == PackageManager.PERMISSION_GRANTED;       
    }

    private String getUniqueHardwareId(){
    	String uniqueId = "";
    	try
		{
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			if (telephonyManager != null){
				uniqueId = telephonyManager.getDeviceId();
			}

			// if we can't retrieve the IMEI we can try and get the serial hardware number.
			// this is only on android gingerbread (v2.3) and up. according to google
			// devices WITHOUT telephony are required to report a unique device ID here;
			// http://android-developers.blogspot.com/2011/03/identifying-app-installations.html
			if((isValidId(uniqueId)) && android.os.Build.VERSION.SDK_INT >= 9 ){
				//try getting from api 9 and up.
				try {
		            // SERIAL is only available in API 9
		            Class clazz = android.os.Build.class;
		             uniqueId = (String) clazz.getField("SERIAL").get(clazz);
		        } catch (Throwable e) {
		        	//#debug debug
		        	System.out.println(e.getStackTrace());
		        }

			}

			//fall back to WIFI. Android documentation says:
			//It may be possible to retrieve a Mac address from a deviceÕs WiFi or Bluetooth hardware. We do not recommend using this as a unique identifier.
			//To start with, not all devices have WiFi. Also, if the WiFi is not turned on, the hardware may not report the Mac address.
			if(isValidId(uniqueId)) {
				WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
				if(wifiMan != null){
					WifiInfo wifiInf = wifiMan.getConnectionInfo();
					if(wifiInf != null){
						uniqueId = wifiInf.getMacAddress();
					}
				}
			}

        } catch (Throwable e) {
        	//#debug debug
        	System.out.println(e.getStackTrace());
        }
        //#debug debug
        System.out.println("Unique hardwar id rerieved and is: " + uniqueId);
    	return uniqueId;
    }

	private boolean isValidId(String uniqueId) {
		return uniqueId == null || uniqueId.length() == 0 || uniqueId.equals("000000000000000") || uniqueId.equals("unknown") || uniqueId.equals("0");
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
            WifiInfo myWifi = null;

            try {
                myWifi = wifiMan.getConnectionInfo();
            }
            catch (SecurityException sex) { }

            String myMAC = "";
            if (myWifi != null) {
                myMAC = myWifi.getBSSID();
                // System.out.println(">>> My wifi MAC: " + myMAC);
            }


            List<ScanResult> scanRes = null;
            
            try {
                scanRes = wifiMan.getScanResults();
            }
            catch (SecurityException sex) { }
            
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
                Logger.warn(e);
            }
        }
    }

    //override
    public void onConfigurationChanged(Configuration newConfig) {
    	System.setProperty("microedition.locale", newConfig.locale.toString());
    	super.onConfigurationChanged(newConfig);
    }

    class SystemChangedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //#debug debug
                System.out.println(">>> SystemChangedBroadcastReceiver: received " + intent.getAction());

                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()) ||
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
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

    public static Class<?> getMainActivityClass() {
        try {
            return Class.forName( instance.getPackageManager().getLaunchIntentForPackage(instance.getPackageName()).getComponent().getClassName() );
        }
        catch (Exception ex) {
            throw new RuntimeException("can not find main activity class",ex);
        }
    }
}
