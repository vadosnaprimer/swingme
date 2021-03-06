package javax.microedition.m3g;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;

public final class Graphics3D {

    public static final int TRUE_COLOR = 8;

    private Graphics targetGraphics;
    private int vpX = 0;
    private int vpY = 0;
    private int vpW = 0;
    private int vpH = 0;

    private Graphics3D() {
    }

    public static Graphics3D getInstance() {
        // do not keep a instance, as this may cause memory leaks and also is not thead safe
        return new Graphics3D();
    }

    public void bindTarget(Object target, boolean depthBuffer, int hints) {

        targetGraphics = (Graphics) target;
    }

    public void releaseTarget() {

    }

    public void clear(Background background) {
        if (background == null) {
            // clear to black
            targetGraphics.setColor(0xff000000);
            targetGraphics.fillRect(vpX, vpY, vpW, vpH);
        }
        else if (background.getImage() == null) {
            // clear to target color
            targetGraphics.setColor(background.getColor());
            targetGraphics.fillRect(vpX, vpY, vpW, vpH);
        }
        else {
            targetGraphics._getAwtGraphics().drawImage(background.getImage().getImage()._image, vpX, vpY, vpW, vpH, null);
        }
    }

    public void setViewport(int x, int y, int width, int height) {
        this.vpX = x;
        this.vpY = y;
        this.vpW = width;
        this.vpH = height;
    }

    public static Hashtable getProperties() {
        return new Hashtable(0);
    }

}
