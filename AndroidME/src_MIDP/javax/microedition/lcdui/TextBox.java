package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;
import net.yura.android.AndroidMeMIDlet;


import android.view.View;

public class TextBox extends Screen {
    private String text;
    private int maxSize;
    private int constraints;
    private Canvas.CanvasView currentCanvasView;

    public TextBox(String title, String text, int maxSize, int constraints) {
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;

        // Hack: Current view could change...
        MIDlet midlet = AndroidMeMIDlet.DEFAULT_ACTIVITY.getMIDlet();
        View view = Display.getDisplay(midlet).getCurrent().getView();

        if (view instanceof Canvas.CanvasView) {
            this.currentCanvasView = (Canvas.CanvasView) view;
        }
    }

    @Override
    public void disposeDisplayable() {
    }

    @Override
    public View getView() {
        return currentCanvasView;
    }

    @Override
    public void initDisplayable(MIDlet midlet) {
        if (currentCanvasView != null) {
            currentCanvasView.showNativeTextInput();
        }
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
