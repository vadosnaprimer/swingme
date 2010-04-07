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
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.ToolTip;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.logging.DesktopLogger;
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
     * This methods should ONLY be called from the updateUI() method in components
     * @see javax.swing.UIManager#getUI(javax.swing.JComponent) UIManager#getUI
     */
    public static Style getDefaultTheme(Component comp) {
        Style style = desktop.theme.getStyle(comp.getName());
        if (style == null) {
            style = desktop.theme.getStyle("");
        }
        return style;
    }

    public static void mySizeChanged(Component aThis) {

        Component p = aThis.getParent();
        if (p==null) return;
        while (!(p instanceof ScrollPane)) {
            Component pp = p.getParent();
            if (pp==null) {
                break;
            }
            p=pp;
        }

        DesktopPane dp = aThis.getWindow().getDesktopPane();
        // if a window is not yet visable it still needs the
        // mulipass validate system to work. e.g. DesktopPane#log() first time
        if (dp==null) { dp = getDesktopPane(); }

        // if this method is being called from a thread other then the event thread
        // we dont want it to mess with whats currently happening in the paint
        synchronized(dp.repaintComponent) {

            // while it chooses what array to add the component to
            // the validating turn id can NOT be changed
            if (dp.validating==0) {
                addToComponentVector(p, dp.revalidateComponents1);
                p.repaint();
            }
            else if (dp.validating==1) {
    //#debug
    Logger.debug("thats some complex layout");
                addToComponentVector(p, dp.revalidateComponents2);
            }
            else if (dp.validating==2) {
    //#debug
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
    public static final boolean debug = false;

    // 2 things that are not related to softkeys also depend on this flag
    // where the icon goes on a option pane,
    // and if by default a window has a close and max button at the top
    public boolean NO_SOFT_KEYS;

    // object variables
    protected Midlet midlet;
    private LookAndFeel theme;
    public int defaultSpace;
    private int menuHeight;
    final private Vector windows = new Vector();
    private Window currentWindow;
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
    private Image fade;
    private boolean paintdone;
    private boolean fullrepaint;
    private boolean killflag;
    private boolean wideScreen;
    private boolean sideSoftKeys;
    private byte[] message;

    /**
     * nothing should ever call serviceRepaints()
     * or repaint() in this class.
     * @param m The Midlet
     * @param back the background color
     * @param sph the splash screen image
     */
    public DesktopPane(Midlet m, int back, Image sph) {

        NO_SOFT_KEYS = (Midlet.getPlatform() == Midlet.PLATFORM_ME4SE);

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
        if (NO_SOFT_KEYS) {
            serviceRepaints();
        }

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

    public Component getAnimatedComponent() {
        return currentAnimatedComponent;
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

            synchronized (this) {
                currentAnimatedComponent = nextAnimatedComponent;
                nextAnimatedComponent = null;

                if (currentAnimatedComponent == null) {
                    try {
                        wait();
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
    public synchronized void animateComponent(Component com) {
        currentAnimatedComponent = null;
        nextAnimatedComponent = com;

        // in case this is called from initialize
        // like when a textfield is added to the main window at starttime
        if (Thread.currentThread() == animationThread) {
            return;
        }

        // does not work on N70
        //animationThread.interrupt();
        notify();
    }
    // called by destroyApp

    void kill() {
        killflag = true;

        animateComponent(null);
    }

    /**
     * sets the default theme, and sets up default values if there are none set
     * @param a The Theme
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
        clear2.addBackground(-1, Style.ALL);
        theme.setStyleFor("TabScroll", clear2);

        if (defaultSpace == 0) {

            int maxSize = Math.max(getWidth(), getHeight());

            defaultSpace = (maxSize <= 128) ? 3 : (maxSize <= 208) ? 5 : 7;
        }

        MenuItemRenderer m = new MenuItemRenderer();
        m.setName("SoftkeyRenderer");
        softkeyRenderer = m;

        menuHeight = DefaultListCellRenderer.setPrototypeCellValue(new Button("test"), softkeyRenderer);

        // this is a hack
        // to make sure that EVERYTHING on screen has a parent window and DesktopPane
        Window dummy = new Window();
        dummy.setDesktopPane(this);

        tooltip = new ToolTip();
        indicator = new ToolTip();

        dummy.add(tooltip);
        dummy.add(indicator);
    //currentWindow.setSize(getWidth(),getHeight());
    }

    public LookAndFeel getLookAndFeel() {
        return theme;
    }

    private ListCellRenderer softkeyRenderer;

    public ListCellRenderer getSoftkeyRenderer() {
        return softkeyRenderer;
    }

    public int getMenuHeight() {
        return menuHeight;
    }

    /**
     * @see javax.swing.SwingUtilities#invokeLater(java.lang.Runnable) SwingUtilities.invokeLater
     */
    public static void invokeLater(Runnable runner) {
        Display.getDisplay(Midlet.getMidlet()).callSerially(runner);
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== painting ============================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    //public void repaint() {
    // cant do this
    //}
    //public void serviceRepaints() {
    // cant do this
    //}
    private Graphics2D graphics;
    private Vector repaintComponent2 = new Vector();
    //#debug
    private String mem;


    /**
     * @param gtmp The Graphics object
     */
    protected void paint(Graphics gtmp) {

        //Logger.debug("CANVAS PAINT!!!  fullrepaint="+fullrepaint+" repaintComponent="+repaintComponent);

        if (!paintdone) {

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            if (background != -1) {
                gtmp.setColor(background);
                gtmp.fillRect(0, 0, getWidth(), getHeight());
            }

            if (splash != null) {
                gtmp.drawImage(splash, (getWidth() - splash.getWidth()) / 2, (getHeight() - splash.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
                splash = null;
            }
            else if (background != -1) {
                gtmp.setColor(0x00FF0000);
                gtmp.drawString("yura.net mobile Loading...", 0, 0, Graphics.TOP | Graphics.LEFT);
            }

            oldw = getWidth();
            oldh = getHeight();

            // Initialize wideScreen for the first time
            wideScreen = (oldw > oldh);

            animationThread = new Thread(this);
            animationThread.start();

            paintdone = true;

            graphics = new Graphics2D();

            return;
        }

        try {

            // can not add or remove windows while this is happening
            synchronized(windows) {

                boolean doFullRepaint;

                // if we have started revalidating, we do not want any random reval and repaint happening
                // as if they do happen half way though, we would get a component being repainted
                // even though it did not get revalidated
                synchronized (repaintComponent) {

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

                    // For thread safety, we cache the components to repaint
                    SystemUtil.addAll(repaintComponent2, repaintComponent);
                    repaintComponent.removeAllElements();

                    // For thread safety, we cache fullrepaint now, and clean it
                    doFullRepaint = fullrepaint;
                    fullrepaint = false;

                }

                Window newWindow = windows.size()==0?null:(Window)windows.lastElement();

                if (newWindow!=currentWindow) {
                    if (currentWindow != null) {
                        Component focusedComponent = currentWindow.getFocusOwner();
                        if (focusedComponent != null) {
                            focusedComponent.focusLost();
                        }
                    }
                    currentWindow = newWindow;
                    if (currentWindow != null) {
                        // this needs to be done after all revalidates have finished
                        // as revalidates can change what component can be focusable
                        Component focusedComponent = newWindow.getMostRecentFocusOwner();
                        if (focusedComponent != null) {
                            // even though MOST focusGained call a repaint
                            // we can NOT allow a repaint here to actually be added
                            // to the list of components to be repainted as then
                            // any thred could repaint at this point
                            // and the layout may not be valid yet for that component.
                            // e.g. If focusGained calls revalidate on its parent too
                            // that will not get done and yet it will still paint
                            focusedComponent.focusGained();
                        }
                    }
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

                repaintComponent2.removeAllElements();

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
                        if (c==0) {
                            paintFirst(graphics);
                        }
                        paintComponent(graphics, (Window) windows.elementAt(c));
                        if (c == (windows.size() - 2) && fade != null) {
                            graphics.drawImage(fade, 0, 0, fade.getWidth(), fade.getHeight(), 0, 0, getWidth(), getHeight(), javax.microedition.lcdui.game.Sprite.TRANS_NONE );
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
                }
            }

            //                if (!windows.isEmpty()) {
            //                    drawSoftkeys(graphics);
            //                }

            if (tooltip!=null && tooltip.isShowing()) {
                paintComponent(graphics, tooltip);
            }
            if (indicator!=null && indicator.getText() != null && !NO_SOFT_KEYS) {
                paintComponent(graphics, indicator);
            }

            paintLast(graphics);

            //#mdebug
            if (mem != null) {

                javax.microedition.lcdui.Font font = gtmp.getFont();

                gtmp.setColor(0x00FFFFFF);
                gtmp.fillRect((getWidth() - (font.stringWidth(mem) + 10)) / 2, 0, font.stringWidth(mem) + 10, font.getHeight() + 10);
                gtmp.setColor(0x00000000);
                gtmp.drawString(mem, (getWidth() - (font.stringWidth(mem) + 10)) / 2 + 5, 5, Graphics.TOP | Graphics.LEFT);
            }
            //#enddebug

            if (message != null) {
                String m = new String(message);
                javax.microedition.lcdui.Font font = gtmp.getFont();

                gtmp.setColor(0x00000000);
                gtmp.fillRect((getWidth() - (font.stringWidth(m) + 10)) / 2, (getHeight() - (font.getHeight() + 10)) / 2, font.stringWidth(m) + 10, font.getHeight() + 10);
                gtmp.setColor(0x00FF0000);
                gtmp.drawString(m, (getWidth() - (font.stringWidth(m) + 10)) / 2 + 5, (getHeight() - (font.getHeight() + 10)) / 2 + 5, Graphics.TOP | Graphics.LEFT);
            }

        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Error in paint: " + th.toString());
            Logger.error(th);
        }

    }

    public void setDimImage(Image a) {
        fade = a;
    }

    public void paintFirst(Graphics2D g) {
    }

    public void paintLast(Graphics2D g) {
    }

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
                    v.removeAllElements();

            //}
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== Different ways of calling repaint ===================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    /**
     * this is called when you call repaint() on a component
     * @param rc The Component to repaint
     */
    public void repaintComponent(Component rc) {
        synchronized(repaintComponent) {
            addToComponentVector(rc, repaintComponent);
        }
        repaint();
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
        Logger.debug("FULL REPAINT!!! this method should NOT normally be called");

        synchronized(repaintComponent) {
            fullrepaint = true;
        }

        repaint();
    }

    /**
     * Repaint the softkeybar
     */
    public void softkeyRepaint() {
        repaint();
    }

    private int validating=0;
    public void revalidateComponent(Component rc) {
        //#mdebug info
        if (rc.getWidth() == 0 || rc.getHeight() ==0 ) {
            Logger.info("revalidate called on a component with 0 width and 0 height");
            //dumpStack();
        }
        //#enddebug

        synchronized (repaintComponent) {
            addToComponentVector(rc, revalidateComponents1);
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
        synchronized (windows) {
            if (w!=null && !windows.contains(w)) {
                w.setDesktopPane(this);
                windows.addElement(w);
                if (w instanceof Frame && ((Frame)w).isMaximum() ) {
                    ((Frame)w).setMaximum(true);
                }
                pointerComponent = null;
            }
            //#mdebug warn
            else {
                Logger.warn("trying to set a window visible when it already is visible or null: "+w);
                Logger.dumpStack();
            }
            //#enddebug
        }
        fullRepaint();
    }

    /**
     * @param w the window to close
     * @see java.awt.Container#remove(java.awt.Component) Container.remove
     */
    public void remove(Window w) {
        // dont want to change the windows Vector while we are painting
        synchronized (windows) {
            if (windows.contains(w)) {
                windows.removeElement(w);
                pointerComponent = null;
            }
            //#mdebug warn
            else {
                Logger.warn("cant remove, this window is not visible: " + w);
                Logger.dumpStack();
            }
            //#enddebug
        }
        fullRepaint();
    }

    /**
     * @param w the internal frame that's currently selected
     * @see javax.swing.JDesktopPane#setSelectedFrame(javax.swing.JInternalFrame) JDesktopPane.setSelectedFrame
     */
    public void setSelectedFrame(Window w) {
        // dont want to change the windows Vector while we are painting
        synchronized (windows) {
            if ( w != null && windows.contains(w) ) {
                if (currentWindow == w) {
                    return;
                }
                windows.removeElement(w);
                windows.addElement(w);
                pointerComponent = null;
            }
            //#mdebug warn
            else {
                Logger.warn("cant setSelected, this window is not visible or null: " + w);
                Logger.dumpStack();
            }
            //#enddebug
        }
        fullRepaint();
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
                mem = (Runtime.getRuntime().freeMemory() >> 10) + "K/" + (Runtime.getRuntime().totalMemory() >> 10) + "K";
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

            // we keep the current window seperate so that we know if a new window
            // has been opened and we need to reworkout the new focused component
            // and that NEEDs to be done after the valiating, and before the painting
            // but sometimes we can get a event after a new window has been opened
            // but before it has become the new window, and we dont even know what
            // component will be focused on this window
            if (currentWindow != null && currentWindow == windows.lastElement()) {

                Button mneonicButton = null;
                int key = keyevent.getJustPressedKey();
                if (key != 0) {
                    if ( key==KeyEvent.KEY_END && (Midlet.getPlatform() == Midlet.PLATFORM_SONY_ERICSSON) ) { // for sony-ericson, back is save as softkey 2
                        key = KeyEvent.KEY_SOFTKEY2;
                    }
                    mneonicButton = currentWindow.findMneonicButton(key);
                }
                Component focusedComponent = currentWindow.getFocusOwner();

                if (mneonicButton != null) {
                    mneonicButton.fireActionPerformed();
                }
                else {
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
        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Error in KeyEvent: " + th.toString());
            Logger.error(th);
        }

        showHideToolTip(
                keyevent.justReleasedAction(Canvas.RIGHT) ||
                keyevent.justReleasedAction(Canvas.DOWN) ||
                keyevent.justReleasedAction(Canvas.LEFT) ||
                keyevent.justReleasedAction(Canvas.UP));


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
    private Component pointerComponent;
    private ScrollPane pointerScrollPane;
    private int pointerFristX;
    private int pointerFristY;


    public void pointerDragged(int x, int y) {
        pointerEvent(DRAGGED, x, y);
    }

    public void pointerPressed(int x, int y) {
        pointerEvent(PRESSED, x, y);
    }

    public void pointerReleased(int x, int y) {
        pointerEvent(RELEASED, x, y);
    }

    private ScrollPane getScrollPaneParent(Component comp) {
        Component parent = comp;
        while (parent != null) {
            if (parent instanceof ScrollPane) {
                return (ScrollPane) parent;
            }
            parent = parent.getParent();
        }

        return null;
    }

    private void pointerEvent(int type, int x, int y) {
        try {
            if (currentWindow != null && currentWindow == windows.lastElement()) {

                // When pointer pressed, initialize pointer Component/ScrollPane
                if (type == PRESSED) {
                    pointerComponent = currentWindow.getComponentAt(x - currentWindow.getX(), y - currentWindow.getY());

                    ScrollPane sp = getScrollPaneParent(pointerComponent);
                    if (sp != null) {
                        if (sp.getViewPortHeight() < sp.getView().getHeight() ||
                            sp.getViewPortWidth() < sp.getView().getWidth()) {

                            pointerScrollPane = sp;

                            if (pointerComponent == sp) {
                                pointerComponent = null;
                            }
                        }
                    }

                    pointerFristX = x;
                    pointerFristY = y;
                }

                // Start forwarding events to pointer ScrollPane?
                if (pointerComponent != null) {
                    if (type == DRAGGED && pointerScrollPane != null) {
                        // check its dragged more then 5px
                        if (Math.abs(pointerFristX - x) > 5 ||
                            Math.abs(pointerFristY - y) > 5) {

                            pointerComponent = null;
                        }
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

                // When pointer released, reset pointer Component/ScrollPane
                if (type == RELEASED) {
                    pointerScrollPane = null;
                    pointerComponent = null;
                }
            }
        }
        catch (Throwable th) {
            //#debug warn
            Logger.warn("Exception in pointerEvent: " + th.toString());
            Logger.error(th);
        }

        // if dragged by only a little bit, should not hide the tooltip

        showHideToolTip(type == PRESSED);
    }

    private void showHideToolTip(boolean show) {

        Component focusedComponent;

        // if a tooltip should be setup
        if (show && currentWindow != null && (focusedComponent = currentWindow.getFocusOwner()) != null && focusedComponent.getToolTipText() != null) {

            tooltip.setText(focusedComponent.getToolTipText());
            tooltip.workoutSize();
            int x = focusedComponent.getToolTipLocationX() + focusedComponent.getXOnScreen();
            int y = focusedComponent.getToolTipLocationY() + focusedComponent.getYOnScreen();
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
            synchronized (this) {
                if ((tooltip.isWaiting() && nextAnimatedComponent == null) || nextAnimatedComponent == tooltip) {
                    animateComponent(null);
                }
            }
        }


    }

    public void setIndicatorText(String txt) {
        indicator.setText(txt);
        indicator.workoutSize();

        setupIndicatorPosition();

        // as we dont know what size it was
        fullRepaint();

    }

    private void setupIndicatorPosition() {

        int w = indicator.getWidthWithBorder();
        int h = indicator.getHeightWithBorder();
        if (sideSoftKeys) {
            indicator.setBoundsWithBorder(0, getHeight() - h, w, h);
        }
        else {
            indicator.setBoundsWithBorder( (Midlet.getPlatform() == Midlet.PLATFORM_SONY_ERICSSON) ?0:(getWidth()-w), 0, w, h);
        }

    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== other events from the canvas ========================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    private int oldw,  oldh;

    protected void sizeChanged(int w, int h) {
        //#debug
        Logger.debug("sizeChanged!! " + paintdone + " w=" + w + " h=" + h);

        sizeChangedImpl();
        fullRepaint();
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

        Vector win = getAllFrames();

        for (int c = 0; c < win.size(); c++) {
            Window window = (Window)win.elementAt(c);

            // TODO RESIZE better

            // when the scren switches from 1 resolution to another, and the 'hidden' menubar is not repositioned,
            // it may cause it to stop being painted at all, as it may bcome totally off the screen

            if (window != null) {
                if (window instanceof Frame && ((Frame) window).isMaximum()) {
                    ((Frame) window).setMaximum(true);
                }
            //window.setBounds(window.getY(),window.getX(),window.getHeight(), window.getWidth());
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
    }

    // this is to fix buttons not being released properly on some phones
    public void showNotify() {

        desktop = this;

        //Logger.debug("showNotify");
        keypad.clear();

        // A landscape change can happens when we are hidden. So check it now.
        sizeChangedImpl();

        fullRepaint();

    }

    protected void hideNotify() {
        //Logger.debug("hideNotify");
        keypad.clear();
    }

    public boolean isSideSoftKeys() {
        return sideSoftKeys;
    }
}
