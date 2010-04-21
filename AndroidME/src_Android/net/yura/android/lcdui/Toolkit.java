package net.yura.android.lcdui;

import android.os.Handler;
import android.view.View;

public interface Toolkit {
    Handler getHandler();

    void invokeAndWait(Runnable r);

    View inflate(int resourceId);

    void addOverlayView(View v);

    void removeOverlayView(View v);

    int getScreenWidth();

    int getScreenHeight();
}
