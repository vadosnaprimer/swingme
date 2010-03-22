package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;



public abstract class GameCanvas extends Canvas {
    // keystate constants
    public static final int UP_PRESSED = 1 << Canvas.UP;
    public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
    public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
    public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
    public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
    public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
    public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
    public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
    public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;

    // true if this GameCanvas doesn't generate
    // key events for the game keys
    private boolean suppressKeyEvents;
    // the latched state of the keys
    // reseted on call to getKeyStates
    private int latchedKeyState;
    // the current keys state
    // this is copied to latchedKeyState
    // on call to getKeyState
    private int actualKeyState;

    Image offscreenBuffer;



    /** Creates a new instance of GameCanvas */
    protected GameCanvas(boolean suppressKeyEvents)
    {
        this.suppressKeyEvents = suppressKeyEvents;
        // never should the size of the Canvas become greater than this
        // if the size reported by Canvas.getXXX() isn't the maximum for
        // a given Canvas implementation other methods should be used to
        // obtain the maximum possible size for such particular implementation
        this.offscreenBuffer = Image.createImage(this.getWidth(), this.getHeight());
    }

    protected Graphics getGraphics() {
        return offscreenBuffer.getGraphics();
    }

    public void paint(Graphics g) {
        g.drawImage(offscreenBuffer, 0, 0, Graphics.TOP | Graphics.LEFT);
    }

    public void flushGraphics(int x, int y, int width, int height) {
        // i dont know if this is correct, the specification is a little
        // vague here.
        // For example this methods fail if the paint method is
        // overriden with something else
        repaint(x, y, width, height);
        serviceRepaints();
    }

    public void flushGraphics() {

        // NOTE see comment at the other flushGraphics method
        repaint();
        serviceRepaints();

        try {
        	//TODO: fix serviceRepaints()
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public int getKeyStates() {
    	synchronized(this) {
	        int ret = latchedKeyState;
	        latchedKeyState = actualKeyState;
	        return ret;
    	}
    }

    // TODO
    // should isDoubleBuffered from class Canvas return true in this class??
}


