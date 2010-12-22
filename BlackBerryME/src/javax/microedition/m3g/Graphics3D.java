package javax.microedition.m3g;

import java.util.Hashtable;

import net.yura.blackberry.rim.Graphics;

public final class Graphics3D {

    public static final int TRUE_COLOR = 8;

    private static Graphics3D instance;

    private Graphics targetGraphics;
    private int vpX = 0;
    private int vpY = 0;
    private int vpW = 0;
    private int vpH = 0;

    private Graphics3D() {
    }

    public static Graphics3D getInstance() {

        if (instance == null) {
            instance = new Graphics3D();
        }
        return instance;
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
        	// TODO keep a back buffer, and the scale the bitmap onto it, then paint it to the normal display!
            //targetGraphics.g.drawImage( background.getImage().getImage().getBitmap(), vpX, vpY, vpX + vpW, vpY + vpH, 0, 0, background.getImage().getImage().getWidth(), background.getImage().getImage().getHeight(), null);
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
