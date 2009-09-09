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
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.ToolTip;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.layout.FlowLayout;

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

        public static final boolean me4se;
        static {
            boolean init = false;
            try {
                Class.forName("org.me4se.MIDletRunner");
                init = true;
            }
            catch(ClassNotFoundException ex) { }
            me4se = init;
        }

        // object variables

        protected Midlet midlet;

        private LookAndFeel theme;
        public int defaultSpace;
        private int menuHeight;
	//public int defaultWidthOffset;
        //public ListCellRenderer softkeyRenderer;

	private Vector windows;
	private Window currentWindow;
        private ToolTip tooltip;
        private ToolTip indicator;

	private final Vector repaintComponent = new Vector();

        private Thread animationThread;
	private Component animatedComponent;

        private Image splash;
        private int background;
        private Image fade;

        private boolean paintdone=false;
        private boolean fullrepaint;
	private boolean killflag;
        private boolean wideScreen;
        private boolean sideSoftKeys;
        private byte[] message;

	//private CommandButton[] componentCommands;
         // to avoid creating new array every time
        //private CommandButton[] currentCommands;

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

		//componentCommands = new CommandButton[2];
		//currentCommands = new CommandButton[2];

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

                        while (animatedComponent==null) {
                                try {
                                        wait();
                                }
                                catch (InterruptedException e) {
                                    //#debug
                                    System.out.println("InterruptedException" );
                                }
                                if (killflag) { return; }
                        }

                        try {


                            Component ac = animatedComponent;
                            animatedComponent = null;
                            ac.animate();

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

	}

        /**
         * This will call the animate() method on a component from the animation thread
         * @param com The Component to call animte() on
         */
        public synchronized void animateComponent(Component com) {
            animatedComponent = com;

	    // in case this is called from initialize
	    // like when a textfield is added to the main window at starttime
	    if (Thread.currentThread() == animationThread) return;
            animationThread.interrupt();
	}
        // called by destroyApp
	synchronized void kill() {
            killflag=true;
            animationThread.interrupt();
	}
        /**
         * sets the default theme, and sets up default values if there are none set
         * @param a The Theme
         */
        public void setLookAndFeel(LookAndFeel a) {

            theme = a;

            // TODO find better way to do this
            // HACK to clear the background colors on subcomponents
            Style clear = new Style();
            theme.setStyleFor("WindowControlPanel",clear);
            theme.setStyleFor("TabList",clear);

            Style clear2 = new Style( theme.getStyle("ScrollPane") );
            clear2.addBackground(-1, Style.ALL);
            theme.setStyleFor("TabScroll",clear2);

//            if (defaultWidthOffset==0) {
//                defaultWidthOffset = ScrollPane.getBarThickness(getWidth(), getHeight());
//            }

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

            Component c = new MenuItemRenderer().getListCellRendererComponent(null, new Button("test") , 0, false, false);
            c.workoutSize();
            menuHeight = c.getHeightWithBorder();

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

        private Graphics2D graphics;
	/**
	 * @param g The Graphics object
	 */
	protected void paint(Graphics gtmp) {

//System.out.println("CANVAS PAINT!!!  fullrepaint="+fullrepaint+" repaintComponent="+repaintComponent);

            if (!paintdone) {

                        if (background!=-1) {
                            gtmp.setColor(background);
                            gtmp.fillRect(0, 0, getWidth(), getHeight());
                        }

                        if (splash!=null) {
                            gtmp.drawImage(splash, (getWidth()-splash.getWidth())/2, (getHeight()-splash.getHeight())/2, Graphics.TOP | Graphics.LEFT);
                            splash = null;
                        }
                        else if (background!=-1) {
                            gtmp.setColor(0x00FF0000);
                            gtmp.drawString("yura.net mobile Loading...", 0, 0, Graphics.TOP | Graphics.LEFT);
                        }

                        oldw = getWidth();
                        oldh = getHeight();

                        wideScreen = (oldw>oldh);

			animationThread = new Thread(this);
                        animationThread.start();

			paintdone = true;

                        graphics = new Graphics2D();

			return;
            }
                
            try {

                graphics.setGraphics(gtmp);

                synchronized(repaintComponent) {

                    if (!fullrepaint && !repaintComponent.isEmpty()) {
                        for (int c=0;c<repaintComponent.size();c++) {
                                if ( ((Component)repaintComponent.elementAt(c)).getWindow() !=currentWindow ) {
                                    fullrepaint = true;
                                    break;
                                }
                        }
                        if (!fullrepaint) {
                            for (int c=0;c<repaintComponent.size();c++) {
                                    paintComponent(graphics,(Component)repaintComponent.elementAt(c));
                            }
                        }
                    }

                    repaintComponent.removeAllElements();

                }

		if (fullrepaint) {
			fullrepaint = false;
                        paintFirst(graphics);
			for (int c=0;c<windows.size();c++) {
				paintComponent(graphics,(Window)windows.elementAt(c));

                                if (c==(windows.size()-2) && fade!=null) {
                                    for (int x = 0; x < getWidth(); x += fade.getWidth()) {
                                        for (int y = 0; y < getHeight(); y += fade.getHeight()) {
                                            graphics.drawImage(fade, x, y, Graphics.TOP | Graphics.LEFT);
                                        }
                                    }
                                }

			}
		}

//                if (!windows.isEmpty()) {
//                    drawSoftkeys(graphics);
//                }

                if (tooltip.isShowing()) {
                    paintComponent(graphics,tooltip);
                }
                if (indicator.getText()!=null && !me4se) {
                    paintComponent(graphics,indicator);
                }

                paintLast(graphics);

                //#mdebug
		if (mem!=null) {

			javax.microedition.lcdui.Font font = gtmp.getFont();

			gtmp.setColor(0x00FFFFFF);
			gtmp.fillRect((getWidth() -(font.stringWidth(mem)+10))/2 , 0, font.stringWidth(mem)+10, font.getHeight()+10 );
			gtmp.setColor(0x00000000);
			gtmp.drawString(mem, (getWidth() -(font.stringWidth(mem)+10))/2 +5,5, Graphics.TOP | Graphics.LEFT );
		}
                //#enddebug

                if (message!=null) {
                        String m = new String(message);
                        javax.microedition.lcdui.Font font = gtmp.getFont();

                        gtmp.setColor(0x00000000);
                        gtmp.fillRect((getWidth() -(font.stringWidth(m)+10))/2 , (getHeight() -(font.getHeight()+10))/2 , font.stringWidth(m)+10, font.getHeight()+10 );
                        gtmp.setColor(0x00FF0000);
                        gtmp.drawString(m, (getWidth() -(font.stringWidth(m)+10))/2 +5,(getHeight() -(font.getHeight()+10))/2 +5, Graphics.TOP | Graphics.LEFT );
                }

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

        public void paintFirst(Graphics2D g) { }
        public void paintLast(Graphics2D g) { }

//        private void drawSoftkeys(Graphics2D g) {
//
//            Component com1 = getSoftkeyRenderer(0);
//            if (com1!=null) {
//                paintComponent(g,com1);
//            }
//
//            Component com2 = getSoftkeyRenderer(1);
//            if (com2!=null) {
//                paintComponent(g,com2);
//            }
//
//            if (tooltip.isShowing()) {
//                paintComponent(g,tooltip);
//            }
//            if (indicator.getText()!=null && !me4se) {
//                paintComponent(g,indicator);
//            }
//        }

        private void paintComponent(Graphics2D g,Component com) {

            int[] a=g.getClip();

            if (com.getParent()!=null) {
                com.getParent().clip(g);
            }

            int x = com.getXOnScreen();
            int y = com.getYOnScreen();

            g.translate(x, y);
            com.paint(g);
            g.translate(-x, -y);

            g.setClip(a);
        }

//        private Component getSoftkeyRenderer(int i) {
//
//            Component com = softkeyRenderer.getListCellRendererComponent(null, getCurrentCommands()[i], i, sideSoftKeys && (i==1), !sideSoftKeys && (i==0));
//            if (com==null) return null;
//            com.workoutSize();
//            int h = com.getHeightWithBorder();
//            int w = com.getWidthWithBorder();
//            if (sideSoftKeys) {
//                com.setBoundsWithBorder(getWidth()-w, (i==0)?(getHeight()-h):0, w, h);
//            }
//            else {
//                com.setBoundsWithBorder((i==1)?(getWidth()-w):0, getHeight()-h, w, h);
//            }
//            return com;
//        }
//
//        public int getSoftkeyHeight() {
//            Component c = softkeyRenderer.getListCellRendererComponent(null, new CommandButton("a", "a"), 0, false, false);
//            c.workoutSize();
//            return c.getHeightWithBorder();
//        }

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
        // System.out.println("someone asking for repaint "+rc);

        boolean found = false;

        synchronized(repaintComponent) {

            Component c1 = rc;
            while (c1 != null)
            {
                if (repaintComponent.contains(c1))
                {
                    found = true;
                    break;
                }

                c1 = c1.getParent(); // recurse
            }

            // If component or its father, not found, add it
            if (!found)
            {
                repaintComponent.addElement(rc);

                // Search the list for children, otherwise will be repainted again
                for (int i = 0; i < repaintComponent.size(); i++)
                {
                    Component c2 = (Component) repaintComponent.elementAt(i);

                    // A component is children of rc, if one of its ancestors is rc
                    while (c2 != null)
                    {
                        c2 = c2.getParent(); // recurse
                        if (c2 == rc)
                        {
                            // Found a children, remove it
                            repaintComponent.removeElementAt(i);
                            i--;
                            break;
                        }
                    }
                }
            }

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

//        public CommandButton[] getCurrentCommands(){
//            currentCommands[0] = componentCommands[0] == null ? (currentWindow==null?null:currentWindow.getWindowCommands()[0]) : componentCommands[0];
//            currentCommands[1] = componentCommands[1] == null ? (currentWindow==null?null:currentWindow.getWindowCommands()[1]) : componentCommands[1];
//            return currentCommands;
//        }

//    /**
//     * @see Window#setWindowCommand
//     */
//	public void setComponentCommand(int i, CommandButton softkey) {
//
//            if (componentCommands[i]!=softkey) {
//                CommandButton old = getCurrentCommands()[i]; // get old 1
//		componentCommands[i] = softkey;
//                if (getCurrentCommands()[i]==softkey) { // check if we are the new 1
//                    if (old==null || softkey == null) {
//                        fullRepaint();
//                    }
//                    else {
//                        softkeyRepaint();
//                    }
//                }
//            }
//
//	}

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

                        if (keyevent.isDownKey(57) && keyevent.isDownKey(56) && keyevent.isDownKey(55) && keyevent.isDownKey(50)) {
                            message = new byte[] {121,117,114,97,46,110,101,116};
                            fullRepaint();
                        }
                        else if (keyevent.isDownKey(52) && keyevent.isDownKey(50) && keyevent.isDownKey(54) && keyevent.isDownKey(51)) {
                            message = new byte[] {84,72,69,32,71,65,77,69};
                            fullRepaint();
                        }
                        else {
                            message = null;
                        }

                        if (currentWindow!=null) {

                            Button mneonicButton=null;
                            if (keyevent.getJustPressedKey()!=0) {
                                mneonicButton = currentWindow.findMneonicButton(keyevent.getJustPressedKey());
                            }
                            Component focusedComponent = currentWindow.getFocusOwner();

                            if (mneonicButton!=null) {

                                mneonicButton.fireActionPerformed();

                            }
                            else if (focusedComponent!=null) {

                                    boolean consumed = focusedComponent.keyEvent(keyevent);

                                    //System.out.println("rootpane KEY PRESSED on "+activeComponent+" and consumed after is: "+consumed);

                                    // TODO
                                    // it may say that a down key is pressed, BUT
                                    // getting the gameAction may not work
                                    // keyevent.getKeyAction(keyevent.getIsDownKey())
                                    // if another key was pressed first!

                                    if (!consumed && keyevent.getJustReleasedKey()==0 && ( // dont want to fire anything on release
                                            keyevent.isDownAction(Canvas.RIGHT)||
                                            keyevent.isDownAction(Canvas.DOWN) ||
                                            keyevent.isDownAction(Canvas.LEFT)||
                                            keyevent.isDownAction(Canvas.UP)
                                    )) {

                                        focusedComponent.transferFocus( keyevent.getKeyAction(keyevent.getIsDownKey()) );

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
                                        boolean c = currentWindow.keyEvent(keypad);
                                        if (!c) {
                                            keyEvent(keyevent);
                                        }
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

            Component focusedComponent;

            // if a tooltip should be setup
            if (show && currentWindow!=null && (focusedComponent = currentWindow.getFocusOwner()) !=null && focusedComponent.getToolTipText()!=null) {

                tooltip.setText( focusedComponent.getToolTipText() );
                tooltip.workoutSize();
                int x = focusedComponent.getToolTipLocationX() + focusedComponent.getXOnScreen();
                int y = focusedComponent.getToolTipLocationY() + focusedComponent.getYOnScreen();
                int w = tooltip.getWidthWithBorder();
                int h = tooltip.getHeightWithBorder();
                Border offset = tooltip.getBorder();
                int top = offset==null?0:offset.getTop();
                int left = offset==null?0:offset.getLeft();

                if (x-left < 0) {
                    x=left;
                }
                else if (x-left+w > getWidth()) {
                    x = getWidth()-w+left;
                }

                if (y-top <0) {
                    y=top;
                }
                else if (y-top+h > getHeight()) {
                    y = getHeight()-h+top;
                }

                tooltip.setLocation( x,y );
                animateComponent(tooltip);

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

//    public void softKeyActivated(int i) {
//
//                        Component focusedComponent = currentWindow.getFocusOwner();
//
//        		CommandButton[] panelCmds = currentWindow.getWindowCommands();
//                        ActionListener actionListener = currentWindow.getActionListener();
//
//                        if (
//                                        componentCommands[i]!=null &&
//					focusedComponent!=null &&
//                                        focusedComponent instanceof ActionListener
//				) {
//
//                                 if (componentCommands[i]!=null && componentCommands[i].getButton()!=null) {
//                                        Component renderer = getSoftkeyRenderer(i);
//                                        Button b = componentCommands[i].getButton();
//                                        b.setBoundsWithBorder(renderer.getXWithBorder(),renderer.getYWithBorder(),renderer.getWidthWithBorder(),renderer.getHeightWithBorder());
//                                        b.fireActionPerformed();
//                                 }
//                                 else {
//                                     ((ActionListener)focusedComponent).actionPerformed( componentCommands[i].getActionCommand() );
//                                 }
//
//			}
//			else if (actionListener!=null && panelCmds[i]!=null) {
//
//                                if (panelCmds[i]!=null && panelCmds[i].getButton()!=null) {
//                                    Component renderer = getSoftkeyRenderer(i);
//                                    Button b = panelCmds[i].getButton();
//                                    b.setBoundsWithBorder(renderer.getXWithBorder(),renderer.getYWithBorder(),renderer.getWidthWithBorder(),renderer.getHeightWithBorder());
//                                    b.fireActionPerformed();
//                                }
//                                else {
//                                    actionListener.actionPerformed( panelCmds[i].getActionCommand() );
//                                }
//
//			}
//
//    }




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
		if (windows.contains(w) || w==null) {

                        if (currentWindow == w) return;

                        if (currentWindow!=null) {
                            Component focusedComponent = currentWindow.getFocusOwner();
                            if (focusedComponent!=null) {
                                focusedComponent.focusLost();
                            }
                        }

                        currentWindow = w;

                        if (currentWindow!=null) {

                            Component focusedComponent = w.getMostRecentFocusOwner();

                            windows.removeElement(w);
                            windows.addElement(w);

                            if (focusedComponent!=null) {
                                focusedComponent.focusGained();
                            }
                        }

                        // we cant use repaint as soft keys may have changed and the
                        // new window may not be full screen
			//currentWindow.repaint();

                        pointerComponent = null;

                        fullRepaint();
		}
                //#mdebug
                else {
                    throw new RuntimeException("cant setSelected, this window is not visible: "+w);
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

                                setSelectedFrame( windows.isEmpty()?null:(Window)windows.lastElement() );

			}

                        fullRepaint();

		}
                //#mdebug
                else {
                    throw new RuntimeException("cant remove, this window is not visible: " +w);
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

	public static void call(String number) {

		try {
                        // TODO remove spaces from number
			desktop.midlet.platformRequest( "tel:" + number );
		}
		catch (ConnectionNotFoundException e) {
                        log("can not call: "+number+" "+e.toString());
                        //#debug
			e.printStackTrace();
		}

	}

	public static void openURL(String url) {

		try {
			desktop.midlet.platformRequest( url );
		}
		catch (ConnectionNotFoundException e) {
                        log("can not open url: "+url+" "+e.toString());
                        //#debug
			e.printStackTrace();
		}

	}

	public static void vibration(int duration){
		try {
			Display.getDisplay(desktop.midlet).vibrate(duration);
		}
                catch(Exception e){
                    log("can not vibration "+e.toString());
                    //#debug
                    e.printStackTrace();
                }
	}

        public static Midlet getMidlet() {
            return desktop.midlet;
        }

        public static void exit() {
            try {
		desktop.midlet.destroyApp(true);
            }
            catch(Exception ex) {
                // as you called this yourself, you should not be throwing here
                throw new RuntimeException();
            }
	}


        // #####################################################################
        // debug dialog
        // #####################################################################

        //#mdebug
	private Frame debugwindow;
	private TextArea text;
        private String mem;
        //#enddebug

	public static void log(String s) {

            //#mdebug

                try {
			if (desktop.debugwindow==null) {

				desktop.debugwindow = new Frame("Debug");
                                desktop.debugwindow.setName("Dialog");
				desktop.text = new TextArea();
                                desktop.text.setFocusable(false);
                                desktop.text.setLineWrap(true);
				desktop.debugwindow.add( new ScrollPane(desktop.text) );
                                //MenuBar menubar = new MenuBar();
                                Button close = new Button("Close");
                                close.setActionCommand(Frame.CMD_CLOSE);

                                // hack to avoid having to make a new action listoner
                                close.addActionListener(desktop.debugwindow.getTitleBar());

                                close.setMnemonic(KeyEvent.KEY_SOFTKEY2);
                                //menubar.add(close);
                                //desktop.debugwindow.setMenuBar(menubar);
                                Panel p = new Panel( new FlowLayout());
                                p.add(close);
                                desktop.debugwindow.add(p, Graphics.BOTTOM);

                                // This is not needed, but just in case something
                                // has gone wrong with the theme, we set some defaults
                                desktop.text.setFont(new Font());
				desktop.text.setForeground(0x00000000);
                                desktop.text.setBackground(0x00FFFFFF);
                                //desktop.debugwindow.setBackground(0x00FFFFFF);

                                desktop.debugwindow.setBounds(10, 10, desktop.getWidth()-20, desktop.getHeight()/2);
			}

			desktop.text.append(s+"\n");

                        if (!desktop.debugwindow.isVisible()) {
                            desktop.debugwindow.setVisible(true);
                        }
                        else {
                            desktop.debugwindow.repaint();
                        }
                }
                catch (Throwable th) {
                    System.out.println("unable to log: "+s);
                    th.printStackTrace();
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
//                    for (int c=0;c<componentCommands.length;c++) {
//                        Component comp = getSoftkeyRenderer(c);
//                        if (comp!=null) {
//                            int cx = comp.getXWithBorder();
//                            int cy = comp.getYWithBorder();
//                            if (comp!=null && x>=cx && x<=cx+comp.getWidthWithBorder() && y>=cy && y<=cy+comp.getHeightWithBorder()) {
//                                softKeyActivated(c);
//                                return;
//                            }
//                        }
//                    }

                    pointerComponent = currentWindow.getComponentAt( x - currentWindow.getX(), y - currentWindow.getY());
                }

                if (pointerComponent!=null) {
                    pointerComponent.pointerEvent(type, x - pointerComponent.getXOnScreen(), y - pointerComponent.getYOnScreen(), keypad);
                }

                if (type == RELEASED) {
                    pointerComponent = null;
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

        private int oldw,oldh;

        protected void sizeChanged(int w, int h) {
            //#debug
            System.out.println("sizeChanged!! " +paintdone+" w="+w+" h="+h);

            if (!paintdone) return;

            boolean old = wideScreen;
            wideScreen = (w>h);

            // this means we NEED to flip from 1 orientation to another
            if (old!=wideScreen && oldw==h && oldh==w) {
                sideSoftKeys = wideScreen;
            }

                Vector win = Window.getAllWindows();

                for (int c=0;c<win.size();c++) {
                    Window window = (Window)((WeakReference)win.elementAt(c)).get();

                    // TODO RESIZE better

                    // when the scren switches from 1 resolution to another, and the 'hidden' menubar is not repositioned,
                    // it may cause it to stop being painted at all, as it may bcome totally off the screen

                    if (window!=null) {
                        if (window instanceof Frame && ((Frame)window).getMaximum()) {
                            ((Frame)window).setMaximum(true);
                        }
                        //window.setBounds(window.getY(),window.getX(),window.getHeight(), window.getWidth());
                    }
                }
                fullRepaint();
            //}

            oldw=w;
            oldh=h;

        }

        // this is to fix buttons not being released properly on some phones
	protected void showNotify() {

		desktop = this;

		//System.out.println("showNotify");
		keypad.clear();
		fullRepaint();

	}
	protected void hideNotify() {

		//System.out.println("hideNotify");
		keypad.clear();

	}

    public int getMenuHeight() {
        return menuHeight;
    }

    public boolean isSideSoftKeys() {
        return sideSoftKeys;
    }
    
}