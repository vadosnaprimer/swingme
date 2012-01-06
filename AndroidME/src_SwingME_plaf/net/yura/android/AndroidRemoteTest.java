package net.yura.android;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Vector;

import net.yura.mobile.util.RemoteTest;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.TextView;

public class AndroidRemoteTest extends RemoteTest {

    public AndroidRemoteTest() {
        Log.d("YURA", "my ip "+getLocalIpAddress());
    }

    @Override
    protected boolean onClickText(String text) {
        View window = getSelectedFrame();

        //System.out.println("Window="+toString(window));

        boolean nativeClick = clickText(window, text); // try native first as menu may be open
        if (!nativeClick && AndroidMeActivity.DEFAULT_ACTIVITY.hasWindowFocus() ) { // TODO check menu is NOT open
            return super.onClickText(text); // Do SwingME click
        }
        return nativeClick;
    }

    @Override
    protected boolean onClickFocusable(final int n1, final int n2) {
        if (AndroidMeActivity.DEFAULT_ACTIVITY.hasWindowFocus()) {
            return super.onClickFocusable(n1, n2);
        }

        View window = getSelectedFrame();

        Vector<View> focusList = new Vector<View>();
        getFocusableViews(window, focusList);

        boolean isValidComponent = (n1 >= 0 && n1 < focusList.size());
        if (isValidComponent) {
            View view = focusList.elementAt(n1);
            if (view instanceof AdapterView) {
                final AdapterView list = (AdapterView) view;
                isValidComponent = (n2 >= 0 && n2 < list.getCount());

                // Note: The item we are about to click may not be visible.
                // performItemClick() seems to do the job but needs to run on
                // the UI thread.
                if (isValidComponent) {
                    AndroidMeActivity.DEFAULT_ACTIVITY.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                list.performItemClick(null, n2, 0);
                            }
                            catch (Throwable e) {}
                        }
                    });
                }
            }
            else {
                click(view);
            }
        }

        return isValidComponent;
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

    @Override
    protected boolean onCommand(String cmd) {
        if ("landscape".equalsIgnoreCase(cmd)) {
            AndroidMeActivity.DEFAULT_ACTIVITY.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return true;
        }

        if ("portrait".equalsIgnoreCase(cmd)) {
            AndroidMeActivity.DEFAULT_ACTIVITY.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }

        return super.onCommand(cmd);
    }

    private static boolean clickText(View view, String clickText) {
        if (view.isShown() && view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            int childcount = viewGroup.getChildCount();
            for (int i=0; i < childcount; i++){
                View childView = viewGroup.getChildAt(i);

                if (childView instanceof TextView && childView.isClickable() && childView.isShown()) {
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

    private static void getFocusableViews(View view, Vector<View> focusList) {
        if (view.isShown()) {
            if (view instanceof AdapterView) { // Always add lists
                focusList.add(view);
            }
            else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;

                int childcount = viewGroup.getChildCount();
                for (int i=0; i < childcount; i++) {
                    // recursive call
                    getFocusableViews(viewGroup.getChildAt(i), focusList);
                }
            }
            else if (view.isClickable() || view instanceof Checkable) {
                // Note: for some weird reason CheckedTextView returns that it's not
                // clickable... but it is. Other Checkable's may be doing he same.
                focusList.add(view);
            }
        }
    }

    public static void click(View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        click(xy[0] + view.getWidth() / 2 , xy[1] + view.getHeight() / 2);
    }

    public static void click(float x, float y) {
        Instrumentation inst = new Instrumentation();
        long downTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent event2 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        inst.sendPointerSync(event);
        inst.sendPointerSync(event2);
    }

    public static String toString(View view) {
        StringBuilder builder = new StringBuilder();
        builder.append( view.getClass().getSimpleName() );
        if (view instanceof TextView) {
            builder.append(" [").append(((TextView)view).getText()).append("]");
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
