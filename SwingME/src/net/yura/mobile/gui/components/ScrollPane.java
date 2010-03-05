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

    public Component getComponent() {

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

        Component component = getComponent();
        int oldX = component.getX();
        int oldY = component.getY();

        //System.out.println("x="+x+" y="+y+" w="+w+" h="+h);
        //System.out.println("viewPortX="+viewPortX+" viewPortY="+viewPortY+" width="+width+" height="+height);

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
        if ((viewX+componentX+viewWidth)>component.getWidth()) { componentX=component.getWidth()-viewWidth-viewX; }
        if ((viewY+componentY+viewHeight)>component.getHeight()) { componentY=component.getHeight()-viewHeight-viewY; }
        if (componentX<-viewX) { componentX=-viewX; }
        if (componentY<-viewY) { componentY=-viewY; }

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

        //System.out.println("new pos: x="+component.getX()+" y="+component.getY() );

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

        Component component = getComponent();
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
            case MODE_SCROLLBARS: return height-getViewPortY()-((getComponent().getWidth()> (width-getViewPortX()) )?barThickness:0);
            case MODE_SCROLLARROWS: return (getComponent().getHeight() > height)?height-(barThickness*2):height;
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
            case MODE_SCROLLBARS: return width-getViewPortX()-((getComponent().getHeight()>vph)?barThickness:0);
            case MODE_SCROLLARROWS: return (getComponent().getWidth() > width)?width-(barThickness*2):width;
            case MODE_NONE:
            case MODE_INDICATOR: return width;
            default: throw new RuntimeException();
        }
    }
    public int getViewPortX() {
        switch (mode) {
            case MODE_SCROLLARROWS: return (getComponent().getWidth() > width)?barThickness:0;
            case MODE_SCROLLBARS:
            case MODE_NONE:
            case MODE_INDICATOR: return 0;
            default: throw new RuntimeException();
        }
    }
    public int getViewPortY() {
        switch (mode) {
            case MODE_SCROLLARROWS: return (getComponent().getHeight() > height)?barThickness:0;
            case MODE_SCROLLBARS:
            case MODE_NONE:
            case MODE_INDICATOR: return 0;
            default: throw new RuntimeException();
        }
    }

    public void workoutMinimumSize() {

        super.workoutMinimumSize();
        width = getComponent().getWidthWithBorder();
        height = getComponent().getHeightWithBorder();
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

            Component comp = getComponent();

            int viewHeight=getViewPortHeight();
            int viewWidth=getViewPortWidth(viewHeight);
            int cw = comp.getWidth();
            int ch = comp.getHeight();
/* this is another solution
            // we need to pass
            if (mode == MODE_SCROLLBARS && ch > viewHeight && cw < viewWidth ) {
System.out.println("size1 "+ viewWidth+" "+ ch);
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

//System.out.println("size2 "+ cw+" "+ ch);
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

        int viewX = getViewPortX();
        int viewY = getViewPortY();
        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);
        int componentWidth = getComponent().getWidth();
        int componentHeight = getComponent().getHeight();


        // NEEDS to be same check as in getViewPortWidth
        if ( componentHeight > viewHeight ) { // vertical

            drawScrollBar(g,
                    viewX + viewWidth,
                    viewY,
                    width - viewX - viewWidth,
                    viewHeight,
                    viewY-getComponent().getY(),
                    viewHeight,
                    componentHeight
            );

        }

        // NEEDS to be same check as in getViewPortHeight
        if ( componentWidth > (width-viewX) ) { // horizontal

            int t = g.getTransform();
            g.setTransform( Sprite.TRANS_MIRROR_ROT270 );

            drawScrollBar(g,
                    viewY + viewHeight,
                    viewX,
                    height - viewY - viewHeight,
                    viewWidth,
                    viewX-getComponent().getX(),
                    viewWidth,
                    componentWidth
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
        //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,

        if (trackTop!=null) {

            int x1 = x + (w-trackTop.getIconWidth())/2;
            trackTop.paintIcon(this, g, x1, y);
            trackBottom.paintIcon(this, g, x1, y+h-trackBottom.getIconHeight() );

            starty = trackTop.getIconHeight();
            extenth = h - starty - trackBottom.getIconHeight();

        }

        // draw the track fill color
        //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,

        if (trackFill!=null) {
            tileIcon(g, trackFill, x + (w-trackFill.getIconWidth())/2 , starty, trackFill.getIconWidth(), extenth);
        }

        // draw the thumb!
        //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,

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

    private int[] getOffsets(int x,int y,int w, int h, int value,int extent, int max) {

        int box = 0;
        int topBotton = 0;
        if (trackTop!=null) {
            box = (trackTop.getIconHeight() >w)?w:trackTop.getIconHeight();
            topBotton = (thumbTop==null)?0:thumbTop.getIconHeight()+thumbBottom.getIconHeight();
        }

        int space = h - box * 2;
        int extenth = (int) ( (extent*space)/(double)max + 0.5);

        int min = (topBotton<MINIMUM_THUMB_SIZE)?MINIMUM_THUMB_SIZE:topBotton;
        min = min>(space/2)?space/2:min;

        if (extenth < min) {
            extenth = min;
            space = space-extenth;
            max = max-extent;
        }

        int starty = y+box+ (int)( (space*value)/(double)max + 0.5 );

        return new int[] {box,starty,extenth};
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

        if (getComponent().getWidth() > width) {

            canScroll = getComponent().getX() < viewX;

            if (indicator) {
                drawArrow(g, width/2 -gap-d, height+(d+3*gap)/2-barThickness, barThickness, d,canScroll,Graphics.LEFT);
            }
            else {
                drawArrow(g, 0, (height-d)/2, barThickness, d,canScroll,Graphics.LEFT);
            }

            canScroll = (getComponent().getWidth()+getComponent().getX()-viewX) > viewWidth;

            if (indicator) {
                drawArrow(g, width/2 +gap+barThickness, height+(d+3*gap)/2-barThickness, barThickness, d,canScroll,Graphics.RIGHT);
            }
            else {
                drawArrow(g, width - barThickness , (height-d)/2, barThickness, d,canScroll,Graphics.RIGHT);
            }

        }

        if (getComponent().getHeight() > height) {

            canScroll = getComponent().getY() < viewY;

            if (indicator) {
                drawArrow(g, (width-d)/2, height+gap, d, barThickness,canScroll,Graphics.TOP);
            }
            else {
                drawArrow(g, (width-d)/2, 0, d, barThickness,canScroll,Graphics.TOP);
            }

            canScroll = (getComponent().getHeight()+getComponent().getY()-viewY) > viewHeight;

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
        if (!mine) {
            return getComponent().isOpaque();
        }
        else {
            return mine;
        }
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

        int viewX=getViewPortX();
        int viewY=getViewPortY();
        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);

        if ( isPointInsideRect(x,y,viewX,viewY,viewWidth,viewHeight) ) {

            return super.getComponentAt(x,y);

        }

        return this;
    }

    private boolean isPointInsideRect(int x,int y,int viewX,int viewY,int viewWidth,int viewHeight) {

        return x>=viewX && x<(viewX+viewWidth) && y>=viewY && y<(viewY+viewHeight);

    }

    public void doClickInScrollbar(int x1,int y1,int w1,int h1,int value1,int extent1, int max1,
            int pointX,int pointY,
            int d1,int d2,int d3
    ) {

        int[] tmp = getOffsets(x1,y1,w1,h1,value1,extent1,max1);
        int box = tmp[0];
        int starty = tmp[1];
        int extenth = tmp[2];


        if ( isPointInsideRect(     pointX, pointY, x1, y1, w1, box ) ) {

            go = true;
            direction = d1;
            jump = 10;

        }
        else if ( isPointInsideRect(pointX, pointY, x1, y1+box ,w1, starty-y1-box ) ) {

            go = true;
            direction = d1;
            jump = extent1;

        }
        else if ( isPointInsideRect(pointX, pointY, x1, starty ,w1,extenth ) ) { // thumb on the right

            direction = d3;
            scrollDrag = value1;
            scrollStart = pointY;

        }
        else if ( isPointInsideRect(pointX, pointY, x1, starty+extenth  ,w1, h1 - box - (starty-y1) -extenth) ) {

            go = true;
            direction = d2;
            jump = extent1;

        }
        else if ( isPointInsideRect(pointX, pointY, x1, y1+h1-box, w1, box) ) {

            go = true;
            direction = d2;
            jump = 10;

        }


        if (go) {

            new Thread(this).start();

        }

    }

    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {


        int viewX = getViewPortX();
        int viewY = getViewPortY();
        int viewHeight=getViewPortHeight();
        int viewWidth=getViewPortWidth(viewHeight);

        if (type == DesktopPane.PRESSED) { //  || type == DesktopPane.DRAGGED

            if (mode == MODE_SCROLLBARS) {

                doClickInScrollbar(
                        viewX + viewWidth,
                        viewY,
                        width - viewX - viewWidth,
                        viewHeight,
                        viewY-getComponent().getY(),
                        viewHeight,
                        getComponent().getHeight(),
                        pointX,pointY,
                        Graphics.TOP,Graphics.BOTTOM,Graphics.RIGHT
                );

                doClickInScrollbar(
                        viewY + viewHeight,
                        viewX,
                        height - viewY - viewHeight,
                        viewWidth,
                        viewX-getComponent().getX(),
                        viewWidth,
                        getComponent().getWidth(),
                        pointY,pointX,
                        Graphics.LEFT,Graphics.RIGHT,Graphics.BOTTOM
                );


            }
            else if (mode == MODE_SCROLLARROWS) {

                jump = 20;

                if (getComponent().getWidth() > width) {

                    if ( isPointInsideRect(pointX, pointY,   0, 0, barThickness, height )) {

                        go = true;
                        direction = Graphics.LEFT;

                    }
                    else if ( isPointInsideRect(pointX, pointY,   width - barThickness , 0, barThickness, height )) {

                        go = true;
                        direction = Graphics.RIGHT;

                    }

                }

                if (getComponent().getHeight() > height) {

                    if ( isPointInsideRect(pointX, pointY,   0, 0, width, barThickness)) {

                        go = true;
                        direction = Graphics.TOP;

                    }
                    else if ( isPointInsideRect(pointX, pointY,   0,height-barThickness, width, barThickness)) {

                        go = true;
                        direction = Graphics.BOTTOM;

                    }

                }

                if (go) {

                    new Thread(this).start();

                }

            }



        }
        else if (type == DesktopPane.DRAGGED) {

            if (mode == MODE_SCROLLBARS && !go) {

                if (direction==Graphics.RIGHT) { // vertical

                    int newValue = getNewValue(
                            viewX + viewWidth,
                            viewY,
                            width - viewX - viewWidth,
                            viewHeight,
                            scrollDrag,//viewY-getComponent().getY(),
                            viewHeight,
                            getComponent().getHeight(),

                            pointY-scrollStart
                    );

                    makeVisible( getViewPortX()-getComponent().getX() , newValue ,viewWidth,viewHeight,false);

                }
                else if (direction==Graphics.BOTTOM) { // horizontal

                    int newValue = getNewValue(
                            viewY + viewHeight,
                            viewX,
                            height - viewY - viewHeight,
                            viewWidth,
                            scrollDrag,//viewX-getComponent().getX(),
                            viewWidth,
                            getComponent().getWidth(),

                            pointX-scrollStart
                    );

                    makeVisible( newValue , getViewPortY()-getComponent().getY() ,viewWidth,viewHeight,false);
                }
            }

        }
        else if (type == DesktopPane.RELEASED) {

            go = false;
            direction = 0;

        }

    }

    private int getNewValue(int x,int y,int w,int h,int value,int extent, int max,int pixels) {

        int[] offsets = getOffsets(x, y, w, h, value, extent, max);

        return value + ((max-extent)*  pixels)/ (h - offsets[0]*2 - offsets[2]);
    }

    private int scrollDrag;
    private int scrollStart;

    private int direction;
    private boolean go;
    private int jump;

    // this can also be done by kidnapping the animation thread
    // and then giving it back when it is not needed any more
    public void run() {

        while (go) {

            int cX = getViewPortX()-getComponent().getX();
            int cY = getViewPortY()-getComponent().getY();
            int viewHeight=getViewPortHeight();
            int viewWidth=getViewPortWidth(viewHeight);

            switch(direction) {

                case Graphics.TOP: cY=cY-jump; break;
                case Graphics.BOTTOM: cY=cY+jump; break;
                case Graphics.LEFT: cX=cX-jump; break;
                case Graphics.RIGHT: cX=cX+jump; break;

            }

            makeVisible(cX,cY,viewWidth,viewHeight,false);

            synchronized (this) {
                try {
                    wait( (long) 100);
                }
                catch(InterruptedException e) {}
            }

        }
    }

}
