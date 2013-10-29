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

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;

import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JComponent
 */
public abstract class Component {

    protected int posX,posY,width,height;
    protected boolean focusable;

    protected Component parent;
    private String name;

    /**
     * @see javax.swing.JComponent#ui JComponent.ui
     */
    protected Style theme;

    protected int background=Style.NO_COLOR;
    protected int foreground=Style.NO_COLOR;
    private Border border;

    private String tooltip;
    private boolean visible;

    protected int preferredWidth=-1;
    protected int preferredHeight=-1;

    /**
     * same value as in Swing
     * @see java.awt.event.FocusEvent#FOCUS_GAINED FocusEvent.FOCUS_GAINED
     */
    public static final int FOCUS_GAINED = 1004;
    /**
     * same value as in Swing
     * @see java.awt.event.FocusEvent#FOCUS_LOST FocusEvent.FOCUS_LOST
     */
    public static final int FOCUS_LOST = 1005;

    private ChangeListener focusListener;

    protected Window popup;

    /**
     * @see javax.swing.JComponent#JComponent() JComponent.JComponent
     */
    public Component() {
            focusable = true;
            visible = true;
            updateUI();
    }

    /**
     * @see java.awt.Component#isVisible() Component.isVisible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @see java.awt.Component#setVisible(boolean) Component.setVisible
     */
    public void setVisible(boolean v) {
        visible = v;
        // should not need to do this, as we need to revalidate
        // after we change visability anyway
        // having this here causes problems as some components need to be revalidated
        // first before they if they can have focus or not
        // and we always will revalidate after we change some visibility
        //Window w = getWindow();
        //if (w!=null) {
        //    w.setupFocusedComponent();
        //}
    }

    /**
     * @see java.awt.Component#addFocusListener(java.awt.event.FocusListener) Component.addFocusListener
     */
    public void addFocusListener(ChangeListener lis) {
        //#mdebug warn
        if (focusListener!=null) {
            Logger.warn("trying to add a FocusListener when there is already one registered");
            Logger.dumpStack();
        }
        if (lis==null) {
            Logger.warn("trying to add a null FocusListener");
            Logger.dumpStack();
        }
        //#enddebug
        focusListener = lis;
    }

    /**
     * @see java.awt.Component#removeFocusListener(java.awt.event.FocusListener) Component.removeFocusListener
     */
    public void removeFocusListener(ChangeListener lis) {
    	if (lis == focusListener) {
    		focusListener = null;
    	}
        //#mdebug warn
        else {
            Logger.warn("trying to remove a FocusListener that is not registered");
            Logger.dumpStack();
        }
        if (lis==null) {
            Logger.warn("trying to remove a null FocusListener");
            Logger.dumpStack();
        }
        //#enddebug
    }

    /**
     * @see java.awt.Component#getFocusListeners() Component.getFocusListeners
     */
    public ChangeListener[] getFocusListeners() {
        return focusListener==null?new ChangeListener[0]:new ChangeListener[] { focusListener };
    }

    /**
     * @param w The preferred Width (can be -1 for no preference)
     * @param h The preferred Height (can be -1 for no preference)
     * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension) JComponent.setPreferredSize
     */
    public void setPreferredSize(int w, int h) {
        preferredWidth = w;
        preferredHeight = h;
    }
    /**
     * @see javax.swing.JComponent#getPreferredSize() JComponent.getPreferredSize
     */
    public int getPreferredWidth() {
            return preferredWidth;
    }
    /**
     * @see javax.swing.JComponent#getPreferredSize() JComponent.getPreferredSize
     */
    public int getPreferredHeight() {
            return preferredHeight;
    }

    /**
     * @param n The new name for this panel
     * @see java.awt.Component#setName(java.lang.String) Component.setName
     * @see TabbedPane#add(net.yura.mobile.gui.components.Component)
     */
    public void setName(String n) {
            name  = n;
            updateUI();
    }

    /**
     * @return The name of the panel
     * @see java.awt.Component#getName() Component.getName
     * @see TabbedPane#add(net.yura.mobile.gui.components.Component)
     */
    public String getName() {
        return name==null?getDefaultName():name;
    }

    protected abstract String getDefaultName();

    /**
     * @see java.awt.Component#isFocusOwner() Component.isFocusOwner
     */
    public boolean isFocusOwner() {
        Window myWindow = getWindow();
        return myWindow !=null && myWindow.isFocused() && myWindow.getFocusOwner() == this;
    }

    /**
     * @see java.awt.Component#requestFocusInWindow()
     */
    public void requestFocusInWindow() {
        Window w = getWindow();
        if (w!=null) {
            w.setFocusedComponent(this);
        }
    }

    /**
     * @see java.awt.Component#isFocusable() Component.isFocusable
     */
    public boolean isFocusable() {
        if (!isVisible()) return false;
            return focusable;
    }

    /**
     * @see java.awt.Component#setFocusable(boolean) Component.setFocusable
     */
    public void setFocusable(boolean selectable) {
        boolean old = focusable;
        this.focusable = selectable;
        Window w = getWindow();
        if (w!=null) {
            w.setupFocusedComponent();
        }
        if (old != focusable) {
            repaint();
        }
    }

    /**
     * This method WILL use small scroll, so if the component is too far then it wont gain focus
     * @param direction can be {@link Canvas#DOWN} {@link Canvas#UP} {@link Canvas#LEFT} or {@link Canvas#RIGHT}
     * @see java.awt.Component#transferFocus() Component.transferFocus
     * @see java.awt.Component#transferFocusBackward() Component.transferFocusBackward
     */
    public void transferFocus(int direction) {
        ((Panel)parent).breakOutAction(this, direction, true,false);
    }

    /**
     * @param component the component to be added
     * @see java.awt.Container#add(java.awt.Component) Container.add
     */
    public void add(Component component){
            addImpl(component,null,-1);
    }

    public void add(Component component,int constraint){
        addImpl(component,new Integer(constraint),-1);
    }

    /**
     * @param component
     * @param constraint
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object) Container.add
     */
    public void add(Component component,Object constraint){
        addImpl(component,constraint,-1);
    }

    /**
     * @see java.awt.Container#add(java.awt.Component, int) Container.add
     */
    public void insert(Component component,int index) {
            addImpl(component,null, index);
    }

    /**
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object, int) Container.add
     */
    public void insert(Component component,Object constraint,int index) {
            addImpl(component,constraint, index);
    }

   /**
    * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object, int) Container.addImpl
    */
   protected void addImpl(Component component,Object cons,int index) {

        //#mdebug debug
        if (component instanceof Window) {
            throw new RuntimeException("trying to add a window to a Component: "+component+" to "+this);
        }
        if (component.parent != null) {
            Logger.warn("this component already has a parent "+component+" PARENT="+component.parent);
            Logger.dumpStack();
        }
        //#enddebug

        component.parent = this;
   }


    /**
     * used by Panel when something is added or removed
     * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
     */
    public void removeParent(Component p) {
        if (parent == p) {
            parent = null;
        }
    }

    /**
     * @see java.awt.Component#getParent() Component.getParent
     */
    public Component getParent() {
            return parent;
    }

    /**
     * @see javax.swing.SwingUtilities#windowForComponent(java.awt.Component) SwingUtilities.windowForComponent
     * @see javax.swing.SwingUtilities#getWindowAncestor(java.awt.Component) SwingUtilities.getWindowAncestor
     */
    public Window getWindow() {
        return (Window)DesktopPane.getAncestorOfClass(Window.class, this);
    }

    /**
     * @return the current x coordinate of the component's origin
     * @see javax.swing.JComponent#getX() JComponent.getX
     */
    public int getX(){
        return posX;
    }

    /**
     * @return the current y coordinate of the component's origin
     * @see javax.swing.JComponent#getY() JComponent.getY
     */
    public int getY(){
        return posY;
    }
    /**
     * @return the current width of this component
     * @see javax.swing.JComponent#getWidth() JComponent.getWidth
     */
    public int getWidth(){
        return width;
    }
    /**
     * @return the current height of this component
     * @see javax.swing.JComponent#getHeight() JComponent.getHeight
     */
    public int getHeight(){
        return height;
    }

    /**
     * @see java.awt.Component#setBounds(int, int, int, int) Component.setBounds
     */
    public void setBounds(int posX, int posY, int width, int height) {
    	setLocation(posX,posY);
    	setSize(width,height);
    }

    /**
     * @see java.awt.Component#setSize(int, int) Component.setSize
     */
    public void setSize(int width, int height){
    	this.width = width;
    	this.height = height;
    }

    /**
     * @see java.awt.Component#setLocation(int, int) Component.setLocation
     */
    public void setLocation(int posX, int posY){
    	this.posX = posX;
    	this.posY = posY;
    }
    /**
     * override and call super when things HAVE to be painted
     * @see java.awt.Component#paint(java.awt.Graphics) Component.paint
     */
    public void paint(Graphics2D g) {
            //Logger.debug("paint "+this);
            paintBorder(g);

            int back = getBackground();
            if ( !Graphics2D.isTransparent(back) ) {
                g.setColor(back);
                g.fillRect(0, 0, width, height);
            }
            //Logger.debug("getname" + getName() + " " + getCurrentBackground() );
            paintComponent(g);

    }

    /**
     * @see javax.swing.JComponent#paintBorder(java.awt.Graphics) JComponent.paintBorder
     */
    protected void paintBorder(Graphics2D g) {

        Border b = getBorder();
        if (b != null) {
            b.paintBorder(this, g,width,height);
        }

    }

    public final static Border empty = new EmptyBorder(0, 0, 0, 0);
    /**
     * @see javax.swing.JComponent#getInsets() JComponent.getInsets
     */
    public Border getInsets() {
        Border b = getBorder();
        return b==null?empty:b;
    }

    /**
     * @see java.awt.Component#getBackground() Component.getBackground
     */
    public final int getBackground() {
        if (background != Style.NO_COLOR) {
            return background;
        }
        return theme.getBackground( getCurrentState() );
    }

    /**
     * @see javax.swing.JComponent#getBorder() JComponent.getBorder
     * @return the border object for this component
     */
    public final Border getBorder() {
        if (border != null) {
            return border;
        }
        return theme.getBorder( getCurrentState() );
    }

    /**
     * @see java.awt.Component#getForeground() Component.getForeground
     */
    public final int getForeground() {
        if (foreground!=Style.NO_COLOR) {
            return foreground;
        }
        int f = theme.getForeground( getCurrentState() );
        if (f!=Style.NO_COLOR || parent==null) {
            return f;
        }
        return parent.getForeground();
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics) JComponent.paintComponent
     */
    public abstract void paintComponent(Graphics2D g);

    /**
     * @see javax.swing.JComponent#processKeyEvent(java.awt.event.KeyEvent) JComponent.processKeyEvent
     */
    public boolean processKeyEvent(KeyEvent keypad) {
            return false;
    }

    /**
     * @see java.awt.Component#processMouseEvent(java.awt.event.MouseEvent) Component.processMouseEvent
     */
    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
        if (focusable) {
            if (type == DesktopPane.PRESSED) {
                if(!isFocusOwner() && isVisible()) { requestFocusInWindow(); }
            }
        }
        else if (parent!=null) {
            parent.processMouseEvent(type,x+posX,y+posY, keys);
        }
        //else {
        //    owner.pointerEvent(type,x+getXInWindow(),y+getYInWindow());
        //}
    }

    public void processMultitouchEvent(int[] type, int[] x, int[] y) {
        if (parent!=null) {
            for (int c=0;c<type.length;c++) {
                x[c] = x[c]+posX;
                y[c] = y[c]+posY;
            }
            parent.processMultitouchEvent(type,x,y);
        }
    }

    public boolean consumesMotionEvents() {
        if (parent!=null) {
            return parent.consumesMotionEvents();
        }
        return false;
    }

    public void run() throws InterruptedException { }
    public void animate() { }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent) FocusListener.focusGained
     */
    public void focusGained() {
        if (focusListener!=null) {
            focusListener.changeEvent(this,FOCUS_GAINED);
        }
        // default focusGained action, make me visible
        makeVisible();
        repaint();
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent) FocusListener.focusLost
     */
    public void focusLost() {
        if (focusListener!=null) {
            focusListener.changeEvent(this,FOCUS_LOST);
        }
        repaint();
    }

    public void makeVisible() {
        Border insets = getInsets();
        scrollRectToVisible(
            -insets.getLeft(),
            -insets.getTop(),
            getWidthWithBorder(),
            getHeightWithBorder(),
            false);
    }

    /**
     * This method checks if a component can be seen in the scrollpane
     */
    public boolean isComponentVisible() {
        Border insets = getInsets();
        return isRectVisible(
            -insets.getLeft(),
            -insets.getTop(),
            getWidthWithBorder(),
            getHeightWithBorder());
    }

    /**
     * This sets the width and height of this component
     * to the MINIMUM that is needed for this component
     * @see javax.swing.JComponent#getMinimumSize() JComponent.getMinimumSize
     */
    protected abstract void workoutMinimumSize();

    /**
     * This method used to be called workoutSize() but that would fail with
     * java/lang/VerifyError: Preverification failed with error code 1. on a Mac
     * @see javax.swing.JComponent#getPreferredSize() JComponent.getPreferredSize
     */
    public final void workoutPreferredSize() {

        // we still need to do this as we may need to workout lines
        //if (preferredWidth==-1 || preferredHeight==-1) {
            workoutMinimumSize();
        //}

        if (preferredWidth!=-1) {
            width = preferredWidth;
        }

        if (preferredHeight!=-1) {
            height = preferredHeight;
        }

    }

    /**
     * @param a The color of the background of the component (-1 for no color to be used)
     * @see javax.swing.JComponent#setBackground(java.awt.Color) JComponent.setBackground
     */
    public void setBackground(int a) {

            background = a;

    }

    /**
     * Opaque means it fully paint all its pixels
     * @return true if it is NOT transparent
     * @see javax.swing.JComponent#isOpaque() JComponent.isOpaque
     */
    public boolean isOpaque() {
        if (Graphics2D.isOpaque( getBackground() )) return true;
        Border b = getBorder();
        if (b!=null) {
            return b.isBorderOpaque();
        }
        return false;
    }

    /**
     * @see java.awt.Component#isShowing() Component.isShowing
     */
    public boolean isShowing() {
        // i need to be visable and my parent needs to be showing
        return isVisible() && (parent==null?false:parent.isShowing()); // is real Swing this method is recursive
    }

    /**
     * @see java.awt.Component#repaint() Component.repaint
     */
    public void repaint() {
        getDesktopPane().repaintComponent(this);
    }

    //#mdebug debug
    /**
     * @see java.awt.Component#paramString() Component.paramString
     * @see java.awt.Component#toString() Component.toString
     */
    public String toString() {
        return getName() +( popup!=null?"<pop="+popup.toString()+">":"" ) ;
    }
    //#enddebug

    /**
     * @param border the border to be rendered for this component
     * @see javax.swing.JComponent#setBorder(javax.swing.border.Border) JComponent.setBorder
     */
    public void setBorder(Border border) {
            this.border = border;
    }

    public int getWidthWithBorder() {
            Border insets = getInsets();
            return getWidth() + insets.getRight() + insets.getLeft();
    }
    public int getHeightWithBorder() {
            Border insets = getInsets();
            return getHeight() + insets.getTop() + insets.getBottom();
    }

    public int getXWithBorder() {
            Border insets = getInsets();
            return getX() - insets.getLeft();
    }
    public int getYWithBorder() {
            Border insets = getInsets();
            return getY() - insets.getTop();
    }

    public void setBoundsWithBorder(int x,int y,int w,int h) {
            Border insets = getInsets();
            setBounds(
                x + insets.getLeft(),
                y + insets.getTop(),
                w - ( insets.getRight() + insets.getLeft() ),
                h - ( insets.getTop() + insets.getBottom() )
            );
    }

    /**
     * @see java.awt.Component#getLocationOnScreen() Component.getLocationOnScreen
     */
    public int getXOnScreen() {
            int x = posX;
            Component p=parent;
            while (p!=null) {
                    x = x+p.posX;
                    p = p.parent;
            }
            return x;
    }

    /**
     * @see java.awt.Component#getLocationOnScreen() Component.getLocationOnScreen
     */
    public int getYOnScreen() {
            int y = posY;
            Component p=parent;
            while (p!=null) {
                    y = y+p.posY;
                    p = p.parent;
            }
            return y;
    }

    public void wait(int a) throws InterruptedException {
        getDesktopPane().aniWait(this,a);
    }

    /**
     * @see javax.swing.JComponent#setForeground(java.awt.Color) JComponent.setForeground
     */
    public void setForeground(int foreground) {
            this.foreground = foreground;
    }


    /**
     * @param x X position inside CURRENT component
     * @param y Y position inside CURRENT component
     * @param w Width of area inside CURRENT component
     * @param h Height of area inside CURRENT component
     * @param smart use smart scroll, if true and the component is too far it will only scroll a bit and not all the way
     * @return if smart was on, returns true if the scroll did reach its destination
     * @see javax.swing.JComponent#scrollRectToVisible(java.awt.Rectangle) JComponent.scrollRectToVisible
     */
    public boolean scrollRectToVisible(int x,int y,int w,int h,boolean smart) {
        if (parent!=null) {
            return parent.scrollRectToVisible(posX+x,posY+y,w,h,smart);
        }
        return true;
    }

    /**
     * @see javax.swing.JComponent#computeVisibleRect(java.awt.Rectangle) JComponent.computeVisibleRect
     * @see javax.swing.JComponent#getVisibleRect() JComponent.getVisibleRect
     */
    public boolean isRectVisible(int x,int y,int w,int h) {
        if (parent!=null) {
            return parent.isRectVisible(posX+x,posY+y,w,h);
        }
        return true;
    }

    /* * not used any more
     * @return if there was a scroll happen or not
     */
    //public boolean scrollUpDown(int d) {
    //    if (parent != null) {
    //        return parent.scrollUpDown(d);
    //    }
    //    return false;
    //}

    /**
     * @see javax.swing.JComponent#updateUI() JComponent.updateUI
     */
    public void updateUI() {
        theme = DesktopPane.getDefaultTheme(this);
    }

    /**
     * @see javax.swing.JComponent#getToolTipText() JComponent.getToolTipText
     * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent) JComponent.getToolTipText
     */
    public String getToolTipText() {
        return tooltip;
    }

    /**
     * @param text The Text to use as the tooltip
     * @see javax.swing.JComponent#setToolTipText(java.lang.String) JComponent.setToolTipText
     */
    public void setToolTipText(String text) {
        tooltip = text;
    }
    /**
     * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent) JComponent.getToolTipLocation
     */
    public int getToolTipLocationX() {
        return 5;
    }
    /**
     * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent) JComponent.getToolTipLocation
     */
    public int getToolTipLocationY() {
        return 5;
    }

    /**
     * this means reclac the size of children
     * and then redo the layout
     * @see javax.swing.JComponent#revalidate() JComponent.revalidate
     */
    public void revalidate() {
        getDesktopPane().revalidateComponent(this);
    }
    /**
     * @see Panel#validate()
     * @see java.awt.Container#validate() Container.validate()
     */
    public void validate() {
        // nothing??
    }
    public void setValue(Object obj) {
    }
    public Object getValue() {
        return null;
    }

    public void setState(int state) {
        this.state = state;
    }

    int state=-1;
    public final int getCurrentState() {
        return state==-1?getState():state;
    }

    protected int getState() {
        int result=Style.ALL;

        if (!focusable) {
            result |= Style.DISABLED;
        }
        if (isFocusOwner()) {
            result |= Style.FOCUSED;
        }

        return result;
    }

    /**
     * use this if this component is a renderer
     */
    public void setupState(Component component, boolean isSelected, boolean cellHasFocus) {
            state=Style.ALL;

            if (cellHasFocus) {
                state |= Style.FOCUSED;
            }
            else if ( component!=null && !component.isFocusable()) { // can be DISABLED only if not FOCUSED
                state |= Style.DISABLED;
            }

            if (isSelected) {
                state |= Style.SELECTED;
            }
    }

    /**
     * this does NOT work like the swing method
     * @see javax.swing.JComponent#computeVisibleRect(java.awt.Rectangle) JComponent.computeVisibleRect
     */
    public void computeVisibleRect(int[] v) {
        if (v[2]!=0) { // just check 1 for speed, but all of them should be 0 if v[2]==0
            if (parent!=null) {
                parent.computeVisibleRect(v);
            }
        }
        //#mdebug debug
        else if (v[0]!=0||v[1]!=0||v[3]!=0) {
            throw new RuntimeException("if visible width is 0, all other values should be 0 values=["+v[0]+" "+v[1]+" "+v[2]+" "+v[3]+"]");
        }
        //#enddebug
    }

    /**
     * if nothing is visible should return [x=0,y=0,width=0,height=0] this is what Swing does
     * @see javax.swing.JComponent#getVisibleRect() JComponent.getVisibleRect
     */
    public int[] getVisibleRect() {
        int[] v = new int[4];
        Border insets = getInsets();

        int xs = getXOnScreen();
        int ys = getYOnScreen();

        v[0] = xs-insets.getLeft();
        v[1] = ys-insets.getTop();
        v[2] = getWidthWithBorder();
        v[3] = getHeightWithBorder();

        if (v[2]>0&&v[3]>0) {
            if (parent!=null) {
                parent.computeVisibleRect(v);
            }
            if (v[2]>0) { // if we have not been set to all 0s
                v[0] = v[0] - xs; // make the values relative to the component
                v[1] = v[1] - ys;
            }
        }
        else {
            v[0]=v[1]=v[2]=v[3]=0; // set all to 0
        }

        //#mdebug debug
        if ((v[2]<=0||v[3]<=0) && (v[0]!=0||v[1]!=0||v[2]!=0||v[3]!=0)) {
            throw new RuntimeException("invalid result");
        }
        //#enddebug

        return v;
    }

    public DesktopPane getDesktopPane() {
        Window w = getWindow();
        if (w!=null) {
            return w.getDesktopPane();
        }
        return DesktopPane.getDesktopPane();
    }

    /**
     * in Swing this is done by adding a MouseListener that will then fire the popup menu
     * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener) Component.addMouseListener
     * @see java.awt.Component#add(java.awt.PopupMenu)
     */
    public void setPopupMenu(Window component) {
        popup = component;
    }
    /**
     * @see javax.swing.JMenu#getPopupMenu() JMenu.getPopupMenu
     */
    public Window getPopupMenu() {
        return popup;
    }

}
