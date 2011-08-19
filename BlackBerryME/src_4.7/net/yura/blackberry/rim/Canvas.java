package net.yura.blackberry.rim;

import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;

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
    
    
    
    public static final int KEY_END  = -11; // copy from SwingME
    public static final int MENU_KEY = -12; // copy value from SwingME

    public static final int KEY_BB_FIRE=-8; // normally its -5, but BB is crazy
    public static final int KEY_BB_UP=1; // -1 normally all these are negative values, but BB is crazy
    public static final int KEY_BB_DOWN=6; // -2
    public static final int KEY_BB_LEFT=2; // -3
    public static final int KEY_BB_RIGHT=5; // -4

    Graphics graphics;
    Field dummyField; // invisible field thats always on the screen
    Field menuField; // hold the current textfield while the menu is open, so the m

    /**
     * @see javax.microedition.lcdui.Canvas#Canvas()
     */
     protected Canvas() {
    
    	 graphics = new Graphics();
    	 
    	this.dummyField = new Field() {
            protected void paint(net.rim.device.api.ui.Graphics graphics) { }
            protected void layout(int width, int height) { }
        };

        add( this.dummyField );

     }

     protected boolean isShown(){
    	 return this.isVisible();
	 }
     
     /**
      * @see javax.microedition.lcdui.Canvas#getKeyCode(int)
      */
     public int getKeyCode(int gameAction) {
         switch ( gameAction ) {
    	     case Canvas.FIRE: return KEY_BB_FIRE;
    	     case Canvas.UP: return KEY_BB_UP;
    	     case Canvas.DOWN: return KEY_BB_DOWN;
    	     case Canvas.LEFT: return KEY_BB_LEFT;
    	     case Canvas.RIGHT: return KEY_BB_RIGHT;
         }
         return 0;
     }

     /**
      * @see javax.microedition.lcdui.Canvas#getKeyName(int)
      */
     public String getKeyName(int keyCode) {
             return "" + Keypad.getUnaltedChar( (char) keyCode );
     }

     /**
      * @see javax.microedition.lcdui.Canvas#getGameAction(int)
      */
     public int getGameAction(int keyCode) {
         switch (keyCode) {
    	     case KEY_BB_UP: 	return Canvas.UP;
    	     case KEY_BB_DOWN:	return Canvas.DOWN;
    	     case KEY_BB_LEFT: 	return Canvas.LEFT;
    	     case KEY_BB_RIGHT: return Canvas.RIGHT;
    	     case '\n':
    	     case ' ':
    	     case KEY_BB_FIRE: 	return Canvas.FIRE;
	     case Canvas.KEY_NUM2: 	return Canvas.UP; // 2
	     case Canvas.KEY_NUM4: 	return Canvas.LEFT; // 4
	     case Canvas.KEY_NUM6: 	return Canvas.RIGHT; // 6
	     case Canvas.KEY_NUM8: 	return Canvas.DOWN; // 8
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
      * @see javax.microedition.lcdui.Canvas#setFullScreenMode(boolean)
      */
     public void setFullScreenMode(boolean mode) {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#keyPressed(int)
      */
     protected void keyPressed(int keyCode) {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
      */
     protected void keyRepeated(int keyCode) {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#keyReleased(int)
      */
     protected void keyReleased(int keyCode) {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
      */
     protected void pointerPressed(int x, int y) {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
      */
     protected void pointerReleased(int x, int y) {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#pointerDragged(int, int)
      */
     protected void pointerDragged(int x, int y) {

     }
     
     public void multitouchEvent(int[] type, int[] x, int[] y) {
         // SwingME method for multi-touch
     }

     /**
      * @see javax.microedition.lcdui.Canvas#repaint(int, int, int, int)
      */
     public final void repaint(int x, int y, int width, int height) {
         invalidate( x, y, width, height );
     }

     /**
      * @see javax.microedition.lcdui.Canvas#repaint()
      */
     public final void repaint() {
         invalidate();
     }

     /**
      * @see javax.microedition.lcdui.Canvas#serviceRepaints()
      */
     public final void serviceRepaints() {
    	 // not used in SwingME
     }

     /**
      * @see javax.microedition.lcdui.Canvas#showNotify()
      */
     protected void showNotify() {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#hideNotify()
      */
     protected void hideNotify() {
     }

     /**
      * @see javax.microedition.lcdui.Canvas#paint(Graphics)
      */
     protected abstract void paint( Graphics g);

     /**
      * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
      */
     protected void sizeChanged(int w, int h) {

     }

     
    
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

	private int getMidpKeyCode(int keyCode) {
		int key = Keypad.key(keyCode);
	    char character = Keypad.map(key, Keypad.status(keyCode));
	    if (key == Keypad.KEY_ESCAPE) {
	        return KEY_END;
	    } else if (key == Keypad.KEY_MENU){
	    	return MENU_KEY;
	    }	    
        return character;
	}
	
	/*
	 * onMenu
	 * 
	 * trackwheelRoll
	 * 
	 * 
	 * keyDown
	 * keyUp
	 * keyRepeat
	 * 
	 * 
	 * touchEvent
	 * 
	 * navigationClick
	 * navigationUnclick
	 * 
	 * 
	 * navigationMovement
	 * 
	 */
		
	public boolean onMenu(int instance) {
		boolean processed = super.onMenu(instance);
		if (processed) {
			return true;
		}
		// unfocus the current item
		Field nativeFocusedField = super.getFieldWithFocus();
		if (nativeFocusedField != this.dummyField) {
			Object lock = MIDlet.getEventLock();
			synchronized (lock) {
				setFocus(this.dummyField, 0, 0, 0, 0);
				this.menuField = nativeFocusedField;
			}
                    }
                    else if (this.menuField != null) {
			Object lock = MIDlet.getEventLock();
			synchronized (lock) {
				setFocus(this.menuField, 0, 0, 0, 0);
				this.menuField = null;
			}
		}
		keyPressed(MENU_KEY);
		keyReleased(MENU_KEY);
		return true;
	}

    /**
     * @see net.rim.device.api.ui.Screen#trackwheelRoll(int, int, int)
     */
	/*
    protected boolean trackwheelRoll( int amount, int status, int time ) {
        try {
                boolean callSuper = false;

                boolean processed = false;
                if (callSuper) {
                    try {
                                processed = super.trackwheelRoll(amount, status, time);
                                if (processed) {
                                        // YURA: whats this all about??
                                        //if (focusChangeDetected(screen)) {
                                        //        processed = false;
                                        //}
                                        //else {
                                                //#debug
                                                System.out.println("Canvas.trackwheel-roll: super processed the call.");
                                                return true;
                                        //}
                                }
                   }
                   catch (Exception e) {
                           //#debug error
                           System.out.println("super.trackwheelRoll(" + amount + ", " + status + ", " + time + ") failed" + e );                           
                   }
                        
                }
            int keyCode;
            if ( amount < 0 ) {
                    amount *= -1;
                    if ( status == net.rim.device.api.system.TrackwheelListener.STATUS_ALT ) {
                            keyCode = KEY_BB_UP;
                    } else {
                            keyCode = KEY_BB_LEFT;
                    }
            } else {
                    if ( status == net.rim.device.api.system.TrackwheelListener.STATUS_ALT ) {
                            keyCode = KEY_BB_DOWN;
                    } else {
                            keyCode = KEY_BB_RIGHT;
                    }
            }
            // for loop outcommented, so that only one scroll step within each event is processed
            //for (; --amount >= 0; ) {
                    keyPressed( keyCode );
                    keyReleased( keyCode );
            //}

            return true;

        } catch (Exception e) {
                //#debug error
                System.out.println("error while processing trackwheel roll" + e);
                return true;
        }
    }
    */
    
    
    
    

    protected boolean keyDown(int keyCode, int time) {
    	/*return super.keyDown(keyCode, status);*/
        // note: the BlackBerry JavaDocs say that true is returned, when the event is consumed.
        // This is correct, but it seems that native components do not return true,
        // even though they (should have) handled the event. Text entry, for example, works
        // only ok, when we return false, so that the Blackberry is forwarding the event
        // to the corresponding consumer. Weird, really. This means that text input seems
        // to be handled differently from keyDown. And it's not keyChar.

		try {
			int key = Keypad.key(keyCode);
			if (key == Keypad.KEY_SEND 
					|| key == Keypad.KEY_END
					|| key == Keypad.KEY_CONVENIENCE_1
					|| key == Keypad.KEY_CONVENIENCE_2
					|| key == Keypad.KEY_VOLUME_UP
					|| key == Keypad.KEY_VOLUME_DOWN
					|| key == Keypad.KEY_LOCK) {
				return super.keyDown(keyCode, time);
			}
			boolean processed = super.keyDown(keyCode, time);
			if (processed) {
				return true;
			}
		} 
		catch (Exception e) {
			// #debug error
			System.out.println("super.keyDown(" + keyCode + ", " + time + ") failed" + e);
		}

		// #debug
		System.out.println("keyDown: keyCode=" + keyCode + ", key=" + Keypad.key(keyCode) + ", char=" + Keypad.map(keyCode));
		int midpKeyCode = getMidpKeyCode(keyCode);
		
		// we get events here when mouse clicks on the screen for some strange reason
		// we need to do this check as passing 0 can cause strange events
		if (midpKeyCode!=0) {
		    keyPressed(midpKeyCode);
		    
		    if (UiApplication.getUiApplication().getActiveScreen()!=this) {
                        keyReleased(midpKeyCode);
                    }
		}
		//#mdebug debug
		else {
		    System.out.println("############################# keyDown SKIP keyCode "+keyCode+" midpKeyCode "+midpKeyCode);
		}
		//#enddebug

		return true; // consume the key event

    }
    
	
	
	protected boolean keyUp(int keyCode, int time) {
		try {
			int key = Keypad.key(keyCode);
			if (key == Keypad.KEY_SEND 
					|| key == Keypad.KEY_END
					|| key == Keypad.KEY_CONVENIENCE_1
					|| key == Keypad.KEY_CONVENIENCE_2
					|| key == Keypad.KEY_VOLUME_UP
					|| key == Keypad.KEY_VOLUME_DOWN
					|| key == Keypad.KEY_LOCK) {
				return super.keyUp(keyCode, time);
			}
			
			boolean processed = super.keyUp(keyCode, time);
			if (processed) {
				return true;
			}
		} 
		catch (Exception e) {
			// #debug error
			System.out.println("super.keyUp(" + keyCode + ", " + time	+ ") failed" + e);
		}

		// #debug
		System.out.println("keyUp: keyCode=" + keyCode + ", key=" + Keypad.key(keyCode) + ", char=" + Keypad.map(keyCode));
		int midpKeyCode = getMidpKeyCode(keyCode);
		
                // we get events here when mouse clicks on the screen for some strange reason
                // we need to do this check as passing 0 can cause strange events
	        if (midpKeyCode!=0) {
	            keyReleased(midpKeyCode);
	        }
	        //#mdebug debug
	        else {
	            System.out.println("############################# keyUp SKIP keyCode "+keyCode+" midpKeyCode "+midpKeyCode);
	        }
	        //#enddebug

		return true; // consume the key event

	}

	    /**
	     * @see net.rim.device.api.ui.Screen#keyRepeat(int, int)
	     */
	    protected boolean keyRepeat(int keyCode, int time) {

	                   try {
	                       boolean processed = super.keyRepeat(keyCode, time);
	                       if (processed) {
                                   return true;
                               }
	                   }
	                   catch (Exception e) {
	                           //#debug error
	                           System.out.println("super.keyRepeat(" + keyCode + ", " + time + ") failed" + e );
	                   }

	                   if (Keypad.map(keyCode)==Keypad.KEY_ESCAPE) {
	                       System.exit(1);
	                   }
	                   
	        //#debug
	        System.out.println("keyRepeat: keyCode=" + keyCode + ", key=" + Keypad.key( keyCode) + ", char=" + Keypad.map( keyCode ) );
	        int midpKeyCode = getMidpKeyCode(keyCode);
	        
                // we get events here when mouse clicks on the screen for some strange reason
                // we need to do this check as passing 0 can cause strange events
	        if (midpKeyCode!=0) {
	            keyRepeated(midpKeyCode);
	        }
	        //#mdebug debug
	        else {
	            System.out.println("############################# keyRepeat SKIP keyCode "+keyCode+" midpKeyCode "+midpKeyCode);
	        }
	        //#enddebug
	        
	        
	        return true; // consume the key event
	    }
	
	
	    // copy and paste from SwingME DesktopPane
	    private static final int DRAGGED = 0;
	    private static final int PRESSED = 1;
	    private static final int RELEASED = 2;
	    //private static final int CANCEL = 3;

	    int x1=-1,x2=-1,y1=-1,y2=-1;
	
	protected boolean touchEvent(TouchEvent message) {

	    
	    
	    int event = message.getEvent();
            int x = message.getGlobalX(1);
            int y = message.getGlobalY(1);
	    
	    /*
	        Screen screen = getPolishScreen();
	        boolean isSuperCalled = false;

	                
	        if ( screen != null && forwardEventToNativeField( screen, 0)) {
	                boolean forwardEvent = true;
	                Item item = this.currentItem;
	                Field field = item != null ? this.currentItem._bbField : null;
	                boolean isTextField = field instanceof PolishTextField; 
	                if (isTextField) {
	                        int absX = item.getAbsoluteX();
	                        int absY = item.getAbsoluteY();
	                        if ( x < absX || y < absY || x > absX + item.itemWidth || y > absY + item.itemHeight) {
	                                forwardEvent = false;
	                        }
	                }
	                if (forwardEvent) {
	                        isSuperCalled = true;
	                        if (super.touchEvent( message ) && !focusChangeDetected(screen) && (!isTextField)) {
	                                return true;
	                        }
	                }
	        }
	        */		//TODO BlackBerry CLICK and UNCLICK events should be handled for storm 1/2
//            		if (event == TouchEvent.CLICK) {
	                if (event == TouchEvent.DOWN) {
	                        pointerPressed( x, y );
	                        //return true;
//	                } else if (event == TouchEvent.UNCLICK) {	                        
	                } else if (event == TouchEvent.UP) {
	                        pointerReleased( x, y );
	                        //return true;
	                } else {
	                        if (event == TouchEvent.MOVE) {
	                                pointerDragged( x, y );
	                                //invalidate();
	                                //return true;
	                        }
	                        
	                        // maybe 1 day support the touch of the storm
	                        
	                        /*
	                        else if (event == TouchEvent.UP) {
	                                        if (handlePointerTouchUp(x, y)) {
	                                                invalidate();
	                                                return true;
	                                        }
	                                        return false;
	                        }
	                        else if (event == TouchEvent.DOWN) {
	                                if (handlePointerTouchDown(x, y)) {
	                                        invalidate();
	                                        return true;
	                                }
	                                return false;
	                        }
	                        */
	        }
	                //if (isSuperCalled) {
	                //        return false;
	                //}
	                
	                
	                
	                
	                
	                
	                int x1 = message.getX(1);
	                int y1 = message.getY(1);
	                
	                int x2 = message.getX(2);
	                int y2 = message.getY(2);
	                
	                if (x1>=0 && y1>=0 && x2>=0 && y2>=0) {
	                    //int event = message.getEvent();
	                    int swingEvent;
	                    //switch (event) {
	                    //    case TouchEvent.DOWN: swingEvent = PRESSED; break;
	                    //    case TouchEvent.UP: swingEvent = RELEASED; break;
	                    //    case TouchEvent.MOVE: swingEvent = DRAGGED; break;
	                    //}
	                    if (this.x1<0 || this.y1<0 || this.x2<0 || this.y2<0) {
	                        swingEvent = PRESSED;
	                    }
	                    else {
	                        swingEvent = DRAGGED;
	                    }
	                    multitouchEvent(new int[]{swingEvent,swingEvent}, new int[]{x1,x2}, new int[]{y1,y2});
	                }
	                else if (this.x1>=0 && this.y1>=0 && this.x2>=0 && this.y2>=0) {
	                    multitouchEvent(new int[]{RELEASED,RELEASED}, new int[]{this.x1,this.x2}, new int[]{this.y1,this.y2});
	                }
	                else {
	                    //#debug debug
	                    System.out.println("BB multitouchEvent skip "+message.getEvent()+" point1="+x1+" "+y1+" point2="+x2+" "+y2);
	                }
	                
	                this.x1=x1;
	                this.y1=y1;
	                this.x2=x2;
	                this.y2=y2;
	                
	                
	                
	                
	                
	                
	                
	                
	        return true;//super.touchEvent(message);
	    }
	
	    protected boolean navigationClick(int status, int time)
	    {
	                /* From Blackberry Java Development Guide, might be useful in the future.
	                  if ((status & KeypadListener.STATUS_TRACKWHEEL) == KeypadListener.STATUS_TRACKWHEEL)
	                  {
	                    // TODO: Do something here.
	                  }
	                  else if ((status & KeypadListener.STATUS_FOUR_WAY) == KeypadListener.STATUS_FOUR_WAY)
	                  {
	                    // TODO: Do something here.
	                  }
	                */
	        boolean processed = super.navigationClick(status, time);
	        if (!processed) {
	                keyPressed( KEY_BB_FIRE );

	                if (UiApplication.getUiApplication().getActiveScreen()!=this) {
	                    keyReleased(KEY_BB_FIRE);
	                }

	                processed = true;
	        }
	        return processed;
	    }
	    
	    
	    
	   
	    /* (non-Javadoc)
	     * @see net.rim.device.api.ui.Screen#navigationUnclick(int, int)
	     */
	    protected boolean navigationUnclick(int status, int time)
	    {
	        boolean processed = super.navigationUnclick(status, time);
	        if (!processed) {
	                keyReleased( KEY_BB_FIRE );
	                processed = true;
	        }
	        return processed;
	    }

	    
	    protected boolean navigationMovement(int dx, int dy, int status, int time) {
	        boolean processed = false;
	        /*Screen screen = getPolishScreen();
	        boolean superImplementationCalled = false;
	        if ( screen != null ) {
	           if ( !screen.isMenuOpened() 
	                   && this.currentItem != null) 
	           { 
	                   try {
	                           if (this.currentItem._bbField instanceof AccessibleField) {
	                                   processed = ((AccessibleField)this.currentItem._bbField).navigationMovement(dx, dy, status, time);                     
	                           } else {
	                                   processed = super.navigationMovement(dx, dy, status, time);                     
	                           }
	                   if (processed) {
	                           if (focusChangeDetected(screen)) {
	                                   processed = false;
	                           } else {
	                                   //#debug 
	                                   System.out.println("super implementation handled navigationMovement()");
	                                   // possiblyt adjust scrolling:
	                                   Item item = this.currentItem;
	                                   if (item != null) {
	                                           item.updateInternalArea();
	                                   }
	                                   return true;
	                           }
	                   }
	                   superImplementationCalled = true;
	                   } catch (Exception e) {
	                           //#debug error
	                           System.out.println("super.navigationMovement(" + dx+ ", " + dy+ ", " + status+ ", " + time + ") failed" + e );
	                   }
	           }
	        }
	        */
	        int absDx = dx < 0 ? -dx : dx;
	        int absDy = dy < 0 ? -dy : dy;
	        if (absDx > absDy) {
	                dy = 0;
	        } else {
	                dx = 0;
	        }
	        
	        int keyCode = 0;
	                // Trackball up.
	                if (dy < 0)
	                {
	                        keyCode = KEY_BB_UP;
	                }
	                // Trackball down.
	                else if (dy > 0)
	                {
	                        keyCode = KEY_BB_DOWN;
	                }
	                // Trackball left.
	                else if (dx < 0)
	                {
	                        keyCode = KEY_BB_LEFT;
	                }
	                // Trackball right.
	                else if (dx > 0)
	                {
	                        keyCode = KEY_BB_RIGHT;
	                }
	                if (keyCode != 0) {
	                        keyPressed( keyCode );
	                        keyReleased( keyCode );
	                        // when false is returned, the BlackBerry will generate a trackwheelRoll event which we don't want,
	                        // so true is always returned:
	                        return true;
//	                      if ( (Object)this instanceof Screen) {
//	                              Screen scr = (Screen) (Object) this;
//	                              return scr.keyPressedProcessed || scr.keyReleasedProcessed;
//	                      } else {
//	                              return true;
//	                      }
	                }
	        //if (!superImplementationCalled) {
	                processed = super.navigationMovement(dx, dy, status, time);
	        //}
	                return processed;
	    }
	
	
	    /**/
	
}


