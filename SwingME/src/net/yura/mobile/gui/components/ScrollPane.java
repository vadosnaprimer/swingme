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

    public static final int MINIMUM_THUMB_SIZE=5;

    public static final int MODE_NONE=-1;
    public static final int MODE_SCROLLBARS=0;
    public static final int MODE_SCROLLARROWS=1;
    public static final int MODE_INDICATOR=2;

    private int mode;
    private int barThickness;

    private Icon thumbTop;
    private Icon thumbBottom;
    private Icon thumbFill;
    private Icon trackTop;
    private Icon trackBottom;
    private Icon trackFill;

    private Icon rightArrow;
    private Icon leftArrow;
    private Icon upArrow;
    private Icon downArrow;


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
        setMode(m);
        super.setName("ScrollPane");
    }

    public void setLayout(Layout lt) {

        if (lt!=null) throw new IllegalArgumentException();

    }

    public ScrollPane(Component view,int a) {
        this(a);
        add(view);
    }

    public void setMode(int m) {

        mode = m;
    }

    public void setSize(int w, int h) {

        switch (mode) {
            case MODE_SCROLLBARS: barThickness = getBarThickness(); break;
            case MODE_SCROLLARROWS: // fall though
            case MODE_INDICATOR: barThickness = (rightArrow != null) ? rightArrow.getIconWidth() : 0; break;
            case MODE_NONE: barThickness = 0; break;
            default: throw new RuntimeException();
        }

        super.setSize(w, h);

        // Size of the scroll changed, we need to reset the component location
        // this is NOT how Swing does it, need to find a better method!
        //getComponent().setLocation(getViewPortX(), getViewPortY());
    }

    public int getBarThickness() {

        return (trackTop != null) ? trackTop.getIconWidth() : 0;

    }

    /**
     * @see javax.swing.JViewport#getView() JViewport.getView
     */
    public Component getView() {

        return ((Component)getComponents().elementAt(0));

    }

    public void add(Component a) {

        removeAll();

        super.add(a);
        //component = a;
        //a.setOwner(owner);
        //if (a instanceof Panel) {
        //    ((Panel)a).setScrollPanel(this);
        //}

        a.setLocation(getViewPortX(), getViewPortY());

        // TODO does it take into account the border?
    }

    public void add(Component component,String constraint){
        throw new RuntimeException("must use add");
    }

    public boolean makeVisible(int x,int y,int w,int h,boolean smartscroll) {
        return makeVisible(x, y, w, h, smartscroll, true);
    }

    private boolean makeVisible(int x,int y,int w,int h,boolean smartscroll, boolean bond) {

        Component component = getView();
        int oldX = component.getX();
        int oldY = component.getY();

        //Logger.debug("x="+x+" y="+y+" w="+w+" h="+h);
        //Logger.debug("viewPortX="+viewPortX+" viewPortY="+viewPortY+" width="+width+" height="+height);

        int right = x+w;
        int bottom = y+h;

        int componentX = -component.getX();
        int componentY = -component.getY();

        int viewX=getViewPortX();
        int viewY=getViewPortY();
        int viewHeight = getViewPortHeight();
        int viewWidth = getViewPortWidth(viewHeight);

        // check if the viewport is maybe already looks at part of this bigger area
        // if it is, then we dont want to scroll to any part of it
        if (!(x<=componentX+viewX && right>=componentX+viewX+viewWidth)) {
            if (right > (viewX + componentX + viewWidth)){
                componentX = right - viewWidth;
            }

            if (x < (viewX + componentX)){
                componentX = x-viewX;
            }
        }

        if (!(y<=componentY+viewY && bottom>=componentY+viewY+viewHeight)) {
            if (bottom > (viewY + componentY + viewHeight)){
                componentY = bottom - viewHeight;
            }

            if (y < (viewY + componentY)){
                componentY = y-viewY;
            }
        }




        // check we r not scrolling off the content panel
        if (bond) {
            componentX = Math.min(componentX, component.getWidth() - viewWidth - viewX);
            componentY = Math.min(componentY, component.getHeight() - viewHeight - viewY);

            componentX = Math.max(componentX, -viewX);
            componentY = Math.max(componentY, -viewY);
        }

        int xdiff=-componentX -component.getX();
        int ydiff=-componentY -component.getY();

        boolean goodscroll=true;

        if (smartscroll) {

            if (Math.abs(xdiff) > viewWidth) {

                xdiff = (xdiff>0)?viewWidth*2/3:-viewWidth*2/3;

                goodscroll = false;

            }

            if (Math.abs(ydiff) > viewHeight) {

                ydiff = (ydiff>0)?viewHeight*2/3:-viewHeight*2/3;

                goodscroll = false;

            }
        }

        component.setLocation( component.getX()+xdiff , component.getY()+ydiff );

        // NEVER CALL setBounds here as it will call setSize and that will cause a revalidate
        //component.setBounds(15, 15, component.getWidth(), component.getHeight());

        //Logger.debug("new pos: x="+component.getX()+" y="+component.getY() );

        // only repint if we have moved
        if (oldX!=component.getX() || oldY!=component.getY()) {
            repaint();
        }

        return goodscroll;

    }

    /**
     * check if something is currently visible or as visible as it can be
     * i.e. scrolling wont make it more visible, even if its not all currently visible
     */
    public boolean isRectVisible(int x,int y,int w,int h) {

        Component component = getView();
        int viewX= -component.getX() + getViewPortX();
        int viewY= -component.getY() + getViewPortY();
        int viewHeight = getViewPortHeight();
        int viewWidth = getViewPortWidth(viewHeight);

        return ( ((x>=viewX && x+w<=viewX+viewWidth)||(x<=viewX && x+w>=viewX+viewWidth)) &&
                ((y>=viewY && y+h<=viewY+viewHeight)||(y<=viewY && y+h>=viewY+viewHeight))
        );
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

    public void workoutMinimumSize() {

        super.workoutMinimumSize();
        width = getView().getWidthWithBorder();
        height = getView().getHeightWithBorder();
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

            if (comp.getX() > getViewPortX()) {
                comp.setLocation(getViewPortX(), comp.getY());
            }
            if (comp.getY() > getViewPortY()) {
                comp.setLocation(comp.getX(), getViewPortY());
            }
            if ((cw - getViewPortX() + comp.getX()) < getViewPortWidth()) {
                comp.setLocation(-cw +getViewPortX() +getViewPortWidth(), comp.getY());
            }
            if ((ch - getViewPortY() + comp.getY()) < getViewPortHeight()) {
                comp.setLocation(comp.getX(),-ch +getViewPortY() +getViewPortHeight());
            }

        }

    }

    public void paintChildren(Graphics2D g) {

        int[] a=g.getClip();

        int viewX=getViewPortX();
        int viewY=getViewPortY();
        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);

        // dont care about clipping for the
        // scrollbars as they r painted over the top
        g.clipRect(viewX, viewY, viewWidth, viewHeight);

        super.paintChildren(g);

        g.setClip(a);

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
        int viewWidth = getView().getWidth();
        int viewHeight = getView().getHeight();


        // NEEDS to be same check as in getViewPortWidth
        if ( viewHeight > viewPortHeight ) { // vertical
            drawScrollBar(g,
                    viewPortX + viewPortWidth,
                    viewPortY,
                    width - viewPortX - viewPortWidth,
                    viewPortHeight,
                    viewPortY-getView().getY(),
                    viewPortHeight,
                    viewHeight
            );

        }

        // NEEDS to be same check as in getViewPortHeight
        if ( viewWidth > (width-viewPortX) ) { // horizontal

            int t = g.getTransform();
            g.setTransform( Sprite.TRANS_MIRROR_ROT270 );

            drawScrollBar(g,
                    viewPortY + viewPortHeight,
                    viewPortX,
                    height - viewPortY - viewPortHeight,
                    viewPortWidth,
                    viewPortX-getView().getX(),
                    viewPortWidth,
                    viewWidth
            );

            g.setTransform( t );
        }


    }

    /**
     * @see javax.swing.JScrollBar#JScrollBar(int, int, int, int, int) JScrollBar.JScrollBar
     */
    public void drawScrollBar(Graphics2D g, int x,int y,int w,int h,int value,int extent, int max) {

        int starty = 0;
        int extenth = h;

        // DRAW ARROWS
        //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,

        if (trackTop!=null) {

            int x1 = x + (w-trackTop.getIconWidth())/2;
            trackTop.paintIcon(this, g, x1, y);
            trackBottom.paintIcon(this, g, x1, y+h-trackBottom.getIconHeight() );

            starty = trackTop.getIconHeight();
            extenth = h - starty - trackBottom.getIconHeight();

        }

        // draw the track fill color
        //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,

        if (trackFill!=null) {
            tileIcon(g, trackFill, x + (w-trackFill.getIconWidth())/2 , starty, trackFill.getIconWidth(), extenth);
        }

        // draw the thumb!
        //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,

        int[] tmp = getOffsets(x,y,w,h,value,extent,max);
        starty = tmp[1];
        extenth = tmp[2];

        if (thumbTop!=null) {
            int x1 = x + (w-thumbTop.getIconWidth())/2;
            thumbTop.paintIcon(this, g, x1, starty);
            thumbBottom.paintIcon(this, g, x1, starty+extenth-thumbBottom.getIconHeight());
            starty = starty + thumbTop.getIconHeight();
            extenth = extenth - (thumbTop.getIconHeight()+thumbBottom.getIconHeight());
        }

        if (thumbFill!=null) {
            tileIcon(g, thumbFill, x + (w-thumbFill.getIconWidth())/2 , starty, thumbFill.getIconWidth(), extenth);
        }

    }

    /**
     * @param x - Ignored?
     * @param y - Start of bar
     * @param w - width of bar
     * @param h - viewPort Height/Width
     * @param value - Desired view X/Y
     * @param extent - viewPort Height/Width > same as h?
     * @param max - view Height/Width
     * @return
     */
    private int[] getOffsets(int x,int y,int w, int h, int value,int extent, int max) {

        final int box;
        final int topBotton;
        if (trackTop!=null) {
            box = (trackTop.getIconHeight() >w)?w:trackTop.getIconHeight();
            topBotton = (thumbTop==null)?0:thumbTop.getIconHeight()+thumbBottom.getIconHeight();
        }
        else {
            box = 0;
            topBotton = 0;
        }
        final int space1 = h - box * 2;

        int extenth = (int) ( (extent*space1)/(double)max + 0.5);
        int min = (topBotton<MINIMUM_THUMB_SIZE)?MINIMUM_THUMB_SIZE:topBotton;
        min = min>(space1/2)?space1/2:min;

        int space = space1;
        if (extenth < min) {
            extenth = min;
            space = space-extenth;
            max = max-extent;
        }
        int starty = box+ (int)( (space*value)/(double)max + 0.5 );

        // make sure the thumb value is bound
        /*
         // this works but is not that nice
        if (starty < box) {
            starty = box;
        }
        else if ((starty+extenth) > (box+space1)) {
            starty = box + space1 - extenth;
        }
         */
        // add squidge!
        if ((starty+extenth) < (box+min)) {
            starty = box;
            extenth = min;
        }
        else if (starty < box) {
            extenth = starty+extenth-box;
            starty = box;
        }
        else if (starty > (box+space1-min)) {
            starty = box+space1-min;
            extenth = min;
        }
        else if ((starty+extenth) > (box+space1)) {
            extenth = box+space1-starty;
        }

        return new int[] {box,y+starty,extenth};
    }

    private void tileIcon(Graphics2D g, Icon icon,int dest_x,int dest_y,int dest_w,int dest_h) {
        int h = icon.getIconHeight();

        final int[] c = g.getClip();

        g.clipRect(dest_x,dest_y,dest_w,dest_h);

        for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+h) {
            icon.paintIcon(this, g, dest_x, pos_y);
        }

        icon.paintIcon(this, g, 0, 0);

        g.setClip(c);

    }


    private void drawScrollArrows(final Graphics2D g,boolean indicator) {

        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);

        int viewX=getViewPortX();
        int viewY=getViewPortY();

        int d = barThickness*2;
        int gap=2;
        boolean canScroll;

        if (getView().getWidth() > width) {

            canScroll = getView().getX() < viewX;

            if (indicator) {
                drawArrow(g, width/2 -gap-d, height+(d+3*gap)/2-barThickness, barThickness, d,canScroll,Graphics.LEFT);
            }
            else {
                drawArrow(g, 0, (height-d)/2, barThickness, d,canScroll,Graphics.LEFT);
            }

            canScroll = (getView().getWidth()+getView().getX()-viewX) > viewWidth;

            if (indicator) {
                drawArrow(g, width/2 +gap+barThickness, height+(d+3*gap)/2-barThickness, barThickness, d,canScroll,Graphics.RIGHT);
            }
            else {
                drawArrow(g, width - barThickness , (height-d)/2, barThickness, d,canScroll,Graphics.RIGHT);
            }

        }

        if (getView().getHeight() > height) {

            canScroll = getView().getY() < viewY;

            if (indicator) {
                drawArrow(g, (width-d)/2, height+gap, d, barThickness,canScroll,Graphics.TOP);
            }
            else {
                drawArrow(g, (width-d)/2, 0, d, barThickness,canScroll,Graphics.TOP);
            }

            canScroll = (getView().getHeight()+getView().getY()-viewY) > viewHeight;

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


    public void clip(Graphics2D g) {

        int viewX=getXOnScreen()+getViewPortX();
        int viewY=getYOnScreen()+getViewPortY();
        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);

        g.clipRect(viewX, viewY, viewWidth, viewHeight);
        super.clip(g);
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


        thumbTop = (Icon)theme.getProperty("thumbTop", Style.ALL);
        thumbBottom = (Icon) theme.getProperty("thumbBottom", Style.ALL);
        thumbFill = (Icon)theme.getProperty("thumbFill", Style.ALL);
        trackTop = (Icon) theme.getProperty("trackTop", Style.ALL);
        trackBottom = (Icon) theme.getProperty("trackBottom", Style.ALL);
        trackFill = (Icon)theme.getProperty("trackFill", Style.ALL);



        rightArrow = (Icon)theme.getProperty("rightArrow", Style.ALL);
        leftArrow = (Icon)theme.getProperty("leftArrow", Style.ALL);
        upArrow = (Icon)theme.getProperty("upArrow", Style.ALL);
        downArrow = (Icon)theme.getProperty("downArrow", Style.ALL);
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

    private boolean isPointInsideRect(int x,int y,int recX,int recY,int recWidth,int recHeight) {

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

    // Keep a history of drag events, so we can later calculate the average speed.
    // These are circular buffers
    private long[] dragTimes = new long[DRAG_BUFFER_SIZE];
    private int[] dragBufferX = new int[DRAG_BUFFER_SIZE];
    private int[] dragBufferY = new int[DRAG_BUFFER_SIZE];

    // Index of the most recent entry.
    private int dragBufferPos;

    private int doClickInScrollbar(int x1,int y1,int w1,int h1,int value1,int extent1, int max1,
            int pointX,int pointY,
            int dragSlider)
    {
        if (dragScrollBarMode != DRAG_NONE) {
            return 0;
        }

        int[] tmp = getOffsets(x1,y1,w1,h1,value1,extent1,max1);
        int box = tmp[0];
        int starty = tmp[1];
        int extenth = tmp[2];

        int dragMode;
        int velocity;
        if (isPointInsideRect(pointX, pointY, x1, y1, w1, box)) {

            dragMode = DRAG_CLICKED_ARROW;
            velocity = 200;
        }
        else if (isPointInsideRect(pointX, pointY, x1, y1+box, w1, starty-y1-box)) {

            dragMode = DRAG_CLICKED_TRACK;
            velocity = extent1 * DRAG_FRAME_RATE;
        }
        else if (isPointInsideRect(pointX, pointY, x1, starty ,w1,extenth)) { // thumb on the right

            velocity = 0;
            dragMode = dragSlider;
        }
        else if (isPointInsideRect(pointX, pointY, x1, starty+extenth, w1, h1 - box - (starty-y1) - extenth)) {

            dragMode = DRAG_CLICKED_TRACK;
            velocity = -extent1 * DRAG_FRAME_RATE;
        }
        else if (isPointInsideRect(pointX, pointY, x1, y1+h1-box, w1, box)) {

            dragMode = DRAG_CLICKED_ARROW;
            velocity = -200;
        }
        else {
            velocity = 0;
            dragMode = DRAG_NONE;
        }

        dragScrollBarMode = dragMode;

        return velocity;
    }

    private synchronized static void setDragScrollPane(ScrollPane dragScrollPane) {
        boolean startThread = (ScrollPane.dragScrollPane != dragScrollPane);

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

        while (true) {

            synchronized (ScrollPane.class) {

                //System.out.println("- ScrollPane Animate -");

                long time = System.currentTimeMillis();
                boolean animate = dragScrollBars((int)(time - startAnimTime));
                if (!animate) {
                    if (ScrollPane.dragScrollPane == this) {
                        ScrollPane.dragScrollPane = null;
                    }
                    break;
                }

                int rate = (dragScrollBarMode == DRAG_CLICKED_TRACK) ? DRAG_PAGE_RATE : DRAG_FRAME_RATE;
                long timeToWait = time - System.currentTimeMillis() + (1000L / rate);
                if (timeToWait > 0) {

                    //System.out.println("--- ScrollPane timeToWait = " + timeToWait);

                    try {
                        ScrollPane.class.wait(timeToWait);
                    }
                    catch(InterruptedException e) {
                      Logger.info(e);
                    }
                }
            }
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

        final long OLDEST_TIME = System.currentTimeMillis() - 200;

        int lastPos = dragBufferPos;
        int pos = lastPos;
        int firstPos;

        do {
            firstPos = pos;

            pos--;
            if (pos < 0) {
                pos = DRAG_BUFFER_SIZE - 1;
            }
        } while (dragTimes[pos] >= OLDEST_TIME && pos != lastPos);


        // Elapsed time can't be zero... Arithmetic exception
        long elapsedTime = Math.max(dragTimes[lastPos] - dragTimes[firstPos], 1);
        int traveledPixels = dragBuffer[lastPos] - dragBuffer[firstPos];

        return (int) (traveledPixels * 1000 / elapsedTime);
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

            if (mode == MODE_SCROLLBARS) {

                dragVelocityX = doClickInScrollbar(
                      viewPortY + viewPortHeight,
                      viewPortX,
                      height - viewPortY - viewPortHeight,
                      viewPortWidth,
                      viewPortX-viewX,
                      viewPortWidth,
                      viewWidth,
                      pointY,pointX,
                      DRAG_SLIDER_HORZ
                );

                dragVelocityY = doClickInScrollbar(
                        viewPortX + viewPortWidth,
                        viewPortY,
                        width - viewPortX - viewPortWidth,
                        viewPortHeight,
                        viewPortY - viewY,
                        viewPortHeight,
                        viewHeight,
                        pointX, pointY,
                        DRAG_SLIDER_VERT
                );
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
                setDragScrollPane(this);
            }
        }
        else if (type == DesktopPane.DRAGGED) {

            if (dragScrollBarMode == DRAG_SLIDER_HORZ) {
                dragLastX = getNewValue(
                        viewPortY + viewHeight,
                        viewPortX,
                        height - viewPortY - viewPortHeight,
                        viewPortWidth,
                        dragStartX,
                        viewPortWidth,
                        viewWidth,
                        dragStartX-pointX
                  );
                dragScrollBarsSync(true);
            }
            else if (dragScrollBarMode == DRAG_SLIDER_VERT) {

                dragLastY = getNewValue(
                        viewPortX + viewWidth,
                        viewPortY,
                        width - viewPortX - viewPortWidth,
                        viewPortHeight,
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
                    dragVelocityX = getDragVelocity(dragBufferX);
                    dragVelocityY = getDragVelocity(dragBufferY);
                }
                //System.out.println("SPEEDX = " + dragVelocityX + " SPEEDY = " + dragVelocityY);
            }
            else {
                // Stop animation...
                dragVelocityX = 0;
                dragVelocityY = 0;
            }

            dragFriction = 1000;
            setDragScrollPane(this);

            // Reset drag speed (0 is a very "old" time)
            updateDragSpeed(pointX, pointY, 0);
        }
    }

    private void dragScrollBarsSync(boolean forceBound) {

        int cX = getView().getX() - getViewPortX();
        int cY = getView().getY() - getViewPortY();
        int cW = getView().getWidth();
        int cH = getView().getHeight();

        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);

        if (cW > viewPortWidth) {
            cX = dragScrollBarSync(forceBound,
                                   dragStartX, dragLastX, dragStartViewX,
                                   cX, cW, viewPortWidth);
        }

        if (cH > viewPortHeight) {
            cY = dragScrollBarSync(forceBound,
                                   dragStartY, dragLastY, dragStartViewY,
                                   cY, cH, viewPortHeight);
        }

        makeVisible(cX, cY, viewPortWidth, viewPortHeight, false, false);
    }

    /**
     * this is called from the event thread
     * @return the new Y or X position of the view (the component inside the scrollpane)
     */
    private int dragScrollBarSync(boolean bound,
                                int dragStartY, int dragLastY, int dragStartViewY,
                                int cY, int cH, int viewPortHeight) {

        int diffBottomY =  viewPortHeight - cY - cH;

        // How far are we from the desire position?
        int jumpY = (dragStartY - dragLastY) - (dragStartViewY - cY);

        if (jumpY != 0 || bound) {

            cY -= jumpY;
            diffBottomY += jumpY;

            if (cY >= 0) {
                if (bound) {
                    cY = 0;
                }
                else {
                    cY = cY / 2;
                }
            }
            else if (diffBottomY >= 0) {
                if (bound) {
                    cY += diffBottomY;
                }
                else {
                    cY += diffBottomY / 2;
                }
            }
        }

        return -cY;
    }

    private boolean dragScrollBars(int time) {
        boolean res = false;

        int cX = getView().getX() - getViewPortX();
        int cY = getView().getY() - getViewPortY();
        int cW = getView().getWidth();
        int cH = getView().getHeight();

        int viewPortHeight = getViewPortHeight();
        int viewPortWidth = getViewPortWidth(viewPortHeight);

        boolean forceBound = (dragScrollBarMode != DRAG_NONE ||
                             (ScrollPane.dragScrollPane != null && ScrollPane.dragScrollPane != this));

        if (cW > viewPortWidth) {
            int[] newPosX = dragScrollBar(dragVelocityX, dragFriction, forceBound,
                                          cX, cW, viewPortWidth,
                                          time, dragTimeX);
            cX = newPosX[0];
            dragVelocityX = newPosX[1];
            res |= (newPosX[2] != 0);
            dragTimeX = newPosX[3];
        }

        if (cH > viewPortHeight) {
            int[] newPosY = dragScrollBar(dragVelocityY, dragFriction, forceBound,
                                          cY, cH, viewPortHeight,
                                          time, dragTimeY);
            cY = newPosY[0];
            dragVelocityY = newPosY[1];
            res |= (newPosY[2] != 0);
            dragTimeY = newPosY[3];
        }

        makeVisible(cX, cY, viewPortWidth, viewPortHeight, false, false);

        return res;
    }

    private int[] dragScrollBar(int dragVelocityY, int dragFriction, boolean bound,
                                int cY, int cH, int viewPortHeight,
                                int time, int springBackTime) {

        int diffBottomY =  viewPortHeight - cY - cH;


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
                springBackTime = time + (int)(Math.sqrt(Math.abs(jumpY) / springDrag));
            }

            // Animation runs "backward"...  Re-map time.
            time = springBackTime - time;
            int newCy = (int)(springDrag * time * time);
            if (jumpY < 0) {
                // If animate bottom,
                newCy = -newCy;
            }

            int newJump = jumpY - newCy ;
            // New jump can never be zero, or bigger than the initial jump...
            if (newJump == 0) {
                jumpY = (jumpY < 0) ? -1 : 1;
            }
            else if (Math.abs(newJump) < Math.abs(jumpY)) {
                jumpY = newJump;
            }
        }

        if (jumpY != 0 || bound) {

            cY -= jumpY;
            diffBottomY += jumpY;

            if (!springBack) {
                final int MAX_SPRING = (viewPortHeight / 4);

                if (cY >= 0) {
                    if (bound) {
                        jumpY = 0;
                        cY = 0;
                    }
                    else if (dragVelocityY != 0 && cY >= MAX_SPRING) {
                        dragVelocityY = 0;
                        cY = MAX_SPRING;
                    }
                }
                else if (diffBottomY >= 0) {
                    if (bound) {
                        jumpY = 0;
                        cY += diffBottomY;
                    }
                    else if (dragVelocityY != 0 && diffBottomY >= MAX_SPRING) {
                        dragVelocityY = 0;
                        cY += diffBottomY - MAX_SPRING;
                    }
                }
            }
        }

        return new int[] {-cY, dragVelocityY, jumpY, springBackTime};
    }

    private int getNewValue(int x,int y,int w,int h,int value,int extent, int max,int pixels) {

        int[] offsets = getOffsets(x, y, w, h, 0, extent, max);

        return value + ((max-extent)*  pixels)/ (h - offsets[0]*2 - offsets[2]);
    }
}
