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

import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JSlider
 * @see javax.swing.JScrollBar
 */
public class Slider extends Component {

    public static final int MINIMUM_THUMB_SIZE=5;

    private Border track,thumb;
    private int min,max,value,extent;
    private boolean horizontal = true;

    private boolean paintTicks = false;
    private int minorTickSpacing,majorTickSpacing;
    private int tickSpace;
    private ChangeListener chl;

    // Slider has horizontal default
    // Scrollbar has vertical default

    /**
     * Creates a horizontal slider with the range 0 to 100 and an initial value of 50.
     * @see javax.swing.JSlider#JSlider() JSlider.JSlider
     * @see javax.swing.JScrollBar#JScrollBar() JScrollBar.JScrollBar
     */
    public Slider() {
        this(0,100,50);
    }

    /**
     * @see javax.swing.JSlider#JSlider(int, int, int) JSlider.JSlider
     */
    public Slider(int min, int max, int value) {
        this.min= min;
        this.max = max;
        this.value = value;
        extent = 0; // ZERO is the default extent for the JSlider
        tickSpace = theme.getFont(Style.ALL).getHeight()/2;
    }

    /**
     * @see javax.swing.JSlider#addChangeListener(javax.swing.event.ChangeListener) JSlider.addChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        //#mdebug warn
        if (chl!=null) {
            Logger.warn("trying to add a ChangeListener when there is already one registered");
            Logger.dumpStack();
        }
        if (l==null) {
            Logger.warn("trying to add a null ChangeListener");
            Logger.dumpStack();
        }
        //#enddebug
        chl = l;
    }

    /**
     * @see javax.swing.JSlider#fireStateChanged() JSlider.fireStateChanged
     */
    protected void fireStateChanged() {
        if (chl!=null) {
            chl.changeEvent(this, value);
        }
    }

    /**
     * @see javax.swing.JSlider#setOrientation(int) JSlider.setOrientation
     * @see javax.swing.JScrollBar#setOrientation(int) JScrollBar.setOrientation
     */
    public void setHorizontal(boolean b) {
        horizontal = b;
    }

    /**
     * @see javax.swing.JSlider#getValue() JSlider.getValue
     * @see javax.swing.JScrollBar#getValue() JScrollBar.getValue
     */
    public Object getValue() {
        return new Integer(value);
    }

    /**
     * @see javax.swing.JSlider#setValue(int) JSlider.setValue
     * @see javax.swing.JScrollBar#setValue(int) JScrollBar.setValue
     */
    public void setValue(Object o) {
        if (o instanceof Integer) {
            value = ((Integer)o).intValue();
        }
        //#mdebug warn
        else {
            Logger.warn("trying to set value that is not a Integer "+o);
        }
        //#enddebug
    }

    /**
     * @see javax.swing.JSlider#getMaximum() JSlider.getMaximum
     * @see javax.swing.JScrollBar#getMaximum() JScrollBar.getMaximum
     */
    public int getMaximum() {
        return max;
    }

    /**
     * @see javax.swing.JSlider#getMinimum() JSlider.getMinimum
     * @see javax.swing.JScrollBar#getMinimum() JScrollBar.getMinimum
     */
    public int getMinimum() {
        return min;
    }

    /**
     * @see javax.swing.JSlider#getExtent() JSlider.getExtent
     * @see javax.swing.JScrollBar#getVisibleAmount() JScrollBar.getVisibleAmount
     */
    public int getExtent() {
        return extent;
    }



    /**
     * @see javax.swing.JSlider#setMaximum(int) JSlider.setMaximum
     * @see javax.swing.JScrollBar#setMaximum(int) JScrollBar.setMaximum
     */
    public void setMaximum(int m) {
        max = m;
    }

    /**
     * @see javax.swing.JSlider#setMinimum(int) JSlider.setMinimum
     * @see javax.swing.JScrollBar#setMinimum(int) JScrollBar.setMinimum
     */
    public void setMinimum(int m) {
        min = m;
    }

    /**
     * @see javax.swing.JSlider#setExtent(int) JSlider.setExtent
     * @see javax.swing.JScrollBar#setVisibleAmount(int) JScrollBar.setVisibleAmount
     */
    public void setExtent(int ex) {
        extent = ex;
    }


    /**
     * @see javax.swing.JSlider#setSnapToTicks(boolean) JSlider.setSnapToTicks
     */
    public void setSnapToTicks(boolean snap) {

    }

    /**
     * @see javax.swing.JSlider#setPaintTicks(boolean) JSlider.setPaintTicks
     */
    public void setPaintTicks(boolean ticks) {
         paintTicks = ticks;
    }

    /**
     * @see javax.swing.JSlider#setMajorTickSpacing(int) JSlider.setMajorTickSpacing
     */
    public void setMajorTickSpacing(int n) {
        majorTickSpacing = n;
    }

    /**
     * @see javax.swing.JSlider#setMinorTickSpacing(int) JSlider.setMinorTickSpacing
     */
    public void setMinorTickSpacing(int n) {
        minorTickSpacing = n;
    }

    public void paintComponent(Graphics2D g) {
        if (horizontal) {

            drawScrollBar(g,
                    0,
                    0,
                    width,
                    height,
                    value,
                    extent,
                    max
            );
        }
        else {
            int t = g.getTransform();
            g.setTransform( Sprite.TRANS_MIRROR_ROT270 );

            drawScrollBar(g,
                    0,
                    0,
                    height,
                    width,
                    value,
                    extent,
                    max
            );

            g.setTransform( t );
        }
    }

    int click;
    int startPos,startValue;
    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {
        super.processMouseEvent(type, pointX, pointY, keys);

        if (!focusable) return;

        if (type==DesktopPane.RELEASED) {
            click = 0;
        }
        else if (type == DesktopPane.PRESSED) {
            if (horizontal) {
                click = doClickInScrollbar(
                        0,
                        0,
                        width,
                        height,
                        value,
                        extent,
                        max,
                        pointX,
                        pointY
                );
                startPos = pointX;
            }
            else {
                click = doClickInScrollbar(
                        0,
                        0,
                        height,
                        width,
                        value,
                        extent,
                        max,
                        pointY,
                        pointX
                );
                startPos = pointY;
            }
            startValue = value;

            if (click==CLICK_UP || click == CLICK_DOWN) {
                getDesktopPane().animateComponent(this);
            }
        }
        else if (type == DesktopPane.DRAGGED) {

            if (click==CLICK_THUMB) {

                int w = horizontal?width:height;
                int h = horizontal?height:width;
                int p = horizontal?pointX:pointY;

                int newValue = getNewValue(
                        0,
                        0,
                        w,
                        h,
                        startValue,
                        extent,
                        max,
                        p-startPos
                  );

                int m = max-extent;
                if (newValue>m) newValue=m;
                if (newValue<min) newValue=min;
                value=newValue;
                fireStateChanged();

                repaint();
            }
        }

    }

    public void workoutMinimumSize() {

        int thickness = (track != null) ? track.getTop() + track.getBottom() : 0;
        if (paintTicks) {
            thickness = thickness + tickSpace;
        }

        if (horizontal) {
            width = 20;
            height = thickness;
        }
        else {
            width = thickness;
            height = 20;
        }
    }

    public void animate() throws InterruptedException {

        while (true) {
            if (click == CLICK_UP && value > min) {
                value--;
                fireStateChanged();
            }
            else if (click == CLICK_DOWN && value < (max-extent)) {
                value++;
                fireStateChanged();
            }
            else {
                break;
            }
            repaint();
            wait(20);
        }
    }

    protected String getDefaultName() {
        return "Slider";
    }

    public String getName() {
        String name = super.getName();
        
        return name;
    }

    public void updateUI() {
        super.updateUI();
        String name = getName();

        Style theme1 = DesktopPane.getDefaultTheme(name+"Thumb");
        Style theme2 = DesktopPane.getDefaultTheme(name+"Track");
        
        thumb = theme1.getBorder(Style.ALL);
        track = theme2.getBorder(Style.ALL);

/*
        thumbTop = (Icon)theme.getProperty("thumbTop", Style.ALL);
        thumbBottom = (Icon) theme.getProperty("thumbBottom", Style.ALL);
        thumbFill = (Icon)theme.getProperty("thumbFill", Style.ALL);
        trackTop = (Icon) theme.getProperty("trackTop", Style.ALL);
        trackBottom = (Icon) theme.getProperty("trackBottom", Style.ALL);
        trackFill = (Icon)theme.getProperty("trackFill", Style.ALL);
*/
    }


    public boolean consumesMotionEvents() {
        return true;
    }


    public static final int CLICK_NONE = 0;
    public static final int CLICK_UP = 1;
    public static final int CLICK_PGUP = 2;
    public static final int CLICK_THUMB = 3;
    public static final int CLICK_PGDOWN = 4;
    public static final int CLICK_DOWN = 5;

    public int doClickInScrollbar(int x1,int y1,int w1,int h1,int value1,int extent1, int max1, int pointX,int pointY) {

        int[] tmp = getOffsets(x1,y1,w1,h1,value1,extent1,max1);
        int box = tmp[0];
        int startX = tmp[1];
        int extentW = tmp[2];

        if (ScrollPane.isPointInsideRect(pointX, pointY, x1, y1, box, h1)) {
            return CLICK_UP;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, x1+box, y1, startX-x1-box, h1)) {
            return CLICK_PGUP;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, startX, y1 ,extentW,h1)) { // thumb on the right
            return CLICK_THUMB;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, startX+extentW, y1, w1 - box - (startX-x1) - extentW, h1)) {
            return CLICK_PGDOWN;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, x1+w1-box, y1, box, h1)) {
            return CLICK_DOWN;
        }
        else {
            return CLICK_NONE;
        }

    }

    /**
     * @see javax.swing.JScrollBar#JScrollBar(int, int, int, int, int) JScrollBar.JScrollBar
     */
    public void drawScrollBar(Graphics2D g, int x,int y,int w,int h,int value,int extent, int max
//            ,Icon trackTop,Icon trackFill,Icon trackBottom,Icon thumbTop,Icon thumbFill,Icon thumbBottom
            ) {

        int[] tmp = getOffsets(x,y,w,h,value,extent,max
//                ,trackTop,trackFill,trackBottom,thumbTop,thumbFill,thumbBottom
                );
        int startX = tmp[1];
        int extentW = tmp[2];

        if (paintTicks) {
            g.setColor( getCurrentForeground() );
            h = h - tickSpace;
            int side = tmp[0]+(thumb!=null?thumb.getTop():0);
            int space = w-side*2;

            if (minorTickSpacing>0) {
                for (int c=0;c<=max;c=c+minorTickSpacing) {
                    int tickX = side + space*c/max;
                    g.drawLine(tickX , h, tickX, h+tickSpace/2);
                }
            }
            if (majorTickSpacing>0) {
                for (int c=0;c<=max;c=c+majorTickSpacing) {
                    int tickX = side + space*c/max;
                    g.drawLine(tickX ,h , tickX, h+tickSpace*3/4);
                }
            }
        }


        // DRAW ARROWS
        //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,

        if (track!=null) {

            g.translate(x+track.getLeft(), y+track.getTop());
            track.paintBorder(this, g, w-track.getLeft()-track.getRight(), h-track.getTop()-track.getBottom());
            g.translate(-x-track.getLeft(), -y-track.getTop());
/*
            int x1 = x + (w-trackTop.getIconWidth())/2;
            trackTop.paintIcon(this, g, x1, y);
            trackBottom.paintIcon(this, g, x1, y+h-trackBottom.getIconHeight() );

            starty = trackTop.getIconHeight();
            extenth = h - starty - trackBottom.getIconHeight();

            tileIcon(g, trackFill, x + (w-trackFill.getIconWidth())/2 , starty, trackFill.getIconWidth(), extenth);
 */
        }

        // draw the thumb!
        //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,


        if (thumb!=null) {

            int y1 = y + (h-thumb.getTop()-thumb.getBottom())/2;

            // this does the floating scrollbars
            if (y1+thumb.getTop()+thumb.getBottom() > y+h) {
                y1 = y+h -thumb.getTop()-thumb.getBottom();
            }

            g.translate(startX+thumb.getLeft(), y1+thumb.getTop());
            thumb.paintBorder(this, g, extentW-thumb.getLeft()-thumb.getRight(), 0);
            g.translate(-startX-thumb.getLeft(), -y1-thumb.getTop());

            /*
            int x1 = x + (w-thumbTop.getIconWidth())/2;
            thumbTop.paintIcon(this, g, x1, starty);
            thumbBottom.paintIcon(this, g, x1, starty+extenth-thumbBottom.getIconHeight());
            starty = starty + thumbTop.getIconHeight();
            extenth = extenth - (thumbTop.getIconHeight()+thumbBottom.getIconHeight());

            tileIcon(g, thumbFill, x + (w-thumbFill.getIconWidth())/2 , starty, thumbFill.getIconWidth(), extenth);
             */
        }

    }
/*
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
*/
    /**
     * @param x - Ignored?
     * @param y - Start of bar
     * @param w - width of bar
     * @param h - viewPort Height/Width
     * @param value - Desired view X/Y
     * @param extent - viewPort Height/Width > same as h?
     * @param max - view Height/Width
     * @return int[0]=boxSize int[1]=thumbY int[2]=thumbSize
     */
    public int[] getOffsets(int x,int y,int w, int h, int value,int extent, int max
//            ,Icon trackTop,Icon trackFill,Icon trackBottom,Icon thumbTop,Icon thumbFill,Icon thumbBottom
            ) {

        final int box = track!=null?((track.getLeft() >h)?h:track.getLeft()):0;
        final int topBotton = (thumb==null)?0:thumb.getTop()+thumb.getBottom();

        final int space1 = w - box * 2;

        int extentW = (int) ( (extent*space1)/(double)max + 0.5);
        int min1 = (topBotton<MINIMUM_THUMB_SIZE)?MINIMUM_THUMB_SIZE:topBotton;
        min1 = min1>(space1/2)?space1/2:min1;

        int space = space1;
        if (extentW < min1) {
            extentW = min1;
            space = space-extentW;
            max = max-extent;
        }
        int startX = box+ (int)( (space*value)/(double)max + 0.5 );

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
        if ((startX+extentW) < (box+min1)) {
            startX = box;
            extentW = min1;
        }
        else if (startX < box) {
            extentW = startX+extentW-box;
            startX = box;
        }
        else if (startX > (box+space1-min1)) {
            startX = box+space1-min1;
            extentW = min1;
        }
        else if ((startX+extentW) > (box+space1)) {
            extentW = box+space1-startX;
        }

        return new int[] {box,x+startX,extentW};
    }

    public int getNewValue(int x,int y,int w,int h,int value,int extent, int max,int pixels) {
        int[] offsets = getOffsets(x, y, w, h, 0, extent, max);
        return value + ((max-extent)*  pixels)/ (w - offsets[0]*2 - offsets[2]);
    }

}
