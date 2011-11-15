package net.yura.android;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import net.yura.mobile.util.RemoteTest;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AndroidRemoteTest extends RemoteTest {

    public AndroidRemoteTest() {

        Log.d("YURA", "my ip "+getLocalIpAddress());

    }

    @Override
    protected boolean onClickText(String text) {
        View window = getSelectedFrame(); // TODO can this be null???

        //System.out.println("Window="+toString(window));

        boolean nativeClick = clickText(window, text); // try native first as menu may be open
        if (!nativeClick && AndroidMeActivity.DEFAULT_ACTIVITY.hasWindowFocus() ) { // TODO check menu is NOT open
            return super.onClickText(text); // Do SwingME click
        }
        return nativeClick;
    }

    @Override
    protected void onSetCursorInvisible() {
        final View focusView = AndroidMeActivity.DEFAULT_ACTIVITY.getWindow().getCurrentFocus();
        if (focusView instanceof TextView) {
            AndroidMeActivity.DEFAULT_ACTIVITY.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) focusView).setCursorVisible(false);
                }
            });
        }
    }

    private static boolean clickText(View view, String clickText) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            int childcount = viewGroup.getChildCount();
            for (int i=0; i < childcount; i++){
                View childView = viewGroup.getChildAt(i);

                if (childView instanceof TextView && childView.isClickable()) {
                    TextView b = (TextView) childView;
                    if (clickText.equalsIgnoreCase(b.getText().toString())) {
                        click(b);
                        return true;
                    }
                }

                // Recursive call
                if (clickText(childView, clickText)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void click(View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        click(xy[0] + view.getWidth() / 2 , xy[1] + view.getHeight() / 2);
    }

    public static void click(float x, float y) {
        Instrumentation inst = new Instrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent event2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
        inst.sendPointerSync(event);
        inst.sendPointerSync(event2);
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
        View[] views = getAllFrames();
        for (View view : views) {
            if (view.hasWindowFocus()) {
                return view;
            }
        }
        return null;
    }

    /**
     * This return all the window, but not in any particular order!
     * @see net.yura.mobile.gui.DesktopPane#getAllFrames()
     */
    public static View[] getAllFrames() {
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
