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

import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JScrollPane
 */
public class ScrollPane extends Panel {
	
	public static final int MODE_NONE=-1;
	public static final int MODE_SCROLLBARS=0;
	public static final int MODE_SCROLLARROWS=1;
	public static final int MODE_INDICATOR=2;
	
	private int mode;
	private int barThickness;
	
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
		
		setMode(m);

		scrollTrackCol = DesktopPane.getDefaultTheme().scrollTrackCol;
		scrollBarCol = DesktopPane.getDefaultTheme().scrollBarCol;
	}
        
	public ScrollPane(Component view,int a) {
		this(a);
		add(view);
	}

	public void setMode(int m) {
		
		mode = m;
	}
	
	public void setSize(int w, int h){
		super.setSize(w,h);
		barThickness = getBarThickness(w,h);
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
		//if(true)throw new RuntimeException();
		
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
	
	
	protected int getViewPortHeight() {
		switch (mode) {
			case MODE_SCROLLBARS: return height-getViewPortY()-((getComponent().getWidth()> (width-getViewPortX()) )?barThickness:0);
			case MODE_SCROLLARROWS: return (getComponent().getHeight() > height)?height-(barThickness*2):height;
			case MODE_NONE:
			case MODE_INDICATOR: return height;
			default: throw new RuntimeException();
		}
	}
	protected int getViewPortWidth(int vph) {
		switch (mode) {
			case MODE_SCROLLBARS: return width-getViewPortX()-((getComponent().getHeight()>vph)?barThickness:0);
			case MODE_SCROLLARROWS: return (getComponent().getWidth() > width)?width-(barThickness*2):width;
			case MODE_NONE:
			case MODE_INDICATOR: return width;
			default: throw new RuntimeException();
		}
	}
	protected int getViewPortX() {
		switch (mode) {
			case MODE_SCROLLARROWS: return (getComponent().getWidth() > width)?barThickness:0;
			case MODE_SCROLLBARS:
			case MODE_NONE:
			case MODE_INDICATOR: return 0;
			default: throw new RuntimeException();
		}
	}
	protected int getViewPortY() {
		switch (mode) {
			case MODE_SCROLLARROWS: return (getComponent().getHeight() > height)?barThickness:0;
			case MODE_SCROLLBARS:
			case MODE_NONE:
			case MODE_INDICATOR: return 0;
			default: throw new RuntimeException();
		}
	}
        
        /**
         * we have to do this here, as only here do we already know what OUR size is
         * so we can resize the content how we want
         */
	public void doLayout() {
                
		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);
		
                if (getComponent().getHeight() <viewHeight) {
                    getComponent().setSize(getComponent().getWidth(), viewHeight);
                }

		// this is a hack to make it easer to code panels and not have a tiny amount of side scrolling
		// as even though this is technically correct, it is very annoying to use this panel
		// now panels that are the width of the scrollpane or less are set to the width of the viewPort
		
		// TODO this hack is only for MODE_SCROLLBARS
		
		// if we have no lower scroll bar AND the width of the component is less then or equal to the width of the scrollpane
		if ( getComponent().getWidth() <= (width-getViewPortX())) {
			
			getComponent().setSize(viewWidth, getComponent().getHeight());
			
		}

                super.doLayout();

	}

	public void paintChildren(Graphics g) {
		
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
	
	protected void paintDecoration(final Graphics g) {
		
		switch (mode) {
			case MODE_NONE: return;
			case MODE_SCROLLBARS: drawScrollBars(g); return;
			case MODE_SCROLLARROWS: drawScrollArrows(g,false); return;
			case MODE_INDICATOR: drawScrollArrows(g,true); return;
			default: throw new RuntimeException();
		}
		
	}
	
	private void drawScrollBars(final Graphics g) {
	
		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);
		
		int bararroww = barThickness - 4;
		int bararrowh = (barThickness - 4) / 2;
		
		int offset = (barThickness - bararroww)/2 +1;

		int viewX=getViewPortX();
		int viewY=getViewPortY();
		
		// NEEDS to be same check as in getViewPortHeight
		if ( getComponent().getWidth() > (width-getViewPortX()) ) {

			g.setColor(scrollBarCol);
			g.fillRect(viewX, height - barThickness, viewWidth, barThickness);
			g.setColor(scrollTrackCol);
			g.drawRect(viewX, height - barThickness, viewWidth - 1, barThickness - 1);
			
			g.drawLine(viewX+barThickness - 1, viewY+viewHeight, viewX+barThickness - 1, height-1);
			g.drawLine(viewX+viewWidth - barThickness, viewY+viewHeight, viewX+viewWidth - barThickness, height-1);
			
			drawLeftArrow(g, viewX+offset, viewY+viewHeight + 2, bararrowh, bararroww);
			drawRightArrow(g, viewX+viewWidth - barThickness + offset, viewY+viewHeight + 2, bararrowh, bararroww);

			int space = viewWidth - barThickness * 2 - 2;
			
			g.fillRect(
					viewX+barThickness+1+ ((viewX-getComponent().getX())*space)/getComponent().getWidth(),
					viewY+viewHeight + 2,
					(viewWidth*space)/getComponent().getWidth(),
					barThickness - 4
			);
		}
		
		// NEEDS to be same check as in getViewPortWidth
		if ( getComponent().getHeight() > viewHeight ) {

			g.setColor(scrollBarCol);
			g.fillRect(width-barThickness, viewY, barThickness, viewHeight);
			g.setColor(scrollTrackCol);
			g.drawRect(width-barThickness, viewY, barThickness - 1, viewHeight - 1);
			
			g.drawLine(viewX+viewWidth, viewY+barThickness - 1, width-1, viewY+barThickness - 1);
			g.drawLine(viewX+viewWidth, viewY+viewHeight - barThickness, width-1, viewY+viewHeight - barThickness);
			
			
			drawUpArrow(g, viewX+viewWidth + 2, viewY+offset, bararroww, bararrowh);
			drawDownArrow(g, viewX+viewWidth + 2, viewY+viewHeight -barThickness +offset, bararroww, bararrowh);
			
			int space = viewHeight - barThickness * 2 - 2;
			
			g.fillRect(
					viewX+viewWidth + 2,
					viewY+barThickness+1+ ((viewY-getComponent().getY())*space)/getComponent().getHeight(),
					barThickness - 4,
					(viewHeight*space)/getComponent().getHeight()
			);
		}

	}

	private void drawScrollArrows(final Graphics g,boolean indicator) {

		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);
		
		int viewX=getViewPortX();
		int viewY=getViewPortY();
		
		int d = barThickness*2;
		int gap=2;
		
		if (getComponent().getWidth() > width) {
			
			if ( getComponent().getX() < viewX ) {
				g.setColor(scrollTrackCol);
			}
			else {
				g.setColor(scrollBarCol);
			}
			
			if (indicator) {
				drawLeftArrow(g, width/2 -gap-d, height+(d+3*gap)/2-barThickness, barThickness, d);
			}
			else {
						drawLeftArrow(g, 0, (height-d)/2, barThickness, d);
			}
			
			if ( (getComponent().getWidth()+getComponent().getX()-viewX) > viewWidth ) {
				g.setColor(scrollTrackCol);
			}
			else {
				g.setColor(scrollBarCol);
			}
			
			if (indicator) {
				drawRightArrow(g, width/2 +gap+barThickness, height+(d+3*gap)/2-barThickness, barThickness, d);
			}
			else {
						drawRightArrow(g, width - barThickness , (height-d)/2, barThickness, d);
			}

		}
		
		if (getComponent().getHeight() > height) {
			
			if (getComponent().getY() < viewY) {
				g.setColor(scrollTrackCol);
			}
			else {
				g.setColor(scrollBarCol);
			}
			
			if (indicator) {
				drawUpArrow(g, (width-d)/2, height+gap, d, barThickness);
			}
			else {
						drawUpArrow(g, (width-d)/2, 0, d, barThickness);
			}
			
			if ( (getComponent().getHeight()+getComponent().getY()-viewY) > viewHeight ) {
				g.setColor(scrollTrackCol);
			}
			else {
				g.setColor(scrollBarCol);
			}
			
			if (indicator) {
				drawDownArrow(g, (width-d)/2,height+barThickness+gap*2, d, barThickness);
			}
			else {
						drawDownArrow(g, (width-d)/2,height-barThickness, d, barThickness);
			}
			
		}
	}
	
    public static void drawDownArrow(Graphics g, int x, int y, int w, int h) {
        for (int i=0; i<h; i++) {
            g.fillRect(x+i, y+i, w-2*i, 1);
        }
    }
    
    public static void drawUpArrow(Graphics g, int x, int y, int w, int h) {
        for (int i=0; i<h; i++) {
            g.fillRect(x+i, y+h-i-1, w-2*i, 1);
        }
    }
    
    public static void drawLeftArrow(Graphics g, int x, int y, int w, int h) {
        for (int i=0; i<h/2; i++) {
            g.fillRect(x+w-i-1, y+i, i+1, 1);
        }
        for (int i=h/2; i<h; i++) {
            g.fillRect(x+i-h/2, y+i, w-i+h/2, 1);
        }
    }
    
    public static void drawRightArrow(Graphics g, int x, int y, int w, int h) {
        for (int i=0; i<h/2; i++) {
            g.fillRect(x, y+i, i+1, 1); 
        }
        for (int i=h/2; i<h; i++) {
            g.fillRect(x, y+i, w-i+h/2, 1);
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
            
            	int viewX=getXInWindow()+getViewPortX();
		int viewY=getYInWindow()+getViewPortY();
		int viewHeight=getViewPortHeight();
		int viewWidth=getViewPortWidth(viewHeight);
		
		g.clipRect(viewX, viewY, viewWidth, viewHeight);
            
        }

        

}
