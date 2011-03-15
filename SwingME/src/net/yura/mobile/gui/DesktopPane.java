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

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.ToolTip;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.SystemUtil;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JDesktopPane
 */
public class DesktopPane extends Canvas implements Runnable {

    //#mdebug
    private static Vector keylisteners = new Vector();

    public static void addKeyPressListener(KeyListener listener){
        keylisteners.addElement(listener);
    }

    public void notifyKeyListeners(KeyEvent event){
        for (int i = 0; i < keylisteners.size(); i++){
            KeyListener listener = (KeyListener)keylisteners.elementAt(i);
            listener.keyPressedEvent(event);
        }
    }
    //#enddebug

    private static DesktopPane desktop;

    public static DesktopPane getDesktopPane() {
        return desktop;
    }

    /**
     * this method is used in {@link Component#getWindow()} and so needs to return the component you pass into it as the result if it is of class c
     * @see javax.swing.SwingUtilities#getAncestorOfClass(java.lang.Class, java.awt.Component) SwingUtilities.getAncestorOfClass
     */
    public static Component getAncestorOfClass(Class c, Component p) {
        while (p!=null) {
            if (c.isInstance(p)) {
                return p;
            }
            p = p.getParent();
        }
        return null;
    }

    /**
     * @see javax.swing.SwingUtilities#invokeLater(java.lang.Runnable) SwingUtilities.invokeLater
     */
    public static void invokeLater(Runnable runner) {
        Display.getDisplay(Midlet.getMidlet()).callSerially(runner);
    }

    /**
     * @see javax.swing.SwingUtilities#updateComponentTreeUI(java.awt.Component) SwingUtilities.updateComponentTreeUI
     */
    public static void updateComponentTreeUI(Component com) {

        if (com instanceof MenuBar) {
            Vector v = ((MenuBar) com).getItems();
            for (int c = 0; c < v.size(); c++) {
                ((Component)v.elementAt(c)).updateUI();
            }
        }
        if (com instanceof Panel) {
            Vector v = ((Panel) com).getComponents();
            for (int c = 0; c < v.size(); c++) {
                updateComponentTreeUI((Component) v.elementAt(c));
            }
        }
        com.updateUI();

    }

    private Hashtable UIManager;

    /**
     * @see javax.swing.UIManager#get(java.lang.Object) UIManager.get
     */
    public static Object get(Object key) {
        return getDesktopPane().UIManager.get(key);
    }

    /*
     * @see javax.swing.UIManager#put(java.lang.Object, java.lang.Object) UIManager.put
     */
    public static void put(Object key,Object value) {
        getDesktopPane().UIManager.put(key, value);
    }


    public static final boolean debug = false;

    public boolean SOFT_KEYS;
    public boolean VERY_BIG_SCREEN; // where the icon goes on a option pane,
    public boolean MAX_CLOSE_BUTTONS; // if by default a window has a close and max button at the top
    public boolean IPHONE_SCROLL;
    public boolean QWERTY_KAYPAD; // if true the multi letter per number is disabled
    public boolean USE_SOFT_KEY_CLEAR; // Nokia S40 does not have a dedicated delete key
    public boolean HIDDEN_MENU_AND_BACK; // on android and blackberry the menu and back are hardware buttons
    public boolean GRID_MENU; // on android

    // object variables
    protected Midlet midlet;
    private LookAndFeel theme;
    public int defaultSpace;
    private int menuHeight;
    final private Vector windows = new Vector();
    final private Object uiLock = windows;
    //private Window currentWindow;
    private ToolTip tooltip;
    private ToolTip indicator;
    private final Vector repaintComponent = new Vector();
    private final Vector revalidateComponents1 = new Vector();
    private final Vector revalidateComponents2 = new Vector();
    private final Vector revalidateComponents3 = new Vector();
    //private final Object revalidateLock = new Object();
    private Thread animationThread;

    // the nextAnimatedComponent can be == to animatedComponent
    // in this case, once the first animation is finished
    // it will start the animation off again
    private Component currentAnimatedComponent;
    private Component nextAnimatedComponent;
    private Image splash;
    private int background;
    private Icon fade;
    private boolean paintdone;
    private boolean fullrepaint;
    private boolean killflag;
    private boolean wideScreen;
    private boolean sideSoftKeys;
    private byte[] message;
    public int inaccuracy;

    // this is the currently focused component in the whole system
    // each window also has its own focused component
    private Component focusedComponent;

    /**
     * nothing should ever call serviceRepaints()
     * or repaint() in this class.
     * @param m The Midlet
     * @param back the background color
     * @param sph the splash screen image
     */
    public DesktopPane(Midlet m, int back, Image sph) {

        SOFT_KEYS = Midlet.getPlatform() != Midlet.PLATFORM_ME4SE && Midlet.getPlatform() != Midlet.PLATFORM_ANDROID && Midlet.getPlatform() != Midlet.PLATFORM_BLACKBERRY;
        HIDDEN_MENU_AND_BACK = Midlet.getPlatform() == Midlet.PLATFORM_ANDROID || Midlet.getPlatform() == Midlet.PLATFORM_BLACKBERRY;

        VERY_BIG_SCREEN = Midlet.getPlatform() == Midlet.PLATFORM_ME4SE;
        MAX_CLOSE_BUTTONS = Midlet.getPlatform() == Midlet.PLATFORM_ME4SE;
        IPHONE_SCROLL = Midlet.getPlatform() != Midlet.PLATFORM_ME4SE;

        QWERTY_KAYPAD = Midlet.getPlatform() == Midlet.PLATFORM_ME4SE || Midlet.getPlatform() == Midlet.PLATFORM_ANDROID || KeyEvent.BLACKBERRY_QWERTY;

        USE_SOFT_KEY_CLEAR = Midlet.getPlatform() == Midlet.PLATFORM_NOKIA_S40;
        GRID_MENU = Midlet.getPlatform() == Midlet.PLATFORM_ANDROID;

        desktop = this;

        background = back;
        splash = sph;

        setFullScreenMode(true);

        midlet = m;

        keypad = new KeyEvent(this);

        UIManager = new Hashtable();
        UIManager.put("clearText", "Clear");
        UIManager.put("selectText", "Select");
        UIManager.put("cancelText", "Cancel");
        UIManager.put("okText", "OK");
        UIManager.put("menuText", "Menu");

        UIManager.put("showText", "Show");
        UIManager.put("allText", "All");
        UIManager.put("newText", "New");

        UIManager.put("viewText", "View");
        UIManager.put("listText", "List");
        UIManager.put("gridText", "Grid");

        UIManager.put("backText", "Back");
        UIManager.put("closeText", "Close");
        UIManager.put("exitText", "Exit");

        UIManager.put("yesText", "Yes");
        UIManager.put("noText", "No");

        // the clipboard
        UIManager.put("cutText", "Cut");
        UIManager.put("copyText", "Copy");
        UIManager.put("pasteText", "Paste");
        UIManager.put("deleteText", "Delete");
        UIManager.put("selectAllText", "Select All");


        //      // check if we want to be in debug mode
        //      String s;
        //      if ((s = midlet.getAppProperty("Debug-Mode")) != null && ( s.toUpperCase().equals("OFF") || s.toUpperCase().equals("NO") || s.toUpperCase().equals("FALSE") || s.toUpperCase().equals("F") ) ) {
        //          DesktopPane.debugMode = false;
        //      }

        // ceate a new window to use as the main window
        //add( new Window() );

        // now we set this as the main display
        // the serviceRepaints will mean the repaint will be called
        // this will then kick of the run method of this class
        // and that will in tern call initialise of the midlet
        Display.getDisplay(m).setCurrent(this);
        repaint();

        // TODO me4se needs to be here, or keyboard events dont come in
        // WHY WHY WHY??!!! this is very strange
        // hacked in my me4se so this is not needed any more
        //if ( Midlet.getPlatform() == Midlet.PLATFORM_ME4SE ) {
        //    serviceRepaints();
        //}

    }

    public final void run() {

        try {
            midlet.initialize(this);
        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Error in initialize: " + th.toString());
            Logger.error(th);
        }

        // Set thread to maximum priority (smother animations and text input)
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        while (true) {
          try {

            if (killflag) {
                return;
            }

            synchronized (uiLock) {
                currentAnimatedComponent = nextAnimatedComponent;
                nextAnimatedComponent = null;

                if (currentAnimatedComponent == null) {
                    try {
                        uiLock.wait();
                    }
                    catch (InterruptedException e) {
                        Logger.info(e);
                    }
                    continue; // Go to while (true) again
                }

                try {
                    currentAnimatedComponent.animate();
                }
                catch (InterruptedException e) {
                    //#debug debug
                    Logger.debug("Interrupted during animation");
                }
            }
        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Error in animation: " + th.toString());
            Logger.error(th);
        }
      }
    }

    /**
     * This will call the animate() method on a component from the animation thread
     * @param com The Component to call animte() on
     */
    public void animateComponent(Component com) {
        synchronized(uiLock) {
            currentAnimatedComponent = null;
            nextAnimatedComponent = com;

            // in case this is called from initialize
            // like when a textfield is added to the main window at starttime
            if (Thread.currentThread() == animationThread) {
                return;
            }

            // does not work on N70
            //animationThread.interrupt();
            uiLock.notify();
        }
    }

    public void aniWait(Object component,int a) throws InterruptedException{
        uiLock.wait(a);
        if (currentAnimatedComponent != component) {
            throw new InterruptedException();
        }
    }

    // called by destroyApp

    void kill() {
        killflag = true;

        animateComponent(null);
    }

    /**
     * sets the default theme, and sets up default values if there are none set
     * @param a The Theme
     * @see javax.swing.UIManager#setLookAndFeel(javax.swing.LookAndFeel) UIManager.setLookAndFeel
     */
    public void setLookAndFeel(LookAndFeel a) {

        // TODO hack
        // this is just in case it has become somthing else
        // as we need the desktop to == to this one
        // as its the static desktop where components will get there
        // new theme from
        desktop = this;

        theme = a;

        // TODO find better way to do this
        // HACK to clear the background colors on subcomponents
        Style clear = new Style();
        theme.setStyleFor("WindowControlPanel", clear);
        theme.setStyleFor("TabList", clear);

        Style clear2 = theme.getStyle("ScrollPane");
        if (clear2==null) clear2 = theme.getStyle("");
        clear2 = new Style(clear2);
        clear2.addBackground(Style.NO_COLOR, Style.ALL);
        theme.setStyleFor("TabScroll", clear2);

        if (defaultSpace == 0) {
            int maxSize = Math.max(getWidth(), getHeight());
            defaultSpace = (maxSize <= 128) ? 3 : (maxSize <= 208) ? 5 : 7;
        }


        // this is only needed for if SOFT_KEYS is true,
        // but we may wish to set it to true after we set the look and feel
        MenuItemRenderer m = new MenuItemRenderer();
        m.setName("SoftkeyRenderer");
        softkeyRenderer = m;
        Component c = softkeyRenderer.getListCellRendererComponent(null, new Button("test"), 0, false, false);
        c.workoutSize();
        menuHeight = c.getHeightWithBorder();


        inaccuracy = theme.getStyle("").getFont(Style.ALL).getHeight();

        // this is a hack
        // to make sure that EVERYTHING on screen has a parent window and DesktopPane
        Window dummy;
        if (tooltip==null) {

            dummy = new Window();
            dummy.setDesktopPane(this);

            tooltip = new ToolTip();
            indicator = new ToolTip();

            dummy.add(tooltip);
            dummy.add(indicator);
        }
        else {
            // if we already have these components, we dont want them to lose there state
            dummy = tooltip.getWindow();
            updateComponentTreeUI(dummy);
        }

        fade = (Icon)getDefaultTheme(dummy).getProperty("dim", Style.ALL);

    }

    /**
     * This methods should ONLY be called from the updateUI() method in components
     * @see javax.swing.UIManager#getUI(javax.swing.JComponent) UIManager#getUI
     */
    public static Style getDefaultTheme(Component comp) {
        return getDefaultTheme(comp.getName());
    }

    public static Style getDefaultTheme(String compName) {
        Style style = desktop.theme.getStyle(compName);
        if (style == null) {
            style = desktop.theme.getStyle("");
        }
        return style;
    }

    /**
     * @see javax.swing.UIManager#getLookAndFeel() UIManager.getLookAndFeel
     */
    public LookAndFeel getLookAndFeel() {
        return theme;
    }

    private ListCellRenderer softkeyRenderer;

    public ListCellRenderer getSoftkeyRenderer() {
        return softkeyRenderer;
    }

    /**
     * this returns how big the area at the top and the bottom is that is
     * reserved for the softkeys, or 0 if there are no softkeys
     */
    public int getMenuHeight() {
        return SOFT_KEYS?menuHeight:0;
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== painting ============================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    private Graphics2D graphics;
    //#debug
    private String mem;

    /**
     * @param gtmp The Graphics object
     */
    protected void paint(Graphics gtmp) {

        // TODO: take copy of this first,
        // as if a component asks for a repaint in a different thread and that requires a fullrepaint,
        // it will only set the flag, but if a paint is in progress, the crop will not be correct
        // This problem shows up with hiding of a tooltip prematurely, as the repaint is called from the animation thread
        boolean doFullRepaint = fullrepaint;
        fullrepaint = false;

        if (!paintdone) {

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            if (background != Style.NO_COLOR) {
                gtmp.setColor(background);
                gtmp.fillRect(0, 0, getWidth(), getHeight());
            }

            if (splash != null) {
                gtmp.drawImage(splash, (getWidth() - splash.getWidth()) / 2, (getHeight() - splash.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
                splash = null;
            }
            else if (background != Style.NO_COLOR) {
                gtmp.setColor(0xFFFF0000); // red
                gtmp.drawString("yura.net mobile Loading...", 0, 0, Graphics.TOP | Graphics.LEFT);
            }

            oldw = getWidth();
            oldh = getHeight();

            // Initialize wideScreen for the first time
            wideScreen = (oldw > oldh);

            animationThread = new Thread(this, "SwingME-Animation");
            animationThread.start();

            paintdone = true;

            graphics = new Graphics2D();

            return;
        }

        try {

            int clipx = gtmp.getClipX();
            int clipy = gtmp.getClipY();
            int clipw = gtmp.getClipWidth();
            int cliph = gtmp.getClipHeight();

            // can not add or remove windows while this is happening
            synchronized(uiLock) {

                if (clipx<=0 && clipy<=0 && clipw>=getWidth() && cliph>=getHeight()) { // the system wants us to repaint everything
                    doFullRepaint = true;
                }
                else if (repaintComponent.isEmpty()) { // we want to repaint a component but have in a previous repaint cleared our list, so we do not know
                    doFullRepaint = true;
                }

                // now we need to layout all the components
                // we use a 3 pass system, as 3 passes is the maximum needed to layout even
                // the most complex layout with flexable components inside scrollpanes
                validating = 1;
                validateComponents(revalidateComponents1);
                validating = 2;
                validateComponents(revalidateComponents2);
                validating = 3;
                validateComponents(revalidateComponents3);
                validating = 0;

                // this needs to be done after all revalidates have finished
                // as revalidates can change what component can be focusable
                // also revalidate can change what components are visible,
                // and if they become hidden, they should not gain focus
                for (int c=0;c<revalidateComponents1.size();c++) {
                    Window w1 = ((Component)revalidateComponents1.elementAt(c)).getWindow();
                    if (w1!=null) {
                        w1.setupFocusedComponent();
                    }
                }
                revalidateComponents1.removeAllElements();
                revalidateComponents2.removeAllElements();
                revalidateComponents3.removeAllElements();

                // For thread safety, we cache the components to repaint
                // and as a component can call repaint from inside its paint method
                Vector repaintComponent2 = new Vector(repaintComponent.size());
                SystemUtil.addAll(repaintComponent2, repaintComponent);
                repaintComponent.removeAllElements();

                // For thread safety, we cache fullrepaint now, and clean it


//Logger.debug("PAINT fullrepaint="+doFullRepaint+" repaintComponent="+repaintComponent2+" x="+clipx+" y="+clipy+" w="+clipw+" h="+cliph);

                Window currentWindow = getSelectedFrame();

                // ALL focus changed events HAVE to happen after the revalidate and before the repaint!!!
                // if they dont, then a layout can not be valid and so they should not be made visible
                Component currentFocusedComponent=null;
                if (currentWindow != null) {
                    currentFocusedComponent = currentWindow.getMostRecentFocusOwner();
                }
                if (focusedComponent != currentFocusedComponent) {
                    if (focusedComponent != null) {
                        focusedComponent.focusLost();
                        focusedComponent=null;
                    }
                    if (currentFocusedComponent != null) {
                        focusedComponent = currentFocusedComponent;
                        focusedComponent.focusGained();
                    }
                }

                if (sizeChanged) {
                    if (currentWindow != null) {
                        //Component focusedComponent = currentWindow.getFocusOwner();
                        if (focusedComponent!=null) {
                            focusedComponent.makeVisible();
                        }
                    }
                    sizeChanged = false;
                }

                // now start painting
                graphics.setGraphics(gtmp);

                if (!doFullRepaint && !repaintComponent2.isEmpty()) {
                    for (int c = 0; c < repaintComponent2.size(); c++) {
                        if (((Component) repaintComponent2.elementAt(c)).getWindow() != currentWindow) {
                            doFullRepaint = true;
                            break;
                        }
                    }
                    if (!doFullRepaint) {
                        for (int c = 0; c < repaintComponent2.size(); c++) {
                            paintComponent(graphics, (Component) repaintComponent2.elementAt(c));
                        }
                    }
                }

                if (doFullRepaint) {
                    int startC = 0;
                    for (int c = windows.size()-1; c >= 0; c--) {
                        Object w = windows.elementAt(c);
                        if ( w instanceof Frame && ((Frame)w).isMaximum() ) {
                            startC = c;
                            break;
                        }
                    }
                    for (int c = startC; c < windows.size(); c++) {
                        //if (c==0) {
                        //    paintFirst(graphics);
                        //}
                        paintComponent(graphics, (Window) windows.elementAt(c));
                        if (c == (windows.size() - 2) && fade != null) {
                            Image i = fade.getImage();
                            if (i==null) {
                                // hack for android
                                fade.paintIcon(null, graphics, 0, 0);
                            }
                            else {
                                // hack for synth and MIDP
                                graphics.drawImage(i, 0, 0, fade.getIconWidth(), fade.getIconHeight(), 0, 0, getWidth(), getHeight() );
                            }
                        }
    /*
                        if (c == (windows.size() - 2) && fade != null) {
                            for (int x = 0; x < getWidth(); x += fade.getWidth()) {
                                for (int y = 0; y < getHeight(); y += fade.getHeight()) {
                                    graphics.drawImage(fade, x, y);
                                }
                            }
                        }
    */
                    }


                    if (tooltip!=null && tooltip.isShowing()) {
                        paintComponent(graphics, tooltip);
                    }
                    if (indicator!=null && indicator.isShowing()) {
                        paintComponent(graphics, indicator);
                    }

                }
            } // end synchronized

            //                if (!windows.isEmpty()) {
            //                    drawSoftkeys(graphics);
            //                }



            //paintLast(graphics);


            //gtmp.setColor( new java.util.Random().nextInt() );
            //gtmp.drawRect(clipx, clipy, clipw-1, cliph-1);


            //#mdebug
            if (mem != null) {

                javax.microedition.lcdui.Font font = gtmp.getFont();

                gtmp.setColor(0xFFFFFFFF);
                gtmp.fillRect((getWidth() - (font.stringWidth(mem) + 10)) / 2, 0, font.stringWidth(mem) + 10, font.getHeight() + 10);
                gtmp.setColor(0xFF000000);
                gtmp.drawString(mem, (getWidth() - (font.stringWidth(mem) + 10)) / 2 + 5, 5, Graphics.TOP | Graphics.LEFT);
            }
            //#enddebug

            if (message != null) {
                String m = new String(message);
                javax.microedition.lcdui.Font font = gtmp.getFont();

                gtmp.setColor(0xFF000000);
                gtmp.fillRect((getWidth() - (font.stringWidth(m) + 10)) / 2, (getHeight() - (font.getHeight() + 10)) / 2, font.stringWidth(m) + 10, font.getHeight() + 10);
                gtmp.setColor(0xFFFF0000);
                gtmp.drawString(m, (getWidth() - (font.stringWidth(m) + 10)) / 2 + 5, (getHeight() - (font.getHeight() + 10)) / 2 + 5, Graphics.TOP | Graphics.LEFT);
            }

            // there is a bug on blackberry that if it is processing a repaint and it is the last thing on the event queue
            // and in that repaint we call repaint again, the 2nd repaint will never get called, even though it does get put on the queue
            // we want the queue to have something more on it, so we put a empty task if we know we need another repaint
            if (Midlet.getPlatform()==Midlet.PLATFORM_BLACKBERRY && (fullrepaint || !repaintComponent.isEmpty())) {
                invokeLater(dummyThread);
            }

        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Error in paint: " + th.toString());
            Logger.error(th);
        }

    }
    static Thread dummyThread;
    static {
        if (Midlet.getPlatform()==Midlet.PLATFORM_BLACKBERRY) {
            dummyThread=new Thread();
        }
    }

    /**
     * this method should not really be used, please use the synth xml instead:
     * <pre>{@code
     *<synth>
     *    <style id="window">
     *      <imageIcon id="dimImage" path="/dim.png"/>
     *      <state>
     *        <property key="dim" value="dimImage"/>
     *      </state>
     *    </style>
     *    <bind style="window" type="region" key="Window"/>
     *</synth>
     * }</pre>
     */
    public void setDimImage(Icon a) {
        fade = a;
    }

    //public void paintFirst(Graphics2D g) {
    //}

    //public void paintLast(Graphics2D g) {
    //}

    private void paintComponent(Graphics2D g, Component com) {

        int[] a = g.getClip();

        if (com.getParent() != null) {
            com.getParent().clip(g);
        }

        int x = com.getXOnScreen();
        int y = com.getYOnScreen();

        g.translate(x, y);
        com.paint(g);
        g.translate(-x, -y);

        g.setClip(a);
    }

    private void validateComponents(Vector v) {

        // while we are going though the vector it can not be updated
            //synchronized (v) {

                    for (int c = 0; c < v.size(); c++) {
                        Component panel = (Component) v.elementAt(c);
                        panel.validate();
                    }
                    //v.removeAllElements();

            //}
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== Different ways of calling repaint ===================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    //public void repaint() {
    // cant do this
    //}
    //public void serviceRepaints() {
    // cant do this
    //}

    /**
     * this is called when you call repaint() on a component
     * @param rc The Component to repaint
     */
    public void repaintComponent(final Component rc) {

        Window myWindow = rc.getWindow();

        // if we are not in a window, do nothing
        if (myWindow==null || !rc.isShowing()) return;

        Component p=rc;

        while (p!=null) {
            if (!p.isOpaque()) {
                p = p.getParent();
            }
            else {
                break;
            }
        }

        synchronized(uiLock) {
            // if we have reached the nothingness, so everything is NOT Opaque
            if (p == null) {
                fullrepaint = true;
            }
            else {
                addToComponentVector(p, repaintComponent);
            }
        }
        Border insets = rc.getInsets();
        repaint(rc.getXOnScreen()-insets.getLeft(), rc.getYOnScreen()-insets.getTop(), rc.getWidthWithBorder(), rc.getHeightWithBorder());
    }

    public void repaintHole(final Component rc) {

        synchronized(uiLock) {
            fullrepaint = true;
        }
        Border insets = rc.getInsets();
        repaint(rc.getXOnScreen()-insets.getLeft(), rc.getYOnScreen()-insets.getTop(), rc.getWidthWithBorder(), rc.getHeightWithBorder());
    }

    private int validating=0;
    public void revalidateComponent(Component rc) {
        //#mdebug info
        if (rc.getWidth() == 0 || rc.getHeight() ==0 ) {
            Logger.info("revalidate called on a component with 0 width and 0 height");
            //dumpStack();
        }
        //#enddebug

        synchronized (uiLock) {
            addToComponentVector(rc, revalidateComponents1);
        }
    }

    public static void mySizeChanged(Component aThis) {

        Component p = getAncestorOfClass(ScrollPane.class, aThis);
        if (p==null) { p = aThis.getWindow(); }
        if (p==null) return;

        DesktopPane dp = aThis.getDesktopPane();
        // if a window is not yet visable it still needs the
        // mulipass validate system to work. e.g. DesktopPane#log() first time

        // if this method is being called from a thread other then the event thread
        // we dont want it to mess with whats currently happening in the paint
        synchronized(dp.uiLock) {

            // while it chooses what array to add the component to
            // the validating turn id can NOT be changed
            if (dp.validating==0) {
                addToComponentVector(p, dp.revalidateComponents1);
                p.repaint();
            }
            else if (dp.validating==1) {
    //#debug debug
    Logger.debug("thats some complex layout");
                addToComponentVector(p, dp.revalidateComponents2);
            }
            else if (dp.validating==2) {
    //#debug debug
    Logger.debug("thats some CRAZY SHIT COMPLEX LAYOUT");
                addToComponentVector(p, dp.revalidateComponents3);
            }
            //#mdebug info
            else {
                // if this happens it means that when i add a scrollbar it says it
                // does not need one, and as soon as i remove it, it says it does
                Logger.info("asking for revalidate 4th time: "+p);
                Logger.dumpStack();
            }
            //#enddebug
        }
    }

    private static void addToComponentVector(Component rc, Vector vec) {
        // Logger.debug("someone asking for repaint "+rc);

        if (rc.getWindow() == null) return;

        boolean found = false;

        // we should not make any other changes to this vector while we are searching
        // and adding to it
        //synchronized (vec) {

            // If we find the parent on the list, we don't need to add this one
            Component c1 = rc;
            while (c1 != null) {
                if (vec.contains(c1)) {
                    found = true;
                    break;
                }

                c1 = c1.getParent(); // recurse
            }

            // If component or its father, not found, add it
            if (!found) {
                vec.addElement(rc);

                // Search the list for children, otherwise will be repainted again
                for (int i = 0; i < vec.size(); i++) {
                    Component c2 = (Component) vec.elementAt(i);

                    // A component is children of rc, if one of its ancestors is rc
                    while (c2 != null) {
                        c2 = c2.getParent(); // recurse
                        if (c2 == rc) {
                            // Found a children, remove it
                            vec.removeElementAt(i);
                            i--;
                            break;
                        }
                    }
                }
            }

        //}

    }


    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== normal desktop calls == (can be called from any thread) =============
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    /**
     * @param w The window to add
     * @see java.awt.Container#add(java.awt.Component) Container.add
     */
    public void add(Window w) {
        // we dont want to add half way though a repaint, as it could be using this vector
        synchronized (uiLock) {
            if (w!=null && !windows.contains(w)) {
                w.setDesktopPane(this);
                windows.addElement(w);
                if (w instanceof Frame && ((Frame)w).isMaximum() ) {
                    ((Frame)w).setMaximum(true);
                }
                pointerComponent = null;

                windowChanged();
            }
            //#mdebug warn
            else {
                Logger.warn("trying to set a window visible when it already is visible or null: "+w);
                Logger.dumpStack();
            }
            //#enddebug
        }
        //if (w!=null) {
        //    if (fade==null) {
        //        w.repaint();
        //    }
        //    else {
                repaint(); // full is needed as softkeys may have changed and may be outside of window
        //    }
        //}
    }

    /**
     * @param w the window to close
     * @see java.awt.Container#remove(java.awt.Component) Container.remove
     */
    public void remove(Window w) {
        // dont want to change the windows Vector while we are painting
        synchronized (uiLock) {
            if (windows.contains(w)) {
                windows.removeElement(w);
                pointerComponent = null;

                windowChanged();
            }
            //#mdebug warn
            else {
                Logger.warn("cant remove, this window is not visible or null: " + w);
                Logger.dumpStack();
            }
            //#enddebug
        }
        //if (w!=null) {
        //    if (fade==null) {
        //        repaintHole(w);
        //    }
        //    else {
                repaint(); // full is needed as softkeys may have changed and may be outside of window
        //    }
        //}
    }

    /**
     * @param w the internal frame that's currently selected
     * @see javax.swing.JDesktopPane#setSelectedFrame(javax.swing.JInternalFrame) JDesktopPane.setSelectedFrame
     */
    public void setSelectedFrame(Window w) {
        // dont want to change the windows Vector while we are painting
        Window old=null;
        synchronized (uiLock) {
            if ( w != null && windows.contains(w) ) {
                old = getSelectedFrame();
                if (old == w) {
                    return;
                }
                windows.removeElement(w);
                windows.addElement(w);
                pointerComponent = null;

                windowChanged();
            }
            //#mdebug warn
            else {
                Logger.warn("cant setSelected, this window is not visible or null: " + w);
                Logger.dumpStack();
            }
            //#enddebug
        }

        //if (old!=null) {
        //    if (fade==null) {
        //        repaintHole(old);
        //    }
        //    else {
                repaint(); // full is needed as softkeys may have changed and may be outside of window
        //    }
        //}
        //if (w!=null) {
        //    if (fade==null) {
        //        w.repaint();
        //    }
        //    else {
        //        repaint();
        //    }
        //}

    }

    // this is here for android, when we move from 1 window to another, we want to close the keyboard
    private static void windowChanged() {
        TextComponent.closeNativeEditor();
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
        return windows.isEmpty()?null:(Window)windows.lastElement();
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== key commands ========================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    // we reuse this keyevent for all keyevents
    private KeyEvent keypad;

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

    private final int[] directions = new int[] {Canvas.RIGHT,Canvas.DOWN,Canvas.LEFT,Canvas.UP};
    private void passKeyEvent(KeyEvent keyevent) {
        //#debug
        notifyKeyListeners(keyevent);

        try {

            //#mdebug
            if (keyevent.isDownKey(Canvas.KEY_STAR)) {
//            	System.gc();
                mem = (Runtime.getRuntime().freeMemory() >> 10) + "K/" + (Runtime.getRuntime().totalMemory() >> 10) + "K";
                repaint();
            }
            else {
                mem = null;
            }
            //#enddebug


            if (keyevent.isDownKey(57) && keyevent.isDownKey(56) && keyevent.isDownKey(55) && keyevent.isDownKey(50)) {
                message = new byte[] {121,117,114,97,46,110,101,116};
                repaint();
            }
            else if (keyevent.isDownKey(52) && keyevent.isDownKey(50) && keyevent.isDownKey(54) && keyevent.isDownKey(51)) {
                message = new byte[] {84,72,69,32,71,65,77,69};
                repaint();
            }
            else {
                message = null;
            }

            Window currentWindow = getSelectedFrame();

            // we keep the current window seperate so that we know if a new window
            // has been opened and we need to reworkout the new focused component
            // and that NEEDs to be done after the valiating, and before the painting
            // but sometimes we can get a event after a new window has been opened
            // but before it has become the new window, and we dont even know what
            // component will be focused on this window
            if (currentWindow != null) {

                Button mneonicButton = null;
                int key = keyevent.getJustPressedKey();
                if (key != 0) {
                    if ( key==KeyEvent.KEY_MENU || key==KeyEvent.KEY_END ) {
                        if (Midlet.getPlatform() != Midlet.PLATFORM_NOKIA_S60) {
                            mneonicButton = currentWindow.findMneonicButton(key);
                            if ( mneonicButton==null ) {
                                if ( key==KeyEvent.KEY_MENU ) {
                                    key = KeyEvent.KEY_SOFTKEY1;
                                }
                                else if ( key==KeyEvent.KEY_END ) {
                                    key = KeyEvent.KEY_SOFTKEY2; // for sony-ericson, back is save as softkey 2
                                }
                                mneonicButton = currentWindow.findMneonicButton(key);
                            }
                        }
                        //else
                        // on S60 MENU and END should not do anything
                    }
                    else {
                        mneonicButton = currentWindow.findMneonicButton(key);
                        if ( mneonicButton==null && ( key==KeyEvent.KEY_SOFTKEY1 || key==KeyEvent.KEY_SOFTKEY2 ) ) {
                            if ( key==KeyEvent.KEY_SOFTKEY1 ) {
                                key = KeyEvent.KEY_MENU;
                            }
                            else if ( key==KeyEvent.KEY_SOFTKEY2 ) {
                                key = KeyEvent.KEY_END;
                            }
                            mneonicButton = currentWindow.findMneonicButton(key);
                        }
                    }
                }

                if (mneonicButton != null) {
                    mneonicButton.fireActionPerformed();
                }
                else {
                    //Component focusedComponent = currentWindow.getFocusOwner();

                    boolean consumed=false;

                    if (focusedComponent != null) {
                        consumed = focusedComponent.processKeyEvent(keyevent);
                    }
                    if (!consumed) {
                        consumed = currentWindow.processKeyEvent(keyevent);
                    }
                    if (!consumed) {
                        consumed = keyEvent(keyevent);
                    }
                    if (!consumed && keyevent.getJustReleasedKey() == 0) {
                        int d = -1;
                        for (int c=0;c<directions.length;c++) {
                            if (keyevent.isDownAction(directions[c])) {
                                d = directions[c];
                                break;
                            }
                        }
                        if (d!=-1) {
                            if (focusedComponent!=null) {
                                focusedComponent.transferFocus(d);
                            }
                            else {
                                currentWindow.passScrollUpDown(d);
                            }
                        }
                    }
                }
            }

            showHideToolTip(
                    keyevent.justReleasedAction(Canvas.RIGHT) ||
                    keyevent.justReleasedAction(Canvas.DOWN) ||
                    keyevent.justReleasedAction(Canvas.LEFT) ||
                    keyevent.justReleasedAction(Canvas.UP),
                    focusedComponent);

        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Error in KeyEvent: "+keyevent+" "+th.toString());
            Logger.error(th);
        }

    }

    // if no command listener is used key events fall though to this method
    // used for adding global shortcut keys
    public boolean keyEvent(KeyEvent kypd) {
        return false;
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== pointer commands ====================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    public static final int DRAGGED = 0;
    public static final int PRESSED = 1;
    public static final int RELEASED = 2;
    public static final int CANCEL = 3;

    private Component pointerComponent;
    private ScrollPane pointerScrollPane;
    private int pointerFristX;
    private int pointerFristY;
    private long pointerFristTime;

    public void pointerDragged(int x, int y) {
        pointerEvent(DRAGGED, x, y);
    }

    public void pointerPressed(int x, int y) {
        pointerEvent(PRESSED, x, y);
    }

    public void pointerReleased(int x, int y) {
        pointerEvent(RELEASED, x, y);
    }

    // ME4SE events
    public void pointerMoved(int x, int y) {
    }

    public void multitouchEvent(int[] type, int[] x, int[] y) {
        if (pointerComponent!=null) {
            int pcX = pointerComponent.getXOnScreen();
            int pcY = pointerComponent.getYOnScreen();
            for (int c=0;c<type.length;c++) {
                x[c] = x[c] - pcX;
                y[c] = y[c] - pcY;
            }
            pointerComponent.processMultitouchEvent(type,x,y);
        }
    }

    public boolean isAccurate(int oldx,int oldy,int x,int y) {
        return (Math.abs(oldx - x) <= inaccuracy && Math.abs(oldy - y) <= inaccuracy);
    }

    private void pointerEvent(int type, int x, int y) {
        try {

            Window currentWindow = getSelectedFrame();

            if (currentWindow != null) {

                // When pointer pressed, initialize pointer Component/ScrollPane
                if (type == PRESSED) {
                    pointerComponent = currentWindow.getComponentAt(x - currentWindow.getX(), y - currentWindow.getY());

                    if (IPHONE_SCROLL) {
                        pointerScrollPane = null;
                        ScrollPane sp = (ScrollPane)getAncestorOfClass(ScrollPane.class,pointerComponent);
                        if (sp != null) {
                            pointerScrollPane = sp;
                            // if the click was already on the scrollcomp, then we can clear the pointerComponent
                            if (pointerComponent == sp) {
                                pointerComponent = null;
                            }
                        }
                    }

                    pointerFristX = x;
                    pointerFristY = y;
                    pointerFristTime = System.currentTimeMillis();
                }

                // Start forwarding events to pointer ScrollPane?
                if (pointerComponent != null && type == DRAGGED && pointerScrollPane != null) {
                    if (pointerComponent.consumesMotionEvents()) {
                        pointerScrollPane = null;
                    }
                    // check its dragged more then 5px
                    else if (!isAccurate(pointerFristX, pointerFristY, x, y)) {
                        pointerComponent.processMouseEvent(CANCEL, x, y, keypad);
                        pointerComponent = null;
                    }
                }

                // Handle events for pointer component
                if (pointerComponent != null) {
                        int pcX = x - pointerComponent.getXOnScreen();
                        int pcY = y - pointerComponent.getYOnScreen();
                        pointerComponent.processMouseEvent(type, pcX, pcY, keypad);
                }

                // Handle events for pointer ScrollPane
                if (pointerScrollPane != null) {
                    if (type == PRESSED || type == RELEASED ||
                       (type == DRAGGED && pointerComponent == null)) {

                        int spX = x - pointerScrollPane.getXOnScreen();
                        int spY = y - pointerScrollPane.getYOnScreen();
                        pointerScrollPane.processMouseEvent(type, spX, spY, keypad);
                    }
                }

                //#mdebug
                // Simulate multi touch, when zero is pressed...
                if (keypad.isDownAction(Canvas.FIRE)) {
                    multitouchEvent(
                            new int[] {type, type},
                            new int[] {x, 2 * pointerFristX - x - 100},
                            new int[] {y, 2 * pointerFristY - y - 100});
                }
                //#enddebug

                // When pointer released, reset pointer Component/ScrollPane
                if (type == RELEASED) {

                	//	this is temporary because we do not want it in v1.4
//                    long time = System.currentTimeMillis();
//                    if (time - pointerFristTime > 1000 && isAccurate(pointerFristX, pointerFristY, x, y)) {
//                        if (pointerComponent!=null) {
//                            Window popup = pointerComponent.getPopupMenu();
//                            if (popup!=null && !popup.isVisible()) {
//                                popup.show(pointerComponent, x, y);
//                            }
//                        }
//                    }

                    pointerScrollPane = null;
                    pointerComponent = null;

                }
            }

            // TODO: if dragged by only a little bit, should not hide the tooltip
            showHideToolTip(type == PRESSED,pointerComponent);

        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Exception in pointerEvent: " + th.toString());
            Logger.error(th);
        }

    }

    private void showHideToolTip(boolean show,Component comp) {

        // if a tooltip should be setup
        if (show && comp != null && comp.getToolTipText() != null) {

            tooltip.setText(comp.getToolTipText());
            tooltip.workoutSize();
            int x = comp.getToolTipLocationX() + comp.getXOnScreen();
            int y = comp.getToolTipLocationY() + comp.getYOnScreen();
            int w = tooltip.getWidthWithBorder();
            int h = tooltip.getHeightWithBorder();
            Border offset = tooltip.getBorder();
            int top = offset == null ? 0 : offset.getTop();
            int left = offset == null ? 0 : offset.getLeft();

            if (x - left < 0) {
                x = left;
            }
            else if (x - left + w > getWidth()) {
                x = getWidth() - w + left;
            }

            if (y - top < 0) {
                y = top;
            }
            else if (y - top + h > getHeight()) {
                y = getHeight() - h + top;
            }

            tooltip.setLocation(x, y);
            animateComponent(tooltip);

        }
        else if (tooltip != null) {
            // this will never be null unless this method is called
            // before the midlet is initialised, and this can happen

            // if there is a tooltip up or ready to go up,
            // then kill it!
            synchronized (uiLock) {
                if ((tooltip.isWaiting() && nextAnimatedComponent == null) || nextAnimatedComponent == tooltip) {
                    animateComponent(null);
                }
            }
        }


    }

    public void setIndicatorText(String txt) {
        // clear the old srea where the indicator was
        if (indicator.isShowing()) {
            repaintHole(indicator);
        }
        indicator.setText(txt);
        indicator.workoutSize();
        indicator.setShowing( txt != null && !QWERTY_KAYPAD );
        setupIndicatorPosition();
        // as we dont know what size it was
        indicator.repaint();
    }

    private void setupIndicatorPosition() {
        if (indicator != null) {
            int w = indicator.getWidthWithBorder();
            int h = indicator.getHeightWithBorder();
            if (sideSoftKeys) {
                indicator.setBoundsWithBorder(0, getHeight() - h, w, h);
            }
            else {
                indicator.setBoundsWithBorder( (Midlet.getPlatform() == Midlet.PLATFORM_SONY_ERICSSON) ?0:(getWidth()-w), 0, w, h);
            }
        }
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== other events from the canvas ========================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    private int oldw,  oldh;

    protected void sizeChanged(int w, int h) {
        //#debug debug
        Logger.debug("sizeChanged!! " + paintdone + " w=" + w + " h=" + h);

        sizeChangedImpl();
        repaint();
    }

    private void sizeChangedImpl() {

        int w = super.getWidth();
        int h = super.getHeight();

        if (oldw == w && oldh == h) {
            // Noting to do. Just return;
            return;
        }

        boolean old = wideScreen;
        wideScreen = (w > h);

        // this means we NEED to flip from 1 orientation to another
        if (old != wideScreen && oldw == h && oldh == w) {
            sideSoftKeys = wideScreen;
        }

        // if we are animating a menu up, kill the animation!
        if (currentAnimatedComponent instanceof Menu) {
            animateComponent(null);
        }


        Vector win = getAllFrames();

        for (int c = 0; c < win.size(); c++) {
            Window window = (Window)win.elementAt(c);

            if (window != null) {// can it be null????

                if (window instanceof Frame && ((Frame) window).isMaximum()) {
                    // if it was max, then make it max again
                    ((Frame) window).setMaximum(true);
                }
                else {

                    // push the window onto the screen in case it has gone of the screen
                    window.makeVisible();

                    // if our window is set to snap to some sides of the scrren, lets make sure it does
                    if (window.snap!=0) {

                        Menu.setupSize(window);

                        boolean left = (window.snap & Graphics.LEFT)!=0;
                        boolean top = (window.snap & Graphics.TOP)!=0;
                        boolean right = (window.snap & Graphics.RIGHT)!=0;
                        boolean bottom = (window.snap & Graphics.BOTTOM)!=0;

                        Border insets = window.getInsets();

                        if (top && bottom) {
                            window.setBoundsWithBorder(window.getXWithBorder(),0, window.getWidthWithBorder(),h);
                        }
                        else if (top || bottom) {
                            window.setLocation(window.getX(), top?insets.getTop():h-window.getHeight()-insets.getBottom() );
                        }

                        if (left && right) {
                            window.setBoundsWithBorder(0, window.getYWithBorder(), w, window.getHeightWithBorder());
                        }
                        else if (left || right) {
                            window.setLocation(left?insets.getLeft():w-window.getWidth()-insets.getRight(), window.getY() );
                        }
                    }
                }
            }
        }
        oldw = w;
        oldh = h;

        // we want to do this later, as on me4se mode we want
        // the size to be setup before we have any paints called
        if (!paintdone) {
            return;
        }
        // Until we don't do the initial setup, ignore this.
        setupIndicatorPosition();

        sizeChanged = true;
        pointerComponent = null;
        pointerScrollPane = null;

    }
    boolean sizeChanged;

    // this is to fix buttons not being released properly on some phones
    public void showNotify() {

        //#debug debug
        Logger.debug("showNotify");

        desktop = this;
        keypad.clear();

        // A landscape change can happens when we are hidden. So check it now.
        sizeChangedImpl();

        repaint();

    }

    protected void hideNotify() {
        //#debug debug
        Logger.debug("hideNotify");
        keypad.clear();
    }

    public boolean isSideSoftKeys() {
        return sideSoftKeys;
    }

    //#mdebug debug
    public String toString() {
        return "DesktopPane"+windows;
    }
    //#enddebug
}
