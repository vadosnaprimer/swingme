package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;
import net.yura.android.AndroidMeMIDlet;


import android.view.View;

public class TextBox extends Screen {
    private String text;
    private int maxSize;
    private int constraints;
    private View currentView;

    public TextBox(String title, String text, int maxSize, int constraints) {
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;

        // Hack: Current view could change...
        MIDlet midlet = AndroidMeMIDlet.DEFAULT_ACTIVITY.getMIDlet();
        this.currentView = Display.getDisplay(midlet).getCurrent().getView();
    }

    @Override
    public void disposeDisplayable() {
    }

    @Override
    public View getView() {
        return currentView;
    }

    @Override
    public void initDisplayable(MIDlet midlet) {
        midlet.getToolkit().showNativeTextInput();
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getString() {
        return this.text;
    }

    public void setString(String text) {
        this.text = text;
    }

    public int getConstraints() {
        return this.constraints;
    }

    public void setConstraints(int constraints) {
        this.constraints = constraints;
    }
}
