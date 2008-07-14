/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui;

import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.cellrenderer.DefaultSoftkeyRenderer;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.MultilineLabel;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.Window;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JDesktopPane
 */
public class DesktopPane extends Canvas implements Runnable {

        // static methods
    
        private static DesktopPane desktop;
	public static boolean debugMode=true;
        
        public static DesktopPane getDesktopPane() {
            return desktop;
        }
        public static Theme getDefaultTheme() {
            return getDesktopPane().theme;
        }
        
        // object variables
        
        protected Midlet midlet;
        private Theme theme;

	private Vector windows;
	private Window currentWindow;

	private Component focusedComponent;
	private Component repaintComponent;
	private Component animatedComponent;

        private Image splash;
        private int background;

        private boolean fullrepaint;
        private boolean paintSoftKey;
        private boolean paintdone=false;
	private boolean killflag;

	private CommandButton[] componentCommands;
         // to avoid creating new array every time
        private CommandButton[] currentCommands;

	/**
	 * nothing should ever call serviceRepaints()
         * or repaint() in this class.
	 * @param m The Midlet
         * @param back the background color
         * @param sph the splash screen image
	 */
	public DesktopPane(Midlet m,int back,Image sph) {
                desktop=this;
                
                background = back;
                splash = sph;

		componentCommands = new CommandButton[2];
		currentCommands = new CommandButton[2];

		setFullScreenMode(true);

		midlet = m;

		// check if we want to be in debug mode
		String s;
		if ((s = midlet.getAppProperty("Debug-Mode")) != null && ( s.toUpperCase().equals("OFF") || s.toUpperCase().equals("NO") || s.toUpperCase().equals("FALSE") || s.toUpperCase().equals("F") ) ) {
			DesktopPane.debugMode = false;
		}

		windows = new Vector();

                // ceate a new window to use as the main window
		add( new Window() );

                // now we set this as the main display
                // the serviceRepaints will mean the repaint will be called
                // this will then kick of the run method of this class
                // and that will in tern call initialise of the midlet
		Display.getDisplay(m).setCurrent(this);
		repaint();
		serviceRepaints();
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
				if (animatedComponent==null) {

					try {
						wait();
					}
					catch (InterruptedException e) {
                                            //#debug
                                            System.out.println("Exception "+e.toString() );
                                        }

					if (killflag) { return; }
				}
			}

			try {

                            Component ac = animatedComponent;
                            animatedComponent = null;
                            ac.animate();

			}
			catch(Throwable th) {
				th.printStackTrace();
				log( "Error in animation: " + th.toString() );
			}
		}

	}

        /**
         * This will call the animate() method on a component from the animation thread
         * @param com The Component to call animte() on
         */
        public void animateComponent(Component com) {

		animatedComponent = com;
		synchronized (this) {
			notify();
		}
	}
        // called by destroyApp
	void kill() {
		killflag=true;
		synchronized (this) {
			notify();
		}
	}
        /**
         * sets the default theme, and sets up default values if there are none set
         * @param a The Theme
         */
        public void setDefaultTheme(Theme a) {

            theme = a;

            if (theme.font==null) {
                theme.font=new Font();
            }

            if (theme.defaultWidth==0) {
                theme.defaultWidth = getWidth()-ScrollPane.getBarThickness(getWidth(), getHeight());
            }

            if (theme.defaultSpace==0) {
                
                    if(theme.defaultWidth <= 128) {
                            theme.defaultSpace=3;
                    }
                    else if(theme.defaultWidth <= 208) {
                            theme.defaultSpace=5;
                    }
                    else {
                            theme.defaultSpace=7;
                    }
                
            }
            
            if (theme.softkeyRenderer==null) {
                theme.softkeyRenderer = new DefaultSoftkeyRenderer();
            }
            
            currentWindow.setSize(getWidth(),getHeight());

        }
        
        // #####################################################################
        // painting
        // #####################################################################
        
        //public void repaint() {
		// cant do this
	//}
	//public void serviceRepaints() {
		// cant do this
	//}
        
	/**
	 * @param g The Graphics object
	 */
	public void paint(Graphics g) {

                //System.out.println("CANVAS PAINT!!!  fullrepaint="+fullrepaint+" repaintComponent="+repaintComponent);

		if (!paintdone) {


                        g.setColor(background);
                        g.fillRect(0, 0, getWidth(), getHeight());

                        if (splash!=null) {
                            g.drawImage(splash, (getWidth()-splash.getWidth())/2, (getHeight()-splash.getHeight())/2, Graphics.TOP | Graphics.LEFT);
                            splash = null;
                        }
                        else {
                            g.setColor(0x00FF0000);
                            g.drawString("yura.net mobile Loading...", 0, 0, Graphics.TOP | Graphics.LEFT);
                        }

			new Thread(this).start();

			paintdone = true;

			return;
		}

		if (!fullrepaint && !paintSoftKey) {
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
                        paintFirst(g);
			for (int c=0;c<windows.size();c++) {
				paintWindow(g,(Window)windows.elementAt(c));
			}
		}

		if (DesktopPane.debugMode && mem!=null) {

			javax.microedition.lcdui.Font font = g.getFont();

			g.setColor(0x00FFFFFF);
			g.fillRect((getWidth() -(font.stringWidth(mem)+10))/2 , 0, font.stringWidth(mem)+10, font.getHeight()+10 );
			g.setColor(0x00000000);
			g.drawString(mem, (getWidth() -(font.stringWidth(mem)+10))/2 +5,5, Graphics.TOP | Graphics.LEFT );
		}

		drawSoftkeys(g);
                paintLast(g);

	}

        public void paintFirst(Graphics g) { }
        public void paintLast(Graphics g) { }
        
        private void drawSoftkeys(Graphics g) {
            
            Component com1 = getSoftkeyRenderer(0);

            if (com1!=null) {

                g.translate(com1.getX(), com1.getY());
                com1.paint(g);
                g.translate(-com1.getX(), -com1.getY());
            }

            Component com2 = getSoftkeyRenderer(1);
            if (com2!=null) {

                g.translate(com2.getX(), com2.getY());
                com2.paint(g);
                g.translate(-com2.getX(), -com2.getY());
            }
            paintSoftKey = false;

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
        
        private boolean sideSoftKeys;
        private Component getSoftkeyRenderer(int i) {
            // if (theme==null || theme.softkeyRenderer==null) return null; // sometimes throws on emulator
            Component com = theme.softkeyRenderer.getListCellRendererComponent(null, getCurrentCommands()[i], i, sideSoftKeys, false);
            if (com==null) return null;
            int h = com.getHeightWithBorder();
            int w = com.getWidthWithBorder();
            if (sideSoftKeys) {
                com.setBoundsWithBorder(getWidth()-w, (i==0)?(getHeight()-h):0, w, h);
            }
            else {
                com.setBoundsWithBorder((i==1)?(getWidth()-w):0, getHeight()-h, w, h);
            }
            return com;
        }
        

        // #####################################################################
        // Different ways of caling repaint
        
        /**
         * this method should NOT normally be called
         * is it called when repaint() is called on a window,
         * but that window is currently in the background
         * or if the window is transparent
         */
	public void fullRepaint() {
		// this is here coz this method should NOT be used
                //#debug
		System.out.println("FULL REPAINT!!! this method should NOT normally be called");
		fullrepaint=true;
		repaintComponent = null;
		repaint();
	}
        /**
         * This is called when u call repaint() on a window
         */
	public void windowRepaint() {
		if (repaintComponent != null && paintdone) {
                    // the repaintComponent may be on another window
                    serviceRepaints();
                }
		repaint();
   
	}

        /**
         * this is called when you call repaint() on a component
         * @param rc The Component to repaint
         */
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
        
        /**
         * Repaint the softkeybar
         */
        public void softkeyRepaint(boolean all) {

            // TODO: is this correct, will ANY repaint do?
            // by default calling repaint() like this will do a window repaint
            if (all) {
                // we ONLY need to repaint all if the renderer does not paint anything on a null softkey
                fullRepaint();
            }
            else {
                serviceRepaints();
                paintSoftKey=true;
                repaint();
            }
        }

        // #####################################################################
        // action handeling
        // #####################################################################
        
        public Component getFocusedComponent() {
		return focusedComponent;
	}

	public void setFocusedComponent(Component ac) {

	       if(focusedComponent != null) {
	    	   focusedComponent.focusLost();
	       }

	       focusedComponent = ac;

                if (focusedComponent != null) {
                    focusedComponent.focusGained();
                }

	}
        
        public CommandButton[] getCurrentCommands(){
            currentCommands[0] = componentCommands[0] == null ? currentWindow.getWindowCommands()[0] : componentCommands[0];
            currentCommands[1] = componentCommands[1] == null ? currentWindow.getWindowCommands()[1] : componentCommands[1];
            return currentCommands;
        }

    /**
     * @see Window#setWindowCommand
     */
	public void setComponentCommand(int i, CommandButton softkey) {

            if (componentCommands[i]!=softkey) {
                CommandButton old = getCurrentCommands()[i]; // get old 1
		componentCommands[i] = softkey;
                if (getCurrentCommands()[i]==softkey) { // check if we are the new 1
                    softkeyRepaint(old==null || softkey ==null);
                }
            }

	}

	private void passKeyEvent(KeyEvent keyevent) {

		try {

			if(keyevent.isDownKey(Canvas.KEY_STAR)){
				mem = (Runtime.getRuntime().freeMemory() >> 10) +"K/" +(Runtime.getRuntime().totalMemory() >> 10)+"K";
				fullRepaint();
			}
			else {
				mem = null;
			}

                        CommandButton[] cmds = getCurrentCommands();

                        if (
                                ( cmds[0]!=null && (keyevent.isDownKey(KeyEvent.KEY_SOFTKEY1) || keyevent.justReleasedKey(KeyEvent.KEY_SOFTKEY1)) ) ||
                                ( cmds[1]!=null && (keyevent.isDownKey(KeyEvent.KEY_SOFTKEY2) || keyevent.justReleasedKey(KeyEvent.KEY_SOFTKEY2)) )
                        ) {
                            
                            // ############# we use justReleasedKey for firing actions, this means the action is ONLY
                            // ############# fired off when the key is released!!!

                            if (keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY1)) {
                                softKeyActivated(0);
                            }
                            else if (keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY2)) {
                                softKeyActivated(1);
                            }
                            
                        }
			else if (focusedComponent!=null) {

				boolean consumed = focusedComponent.keyEvent(keyevent);

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

					Panel parent = focusedComponent.getParent();

					//if (parent!=null) {
						parent.breakOutAction(focusedComponent, keyevent.getKeyAction(keyevent.getIsDownKey()), true);
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

        
        
        
        
        
        
    public void softKeyActivated(int i) {

        		CommandButton[] panelCmds = currentWindow.getWindowCommands();
                        ActionListener actionListener = currentWindow.getActionListener();

                        if (
                                        componentCommands[i]!=null &&
					focusedComponent!=null &&
                                        focusedComponent instanceof ActionListener
				) {

				((ActionListener)focusedComponent).actionPerformed( componentCommands[i].getActionCommand() );

                                 if (componentCommands[i]!=null && componentCommands[i].getMenu()!=null) {
                                        Component renderer = getSoftkeyRenderer(i);
                                        componentCommands[i].getMenu().openMenu(renderer.getXWithBorder(),renderer.getYWithBorder(),renderer.getWidthWithBorder(),renderer.getHeightWithBorder());
                                 }
                                
			}
			else if (actionListener!=null && panelCmds[i]!=null) {

                                actionListener.actionPerformed( panelCmds[i].getActionCommand() );

                                if (panelCmds[i]!=null && panelCmds[i].getMenu()!=null) {
                                    Component renderer = getSoftkeyRenderer(i);
                                    panelCmds[i].getMenu().openMenu(renderer.getXWithBorder(),renderer.getYWithBorder(),renderer.getWidthWithBorder(),renderer.getHeightWithBorder());
                                }

			}
        
    }

        
        
        
	// if no command listener is used key events fall though to this method
        // used for adding global shortcut keys
	public void keyEvent(KeyEvent kypd) { }
        
        // #####################################################################
        // normal desktop calls
        // #####################################################################
        
        /**
         * @param w The window to add
         * @see java.awt.Container#add(java.awt.Component) Container.add
         */
	public void add(Window w) {
                
		windows.addElement(w);
		setSelectedFrame(w);
	}

        /**
         * @param w the internal frame that's currently selected
         * @see javax.swing.JDesktopPane#setSelectedFrame(javax.swing.JInternalFrame) JDesktopPane.setSelectedFrame
         */
	public void setSelectedFrame(Window w) {
		if (windows.contains(w)) {
                        setFocusedComponent(null);
			currentWindow = w;
			windows.removeElement(w);
			windows.addElement(w);
			currentWindow.setupFocusedComponent();
			currentWindow.repaint();
		}
	}

        /**
         * @param w the window to close
         * @see java.awt.Container#remove(java.awt.Component) Container.remove
         */
	public void remove(Window w) {

		if (windows.contains(w)) {

			windows.removeElement(w);

			if (w==currentWindow) {

                                setFocusedComponent(null);
				currentWindow = (Window)windows.lastElement();
                                currentWindow.setupFocusedComponent();
			}

		}

	}
        
        /**
         * @return an Vector of InternalFrame objects
         * @see javax.swing.JDesktopPane#getAllFrames() JDesktopPane.getAllFrames
         */
        public Vector getAllFrames() {
            
            return windows;

        }

        /**
         * @return the internal frame that's currently selected
         * @see javax.swing.JDesktopPane#getSelectedFrame() JDesktopPane.getSelectedFrame
         */
	public Window getSelectedFrame() {

		return currentWindow;
	}

        // #####################################################################
        // platform Requests
        // #####################################################################
        
	public void call(String number) {

		try {
                        // TODO remove spaces from number
			midlet.platformRequest( "tel:" + number );
		}
		catch (ConnectionNotFoundException e) {
                        log("can not call "+e.toString());
			e.printStackTrace();
		}

	}

	public void vibration(int duration){
		try {
			Display.getDisplay(midlet).vibrate(duration);
		}
                catch(Exception e){
                    log("can not vibration "+e.toString());
                    e.printStackTrace();
                }
	}

        public Midlet getMidlet() {
            return midlet;
        }
        
        public void exit() {
		midlet.destroyApp(true);
	}
        
        
        // #####################################################################
        // debug dialog
        // #####################################################################

	private Window debugwindow;
	private MultilineLabel text;
        private String mem;
        
	public void log(String s) {

		if (debugMode) {
			
			if (debugwindow==null) {

				debugwindow = new Window( new LineBorder(0x00000000) );
				debugwindow.setBounds(10, 10, getWidth()-20, getHeight()/2);
				text = new MultilineLabel("",new Font(),Graphics.LEFT,0);
				text.setForeground(0x00000000);
				debugwindow.setContentPane( new ScrollPane(text) );
				debugwindow.getContentPane().setBackground(0x00FFFFFF);
				//debugwindow.getContentPane().setTransparent(false);
				//debugwindow.getContentPane().doLayout();
				add(debugwindow);
                                debugwindow.setActionListener(debugwindow);
				debugwindow.setWindowCommand(1, new CommandButton("OK","hide") );

			}

			text.setSize( debugwindow.getWidth()-ScrollPane.getBarThickness(debugwindow.getWidth(), debugwindow.getHeight()) , text.getHeight() );
			text.append(s+"\n");
			setSelectedFrame(debugwindow);
			
		}
	}

        // #####################################################################
        // key commands
        // #####################################################################

        // we reuse this keyevent for all keyevents
	private KeyEvent keypad = new KeyEvent(this);

	public void keyPressed(int keyCode) {

		keypad.keyPressed(keyCode);

		passKeyEvent(keypad);

                // TODO: add this
		// if (vendor == Samsung) {
                //   if (keyCode == KeyEvent.KEY_SOFTKEY1 || KeyEvent == Keypad.KEY_SOFTKEY2) {
                //           keyReleased(keyCode);
                //   }
		// }

	}

	public void keyReleased(int keyCode) {

		keypad.keyReleased(keyCode);

		passKeyEvent(keypad);

	}

	public void keyRepeated(int keyCode) {

	    keypad.keyRepeated(keyCode);

	    passKeyEvent(keypad);
	}


        // #####################################################################
        // pointer commands
        // #####################################################################

	public static final int DRAGGED = 0;
	public static final int PRESSED = 1;
	public static final int RELEASED = 2;
        private Component pointerComponent;
        
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

                if (type == PRESSED) {
                    
                    // check if pressing on a softkey
                    for (int c=0;c<componentCommands.length;c++) {
                        Component comp = getSoftkeyRenderer(c);
                        if (comp!=null) {
                            int cx = comp.getXWithBorder();
                            int cy = comp.getYWithBorder();
                            if (comp!=null && x>=cx && x<=cx+comp.getWidthWithBorder() && y>=cy && y<=cy+comp.getHeightWithBorder()) {
                                softKeyActivated(c);
                                return;
                            }
                        }
                    }

                    
                    pointerComponent = currentWindow.getComponentAt( x - currentWindow.getX(), y - currentWindow.getY());
                }
                
                if (pointerComponent!=null) {
                    pointerComponent.pointerEvent(type, x - currentWindow.getX()-pointerComponent.getXInWindow(), y - currentWindow.getY()-pointerComponent.getYInWindow());
                }
            }
            catch(Throwable th) {
                    th.printStackTrace();
                    log( "Exception in pointerEvent" + th.toString() );
            }

        }
        
	// #####################################################################
        // other events from the canvas
        // #####################################################################

        protected void sizeChanged(int w, int h) {
            //#debug
            System.out.println("sizeChanged!!");
            if (w>h) {
                sideSoftKeys = true;
            }
            else {
                sideSoftKeys = false;
            }
            
            for (int c=0;c<windows.size();c++) {
                Window window = (Window)windows.elementAt(c);
                
                // TODO RESIZE better
                if (window.getX()==0 && window.getY()==0) {
                    window.setSize(w, h);
                }
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


}