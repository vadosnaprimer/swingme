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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.layout.Layout;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JScrollPane
 */
public class ScrollPane extends Panel implements Runnable {

    public static final int MODE_NONE=-1;
    public static final int MODE_SCROLLBARS=0;
    public static final int MODE_SCROLLARROWS=1;
    public static final int MODE_INDICATOR=2;

    private int mode;
    private int barThickness;

    private Icon rightArrow;
    private Icon leftArrow;
    private Icon upArrow;
    private Icon downArrow;

    private Slider slider;
    private boolean clip = true;

    /**
     * @see javax.swing.JScrollPane#JScrollPane() JScrollPane.JScrollPane
     */
    public ScrollPane() {
        this(MODE_SCROLLBARS);
    }

    /**
     * @param view the component to display in the scrollpane's viewport
     * @see javax.swing.JScrollPane#JScrollPane(java.awt.Component) JScrollPane.JScrollPane
     */
    public ScrollPane(Component view) {
        this(view,MODE_SCROLLBARS);
    }

    public ScrollPane(int m) {
        super(null);
        setName("ScrollPane");

        slider = new Slider();
        slider.setName("ScrollBar");

        setMode(m);
    }

    public ScrollPane(Component view,int a) {
        this(a);
        add(view);
    }

    //#mdebug debug
    public void setLayout(Layout lt) {
        if (lt!=null) throw new IllegalArgumentException();
    }
    //#enddebug

    public void setMode(int m) {
        mode = m;
    }

    public void setClip(boolean c) {
        clip = c;
    }

    final int BOUNCE_LEFT = 0x01;
    final int BOUNCE_RIGHT = 0x02;
    final int BOUNCE_TOP = 0x04;
    final int BOUNCE_BOTTOM = 0x08;
    final int BOUNCE_HORIZONTAL = BOUNCE_LEFT | BOUNCE_RIGHT;
    final int BOUNCE_VERTICAL = BOUNCE_TOP | BOUNCE_BOTTOM;

    private int bounceMode;

    public void setBounceMode(int bounceMode) {
        this.bounceMode = bounceMode;
    }

    public int getBarThickness() {
        return slider.getHeight();
    }

    /**
     * @see javax.swing.JViewport#getView() JViewport.getView
     */
    public Component getView() {
        return ((Component)getComponents().elementAt(0));
    }

    protected void addImpl(Component component, Object cons, int index) {

        //#mdebug debug
        if (cons!=null || index!=-1) {
            throw new RuntimeException("must use add, cons="+cons+" index="+index);
        }
        //#enddebug

        removeAll();

        super.addImpl(component, null, -1);

        //component = a;
        //a.setOwner(owner);
        //if (a instanceof Panel) {
        // ((Panel)a).setScrollPanel(this);
        //}

        // TODO this does not work as the size of the component is not set yet
        // and so we do not know if we need to scroll, and so do not know the size taken
        // up by any scroll arrows or scrollbars
        // so all this ever does it reset the view to 0,0
        setViewLocation(getViewPortX(), getViewPortY());

        // TODO does it take into account the border?
    }

    public boolean scrollRectToVisible(int x,int y,int w,int h,boolean smart) {

        Component v = getView();
        int oldx = v.getX();
        int oldy = v.getY();
        boolean reached = makeVisible(x-oldx,y-oldy,w,h,smart);

        if ( oldx!=v.getX() || oldy!=v.getY() ) {
            return reached;
        }
        return super.scrollRectToVisible(x, y, w, h, smart);
    }

    /**
     * check if something is currently visible or as visible as it can be
     * i.e. scrolling wont make it more visible, even if its not all currently visible
     */
    public boolean isRectVisible(int x,int y,int w,int h) {

        int viewX= getViewPortX();
        int viewY= getViewPortY();
        int viewHeight = getViewPortHeight();
        int viewWidth = getViewPortWidth(viewHeight);

        boolean visible = ( ((x>=viewX && x+w<=viewX+viewWidth)||(x<=viewX && x+w>=viewX+viewWidth)) &&
                ((y>=viewY && y+h<=viewY+viewHeight)||(y<=viewY && y+h>=viewY+viewHeight))
        );
        if (!visible) {
            return false;
        }
        return super.isRectVisible(x, y, w, h);
    }

    protected boolean scrollUpDown(int d) {

        Component v = getView();

        int oldx = v.getX();
        int oldy = v.getY();

        if (d==Canvas.RIGHT) {
                makeVisible(v.getWidth()-1,-oldy+getViewPortY(),1,1,true);
        }
        else if (d==Canvas.LEFT) {
                makeVisible(0,-oldy+getViewPortY(),1,1,true);
        }
        else if (d==Canvas.UP) {
                makeVisible(-oldx+getViewPortX(),0,1,1,true);
        }
        else { // DOWN
                makeVisible(-oldx+getViewPortX(),v.getHeight()-1,1,1,true);
        }

        return oldx!=v.getX() || oldy!=v.getY();

    }

    private boolean makeVisible(int x,int y,int w,int h,boolean smartscroll) {
        return makeVisible(x, y, w, h, smartscroll, true);
    }

    private boolean makeVisible(int x,int y,int w,int h,boolean smartscroll, boolean bond) {

        Component component = getView();
        int oldX = component.getX();
        int oldY = component.getY();

        int viewPortX=getViewPortX();
        int viewPortY=getViewPortY();
        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);

        //Logger.debug("x="+x+" y="+y+" w="+w+" h="+h);
        //Logger.debug("ViewPortX="+viewPortX+" ViewPortY="+viewPortY+" ViewPortWidth="+viewPortWidth+" ViewPortHeight="+viewPortHeight);

        int right = x+w;
        int bottom = y+h;

        int componentX = -oldX;
        int componentY = -oldY;

        // check if the viewport is maybe already looks at part of this bigger area
        // if it is, then we dont want to scroll to any part of it

        boolean rightTooFar = right > (viewPortX + componentX + viewPortWidth);
        boolean leftTooFar = x < (viewPortX + componentX);
        if (!(leftTooFar && rightTooFar)) {
            boolean tooWide = w > viewPortWidth;
            if ((rightTooFar && !tooWide) || (leftTooFar && tooWide)) {
                componentX = right - viewPortWidth - viewPortX;
            }
            if ((rightTooFar && tooWide) || (leftTooFar && !tooWide)) {
                componentX = x-viewPortX;
            }
        }

        boolean bottomTooFar = bottom > (viewPortY + componentY + viewPortHeight);
        boolean topTooFar = y < (viewPortY + componentY);
        if (!(topTooFar && bottomTooFar)) {
            boolean tooTall = h > viewPortHeight;
            if ((bottomTooFar && !tooTall) || (topTooFar && tooTall)) {
                componentY = bottom - viewPortHeight - viewPortY;
            }
            if ((bottomTooFar && tooTall) || (topTooFar && !tooTall)) {
                componentY = y-viewPortY;
            }
        }


        // check we r not scrolling off the content panel
        if (bond) {
            componentX = Math.min(componentX, component.getWidth() - viewPortWidth - viewPortX);
            componentY = Math.min(componentY, component.getHeight() - viewPortHeight - viewPortY);

            componentX = Math.max(componentX, -viewPortX);
            componentY = Math.max(componentY, -viewPortY);
        }

        int xdiff=-componentX -oldX;
        int ydiff=-componentY -oldY;

        boolean goodscroll=true;

        if (smartscroll) {

            if (Math.abs(xdiff) > viewPortWidth) {
                xdiff = (xdiff>0)?viewPortWidth*2/3:-viewPortWidth*2/3;
                goodscroll = false;
            }

            if (Math.abs(ydiff) > viewPortHeight) {
                ydiff = (ydiff>0)?viewPortHeight*2/3:-viewPortHeight*2/3;
                goodscroll = false;
            }
        }

        int newX = oldX+xdiff;
        int newY = oldY+ydiff;

        // NEVER CALL setBounds here as it will call setSize and that will cause a revalidate
        //component.setBounds(15, 15, component.getWidth(), component.getHeight());

        //Logger.debug("new pos: x="+component.getX()+" y="+component.getY() );

        // only repint if we have moved
        if (oldX!=newX || oldY!=newY) {
            setViewLocation( newX , newY );
            repaint();
        }

        return goodscroll;

    }

    public int getViewPortHeight() {
        switch (mode) {
            case MODE_SCROLLBARS: return height-getViewPortY()-((getView().getWidth()> (width-getViewPortX()) )?barThickness:0);
            case MODE_SCROLLARROWS: return (getView().getHeight() > height)?height-(barThickness*2):height;
            case MODE_NONE:
            case MODE_INDICATOR: return height;
            default: throw new RuntimeException();
        }
    }
    public int getViewPortWidth() {
        return getViewPortWidth(getViewPortHeight());
    }

    private int getViewPortWidth(int vph) {
        switch (mode) {
            case MODE_SCROLLBARS: return width-getViewPortX()-((getView().getHeight()>vph)?barThickness:0);
            case MODE_SCROLLARROWS: return (getView().getWidth() > width)?width-(barThickness*2):width;
            case MODE_NONE:
            case MODE_INDICATOR: return width;
            default: throw new RuntimeException();
        }
    }
    public int getViewPortX() {
        switch (mode) {
            case MODE_SCROLLARROWS: return (getView().getWidth() > width)?barThickness:0;
            case MODE_SCROLLBARS:
            case MODE_NONE:
            case MODE_INDICATOR: return 0;
            default: throw new RuntimeException();
        }
    }
    public int getViewPortY() {
        switch (mode) {
            case MODE_SCROLLARROWS: return (getView().getHeight() > height)?barThickness:0;
            case MODE_SCROLLBARS:
            case MODE_NONE:
            case MODE_INDICATOR: return 0;
            default: throw new RuntimeException();
        }
    }

    protected void workoutMinimumSize() {

        slider.workoutPreferredSize();

        switch (mode) {
            case MODE_SCROLLBARS: barThickness = getBarThickness(); break;
            case MODE_SCROLLARROWS: // fall though
            case MODE_INDICATOR: barThickness = (rightArrow != null) ? rightArrow.getIconWidth() : 0; break;
            case MODE_NONE: barThickness = 0; break;
            default: throw new RuntimeException();
        }

        super.workoutMinimumSize();

        Component view = getView();

        // we use the real size and NOT the size with border here
        // as the border is outside the content, and only visable when you
        // drag past the edge
        width = view.getWidth();
        height = view.getHeight();
    }


    /**
     * we have to do this here, as only here do we already know what OUR size is
     * so we can resize the content how we want
     *
     * scrollpane will go to the size of the content panel when workout size is called on it
     * if we have sence been shrunk, this means we need to be able to scroll, and
     * scrollbars are needed, this means we need to trigger the content to give me good sizes
     */
    public void doLayout() {
        if (getComponents().size() == 1) {

            Component comp = getView();

            int viewHeight=getViewPortHeight();
            int viewWidth=getViewPortWidth(viewHeight);
            int cw = comp.getWidth();
            int ch = comp.getHeight();
/* this is another solution
            // we need to pass
            if (mode == MODE_SCROLLBARS && ch > viewHeight && cw < viewWidth ) {
Logger.debug("size1 "+ viewWidth+" "+ ch);
                comp.setSize( viewWidth , ch);
                comp.workoutSize();
                cw = comp.getWidth();
                ch = comp.getHeight();
            }
*/
            if ( cw < viewWidth) {
                cw = viewWidth;
            }
            if (ch < viewHeight) {
                ch = viewHeight;
            }

//Logger.debug("size2 "+ cw+" "+ ch);
            comp.setSize(cw, ch);

            // only do this in the last revalidate
            if (DesktopPane.myLastRevalidate(this)) {
                if (comp.getX() > getViewPortX()) {
                    setViewLocation(getViewPortX(), comp.getY());
                }
                if (comp.getY() > getViewPortY()) {
                    setViewLocation(comp.getX(), getViewPortY());
                }
                if ((cw - getViewPortX() + comp.getX()) < getViewPortWidth()) {
                    setViewLocation(-cw +getViewPortX() +getViewPortWidth(), comp.getY());
                }
                if ((ch - getViewPortY() + comp.getY()) < getViewPortHeight()) {
                    setViewLocation(comp.getX(),-ch +getViewPortY() +getViewPortHeight());
                }
            }
        }

    }

    public void paintChildren(Graphics2D g) {

        int[] a=null;
        if (clip) {
            a=g.getClip();

            int viewX=getViewPortX();
            int viewY=getViewPortY();
            int viewHeight=getViewPortHeight();
            int viewWidth=getViewPortWidth(viewHeight);

            // dont care about clipping for the
            // scrollbars as they r painted over the top
            g.clipRect(viewX, viewY, viewWidth, viewHeight);
        }
        super.paintChildren(g);
        if (clip) {
            g.setClip(a);
        }

        //g.setColor(0x00FF0000);
        //g.drawRect(viewX, viewY, viewWidth-1, viewHeight-1);

        paintDecoration(g);
    }

    protected void paintDecoration(final Graphics2D g) {

        switch (mode) {
            case MODE_NONE: return;
            case MODE_SCROLLBARS: drawScrollBars(g); return;
            case MODE_SCROLLARROWS: drawScrollArrows(g,false); return;
            case MODE_INDICATOR: drawScrollArrows(g,true); return;
            default: throw new RuntimeException();
        }

    }

    private void drawScrollBars(final Graphics2D g) {

        int viewPortX = getViewPortX();
        int viewPortY = getViewPortY();
        int viewPortHeight=getViewPortHeight();
        int viewPortWidth=getViewPortWidth(viewPortHeight);
        Component view = getView();
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        boolean vertical = viewHeight > viewPortHeight; // NEEDS to be same check as in getViewPortWidth

        boolean horizontal = viewWidth > (width-viewPortX); // NEEDS to be same check as in getViewPortHeight

        if ( vertical ) {
            int t = g.getTransform();
            g.setTransform( Sprite.TRANS_MIRROR_ROT270 );
            slider.drawScrollBar(g,
                    viewPortY,
                    width -barThickness,
                    height -viewPortY -(horizontal?barThickness:0),
                    barThickness,
                    viewPortY-view.getY(),
                    viewPortHeight,
                    viewHeight
            );
            g.setTransform( t );
        }
        if ( horizontal ) {
            slider.drawScrollBar(g,
                    viewPortX,
                    height -barThickness,
                    width -viewPortX -(vertical?barThickness:0),
                    barThickness,
                    viewPortX-view.getX(),
                    viewPortWidth,
                    viewWidth
            );
        }
    }


    private void drawScrollArrows(final Graphics2D g,boolean indicator) {

        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);

        int viewX=getViewPortX();
        int viewY=getViewPortY();

        Component view = getView();

        int d = barThickness*2;
        int gap=2;
        boolean canScroll;

        if (view.getWidth() > width) {

            canScroll = view.getX() < viewX;

            if (indicator) {
                drawArrow(g, width/2 -gap-d, height+(d+3*gap)/2-barThickness, barThickness, d,canScroll,Graphics.LEFT);
            }
            else {
                drawArrow(g, 0, (height-d)/2, barThickness, d,canScroll,Graphics.LEFT);
            }

            canScroll = (view.getWidth()+view.getX()-viewX) > viewWidth;

            if (indicator) {
                drawArrow(g, width/2 +gap+barThickness, height+(d+3*gap)/2-barThickness, barThickness, d,canScroll,Graphics.RIGHT);
            }
            else {
                drawArrow(g, width - barThickness , (height-d)/2, barThickness, d,canScroll,Graphics.RIGHT);
            }

        }

        if (view.getHeight() > height) {

            canScroll = view.getY() < viewY;

            if (indicator) {
                drawArrow(g, (width-d)/2, height+gap, d, barThickness,canScroll,Graphics.TOP);
            }
            else {
                drawArrow(g, (width-d)/2, 0, d, barThickness,canScroll,Graphics.TOP);
            }

            canScroll = (view.getHeight()+view.getY()-viewY) > viewHeight;

            if (indicator) {
                drawArrow(g, (width-d)/2,height+barThickness+gap*2, d, barThickness,canScroll,Graphics.BOTTOM);
            }
            else {
                drawArrow(g, (width-d)/2,height-barThickness, d, barThickness,canScroll,Graphics.BOTTOM);
            }

        }
    }

    public void drawArrow(Graphics2D g, int x, int y, int w, int h,boolean canScroll,int direction) {

        switch (direction) {
        case Graphics.LEFT: {
                if (leftArrow!=null) {
                if (canScroll) {
                        leftArrow.paintIcon(this, g,  x+(w-leftArrow.getIconWidth())/2, y+(h-leftArrow.getIconHeight())/2 );
                    }
                }
                else {
                    //drawLeftArrow(g, x, y, w, h);
            }
            break;
        }
        case Graphics.RIGHT: {
                if (rightArrow!=null) {
                if (canScroll) {
                        rightArrow.paintIcon(this, g,x+(w-rightArrow.getIconWidth())/2, y+(h-rightArrow.getIconHeight())/2 );
                    }
                }
                else {
                    //drawRightArrow(g, x, y, w, h);
            }
            break;
        }
        case Graphics.TOP: {
                if (upArrow!=null) {
                if (canScroll) {
                        upArrow.paintIcon(this, g, x+(w-upArrow.getIconWidth())/2, y+(h-upArrow.getIconHeight())/2 );
                    }
                }
                else {
                    //drawUpArrow(g, x, y, w, h);
            }
            break;
        }
        case Graphics.BOTTOM: {
                if (downArrow!=null) {
                if (canScroll) {
                        downArrow.paintIcon(this, g, x+(w-downArrow.getIconWidth())/2, y+(h-downArrow.getIconHeight())/2 );
                    }
                }
                else {
                    //drawDownArrow(g, x, y, w, h);
            }
            break;
        }
        }
    }

    /**
     * will crop down v to the size of the vewport
     * @see javax.swing.SwingUtilities#computeIntersection(int, int, int, int, java.awt.Rectangle) SwingUtilities.computeIntersection
     */
    public void computeVisibleRect(int[] v) {
        if (clip) {
            int viewPortX=getXOnScreen()+getViewPortX();
            int viewPortY=getYOnScreen()+getViewPortY();
            int viewPortHeight=getViewPortHeight();
            int viewPortWidth=getViewPortWidth(viewPortHeight);

            if (viewPortX>v[0]) {
                v[2] = v[2] - (viewPortX-v[0]);
                v[0] = viewPortX;
            }
            if (viewPortY>v[1]) {
                v[3] = v[3] - (viewPortY-v[1]);
                v[1] = viewPortY;
            }
            int a = viewPortX+viewPortWidth; // view right
            int b = v[0]+v[2]; // clip right
            if (a<b) {
                v[2] = v[2]-(b-a);
            }
            int c = viewPortY+viewPortHeight; // view bottom
            int d = v[1]+v[3]; // clip bottom
            if (c<d) {
                v[3] = v[3]-(d-c);
            }
            if (v[2]<=0||v[3]<=0) {
                v[0]=v[1]=v[2]=v[3]=0; // set all to 0
            }
        }
        super.computeVisibleRect(v);
    }

    /**
     * as the is ALWAYS fully covered by its component, is uses that too, to work this out
     */
    public boolean isOpaque() {
        boolean mine = super.isOpaque();
        return (mine) ? mine : getView().isOpaque();
    }
    public void updateUI() {
        super.updateUI();

        rightArrow = (Icon)theme.getProperty("rightArrow", Style.ALL);
        leftArrow = (Icon)theme.getProperty("leftArrow", Style.ALL);
        upArrow = (Icon)theme.getProperty("upArrow", Style.ALL);
        downArrow = (Icon)theme.getProperty("downArrow", Style.ALL);

        if (slider!=null)
            slider.updateUI();
    }


    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the top-most child is returned
     * @see java.awt.Container#getComponentAt(int, int) Container.getComponentAt
     */
    public Component getComponentAt(int x, int y) {

        int viewPortX=getViewPortX();
        int viewYPortY=getViewPortY();
        int viewPortHeight=getViewPortHeight();
        int viewPortWidth=getViewPortWidth(viewPortHeight);

        if (isPointInsideRect(x,y,viewPortX,viewYPortY,viewPortWidth,viewPortHeight)) {

            return super.getComponentAt(x,y);
        }

        return this;
    }

    public static boolean isPointInsideRect(int x,int y,int recX,int recY,int recWidth,int recHeight) {
        return x>=recX && x<(recX+recWidth) && y>=recY && y<(recY+recHeight);
    }



    // -------------------------- POINTER DRAGGING ------------------------
    private static final int DRAG_NONE = 0;
    private static final int DRAG_CLICKED_ARROW = 1;
    private static final int DRAG_CLICKED_TRACK = 2;
    private static final int DRAG_SLIDER_HORZ = 3;
    private static final int DRAG_SLIDER_VERT = 4;

    private static final int DRAG_BUFFER_SIZE = 50;
    private static final int DRAG_PAGE_RATE = 10;
    private static final int DRAG_FRAME_RATE = 50;

    private static ScrollPane dragScrollPane;

    private int dragScrollBarMode;
    private int dragStartViewY, dragStartViewX;
    private int dragStartY, dragStartX;
    private int dragLastY, dragLastX;
    private int dragVelocityY, dragVelocityX;
    private int dragFriction;
    private int dragTimeY, dragTimeX;
    private int fitSizeTime;


    // Keep a history of drag events, so we can later calculate the average speed.
    // These are circular buffers
    private long[] dragTimes = new long[DRAG_BUFFER_SIZE];
    private int[] dragBufferX = new int[DRAG_BUFFER_SIZE];
    private int[] dragBufferY = new int[DRAG_BUFFER_SIZE];

    // Index of the most recent entry.
    private int dragBufferPos;

    private int doClickInScrollbar(int click,int dragSlider,int extent1) {

        if (dragScrollBarMode != DRAG_NONE) {
            return 0;
        }

        int dragMode;
        int velocity;

        switch(click) {
        case Slider.CLICK_UP:
            dragMode = DRAG_CLICKED_ARROW;
            velocity = 200;
            break;
        case Slider.CLICK_PGUP:
            dragMode = DRAG_CLICKED_TRACK;
            velocity = extent1 * DRAG_FRAME_RATE;
            break;
        case Slider.CLICK_THUMB:
            velocity = 0;
            dragMode = dragSlider;
            break;
        case Slider.CLICK_PGDOWN:
            dragMode = DRAG_CLICKED_TRACK;
            velocity = -extent1 * DRAG_FRAME_RATE;
            break;
        case Slider.CLICK_DOWN:
            dragMode = DRAG_CLICKED_ARROW;
            velocity = -200;
            break;
        default: // same as CLICK_NONE
            velocity = 0;
            dragMode = DRAG_NONE;
            break;
        }

        dragScrollBarMode = dragMode;

        return velocity;
    }

    private synchronized static void animateScrollPane(ScrollPane dragScrollPane) {
        boolean startThread = (dragScrollPane != null && ScrollPane.dragScrollPane != dragScrollPane);

        ScrollPane.dragScrollPane = dragScrollPane;

        if (startThread) {
            new Thread(dragScrollPane,"SwingME-Scroll").start();
        }

        ScrollPane.class.notifyAll();
    }

    // this can also be done by kidnapping the animation thread
    // and then giving it back when it is not needed any more
    public void run() {
        //System.out.println("START ScrollPane Thread...");
        long startAnimTime = System.currentTimeMillis();
        // int count = 0;

        try {
            while (true) {
                synchronized (ScrollPane.class) {

                    //System.out.println("- ScrollPane Animate -");

                    long time = System.currentTimeMillis();

                    // HACK: Getting the UI lock. At the moment this lock is the
                    // same as the "windows" object
                    Object uiLock = getWindow().getDesktopPane().getAllFrames();
                    synchronized (uiLock) {

                        boolean animate = dragScrollBars((int)(time - startAnimTime));
                        //  boolean animate = dragScrollBars(count * 20);
                        //  count++;

                        if (!animate) {
                            if (ScrollPane.dragScrollPane == this) {
                                ScrollPane.dragScrollPane = null;
                            }
                            break;
                        }
                    }

                    int rate = (dragScrollBarMode == DRAG_CLICKED_TRACK) ? DRAG_PAGE_RATE : DRAG_FRAME_RATE;
                    long timeToWait = time - System.currentTimeMillis() + (1000L / rate);
                    if (timeToWait > 0) {
                        //System.out.println("--- ScrollPane timeToWait = " + timeToWait);
                        ScrollPane.class.wait(timeToWait);
                    }
                }
            }

        }
        catch(Throwable e) {
            //#debug info
            Logger.info(e);

            // we use this instead of using brackets as blackberry build script does not like brackets in catch blocks
            ScrollPane.dragScrollPane = ScrollPane.dragScrollPane == this?null:ScrollPane.dragScrollPane;
        }

        //System.out.println("END ScrollPane Thread... (" + (System.currentTimeMillis() - startAnimTime) + "ms)");
    }

    private void updateDragSpeed(int x, int y, long time) {
        dragBufferPos++;
        if (dragBufferPos >= DRAG_BUFFER_SIZE) {
            dragBufferPos = 0;
        }

        dragTimes[dragBufferPos] = time;
        dragBufferX[dragBufferPos] = x;
        dragBufferY[dragBufferPos] = y;
    }

    /**
     * Uses the recent history of drag events to calculate the velocity (in
     * pixels per second).
     * @param x the x coordinate of released point
     * @param y the y coordinate of released point
     * @param vertical if true, returns vertical velocity. Horizontal otherwise.
     */
    private int getDragVelocity(int[] dragBuffer) {

        int lastPos = dragBufferPos;
        int pos = lastPos;
        int firstPos = lastPos;
        long oldestTime = dragTimes[lastPos] - 200;

        while (dragTimes[pos] > 0 && dragTimes[pos] >= oldestTime) {
            firstPos = pos;

            pos--;
            if (pos < 0) {
                pos = DRAG_BUFFER_SIZE - 1;
            }

            if (pos == lastPos) {
                // Stop over-run
                break;
            }
        }

        // Elapsed time can't be zero... Arithmetic exception
        long elapsedTime = Math.max(dragTimes[lastPos] - dragTimes[firstPos], 1);
        int traveledPixels = dragBuffer[lastPos] - dragBuffer[firstPos];

        return (int) (traveledPixels * 1000 / elapsedTime);
    }

    protected int getDragVelocity(boolean horizontal) {
        return getDragVelocity(horizontal ? dragBufferX : dragBufferY);
    }

    protected void resetDragSpeed() {
        // Reset drag speed (0 is "stop" value)
        updateDragSpeed(0, 0, 0);

        // Stop animation...
        dragFriction = 1000;
        dragVelocityX = 0;
        dragVelocityY = 0;
    }

    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {

        Component view = getView();
        int viewY = view.getY();
        int viewX = view.getX();
        int viewHeight = view.getHeight();
        int viewWidth = view.getWidth();

        int viewPortX = getViewPortX();
        int viewPortY = getViewPortY();
        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);

        updateDragSpeed(pointX, pointY, System.currentTimeMillis());

        if (type == DesktopPane.PRESSED) {
            dragScrollBarMode = DRAG_NONE;
            dragFriction = 0;

            dragLastX = pointX;
            dragStartX = pointX;
            dragStartViewX = viewX - viewPortX;
            dragVelocityX = 0;
            dragTimeX = -1;

            dragLastY = pointY;
            dragStartY = pointY;
            dragStartViewY = viewY - viewPortY;
            dragVelocityY = 0;
            dragTimeY = -1;

            // Stop any "component resize" animation
            animateToFit(0, true);

            if (mode == MODE_SCROLLBARS) {

                int clickX = slider.doClickInScrollbar(
                      viewPortX,
                      viewPortY + viewPortHeight,
                      viewPortWidth,
                      height - viewPortY - viewPortHeight,
                      viewPortX-viewX,
                      viewPortWidth,
                      viewWidth,
                      pointX,pointY
                );

                dragVelocityX = doClickInScrollbar(clickX,DRAG_SLIDER_HORZ,viewPortWidth);

                int clickY = slider.doClickInScrollbar(
                        viewPortY,
                        viewPortX + viewPortWidth,
                        viewPortHeight,
                        width - viewPortX - viewPortWidth,
                        viewPortY - viewY,
                        viewPortHeight,
                        viewHeight,
                        pointY,pointX
                );

                dragVelocityY = doClickInScrollbar(clickY,DRAG_SLIDER_VERT,viewPortHeight);

            }
            else if (mode == MODE_SCROLLARROWS) {

                if (viewWidth > width) {
                    if (isPointInsideRect(pointX, pointY, 0, 0, barThickness, height)) {

                        dragVelocityX = 200;
                    }
                    else if (isPointInsideRect(pointX, pointY, width - barThickness, 0, barThickness, height)) {

                        dragVelocityX = -200;
                    }
                }

                if (viewHeight > height) {
                    if (isPointInsideRect(pointX, pointY, 0, 0, width, barThickness)) {

                        dragVelocityY = 200;
                    }
                    else if (isPointInsideRect(pointX, pointY, 0, height-barThickness, width, barThickness)) {

                        dragVelocityY = -200;
                    }
                }

                if (dragVelocityX != 0 || dragVelocityY != 0) {
                    dragScrollBarMode = DRAG_CLICKED_ARROW;
                }
            }

            // this is for when you click on the scrollbar arrows
            if (dragVelocityX != 0 || dragVelocityY != 0) {
                animateScrollPane(this);
            }
        }
        else if (type == DesktopPane.DRAGGED) {

            if (dragScrollBarMode == DRAG_SLIDER_HORZ) {
                dragLastX = slider.getNewValue(
                        viewPortX,
                        viewPortY + viewHeight,
                        viewPortWidth,
                        height - viewPortY - viewPortHeight,
                        dragStartX,
                        viewPortWidth,
                        viewWidth,
                        dragStartX-pointX
                  );
                dragScrollBarsSync(true);
            }
            else if (dragScrollBarMode == DRAG_SLIDER_VERT) {

                dragLastY = slider.getNewValue(
                        viewPortY,
                        viewPortX + viewWidth,
                        viewPortHeight,
                        width - viewPortX - viewPortWidth,
                        dragStartY,
                        viewPortHeight,
                        viewHeight,
                        dragStartY-pointY
                  );

                dragScrollBarsSync(true);
            }
            else if (dragScrollBarMode == DRAG_NONE) {
                if (getDesktopPane().IPHONE_SCROLL) {
                    dragLastX = pointX;
                    dragLastY = pointY;

                    dragScrollBarsSync(false);
                }
            }
        }
        else if (type == DesktopPane.RELEASED) {

            if (dragScrollBarMode == DRAG_NONE) {
                if (getDesktopPane().IPHONE_SCROLL) {
                    dragVelocityX = getDragVelocity(true);
                    dragVelocityY = getDragVelocity(false);
                }
                //System.out.println("SPEEDX = " + dragVelocityX + " SPEEDY = " + dragVelocityY);
            }
            else {
                // Stop animation...
                dragVelocityX = 0;
                dragVelocityY = 0;
            }

            dragFriction = 1000;
            animateScrollPane(this);

         // Reset drag speed (0 is "stop" value)
            updateDragSpeed(0, 0, 0);
        }
    }

    private void dragScrollBarsSync(boolean forceBound) {

        Component view = getView();
        int cX = view.getX() - getViewPortX();
        int cY = view.getY() - getViewPortY();
        int cW = view.getWidth();
        int cH = view.getHeight();

        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);

        boolean springBackLeft = ((bounceMode & BOUNCE_LEFT) == 0);
        boolean springBackRight = ((bounceMode & BOUNCE_RIGHT) == 0);
        boolean springBackTop = ((bounceMode & BOUNCE_TOP) == 0);
        boolean springBackBottom = ((bounceMode & BOUNCE_BOTTOM) == 0);

        if (cW > viewPortWidth || !springBackLeft || !springBackRight) {
            cX = dragScrollBarSync(forceBound,
                                   dragStartX, dragLastX, dragStartViewX,
                                   cX, cW, viewPortWidth,
                                   springBackLeft, springBackRight);
        }

        if (cH > viewPortHeight || !springBackTop || !springBackBottom) {
            cY = dragScrollBarSync(forceBound,
                                   dragStartY, dragLastY, dragStartViewY,
                                   cY, cH, viewPortHeight,
                                   springBackTop, springBackBottom);
        }

        makeVisible(cX, cY, viewPortWidth, viewPortHeight, false, false);
    }

    /**
     * this is called from the event thread
     * @return the new Y or X position of the view (the component inside the scrollpane)
     */
    private int dragScrollBarSync(boolean bound,
                                int dragStartY, int dragLastY, int dragStartViewY,
                                int cY, int cH, int viewPortHeight,
                                boolean springBackTop, boolean springBackBottom) {

        int diffBottomY = viewPortHeight - cY - cH;

        // How far are we from the desire position?
        int jumpY = (dragStartY - dragLastY) - (dragStartViewY - cY);

        if (jumpY != 0 || bound) {

            cY -= jumpY;
            diffBottomY += jumpY;

            if (cY >= 0) {
                if (bound) {
                    cY = 0;
                }
                else if (springBackTop) {
                    cY = cY / 2;
                }
            }
            else if (diffBottomY >= 0) {
                if (bound) {
                    cY += diffBottomY;
                }
                else if (springBackBottom) {
                    cY += diffBottomY / 2;
                }
            }
        }

        return -cY;
    }

    private boolean dragScrollBars(int time) {

        boolean endThread = (ScrollPane.dragScrollPane != this);

        if (animateToFit(time, endThread) && !endThread) {
            return true;
        }

        Component view = getView();
        int cX = view.getX() - getViewPortX();
        int cY = view.getY() - getViewPortY();
        int cW = view.getWidth();
        int cH = view.getHeight();

        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);

        boolean res = false;
        boolean forceBound = (dragScrollBarMode != DRAG_NONE || endThread);

        if ((cW == viewPortWidth) && (bounceMode & BOUNCE_HORIZONTAL) == 0) {
            dragVelocityX = 0;
        }

        if ((cH == viewPortHeight) && (bounceMode & BOUNCE_VERTICAL) == 0) {
            dragVelocityY = 0;
        }


        boolean springBackLeft = ((bounceMode & BOUNCE_LEFT) == 0);
        boolean springBackRight = ((bounceMode & BOUNCE_RIGHT) == 0);
        boolean springBackTop = ((bounceMode & BOUNCE_TOP) == 0);
        boolean springBackBottom = ((bounceMode & BOUNCE_BOTTOM) == 0);

        int[] newPosX = dragScrollBar(dragVelocityX, dragFriction, forceBound,
                                      cX, cW, viewPortWidth,
                                      time, dragTimeX,
                                      springBackLeft, springBackRight);
        cX = newPosX[0];
        dragVelocityX = newPosX[1];
        res |= (newPosX[2] != 0);
        dragTimeX = newPosX[3];


        int[] newPosY = dragScrollBar(dragVelocityY, dragFriction, forceBound,
                                      cY, cH, viewPortHeight,
                                      time, dragTimeY,
                                      springBackTop, springBackBottom);
        cY = newPosY[0];
        dragVelocityY = newPosY[1];
        res |= (newPosY[2] != 0);
        dragTimeY = newPosY[3];

        makeVisible(cX, cY, viewPortWidth, viewPortHeight, false, false);

        return endThread ? false : res;
    }

    private int[] dragScrollBar(int dragVelocityY, int dragFriction, boolean bound,
                                int cY, int cH, int viewPortHeight,
                                int time, int springBackTime,
                                boolean springBackTop, boolean springBackBottom) {

        int diffBottomY = viewPortHeight - cY - cH;


        int jumpY = 0;
        if (dragVelocityY != 0) {
            // Friction is always opposite to velocity, and never changes its direction
            int velocityInc = dragFriction / DRAG_FRAME_RATE;
            if (Math.abs(dragVelocityY) < velocityInc) {
                dragVelocityY = 0;
            }
            else if (dragVelocityY > 0) {
                dragVelocityY -= velocityInc;
            }
            else if (dragVelocityY < 0) {
                dragVelocityY += velocityInc;
            }

            jumpY = -(dragVelocityY / DRAG_FRAME_RATE);
            if (jumpY == 0) {
                dragVelocityY = 0;
            }
        }

        boolean springBack = (dragVelocityY == 0 && dragFriction != 0);
        if (springBack) {
            if (cY >= 0) {
                jumpY = cY;
            }
            else if (diffBottomY >= 0) {
                jumpY = -diffBottomY;
            }
        }

        if (springBack && jumpY != 0) {

            int[] vals = calculateSpringBack(time, springBackTime, jumpY);

            jumpY = jumpY - vals[0];
            springBackTime = vals[1];
        }

        if (jumpY != 0 || bound) {

            cY -= jumpY;
            diffBottomY += jumpY;

            if (!springBack) {
                final int MAX_SPRING = (viewPortHeight / 4);

                if (cY >= 0) {
                    if (bound) {
                        cY = 0;
                        jumpY = 0;
                    }
                    else if (dragVelocityY != 0 && cY >= MAX_SPRING && springBackTop) {
                        dragVelocityY = 0;
                        cY = MAX_SPRING;
                    }
                }
                else if (diffBottomY >= 0) {
                    if (bound) {
                        cY += diffBottomY;
                        jumpY = 0;
                    }
                    else if (dragVelocityY != 0 && diffBottomY >= MAX_SPRING && springBackBottom) {
                        dragVelocityY = 0;
                        cY += diffBottomY - MAX_SPRING;
                    }
                }
            }
        }

        return new int[] {-cY, dragVelocityY, jumpY, springBackTime};
    }

    private int[] calculateSpringBack(int time, int springBackTime, int dist) {
        // Quadratic motion equations:
        // (1) position = acceleration * time * time;
        // (2) time = sqrt(position / acceleration)
        // (3) acceleration = distance / (time * time)

        // Acceleration (3): Is a constant
        // Time is in milli's... Drag needs to be divided by (1000 * 1000)
        final float springDrag = 300.0f / (1000 * 1000);

        // Animation runs "backward"... De-accelerates, and ends at zero.
        if (springBackTime < 0) {
            // When would it finish, if it run forward? (2)
            springBackTime = time + (int)(Math.sqrt(Math.abs(dist) / springDrag));
        }

        // Animation runs "backward"... Re-map time.
        time = springBackTime - time;

        int newDist = (time >= 0) ? (int)(springDrag * time * time) : 0;
        if (dist < 0) {
            // If animate bottom,
            newDist = -newDist;
        }

        // New dist can never be bigger than the initial dist...
        if (newDist == dist && newDist != 0) {
            newDist = (dist > 0) ? dist - 1 : dist + 1;
        }
        else if (Math.abs(newDist) > Math.abs(dist)) {
            newDist = dist;
        }

        return new int[] {newDist, springBackTime};
    }

    private boolean animateToFit(int time, boolean force) {

        Component view = getView();
        int cW = view.getWidth();
        int cH = view.getHeight();
        int cX = view.getX();
        int cY = view.getY();

        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);
        int viewPortX = getViewPortX();
        int viewPortY = getViewPortY();

        int diffW = (viewPortWidth - cW);
        int diffH = (viewPortHeight - cH);
        int viewPosX = view.getX();
        int viewPosY = view.getY();

        if (!force && diffW > 0 && diffH > 0) {

            int minDiff = (diffW < diffH) ? diffW : diffH;
            double ratioW = (diffW < diffH) ? 1.0 : (viewPortWidth / (double)viewPortHeight);
            double ratioH = (diffW < diffH) ? (viewPortHeight / (double)viewPortWidth) : 1.0;

            int[] vals = calculateSpringBack(time, fitSizeTime, minDiff);

            int newMaxDiff = vals[0];
            fitSizeTime = vals[1];

            view.width = (int) (viewPortWidth - newMaxDiff * ratioW);
            view.height = (int) (viewPortHeight - newMaxDiff * ratioH);

            viewPosX = viewPortX + (viewPortWidth - view.width) / 2;
            viewPosY = viewPortY + (viewPortHeight - view.height) / 2;
        }
        else {
            if (diffW > 0) {
                view.width = viewPortWidth;
                viewPosX = viewPortX;
            }

            if (diffH > 0) {
                view.height = viewPortHeight;
                viewPosY = viewPortY;
            }
        }

        view.setLocation(viewPosX, viewPosY);

        boolean res = (cW != view.getWidth() || cH != view.getHeight() ||
                       cX != view.getX() || cY != view.getY());
        if (res) {
            repaint();
        }

        return res;
    }

    public void animateToFit(boolean startAnimation) {
        // Stop any animation and "springBack".
        dragVelocityX = 0;
        dragVelocityY = 0;
        dragFriction = 1000;
        resetDragSpeed();

        // Start new animation
        fitSizeTime = -1;
        animateScrollPane(startAnimation ? this : null);
    }

    // Can be overloaded by sub-classes
    protected void setViewLocation(int viewX, int viewY) {
        getView().setLocation(viewX, viewY);
    }
}
