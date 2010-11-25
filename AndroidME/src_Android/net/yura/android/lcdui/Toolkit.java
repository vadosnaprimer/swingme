package net.yura.android.lcdui;

public interface Toolkit {

    void invokeAndWait(Runnable r);

    int getScreenWidth();

    int getScreenHeight();
}
