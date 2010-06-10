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
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.KeyEvent;
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
    protected Style theme;

    protected int background=Style.NO_COLOR;
    protected int foreground=Style.NO_COLOR;
    private Border border;

    private String tooltip;
    private boolean visible;

    protected int preferredWidth=-1;
    protected int preferredHeight=-1;

    public static final int FOCUS_GAINED = 1;
    public static final int FOCUS_LOST = 2;
    private ChangeListener focusListener;

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
        this.focusable = selectable;
        Window w = getWindow();
        if (w!=null) {
            w.setupFocusedComponent();
        }
    }

    /**
     * This method WILL use small scroll, so if the component is too far then it wont gain focus
     * @param direction can be up down right or left
     * @see java.awt.Component#transferFocus() Component.transferFocus
     * @see java.awt.Component#transferFocusBackward() Component.transferFocusBackward
     */
    public void transferFocus(int direction) {
        ((Panel)parent).breakOutAction(this, direction, true,false);
    }

    /**
     * used by Panel when something is added or removed
     * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
     */
    protected void setParent(Component p) {
        //#mdebug
        if (p==null) {
            throw new NullPointerException("parent can not be set to null");
        }
        //#enddebug
        parent = p;
    }

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
     */
    public Window getWindow() {
        if (parent == null) { return (this instanceof Window)?(Window)this : null; }
        return parent.getWindow();
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

            int back = getCurrentBackground();
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

        Border b = getCurrentBorder();
        if (b != null) {
            b.paintBorder(this, g,width,height);
        }

    }

    public final static Border empty = new EmptyBorder(0, 0, 0, 0);
    /**
     * @see javax.swing.JComponent#getInsets() JComponent.getInsets
     */
    public Border getInsets() {
        Border b = getCurrentBorder();
        return b==null?empty:b;
    }

    public final int getCurrentBackground() {
        if (background != Style.NO_COLOR) {
            return background;
        }
        else {
            return theme.getBackground( getCurrentState() );
        }
    }

    public final Border getCurrentBorder() {
            if (border != null) {
                return border;
            }
            else {
                return theme.getBorder( getCurrentState() );
            }
    }

    public final int getCurrentForeground() {
            if (foreground!=Style.NO_COLOR) {
                return foreground;
            }
            else {
                return theme.getForeground( getCurrentState() );
            }
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
        return false;
    }

    public void animate() throws InterruptedException { }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent) FocusListener.focusLost
     */
    public void focusLost() {
        if (focusListener!=null) {
            focusListener.changeEvent(this,FOCUS_LOST);
        }
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent) FocusListener.focusGained
     */
    public void focusGained() {

        if (focusListener!=null) {
            focusListener.changeEvent(this,FOCUS_GAINED);
        }

        // default focusGained action, make me visible
        makeVisible();

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
    public abstract void workoutMinimumSize();

    /**
     * @see javax.swing.JComponent#getPreferredSize() JComponent.getPreferredSize
     */
    public final void workoutSize() {

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
        if (Graphics2D.isOpaque( getCurrentBackground() )) return true;
        Border b = getCurrentBorder();
        if (b!=null) {
            return b.isBorderOpaque();
        }
        return false;
    }

    /**
     * @see java.awt.Component#isShowing() Component.isShowing
     */
    public boolean isShowing() {
        boolean vis = isVisible();
        Component p1 = getParent();
        while (vis && p1!=null) {
            vis = p1.isVisible();
            p1 = p1.getParent();
        }
        return vis;
    }

    /**
     * @see java.awt.Component#repaint() Component.repaint
     */
    public void repaint() {

            Window myWindow = getWindow();

            // if we are not in a window, do nothing
            if (myWindow==null || !isShowing()) return;

            DesktopPane desktop = myWindow.getDesktopPane();

            if (!isOpaque()) {

                    Component p=parent;

                    while (p!=null) {

                            if (!p.isOpaque()) {
                                    p = p.parent;
                            }
                            else {
                                    break;
                            }

                    }
                    // if we have reached the nothingness
                    if (p == null) {
                            desktop.fullRepaint();
                    }
                    else {
                            desktop.repaintComponent(p);
                    }
            }
            else {
                    desktop.repaintComponent(this);
            }
    }

    //#mdebug debug
    /**
     * @see java.awt.Component#toString() Component.toString
     */
    public String toString() {
            return getName();
    }
    //#enddebug

    /**
     * @return the border object for this component
     * @see javax.swing.JComponent#getBorder() JComponent.getBorder
     */
    public Border getBorder() {
            return border;
    }

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
     * @see java.awt.Component#getForeground() Component.getForeground
     */
    public int getForeground() {
            return foreground;
    }

    /**
     * @see javax.swing.JComponent#setForeground(java.awt.Color) JComponent.setForeground
     */
    public void setForeground(int foreground) {
            this.foreground = foreground;
    }

    /**
     * @see java.awt.Component#getBackground() Component.getBackground
     */
    public int getBackground() {
            return background;
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

            if (parent instanceof ScrollPane) {
                    return ((ScrollPane)parent).makeVisible(x,y,w,h,smart);
            }

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

            if (parent instanceof ScrollPane) {
                    return ((ScrollPane)parent).isRectVisible(x,y,w,h);
            }

            if (parent!=null) {

                    return parent.isRectVisible(posX+x,posY+y,w,h);
            }

            return true;
    }

    /**
     * @return if there was a scroll happen or not
     */
    public boolean scrollUpDown(int d) {

        if (parent instanceof ScrollPane) {

            ScrollPane scroller = (ScrollPane)parent;

            int oldx = posX;
            int oldy = posY;

            if (d==Canvas.RIGHT) {
                    scroller.makeVisible(width-1,-posY+scroller.getViewPortY(),1,1,true);
            }
            else if (d==Canvas.LEFT) {
                    scroller.makeVisible(0,-posY+scroller.getViewPortY(),1,1,true);
            }
            else if (d==Canvas.UP) {
                    scroller.makeVisible(-posX+scroller.getViewPortX(),0,1,1,true);
            }
            else { // DOWN
                    scroller.makeVisible(-posX+scroller.getViewPortX(),height-1,1,1,true);
            }
            return oldx!=posX || oldy!=posY;
        }
        else if (parent != null) {
            return parent.scrollUpDown(d);
        }

        return false;
    }

    /**
     * @see javax.swing.JComponent#updateUI() JComponent.updateUI
     */
    public void updateUI() {

        theme = DesktopPane.getDefaultTheme(this);
        //background = theme.getBackground(Style.ALL);
        //foreground = theme.getForeground(Style.ALL);
        //border = theme.getBorder(Style.ALL);
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

    public int getState() {
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
            if ( component!=null && !component.isFocusable()) {
                state |= Style.DISABLED;
            }
            if (cellHasFocus) {
                state |= Style.FOCUSED;
            }
            if (isSelected) {
                state |= Style.SELECTED;
            }
    }

    public void clip(Graphics2D g) {
        if (parent!=null) {
            parent.clip(g);
        }
    }

    public DesktopPane getDesktopPane() {
        Window w = getWindow();
        if (w!=null) {
            return w.getDesktopPane();
        }
        return DesktopPane.getDesktopPane();
    }

}
