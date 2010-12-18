package javax.microedition.lcdui;


/**
 * @author Stefan Haustein
 *
 * @ME4SE INTERNAL
 */

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import org.me4se.impl.Log;
import org.me4se.scm.*;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.ApplicationManager;


class ScmCanvas extends ScmComponent {

	Canvas canvas;
	boolean first = true;
	int keyCode;
	Object repaintLock = new Object();
	boolean repaintPending;

        private BufferedImage offscreen;
    
	ScmCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public void paint(java.awt.Graphics g) {

		//  g.drawLine (0, 50, 50, 0);
		//System.out.println ("paint called");
        
		try {
			synchronized (repaintLock) {

				ApplicationManager manager = ApplicationManager.getInstance();

				//java.awt.Dimension d = getSize();

                                // YURA FIX
				if (offscreen == null || offscreen.getWidth() != getWidth() || offscreen.getHeight() != getHeight()) {
					offscreen = new BufferedImage(
							getWidth(),
							getHeight(), BufferedImage.TYPE_INT_RGB);
				}

				if (repaintPending) {
					Graphics mg =
						new Graphics(
							canvas,
							offscreen,
							offscreen.getGraphics());
                    
					if (mg != null) {
						repaintPending = false;  // moved up here to allow the request of a repaint in paint
					
					   Log.log(Log.DRAW_EVENTS, "ScmCanvas.paint() entering");
                        
                        if(canvas.oldW != canvas.getWidth() || canvas.oldH != canvas.getHeight()){
                            try{
                                canvas.sizeChanged(canvas.getWidth(), canvas.getHeight());
                                canvas.oldW = canvas.getWidth();
                                canvas.oldH = canvas.getHeight();
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                      Rectangle clip = g.getClipBounds();
                      mg.clipRect(clip.x, clip.y, clip.width, clip.height);

                        canvas.paint(mg);  // thanks for the fix to Steven Lagerweij
                      
                        Log.log(Log.DRAW_EVENTS, "ScmCanvas.paint() left");

                        
//						mg.stale = true;
						if (canvas.videoFrameImage != null)
							mg.drawImage(canvas.videoFrameImage, 
									canvas.videoFrameX, canvas.videoFrameY,
									Graphics.TOP | Graphics.LEFT);
						
					}
					else repaint ();
				}

				//TODO: Clarify under which circumstances g may be null ... 
				
				if(g != null){
				g.drawImage(
					offscreen,
					0,
					0,
					manager.awtContainer);
				}

                                if (pointx!=0&&pointy!=0) {
                                    g.setColor(Color.RED);
                                    drawPoint(g,pointx, pointy);
                                    drawPoint(g,getWidth() - pointx, getHeight() - pointy);
                                }

				repaintLock.notify();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        
		//System.out.println ("paint left");
		//g.drawLine (0, 0, 999, 999);
	}

    private void drawPoint(java.awt.Graphics g,int x,int y) {
        g.drawLine(x, y+5, x, y-5);
        g.drawLine(x+5, y, x-5, y);
    }

        // copy from DesktopPane in SwingME
    public static final int DRAGGED = 0;
    public static final int PRESSED = 1;
    public static final int RELEASED = 2;

    int pointx,pointy;

	public boolean mouseDragged(int x, int y, int modifiers) {

		if(canvas.hasPointerEvents)
			canvas.pointerDragged(x, y);

            if (pointx!=0||pointy!=0) {
                pointx = x;
                pointy = y;
                repaint();
                canvas.multitouchEvent(new int[] {DRAGGED,DRAGGED}, new int[]{x,getWidth()-x}, new int[]{y,getHeight()-y});
            }

		return true;
	}

	public boolean mousePressed(int button, int x, int y, int modifiers) {
		if (button != 1) return false;

	//	if(canvas.hasPointerEvents)
			canvas.pointerPressed(x, y);

            // we need to do this after the pointerDragged so we can setup what componet will get the events in the pointerDragged
            if ((modifiers & InputEvent.ALT_MASK) == InputEvent.ALT_MASK) {
                pointx = x;
                pointy = y;
                repaint();
                canvas.multitouchEvent(new int[] {PRESSED,PRESSED}, new int[]{x,getWidth()-x}, new int[]{y,getHeight()-y});
            }

		return true;
	}

	public boolean mouseReleased(int button, int x, int y, int modifiers) {
		if (button != 1) return false;

                // we need to call this first as we dont want the current component to be set to null before it gets the multitouch event
            if (pointx!=0||pointy!=0) {
                pointx = 0;
                pointy = 0;
                repaint();
                canvas.multitouchEvent(new int[] {RELEASED,RELEASED}, new int[]{x,getWidth()-x}, new int[]{y,getHeight()-y});
            }

	//	if(canvas.hasPointerEvents)
			canvas.pointerReleased(x, y);

		return true;
	}

        public boolean mouseMoved(int x, int y, int modifiers) {
            canvas.pointerMoved(x, y);
            return true;
        }

	public boolean keyPressed(String code) {
		//System.out.println ("key: "+code);
        
        Log.log(Log.INPUT_EVENTS, "ScmCanvas.keyPressed() entering; key: "+code);
        
        int kc = ApplicationManager.getInstance().getDeviceKeyCode(code);
        ApplicationManager.getInstance().keyStates |= getKeyFlag(kc);
        
		canvas.keyPressed(kc);        	

        Log.log(Log.INPUT_EVENTS, "ScmCanvas.keyPressed() leaving");

		return true;
	}

	public boolean keyRepeated(String code) {
		//System.out.println ("key: "+ev+" decoded: "+decode(ev));
        
        Log.log(Log.INPUT_EVENTS, "ScmCanvas.keyRepeated() entering; key: "+code);
        
		canvas.keyRepeated(ApplicationManager.getInstance().getDeviceKeyCode(code));        	

        Log.log(Log.INPUT_EVENTS, "ScmCanvas.keyRepeated() leaving");

		return true;
	}

	
    int getKeyFlag(int code){
        switch(canvas.getGameAction(code)){
        case Canvas.UP: return GameCanvas.UP_PRESSED;
        case Canvas.DOWN: return GameCanvas.DOWN_PRESSED;
        case Canvas.LEFT: return GameCanvas.LEFT_PRESSED;
        case Canvas.RIGHT: return GameCanvas.RIGHT_PRESSED;
        case Canvas.FIRE: return GameCanvas.FIRE_PRESSED;
        case Canvas.GAME_A: return GameCanvas.GAME_A_PRESSED;
        case Canvas.GAME_B: return GameCanvas.GAME_B_PRESSED;
        case Canvas.GAME_C: return GameCanvas.GAME_C_PRESSED;
        case Canvas.GAME_D: return GameCanvas.GAME_D_PRESSED;
        }
        return 0;
    }

	public boolean keyReleased(String code) {

		Log.log(Log.INPUT_EVENTS, "ScmCanvas.keyReleased() entering; key: "+code);

		
        int kc = ApplicationManager.getInstance().getDeviceKeyCode(code);
        ApplicationManager.getInstance().keyStates &= ~getKeyFlag(kc);

		canvas.keyReleased(kc); 

        Log.log(Log.INPUT_EVENTS, "ScmCanvas.keyReleased() leaving");

		return true;
	}

}


