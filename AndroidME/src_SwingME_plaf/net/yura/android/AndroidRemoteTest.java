package net.yura.android;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import net.yura.mobile.util.RemoteTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AndroidRemoteTest extends RemoteTest {

    public AndroidRemoteTest() {

        Log.d("YURA", "my ip "+getLocalIpAddress());

    }

    @Override
    protected boolean click(String text) {
        View window = getSelectedFrame(); // TODO can this be null???
        boolean nativeClick = clickView(window, text); // try native first as menu may be open
        if (!nativeClick && AndroidMeActivity.DEFAULT_ACTIVITY.hasWindowFocus() ) { // TODO check menu is NOT open
            return super.click(text); // Do SwingME click
        }
        return nativeClick;
    }

    private static boolean clickView(View view, String clickText) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            int childcount = viewGroup.getChildCount();
            for (int i=0; i < childcount; i++){
                View childView = viewGroup.getChildAt(i);
                //String printText = debugStr + "> " + childView;

                if (childView instanceof TextView) {
                    final TextView b = (TextView) childView;
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
