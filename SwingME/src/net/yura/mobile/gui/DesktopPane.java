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

import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.cellrenderer.DefaultSoftkeyRenderer;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.ToolTip;
import net.yura.mobile.gui.components.Window;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JDesktopPane
 */
public class DesktopPane extends Canvas implements Runnable {

        // static methods
        private static DesktopPane desktop;
        
        public static DesktopPane getDesktopPane() {
            return desktop;
        }
        
        /**
         * this methos should ONLY be called from the updateUI() method in components
         * @see javax.swing.UIManager#getUI(javax.swing.JComponent) UIManager#getUI
         */
        public static Style getDefaultTheme(Component comp) {
            Style style = desktop.theme.getStyle(comp.getName());
            if (style==null) {
                style = desktop.theme.getStyle("");
            }
            return style;
        }
        /**
         * @param com
         * @see javax.swing.SwingUtilities#updateComponentTreeUI(java.awt.Component) SwingUtilities.updateComponentTreeUI
         */
        public static void updateComponentTreeUI(Component com) {

            if (com instanceof Panel) {
                Vector v = ((Panel)com).getComponents();
                for (int c=0;c<v.size();c++) {
                    updateComponentTreeUI( (Component)v.elementAt(c) );
                }
            }
            com.updateUI();

        }
        
        // object variables
        
        protected Midlet midlet;
        
        private LookAndFeel theme;
        public int defaultSpace;
	public int defaultWidthOffset;
        public ListCellRenderer softkeyRenderer;

	private Vector windows;
	private Window currentWindow;
        private ToolTip tooltip;
        private ToolTip indicator;

	private Component focusedComponent;
	private Vector repaintComponent = new Vector();
        
        private Thread animationThread;
	private Component animatedComponent;

        private Image splash;
        private Image fade;
        private int background;

        private boolean paintdone=false;
        private boolean fullrepaint;
	private boolean killflag;
        private boolean wideScreen;
        private boolean sideSoftKeys;

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

//		// check if we want to be in debug mode
//		String s;
//		if ((s = midlet.getAppProperty("Debug-Mode")) != null && ( s.toUpperCase().equals("OFF") || s.toUpperCase().equals("NO") || s.toUpperCase().equals("FALSE") || s.toUpperCase().equals("F") ) ) {
//			DesktopPane.debugMode = false;
//		}

		windows = new Vector();

                // ceate a new window to use as the main window
		//add( new Window() );

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
                    //#debug
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
                                            System.out.println("InterruptedException" );
                                        }
					if (killflag) { return; }
				}
			}

			try {

                            synchronized (this) {
                                Component ac = animatedComponent;
                                animatedComponent = null;
                                ac.animate();
                            }

			}
                        catch (InterruptedException e) {
                                //#debug
                                System.out.println("InterruptedException during animation" );
                        }
			catch(Throwable th) {
                            //#debug
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
            synchronized (this) {
		animatedComponent = com;
                animationThread.interrupt();

            }
	}
        // called by destroyApp
	void kill() {
            synchronized (this) {
                killflag=true;
                animationThread.interrupt();
            }
	}
        /**
         * sets the default theme, and sets up default values if there are none set
         * @param a The Theme
         */
        public void setLookAndFeel(LookAndFeel a) {

            theme = a;

            if (defaultWidthOffset==0) {
                defaultWidthOffset = ScrollPane.getBarThickness(getWidth(), getHeight());
            }

            if (defaultSpace==0) {
                
                int maxSize = Math.max(getWidth(),getHeight());
                
                    if(maxSize <= 128) {
                            defaultSpace=3;
                    }
                    else if(maxSize <= 208) {
                            defaultSpace=5;
                    }
                    else {
                            defaultSpace=7;
                    }
                
            }
            
            if (softkeyRenderer==null) {
                softkeyRenderer = new DefaultSoftkeyRenderer();
            }
            tooltip = new ToolTip();
            indicator = new ToolTip();
            //currentWindow.setSize(getWidth(),getHeight());

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

                        wideScreen = (getWidth()>getHeight());
                        
			animationThread = new Thread(this);
                        animationThread.start();

			paintdone = true;

			return;
            }
                
            try {

                if (!fullrepaint && !repaintComponent.isEmpty()) {
                    for (int c=0;c<repaintComponent.size();c++) {
                            if ( ((Component)repaintComponent.elementAt(c)).getWindow() !=currentWindow ) {
                                fullrepaint = true;
                                break;
                            }
                    }
                    if (!fullrepaint) {
			for (int c=0;c<repaintComponent.size();c++) {
				paintComponent(g,(Component)repaintComponent.elementAt(c));                               
			}
                    }
                }

                repaintComponent.removeAllElements();
                
		if (fullrepaint) {
			fullrepaint = false;
                        paintFirst(g);
			for (int c=0;c<windows.size();c++) {
				paintComponent(g,(Window)windows.elementAt(c));
                                
                                if (c==(windows.size()-2) && fade!=null) {
                                    for (int x = 0; x < getWidth(); x += fade.getWidth()) {
                                        for (int y = 0; y < getHeight(); y += fade.getHeight()) {
                                            g.drawImage(fade, x, y, Graphics.TOP | Graphics.LEFT);
                                        }
                                    }
                                }
                                
			}
		}

                //#mdebug
		if (mem!=null) {

			javax.microedition.lcdui.Font font = g.getFont();

			g.setColor(0x00FFFFFF);
			g.fillRect((getWidth() -(font.stringWidth(mem)+10))/2 , 0, font.stringWidth(mem)+10, font.getHeight()+10 );
			g.setColor(0x00000000);
			g.drawString(mem, (getWidth() -(font.stringWidth(mem)+10))/2 +5,5, Graphics.TOP | Graphics.LEFT );
		}
                //#enddebug

		drawSoftkeys(g);
                paintLast(g);
            }
            catch(Throwable th) {
                //#debug
                    th.printStackTrace();
                    log( "Error in paint: " + th.toString() );
            }

	}

        public void setDimImage(Image a) {
            fade = a;
        }
        
        public void paintFirst(Graphics g) { }
        public void paintLast(Graphics g) { }
        
        private void drawSoftkeys(Graphics g) {
            
            Component com1 = getSoftkeyRenderer(0);
            if (com1!=null) {
                paintComponent(g,com1);
            }

            Component com2 = getSoftkeyRenderer(1);
            if (com2!=null) {
                paintComponent(g,com2);
            }

            if (tooltip.isShowing()) {
                paintComponent(g,tooltip);
            }
            if (indicator.getText()!=null) {
                paintComponent(g,indicator);
            }
        }

        private void paintComponent(Graphics g,Component com) {
            
            int a=g.getClipX();
            int b=g.getClipY();
            int c=g.getClipWidth();
            int d=g.getClipHeight();
            if (com.getParent()!=null) {
                com.getParent().clip(g);
            }

            int x = com.getXOnScreen();
            int y = com.getYOnScreen();

            g.translate(x, y);
            com.paint(g);
            g.translate(-x, -y);
            
            g.setClip(a,b,c,d);
        }

        private Component getSoftkeyRenderer(int i) {
            // if (theme==null || theme.softkeyRenderer==null) return null; // sometimes throws on emulator
            Component com = softkeyRenderer.getListCellRendererComponent(null, getCurrentCommands()[i], i, sideSoftKeys && (i==1), !sideSoftKeys && (i==0));
            if (com==null) return null;
            com.workoutSize();
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
        
        public int getSoftkeyHeight() {
            Component c = softkeyRenderer.getListCellRendererComponent(null, new CommandButton("a", "a"), 0, false, false);
            c.workoutSize();
            return c.getHeightWithBorder();
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

		repaint();
	}

        /**
         * this is called when you call repaint() on a component
         * @param rc The Component to repaint
         */
        public void repaintComponent(Component rc) {
                //System.out.println("someone asking for repaint "+rc);
		if (!repaintComponent.contains(rc)) {
			repaintComponent.addElement(rc);
		}

		repaint();
	}
        
        /**
         * Repaint the softkeybar
         */
        public void softkeyRepaint() {
                repaint();
        }

        // #####################################################################
        // action handeling
        // #####################################################################
        
        public Component getFocusedComponent() {
		return focusedComponent;
	}

	public void setFocusedComponent(Component ac) {

		// TODO maybe have an option so the window will become active if any component on it is activated

            //#mdebug
            if (ac!=null && ac.getWindow() != currentWindow) {
                    throw new RuntimeException("setFocusedComponent on component thats not in the current window: "+ac);
            }
            //#enddebug
            
	       if(focusedComponent != null) {
	    	   focusedComponent.focusLost();
	       }

	       focusedComponent = ac;

                if (focusedComponent != null) {
                    focusedComponent.focusGained();
                }

	}
        
        public CommandButton[] getCurrentCommands(){
            currentCommands[0] = componentCommands[0] == null ? (currentWindow==null?null:currentWindow.getWindowCommands()[0]) : componentCommands[0];
            currentCommands[1] = componentCommands[1] == null ? (currentWindow==null?null:currentWindow.getWindowCommands()[1]) : componentCommands[1];
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
                    if (old==null || softkey == null) {
                        fullRepaint();
                    }
                    else {
                        softkeyRepaint();
                    }
                }
            }

	}
        
	private void passKeyEvent(KeyEvent keyevent) {

		try {

                        //#mdebug
			if(keyevent.isDownKey(Canvas.KEY_STAR)){
				mem = (Runtime.getRuntime().freeMemory() >> 10) +"K/" +(Runtime.getRuntime().totalMemory() >> 10)+"K";
				fullRepaint();
			}
			else {
				mem = null;
			}
                        //#enddebug

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

                                    boolean c = currentWindow.keyEvent(keypad);
                                    if (!c) {
					keyEvent(keyevent);
                                    }
				}
                                
			}
                        // sometimes keyevents come in on S40 b4 anything has been setup,
                        // such as the fire key being released after you start the app
			else if (currentWindow!=null) { //  if (keyListener!=null) {

				if (

					keyevent.isDownAction(Canvas.RIGHT)||
					keyevent.isDownAction(Canvas.DOWN) ||
					keyevent.isDownAction(Canvas.LEFT)||
					keyevent.isDownAction(Canvas.UP)

				) {
					currentWindow.passScrollUpDown( keyevent.getKeyAction(keyevent.getIsDownKey()) );
				}
                                else {
                                    boolean c = currentWindow.keyEvent(keypad);
                                    if (!c) {
					keyEvent(keyevent);
                                    }
                                }
			}



		}
		catch(Throwable th) {
                    //#debug
			th.printStackTrace();
			log( "Error in KeyEvent: " + th.toString() );
		}

                showHideToolTip(
                            keyevent.justReleasedAction(Canvas.RIGHT)||
                            keyevent.justReleasedAction(Canvas.DOWN) ||
                            keyevent.justReleasedAction(Canvas.LEFT)||
                            keyevent.justReleasedAction(Canvas.UP)
                    );

                
	}

	private void showHideToolTip(boolean show) {

                    // if a tooltip should be setup
                    if (show && focusedComponent!=null && focusedComponent.getToolTipText()!=null) {

                        tooltip.setText( focusedComponent.getToolTipText() );
                        tooltip.workoutSize();
                        tooltip.setLocation(
                            focusedComponent.getToolTipLocationX() + focusedComponent.getXOnScreen(),
                            focusedComponent.getToolTipLocationY() + focusedComponent.getYOnScreen()
                        );
                        animateComponent(tooltip);
                        // TODO make sure its not going off the screen!
                    }
                    else if (tooltip!=null) {
                        // this will never be null unless this method is called
                        // before the midlet is initialised, and this can happen
                        
                        // if there is a tooltip up or ready to go up,
                        // then kill it!
                        synchronized (this) {
                            if (tooltip.isWaiting()) {
                                animationThread.interrupt();
                            }
                        }
                    }


	}

        public void setIndicatorText(String txt) {
            indicator.setText(txt);
            indicator.workoutSize();
            int w = indicator.getWidthWithBorder();
            int h = indicator.getHeightWithBorder();
            if (sideSoftKeys) {
                indicator.setBoundsWithBorder(0, getHeight()-h,w,h);
            }
            else {
                indicator.setBoundsWithBorder(getWidth()-w, 0,w,h);
            }

            // as we dont know what size it was
            fullRepaint();

        }
        
    public void softKeyActivated(int i) {

        		CommandButton[] panelCmds = currentWindow.getWindowCommands();
                        ActionListener actionListener = currentWindow.getActionListener();

                        if (
                                        componentCommands[i]!=null &&
					focusedComponent!=null &&
                                        focusedComponent instanceof ActionListener
				) {

                                 if (componentCommands[i]!=null && componentCommands[i].getButton()!=null) {
                                        Component renderer = getSoftkeyRenderer(i);
                                        Button b = componentCommands[i].getButton();
                                        b.setBoundsWithBorder(renderer.getXWithBorder(),renderer.getYWithBorder(),renderer.getWidthWithBorder(),renderer.getHeightWithBorder());
                                        b.fireActionPerformed();
                                 }
                                 else {
                                     ((ActionListener)focusedComponent).actionPerformed( componentCommands[i].getActionCommand() );
                                 }
                                
			}
			else if (actionListener!=null && panelCmds[i]!=null) {

                                if (panelCmds[i]!=null && panelCmds[i].getButton()!=null) {
                                    Component renderer = getSoftkeyRenderer(i);
                                    Button b = panelCmds[i].getButton();
                                    b.setBoundsWithBorder(renderer.getXWithBorder(),renderer.getYWithBorder(),renderer.getWidthWithBorder(),renderer.getHeightWithBorder());
                                    b.fireActionPerformed();
                                }
                                else {
                                    actionListener.actionPerformed( panelCmds[i].getActionCommand() );
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

            //#mdebug
            if (windows.contains(w)) {
                    throw new RuntimeException("trying to set a window visible when it already is visible");
            }
            //#enddebug

		windows.addElement(w);
		setSelectedFrame(w);
	}

        /**
         * @param w the internal frame that's currently selected
         * @see javax.swing.JDesktopPane#setSelectedFrame(javax.swing.JInternalFrame) JDesktopPane.setSelectedFrame
         */
	public void setSelectedFrame(Window w) {
		if (windows.contains(w)) {
                    
                        if (currentWindow == w) return;
                        
                        setFocusedComponent(null);
			currentWindow = w;
			windows.removeElement(w);
			windows.addElement(w);
			currentWindow.setupFocusedComponent();
			currentWindow.repaint();
		}
                //#mdebug
                else {
                    throw new RuntimeException("cant setSelected, this window is not visible");
                }
                //#enddebug
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
                        
                        fullRepaint();

		}
                //#mdebug
                else {
                    throw new RuntimeException("cant remove, this window is not visible");
                }
                //#enddebug

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
                        //#debug
			e.printStackTrace();
		}

	}

	public void vibration(int duration){
		try {
			Display.getDisplay(midlet).vibrate(duration);
		}
                catch(Exception e){
                    log("can not vibration "+e.toString());
                    //#debug
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

        //#mdebug
	private Window debugwindow;
	private TextArea text;
        private String mem;
        //#enddebug
        
	public void log(String s) {

		//#mdebug
			
			if (debugwindow==null) {

				debugwindow = new Window();
                                debugwindow.setName("Dialog");
				text = new TextArea();
                                text.setSelectable(false);
                                text.setLineWrap(true);
				debugwindow.add( new ScrollPane(text) );
                                debugwindow.setActionListener(debugwindow);
				debugwindow.setWindowCommand(1, new CommandButton("OK","close") );
                                debugwindow.setBounds(10, 10, getWidth()-20, getHeight()/2);
                                
                                // This is not needed, but just in case something
                                // has gone wrong with the theme, we set some defaults
                                text.setFont(new Font());
				text.setForeground(0x00000000);
                                text.setBackground(0x00FFFFFF);
                                debugwindow.setBackground(0x00FFFFFF);

			}

			text.append(s+"\n");
                        
                        if (!debugwindow.isVisible()) {
                            debugwindow.setVisible(true);
                        }
                        else {
                            debugwindow.repaint();
                        }
			
		//#enddebug
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
                    pointerComponent.pointerEvent(type, x - pointerComponent.getXOnScreen(), y - pointerComponent.getYOnScreen());
                }
            }
            catch(Throwable th) {
                //#debug
                    th.printStackTrace();
                    log( "Exception in pointerEvent: " + th.toString() );
            }

	    showHideToolTip(type == PRESSED);

        }
        
	// #####################################################################
        // other events from the canvas
        // #####################################################################

        protected void sizeChanged(int w, int h) {
            //#debug
            System.out.println("sizeChanged!! " +paintdone);
            
            if (!paintdone) return;
            
            boolean old = wideScreen;
            wideScreen = (w>h);
            
            // this means we NEED to flip from 1 orientation to another
            if (old!=wideScreen) {

                sideSoftKeys = wideScreen;
                
                Vector win = Window.getAllWindows();
                
                for (int c=0;c<win.size();c++) {
                    Window window = (Window)((WeakReference)win.elementAt(c)).get();

                    // TODO RESIZE better
                    if (window!=null) {
                        window.setBounds(window.getY(),window.getX(),window.getHeight(), window.getWidth());
                    }
                }
                fullRepaint();
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