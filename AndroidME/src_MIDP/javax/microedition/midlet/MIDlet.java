package javax.microedition.midlet;


import java.util.Properties;
import javax.microedition.io.ConnectionNotFoundException;
import net.yura.android.lcdui.Toolkit;
import android.app.Activity;
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

    public Properties getApplicationProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(Properties applicationProperties) {
        this.applicationProperties = applicationProperties;
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
        String action;
        if (url.startsWith(PROTOCOL_PHONE)) {
            action = Intent.ACTION_DIAL;
        } else {
            action = Intent.ACTION_DEFAULT;
        }
        Intent intent = new Intent(action, content);
        this.getActivity().startActivity(intent);
        return false;
    }

    public String getAppProperty(String key) {
        return this.applicationProperties.getProperty(key);
    }

    public int checkPermission(String string) {
        // TODO Auto-generated method stub
        return 0;
    }


    private static class PhoneListener extends PhoneStateListener {

        private static PhoneListener instance;
        static void init() {
            if (instance == null) {
                try {
                    instance = new PhoneListener();

                    // Register our Telephony Listener, so we can have Cell ID's updates
                    TelephonyManager tm = getTelephonyManager();
                    tm.listen(instance, PhoneListener.LISTEN_CELL_LOCATION);
                    tm.listen(instance, PhoneListener.LISTEN_SIGNAL_STRENGTH);

                    // Request Cell Location
                    CellLocation.requestLocationUpdate();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        static private TelephonyManager getTelephonyManager() {
            return (TelephonyManager) DEFAULT_ACTIVITY.getSystemService(Context.TELEPHONY_SERVICE);
        }


        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);

            try {
                GsmCellLocation gsmLocation = (GsmCellLocation) location;
                System.setProperty("CellID", "" + gsmLocation.getCid());
                System.setProperty("LAC", "" + gsmLocation.getLac());

                TelephonyManager tm = getTelephonyManager();
                System.setProperty("CMCC", tm.getNetworkCountryIso());
                System.setProperty("MCC", tm.getSimCountryIso());
                System.setProperty("IMSI", tm.getSubscriberId());

                // Network Operator = MMC + MNC
                String op = tm.getNetworkOperator();
                System.setProperty("MMC", op.substring(0, 3));
                System.setProperty("MNC", op.substring(3));
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
}
