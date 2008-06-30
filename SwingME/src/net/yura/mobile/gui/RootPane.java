package net.yura.mobile.gui;

import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.MultilineLabel;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.Window;

//Import any additional handset specific packages.

//#if polish.midp1
	//#if false // TODO: polish.api.nokia-ui
		//# import com.nokia.mid.ui.FullCanvas;
		//#define tmp.useNokiaFullCanvas
	//#else
		//# import javax.microedition.lcdui.CommandListener;
		//#define tmp.useSoftkeyCommands
	//#endif
//#else
	//#define tmp.useMidp2FullScreen
//#endif


public class RootPane extends


//#ifdef tmp.useNokiaFullCanvas
//# FullCanvas implements Runnable
//#elif tmp.useSoftkeyCommands
//# Canvas implements Runnable, CommandListener
//#else
Canvas implements Runnable
//#endif
{

    	protected Midlet midlet;
        private static RootPane owner;
        public static RootPane getRootPane() {
            return owner;
        }
        public Midlet getMidlet() {
            return midlet;
        }
        
        
	private boolean killflag;

	private Vector windows;
	private Window currentWindow;

	private Component activeComponent;
	private Component repaintComponent;
	private Component animComponent;


        private Image splash;
        private int background;
	private boolean paintdone=false;

	private CommandButton[] compCmds;
        private CommandButton[] currentCommands; // To avoid creating new array every cycle

	/**
	 * NOTHING SHOULD EVER USE serviceRepaints()!!!!
         * or repaint(); !!!
	 * @param m The Midlet
	 */
	public RootPane(Midlet m,int back,Image sph) {
                owner=this;
                
                background = back;
                splash = sph;
                
		// MUST DO THIS FIRST SO initialise is called
		compCmds = new CommandButton[2];
		currentCommands = new CommandButton[2];

		//#ifdef tmp.useMidp2FullScreen
		setFullScreenMode(true);
		//#endif

		// now setup the rest of the stuff

		midlet = m;

		// Are we in debug mode?
		String s;
		if ((s = midlet.getAppProperty("Debug-Mode")) != null && ( s.toUpperCase().equals("OFF") || s.toUpperCase().equals("NO") || s.toUpperCase().equals("FALSE") ) ) {
			RootPane.debugMode = false;
		}
		
		//#ifdef tmp.useSoftkeyCommands
		//#setCommandListener(this);
		//#endif

		windows = new Vector();

		openNewWindow( new Window() );

		Display.getDisplay(m).setCurrent(this);
		repaint();
		serviceRepaints();
	}

        public CommandButton[] getCurrentCommands(){
            currentCommands[0] = compCmds[0] == null ? currentWindow.panelCmds[0] : compCmds[0];
            currentCommands[1] = compCmds[1] == null ? currentWindow.panelCmds[1] : compCmds[1];
            return currentCommands;
        }

	public void setComponentCommand(int i, CommandButton softkey) {

		compCmds[i] = softkey;
		repaint(); // TODO is this correct, will ANY repaint do? also another place

	}

	public void openNewWindow(Window w) {
                
		windows.addElement(w);

		bringToTop(w);
	}

	public void bringToTop(Window w) {
		if (windows.contains(w)) {
			currentWindow = w;

			windows.removeElement(w);
			windows.addElement(w);
			setupActiveComponent();
			currentWindow.repaint();
		}
	}

	public void closeWindow(Window w) {

		if (windows.contains(w)) {

			windows.removeElement(w);

			if (w==currentWindow) {

				currentWindow = (Window)windows.lastElement();
                                currentWindow.setupActiveComponent();
			}

		}

	}
        public void hideWindow(Window w) {
            
            if (currentWindow == w) {
                        if (windows.size() >1) {
                                bringToTop( (Window)windows.elementAt( windows.size()-2 ) );
                        }
            }
        }

	public Window getCurrentWindow() {

		return currentWindow;
	}

	public final void run() {

		try {
			midlet.initialize(this);
		}
		catch(Throwable th) {
			th.printStackTrace();
			log( "Error in initialize: " + th.toString() );
		}

		while (true) {

			if (killflag) { return; }

			synchronized (this) {
				if (animComponent==null) {

					try {
						wait();
					}
					catch (InterruptedException e) {
                                            //#debug
                                            System.out.println("Exception "+e);
                                        }

					if (killflag) { return; }
				}
			}

			try {

				//if (animComponent!=null) {

					Component ac = animComponent;

					animComponent = null;

					ac.animate();
				//}
			}
			catch(Throwable th) {
				th.printStackTrace();
				log( "Error in animation: " + th.toString() );
			}
		}

	}

	public void exit() {
		midlet.destroyApp(true);
	}
	void kill() {

		killflag=true;
		synchronized (this) {
			notify();
		}

	}

	public void animateComponent(Component com) {

		//System.out.println(animComponent +" animateComponent "+com);

		animComponent = com;
		synchronized (this) {
			notify();
		}
	}

	/**
	 * Return's the current active Item.
	 * @return Item - Item object or return's null if Form has only String Item's
	 *
	 */
	public Component getCurrentItem() {

		return activeComponent;

	}

	//public void setKeyListener(KeyListener kl) {

	//	keyListener = kl;
	//}

	private Window debugwindow;
	private MultilineLabel text;

	public void log(String s) {

		if (debugMode) {
			
			if (debugwindow==null) {

				debugwindow = new Window( new LineBorder(0x00000000) );
				debugwindow.setBounds(10, 10, getWidth()-20, getHeight()/2);
				text = new MultilineLabel("",new Font(),Graphics.LEFT,0);
				text.setForeground(0x00000000);
				debugwindow.setContentPane( new ScrollPane(text) );
				debugwindow.getContentPane().setBackground(0x00FFFFFF);
				debugwindow.getContentPane().setTransparent(false);
				//debugwindow.getContentPane().doLayout();
				openNewWindow(debugwindow);
                                debugwindow.setActionListener(debugwindow);
				debugwindow.setWindowCommand(1, new CommandButton("OK","hide") );

			}

			text.setSize( debugwindow.getWidth()-ScrollPane.getBarThickness(debugwindow.getWidth(), debugwindow.getHeight()) , text.getHeight() );
			text.append(s+"\n");
			bringToTop(debugwindow);
			
		}
	}

	private String mem;

	/**
	 * Controls Form activity, updates Items and traversing between Items and screen scrolling.
	 * This function should be called in the game update() every frame to update Form activity.
	 * Note: Call this routine only if Form is active and displayed on screen
	 * @param keypad - keypad object
	 */
	private void passKeyEvent(KeyEvent keyevent) {

		try {

			// Log out the free memory if the star key is pressed
			if(keyevent.isDownKey(Canvas.KEY_STAR)){

				mem = (Runtime.getRuntime().freeMemory() >> 10) +"K/" +(Runtime.getRuntime().totalMemory() >> 10)+"K";
				repaint();
				//System.out.println("--> Total memory: [" + (Runtime.getRuntime().totalMemory() >> 10) + "K] <--");
				//System.out.println("--> Free memory: [" + (Runtime.getRuntime().freeMemory() >> 10) + "K] <--");
			}
			else {

				mem = null;

			}

			CommandButton[] panelCmds = currentWindow.getPanelCommands();
                        ActionListener actionListener = currentWindow.getActionListener();
                        
                        boolean softkey1Action = keyevent.isDownKey(KeyEvent.KEY_SOFTKEY1) || keyevent.justReleasedKey(KeyEvent.KEY_SOFTKEY1);
                        boolean softkey2Action = keyevent.isDownKey(KeyEvent.KEY_SOFTKEY2) || keyevent.justReleasedKey(KeyEvent.KEY_SOFTKEY2);
                        
                        // ############# we use justReleasedKey for firing actions, this means the action is ONLY
                        // ############# fired off when the key is released!!!
                        
			if (
					activeComponent!=null && activeComponent instanceof ActionListener && (
						(compCmds[0]!=null && softkey1Action ) ||
						(compCmds[1]!=null && softkey2Action )
					)
				) {

				if (compCmds[0]!=null && keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY1)) {
					((ActionListener)activeComponent).actionPerformed( compCmds[0].getActionCommand() );
				}
				else if (compCmds[1]!=null && keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY2)) {
					((ActionListener)activeComponent).actionPerformed( compCmds[1].getActionCommand() );
				}
			}
			else if (
					actionListener!=null && (
						(panelCmds[0]!=null && softkey1Action ) ||
						(panelCmds[1]!=null && softkey2Action )
					)
				) {

				if (panelCmds[0]!=null && keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY1)) {
					actionListener.actionPerformed( panelCmds[0].getActionCommand() );
				}
				else if (panelCmds[1]!=null && keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY2)) {
					actionListener.actionPerformed( panelCmds[1].getActionCommand() );
				}

			}
			else if (activeComponent!=null) {

				boolean consumed = activeComponent.keyEvent(keyevent);

				//System.out.println("rootpane KEY PRESSED on "+activeComponent+" and consumed after is: "+consumed);

                                // TODO
                                // it may say that a down key is pressed, BUT
                                // getting the gameAction may not work
                                // keyevent.getKeyAction(keyevent.getIsDownKey())
                                // if another key was pressed first!
                                
				if (!consumed && (

					keyevent.isDownAction(Canvas.RIGHT)||
					keyevent.isDownAction(Canvas.DOWN) ||
					keyevent.isDownAction(Canvas.LEFT)||
					keyevent.isDownAction(Canvas.UP)

				)) {

					Panel parent = activeComponent.getParent();

					//if (parent!=null) {
						parent.breakOutAction(activeComponent, keyevent.getKeyAction(keyevent.getIsDownKey()), true);
					//}
					//else {
						// as we have no active component just send a up/down event to the panel
						//currentWindow.breakOutAction( keypad.justPressed(Keypad.RIGHT) || keypad.justPressed(Keypad.DOWN) );
					//}
				}
				else if (!consumed ) {//&& keyListener!=null) {

					keyEvent(keyevent);
				}
			}
			else { //  if (keyListener!=null) {

				if (

					keyevent.isDownAction(Canvas.RIGHT)||
					keyevent.isDownAction(Canvas.DOWN) ||
					keyevent.isDownAction(Canvas.LEFT)||
					keyevent.isDownAction(Canvas.UP)

				) {
					currentWindow.passScrollUpDown( keyevent.getKeyAction(keyevent.getIsDownKey()) );
				}
                                else {
                                    keyEvent(keyevent);
                                }
			}



		}
		catch(Throwable th) {
			th.printStackTrace();
			log( "Error in KeyEvent: " + th.toString() );
		}
	}

	// if no command listener is used key events fall though to this method
        // used for adding global shortcut keys
	public void keyEvent(KeyEvent kypd) { }

        
        protected void sizeChanged(int w, int h){
            
            for (int c=0;c<windows.size();c++) {
                Window window = (Window)windows.elementAt(c);
                
                // TODO RESIZE
                
            }
            
        }
        
	/**
	 * By default Form is draw at (0, 0) of the screen coordinate system.
	 * Ignores setClip() and clipRect() function calls made outside of this routine.
	 * @param g - Graphics object
	 */
	public void paint(Graphics g) {

//System.out.println("ROOTPANE RENDER!! STARTING PAINT!!");

		if (!paintdone) {


				g.setColor(background);
				g.fillRect(0, 0, getWidth(), getHeight());
                                
                                if (splash!=null) {
                                    g.drawImage(splash, (getWidth()-splash.getWidth())/2, (getHeight()-splash.getHeight())/2, Graphics.TOP | Graphics.LEFT);
                                }
                                else {
                                    g.setColor(0x00FF0000);
                                    g.drawString("yura.net mobile Loading..."
                                    //#= + "${polish.identifier}"
                                    , 0, 0, Graphics.TOP | Graphics.LEFT);
                                }


			//#if polish.cldc10
			//# new Thread(this).start();
			//#else
			 new Thread(this,"Animation-Thread").start();
			//#endif

			paintdone = true;

			return;
		}

		if (!fullrepaint) {
			boolean ret = paintWindow(g,currentWindow);
			if (!ret) {
				fullrepaint=true;
				//#debug
				System.out.println("NORMAL REPAINT FAILED! having to repaint EVERYTHING");
			}
		}

		repaintComponent = null;

		if (fullrepaint) {
			fullrepaint = false;
			for (int c=0;c<windows.size();c++) {
				paintWindow(g,(Window)windows.elementAt(c));
			}
		}

		if (RootPane.debugMode && mem!=null) {

			javax.microedition.lcdui.Font font = g.getFont();

			g.setColor(0x00FFFFFF);
			g.fillRect((getWidth() -(font.stringWidth(mem)+10))/2 , 0, font.stringWidth(mem)+10, font.getHeight()+10 );
			g.setColor(0x00000000);
			g.drawString(mem, (getWidth() -(font.stringWidth(mem)+10))/2 +5,5, Graphics.TOP | Graphics.LEFT );
		}

		//#ifndef tmp.useSoftkeyCommands
		drawSoftkeys(g);
		//#endif

	}

	private boolean paintWindow(Graphics g,Window w) {

//System.out.println("paintWindow window:"+ w+" component:"+repaintComponent );
		boolean ret=false;

		int wx=w.getX();
		int wy=w.getY();

		g.translate(wx,wy);

		try {
			ret = w.paintWindow(g,repaintComponent);
		}
		catch(Throwable th) {
			th.printStackTrace();
			log( "Error in paintWindow: " + th.toString() );
		}

		g.translate(-wx, -wy);
		return ret;
	}

	// things like draw background
//    public void paintComponent(Graphics g) {}
    public void drawSoftkeys(Graphics g) {

    	Font f = getDefaultStyle().font;

    	g.setColor( getDefaultStyle().background );
    	g.fillRect(0, getHeight() - f.getHeight(), getWidth(), f.getHeight());

    	g.setColor( f.getColors()[0] );

    	CommandButton[] buttons = getCurrentCommands();
    	String s;

    	if (buttons[0]!=null) {

    		s= buttons[0].getLabel();

    		f.drawString(g,s , ((getWidth()/2)-( f.getWidth(s) ))/2, getHeight() - f.getHeight() , Graphics.TOP | Graphics.LEFT );
    	}
    	if (buttons[1]!=null) {

    		s= buttons[1].getLabel();

    		f.drawString(g,s ,(getWidth()/2)+  ((getWidth()/2)-( f.getWidth(s) ))/2, getHeight() - f.getHeight() , Graphics.TOP | Graphics.LEFT );
    	}
    }
/*
    //private Component old;
    public void setGlassPaneComponent(Panel c) {

    	currentWindow.setGlassPaneComponent(c);

    }

    public Panel getGlassPaneComponent() {

    	return currentWindow.getGlassPaneComponent();
    }

	public Panel getContentPane() {

		return currentWindow.getContentPane();

	}

	public void setContentPane(Panel a) {

    	currentWindow.setContentPane(a);

	}
*/
	public Component getActiveComponent() {
		return activeComponent;
	}

	public void setActiveComponent(Component ac) {

	       if(activeComponent != null) {
	    	   activeComponent.focusLost();
	       }

	       activeComponent = ac;

			//Command[] newCmd = null;
			if (activeComponent != null) {

			//	newCmd = component.getCommands();
				activeComponent.focusGained();
			}
			//removeAllComponentCommands();

			//if(newCmd != null) {
			//	addComponentCommand(newCmd[0]);
			//	addComponentCommand(newCmd[1]);
			//}

	       //fullRepaint();

	}

	public void setupActiveComponent() {

		setActiveComponent(null);
		currentWindow.setupActiveComponent();

	}

	public void repaintComponent(Component rc) {

//System.out.println("someone asking for repaint "+rc);

		if (repaintComponent!=rc && paintdone) {
			// we want to do this no matter what
			// kind of repaint is waiting for
			serviceRepaints();
		}

		repaintComponent = rc;

		repaint();

	}

	private boolean fullrepaint;
	//public void repaint() {
		// cant do this
	//}
	//public void serviceRepaints() {
		// cant do this
	//}
	public void fullRepaint() {
		// this is here coz this method should NOT be used
		System.out.println("FULL REPAINT!!!");
		fullrepaint=true;
		repaintComponent = null;
		repaint();
	}
	public void windowRepaint() {
		repaintComponent = null;
		repaint();
	}

	private static Theme defaultStyle;

	public static boolean debugMode=true;

    public static Theme getDefaultStyle() {

    	return defaultStyle;

    }
    public static void setDefaultStyle(Theme a) {

    	defaultStyle = a;

        if (defaultStyle.font==null) {
            defaultStyle.font=new Font();
        }
        if (defaultStyle.barHeight==0) {
            defaultStyle.barHeight=defaultStyle.font.getHeight();
        }
        if (defaultStyle.defaultWidth==0) {
            defaultStyle.defaultWidth = owner.getWidth()-ScrollPane.getBarThickness(owner.getWidth(), owner.getHeight());
        }
        
        owner.currentWindow.setSize(owner.getWidth(),owner.getHeight()-defaultStyle.barHeight);
        
    }

    public static int getDefaultSpace() {
		if(RootPane.getDefaultStyle().defaultWidth <= 128)
		{
			return 3;
		}
		else if(RootPane.getDefaultStyle().defaultWidth <= 208)
		{
			return 5;
		}
		else
		{
			return 7;
		}
    }

    /*
	private static int DEFAULT_WIDTH=0;

	public static int getDefaultComponentWidth() {

		if (DEFAULT_WIDTH==0) {

			throw new IllegalArgumentException("default width not set");
		}

		return DEFAULT_WIDTH;

	}

	public static void setDefaultComponentWidth(int a) {

		DEFAULT_WIDTH = a;
	}
    */

    // #############################################################################################
    // command actions
    // #############################################################################################

    // ActionListoner interface implementation
	//#ifdef tmp.useSoftkeyCommands
  //#public void commandAction(Command arg0, Displayable arg1) {
  //#
  //# 	CommandButton[] cmds = getCurrentCommands();
  //#
  //#	if (arg0 == cmds[0]) {
  //#
  //#
  //#		keyPressed(KEY_SOFTKEY1);
  //#		keyReleased(KEY_SOFTKEY1);
  //#
  //#	}
  //#	else if (arg0 == cmds[1]) {
  //#
  //#		keyPressed(KEY_SOFTKEY2);
  //#		keyReleased(KEY_SOFTKEY2);
  //#
  //#	}
  //#}
	//#endif

    // #############################################################################################
    // pointer commands
    // #############################################################################################

	public static final int DRAGGED = 1;
	public static final int PRESSED = 2;
	public static final int RELEASED = 3;

    public void pointerDragged(int x, int y) {
    	pointerEvent(DRAGGED,x,y);
    }

    public void pointerPressed(int x, int y) {
    	pointerEvent(PRESSED,x,y);
    }

    public void pointerReleased(int x, int y) {
    	pointerEvent(RELEASED,x,y);
    }


    private void pointerEvent(int type, int x, int y){

    	try {
        	currentWindow.pointerEvent(type, x - currentWindow.getX(), y - currentWindow.getY());
    	}
		catch(Throwable th) {
			th.printStackTrace();
			log( th.toString() );
		}

    }

    // #############################################################################################
    // key commands
    // #############################################################################################

	private KeyEvent keypad = new KeyEvent(this);

	public void keyPressed(int keyCode) {
//              System.out.println("keyPressed "+ keyCode+" "+getKeyName(keyCode));

		keypad.keyPressed(keyCode);

		passKeyEvent(keypad);

		//#if polish.vendor != "" && polish.vendor == Samsung
                //#   if (keyCode == KeyEvent.KEY_SOFTKEY1 || KeyEvent == Keypad.KEY_SOFTKEY2) {
                //#           keyReleased(keyCode);
                //#   }
		//#endif

	}

	public void keyReleased(int keyCode) {

		keypad.keyReleased(keyCode);

		passKeyEvent(keypad);

	}

	public void keyRepeated(int keyCode) {

	    keypad.keyRepeated(keyCode);

	    passKeyEvent(keypad);
	}


	// #####################################################

	public void call(String number) {

		try {

			midlet.platformRequest( "tel:" + number );
		}
		catch (ConnectionNotFoundException e) {

			e.printStackTrace();
		}

	}

	public void vibration(int duration){
		try {
			Display.getDisplay(midlet).vibrate(duration);
		}
                catch(Exception e){
                    //#debug
                    System.out.println("Exception "+e);
                }
	}

	// this is to fix buttons not being released properly on some phones

	protected void showNotify() {

		//System.out.println("showNotify");
		keypad.clear();
		fullRepaint();

	}
	protected void hideNotify() {

		//System.out.println("hideNotify");
		keypad.clear();

	}
	
	/**
	 * TODO this is some keep light on code
	 * it does not work fully yet, but its a start
	 * source http://discussion.forum.nokia.com/forum/archive/index.php/t-48831.html
	 * http://forum.java.sun.com/thread.jspa?threadID=5244531
	 */
/*	public static void backLight() {
		//#ifdef false // TODO polish.api.nokia-ui
		//#		try {
		//#			com.nokia.mid.ui.DeviceControl.setLights(0,100);
		//#		} catch(Exception ex) {
					//#debug error
		//#			System.out.println("Unable to set light: " + ex );
		//#		}
		//#elif polish.Vendor == Samsung
	try {
		com.samsung.util.LCDLight.on(10000); // max of 10 seconds
	} catch(Exception ex) {
					//#debug error
		System.out.println("Unable to set light: " + ex );
	}
		//#elif polish.Vendor == Sharp
		//#		try {
		//#			com.vodafone.v10.system.device.DeviceControl dc = com.vodafone.v10.system.device.DeviceControl.getDefaultDeviceControl();
		//#			dc.setDeviceActive( com.vodafone.v10.system.device.DeviceControl.BACK_LIGHT, true );
		//#		} catch (Exception ex) {
					//#debug error
		//#			System.out.println("Unable to set light: " + ex );
		//#		}
		//#elif polish.Vendor == Motorola
		//#
		//#		Display.getDisplay(midlet).flashBacklight(100);
		//#
		//#endif
	}*/
	
}