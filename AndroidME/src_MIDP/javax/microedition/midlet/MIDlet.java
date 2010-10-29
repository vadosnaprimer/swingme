package javax.microedition.midlet;


import java.util.Properties;
import javax.microedition.io.ConnectionNotFoundException;


import net.yura.android.AndroidMeMIDlet;
import net.yura.android.lcdui.Toolkit;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

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
    public static Toolkit DEFAULT_TOOLKIT;
    public static Activity DEFAULT_ACTIVITY;
    public static Properties DEFAULT_APPLICATION_PROPERTIES;


    private Activity activity = DEFAULT_ACTIVITY;
    private Toolkit toolkit = DEFAULT_TOOLKIT;
    private Properties applicationProperties = DEFAULT_APPLICATION_PROPERTIES;

    protected MIDlet() {
        DEFAULT_MIDLET = this;
        PhoneListener.init();
    }

    public Handler getHandler() {
        return this.toolkit.getHandler();
    }

    public void invokeAndWait(Runnable r) {
        this.toolkit.invokeAndWait(r);
    }

    public Activity getActivity() {
        return activity;
    }

    public Toolkit getToolkit() {
        return toolkit;
    }

    public void setToolkit(Toolkit toolkit) {
        this.toolkit = toolkit;
    }

    protected abstract void destroyApp(boolean unconditional)
            throws MIDletStateChangeException;

    protected abstract void pauseApp() throws MIDletStateChangeException;

    protected abstract void startApp() throws MIDletStateChangeException;

    public final void notifyDestroyed() {
        this.activity.finish();

        new Thread() {
            public void run() {
                try {
                    // Allow time for threads to gracefully die...
                    sleep(2000);
                    System.exit(0);
                } catch (Throwable e) {
                    e.printStackTrace();
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

    public boolean platformRequest(String url)
            throws ConnectionNotFoundException {

        Uri content = Uri.parse(url);
        boolean isProtoNative = url.startsWith(PROTOCOL_NATIVE);
        boolean isProtoNativeNoRes = url.startsWith(PROTOCOL_NATIVE_NO_RESULT);

        if (isProtoNative || isProtoNativeNoRes) {
            try {
                String clName = content.getHost();
                Class cls = Class.forName(clName);
                Intent i = new Intent(Intent.ACTION_VIEW, content, getActivity(), cls);
                i.setData(content);

                if (isProtoNative) {
                    getActivity().startActivityForResult(i, 0);
                }
                else {
                    getActivity().startActivity(i);
                }
            } catch (Throwable e) {
                //#debug debug
                e.printStackTrace();

                ConnectionNotFoundException connEx = new ConnectionNotFoundException(url);
                connEx.initCause(e);

                throw connEx;
            }
        }
        else if (url.startsWith(PROTOCOL_NOTIFY)) {
            showNotification(content);
        }
        else {
            String action = (url.startsWith(PROTOCOL_PHONE)) ?
                 Intent.ACTION_DIAL : Intent.ACTION_DEFAULT;
            Intent intent = new Intent(action, content);
            getActivity().startActivity(intent);
        }

        return false;
    }

    public String getAppProperty(String key) {
        return this.applicationProperties.getProperty(key);
    }

    public int checkPermission(String string) {
        // TODO Auto-generated method stub
        return 0;
    }

    private void showNotification(Uri uri) {
        String title = uri.getQueryParameter("title");
        String num = uri.getQueryParameter("num");
        String message = uri.getQueryParameter("message");
        String icon = uri.getQueryParameter("icon");

        Context ctx = getActivity();
        int iconId = ctx.getResources().getIdentifier(icon, "drawable", ctx.getPackageName());

        System.out.println(">>>> showNotification:" +
                " title = " + title +
                " num = " + num +
                " message = " + message +
                " icon = " + icon +
                " iconId = " + iconId);

        Intent notifyIntent = new Intent(ctx, AndroidMeMIDlet.class);
        PendingIntent intent = PendingIntent.getActivity(ctx, 0, notifyIntent, 0);

        Notification notif = new Notification(iconId, title, System.currentTimeMillis());

        notif.iconLevel = 3;
        notif.vibrate = new long[] {100,100,200,300};
        notif.defaults = Notification.DEFAULT_ALL;
        notif.ledOnMS = 100;
        notif.ledOffMS = 100;
        //notif.flags = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        try {
            notif.number = Integer.parseInt(num);
        } catch (Throwable e) {
            // Ignore wrong or missing message number
        }

        notif.setLatestEventInfo(ctx, title, message, intent);

        NotificationManager notifManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(0, notif);
    }


    private static class PhoneListener extends PhoneStateListener {

        private static PhoneListener instance;
        static void init() {
            if (instance == null) {
                try {
                    instance = new PhoneListener();

                    // Register our Telephony Listener, so we can have Cell ID's updates
                    TelephonyManager tm = instance.getTelephonyManager();
                    tm.listen(instance, PhoneListener.LISTEN_CELL_LOCATION);
                    tm.listen(instance, PhoneListener.LISTEN_SIGNAL_STRENGTH);

                    // Request Cell Location
                    CellLocation.requestLocationUpdate();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        private TelephonyManager getTelephonyManager() {
            return (TelephonyManager) DEFAULT_ACTIVITY.getSystemService(Context.TELEPHONY_SERVICE);
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
                GsmCellLocation gsmLocation = (GsmCellLocation) location;
                setProperty("CellID", "" + gsmLocation.getCid());
                setProperty("LAC", "" + gsmLocation.getLac());

                TelephonyManager tm = getTelephonyManager();
                setProperty("CMCC", tm.getNetworkCountryIso());
                setProperty("MCC", tm.getSimCountryIso());
                setProperty("IMSI", tm.getSubscriberId());

                // Network Operator = MMC + MNC
                String op = tm.getNetworkOperator();
                setProperty("MMC", op.substring(0, 3));
                setProperty("MNC", op.substring(3));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

       @Override
       public void onSignalStrengthChanged(int asu) {
           super.onSignalStrengthChanged(asu);

           System.setProperty("NETWORKSIGNAL", "" + asu);
       }
    }

    // To be overload by children
    public void onResult(int resultCode, Object result) {

    }

    // To be overload by children
    public Object onGetModel() {
        return null;
    }
}
