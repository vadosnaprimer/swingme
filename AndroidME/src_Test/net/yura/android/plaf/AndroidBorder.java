package net.yura.android.plaf;

import java.util.Vector;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.Style;
import android.graphics.drawable.Drawable;

public class AndroidBorder implements Border {

    private Drawable drawable;

    public AndroidBorder(Drawable d) {
        drawable = d;
    }

    public int getBottom() {
        return 0;
    }

    public int getLeft() {
        return 0;
    }

    public int getRight() {
        return 0;
    }

    public int getTop() {
        return 0;
    }

    public boolean isBorderOpaque() {
        System.out.println("getOpacity= "+drawable.getOpacity());
        return true;
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {

        if (c instanceof Button) {
            System.out.println(">>>");
        }

        int state = c.getCurrentState();
        setStateSet(state, drawable);

        android.graphics.Canvas canvas = g.getGraphics().getCanvas();
        drawable.setBounds(-getLeft(), -getTop(), width+getRight(), height+getBottom());
        drawable.draw(canvas);
    }

    static void setStateSet(int state, Drawable drawable) {
        Vector stateList = new Vector(3);

        stateList.add(new Integer(android.R.attr.state_window_focused));

        if ((state & Style.DISABLED) != 0) {

        }
        else {
            stateList.add(new Integer(android.R.attr.state_enabled));
        }

        if ((state & Style.FOCUSED) != 0) {
            stateList.add(new Integer(android.R.attr.state_focused));
            stateList.add(new Integer(android.R.attr.state_active));
        }

        if ((state & Style.SELECTED) != 0) {
            stateList.add(new Integer(android.R.attr.state_pressed));
        }

        int[] stateSet = new int[stateList.size()];
        for (int i = 0; i < stateSet.length; i++) {
            stateSet[i] = ((Integer) stateList.elementAt(i)).intValue();
        }
        drawable.setState(stateSet);
    }

}
