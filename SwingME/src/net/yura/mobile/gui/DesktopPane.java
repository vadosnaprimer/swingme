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
        private boolean paintdone=false;
	private boolean killflag;

	private CommandButton[] omponentCommands;
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

		omponentCommands = new CommandButton[2];
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
            if (theme.barHeight==0) {
                theme.barHeight=theme.font.getHeight();
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
            
            currentWindow.setSize(getWidth(),getHeight()-theme.barHeight);

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

                System.out.println("CANVAS PAINT!!!  fullrepaint="+fullrepaint+" repaintComponent="+repaintComponent);

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

		if (DesktopPane.debugMode && mem!=null) {

			javax.microedition.lcdui.Font font = g.getFont();

			g.setColor(0x00FFFFFF);
			g.fillRect((getWidth() -(font.stringWidth(mem)+10))/2 , 0, font.stringWidth(mem)+10, font.getHeight()+10 );
			g.setColor(0x00000000);
			g.drawString(mem, (getWidth() -(font.stringWidth(mem)+10))/2 +5,5, Graphics.TOP | Graphics.LEFT );
		}

		drawSoftkeys(g);

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
		repaintComponent = null;
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
         * override this to draw softkeys how you want to
         * @param g The Graphics
         */
        public void drawSoftkeys(Graphics g) {

            Font f = getDefaultTheme().font;

            g.setColor( getDefaultTheme().background );
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
            currentCommands[0] = omponentCommands[0] == null ? currentWindow.getWindowCommands()[0] : omponentCommands[0];
            currentCommands[1] = omponentCommands[1] == null ? currentWindow.getWindowCommands()[1] : omponentCommands[1];
            return currentCommands;
        }

    /**
     * @see Window#setWindowCommand
     */
	public void setComponentCommand(int i, CommandButton softkey) {

		omponentCommands[i] = softkey;
		repaint();
                // TODO: is this correct, will ANY repaint do? (also in another place)

	}

	private void passKeyEvent(KeyEvent keyevent) {

		try {

			if(keyevent.isDownKey(Canvas.KEY_STAR)){

				mem = (Runtime.getRuntime().freeMemory() >> 10) +"K/" +(Runtime.getRuntime().totalMemory() >> 10)+"K";
				repaint();
			}
			else {

				mem = null;

			}

			CommandButton[] panelCmds = currentWindow.getWindowCommands();
                        ActionListener actionListener = currentWindow.getActionListener();
                        
                        boolean softkey1Action = keyevent.isDownKey(KeyEvent.KEY_SOFTKEY1) || keyevent.justReleasedKey(KeyEvent.KEY_SOFTKEY1);
                        boolean softkey2Action = keyevent.isDownKey(KeyEvent.KEY_SOFTKEY2) || keyevent.justReleasedKey(KeyEvent.KEY_SOFTKEY2);
                        
                        // ############# we use justReleasedKey for firing actions, this means the action is ONLY
                        // ############# fired off when the key is released!!!
                        
			if (
					focusedComponent!=null && focusedComponent instanceof ActionListener && (
						(omponentCommands[0]!=null && softkey1Action ) ||
						(omponentCommands[1]!=null && softkey2Action )
					)
				) {

				if (omponentCommands[0]!=null && keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY1)) {
					((ActionListener)focusedComponent).actionPerformed( omponentCommands[0].getActionCommand() );
				}
				else if (omponentCommands[1]!=null && keyevent.justPressedKey(KeyEvent.KEY_SOFTKEY2)) {
					((ActionListener)focusedComponent).actionPerformed( omponentCommands[1].getActionCommand() );
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
				debugwindow.getContentPane().setTransparent(false);
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
                    log( "Exception in pointerEvent" + th.toString() );
            }

        }
        
	// #####################################################################
        // other events from the canvas
        // #####################################################################

        protected void sizeChanged(int w, int h){
            
            for (int c=0;c<windows.size();c++) {
                Window window = (Window)windows.elementAt(c);
                
                // TODO RESIZE
                
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