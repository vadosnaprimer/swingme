package net.yura.android.plaf;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.plaf.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AndroidBorder implements Border {

    private Drawable drawable;
    private Rect padding;

    public AndroidBorder(Drawable d) {
        this.drawable = d;
        this.padding = new Rect();
        d.getPadding(padding);
    }

    // OREN: we want to override the padding values as on some versions
    // of android the padding is part of the Drawable and on others its
    // part of the border padding.
    public AndroidBorder(Drawable d, Rect extraPadding) {
        this(d);
        padding.set(
            extraPadding.left,
            extraPadding.top,
            extraPadding.right,
            extraPadding.bottom
        );
    }

    public int getBottom() {
        return padding.bottom;
    }

    public int getLeft() {
        return padding.left;
    }

    public int getRight() {
        return padding.right;
    }

    public int getTop() {
        return padding.top;
    }

    public boolean isBorderOpaque() {
        return (drawable.getOpacity() == PixelFormat.OPAQUE);
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {
        setDrawableState(c, drawable);
        android.graphics.Canvas canvas = g.getGraphics().getCanvas();
        canvas.save();
        canvas.concat( Graphics.getMatrix(g.getTransform()) );
        canvas.clipRect(-getLeft(), -getTop(), width+getRight(), height+getBottom());
        drawable.setBounds(-getLeft(), -getTop(), width+getRight(), height+getBottom());
        drawable.draw(canvas);
        canvas.restore();
    }

    static int[] getDrawableState(int state,Class<?> cclass,boolean windowFocused) {
        Vector<Integer> stateList = new Vector<Integer>(3);

        if (windowFocused) {
            stateList.add(new Integer(android.R.attr.state_window_focused));
        }

        if ((state & Style.DISABLED) == 0) {
            stateList.add(new Integer(android.R.attr.state_enabled));
        }

        // List hack:

        // SwingME lists have 2 states, selected and focused
        // Android lists have 2 states, focused and pressed

        // SwingME selected -> Android focused
        // SwingME focused -> Android pressed

        if ((state & Style.FOCUSED) != 0) {
            if (cclass!=null && ListCellRenderer.class.isAssignableFrom(cclass)) {
                stateList.add(new Integer(android.R.attr.state_pressed));
            }
            //else { // we need this or the list text is the wrong color, as in Android u can NOT have pressed and not focused
                stateList.add(new Integer(android.R.attr.state_focused));
            //}
        }

        if ((state & Style.SELECTED) != 0) {
            if (cclass!=null && RadioButton.class.isAssignableFrom(cclass)) {
                stateList.add(new Integer(android.R.attr.state_checked));
            }
            else if (cclass!=null && Button.class.isAssignableFrom(cclass)) {
                stateList.add(new Integer(android.R.attr.state_pressed));
            }
            else if (cclass!=null && ListCellRenderer.class.isAssignableFrom(cclass)) {
                stateList.add(new Integer(android.R.attr.state_focused));
            }
            // commenting out the else seems to fix the foreground color of Lists
            //else {// other things ???? this is not currently used for anything ????
            //    System.out.println(" SELECTED ########################## "+cclass);
                stateList.add(new Integer(android.R.attr.state_selected));
            //}
        }

        int[] res = new int[stateList.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = stateList.elementAt(i).intValue();
        }

        return res;
    }

    static void setDrawableState(Component comp, Drawable drawable) {
        Window w = comp.getWindow();
        drawable.setState(getDrawableState(comp.getCurrentState(),comp.getClass(), w==null || w.isFocused() ));
    }

}
