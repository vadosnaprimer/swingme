package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.Log;

/**
 * @API MIDP-2.0 
 */
public abstract class GameCanvas extends Canvas {

	/**
	 * @API MIDP-2.0 
	 */
	public static final int UP_PRESSED = 0x0002;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int DOWN_PRESSED = 0x0040;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int LEFT_PRESSED = 0x0004;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int RIGHT_PRESSED = 0x0020;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int FIRE_PRESSED = 0x0100;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int GAME_A_PRESSED = 0x0200;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int GAME_B_PRESSED = 0x0400;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int GAME_C_PRESSED = 0x0800;

	/**
	 * @API MIDP-2.0 
	 */
	public static final int GAME_D_PRESSED = 0x1000;

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
    
    boolean supressKeyEvents;
    Image buffer;

    
	protected GameCanvas(boolean suppressKeyEvents) {
        this.supressKeyEvents = suppressKeyEvents;
	}

    private Image getBuffer(){
        
        if(buffer == null){
            boolean saveMode = _fullScreenMode;
            setFullScreenMode(true);
       //     System.out.println("Allocating game canvas buffer: "+getWidth()+"x"+getHeight());
            buffer = Image.createImage(getWidth(), getHeight());
            setFullScreenMode(saveMode);
        }
        return buffer;
    }
    
    
	/**
	 * @API MIDP-2.0 
	 */
	protected Graphics getGraphics() {
		return getBuffer().getGraphics();
	}

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
	public int getKeyStates() {
		System.out.println("GameCanvas.getKeyStates() called with no effect!");
		return ApplicationManager.getInstance().keyStates;
	}

	/**
	 * @API MIDP-2.0 
	 */
    
	public void paint(Graphics g) {
       g.drawImage(getBuffer(), 0, 0, 0);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void flushGraphics(int x, int y, int width, int height) {
        Log.log(Log.DRAW_EVENTS, "flushGraphics() start");
        
	    repaint(x, y, width, height);
        serviceRepaints();
        
        Log.log(Log.DRAW_EVENTS, "flushGraphics() end");
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void flushGraphics() {
        repaint();
        serviceRepaints();
	}
}
