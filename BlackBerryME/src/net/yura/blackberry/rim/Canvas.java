package net.yura.blackberry.rim;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.Keypad;

/**
* Provides the base screen / displayable implementation for BlackBerry devices.
* 
* @since J2ME Polish 2.1
* @author robert virkus, j2mepolish@enough.de 
*/
public abstract class Canvas extends MainScreen {

    public static final int UP = javax.microedition.lcdui.Canvas.UP;
    public static final int DOWN = javax.microedition.lcdui.Canvas.DOWN;
    public static final int LEFT = javax.microedition.lcdui.Canvas.LEFT;
    public static final int RIGHT = javax.microedition.lcdui.Canvas.RIGHT;
    public static final int FIRE = javax.microedition.lcdui.Canvas.FIRE;

    public static final int GAME_A = javax.microedition.lcdui.Canvas.GAME_A;
    public static final int GAME_B = javax.microedition.lcdui.Canvas.GAME_B;
    public static final int GAME_C = javax.microedition.lcdui.Canvas.GAME_C;
    public static final int GAME_D = javax.microedition.lcdui.Canvas.GAME_D;

    public static final int KEY_NUM0 = javax.microedition.lcdui.Canvas.KEY_NUM0;
    public static final int KEY_NUM1 = javax.microedition.lcdui.Canvas.KEY_NUM1;
    public static final int KEY_NUM2 = javax.microedition.lcdui.Canvas.KEY_NUM2;
    public static final int KEY_NUM3 = javax.microedition.lcdui.Canvas.KEY_NUM3;
    public static final int KEY_NUM4 = javax.microedition.lcdui.Canvas.KEY_NUM4;
    public static final int KEY_NUM5 = javax.microedition.lcdui.Canvas.KEY_NUM5;
    public static final int KEY_NUM6 = javax.microedition.lcdui.Canvas.KEY_NUM6;
    public static final int KEY_NUM7 = javax.microedition.lcdui.Canvas.KEY_NUM7;
    public static final int KEY_NUM8 = javax.microedition.lcdui.Canvas.KEY_NUM8;
    public static final int KEY_NUM9 = javax.microedition.lcdui.Canvas.KEY_NUM9;
    public static final int KEY_STAR = javax.microedition.lcdui.Canvas.KEY_NUM9;
    public static final int KEY_POUND = javax.microedition.lcdui.Canvas.KEY_POUND;
	
 /**
  * Constructs a new <code>Canvas</code> object.
  */
 protected Canvas() {

	 add(new RichTextField("Hello YURA World!")); 
 }

 public int getKeyCode(int gameAction)
 {
     switch ( gameAction ) {
	     case Canvas.FIRE: return -5;
	     case Canvas.UP: return -1;
	     case Canvas.DOWN: return -2;
	     case Canvas.LEFT: return -3;
	     case Canvas.RIGHT: return -4;
     }
     return 0;
 }

 public String getKeyName(int keyCode)
 {
         return "" + Keypad.getUnaltedChar( (char) keyCode );
 }

 /**
  * Gets the game action associated with the given key code of the
  * device.  Returns zero if no game action is associated with this key
  * code.  See <a href="#gameactions">above</a> for further discussion of
  * game actions.
  * 
  * <P>The mapping between key codes and game actions
  * will not change during the execution of the application.</P>
  * 
  * @param keyCode - the key code
  * @return the game action corresponding to this key, or  0 if none
  * @throws IllegalArgumentException - if keyCode is not a valid key code
  */
 public int getGameAction(int keyCode)
 {
     switch (keyCode) {
	     case -1: 	return Canvas.UP;
	     case -2:	return Canvas.DOWN;
	     case -3: 	return Canvas.LEFT;
	     case -4: 	return Canvas.RIGHT;
	     case '\n':
	     case ' ':
	     case -5: 	return Canvas.FIRE;
	     case Canvas.KEY_NUM2: 		return Canvas.UP; // 2
	     case Canvas.KEY_NUM4: 		return Canvas.LEFT; // 4
	     case Canvas.KEY_NUM6: 		return Canvas.RIGHT; // 6
	     case Canvas.KEY_NUM8: 		return Canvas.DOWN; // 8
     }
     int key = Keypad.key( keyCode );
     switch ( key ) {
     	case Keypad.KEY_ENTER: return Canvas.FIRE;
     	case Keypad.KEY_SPACE: return Canvas.FIRE;
     	case Keypad.KEY_NEXT: return Canvas.DOWN;
     }

     return 0;
 }

 /**
  * Controls whether the <code>Canvas</code> is in full-screen mode
  * or in normal mode.
  * 
  * @param mode true if the Canvas is to be in full screen mode, false otherwise
  * @since  MIDP 2.0
  */
 public void setFullScreenMode(boolean mode) {

 }

 /**
  * Called when a key is pressed.
  * 
  * <P>The <code>getGameAction()</code> method can be called to
  * determine what game action, if any, is mapped to the key.
  * Class <code>Canvas</code> has an empty implementation of this method, and
  * the subclass has to redefine it if it wants to listen this method.
  * 
  * @param keyCode - the key code of the key that was pressed
  */
 protected void keyPressed(int keyCode)
 {
         // do nothing
 }

 /**
  * Called when a key is repeated (held down).
  * 
  * <P>The <code>getGameAction()</code> method can
  * be called to determine what game action,
  * if any, is mapped to the key.
  * Class <code>Canvas</code> has an empty implementation of this method, and
  * the subclass has to redefine it if it wants to listen this method.
  * </P>
  * 
  * @param keyCode - the key code of the key that was repeated
  * @see #hasRepeatEvents()
  */
 protected void keyRepeated(int keyCode)
 {
         // do nothing
 }

 /**
  * Called when a key is released.
  * <P>
  * The <code>getGameAction()</code> method can be called to
  * determine what game action, if any, is mapped to the key.
  * Class <code>Canvas</code> has an empty implementation of this method, and
  * the subclass has to redefine it if it wants to listen this method.
  * </P>
  * 
  * @param keyCode - the key code of the key that was released
  */
 protected void keyReleased(int keyCode)
 {
         // do nothing
 }

 /**
  * Called when the pointer is pressed.
  * 
  * <P>
  * The <A HREF="../../../javax/microedition/lcdui/Canvas.html#hasPointerEvents()"><CODE>hasPointerEvents()</CODE></A>
  * method may be called to determine if the device supports pointer events.
  * Class <code>Canvas</code> has an empty implementation of this method, and
  * the subclass has to redefine it if it wants to listen this method.
  * </P>
  * 
  * @param x - the horizontal location where the pointer was pressed (relative to the Canvas)
  * @param y - the vertical location where the pointer was pressed (relative to the Canvas)
  */
 protected void pointerPressed(int x, int y)
 {
         // do nothing
 }

 /**
  * Called when the pointer is released.
  * 
  * <P>
  * The <A HREF="../../../javax/microedition/lcdui/Canvas.html#hasPointerEvents()"><CODE>hasPointerEvents()</CODE></A>
  * method may be called to determine if the device supports pointer events.
  * Class <code>Canvas</code> has an empty implementation of this method, and
  * the subclass has to redefine it if it wants to listen this method.
  * </P>
  * 
  * @param x - the horizontal location where the pointer was released (relative to the Canvas)
  * @param y - the vertical location where the pointer was released (relative to the Canvas)
  */
 protected void pointerReleased(int x, int y)
 {
         // do nothing
 }

 /**
  * Called when the pointer is dragged.
  * 
  * <P>
  * The <A HREF="../../../javax/microedition/lcdui/Canvas.html#hasPointerMotionEvents()"><CODE>hasPointerMotionEvents()</CODE></A>
  * method may be called to determine if the device supports pointer events.
  * Class <code>Canvas</code> has an empty implementation of this method, and
  * the subclass has to redefine it if it wants to listen this method.
  * </P>
  * 
  * @param x - the horizontal location where the pointer was dragged (relative to the Canvas)
  * @param y - the vertical location where the pointer was dragged (relative to the Canvas)
  */
 protected void pointerDragged(int x, int y)
 {
         // do nothing
 }
 
 /**
	 * Handles a touch down/press event. 
	 * This is similar to a pointerPressed event, however it is only available on devices with screens that differentiate
	 * between press and touch events (read: BlackBerry Storm).
	 * 
	 * @param x the absolute horizontal pixel position of the touch event 
	 * @param y  the absolute vertical pixel position of the touch event
	 * @return true when the event was handled
	 */
	public boolean handlePointerTouchDown( int x, int y ) {
		return false;
	}
	

	/**
	 * Handles a touch up/release event. 
	 * This is similar to a pointerReleased event, however it is only available on devices with screens that differentiate
	 * between press and touch events (read: BlackBerry Storm).
	 * 
	 * @param x the absolute horizontal pixel position of the touch event 
	 * @param y  the absolute vertical pixel position of the touch event
	 * @return true when the event was handled
	 */
	public boolean handlePointerTouchUp( int x, int y ) {
		return false;
	}
	

 /**
  * Requests a repaint for the specified region of the <code>Canvas</code>. 
  * Calling
  * this method may result in subsequent call to
  * <code>paint()</code>, where the passed
  * <code>Graphics</code> object's clip region will include at
  * least the specified
  * region.
  * 
  * <p> If the canvas is not visible, or if width and height are zero or
  * less, or if the rectangle does not specify a visible region of
  * the display, this call has no effect. </p>
  * 
  * <p> The call to <code>paint()</code> occurs asynchronously of
  * the call to <code>repaint()</code>.
  * That is, <code>repaint()</code> will not block waiting for
  * <code>paint()</code> to finish. The
  * <code>paint()</code> method will either be called after the
  * caller of <code>repaint(</code>)
  * returns
  * to the implementation (if the caller is a callback) or on another thread
  * entirely. </p>
  * 
  * <p> To synchronize with its <code>paint()</code> routine,
  * applications can use either
  * <A HREF="../../../javax/microedition/lcdui/Display.html#callSerially(java.lang.Runnable)"><CODE>Display.callSerially()</CODE></A> or
  * <A HREF="../../../javax/microedition/lcdui/Canvas.html#serviceRepaints()"><CODE>serviceRepaints()</CODE></A>, or they can code explicit
  * synchronization into their <code>paint()</code> routine. </p>
  * 
  * <p> The origin of the coordinate system is above and to the left of the
  * pixel in the upper left corner of the displayable area of the
  * <code>Canvas</code>.
  * The X-coordinate is positive right and the Y-coordinate is
  * positive downwards.
  * </p>
  * 
  * @param x - the x coordinate of the rectangle to be repainted
  * @param y - the y coordinate of the rectangle to be repainted
  * @param width - the width of the rectangle to be repainted
  * @param height - the height of the rectangle to be repainted
  * @see Display#callSerially(Runnable)
  * @see #serviceRepaints()
  */
 public final void repaint(int x, int y, int width, int height)
 {
     invalidate( x, y, width, height );
 }

 /**
  * Requests a repaint for the entire <code>Canvas</code>. The
  * effect is identical to
  * <p><code>repaint(0, 0, getWidth(), getHeight());</code>
  * 
  */
 public final void repaint()
 {
     invalidate();
 }

 /**
  * Forces any pending repaint requests to be serviced immediately. This
  * method blocks until the pending requests have been serviced. If
  * there are
  * no pending repaints, or if this canvas is not visible on the display,
  * this call does nothing and returns immediately.
  * 
  * <p><strong>Warning:</strong> This method blocks until the call to the
  * application's <code>paint()</code> method returns. The
  * application has no
  * control over
  * which thread calls <code>paint()</code>; it may vary from
  * implementation to
  * implementation. If the caller of <code>serviceRepaints()</code>
  * holds a lock that the
  * <code>paint()</code> method acquires, this may result in
  * deadlock. Therefore, callers
  * of <code>serviceRepaints()</code> <em>must not</em> hold any
  * locks that might be
  * acquired within the <code>paint()</code> method. The
  * <A HREF="../../../javax/microedition/lcdui/Display.html#callSerially(java.lang.Runnable)"><CODE>Display.callSerially()</CODE></A>
  * method provides a facility where an application can be called back after
  * painting has completed, avoiding the danger of deadlock.
  * </p>
  * 
  * @see Display#callSerially(Runnable)
  */
 public final void serviceRepaints()
 {
	 // not used in SwingME
 }

 /**
  * The implementation calls <code>showNotify()</code>
  * immediately prior to this <code>Canvas</code> being made
  * visible on the display.
  * Canvas subclasses may override
  * this method to perform tasks before being shown, such
  * as setting up animations, starting timers, etc.
  * 
  */
 protected void showNotify()
 {
 	// do nothing
 }

 /**
  * The implementation calls <code>hideNotify()</code> shortly
  * after the <code>Canvas</code> has been
  * removed from the display.
  * <code>Canvas</code> subclasses may override this method in
  * order to pause
  * animations,
  * revoke timers, etc.  The default implementation of this
  * method in class <code>Canvas</code> is empty.
  * 
  */
 protected void hideNotify()
 {
 	// do nothing

 }

 /**
  * Renders the <code>Canvas</code>. The application must implement
  * this method in
  * order to paint any graphics.
  * 
  * <p>The <code>Graphics</code> object's clip region defines the
  * area of the screen
  * that is considered to be invalid. A correctly-written
  * <code>paint()</code> routine
  * must paint <em>every</em> pixel within this region. This is necessary
  * because the implementation is not required to clear the region prior to
  * calling <code>paint()</code> on it.  Thus, failing to paint
  * every pixel may result
  * in a portion of the previous screen image remaining visible. </p>
  * 
  * <p>Applications <em>must not</em> assume that
  * they know the underlying source of the <code>paint()</code>
  * call and use this
  * assumption
  * to paint only a subset of the pixels within the clip region. The
  * reason is
  * that this particular <code>paint()</code> call may have
  * resulted from multiple
  * <code>repaint()</code>
  * requests, some of which may have been generated from outside the
  * application. An application that paints only what it thinks is
  * necessary to
  * be painted may display incorrectly if the screen contents had been
  * invalidated by, for example, an incoming telephone call. </p>
  * 
  * <p>Operations on this graphics object after the <code>paint()
  * </code>call returns are
  * undefined. Thus, the application <em>must not</em> cache this
  * <code>Graphics</code>
  * object for later use or use by another thread. It must only be
  * used within
  * the scope of this method. </p>
  * 
  * <p>The implementation may postpone visible effects of
  * graphics operations until the end of the paint method.</p>
  * 
  * <p> The contents of the <code>Canvas</code> are never saved if
  * it is hidden and then
  * is made visible again. Thus, shortly after
  * <code>showNotify()</code> is called,
  * <code>paint()</code> will always be called with a
  * <code>Graphics</code> object whose clip region
  * specifies the entire displayable area of the
  * <code>Canvas</code>.  Applications
  * <em>must not</em> rely on any contents being preserved from a previous
  * occasion when the <code>Canvas</code> was current. This call to
  * <code>paint()</code> will not
  * necessarily occur before any other key or pointer
  * methods are called on the <code>Canvas</code>.  Applications
  * whose repaint
  * recomputation is expensive may create an offscreen
  * <code>Image</code>, paint into it,
  * and then draw this image on the <code>Canvas</code> when
  * <code>paint()</code> is called. </p>
  * 
  * <P>The application code must never call <code>paint()</code>;
  * it is called only by
  * the implementation.</P>
  * 
  * <P>The <code>Graphics</code> object passed to the
  * <code>paint()</code> method has the following
  * properties:</P>
  * <UL>
  * <LI>the destination is the actual display, or if double buffering is in
  * effect, a back buffer for the display;</LI>
  * <LI>the clip region includes at least one pixel
  * within this <code>Canvas</code>;</LI>
  * <LI>the current color is black;</LI>
  * <LI>the font is the same as the font returned by
  * <A HREF="../../../javax/microedition/lcdui/Font.html#getDefaultFont()"><CODE>Font.getDefaultFont()</CODE></A>;</LI>
  * <LI>the stroke style is <A HREF="../../../javax/microedition/lcdui/Graphics.html#SOLID"><CODE>SOLID</CODE></A>;</LI>
  * <LI>the origin of the coordinate system is located at the upper-left
  * corner of the <code>Canvas</code>; and</LI>
  * <LI>the <code>Canvas</code> is visible, that is, a call to
  * <code>isShown()</code> will return
  * <code>true</code>.</LI>
  * </UL>
  * 
  * @param g - the Graphics object to be used for rendering the Canvas
  */
 protected abstract void paint( Graphics g);

 /**
  * Called when the drawable area of the <code>Canvas</code> has
  * been changed.  This
  * method has augmented semantics compared to <A HREF="../../../javax/microedition/lcdui/Displayable.html#sizeChanged(int, int)"><CODE>Displayable.sizeChanged</CODE></A>.
  * 
  * <p>In addition to the causes listed in
  * <code>Displayable.sizeChanged</code>, a size change can occur on a
  * <code>Canvas</code> because of a change between normal and
  * full-screen modes.</p>
  * 
  * <p>If the size of a <code>Canvas</code> changes while it is
  * actually visible on the
  * display, it may trigger an automatic repaint request.  If this occurs,
  * the call to <code>sizeChanged</code> will occur prior to the call to
  * <code>paint</code>.  If the <code>Canvas</code> has become smaller, the
  * implementation may choose not to trigger a repaint request if the
  * remaining contents of the <code>Canvas</code> have been
  * preserved.  Similarly, if
  * the <code>Canvas</code> has become larger, the implementation
  * may choose to trigger
  * a repaint only for the new region.  In both cases, the preserved
  * contents must remain stationary with respect to the origin of the
  * <code>Canvas</code>.  If the size change is significant to the
  * contents of the
  * <code>Canvas</code>, the application must explicitly issue a
  * repaint request for the
  * changed areas.  Note that the application's repaint request should not
  * cause multiple repaints, since it can be coalesced with repaint
  * requests that are already pending.</p>
  * 
  * <p>If the size of a <code>Canvas</code> changes while it is not
  * visible, the
  * implementation may choose to delay calls to <code>sizeChanged</code>
  * until immediately prior to the call to <code>showNotify</code>.  In
  * that case, there will be only one call to <code>sizeChanged</code>,
  * regardless of the number of size changes.</p>
  * 
  * <p>An application that is sensitive to size changes can update instance
  * variables in its implementation of <code>sizeChanged</code>.  These
  * updated values will be available to the code in the
  * <code>showNotify</code>, <code>hideNotify</code>, and
  * <code>paint</code> methods.</p>
  * 
  * @param w - the new width in pixels of the drawable area of the Canvas
  * @param h - the new height in pixels of the drawable area of the Canvas
  * @see Displayable#sizeChanged in class Displayable
  * @since  MIDP 2.0
  */
 protected void sizeChanged(int w, int h)
 {
         // do nothing
 }

// protected void paint( net.rim.device.api.ui.Graphics g ) {
//   // do nothing..
// }
 
 //#if !polish.useFullScreen
 protected void paint( net.rim.device.api.ui.Graphics g ) {
	    // when extending the BB MainScreen, super.paint(g) will
 	// clear the paint area, subpaint(g) will only render the fields.
	    super.subpaint(g);
}
 //#endif

 protected void paintBackground( net.rim.device.api.ui.Graphics g ) {
     //System.out.println("Canvas.paintBackground(): enter");
 	try {
	        //this.graphics.setGraphics( g );
	    	//paint( this.graphics );
 	} catch (Exception e) {
 		//#debug error
 		System.out.println("unable to paint screen " + this + e );
 	}
 }
 
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Screen#onExposed()
	 */
	protected void onExposed() {
		//this.isObscured = false;
		super.onExposed();
	}
	
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Screen#onObscured()
	 */
	protected void onObscured() {
		//this.isObscured = true;
		super.onObscured();
	}
	
protected void onDisplay() {
     super.onDisplay();
     showNotify();
 }
 
 protected void onUndisplay() {
     super.onUndisplay();
     hideNotify();
 }


	/* (non-Javadoc)
  * @see net.rim.device.api.ui.container.FullScreen#sublayout(int, int)
  */
 protected void sublayout(int width, int height) {
         super.sublayout(width, height);
         int w = net.rim.device.api.ui.Graphics.getScreenWidth();
         int h = net.rim.device.api.ui.Graphics.getScreenHeight();
         //if (w != this.lastWidth || h != this.lastHeight) {
         	//this.lastWidth = w;
         	//this.lastHeight = h;
         	setExtent( w,  h );
         	sizeChanged( w, h );
         //}
 }


	/**
	 * Translates a BlackBerry key event into a MIDP event
	 * @param keyCode the BlackBerry key
	 * @param status the status of the BlackBerry keyboard
	 * @return the MIDP equivalent of the key event
	 */
	private int getMidpKeyCode(int keyCode, int status)
	{
		int key = Keypad.key( keyCode );
     switch ( Keypad.map( key, 1 ) ) { // 1 is the ALT status
	        case '0': keyCode = Canvas.KEY_NUM0; break;
	        case '1': keyCode = Canvas.KEY_NUM1; break;
	        case '2': keyCode = Canvas.KEY_NUM2; break;
	        case '3': keyCode = Canvas.KEY_NUM3; break;
	        case '4': keyCode = Canvas.KEY_NUM4; break;
	        case '5': keyCode = Canvas.KEY_NUM5; break;
	        case '6': keyCode = Canvas.KEY_NUM6; break;
	        case '7': keyCode = Canvas.KEY_NUM7; break;
	        case '8': keyCode = Canvas.KEY_NUM8; break;
	        case '9': keyCode = Canvas.KEY_NUM9; break;
     }
     return keyCode;
	}

}


