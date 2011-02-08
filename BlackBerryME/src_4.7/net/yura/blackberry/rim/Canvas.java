package net.yura.blackberry.rim;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.Keypad;

public abstract class Canvas extends FullScreen {

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
	
    Graphics graphics;

 protected Canvas() {

	 graphics = new Graphics();
	 
	 add(new RichTextField("Hello YURA World!"));
	 
	 
	try {
		//add(new RichTextField("hello "+net.rim.device.api.ui.TouchEvent.class) );
	}
	catch(Exception th) {
		add(new RichTextField(th.toString())); 
	}
	catch(Error th) {
		add(new RichTextField(th.toString()));
	}
	catch(Throwable th) {
		add(new RichTextField(th.toString()));
	}
	 
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

 public void setFullScreenMode(boolean mode) {

 }

 protected void keyPressed(int keyCode)
 {
         // do nothing
 }

 protected void keyRepeated(int keyCode)
 {
         // do nothing
 }

 protected void keyReleased(int keyCode)
 {
         // do nothing
 }

 protected void pointerPressed(int x, int y)
 {
         // do nothing
 }

 protected void pointerReleased(int x, int y)
 {
         // do nothing
 }

 protected void pointerDragged(int x, int y)
 {
         // do nothing
 }

	public boolean handlePointerTouchDown( int x, int y ) {
		return false;
	}

	public boolean handlePointerTouchUp( int x, int y ) {
		return false;
	}

 public final void repaint(int x, int y, int width, int height)
 {
     invalidate( x, y, width, height );
 }

 public final void repaint()
 {
     invalidate();
 }

 public final void serviceRepaints()
 {
	 // not used in SwingME
 }

 protected void showNotify()
 {
 	// do nothing
 }

 protected void hideNotify()
 {
 	// do nothing

 }

 protected abstract void paint( Graphics g);

 protected void sizeChanged(int w, int h)
 {
         // do nothing
 }

// protected void paint( net.rim.device.api.ui.Graphics g ) {
//   // do nothing..
// }
 

 protected void paint( net.rim.device.api.ui.Graphics g ) {
	    // when extending the BB MainScreen, super.paint(g) will
 	// clear the paint area, subpaint(g) will only render the fields.
	    super.subpaint(g);
}


 protected void paintBackground( net.rim.device.api.ui.Graphics g ) {
     //System.out.println("Canvas.paintBackground(): enter");
 	try {
	        this.graphics.setGraphics( g );
	    	paint( this.graphics );
 	}
 	catch (Exception e) {
 		//#debug error
 		System.out.println("unable to paint screen " + this + e );
 	}
 }

	protected void onExposed() {
		//this.isObscured = false;
		super.onExposed();
	}

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


