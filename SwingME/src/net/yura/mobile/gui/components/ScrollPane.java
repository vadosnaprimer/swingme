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

        public static final int MINIMUM_THUMB_SIZE=3;

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

	private int scrollTrackCol;
	private int scrollBarCol;

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

        public void setName(String n) {
            throw new IllegalArgumentException();
        }

	public ScrollPane(Component view,int a) {
		this(a);
		add(view);
	}

	public void setMode(int m) {

		mode = m;
	}

    public void setSize(int w, int h) {

	//boolean resetChild = w!=width || h!=height;

        super.setSize(w, h);

        switch (mode) {
            case MODE_SCROLLBARS: barThickness = (trackTop != null) ? trackTop.getIconWidth() : getBarThickness(w, h); break;
            case MODE_SCROLLARROWS: // fall though
            case MODE_INDICATOR: barThickness = (rightArrow != null) ? rightArrow.getIconWidth() : getBarThickness(w, h); break;
            case MODE_NONE: barThickness = 0; break;
            default: throw new RuntimeException();
        }

        // TODO WHY IS THIS HERE????

	//if (resetChild) {
	        // Size of the scroll changed, we need to reset the component location
        	getComponent().setLocation(getViewPortX(), getViewPortY());
	//}
    }

	public static int getBarThickness(int w,int h) {

		return Math.max(6, Math.min(w / 20, h / 20));

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
		//	((Panel)a).setScrollPanel(this);
		//}

		a.setLocation(getViewPortX(), getViewPortY());

		a.setScrollPanel(this);

		// TODO does it take into account the border?
	}

	public void add(Component component,String constraint){
		throw new RuntimeException("must use add");
	}

	public void remove(Component a){
		super.remove(a);

		//if (a instanceof Panel) {
		//	((Panel)a).setScrollPanel(null);
		//}

		a.setScrollPanel(null);
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

		component.setBounds( component.getX()+xdiff , component.getY()+ydiff , component.getWidth(), component.getHeight());

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

        public void workoutSize() {

                if (preferredWidth!=-1) {
                    width = preferredWidth;
                }
                else {
			width = 0;
		}

                if (preferredHeight!=-1) {
                    height = preferredHeight;
                }
                else {
			height = 0;
		}

        }


        /**
         * we have to do this here, as only here do we already know what OUR size is
         * so we can resize the content how we want
         */
	public void doLayout() {

	    if (getComponents().size() == 1) {

		getComponent().workoutSize();

		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);

		// this is a hack to make it easer to code panels and not have a tiny amount of side scrolling
		// as even though this is technically correct, it is very annoying to use this panel
		// now panels that are the width of the scrollpane or less are set to the width of the viewPort

		// TODO this hack is only for MODE_SCROLLBARS

		// if we have no lower scroll bar AND the width of the component is less then or equal to the width of the scrollpane
		if ( getComponent().getWidth() <= (width-getViewPortX())) {

			getComponent().setSize(viewWidth, getComponent().getHeight());

		}

                if (getComponent().getHeight() <viewHeight) {
                    getComponent().setSize(getComponent().getWidth(), viewHeight);
                }

                super.doLayout();
	    }

	}

	public void paintChildren(Graphics2D g) {

		int a=g.getClipX();
		int b=g.getClipY();
		int c=g.getClipWidth();
		int d=g.getClipHeight();

		int viewX=getViewPortX();
		int viewY=getViewPortY();
		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);

		// dont care about clipping for the
		// scrollbars as they r painted over the top
		g.clipRect(viewX, viewY, viewWidth, viewHeight);

	    super.paintChildren(g);

	    g.setClip(a,b,c,d);

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

                int starty;
                int extenth;
                int box;

                // DRAW ARROWS
                // #############################################################

                if (trackTop!=null) {

                    int x1 = x + (w-trackTop.getIconWidth())/2;
                    trackTop.paintIcon(this, g, x1, y);
                    trackBottom.paintIcon(this, g, x1, y+h-trackBottom.getIconHeight() );

                    starty = trackTop.getIconHeight();
                    extenth = h - starty - trackBottom.getIconHeight();
                    box = (trackTop.getIconHeight() >w)?w:trackTop.getIconHeight();

                }
                else {

                    g.setColor(scrollTrackCol);
                    g.fillRect(x, y, w, w);
                    g.fillRect(x, y+h-w, w, w);

                    g.setColor(scrollBarCol);
                    g.drawRect(x, y, w - 1, w - 1);
                    g.drawRect(x, y+h-w, w - 1, w - 1);

                    int bararroww = w - 4;
                    int bararrowh = (w - 4) / 2;
                    int offset = (w - bararroww)/2 +1;

		    g.fillTriangle(	x + 2+(bararroww/2), y+offset,
					x + 2, y+offset+bararrowh,
					x + 2+bararroww, y+offset+bararrowh);


		    g.fillTriangle(	x + 2+(bararroww/2), y+h -w +offset+bararrowh,
					x + 2, y+h -w +offset,
					x + 2+bararroww, y+h -w +offset);



		    // old way of doing this
                    //drawUpArrow(g, x + 2, y+offset, bararroww, bararrowh);
                    //drawDownArrow(g, x + 2, y+h -w +offset, bararroww, bararrowh);

                    starty = w;
                    extenth = h - starty - w;
                    box = w;

                }

                // draw the track fill color
                // #############################################################

                if (trackFill!=null) {
                    tileIcon(g, trackFill, x + (w-trackFill.getIconWidth())/2 , starty, trackFill.getIconWidth(), extenth);
                }

                // draw the thumb!
                // #############################################################

                int space = h - box * 2 - 1;
                extenth = (extent*space)/max;
                int min = (thumbTop==null)?MINIMUM_THUMB_SIZE:thumbTop.getIconHeight()+thumbBottom.getIconHeight();
                if (extenth < min) {
                    extenth = min;
                    space = space-extenth;
                    max = max-extent;
                }
                starty = y+box+1+ (space*value)/max;

                if (thumbTop!=null) {
                    int x1 = x + (w-thumbTop.getIconWidth())/2;
                    thumbTop.paintIcon(this, g, x1, starty);
                    thumbBottom.paintIcon(this, g, x1, starty+extenth-thumbBottom.getIconHeight());
                    starty = starty + thumbTop.getIconHeight();
                    extenth = extenth - min;
                }

                if (thumbFill!=null) {

                    tileIcon(g, thumbFill,
                            x + (w-thumbFill.getIconWidth())/2 , starty, thumbFill.getIconWidth(), extenth);

                }
                else {
                    g.setColor(scrollBarCol);
                    g.fillRect(
                        x + 2,
                        starty,
                        w - 4,
                        extenth
                    );
                }

    }

    private void tileIcon(Graphics2D g, Icon icon,int dest_x,int dest_y,int dest_w,int dest_h) {
        int h = icon.getIconHeight();

        final int cx = g.getClipX();
        final int cy = g.getClipY();
        final int cw = g.getClipWidth();
        final int ch = g.getClipHeight();

        g.clipRect(dest_x,dest_y,dest_w,dest_h);

        for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+h) {
            icon.paintIcon(this, g, dest_x, pos_y);
        }

        icon.paintIcon(this, g, 0, 0);

        g.setClip(cx,cy,cw,ch);

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

	if (canScroll) {
		g.setColor(scrollBarCol);
	}
	else {
		g.setColor(scrollTrackCol);
	}

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


	public int getScrollTrackCol() {
		return scrollTrackCol;
	}
	public void setScrollTrackCol(int scrollTrackCol) {
		this.scrollTrackCol = scrollTrackCol;
	}
	public int getScrollBarCol() {
		return scrollBarCol;
	}
	public void setScrollBarCol(int scrollBarCol) {
		this.scrollBarCol = scrollBarCol;
	}

        public void clip(Graphics g) {

            	int viewX=getXOnScreen()+getViewPortX();
		int viewY=getYOnScreen()+getViewPortY();
		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);

		g.clipRect(viewX, viewY, viewWidth, viewHeight);

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

                Style theme = DesktopPane.getDefaultTheme(this);

                thumbTop = (Icon)theme.getProperty("thumbTop", Style.ALL);
                thumbBottom = (Icon) theme.getProperty("thumpBottom", Style.ALL);
                Object thumbFill = theme.getProperty("thumbFill", Style.ALL);
                trackTop = (Icon) theme.getProperty("trackTop", Style.ALL);
                trackBottom = (Icon) theme.getProperty("trackBottom", Style.ALL);
                Object trackFill = theme.getProperty("trackFill", Style.ALL);

                if (thumbFill instanceof Icon) {
                    this.thumbFill = (Icon)thumbFill;
                    scrollBarCol=-1;
                }
                else if (thumbFill instanceof Integer) {
                    scrollBarCol = ((Integer)thumbFill).intValue();
                    this.thumbFill = null;
                }

                if (trackFill instanceof Icon) {
                    this.trackFill = (Icon)trackFill;
                    scrollTrackCol=-1;
                }
                else if (trackFill instanceof Integer) {
                    scrollTrackCol = ((Integer)trackFill).intValue();
                    this.trackFill=null;
                }

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


			int buttonHeight = (trackTop==null || trackTop.getIconHeight() >w1)?w1:trackTop.getIconHeight();
			int space1 = h1 - (buttonHeight*2);


                	if ( isPointInsideRect(     pointX, pointY, x1, y1, w1, buttonHeight ) ) {

				go = true;
				direction = d1;
				jump = 10;

			}
			else if ( isPointInsideRect(pointX, pointY, x1, y1+buttonHeight ,w1, (space1*value1)/max1 ) ) {

				go = true;
				direction = d1;
				jump = space1;

			}
			else if ( isPointInsideRect(pointX, pointY, x1, y1+buttonHeight + (space1*value1)/max1 ,w1,(extent1*space1)/max1 ) ) { // thumb on the right

				direction = d3;
				scrollDrag = value1;
				scrollStart = pointY;

			}
			else if ( isPointInsideRect(pointX, pointY, x1, y1+buttonHeight + (space1*(value1+extent1))/max1  ,w1, h1-( buttonHeight*2 + (space1*(value1+extent1))/max1 ) ) ) {

				go = true;
				direction = d2;
				jump = space1;

			}
                	else if ( isPointInsideRect(pointX, pointY, x1, y1+h1-buttonHeight, w1, buttonHeight) ) {

				go = true;
				direction = d2;
				jump = 10;

			}


			if (go) {

				new Thread(this).start();

			}

	}

        public void pointerEvent(int type, int pointX, int pointY, KeyEvent keys) {


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

				// vertical

				int w1 = width - viewX - viewWidth;
				int h1 = viewHeight;
                        	int max1 = getComponent().getHeight();

				int buttonHeight = (trackTop==null || trackTop.getIconHeight() >w1)?w1:trackTop.getIconHeight();
				int space1 = h1 - (buttonHeight*2);

                        	// horizontal

				int w2 = viewWidth;
				int h2 = height - viewY - viewHeight;
                        	int max2 = getComponent().getWidth();

				int buttonWidth = (trackTop == null || trackTop.getIconHeight() >h2)?h2:trackTop.getIconHeight();
                        	int space2 = w2 - (buttonWidth*2);


				if (direction==Graphics.RIGHT) {

					int cX = getViewPortX()-getComponent().getX();
					int cY = scrollDrag + (max1*(pointY-scrollStart))/space1;

					makeVisible(cX,cY,viewWidth,viewHeight,false);

				}
				else if (direction==Graphics.BOTTOM) {

					int cX = scrollDrag + (max2*(pointX-scrollStart))/space2;
					int cY = getViewPortY()-getComponent().getY();

					makeVisible(cX,cY,viewWidth,viewHeight,false);
				}
			}

		}
		else if (type == DesktopPane.RELEASED) {

			go = false;
			direction = 0;

		}

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
	                        	wait(100);
				}
				catch(InterruptedException e) {}
                	}

		}
	}

}
