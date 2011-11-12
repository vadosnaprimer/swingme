package net.yura.android;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import net.yura.mobile.util.RemoteTest;

public class AndroidRemoteTest extends RemoteTest {

    public AndroidRemoteTest() {

        Log.d("YURA", "my ip "+getLocalIpAddress());

    }

    @Override
    protected boolean click(String text) {

        if (AndroidMeActivity.DEFAULT_ACTIVITY.hasWindowFocus()) { // SwingME has focus
            return super.click(text);
        }
        else {
            View window = getSelectedFrame(); // TODO can this be null???
            return clickView(window, text);
        }
    }

    private static boolean clickView(View view, String clickText) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            int childcount = viewGroup.getChildCount();
            for (int i=0; i < childcount; i++){
                View childView = viewGroup.getChildAt(i);
                //String printText = debugStr + "> " + childView;

                if (childView instanceof Button) {
                    final Button b = (Button) childView;
                    //printText += "(text = " + b.getText() + ")";

                    if (clickText.equalsIgnoreCase(b.getText().toString())) {
                        //printText += " <== FOUND!";

                        b.post(new Runnable() {
                            @Override
                            public void run() {
                                b.performClick();
                            }
                        });
                        return true;
                    }
                }

                //System.out.println(printText);

                // Recursive call
                if (clickView(childView, clickText)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String toString(View view) {
        StringBuilder builder = new StringBuilder();
        builder.append( view.getClass().getSimpleName() );
        if (view instanceof TextView) {
            builder.append(" [");
            builder.append( ((TextView)view).getText() );
            builder.append("]");
        }
        if (view instanceof ViewGroup) {
            builder.append(" [");
            ViewGroup $vg = (ViewGroup)view;
            for (int i = 0; i < $vg.getChildCount(); i++) {
                builder.append( toString( $vg.getChildAt(i) ) );
            }
            builder.append("]");
        }
        return builder.toString();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @see net.yura.mobile.gui.DesktopPane#getSelectedFrame()
     */
    public static View getSelectedFrame() {
        View[] views = getAllWindowViews();
        for (View view : views) {
            if (view.hasWindowFocus()) {
                return view;
            }
        }
        return null;
    }

    static View[] getAllWindowViews() {
        try {
            Class windowManager = Class.forName("android.view.WindowManagerImpl");

            Field viewsField = windowManager.getDeclaredField("mViews");
            viewsField.setAccessible(true);

            String wndManStr = (android.os.Build.VERSION.SDK_INT >= 13) ? "sWindowManager" : "mWindowManager";
            Field instanceField = windowManager.getDeclaredField(wndManStr);
            instanceField.setAccessible(true);

            Object instance = instanceField.get(null);
            return (View[]) viewsField.get(instance);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
