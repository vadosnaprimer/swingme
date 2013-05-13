package javax.microedition.midlet;

import java.util.Properties;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import net.yura.android.AndroidMeActivity;
import net.yura.android.AndroidMeApp;
import net.yura.android.WebViewActivity;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Url;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public abstract class MIDlet {
    public static final String PROTOCOL_HTTP = "http://";
    public static final String PROTOCOL_HTTPS = "https://";
    public static final String PROTOCOL_SMS = "sms:";
    public static final String PROTOCOL_PHONE = "tel:";
    public static final String PROTOCOL_EMAIL = "email:";
    public static final String PROTOCOL_NOTIFY = "notify:";
    public static final String PROTOCOL_NATIVE = "native:";
    public static final String PROTOCOL_NATIVE_NO_RESULT = "nativeNoResult:";

    public static MIDlet DEFAULT_MIDLET;
    public static Properties DEFAULT_APPLICATION_PROPERTIES;

    private static String platformLastUrl;
    private static long platformLastTime;


    private Properties applicationProperties = DEFAULT_APPLICATION_PROPERTIES;

    protected MIDlet() {
        DEFAULT_MIDLET = this;
        PhoneListener.init();
    }

    protected abstract void destroyApp(boolean unconditional)
            throws MIDletStateChangeException;

    protected abstract void pauseApp() throws MIDletStateChangeException;

    protected abstract void startApp() throws MIDletStateChangeException;

    public final void notifyDestroyed() {
        AndroidMeActivity.DEFAULT_ACTIVITY.finish();

        new Thread() {
            public void run() {
                try {
                    // Allow time for threads to gracefully die...
                    sleep(2000);
                    System.exit(0);
                } catch (Throwable e) {
                    Logger.warn(e);
                }
            }
        }.start();
    }

    public final void doDestroyApp(boolean unconditional)
            throws MIDletStateChangeException {
        this.destroyApp(unconditional);
    }

    public final void doStartApp() throws MIDletStateChangeException {
        this.startApp();
    }

    public final void doPauseApp() throws MIDletStateChangeException {
        this.pauseApp();
    }

    public boolean platformRequest(String url) throws ConnectionNotFoundException {

        try {
            Uri content = Uri.parse(url);
            boolean isProtoNative = url.startsWith(PROTOCOL_NATIVE);
            boolean isProtoNativeNoRes = url.startsWith(PROTOCOL_NATIVE_NO_RESULT);
            Activity activity = AndroidMeActivity.DEFAULT_ACTIVITY;

            if (isProtoNative || isProtoNativeNoRes) {

                if (isProtoNative) {
                    hideSoftKeyboard();
                }

                long now = System.currentTimeMillis();

                // HACK: Android: platformRequest() is normally called, when a
                // button is pressed. If the button is quickly pressed more than
                // once, it will launch the same activity more than once. To
                // minimize this, we don't launch the same url more than once
                // for a short period of time
                if (platformLastUrl == null || !platformLastUrl.equals(url) || (now - platformLastTime) > 1000) {
                    platformLastUrl = url;
                    platformLastTime = now;

                    Class cls = Class.forName(content.getHost());
                    Intent i = new Intent(activity, cls);
                    i.setData(content);

                    if ("true".equalsIgnoreCase(content.getQueryParameter("isSingleton"))) {
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    }

                    String reqStr = content.getQueryParameter("requestCode");
                    int reqCode = reqStr == null ? 0 : Integer.parseInt(reqStr);

                    if (isProtoNative) {
                        activity.startActivityForResult(i, reqCode);
                    }
                    else {
                        activity.startActivity(i);
                    }
                }
            }
            else if (url.startsWith(PROTOCOL_NOTIFY)) {
                // there is a bug on android older versions so we use our Url to decode.
                Url myurl = new Url( url );
                showNotification(
                        myurl.getQueryParameter("title"),
                        myurl.getQueryParameter("num"),
                        myurl.getQueryParameter("message"),
                        myurl.getQueryParameter("icon"),
                        myurl.getQueryParameter("onlyBackground"));
            }
            else if (url.startsWith("toast://show")) {
                // there is a bug on android older versions so we use our Url to decode. 
                Url myurl = new Url( url );
                showToast(myurl.getQueryParameter("message"),"SHORT".equals(myurl.getQueryParameter("duration"))?Toast.LENGTH_SHORT:Toast.LENGTH_LONG);
            }


            else if (url.startsWith("grasshopper")) {
                try {
                    String params = url.substring(url.indexOf('?')+1);
                    String[] s1 = params.split("\\&");
                    String appName="Unknown android app",appVersion="Unknown version",locale="";
                    for (int c=0;c<s1.length;c++) {
                        String[] s2 = s1[c].split("\\=");
                        if ("name".equals(s2[0])) {
                            appName = s2[1];
                        }
                        else if ("version".equals(s2[0])) {
                            appVersion = s2[1];
                        }
                        else if ("locale".equals(s2[0])) {
                            locale = s2[1];
                        }
                        else {
                            System.out.println("unknown grasshopper param: "+s1[c]);
                        }
                    }
                    try {
                        Class<?> simpleBug = Class.forName("net.yura.grasshopper.SimpleBug");
                        java.lang.reflect.Method initLogFile = simpleBug.getMethod("initLogFile", new Class[] {String.class,String.class,String.class});
                        initLogFile.invoke( null, new Object[] {appName, appVersion, locale} );
                        System.out.println("Grasshopper loaded");
                    }
                    catch(Throwable th) {
                        System.out.println("Grasshopper not loaded");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            
            
            else if (url.equals("clipboard://get")) {

            	ClipboardManager clipboardManager = (ClipboardManager) AndroidMeApp.getIntance().getSystemService(Context.CLIPBOARD_SERVICE);
            	Object obj = clipboardManager.getText();
            	if (obj==null) {
            		System.clearProperty("clipboard.text");
            	}
            	else {
            		System.setProperty("clipboard.text", obj.toString()); // so far we only support Strings
            	}

            }
            else if (url.startsWith("clipboard://put/")) {
            	ClipboardManager clipboardManager = (ClipboardManager) AndroidMeApp.getIntance().getSystemService(Context.CLIPBOARD_SERVICE);

            	// this makes us link to SwingME where we do not really want to, as me4se would need to too
            	//net.yura.mobile.gui.Midlet midlet = net.yura.mobile.gui.Midlet.getMidlet();
            	//Object obj = midlet.retrievePlatformRequestParam(url);
            	//if (obj instanceof String) {
            	//	clipboardManager.setText( (String)obj );
            	//}

            	String text = url.substring( "clipboard://put/".length() );
            	if (!"".equals(text)) {
            		clipboardManager.setText( text );
            	}

            }
            else if (url.equals("wakelock://true")) {
            	((AndroidMeActivity)AndroidMeActivity.DEFAULT_ACTIVITY).setWakeLock(true);
            }
            else if (url.equals("wakelock://false")) {
            	((AndroidMeActivity)AndroidMeActivity.DEFAULT_ACTIVITY).setWakeLock(false);
            }
            else if (url.startsWith("geo:")) {
            	// eg (with the brackets and everything else past the = URL encoded): geo://lat,long?q=lat,long(description)
            	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	try {
            		AndroidMeActivity.DEFAULT_ACTIVITY.startActivity(intent);
            	}
            	catch (ActivityNotFoundException anf) {
            		// there's nothing on the phone that knows how to process a geo: URI.
            		url = "http://maps.google.com/" + url.substring(url.indexOf("?q="));
            		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url) );
            		browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               		AndroidMeActivity.DEFAULT_ACTIVITY.startActivity(browserIntent);
            	}
            	
            }
            else if (url.startsWith("file:///android_asset/")) {
                
                Intent i = new Intent(activity, WebViewActivity.class);
                //i.setClassName("com.android.browser", "com.android.browser.BrowserActivity"); // does not work
                i.setData(content);
                activity.startActivity(i);
                
            }
            else {
                String action = (url.startsWith(PROTOCOL_PHONE)) ?
                     Intent.ACTION_DIAL : Intent.ACTION_DEFAULT;
                Intent intent = new Intent(action, content);
                activity.startActivity(intent);
            }
        } catch (Throwable e) {
            //#debug debug
            Logger.warn(e);

            ConnectionNotFoundException connEx = new ConnectionNotFoundException(url);
            connEx.initCause(e);

            throw connEx;
        }

        return false;
    }

    public String getAppProperty(String key) {
        return this.applicationProperties.getProperty(key);
    }

    public int checkPermission(String string) {
        return 0;
    }

    private void showNotification(String title,String num,String message,String icon,String onlyBackground) {

        if (    onlyBackground==null || 
                !onlyBackground.equals("true") || 
                AndroidMeActivity.DEFAULT_ACTIVITY==null || 
                !((AndroidMeActivity)AndroidMeActivity.DEFAULT_ACTIVITY).isForeground() ) {

            Context ctx = AndroidMeApp.getContext();
            int iconId = ctx.getResources().getIdentifier(icon, "drawable", ctx.getPackageName());

            System.out.println(">>>> showNotification:" +
                    " title = " + title +
                    " num = " + num +
                    " message = " + message +
                    " icon = " + icon +
                    " iconId = " + iconId);

            Intent notifyIntent = new Intent(ctx, AndroidMeApp.getMainActivityClass() );
            PendingIntent intent = PendingIntent.getActivity(ctx, 0, notifyIntent, 0);

            Notification notif = new Notification(iconId, title, System.currentTimeMillis());

            notif.iconLevel = 3;
            notif.vibrate = new long[] {100,100,200,300};
            notif.defaults = Notification.DEFAULT_ALL;
            notif.ledOnMS = 100;
            notif.ledOffMS = 100;
            notif.flags |= Notification.FLAG_AUTO_CANCEL;

            try {
                notif.number = Integer.parseInt(num);
            }
            catch (Throwable e) { } // Ignore wrong or missing message number

            notif.setLatestEventInfo(ctx, title, message, intent);

            NotificationManager notifManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.notify(0, notif);
        }
    }

    public void showToast(final String message,final int duration) {
        final Activity activity = AndroidMeActivity.DEFAULT_ACTIVITY;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, duration).show();
            }
        });
    }


    private static class PhoneListener extends PhoneStateListener {

        private static PhoneListener instance;
        static void init() {
            if (instance == null) {
                try {
                    instance = new PhoneListener();

                    // Register our Telephony Listener, so we can have Cell ID's updates
                    TelephonyManager tm = instance.getTelephonyManager();
                    try {
                        tm.listen(instance, PhoneListener.LISTEN_CELL_LOCATION);
                    }
                    catch (SecurityException sex) { }

                    tm.listen(instance, PhoneListener.LISTEN_SIGNAL_STRENGTH);

                    // Request Cell Location
                    CellLocation.requestLocationUpdate();
                }
                catch (Throwable e) {
                    Logger.warn(e);
                }
            }
        }

        private TelephonyManager getTelephonyManager() {
            return (TelephonyManager) AndroidMeApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        }


        private static void setProperty(String prop, String value) {
            if (value == null) {
                System.out.println("CellLocation: " + prop + " not found...");
                value = "";
            }

            System.setProperty(prop, value);
        }

        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);

            try {

            	if (location instanceof GsmCellLocation) {
            		GsmCellLocation gsmLocation = (GsmCellLocation) location;
	                setProperty("CellID", "" + gsmLocation.getCid());
	                setProperty("LAC", "" + gsmLocation.getLac());
            	}
            	else {
            		System.out.println("[MIDlet] unknown location object "+location);
            	}

                TelephonyManager tm = getTelephonyManager();
                setProperty("CMCC", tm.getNetworkCountryIso());
                setProperty("MCC", tm.getSimCountryIso());
                setProperty("IMSI", tm.getSubscriberId());

                // Network Operator = MMC + MNC
                String op = tm.getNetworkOperator();
                if (op != null && op.length() > 4) {
                    setProperty("MMC", op.substring(0, 3));
                    setProperty("MNC", op.substring(3));
                }
            }
            catch (Throwable e) {
                Logger.warn(e);
            }
        }

       @Override
       public void onSignalStrengthChanged(int asu) {
           super.onSignalStrengthChanged(asu);

           System.setProperty("NETWORKSIGNAL", "" + asu);
       }
    }

    // To be overload by children
    public void onResult(int requestCode, int resultCode, Object result) {

    }

    private void hideSoftKeyboard() {
        try {
            View view = Display.getDisplay(this).getCurrent().getView();
            Context ctx = view.getContext();
            InputMethodManager im = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Throwable e) {
            //#debug info
            Logger.warn(e);
        }
    }
}
